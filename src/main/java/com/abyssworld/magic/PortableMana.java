package com.abyssworld.magic;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class PortableMana {
    private PortableMana() {
    }

    public static int available(Player player) {
        if (player.getAbilities().instabuild) {
            return Integer.MAX_VALUE;
        }
        long total = 0;
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.getItem() instanceof PortableManaContainer container) {
                total += container.storedMana(stack);
                if (total >= Integer.MAX_VALUE) {
                    return Integer.MAX_VALUE;
                }
            }
        }
        return (int) total;
    }

    public static boolean has(Player player, int amount) {
        return amount <= 0 || available(player) >= amount;
    }

    public static boolean consume(Player player, int amount) {
        if (amount <= 0 || player.getAbilities().instabuild) {
            return true;
        }
        if (!has(player, amount)) {
            return false;
        }

        int remaining = amount;
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.getItem() instanceof PortableManaContainer container) {
                remaining -= container.extractMana(stack, remaining);
            }
        }
        inventory.setChanged();
        return remaining == 0;
    }
}
