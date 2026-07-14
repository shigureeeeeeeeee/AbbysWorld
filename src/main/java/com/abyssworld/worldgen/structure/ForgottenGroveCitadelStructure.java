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

public class ForgottenGroveCitadelStructure extends Structure {
    public static final Codec<ForgottenGroveCitadelStructure> CODEC =
            simpleCodec(ForgottenGroveCitadelStructure::new);

    public ForgottenGroveCitadelStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunk = context.chunkPos();
        int centerX = chunk.getMiddleBlockX();
        int centerZ = chunk.getMiddleBlockZ();
        int surfaceY = sampleSurface(context, centerX, centerZ);
        int baseY = Mth.clamp(surfaceY + 1,
                context.heightAccessor().getMinBuildHeight() + 12,
                context.heightAccessor().getMaxBuildHeight() - 28);
        int originX = centerX - 48;
        int originZ = centerZ - 48;

        return Optional.of(new GenerationStub(new BlockPos(centerX, baseY, centerZ), builder -> {
            addPiece(builder, ForgottenGroveCitadelPiece.Room.GATE,
                    originX + 34, baseY, originZ + 78, originX + 62, baseY + 19, originZ + 96);
            addPiece(builder, ForgottenGroveCitadelPiece.Room.COURTYARD,
                    originX + 24, baseY, originZ + 38, originX + 72, baseY + 16, originZ + 79);
            addPiece(builder, ForgottenGroveCitadelPiece.Room.WEST_WING,
                    originX, baseY, originZ + 36, originX + 31, baseY + 18, originZ + 66);
            addPiece(builder, ForgottenGroveCitadelPiece.Room.EAST_WING,
                    originX + 65, baseY, originZ + 36, originX + 96, baseY + 18, originZ + 66);
            addPiece(builder, ForgottenGroveCitadelPiece.Room.SEALED_HALL,
                    originX + 35, baseY, originZ + 16, originX + 61, baseY + 18, originZ + 43);
            addPiece(builder, ForgottenGroveCitadelPiece.Room.BOSS_SANCTUM,
                    originX + 28, baseY, originZ, originX + 68, baseY + 24, originZ + 20);
        }));
    }

    private static int sampleSurface(GenerationContext context, int centerX, int centerZ) {
        int max = Integer.MIN_VALUE;
        int[] offsets = {-40, 0, 40};
        for (int dx : offsets) {
            for (int dz : offsets) {
                max = Math.max(max, context.chunkGenerator().getBaseHeight(
                        centerX + dx, centerZ + dz, Heightmap.Types.WORLD_SURFACE_WG,
                        context.heightAccessor(), context.randomState()));
            }
        }
        return max;
    }

    private static void addPiece(net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder builder,
                                 ForgottenGroveCitadelPiece.Room room,
                                 int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        builder.addPiece(new ForgottenGroveCitadelPiece(room,
                new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ)));
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.FORGOTTEN_GROVE_CITADEL.get();
    }
}
