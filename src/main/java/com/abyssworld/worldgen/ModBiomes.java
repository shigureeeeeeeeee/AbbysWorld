package com.abyssworld.worldgen;

import com.abyssworld.AbyssWorld;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public final class ModBiomes {
    public static final ResourceKey<Biome> BOUNDARY_SCAR = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(AbyssWorld.MODID, "boundary_scar"));

    private ModBiomes() {
    }
}
