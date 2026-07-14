package com.abyssworld.item;

import net.minecraft.world.item.Item;

public class AbyssMachineUpgradeItem extends Item {
    public enum Type { SPEED, EFFICIENCY, CAPACITY, AUTO_EXPORT, RANGE, FACTORY }

    private final Type type;
    private final int level;

    public AbyssMachineUpgradeItem(Properties properties, Type type, int level) {
        super(properties);
        this.type = type;
        this.level = level;
    }

    public Type type() {
        return type;
    }

    public int level() {
        return level;
    }
}
