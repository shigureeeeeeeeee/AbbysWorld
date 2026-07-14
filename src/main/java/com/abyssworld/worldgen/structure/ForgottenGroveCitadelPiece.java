package com.abyssworld.worldgen.structure;

import com.abyssworld.AbyssWorld;
import com.abyssworld.registry.ModBlocks;
import com.abyssworld.registry.ModEntities;
import com.abyssworld.registry.ModStructures;
import com.abyssworld.entity.GroveSentinelEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.AABB;

public class ForgottenGroveCitadelPiece extends StructurePiece {
    private static final ResourceLocation COMMON_LOOT =
            ResourceLocation.fromNamespaceAndPath(AbyssWorld.MODID, "chests/forgotten_grove_citadel");
    private static final ResourceLocation VAULT_LOOT =
            ResourceLocation.fromNamespaceAndPath(AbyssWorld.MODID, "chests/forgotten_grove_vault");

    private final Room room;

    public ForgottenGroveCitadelPiece(Room room, BoundingBox boundingBox) {
        super(ModStructures.FORGOTTEN_GROVE_CITADEL_PIECE.get(), 0, boundingBox);
        this.room = room;
    }

    public ForgottenGroveCitadelPiece(CompoundTag tag) {
        super(ModStructures.FORGOTTEN_GROVE_CITADEL_PIECE.get(), tag);
        this.room = Room.byName(tag.getString("Room"));
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putString("Room", room.serializedName);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager,
                            ChunkGenerator chunkGenerator, RandomSource random,
                            BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        int width = boundingBox.maxX() - boundingBox.minX();
        int depth = boundingBox.maxZ() - boundingBox.minZ();
        int wallHeight = wallHeight();

        buildFoundation(level, chunkBox, width, depth);
        clear(level, chunkBox, 1, 1, 1, width - 1, wallHeight - 1, depth - 1);
        buildFloor(level, chunkBox, width, depth);
        buildWalls(level, chunkBox, width, depth, wallHeight);
        if (room != Room.COURTYARD) {
            buildRoof(level, chunkBox, width, depth, wallHeight);
        }
        carveConnections(level, chunkBox, width, depth, wallHeight);

        switch (room) {
            case GATE -> decorateGate(level, chunkBox, width, depth);
            case COURTYARD -> decorateCourtyard(level, chunkBox, width, depth);
            case WEST_WING -> decorateWestWing(level, chunkBox, random, width, depth);
            case EAST_WING -> decorateEastWing(level, chunkBox, random, width, depth);
            case SEALED_HALL -> decorateSealedHall(level, chunkBox, width, depth);
            case BOSS_SANCTUM -> decorateBossSanctum(level, chunkBox, random, width, depth);
        }
    }

    private int wallHeight() {
        return switch (room) {
            case COURTYARD -> 10;
            case BOSS_SANCTUM -> 16;
            default -> 12;
        };
    }

    private void buildFoundation(WorldGenLevel level, BoundingBox chunkBox, int width, int depth) {
        for (int x = 0; x <= width; x++) {
            for (int z = 0; z <= depth; z++) {
                for (int y = 0; y >= -18; y--) {
                    BlockPos pos = worldPos(x, y, z);
                    if (!chunkBox.isInside(pos) || !level.isOutsideBuildHeight(pos)) {
                        if (chunkBox.isInside(pos)) {
                            BlockState existing = level.getBlockState(pos);
                            if (y < 0 && !existing.isAir() && !existing.canBeReplaced()) {
                                break;
                            }
                            set(level, chunkBox, x, y, z, foundationPalette(pos));
                        }
                    }
                }
            }
        }
    }

    private void buildFloor(WorldGenLevel level, BoundingBox chunkBox, int width, int depth) {
        for (int x = 0; x <= width; x++) {
            for (int z = 0; z <= depth; z++) {
                BlockPos pos = worldPos(x, 0, z);
                BlockState state = ((x + z) % 7 == 0 || (x - z) % 11 == 0)
                        ? Blocks.CHISELED_STONE_BRICKS.defaultBlockState()
                        : wallPalette(pos);
                set(level, chunkBox, x, 0, z, state);
            }
        }
    }

