package com.abyssworld.worldgen.structure;

import com.abyssworld.AbyssWorld;
import com.abyssworld.registry.ModBlocks;
import com.abyssworld.registry.ModEntities;
import com.abyssworld.registry.ModStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.phys.AABB;

public final class RiftfallCitadelPiece extends StructurePiece {
    private static final ResourceLocation COMMON_LOOT =
            ResourceLocation.fromNamespaceAndPath(AbyssWorld.MODID, "chests/riftfall_citadel");
    private static final ResourceLocation TOWER_LOOT =
            ResourceLocation.fromNamespaceAndPath(AbyssWorld.MODID, "chests/riftfall_citadel_tower");
    private static final ResourceLocation VAULT_LOOT =
            ResourceLocation.fromNamespaceAndPath(AbyssWorld.MODID, "chests/riftfall_citadel_vault");

    private final Section section;

    public RiftfallCitadelPiece(Section section, BoundingBox boundingBox) {
        super(ModStructures.RIFTFALL_CITADEL_PIECE.get(), 0, boundingBox);
        this.section = section;
    }

    public RiftfallCitadelPiece(CompoundTag tag) {
        super(ModStructures.RIFTFALL_CITADEL_PIECE.get(), tag);
        this.section = Section.byName(tag.getString("Section"));
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putString("Section", section.serializedName);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager,
                            ChunkGenerator chunkGenerator, RandomSource random,
                            BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        int width = boundingBox.maxX() - boundingBox.minX();
        int height = boundingBox.maxY() - boundingBox.minY();
        int depth = boundingBox.maxZ() - boundingBox.minZ();

        switch (section) {
            case COURTYARD -> buildCourtyard(level, chunkBox, random, width, depth);
            case NORTH_WALL, WEST_WALL, EAST_WALL -> buildWall(level, chunkBox, width, height, depth);
            case SOUTH_GATE -> buildGate(level, chunkBox, random, width, height, depth);
            case NORTHWEST_TOWER, NORTHEAST_TOWER, SOUTHWEST_TOWER, SOUTHEAST_TOWER ->
                    buildTower(level, chunkBox, random, width, height, depth);
            case BARRACKS -> buildBarracks(level, chunkBox, random, width, height, depth);
            case WORKSHOP -> buildWorkshop(level, chunkBox, random, width, height, depth);
            case KEEP -> buildKeep(level, chunkBox, random, width, height, depth);
            case VAULT -> buildVault(level, chunkBox, random, width, height, depth);
        }
    }

    private void buildCourtyard(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                                int width, int depth) {
        foundation(level, chunkBox, width, depth, 18);
        clear(level, chunkBox, 0, 1, 0, width, 9, depth);
        for (int x = 0; x <= width; x++) {
            for (int z = 0; z <= depth; z++) {
                BlockState floor = ((x + z) % 11 == 0 || Math.abs(x - width / 2) <= 3
                        || Math.abs(z - depth / 2) <= 3)
                        ? ModBlocks.BOUNDARY_BRICKS.get().defaultBlockState()
                        : Blocks.POLISHED_DEEPSLATE.defaultBlockState();
                set(level, chunkBox, x, 0, z, floor);
            }
        }
        int cx = width / 2;
        int cz = depth / 2;
        for (int radius : new int[]{6, 10, 14}) {
            ritualRing(level, chunkBox, cx, cz, radius);
        }
        for (int y = 1; y <= 10; y++) {
            set(level, chunkBox, cx, y, cz, y % 4 == 0
                    ? ModBlocks.RIFT_CORE.get().defaultBlockState()
                    : Blocks.CRYING_OBSIDIAN.defaultBlockState());
        }
        spawnMob(level, chunkBox, ModEntities.ABYSS_HOUND.get(), cx - 13, 2, cz + 12, 3);
        spawnMob(level, chunkBox, ModEntities.MANA_LEECH.get(), cx + 13, 2, cz + 12, 3);
    }

