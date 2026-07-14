package com.abyssworld.item;

import com.abyssworld.magic.PortableMana;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class AbyssReturnTalismanItem extends Item {
    private static final String DIMENSION_TAG = "ReturnDimension";
    private static final String POSITION_TAG = "ReturnPosition";
    private static final int CROSS_DIMENSION_COST = 8_000;

    public AbyssReturnTalismanItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        if (player.isShiftKeyDown()) {
            bind(stack, player);
            return InteractionResultHolder.success(stack);
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.fail(stack);
        }
        return teleport(stack, serverPlayer)
                ? InteractionResultHolder.success(stack) : InteractionResultHolder.fail(stack);
    }

    private static void bind(ItemStack stack, Player player) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(DIMENSION_TAG, player.level().dimension().location().toString());
        tag.putLong(POSITION_TAG, player.blockPosition().asLong());
        player.displayClientMessage(Component.translatable("item.abyssworld.abyss_return_talisman.bound",
                player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ())
                .withStyle(ChatFormatting.AQUA), true);
        player.level().playSound(null, player.blockPosition(), SoundEvents.LODESTONE_COMPASS_LOCK,
                SoundSource.PLAYERS, 0.8F, 1.3F);
    }

    private boolean teleport(ItemStack stack, ServerPlayer player) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(DIMENSION_TAG) || !tag.contains(POSITION_TAG)) {
            player.displayClientMessage(Component.translatable(
                    "item.abyssworld.abyss_return_talisman.unbound").withStyle(ChatFormatting.RED), true);
            return false;
        }

        ResourceLocation location = ResourceLocation.tryParse(tag.getString(DIMENSION_TAG));
        if (location == null) {
            return false;
        }
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, location);
        ServerLevel targetLevel = player.server.getLevel(dimension);
        if (targetLevel == null) {
            player.displayClientMessage(Component.translatable(
                    "item.abyssworld.abyss_return_talisman.invalid").withStyle(ChatFormatting.RED), true);
            return false;
        }

        BlockPos target = BlockPos.of(tag.getLong(POSITION_TAG));
        targetLevel.getChunkAt(target);
        BlockPos destination = findSafeDestination(targetLevel, target);
        if (destination == null) {
            player.displayClientMessage(Component.translatable(
                    "item.abyssworld.abyss_return_talisman.unsafe").withStyle(ChatFormatting.RED), true);
            return false;
        }
        boolean crossDimension = player.level() != targetLevel;
        int cost = crossDimension ? CROSS_DIMENSION_COST
                : 500 + Math.min(4_500, (int) Math.sqrt(player.blockPosition().distSqr(target)));
        if (!PortableMana.consume(player, cost)) {
            player.displayClientMessage(Component.translatable(
                    "item.abyssworld.portable_mana.no_mana", cost).withStyle(ChatFormatting.RED), true);
            return false;
        }

        player.level().playSound(null, player.blockPosition(), SoundEvents.PORTAL_TRAVEL,
                SoundSource.PLAYERS, 0.55F, 1.35F);
        player.stopRiding();
        player.teleportTo(targetLevel, destination.getX() + 0.5D, destination.getY() + 0.1D,
                destination.getZ() + 0.5D, player.getYRot(), player.getXRot());
        targetLevel.playSound(null, destination, SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS, 1.0F, 0.8F);
        player.getCooldowns().addCooldown(this, 100);
        player.displayClientMessage(Component.translatable(
                "item.abyssworld.abyss_return_talisman.teleported", cost)
                .withStyle(ChatFormatting.LIGHT_PURPLE), true);
        return true;
    }

    @Nullable
    private static BlockPos findSafeDestination(ServerLevel level, BlockPos target) {
        for (int distance = 0; distance <= 6; distance++) {
            BlockPos above = target.above(distance);
            if (isSafe(level, above)) {
                return above;
            }
            if (distance > 0) {
                BlockPos below = target.below(distance);
                if (isSafe(level, below)) {
                    return below;
                }
            }
        }
        return null;
    }

    private static boolean isSafe(ServerLevel level, BlockPos feet) {
        if (!level.getWorldBorder().isWithinBounds(feet)
                || feet.getY() <= level.getMinBuildHeight()
                || feet.getY() + 1 >= level.getMaxBuildHeight()) {
            return false;
        }
        return level.getBlockState(feet).getCollisionShape(level, feet).isEmpty()
                && level.getBlockState(feet.above()).getCollisionShape(level, feet.above()).isEmpty()
                && !level.getBlockState(feet.below()).getCollisionShape(level, feet.below()).isEmpty();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(POSITION_TAG);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(DIMENSION_TAG) && tag.contains(POSITION_TAG)) {
            BlockPos pos = BlockPos.of(tag.getLong(POSITION_TAG));
            tooltip.add(Component.translatable("item.abyssworld.abyss_return_talisman.destination",
                    pos.getX(), pos.getY(), pos.getZ(), tag.getString(DIMENSION_TAG))
                    .withStyle(ChatFormatting.AQUA));
        } else {
            tooltip.add(Component.translatable("item.abyssworld.abyss_return_talisman.unbound_tooltip")
                    .withStyle(ChatFormatting.GRAY));
        }
        tooltip.add(Component.translatable("item.abyssworld.abyss_return_talisman.desc")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.abyssworld.abyss_return_talisman.hint")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
