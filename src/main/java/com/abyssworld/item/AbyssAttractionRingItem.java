package com.abyssworld.item;

import com.abyssworld.magic.PortableMana;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class AbyssAttractionRingItem extends Item {
    private static final String ENABLED_TAG = "AttractionEnabled";
    private static final int MANA_PER_PULSE = 3;
    private static final double RANGE = 7.0D;

    public AbyssAttractionRingItem(Properties properties) {
        super(properties);
    }

    public static boolean isEnabled(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(ENABLED_TAG);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            boolean enabled = !isEnabled(stack);
            stack.getOrCreateTag().putBoolean(ENABLED_TAG, enabled);
            player.displayClientMessage(Component.translatable(
                    enabled ? "item.abyssworld.abyss_attraction_ring.enabled"
                            : "item.abyssworld.abyss_attraction_ring.disabled")
                    .withStyle(enabled ? ChatFormatting.AQUA : ChatFormatting.GRAY), true);
            level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME,
                    SoundSource.PLAYERS, 0.7F, enabled ? 1.45F : 0.75F);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide || !(entity instanceof Player player) || player.tickCount % 10 != 0
                || !isEnabled(stack) || !isFirstEnabledRing(player, stack)) {
            return;
        }

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class,
                player.getBoundingBox().inflate(RANGE), item -> item.isAlive() && !item.getItem().isEmpty());
        if (items.isEmpty() || !PortableMana.consume(player, MANA_PER_PULSE)) {
            return;
        }

        Vec3 destination = player.position().add(0.0D, 0.75D, 0.0D);
        for (ItemEntity item : items) {
            Vec3 offset = destination.subtract(item.position());
            if (offset.lengthSqr() < 0.04D) {
                continue;
            }
            double strength = offset.lengthSqr() > 9.0D ? 0.34D : 0.22D;
            item.setDeltaMovement(item.getDeltaMovement().scale(0.55D)
                    .add(offset.normalize().scale(strength)));
            item.setNoPickUpDelay();
        }
    }

    private static boolean isFirstEnabledRing(Player player, ItemStack current) {
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.getItem() instanceof AbyssAttractionRingItem && isEnabled(stack)) {
                return stack == current;
            }
        }
        return false;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isEnabled(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.abyssworld.abyss_attraction_ring.status",
                Component.translatable(isEnabled(stack)
                        ? "item.abyssworld.status.enabled" : "item.abyssworld.status.disabled"))
                .withStyle(isEnabled(stack) ? ChatFormatting.AQUA : ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.abyssworld.abyss_attraction_ring.desc", (int) RANGE)
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.abyssworld.abyss_attraction_ring.cost", MANA_PER_PULSE)
                .withStyle(ChatFormatting.DARK_PURPLE));
    }
}
