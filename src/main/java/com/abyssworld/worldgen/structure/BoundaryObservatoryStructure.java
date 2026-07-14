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

import java.util.Optional;

public final class BoundaryObservatoryStructure extends Structure {
    public static final Codec<BoundaryObservatoryStructure> CODEC =
            simpleCodec(BoundaryObservatoryStructure::new);

    public BoundaryObservatoryStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunk = context.chunkPos();
        int centerX = chunk.getMiddleBlockX();
        int centerZ = chunk.getMiddleBlockZ();
        int surfaceY = sampleSurface(context, centerX, centerZ);
        int baseY = Mth.clamp(surfaceY + 1,
                context.heightAccessor().getMinBuildHeight() + 18,
                context.heightAccessor().getMaxBuildHeight() - 46);
        int originX = centerX - 48;
        int originZ = centerZ - 48;

        return Optional.of(new GenerationStub(new BlockPos(centerX, baseY, centerZ), builder -> {
            add(builder, BoundaryStructurePiece.Kind.OBSERVATORY_GATE,
                    originX + 34, baseY, originZ + 75, originX + 62, baseY + 20, originZ + 96);
            add(builder, BoundaryStructurePiece.Kind.OBSERVATORY_COURT,
                    originX + 28, baseY, originZ + 39, originX + 68, baseY + 18, originZ + 76);
            add(builder, BoundaryStructurePiece.Kind.OBSERVATORY_LAB,
                    originX, baseY, originZ + 36, originX + 30, baseY + 20, originZ + 68);
            add(builder, BoundaryStructurePiece.Kind.OBSERVATORY_ARCHIVE,
                    originX + 66, baseY, originZ + 36, originX + 96, baseY + 20, originZ + 68);
            add(builder, BoundaryStructurePiece.Kind.OBSERVATORY_TOWER,
                    originX + 31, baseY, originZ, originX + 65, baseY + 40, originZ + 40);
            add(builder, BoundaryStructurePiece.Kind.OBSERVATORY_SUBLEVEL,
                    originX + 31, baseY - 14, originZ + 40, originX + 65, baseY, originZ + 74);
        }));
    }

    private static int sampleSurface(GenerationContext context, int centerX, int centerZ) {
        int max = Integer.MIN_VALUE;
        for (int dx : new int[]{-40, 0, 40}) {
            for (int dz : new int[]{-40, 0, 40}) {
                max = Math.max(max, context.chunkGenerator().getBaseHeight(
                        centerX + dx, centerZ + dz, Heightmap.Types.WORLD_SURFACE_WG,
                        context.heightAccessor(), context.randomState()));
            }
        }
        return max;
    }

    private static void add(net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder builder,
                            BoundaryStructurePiece.Kind kind,
                            int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        builder.addPiece(new BoundaryStructurePiece(kind,
                new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ)));
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.BOUNDARY_OBSERVATORY.get();
    }
}
