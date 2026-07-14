package com.abyssworld.worldgen;

import com.abyssworld.registry.ModItems;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.util.RandomSource;

import java.util.function.Supplier;

public class LayerAltarFeature extends Feature<NoneFeatureConfiguration> {
    private final Supplier<? extends Block> altarBlock;
    private final BlockState floorState;
    private final BlockState pillarState;

    public LayerAltarFeature(Codec<NoneFeatureConfiguration> codec,
                             Supplier<? extends Block> altarBlock,
                             BlockState floorState,
                             BlockState pillarState) {
        super(codec);
        this.altarBlock = altarBlock;
        this.floorState = floorState;
        this.pillarState = pillarState;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos center = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, context.origin());
        if (center.getY() <= level.getMinBuildHeight() + 2 || center.getY() >= level.getMaxBuildHeight() - 4) {
            return false;
        }
        if (!canPlaceAt(level, center)) {
            return false;
        }

        RandomSource random = context.random();
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                int edge = Math.max(Math.abs(dx), Math.abs(dz));
                if (edge == 4 && random.nextInt(3) != 0) {
                    continue;
                }
                BlockPos floorPos = center.offset(dx, 0, dz);
                if (edge <= 2 || random.nextBoolean()) {
                    setBlock(level, floorPos, floorState);
                }
                clear(level, floorPos.above());
                clear(level, floorPos.above(2));
            }
        }

        placePillar(level, random, center.offset(2, 1, 0));
        placePillar(level, random, center.offset(-2, 1, 0));
        placePillar(level, random, center.offset(0, 1, 2));
        placePillar(level, random, center.offset(0, 1, -2));
        placePillar(level, random, center.offset(4, 1, 4));
        placePillar(level, random, center.offset(-4, 1, 4));
        placePillar(level, random, center.offset(4, 1, -4));
        placePillar(level, random, center.offset(-4, 1, -4));
        placeHazards(level, random, center);
        placeLootCache(level, random, center);
        setBlock(level, center.above(), altarBlock.get().defaultBlockState());
        clear(level, center.above(2));
        clear(level, center.above(3));
        return true;
    }

    private boolean canPlaceAt(WorldGenLevel level, BlockPos center) {
        BlockPos below = center.below();
        return level.getFluidState(center).isEmpty()
                && level.getFluidState(below).isEmpty()
                && level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    private void placePillar(WorldGenLevel level, RandomSource random, BlockPos pos) {
        setBlock(level, pos, pillarState);
        if (random.nextBoolean()) {
            setBlock(level, pos.above(), pillarState);
        }
        if (random.nextInt(4) == 0) {
            setBlock(level, pos.above(2), pillarState);
        }
    }

    private void placeHazards(WorldGenLevel level, RandomSource random, BlockPos center) {
        for (int i = 0; i < 4; i++) {
            BlockPos pos = center.offset(random.nextInt(7) - 3, 0, random.nextInt(7) - 3);
            if (!pos.equals(center)) {
                setBlock(level, pos, Blocks.MAGMA_BLOCK.defaultBlockState());
            }
        }
    }

    private void placeLootCache(WorldGenLevel level, RandomSource random, BlockPos center) {
        BlockPos chestPos = center.offset(random.nextBoolean() ? 3 : -3, 1, random.nextBoolean() ? 3 : -3);
        clear(level, chestPos);
        clear(level, chestPos.above());
        setBlock(level, chestPos, Blocks.CHEST.defaultBlockState());
        if (level.getBlockEntity(chestPos) instanceof Container container) {
            container.setItem(0, new ItemStack(ModItems.ABYSS_CRYSTAL.get(), 2 + random.nextInt(4)));
            container.setItem(1, new ItemStack(ModItems.ABYSS_IRON_INGOT.get(), 3 + random.nextInt(5)));
            if (random.nextBoolean()) {
                container.setItem(2, new ItemStack(ModItems.COMPRESSED_ABYSS_CRYSTAL.get()));
            }
            if (random.nextInt(3) == 0) {
                container.setItem(3, new ItemStack(ModItems.HIGH_DENSITY_ABYSS_ALLOY.get()));
            }
        }
    }

    private void clear(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.isAir()) {
            setBlock(level, pos, Blocks.AIR.defaultBlockState());
        }
    }
}