    private void buildWalls(WorldGenLevel level, BoundingBox chunkBox,
                            int width, int depth, int height) {
        for (int y = 1; y <= height; y++) {
            for (int x = 0; x <= width; x++) {
                set(level, chunkBox, x, y, 0, wallPalette(worldPos(x, y, 0)));
                set(level, chunkBox, x, y, depth, wallPalette(worldPos(x, y, depth)));
            }
            for (int z = 1; z < depth; z++) {
                set(level, chunkBox, 0, y, z, wallPalette(worldPos(0, y, z)));
                set(level, chunkBox, width, y, z, wallPalette(worldPos(width, y, z)));
            }
        }

        for (int x = 0; x <= width; x += 6) {
            pillar(level, chunkBox, x, 1, 0, height + 2);
            pillar(level, chunkBox, x, 1, depth, height + 2);
        }
        for (int z = 0; z <= depth; z += 6) {
            pillar(level, chunkBox, 0, 1, z, height + 2);
            pillar(level, chunkBox, width, 1, z, height + 2);
        }
    }

    private void buildRoof(WorldGenLevel level, BoundingBox chunkBox,
                           int width, int depth, int height) {
        for (int inset = 0; inset <= 3; inset++) {
            int y = height + inset;
            int minX = 2 + inset;
            int maxX = width - 2 - inset;
            int minZ = 2 + inset;
            int maxZ = depth - 2 - inset;
            if (minX > maxX || minZ > maxZ) {
                break;
            }
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (inset == 3 || x == minX || x == maxX || z == minZ || z == maxZ) {
                        set(level, chunkBox, x, y, z, roofPalette(worldPos(x, y, z)));
                    }
                }
            }
        }
    }

    private void carveConnections(WorldGenLevel level, BoundingBox chunkBox,
                                  int width, int depth, int wallHeight) {
        int centerX = width / 2;
        int centerZ = depth / 2;
        switch (room) {
            case GATE -> {
                doorwayZ(level, chunkBox, centerX, depth - 3, depth, 6);
                doorwayZ(level, chunkBox, centerX, 0, 3, 6);
            }
            case COURTYARD -> {
                doorwayZ(level, chunkBox, centerX, depth - 5, depth, 6);
                doorwayZ(level, chunkBox, centerX, 0, 5, 6);
                doorwayX(level, chunkBox, 0, 6, centerZ, 6);
                doorwayX(level, chunkBox, width - 6, width, centerZ, 6);
            }
            case WEST_WING -> doorwayX(level, chunkBox, width - 6, width, centerZ, 6);
            case EAST_WING -> doorwayX(level, chunkBox, 0, 6, centerZ, 6);
            case SEALED_HALL -> {
                doorwayZ(level, chunkBox, centerX, depth - 5, depth, 7);
                doorwayZ(level, chunkBox, centerX, 0, 5, 8);
            }
            case BOSS_SANCTUM -> doorwayZ(level, chunkBox, centerX, depth - 5, depth, 8);
        }

        if (room == Room.COURTYARD) {
            for (int x = 3; x < width; x += 6) {
                set(level, chunkBox, x, wallHeight + 1, 0, Blocks.MOSSY_STONE_BRICK_WALL.defaultBlockState());
                set(level, chunkBox, x, wallHeight + 1, depth, Blocks.MOSSY_STONE_BRICK_WALL.defaultBlockState());
            }
        }
    }

    private void decorateGate(WorldGenLevel level, BoundingBox chunkBox, int width, int depth) {
        tower(level, chunkBox, 1, depth - 8, 7, 18);
        tower(level, chunkBox, width - 8, depth - 8, 7, 18);
        tower(level, chunkBox, 1, 1, 6, 16);
        tower(level, chunkBox, width - 7, 1, 6, 16);
        int centerX = width / 2;
        for (int z = 4; z < depth - 3; z += 5) {
            set(level, chunkBox, centerX - 4, 1, z, Blocks.SOUL_LANTERN.defaultBlockState());
            set(level, chunkBox, centerX + 4, 1, z, Blocks.SOUL_LANTERN.defaultBlockState());
        }
        for (int y = 7; y <= 11; y++) {
            set(level, chunkBox, centerX - 5, y, depth, Blocks.CHISELED_STONE_BRICKS.defaultBlockState());
            set(level, chunkBox, centerX + 5, y, depth, Blocks.CHISELED_STONE_BRICKS.defaultBlockState());
        }
    }

    private void decorateCourtyard(WorldGenLevel level, BoundingBox chunkBox, int width, int depth) {
        int centerX = width / 2;
        int centerZ = depth / 2;
        for (int radius = 4; radius <= 10; radius += 3) {
            for (int x = centerX - radius; x <= centerX + radius; x++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    int distance = Math.abs(x - centerX) + Math.abs(z - centerZ);
                    if (distance == radius || distance == radius + 1) {
                        set(level, chunkBox, x, 1, z, Blocks.MOSS_CARPET.defaultBlockState());
                    }
                }
            }
        }
        BlockState logY = Blocks.DARK_OAK_LOG.defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
        for (int y = 1; y <= 14; y++) {
            int spread = y < 5 ? 2 : 1;
            for (int x = centerX - spread; x <= centerX + spread; x++) {
                for (int z = centerZ - spread; z <= centerZ + spread; z++) {
                    if ((x + z + y) % 3 != 0) {
                        set(level, chunkBox, x, y, z, logY);
                    }
                }
            }
        }
        branch(level, chunkBox, centerX, 10, centerZ, Direction.NORTH, 8);
        branch(level, chunkBox, centerX, 12, centerZ, Direction.SOUTH, 7);
        branch(level, chunkBox, centerX, 9, centerZ, Direction.WEST, 9);
        branch(level, chunkBox, centerX, 13, centerZ, Direction.EAST, 8);
        for (int x = 5; x < width - 4; x += 9) {
            set(level, chunkBox, x, 2, 3, Blocks.SOUL_LANTERN.defaultBlockState());
            set(level, chunkBox, x, 2, depth - 3, Blocks.SOUL_LANTERN.defaultBlockState());
        }
    }

    private void decorateWestWing(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                                  int width, int depth) {
        int centerX = width / 2;
        int centerZ = depth / 2;
        for (int x = 5; x <= width - 5; x += 7) {
            pillar(level, chunkBox, x, 1, 5, 8);
            pillar(level, chunkBox, x, 1, depth - 5, 8);
        }
        ritualRing(level, chunkBox, centerX, centerZ, 7);
        placeChest(level, chunkBox, random, 5, 1, 5, COMMON_LOOT, Direction.SOUTH);
        placeChest(level, chunkBox, random, 5, 1, depth - 5, COMMON_LOOT, Direction.NORTH);
        spawnGroveSentinel(level, chunkBox, centerX, centerZ);
    }

    private void decorateEastWing(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                                  int width, int depth) {
        for (int x = 5; x < width - 4; x += 6) {
            for (int z = 4; z < depth - 3; z++) {
                if ((x / 6 + z / 5) % 2 == 0 && z % 6 != 0) {
                    set(level, chunkBox, x, 1, z, Blocks.CHISELED_BOOKSHELF.defaultBlockState());
                    set(level, chunkBox, x, 2, z, Blocks.BOOKSHELF.defaultBlockState());
                    set(level, chunkBox, x, 3, z, Blocks.MOSSY_STONE_BRICKS.defaultBlockState());
                }
            }
        }
        int centerX = width / 2;
        int centerZ = depth / 2;
        box(level, chunkBox, centerX - 4, 1, centerZ - 4,
                centerX + 4, 1, centerZ + 4, Blocks.POLISHED_DEEPSLATE.defaultBlockState());
        placeChest(level, chunkBox, random, centerX, 2, centerZ, VAULT_LOOT, Direction.SOUTH);
        set(level, chunkBox, centerX - 3, 2, centerZ, Blocks.SOUL_LANTERN.defaultBlockState());
        set(level, chunkBox, centerX + 3, 2, centerZ, Blocks.SOUL_LANTERN.defaultBlockState());
    }

    private void decorateSealedHall(WorldGenLevel level, BoundingBox chunkBox, int width, int depth) {
        int centerX = width / 2;
        for (int z = 6; z < depth - 4; z += 5) {
            pillar(level, chunkBox, 4, 1, z, 9);
            pillar(level, chunkBox, width - 4, 1, z, 9);
            set(level, chunkBox, 7, 2, z, Blocks.SOUL_LANTERN.defaultBlockState());
            set(level, chunkBox, width - 7, 2, z, Blocks.SOUL_LANTERN.defaultBlockState());
        }
        for (int x = centerX - 4; x <= centerX + 4; x++) {
            for (int y = 1; y <= 8; y++) {
                if (Math.abs(x - centerX) + y <= 11) {
                    set(level, chunkBox, x, y, 5, ModBlocks.GROVE_SEAL.get().defaultBlockState());
                }
            }
        }
    }

    private void spawnGroveSentinel(WorldGenLevel level, BoundingBox chunkBox, int x, int z) {
        BlockPos spawnPos = worldPos(x, 2, z);
        if (!chunkBox.isInside(spawnPos)) {
            return;
        }
        AABB searchArea = new AABB(spawnPos).inflate(10.0D, 5.0D, 10.0D);
        if (!level.getLevel().getEntitiesOfClass(GroveSentinelEntity.class, searchArea).isEmpty()) {
            return;
        }
        GroveSentinelEntity sentinel = ModEntities.GROVE_SENTINEL.get().create(level.getLevel());
        if (sentinel == null) {
            return;
        }
        sentinel.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D,
                level.getRandom().nextFloat() * 360.0F, 0.0F);
        sentinel.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos),
                MobSpawnType.STRUCTURE, null, null);
        sentinel.setPersistenceRequired();
        level.addFreshEntity(sentinel);
    }

    private void decorateBossSanctum(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                                     int width, int depth) {
        int centerX = width / 2;
        int centerZ = depth / 2;
        for (int x : new int[]{4, width - 4}) {
            for (int z : new int[]{4, depth - 4}) {
                tower(level, chunkBox, x - 2, z - 2, 4, 22);
            }
        }
        for (int radius = 3; radius <= 9; radius += 3) {
            ritualRing(level, chunkBox, centerX, centerZ, radius);
        }
        box(level, chunkBox, centerX - 2, 1, centerZ - 2,
                centerX + 2, 1, centerZ + 2, Blocks.POLISHED_DEEPSLATE.defaultBlockState());
        set(level, chunkBox, centerX, 2, centerZ, ModBlocks.FORGOTTEN_FOREST_ALTAR.get().defaultBlockState());
        placeChest(level, chunkBox, random, 6, 2, centerZ, VAULT_LOOT, Direction.EAST);
        placeChest(level, chunkBox, random, width - 6, 2, centerZ, VAULT_LOOT, Direction.WEST);
    }

    private void tower(WorldGenLevel level, BoundingBox chunkBox,
                       int startX, int startZ, int size, int height) {
        for (int y = 1; y <= height; y++) {
            for (int x = startX; x <= startX + size; x++) {
                for (int z = startZ; z <= startZ + size; z++) {
                    boolean edge = x == startX || x == startX + size || z == startZ || z == startZ + size;
                    if (edge) {
                        set(level, chunkBox, x, y, z, wallPalette(worldPos(x, y, z)));
                    } else if (y < height) {
                        set(level, chunkBox, x, y, z, Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }
        box(level, chunkBox, startX, height, startZ,
                startX + size, height, startZ + size, Blocks.POLISHED_DEEPSLATE.defaultBlockState());
        for (int x = startX; x <= startX + size; x += 2) {
            set(level, chunkBox, x, height + 1, startZ, Blocks.MOSSY_STONE_BRICK_WALL.defaultBlockState());
            set(level, chunkBox, x, height + 1, startZ + size, Blocks.MOSSY_STONE_BRICK_WALL.defaultBlockState());
        }
        for (int z = startZ; z <= startZ + size; z += 2) {
            set(level, chunkBox, startX, height + 1, z, Blocks.MOSSY_STONE_BRICK_WALL.defaultBlockState());
            set(level, chunkBox, startX + size, height + 1, z, Blocks.MOSSY_STONE_BRICK_WALL.defaultBlockState());
        }
    }

    private void branch(WorldGenLevel level, BoundingBox chunkBox, int x, int y, int z,
                        Direction direction, int length) {
        BlockState state = Blocks.DARK_OAK_LOG.defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, direction.getAxis());
        for (int i = 1; i <= length; i++) {
            int rise = i / 4;
            set(level, chunkBox, x + direction.getStepX() * i, y + rise,
                    z + direction.getStepZ() * i, state);
        }
    }

    private void ritualRing(WorldGenLevel level, BoundingBox chunkBox,
                            int centerX, int centerZ, int radius) {
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                int distance = Math.abs(x - centerX) + Math.abs(z - centerZ);
                if (distance == radius) {
                    set(level, chunkBox, x, 1, z,
                            (x + z) % 3 == 0 ? Blocks.SCULK.defaultBlockState()
                                    : Blocks.POLISHED_DEEPSLATE.defaultBlockState());
                }
            }
        }
    }

    private void pillar(WorldGenLevel level, BoundingBox chunkBox,
                        int x, int minY, int z, int maxY) {
        for (int y = minY; y <= maxY; y++) {
            set(level, chunkBox, x, y, z, y % 5 == 0
                    ? Blocks.CHISELED_STONE_BRICKS.defaultBlockState()
                    : Blocks.POLISHED_DEEPSLATE.defaultBlockState());
        }
    }

    private void doorwayZ(WorldGenLevel level, BoundingBox chunkBox,
                          int centerX, int minZ, int maxZ, int height) {
        clear(level, chunkBox, centerX - 3, 1, minZ, centerX + 3, height, maxZ);
    }

    private void doorwayX(WorldGenLevel level, BoundingBox chunkBox,
                          int minX, int maxX, int centerZ, int height) {
        clear(level, chunkBox, minX, 1, centerZ - 3, maxX, height, centerZ + 3);
    }

    private void placeChest(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                            int x, int y, int z, ResourceLocation loot, Direction facing) {
        BlockPos pos = worldPos(x, y, z);
        if (chunkBox.isInside(pos)) {
            createChest(level, chunkBox, random, pos, loot,
                    Blocks.CHEST.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing));
        }
    }

    private void box(WorldGenLevel level, BoundingBox chunkBox,
                     int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState state) {
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    set(level, chunkBox, x, y, z, state);
                }
            }
        }
    }

    private void clear(WorldGenLevel level, BoundingBox chunkBox,
                       int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        box(level, chunkBox, minX, minY, minZ, maxX, maxY, maxZ, Blocks.AIR.defaultBlockState());
    }

    private void set(WorldGenLevel level, BoundingBox chunkBox,
                     int x, int y, int z, BlockState state) {
        BlockPos pos = worldPos(x, y, z);
        if (chunkBox.isInside(pos) && !level.isOutsideBuildHeight(pos)) {
            level.setBlock(pos, state, 2);
        }
    }

    private BlockPos worldPos(int x, int y, int z) {
        return new BlockPos(boundingBox.minX() + x, boundingBox.minY() + y, boundingBox.minZ() + z);
    }

    private static BlockState foundationPalette(BlockPos pos) {
        return hash(pos) % 5 == 0
                ? Blocks.COBBLED_DEEPSLATE.defaultBlockState()
                : ModBlocks.FORGOTTEN_STONE.get().defaultBlockState();
    }

    private static BlockState wallPalette(BlockPos pos) {
        return switch (hash(pos) % 12) {
            case 0, 1 -> Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
            case 2 -> Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
            case 3 -> ModBlocks.FORGOTTEN_STONE.get().defaultBlockState();
            default -> Blocks.STONE_BRICKS.defaultBlockState();
        };
    }

    private static BlockState roofPalette(BlockPos pos) {
        return hash(pos) % 7 == 0
                ? Blocks.MOSSY_COBBLESTONE.defaultBlockState()
                : Blocks.POLISHED_DEEPSLATE.defaultBlockState();
    }

    private static int hash(BlockPos pos) {
        long value = pos.asLong() * 31L + pos.getX() * 73428767L + pos.getZ() * 912367L;
        return (int) Math.floorMod(value ^ (value >>> 32), 97L);
    }

    public enum Room {
        GATE("gate"),
        COURTYARD("courtyard"),
        WEST_WING("west_wing"),
        EAST_WING("east_wing"),
        SEALED_HALL("sealed_hall"),
        BOSS_SANCTUM("boss_sanctum");

        private final String serializedName;

        Room(String serializedName) {
            this.serializedName = serializedName;
        }

        private static Room byName(String name) {
            for (Room room : values()) {
                if (room.serializedName.equals(name)) {
                    return room;
                }
            }
            return GATE;
        }
    }
}
