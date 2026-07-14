package com.abyssworld.worldgen;

import com.abyssworld.registry.ModBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public final class BoundaryScarFeature extends Feature<NoneFeatureConfiguration> {
    public BoundaryScarFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        BlockPos surface = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, origin);
        if (surface.getY() <= level.getMinBuildHeight() + 2
                || !level.getFluidState(surface).isEmpty()
                || !level.getBlockState(surface.below()).isFaceSturdy(level, surface.below(), Direction.UP)) {
            return false;
        }

        return switch (random.nextInt(4)) {
            case 0 -> placeDeadTree(level, surface, random);
            case 1 -> placeCrystalVent(level, surface, random);
            case 2 -> placeObelisk(level, surface, random);
            default -> placeRiftCrack(level, surface, random);
        };
    }

    private boolean placeDeadTree(WorldGenLevel level, BlockPos base, RandomSource random) {
        int height = 5 + random.nextInt(5);
        BlockState vertical = Blocks.STRIPPED_DARK_OAK_LOG.defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
        for (int y = 0; y < height; y++) {
            setBlock(level, base.above(y), vertical);
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            int branchY = height - 2 - random.nextInt(2);
            int length = 2 + random.nextInt(3);
            BlockState horizontal = Blocks.STRIPPED_DARK_OAK_LOG.defaultBlockState()
                    .setValue(RotatedPillarBlock.AXIS, direction.getAxis());
            for (int i = 1; i <= length; i++) {
                setBlock(level, base.above(branchY).relative(direction, i), horizontal);
            }
            if (random.nextBoolean()) {
                setBlock(level, base.above(branchY - 1).relative(direction, length), Blocks.COBWEB.defaultBlockState());
            }
        }
        return true;
    }

    private boolean placeCrystalVent(WorldGenLevel level, BlockPos base, RandomSource random) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (Math.abs(dx) + Math.abs(dz) <= 3 && random.nextInt(4) != 0) {
                    setBlock(level, base.offset(dx, -1, dz), ModBlocks.BOUNDARY_STONE.get().defaultBlockState());
                }
            }
        }
        int columns = 3 + random.nextInt(4);
        for (int i = 0; i < columns; i++) {
            BlockPos column = base.offset(random.nextInt(5) - 2, 0, random.nextInt(5) - 2);
            int height = 1 + random.nextInt(4);
            for (int y = 0; y < height; y++) {
                setBlock(level, column.above(y), y == height - 1
                        ? ModBlocks.RIFT_CORE.get().defaultBlockState()
                        : Blocks.BUDDING_AMETHYST.defaultBlockState());
            }
        }
        return true;
    }

    private boolean placeObelisk(WorldGenLevel level, BlockPos base, RandomSource random) {
        int height = 6 + random.nextInt(6);
        for (int y = -1; y < height; y++) {
            int radius = y <= 0 ? 2 : y < height - 2 ? 1 : 0;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (radius == 0 || Math.abs(dx) + Math.abs(dz) <= radius + 1) {
                        setBlock(level, base.offset(dx, y, dz), y == height / 2 && dx == 0 && dz == 0
                                ? ModBlocks.RIFT_CORE.get().defaultBlockState()
                                : ModBlocks.BOUNDARY_BRICKS.get().defaultBlockState());
                    }
                }
            }
        }
        return true;
    }

    private boolean placeRiftCrack(WorldGenLevel level, BlockPos base, RandomSource random) {
        BlockPos cursor = base;
        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        int length = 7 + random.nextInt(9);
        for (int i = 0; i < length; i++) {
            BlockPos floor = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, cursor).below();
            setBlock(level, floor, i == length / 2
                    ? ModBlocks.RIFT_CORE.get().defaultBlockState()
                    : (i % 3 == 0 ? Blocks.CRYING_OBSIDIAN : Blocks.OBSIDIAN).defaultBlockState());
            if (random.nextInt(3) == 0) {
                direction = random.nextBoolean() ? direction.getClockWise() : direction.getCounterClockWise();
            }
            cursor = cursor.relative(direction);
        }
        return true;
    }
}
