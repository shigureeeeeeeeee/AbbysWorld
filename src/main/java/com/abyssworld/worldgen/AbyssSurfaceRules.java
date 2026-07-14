package com.abyssworld.worldgen;

import com.abyssworld.registry.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.SurfaceRules;

public final class AbyssSurfaceRules {
    private AbyssSurfaceRules() {
    }

    public static SurfaceRules.RuleSource makeRules() {
        SurfaceRules.RuleSource soil = state(ModBlocks.BOUNDARY_SOIL.get());
        SurfaceRules.RuleSource stone = state(ModBlocks.BOUNDARY_STONE.get());
        SurfaceRules.RuleSource scarSurface = SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, soil),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, stone),
                SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR, stone));
        return SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.BOUNDARY_SCAR), scarSurface);
    }

    private static SurfaceRules.RuleSource state(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}
