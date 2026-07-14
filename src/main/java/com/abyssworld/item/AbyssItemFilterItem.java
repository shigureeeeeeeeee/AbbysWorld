package com.abyssworld.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AbyssItemFilterItem extends Item {
    public static final String FILTER_TAG = "Filter";

    public AbyssItemFilterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack filter = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            filter.getOrCreateTag().remove(FILTER_TAG);
            if (!level.isClientSide) player.displayClientMessage(
                    Component.translatable("item.abyssworld.item_filter.cleared").withStyle(ChatFormatting.GRAY), true);
            return InteractionResultHolder.sidedSuccess(filter, level.isClientSide);
        }
        InteractionHand other = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack sample = player.getItemInHand(other);
        if (!sample.isEmpty() && sample.getItem() != this) {
            ItemStack stored = sample.copyWithCount(1);
            filter.getOrCreateTag().put(FILTER_TAG, stored.save(new net.minecraft.nbt.CompoundTag()));
            if (!level.isClientSide) player.displayClientMessage(Component.translatable(
                    "item.abyssworld.item_filter.set", sample.getHoverName()).withStyle(ChatFormatting.AQUA), true);
            return InteractionResultHolder.sidedSuccess(filter, level.isClientSide);
        }
        return InteractionResultHolder.pass(filter);
    }

    public static ItemStack filter(ItemStack module) {
        return module.hasTag() && module.getTag().contains(FILTER_TAG)
                ? ItemStack.of(module.getTag().getCompound(FILTER_TAG)) : ItemStack.EMPTY;
    }
}
