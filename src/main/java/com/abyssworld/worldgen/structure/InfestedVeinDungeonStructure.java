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

public final class InfestedVeinDungeonStructure extends Structure {
    public static final Codec<InfestedVeinDungeonStructure> CODEC =
            simpleCodec(InfestedVeinDungeonStructure::new);

    public InfestedVeinDungeonStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunk = context.chunkPos();
        int centerX = chunk.getMiddleBlockX();
        int centerZ = chunk.getMiddleBlockZ();
        int surface = context.chunkGenerator().getBaseHeight(centerX, centerZ,
                Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        int baseY = Mth.clamp(surface - 34,
                context.heightAccessor().getMinBuildHeight() + 12,
                context.heightAccessor().getMaxBuildHeight() - 24);

        return Optional.of(new GenerationStub(new BlockPos(centerX, baseY, centerZ), builder -> {
            add(builder, BoundaryStructurePiece.Kind.DUNGEON_HUB,
                    centerX - 12, baseY, centerZ - 12, centerX + 12, baseY + 13, centerZ + 12);
            add(builder, BoundaryStructurePiece.Kind.DUNGEON_NEST,
                    centerX - 11, baseY - 1, centerZ - 37, centerX + 11, baseY + 12, centerZ - 12);
            add(builder, BoundaryStructurePiece.Kind.DUNGEON_LAB,
                    centerX - 11, baseY - 2, centerZ + 12, centerX + 11, baseY + 11, centerZ + 37);
            add(builder, BoundaryStructurePiece.Kind.DUNGEON_CRYSTAL,
                    centerX + 12, baseY - 3, centerZ - 11, centerX + 38, baseY + 11, centerZ + 11);
            add(builder, BoundaryStructurePiece.Kind.DUNGEON_BONE,
                    centerX - 38, baseY - 1, centerZ - 11, centerX - 12, baseY + 12, centerZ + 11);
            add(builder, BoundaryStructurePiece.Kind.DUNGEON_VAULT,
                    centerX - 12, baseY - 4, centerZ - 59, centerX + 12, baseY + 12, centerZ - 37);
        }));
    }

    private static void add(net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder builder,
                            BoundaryStructurePiece.Kind kind,
                            int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        builder.addPiece(new BoundaryStructurePiece(kind,
                new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ)));
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.INFESTED_VEIN_DUNGEON.get();
    }
}
