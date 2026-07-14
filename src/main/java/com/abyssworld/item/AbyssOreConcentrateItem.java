package com.abyssworld.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class AbyssOreConcentrateItem extends Item {
    private static final String GRADE_TAG = "AmplificationGrade";
    public static final int MIN_GRADE = 4;
    public static final int MAX_GRADE = 10;

    public AbyssOreConcentrateItem(Properties properties) {
        super(properties);
    }

    public static int grade(ItemStack stack) {
        int stored = stack.getOrCreateTag().getInt(GRADE_TAG);
        return Math.max(MIN_GRADE, Math.min(MAX_GRADE, stored));
    }

    public static ItemStack create(Item item, int count, int grade) {
        ItemStack stack = new ItemStack(item, count);
        stack.getOrCreateTag().putInt(GRADE_TAG, Math.max(MIN_GRADE, Math.min(MAX_GRADE, grade)));
        return stack;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return grade(stack) == MAX_GRADE;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.abyssworld.ore_concentrate.grade", grade(stack))
                .withStyle(ChatFormatting.AQUA));
    }
}
