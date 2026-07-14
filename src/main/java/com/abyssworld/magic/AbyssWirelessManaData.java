package com.abyssworld.magic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

public class AbyssWirelessManaData extends SavedData {
    public static final int CAPACITY = 10_000_000;
    private int mana;

    public static AbyssWirelessManaData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
                AbyssWirelessManaData::load, AbyssWirelessManaData::new, "abyssworld_wireless_mana");
    }

    public int mana() { return mana; }
    public int add(int amount) {
        int accepted = Math.min(Math.max(0, amount), CAPACITY - mana);
        if (accepted > 0) { mana += accepted; setDirty(); }
        return accepted;
    }
    public int consume(int amount) {
        int consumed = Math.min(Math.max(0, amount), mana);
        if (consumed > 0) { mana -= consumed; setDirty(); }
        return consumed;
    }
    @Override public CompoundTag save(CompoundTag tag) { tag.putInt("Mana", mana); return tag; }
    private static AbyssWirelessManaData load(CompoundTag tag) {
        AbyssWirelessManaData data = new AbyssWirelessManaData();
        data.mana = Math.max(0, Math.min(CAPACITY, tag.getInt("Mana"))); return data;
    }
}
