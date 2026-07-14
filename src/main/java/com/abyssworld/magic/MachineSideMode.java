package com.abyssworld.magic;

import net.minecraft.util.StringRepresentable;

public enum MachineSideMode implements StringRepresentable {
    BOTH("both", true, true),
    INPUT("input", true, false),
    OUTPUT("output", false, true),
    DISABLED("disabled", false, false);

    private final String name;
    private final boolean input;
    private final boolean output;

    MachineSideMode(String name, boolean input, boolean output) {
        this.name = name;
        this.input = input;
        this.output = output;
    }

    public boolean allowsInput() {
        return input;
    }

    public boolean allowsOutput() {
        return output;
    }

    public MachineSideMode next() {
        MachineSideMode[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