    private void buildWall(WorldGenLevel level, BoundingBox chunkBox,
                           int width, int height, int depth) {
        foundation(level, chunkBox, width, depth, 22);
        clear(level, chunkBox, 1, 1, 1, width - 1, height - 1, depth - 1);
        floor(level, chunkBox, width, depth);
        outerWalls(level, chunkBox, width, height, depth);
        box(level, chunkBox, 1, height - 3, 1, width - 1, height - 3, depth - 1,
                Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState());
        battlements(level, chunkBox, width, height, depth);
        carveDoors(level, chunkBox, width, depth, 6);
    }

    private void buildGate(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                           int width, int height, int depth) {
        buildWall(level, chunkBox, width, height, depth);
        int cx = width / 2;
        clear(level, chunkBox, cx - 5, 1, 0, cx + 5, 11, depth);
        for (int x : new int[]{cx - 10, cx + 10}) {
            pillar(level, chunkBox, x, 1, 3, height + 5);
            pillar(level, chunkBox, x, 1, depth - 3, height + 5);
        }
        for (int y = 12; y <= 16; y++) {
            box(level, chunkBox, cx - 7, y, 0, cx + 7, y, depth,
                    y == 14 ? ModBlocks.RIFT_CORE.get().defaultBlockState()
                            : ModBlocks.BOUNDARY_BRICKS.get().defaultBlockState());
        }
        placeChest(level, chunkBox, random, 7, 2, depth / 2, COMMON_LOOT, Direction.EAST);
        placeChest(level, chunkBox, random, width - 7, 2, depth / 2, COMMON_LOOT, Direction.WEST);
        spawnMob(level, chunkBox, ModEntities.FALLEN_RESEARCHER.get(), cx - 13, 2, depth / 2, 2);
        spawnMob(level, chunkBox, ModEntities.SHADOW_WALKER.get(), cx + 13, 2, depth / 2, 1);
    }

    private void buildTower(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                            int width, int height, int depth) {
        foundation(level, chunkBox, width, depth, 24);
        clear(level, chunkBox, 1, 1, 1, width - 1, height - 1, depth - 1);
        floor(level, chunkBox, width, depth);
        outerWalls(level, chunkBox, width, height, depth);
        int cx = width / 2;
        int cz = depth / 2;
        for (int y = 8; y < height; y += 8) {
            box(level, chunkBox, 1, y, 1, width - 1, y, depth - 1,
                    Blocks.POLISHED_DEEPSLATE.defaultBlockState());
            clear(level, chunkBox, cx - 2, y, cz - 2, cx + 2, y, cz + 2);
        }
        battlements(level, chunkBox, width, height, depth);
        for (int y = 2; y < height; y += 4) {
            set(level, chunkBox, cx, y, cz, y % 8 == 2
                    ? ModBlocks.RIFT_CORE.get().defaultBlockState()
                    : Blocks.CRYING_OBSIDIAN.defaultBlockState());
        }
        placeChest(level, chunkBox, random, 4, 2, cz, TOWER_LOOT, Direction.EAST);
        spawnMob(level, chunkBox, ModEntities.SHADOW_WALKER.get(), cx, 2, cz, 1);
        spawnMob(level, chunkBox, ModEntities.CRYSTAL_PARASITE.get(), cx + 4, 10, cz, 1);
    }

    private void buildBarracks(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                               int width, int height, int depth) {
        buildBuilding(level, chunkBox, width, height, depth);
        for (int z = 5; z < depth - 4; z += 7) {
            for (int x : new int[]{4, width - 4}) {
                box(level, chunkBox, x - 1, 1, z - 2, x + 1, 1, z + 2,
                        Blocks.DARK_OAK_SLAB.defaultBlockState());
                set(level, chunkBox, x, 2, z, Blocks.CHAIN.defaultBlockState());
            }
        }
        placeChest(level, chunkBox, random, width / 2, 2, 5, COMMON_LOOT, Direction.SOUTH);
        spawnMob(level, chunkBox, ModEntities.ABYSS_HOUND.get(), width / 2, 2, depth / 2, 4);
    }

