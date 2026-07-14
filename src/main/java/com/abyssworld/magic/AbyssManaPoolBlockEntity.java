package com.abyssworld.magic;

import com.abyssworld.item.AbyssKeyItem;
import com.abyssworld.block.AbyssManaPoolBlock;
import com.abyssworld.registry.ModBlockEntities;
import com.abyssworld.registry.ModBlocks;
import com.abyssworld.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

public class AbyssManaPoolBlockEntity extends BlockEntity {
    private int mana;
    private int reactorFuel;

    public AbyssManaPoolBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ABYSS_MANA_POOL.get(), pos, state);
    }

    public int mana() {
        if (tier() == AbyssManaPoolBlock.Tier.WIRELESS && level instanceof ServerLevel serverLevel) {
            return AbyssWirelessManaData.get(serverLevel.getServer()).mana();
        }
        return isInfinite() ? Integer.MAX_VALUE : mana;
    }

    public int capacity() {
        return tier().capacity();
    }

    public boolean isInfinite() {
        return tier().infinite();
    }

    public int addMana(int amount) {
        if (isInfinite()) {
            return Math.max(0, amount);
        }
        if (tier() == AbyssManaPoolBlock.Tier.WIRELESS && level instanceof ServerLevel serverLevel) {
            return AbyssWirelessManaData.get(serverLevel.getServer()).add(amount);
        }
        int accepted = Math.min(Math.max(0, amount), capacity() - mana);
        if (accepted > 0) {
            mana += accepted;
            setChanged();
        }
        return accepted;
    }

    public int consumeMana(int amount) {
        if (isInfinite()) {
            return Math.max(0, amount);
        }
        if (tier() == AbyssManaPoolBlock.Tier.WIRELESS && level instanceof ServerLevel serverLevel) {
            return AbyssWirelessManaData.get(serverLevel.getServer()).consume(amount);
        }
        int consumed = Math.min(Math.max(0, amount), mana);
        if (consumed > 0) {
            mana -= consumed;
            setChanged();
        }
        return consumed;
    }

    public boolean tryChargeFrom(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) {
            return false;
        }
        if (tier() == AbyssManaPoolBlock.Tier.REACTOR && held.is(ModItems.SINGULARITY_RESIDUE.get())) {
            reactorFuel = Math.min(96_000, reactorFuel + 12_000);
            held.shrink(1); setChanged();
            player.displayClientMessage(Component.translatable("block.abyssworld.mana_vortex_reactor.fueled",
                    reactorFuel / 20).withStyle(ChatFormatting.LIGHT_PURPLE), true);
            return true;
        }
        if (held.getItem() instanceof PortableManaContainer container) {
            return chargePortableContainer(player, held, container);
        }
        if (isInfinite()) {
            return false;
        }

        int value = chargeValue(held.getItem()) * tier().chargeMultiplier();
        if (value <= 0) {
            return false;
        }
        int accepted = addMana(value);
        if (accepted <= 0) {
            player.displayClientMessage(Component.translatable("block.abyssworld.abyss_mana_pool.full")
                    .withStyle(ChatFormatting.YELLOW), true);
            return true;
        }

        held.shrink(1);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.WITCH,
                    worldPosition.getX() + 0.5D, worldPosition.getY() + 1.0D, worldPosition.getZ() + 0.5D,
                    24, 0.4D, 0.4D, 0.4D, 0.02D);
            serverLevel.playSound(null, worldPosition, SoundEvents.RESPAWN_ANCHOR_CHARGE,
                    SoundSource.BLOCKS, 1.0F, 0.75F);
        }
        player.displayClientMessage(Component.translatable("block.abyssworld.abyss_mana_pool.charged",
                accepted, mana, capacity()).withStyle(ChatFormatting.AQUA), true);
        return true;
    }

    private boolean chargePortableContainer(Player player, ItemStack stack, PortableManaContainer container) {
        int missing = container.manaCapacity(stack) - container.storedMana(stack);
        if (missing <= 0) {
            player.displayClientMessage(Component.translatable(
                    "item.abyssworld.portable_mana_core.full").withStyle(ChatFormatting.YELLOW), true);
            return true;
        }

        int transferable = isInfinite() ? missing : Math.min(mana, missing);
        if (transferable <= 0) {
            player.displayClientMessage(Component.translatable(
                    "block.abyssworld.abyss_mana_pool.empty").withStyle(ChatFormatting.RED), true);
            return true;
        }

        int accepted = container.insertMana(stack, transferable);
        if (!isInfinite()) {
            consumeMana(accepted);
        }
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    worldPosition.getX() + 0.5D, worldPosition.getY() + 1.0D, worldPosition.getZ() + 0.5D,
                    18, 0.35D, 0.35D, 0.35D, 0.02D);
            serverLevel.playSound(null, worldPosition, SoundEvents.BEACON_POWER_SELECT,
                    SoundSource.BLOCKS, 0.9F, 1.35F);
        }
        player.getInventory().setChanged();
        player.displayClientMessage(Component.translatable(
                "item.abyssworld.portable_mana_core.charged", accepted,
                container.storedMana(stack), container.manaCapacity(stack))
                .withStyle(ChatFormatting.AQUA), true);
        return true;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AbyssManaPoolBlockEntity pool) {
        AbyssManaPoolBlock.Tier tier = pool.tier();
        if (tier.infinite() || !(level instanceof ServerLevel serverLevel)
                || level.getGameTime() % tier.generationInterval() != 0L) {
            return;
        }

        if (tier == AbyssManaPoolBlock.Tier.WIRELESS) {
            AbyssWirelessManaData data = AbyssWirelessManaData.get(serverLevel.getServer());
            int room = AbyssWirelessManaData.CAPACITY - data.mana();
            if (room <= 0) return;
            for (AbyssManaPoolBlockEntity source : AbyssManaNetwork.pools(level, pos, 64)) {
                if (source == pool || source.tier() == AbyssManaPoolBlock.Tier.WIRELESS) continue;
                int drained = source.consumeMana(Math.min(512, room));
                if (drained > 0) data.add(drained);
                break;
            }
            return;
        }

        if (tier == AbyssManaPoolBlock.Tier.REACTOR) {
            if (pool.reactorFuel <= 0 || !isReactorFormed(level, pos)) return;
            if (pool.addMana(2048) > 0) { pool.reactorFuel--; pool.setChanged(); }
            return;
        }

        int gain = passiveGain(level, pos, tier);
        if (gain <= 0 || pool.addMana(gain) <= 0) {
            return;
        }
        if (level.getGameTime() % 160L == 0L) {
            serverLevel.sendParticles(ParticleTypes.ENCHANT,
                    pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D,
                    8, 0.35D, 0.45D, 0.35D, 0.01D);
        }
    }

    private static int passiveGain(Level level, BlockPos pos, AbyssManaPoolBlock.Tier tier) {
        if (tier == AbyssManaPoolBlock.Tier.STORAGE) return 0;
        if (tier == AbyssManaPoolBlock.Tier.VOID) {
            return (level.dimension() == Level.END || AbyssKeyItem.isAbyssDimension(level.dimension())) ? 512 : 32;
        }
        if (tier == AbyssManaPoolBlock.Tier.LIFE || tier == AbyssManaPoolBlock.Tier.INFERNO
                || tier == AbyssManaPoolBlock.Tier.CRYO) {
            int gain = 0;
            for (BlockPos scan : BlockPos.betweenClosed(pos.offset(-5, -3, -5), pos.offset(5, 3, 5))) {
                BlockState state = level.getBlockState(scan);
                if (tier == AbyssManaPoolBlock.Tier.LIFE
                        && (state.is(BlockTags.LEAVES) || state.is(BlockTags.FLOWERS) || state.is(BlockTags.CROPS))) gain += 4;
                if (tier == AbyssManaPoolBlock.Tier.INFERNO
                        && (state.is(Blocks.LAVA) || state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE))) gain += 8;
                if (tier == AbyssManaPoolBlock.Tier.CRYO
                        && (state.is(BlockTags.ICE) || state.is(BlockTags.SNOW))) gain += 12;
            }
            return Math.min(gain, tier.maximumGain());
        }
        int gain = tier.baseGain(AbyssKeyItem.isAbyssDimension(level.dimension()));
        for (BlockPos scan : BlockPos.betweenClosed(pos.offset(-4, -2, -4), pos.offset(4, 2, 4))) {
            Block block = level.getBlockState(scan).getBlock();
            if (block == ModBlocks.PRIMORDIAL_BLOOM.get()
                    || block == ModBlocks.ASH_VEIN.get()
                    || block == ModBlocks.FROZEN_CLUSTER.get()
                    || block == ModBlocks.FLESH_DEPOSIT.get()
                    || block == ModBlocks.VOID_CRYSTAL.get()) {
                gain += tier.nearbyDepositGain();
            }
        }
        return Math.min(gain, tier.maximumGain());
    }

    public boolean isReactorFormed() {
        return level != null && isReactorFormed(level, worldPosition);
    }

    private static boolean isReactorFormed(Level level, BlockPos center) {
        for (int x = -2; x <= 2; x++) for (int z = -2; z <= 2; z++) {
            if (Math.abs(x) == 2 || Math.abs(z) == 2) {
                if (!level.getBlockState(center.offset(x, 0, z)).is(ModBlocks.ABYSS_MACHINE_CASING.get())) return false;
            }
        }
        for (int x : new int[]{-2, 2}) for (int z : new int[]{-2, 2}) for (int y = 1; y <= 2; y++) {
            if (!level.getBlockState(center.offset(x, y, z)).is(ModBlocks.ABYSS_MACHINE_CASING.get())) return false;
        }
        return true;
    }

    private AbyssManaPoolBlock.Tier tier() {
        if (getBlockState().getBlock() instanceof AbyssManaPoolBlock poolBlock) {
            return poolBlock.tier();
        }
        return AbyssManaPoolBlock.Tier.BASIC;
    }

    private static int chargeValue(Item item) {
        if (item == ModItems.ABYSS_CRYSTAL.get()) {
            return 250;
        }
        if (item == ModItems.COMPRESSED_ABYSS_CRYSTAL.get()) {
            return 2200;
        }
        if (item == ModItems.HIGH_DENSITY_ABYSS_ALLOY.get()) {
            return 4000;
        }
        if (item == ModItems.ABYSS_GOD_CORE.get()) {
            return 10000;
        }
        return 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Mana", mana);
        tag.putInt("ReactorFuel", reactorFuel);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        mana = Math.max(0, Math.min(capacity(), tag.getInt("Mana")));
        reactorFuel = Math.max(0, tag.getInt("ReactorFuel"));
    }
}
