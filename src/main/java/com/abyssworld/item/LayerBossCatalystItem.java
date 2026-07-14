package com.abyssworld.item;

import com.abyssworld.AbyssWorld;
import com.abyssworld.block.LayerAltarBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * Existing layer materials can be used as challenge catalysts in their matching Abyss biome.
 */
public class LayerBossCatalystItem extends Item {
    private final Supplier<? extends EntityType<? extends Mob>> bossType;
    private final ResourceKey<Biome> requiredBiome;
    private final String bossTranslationKey;
    private final String biomeTranslationKey;

    public LayerBossCatalystItem(Properties properties,
                                 Supplier<? extends EntityType<? extends Mob>> bossType,
                                 String requiredBiomeName,
                                 String bossTranslationKey) {
        super(properties);
        this.bossType = bossType;
        this.requiredBiome = ResourceKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(AbyssWorld.MODID, requiredBiomeName));
        this.bossTranslationKey = bossTranslationKey;
        this.biomeTranslationKey = "biome." + AbyssWorld.MODID + "." + requiredBiomeName;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return trySummon(context.getLevel(), context.getPlayer(), context.getHand(),
                context.getClickedPos()) ? InteractionResult.sidedSuccess(context.getLevel().isClientSide)
                : InteractionResult.FAIL;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (trySummon(level, player, hand, null)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return InteractionResultHolder.fail(stack);
    }

    private boolean trySummon(Level level, @Nullable Player player, InteractionHand hand, @Nullable BlockPos altarPos) {
        if (player == null) {
            return false;
        }
        if (level.isClientSide) {
            return true;
        }
        if (!AbyssKeyItem.isAbyssDimension(level.dimension())) {
            player.displayClientMessage(Component.translatable("item.abyssworld.layer_boss_catalyst.wrong_dimension")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }

        EntityType<? extends Mob> type = bossType.get();
        if (altarPos == null) {
            player.displayClientMessage(Component.translatable("item.abyssworld.layer_boss_catalyst.needs_altar")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        BlockState altarState = level.getBlockState(altarPos);
        if (!(altarState.getBlock() instanceof LayerAltarBlock altar) || !altar.isAltarFor(type)) {
            player.displayClientMessage(Component.translatable("item.abyssworld.layer_boss_catalyst.wrong_altar",
                    Component.translatable(bossTranslationKey)).withStyle(ChatFormatting.RED), true);
            return false;
        }
        if (!level.getBiome(altarPos).is(requiredBiome)) {
            player.displayClientMessage(Component.translatable("item.abyssworld.layer_boss_catalyst.wrong_biome",
                    Component.translatable(biomeTranslationKey)).withStyle(ChatFormatting.RED), true);
            return false;
        }

        BlockPos spawnPos = altarPos.above();
        boolean bossNearby = !level.getEntitiesOfClass(Mob.class, new AABB(spawnPos).inflate(96.0D),
                mob -> mob.getType().equals(type) && mob.isAlive()).isEmpty();
        if (bossNearby) {
            player.displayClientMessage(Component.translatable("item.abyssworld.layer_boss_catalyst.already_summoned",
                    Component.translatable(bossTranslationKey)).withStyle(ChatFormatting.YELLOW), true);
            return false;
        }

        Mob boss = type.create(level);
        if (boss == null) {
            return false;
        }

        boss.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D,
                level.random.nextFloat() * 360.0F, 0.0F);
        if (level instanceof ServerLevel serverLevel) {
            boss.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(spawnPos),
                    MobSpawnType.EVENT, null, null);
        }
        boss.setPersistenceRequired();
        level.addFreshEntity(boss);
        level.playSound(null, spawnPos, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.2F, 0.8F);

        if (level.getServer() != null) {
            level.getServer().getPlayerList().broadcastSystemMessage(
                    Component.translatable("item.abyssworld.layer_boss_catalyst.summoned",
                            Component.translatable(bossTranslationKey))
                            .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD),
                    false);
        }

        if (!player.getAbilities().instabuild) {
            player.getItemInHand(hand).shrink(1);
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.abyssworld.layer_boss_catalyst.desc",
                Component.translatable(biomeTranslationKey),
                Component.translatable(bossTranslationKey)).withStyle(ChatFormatting.GRAY));
    }
}