    private void buildWorkshop(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                               int width, int height, int depth) {
        buildBuilding(level, chunkBox, width, height, depth);
        for (int z = 5; z < depth - 4; z += 7) {
            box(level, chunkBox, 4, 1, z - 2, width - 4, 1, z + 2,
                    Blocks.POLISHED_BLACKSTONE.defaultBlockState());
            set(level, chunkBox, width / 2, 2, z, z % 2 == 0
                    ? Blocks.SMITHING_TABLE.defaultBlockState()
                    : Blocks.BLAST_FURNACE.defaultBlockState());
        }
        placeChest(level, chunkBox, random, width / 2, 2, depth - 5, COMMON_LOOT, Direction.NORTH);
        spawnMob(level, chunkBox, ModEntities.FALLEN_RESEARCHER.get(), width / 2, 2, depth / 2, 3);
    }

    private void buildKeep(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                           int width, int height, int depth) {
        buildBuilding(level, chunkBox, width, height, depth);
        int cx = width / 2;
        int cz = depth / 2;
        for (int y = 9; y < height - 3; y += 9) {
            box(level, chunkBox, 1, y, 1, width - 1, y, depth - 1,
                    Blocks.POLISHED_DEEPSLATE.defaultBlockState());
            clear(level, chunkBox, cx - 3, y, cz - 3, cx + 3, y, cz + 3);
            for (int step = 0; step < 7; step++) {
                set(level, chunkBox, 5 + step, y - 7 + step, 5,
                        Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS.defaultBlockState());
            }
        }
        for (int radius : new int[]{5, 9, 13}) {
            ritualRing(level, chunkBox, cx, cz, radius);
        }
        set(level, chunkBox, cx, 2, cz, ModBlocks.RIFT_CORE.get().defaultBlockState());
        placeChest(level, chunkBox, random, 5, 2, cz, TOWER_LOOT, Direction.EAST);
        placeChest(level, chunkBox, random, width - 5, 2, cz, TOWER_LOOT, Direction.WEST);
        spawnMob(level, chunkBox, ModEntities.CRYSTAL_PARASITE.get(), cx - 8, 2, cz, 2);
        spawnMob(level, chunkBox, ModEntities.SHADOW_WALKER.get(), cx + 8, 2, cz, 2);
    }

