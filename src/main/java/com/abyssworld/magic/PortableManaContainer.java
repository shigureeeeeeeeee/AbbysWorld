package com.abyssworld.magic;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public interface PortableManaContainer {
    String MANA_TAG = "PortableMana";

    int manaCapacity(ItemStack stack);

    default int storedMana(ItemStack stack) {
        return Mth.clamp(stack.getOrCreateTag().getInt(MANA_TAG), 0, manaCapacity(stack));
    }

    default int insertMana(ItemStack stack, int amount) {
        int accepted = Math.min(Math.max(0, amount), manaCapacity(stack) - storedMana(stack));
        if (accepted > 0) {
            stack.getOrCreateTag().putInt(MANA_TAG, storedMana(stack) + accepted);
        }
        return accepted;
    }

    default int extractMana(ItemStack stack, int amount) {
        int extracted = Math.min(Math.max(0, amount), storedMana(stack));
        if (extracted > 0) {
            stack.getOrCreateTag().putInt(MANA_TAG, storedMana(stack) - extracted);
        }
        return extracted;
    }
}
