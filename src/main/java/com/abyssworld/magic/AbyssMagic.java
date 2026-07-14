package com.abyssworld.magic;

import com.abyssworld.item.AbyssKeyItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class AbyssMagic {
    public static final int MAX_MANA = 200;
    public static final int MAX_STRAIN = 100;
    private static final String ROOT = "AbyssWorldMagic";
    private static final String MANA = "AbyssMana";
    private static final String STRAIN = "AbyssStrain";

    public enum Aspect {
        FOREST,
        ASH,
        FROST,
        FLESH,
        VOID,
        ABYSS
    }

    private AbyssMagic() {
    }

    public static int mana(Player player) {
        return data(player).getInt(MANA);
    }

    public static int strain(Player player) {
        return data(player).getInt(STRAIN);
    }

    public static void addMana(Player player, int amount) {
        CompoundTag data = data(player);
        data.putInt(MANA, clamp(data.getInt(MANA) + amount, 0, MAX_MANA));
    }

    public static boolean consumeMana(Player player, int amount) {
        CompoundTag data = data(player);
        int current = data.getInt(MANA);
        if (current < amount) {
            return false;
        }
        data.putInt(MANA, current - amount);
        return true;
    }

    public static void addStrain(Player player, int amount) {
        CompoundTag data = data(player);
        data.putInt(STRAIN, clamp(data.getInt(STRAIN) + amount, 0, MAX_STRAIN));
    }

    public static void reduceStrain(Player player, int amount) {
        addStrain(player, -amount);
    }

    public static void copy(Player original, Player revived) {
        revived.getPersistentData().put(ROOT, original.getPersistentData().getCompound(ROOT).copy());
    }

    public static Component status(Player player) {
        return Component.translatable("magic.abyssworld.status", mana(player), MAX_MANA, strain(player), MAX_STRAIN)
                .withStyle(strain(player) >= 80 ? ChatFormatting.RED : ChatFormatting.DARK_PURPLE);
    }

    public static Aspect aspectFor(ResourceKey<Level> dimension) {
        if (dimension.equals(AbyssKeyItem.FORGOTTEN_FOREST)) {
            return Aspect.FOREST;
        }
        if (dimension.equals(AbyssKeyItem.ASH_WASTELAND)) {
            return Aspect.ASH;
        }
        if (dimension.equals(AbyssKeyItem.FROZEN_CAVERN)) {
            return Aspect.FROST;
        }
        if (dimension.equals(AbyssKeyItem.FLESH_MINE)) {
            return Aspect.FLESH;
        }
        if (dimension.equals(AbyssKeyItem.VOID_CITY)) {
            return Aspect.VOID;
        }
        return Aspect.ABYSS;
    }

    private static CompoundTag data(Player player) {
        CompoundTag persistent = player.getPersistentData();
        if (!persistent.contains(ROOT)) {
            CompoundTag root = new CompoundTag();
            root.putInt(MANA, 40);
            root.putInt(STRAIN, 0);
            persistent.put(ROOT, root);
        }
        return persistent.getCompound(ROOT);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
