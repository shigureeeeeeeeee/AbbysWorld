package com.abyssworld.magic;

import net.minecraft.core.Direction;

public interface SideConfigurable {
    MachineSideMode cycleSideMode(Direction direction);
    MachineSideMode sideMode(Direction direction);
}