    private void buildVault(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                            int width, int height, int depth) {
        box(level, chunkBox, 0, 0, 0, width, height, depth,
                ModBlocks.BOUNDARY_STONE.get().defaultBlockState());
        clear(level, chunkBox, 2, 2, 2, width - 2, height - 2, depth - 2);
        box(level, chunkBox, 1, 1, 1, width - 1, 1, depth - 1,
                Blocks.REINFORCED_DEEPSLATE.defaultBlockState());
        int cx = width / 2;
        int cz = depth / 2;
        clear(level, chunkBox, cx - 3, height - 4, 0, cx + 3, height, 4);
        for (int step = 0; step <= 10; step++) {
            int y = height - 1 - step;
            int z = 3 + step;
            clear(level, chunkBox, cx - 2, y, z, cx + 2, y + 3, z + 2);
            for (int x = cx - 1; x <= cx + 1; x++) {
                set(level, chunkBox, x, y - 1, z,
                        Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS.defaultBlockState()
                                .setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH));
            }
        }
        for (int radius : new int[]{4, 8, 12}) {
            ritualRing(level, chunkBox, cx, cz, radius);
        }
        box(level, chunkBox, cx - 2, 2, cz - 2, cx + 2, 2, cz + 2,
                Blocks.CRYING_OBSIDIAN.defaultBlockState());
        set(level, chunkBox, cx, 3, cz, ModBlocks.RIFT_CORE.get().defaultBlockState());
        placeChest(level, chunkBox, random, cx - 5, 3, cz, VAULT_LOOT, Direction.EAST);
        placeChest(level, chunkBox, random, cx + 5, 3, cz, VAULT_LOOT, Direction.WEST);
        spawnMob(level, chunkBox, ModEntities.MANA_LEECH.get(), cx - 7, 3, cz, 4);
        spawnMob(level, chunkBox, ModEntities.CRYSTAL_PARASITE.get(), cx + 7, 3, cz, 2);
    }

    private void buildBuilding(WorldGenLevel level, BoundingBox chunkBox,
                               int width, int height, int depth) {
        foundation(level, chunkBox, width, depth, 20);
        clear(level, chunkBox, 1, 1, 1, width - 1, height - 1, depth - 1);
        floor(level, chunkBox, width, depth);
        outerWalls(level, chunkBox, width, height, depth);
        box(level, chunkBox, 0, height, 0, width, height, depth,
                Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState());
        battlements(level, chunkBox, width, height, depth);
        carveDoors(level, chunkBox, width, depth, 7);
    }

    private void foundation(WorldGenLevel level, BoundingBox chunkBox,
                            int width, int depth, int maxDepth) {
        for (int x = 0; x <= width; x++) {
            for (int z = 0; z <= depth; z++) {
                for (int y = 0; y >= -maxDepth; y--) {
                    BlockPos pos = worldPos(x, y, z);
                    if (!chunkBox.isInside(pos) || level.isOutsideBuildHeight(pos)) {
                        continue;
                    }
                    BlockState existing = level.getBlockState(pos);
                    if (y < 0 && !existing.isAir() && !existing.canBeReplaced()) {
                        break;
                    }
                    level.setBlock(pos, palette(pos), 2);
                }
            }
        }
    }

    private void floor(WorldGenLevel level, BoundingBox chunkBox, int width, int depth) {
        for (int x = 0; x <= width; x++) {
            for (int z = 0; z <= depth; z++) {
                set(level, chunkBox, x, 0, z, palette(worldPos(x, 0, z)));
            }
        }
    }

    private void outerWalls(WorldGenLevel level, BoundingBox chunkBox,
                            int width, int height, int depth) {
        for (int y = 1; y <= height; y++) {
            for (int x = 0; x <= width; x++) {
                set(level, chunkBox, x, y, 0, palette(worldPos(x, y, 0)));
                set(level, chunkBox, x, y, depth, palette(worldPos(x, y, depth)));
            }
            for (int z = 1; z < depth; z++) {
                set(level, chunkBox, 0, y, z, palette(worldPos(0, y, z)));
                set(level, chunkBox, width, y, z, palette(worldPos(width, y, z)));
            }
        }
    }

    private void battlements(WorldGenLevel level, BoundingBox chunkBox,
                             int width, int height, int depth) {
        for (int x = 0; x <= width; x += 3) {
            set(level, chunkBox, x, height + 1, 0, Blocks.POLISHED_BLACKSTONE_BRICK_WALL.defaultBlockState());
            set(level, chunkBox, x, height + 1, depth, Blocks.POLISHED_BLACKSTONE_BRICK_WALL.defaultBlockState());
        }
        for (int z = 0; z <= depth; z += 3) {
            set(level, chunkBox, 0, height + 1, z, Blocks.POLISHED_BLACKSTONE_BRICK_WALL.defaultBlockState());
            set(level, chunkBox, width, height + 1, z, Blocks.POLISHED_BLACKSTONE_BRICK_WALL.defaultBlockState());
        }
    }

    private void carveDoors(WorldGenLevel level, BoundingBox chunkBox,
                            int width, int depth, int doorHeight) {
        int cx = width / 2;
        int cz = depth / 2;
        clear(level, chunkBox, cx - 3, 1, 0, cx + 3, doorHeight, 3);
        clear(level, chunkBox, cx - 3, 1, depth - 3, cx + 3, doorHeight, depth);
        clear(level, chunkBox, 0, 1, cz - 3, 3, doorHeight, cz + 3);
        clear(level, chunkBox, width - 3, 1, cz - 3, width, doorHeight, cz + 3);
    }

    private void ritualRing(WorldGenLevel level, BoundingBox chunkBox,
                            int centerX, int centerZ, int radius) {
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                if (Math.abs(x - centerX) + Math.abs(z - centerZ) == radius) {
                    set(level, chunkBox, x, 1, z, (x + z) % 5 == 0
                            ? ModBlocks.RIFT_CORE.get().defaultBlockState()
                            : Blocks.CRYING_OBSIDIAN.defaultBlockState());
                }
            }
        }
    }

    private void pillar(WorldGenLevel level, BoundingBox chunkBox,
                        int x, int minY, int z, int maxY) {
        for (int y = minY; y <= maxY; y++) {
            set(level, chunkBox, x, y, z, y % 5 == 0
                    ? ModBlocks.RIFT_CORE.get().defaultBlockState()
                    : ModBlocks.BOUNDARY_BRICKS.get().defaultBlockState());
        }
    }

    private void placeChest(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                            int x, int y, int z, ResourceLocation loot, Direction facing) {
        BlockPos pos = worldPos(x, y, z);
        if (chunkBox.isInside(pos)) {
            createChest(level, chunkBox, random, pos, loot,
                    Blocks.CHEST.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing));
        }
    }

    private void spawnMob(WorldGenLevel level, BoundingBox chunkBox,
                          EntityType<? extends Mob> type, int x, int y, int z, int count) {
        BlockPos center = worldPos(x, y, z);
        if (!chunkBox.isInside(center)) {
            return;
        }
        AABB search = new AABB(center).inflate(14.0D, 8.0D, 14.0D);
        long existing = level.getLevel().getEntitiesOfClass(Mob.class, search,
                mob -> mob.getType().equals(type)).size();
        for (int i = (int) existing; i < count; i++) {
            Mob mob = type.create(level.getLevel());
            if (mob == null) {
                continue;
            }
            BlockPos pos = center.offset((i % 3) * 3 - 3, 0, (i / 3) * 3 - 1);
            mob.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                    level.getRandom().nextFloat() * 360.0F, 0.0F);
            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.STRUCTURE, null, null);
            mob.setPersistenceRequired();
            level.addFreshEntity(mob);
        }
    }

    private void box(WorldGenLevel level, BoundingBox chunkBox,
                     int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState state) {
        if (minX > maxX || minY > maxY || minZ > maxZ) {
            return;
        }
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

    private static BlockState palette(BlockPos pos) {
        return switch (hash(pos) % 17) {
            case 0 -> Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState();
            case 1 -> Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState();
            case 2 -> Blocks.CRYING_OBSIDIAN.defaultBlockState();
            case 3 -> ModBlocks.BOUNDARY_STONE.get().defaultBlockState();
            default -> ModBlocks.BOUNDARY_BRICKS.get().defaultBlockState();
        };
    }

    private static int hash(BlockPos pos) {
        long value = pos.asLong() * 31L + pos.getX() * 73428767L + pos.getZ() * 912367L;
        return (int) Math.floorMod(value ^ (value >>> 32), 127L);
    }

    public enum Section {
        COURTYARD("courtyard"),
        NORTH_WALL("north_wall"),
        SOUTH_GATE("south_gate"),
        WEST_WALL("west_wall"),
        EAST_WALL("east_wall"),
        NORTHWEST_TOWER("northwest_tower"),
        NORTHEAST_TOWER("northeast_tower"),
        SOUTHWEST_TOWER("southwest_tower"),
        SOUTHEAST_TOWER("southeast_tower"),
        BARRACKS("barracks"),
        WORKSHOP("workshop"),
        KEEP("keep"),
        VAULT("vault");

        private final String serializedName;

        Section(String serializedName) {
            this.serializedName = serializedName;
        }

        private static Section byName(String name) {
            for (Section value : values()) {
                if (value.serializedName.equals(name)) {
                    return value;
                }
            }
            return COURTYARD;
        }
    }
}
