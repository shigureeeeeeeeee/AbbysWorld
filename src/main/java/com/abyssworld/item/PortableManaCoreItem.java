package com.abyssworld.item;

import com.abyssworld.magic.PortableManaContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class PortableManaCoreItem extends Item implements PortableManaContainer {
    public static final int CAPACITY = 50_000;

    public PortableManaCoreItem(Properties properties) {
        super(properties);
    }

    @Override
    public int manaCapacity(ItemStack stack) {
        return CAPACITY;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return storedMana(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * storedMana(stack) / manaCapacity(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float ratio = (float) storedMana(stack) / manaCapacity(stack);
        return Mth.hsvToRgb(0.72F - ratio * 0.18F, 0.85F, 1.0F);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return storedMana(stack) >= manaCapacity(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.abyssworld.portable_mana_core.stored",
                storedMana(stack), manaCapacity(stack)).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("item.abyssworld.portable_mana_core.desc")
                .withStyle(ChatFormatting.GRAY));
    }
}
