package com.abyssworld.worldgen.structure;

import com.abyssworld.registry.ModStructures;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Arrays;
import java.util.Optional;

public final class RiftfallCitadelStructure extends Structure {
    public static final Codec<RiftfallCitadelStructure> CODEC = simpleCodec(RiftfallCitadelStructure::new);

    public RiftfallCitadelStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunk = context.chunkPos();
        int centerX = chunk.getMiddleBlockX();
        int centerZ = chunk.getMiddleBlockZ();
        int baseY = Mth.clamp(sampleMedianSurface(context, centerX, centerZ) + 1,
                context.heightAccessor().getMinBuildHeight() + 24,
                context.heightAccessor().getMaxBuildHeight() - 48);
        int originX = centerX - 64;
        int originZ = centerZ - 64;

        return Optional.of(new GenerationStub(new BlockPos(centerX, baseY, centerZ), builder -> {
            add(builder, RiftfallCitadelPiece.Section.COURTYARD,
                    originX + 13, baseY, originZ + 13, originX + 115, baseY + 9, originZ + 103);

            add(builder, RiftfallCitadelPiece.Section.NORTH_WALL,
                    originX + 24, baseY, originZ, originX + 104, baseY + 18, originZ + 12);
            add(builder, RiftfallCitadelPiece.Section.SOUTH_GATE,
                    originX + 24, baseY, originZ + 104, originX + 104, baseY + 24, originZ + 128);
            add(builder, RiftfallCitadelPiece.Section.WEST_WALL,
                    originX, baseY, originZ + 24, originX + 12, baseY + 18, originZ + 104);
            add(builder, RiftfallCitadelPiece.Section.EAST_WALL,
                    originX + 116, baseY, originZ + 24, originX + 128, baseY + 18, originZ + 104);

            add(builder, RiftfallCitadelPiece.Section.NORTHWEST_TOWER,
                    originX, baseY, originZ, originX + 24, baseY + 31, originZ + 24);
            add(builder, RiftfallCitadelPiece.Section.NORTHEAST_TOWER,
                    originX + 104, baseY, originZ, originX + 128, baseY + 31, originZ + 24);
            add(builder, RiftfallCitadelPiece.Section.SOUTHWEST_TOWER,
                    originX, baseY, originZ + 104, originX + 24, baseY + 31, originZ + 128);
            add(builder, RiftfallCitadelPiece.Section.SOUTHEAST_TOWER,
                    originX + 104, baseY, originZ + 104, originX + 128, baseY + 31, originZ + 128);

            add(builder, RiftfallCitadelPiece.Section.BARRACKS,
                    originX + 15, baseY, originZ + 34, originX + 39, baseY + 16, originZ + 76);
            add(builder, RiftfallCitadelPiece.Section.WORKSHOP,
                    originX + 89, baseY, originZ + 34, originX + 113, baseY + 16, originZ + 76);
            add(builder, RiftfallCitadelPiece.Section.KEEP,
                    originX + 40, baseY, originZ + 25, originX + 88, baseY + 42, originZ + 78);
            add(builder, RiftfallCitadelPiece.Section.VAULT,
                    originX + 48, baseY - 15, originZ + 43, originX + 80, baseY, originZ + 75);
        }));
    }

    private static int sampleMedianSurface(GenerationContext context, int centerX, int centerZ) {
        int[] heights = new int[9];
        int index = 0;
        for (int dx : new int[]{-52, 0, 52}) {
            for (int dz : new int[]{-52, 0, 52}) {
                heights[index++] = context.chunkGenerator().getBaseHeight(centerX + dx, centerZ + dz,
                        Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
            }
        }
        Arrays.sort(heights);
        return heights[heights.length / 2];
    }

    private static void add(net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder builder,
                            RiftfallCitadelPiece.Section section,
                            int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        builder.addPiece(new RiftfallCitadelPiece(section,
                new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ)));
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.RIFTFALL_CITADEL.get();
    }
}
