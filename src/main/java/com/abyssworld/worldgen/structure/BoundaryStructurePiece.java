package com.abyssworld.worldgen.structure;

import com.abyssworld.AbyssWorld;
import com.abyssworld.entity.BoundaryWatcherEntity;
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

public final class BoundaryStructurePiece extends StructurePiece {
    private static final ResourceLocation OBSERVATORY_LOOT =
            ResourceLocation.fromNamespaceAndPath(AbyssWorld.MODID, "chests/boundary_observatory");
    private static final ResourceLocation OBSERVATORY_VAULT =
            ResourceLocation.fromNamespaceAndPath(AbyssWorld.MODID, "chests/boundary_observatory_vault");
    private static final ResourceLocation DUNGEON_LOOT =
            ResourceLocation.fromNamespaceAndPath(AbyssWorld.MODID, "chests/infested_vein_dungeon");
    private static final ResourceLocation DUNGEON_VAULT =
            ResourceLocation.fromNamespaceAndPath(AbyssWorld.MODID, "chests/infested_vein_vault");

    private final Kind kind;

    public BoundaryStructurePiece(Kind kind, BoundingBox boundingBox) {
        super(ModStructures.BOUNDARY_STRUCTURE_PIECE.get(), 0, boundingBox);
        this.kind = kind;
    }

    public BoundaryStructurePiece(CompoundTag tag) {
        super(ModStructures.BOUNDARY_STRUCTURE_PIECE.get(), tag);
        this.kind = Kind.byName(tag.getString("Kind"));
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putString("Kind", kind.serializedName);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager,
                            ChunkGenerator chunkGenerator, RandomSource random,
                            BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        int width = boundingBox.maxX() - boundingBox.minX();
        int height = boundingBox.maxY() - boundingBox.minY();
        int depth = boundingBox.maxZ() - boundingBox.minZ();

        if (kind.isObservatory() && kind != Kind.OBSERVATORY_SUBLEVEL) {
            buildObservatoryShell(level, chunkBox, width, height, depth);
        } else {
            buildDungeonShell(level, chunkBox, width, height, depth);
        }
        carveConnections(level, chunkBox, width, height, depth);

        switch (kind) {
            case OBSERVATORY_GATE -> decorateGate(level, chunkBox, width, height, depth);
            case OBSERVATORY_COURT -> decorateCourt(level, chunkBox, width, depth);
            case OBSERVATORY_LAB -> decorateLab(level, chunkBox, random, width, depth);
            case OBSERVATORY_ARCHIVE -> decorateArchive(level, chunkBox, random, width, depth);
            case OBSERVATORY_TOWER -> decorateTower(level, chunkBox, random, width, height, depth);
            case OBSERVATORY_SUBLEVEL -> decorateSublevel(level, chunkBox, random, width, depth);
            case DUNGEON_HUB -> decorateDungeonHub(level, chunkBox, width, depth);
            case DUNGEON_NEST -> decorateNest(level, chunkBox, random, width, depth);
            case DUNGEON_LAB -> decorateDungeonLab(level, chunkBox, random, width, depth);
            case DUNGEON_CRYSTAL -> decorateCrystalRoom(level, chunkBox, random, width, depth);
            case DUNGEON_BONE -> decorateBoneRoom(level, chunkBox, random, width, depth);
            case DUNGEON_VAULT -> decorateDungeonVault(level, chunkBox, random, width, depth);
        }
    }

    private void buildObservatoryShell(WorldGenLevel level, BoundingBox chunkBox,
                                       int width, int height, int depth) {
        for (int x = 0; x <= width; x++) {
            for (int z = 0; z <= depth; z++) {
                for (int y = 0; y >= -10; y--) {
                    BlockPos pos = worldPos(x, y, z);
                    if (!chunkBox.isInside(pos) || level.isOutsideBuildHeight(pos)) {
                        continue;
                    }
                    if (y < 0 && !level.getBlockState(pos).isAir()
                            && !level.getBlockState(pos).canBeReplaced()) {
                        break;
                    }
                    level.setBlock(pos, foundationPalette(pos), 2);
                }
            }
        }
        clear(level, chunkBox, 1, 1, 1, width - 1, height - 1, depth - 1);
        box(level, chunkBox, 0, 0, 0, width, 0, depth, ModBlocks.BOUNDARY_BRICKS.get().defaultBlockState());
        for (int y = 1; y < height; y++) {
            for (int x = 0; x <= width; x++) {
                set(level, chunkBox, x, y, 0, wallPalette(worldPos(x, y, 0)));
                set(level, chunkBox, x, y, depth, wallPalette(worldPos(x, y, depth)));
            }
            for (int z = 1; z < depth; z++) {
                set(level, chunkBox, 0, y, z, wallPalette(worldPos(0, y, z)));
                set(level, chunkBox, width, y, z, wallPalette(worldPos(width, y, z)));
            }
        }
        box(level, chunkBox, 0, height, 0, width, height, depth,
                Blocks.POLISHED_DEEPSLATE.defaultBlockState());
        for (int x = 3; x < width; x += 6) {
            for (int y = 4; y < height - 2; y += 7) {
                set(level, chunkBox, x, y, 0, Blocks.TINTED_GLASS.defaultBlockState());
                set(level, chunkBox, x, y, depth, Blocks.TINTED_GLASS.defaultBlockState());
            }
        }
    }

    private void buildDungeonShell(WorldGenLevel level, BoundingBox chunkBox,
                                   int width, int height, int depth) {
        box(level, chunkBox, 0, 0, 0, width, height, depth,
                ModBlocks.BOUNDARY_STONE.get().defaultBlockState());
        clear(level, chunkBox, 1, 1, 1, width - 1, height - 1, depth - 1);
        box(level, chunkBox, 0, 0, 0, width, 0, depth,
                ModBlocks.BOUNDARY_BRICKS.get().defaultBlockState());
        box(level, chunkBox, 0, height, 0, width, height, depth,
                Blocks.COBBLED_DEEPSLATE.defaultBlockState());
        for (int x = 2; x < width; x += 5) {
            set(level, chunkBox, x, 1, 1, Blocks.SOUL_LANTERN.defaultBlockState());
            set(level, chunkBox, x, 1, depth - 1, Blocks.SOUL_LANTERN.defaultBlockState());
        }
    }

    private void carveConnections(WorldGenLevel level, BoundingBox chunkBox,
                                  int width, int height, int depth) {
        int doorHeight = Math.min(6, height - 1);
        int cx = width / 2;
        int cz = depth / 2;
        clear(level, chunkBox, cx - 3, 1, 0, cx + 3, doorHeight, 3);
        clear(level, chunkBox, cx - 3, 1, depth - 3, cx + 3, doorHeight, depth);
        clear(level, chunkBox, 0, 1, cz - 3, 3, doorHeight, cz + 3);
        clear(level, chunkBox, width - 3, 1, cz - 3, width, doorHeight, cz + 3);
    }

    private void decorateGate(WorldGenLevel level, BoundingBox chunkBox,
                              int width, int height, int depth) {
        for (int x : new int[]{3, width - 3}) {
            for (int z : new int[]{3, depth - 3}) {
                pillar(level, chunkBox, x, 1, z, height + 5);
            }
        }
        int cx = width / 2;
        for (int z = 4; z < depth - 3; z += 4) {
            set(level, chunkBox, cx - 5, 2, z, Blocks.SOUL_LANTERN.defaultBlockState());
            set(level, chunkBox, cx + 5, 2, z, Blocks.SOUL_LANTERN.defaultBlockState());
        }
    }

    private void decorateCourt(WorldGenLevel level, BoundingBox chunkBox, int width, int depth) {
        int cx = width / 2;
        int cz = depth / 2;
        for (int radius : new int[]{4, 8, 12}) {
            ritualRing(level, chunkBox, cx, cz, radius);
        }
        for (int y = 1; y <= 9; y++) {
            set(level, chunkBox, cx, y, cz, y == 4 || y == 8
                    ? ModBlocks.RIFT_CORE.get().defaultBlockState()
                    : Blocks.CRYING_OBSIDIAN.defaultBlockState());
        }
    }

    private void decorateLab(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                             int width, int depth) {
        for (int x = 5; x < width - 4; x += 7) {
            for (int z : new int[]{5, depth - 5}) {
                box(level, chunkBox, x - 2, 1, z - 1, x + 2, 1, z + 1,
                        Blocks.POLISHED_DEEPSLATE.defaultBlockState());
                set(level, chunkBox, x, 2, z, random.nextBoolean()
                        ? Blocks.BREWING_STAND.defaultBlockState()
                        : Blocks.CAULDRON.defaultBlockState());
            }
        }
        placeChest(level, chunkBox, random, 4, 2, depth / 2, OBSERVATORY_LOOT, Direction.EAST);
        spawnMob(level, chunkBox, ModEntities.FALLEN_RESEARCHER.get(), width / 2, 2, depth / 2, 2);
    }

    private void decorateArchive(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                                 int width, int depth) {
        for (int x = 4; x < width - 3; x += 5) {
            for (int y = 1; y <= 5; y++) {
                set(level, chunkBox, x, y, 3, Blocks.BOOKSHELF.defaultBlockState());
                set(level, chunkBox, x, y, depth - 3, y == 3
                        ? Blocks.CHISELED_BOOKSHELF.defaultBlockState()
                        : Blocks.BOOKSHELF.defaultBlockState());
            }
        }
        placeChest(level, chunkBox, random, width / 2, 2, depth / 2,
                OBSERVATORY_LOOT, Direction.SOUTH);
        spawnMob(level, chunkBox, ModEntities.FALLEN_RESEARCHER.get(), width / 2, 2, depth / 2, 2);
    }

    private void decorateTower(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                               int width, int height, int depth) {
        int cx = width / 2;
        int cz = depth / 2;
        for (int floor = 10; floor < height - 4; floor += 10) {
            box(level, chunkBox, 1, floor, 1, width - 1, floor, depth - 1,
                    Blocks.POLISHED_DEEPSLATE.defaultBlockState());
            clear(level, chunkBox, cx - 2, floor, cz - 2, cx + 2, floor, cz + 2);
            for (int y = floor - 7; y < floor; y++) {
                int step = y - (floor - 7);
                set(level, chunkBox, 4 + step, y, 4,
                        Blocks.POLISHED_DEEPSLATE_STAIRS.defaultBlockState());
            }
        }
        for (int radius : new int[]{5, 9, 13}) {
            ritualRing(level, chunkBox, cx, cz, radius);
        }
        box(level, chunkBox, cx - 2, 1, cz - 2, cx + 2, 1, cz + 2,
                Blocks.CRYING_OBSIDIAN.defaultBlockState());
        set(level, chunkBox, cx, 2, cz, ModBlocks.RIFT_CORE.get().defaultBlockState());
        placeChest(level, chunkBox, random, 5, 2, cz, OBSERVATORY_VAULT, Direction.EAST);
        placeChest(level, chunkBox, random, width - 5, 2, cz, OBSERVATORY_VAULT, Direction.WEST);
        spawnWatcher(level, chunkBox, cx, 3, cz);
    }

    private void decorateSublevel(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                                  int width, int depth) {
        int cx = width / 2;
        int cz = depth / 2;
        for (int x = 4; x < width; x += 7) {
            pillar(level, chunkBox, x, 1, 4, 10);
            pillar(level, chunkBox, x, 1, depth - 4, 10);
        }
        placeChest(level, chunkBox, random, cx, 2, cz, OBSERVATORY_LOOT, Direction.SOUTH);
        spawnMob(level, chunkBox, ModEntities.MANA_LEECH.get(), cx - 5, 2, cz, 3);
        spawnMob(level, chunkBox, ModEntities.CRYSTAL_PARASITE.get(), cx + 5, 2, cz, 1);
    }

    private void decorateDungeonHub(WorldGenLevel level, BoundingBox chunkBox, int width, int depth) {
        int cx = width / 2;
        int cz = depth / 2;
        for (int radius : new int[]{3, 6, 9}) {
            ritualRing(level, chunkBox, cx, cz, radius);
        }
        set(level, chunkBox, cx, 1, cz, ModBlocks.RIFT_CORE.get().defaultBlockState());
        spawnMob(level, chunkBox, ModEntities.MANA_LEECH.get(), cx, 2, cz, 2);
    }

    private void decorateNest(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                              int width, int depth) {
        for (int i = 0; i < 28; i++) {
            int x = 2 + random.nextInt(Math.max(1, width - 3));
            int z = 2 + random.nextInt(Math.max(1, depth - 3));
            set(level, chunkBox, x, 1 + random.nextInt(4), z,
                    random.nextInt(4) == 0 ? Blocks.BUDDING_AMETHYST.defaultBlockState()
                            : Blocks.COBWEB.defaultBlockState());
        }
        placeChest(level, chunkBox, random, width / 2, 2, depth / 2, DUNGEON_LOOT, Direction.SOUTH);
        spawnMob(level, chunkBox, ModEntities.MANA_LEECH.get(), width / 2, 2, depth / 2, 4);
    }

    private void decorateDungeonLab(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                                    int width, int depth) {
        for (int z = 4; z < depth - 3; z += 5) {
            box(level, chunkBox, 3, 1, z, width - 3, 1, z,
                    Blocks.DARK_OAK_SLAB.defaultBlockState());
            set(level, chunkBox, width / 2, 2, z, Blocks.BREWING_STAND.defaultBlockState());
        }
        placeChest(level, chunkBox, random, width - 4, 2, depth / 2, DUNGEON_LOOT, Direction.WEST);
        spawnMob(level, chunkBox, ModEntities.FALLEN_RESEARCHER.get(), width / 2, 2, depth / 2, 1);
    }

    private void decorateCrystalRoom(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                                     int width, int depth) {
        for (int i = 0; i < 18; i++) {
            int x = 2 + random.nextInt(Math.max(1, width - 3));
            int z = 2 + random.nextInt(Math.max(1, depth - 3));
            int h = 1 + random.nextInt(5);
            for (int y = 1; y <= h; y++) {
                set(level, chunkBox, x, y, z, y == h && random.nextInt(3) == 0
                        ? ModBlocks.RIFT_CORE.get().defaultBlockState()
                        : Blocks.AMETHYST_BLOCK.defaultBlockState());
            }
        }
        spawnMob(level, chunkBox, ModEntities.CRYSTAL_PARASITE.get(), width / 2, 2, depth / 2, 2);
    }

    private void decorateBoneRoom(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                                  int width, int depth) {
        for (int i = 0; i < 24; i++) {
            int x = 2 + random.nextInt(Math.max(1, width - 3));
            int z = 2 + random.nextInt(Math.max(1, depth - 3));
            set(level, chunkBox, x, 1, z, random.nextBoolean()
                    ? Blocks.BONE_BLOCK.defaultBlockState()
                    : Blocks.SOUL_SAND.defaultBlockState());
        }
        spawnMob(level, chunkBox, ModEntities.ABYSS_HOUND.get(), width / 2, 2, depth / 2, 3);
    }

    private void decorateDungeonVault(WorldGenLevel level, BoundingBox chunkBox, RandomSource random,
                                      int width, int depth) {
        int cx = width / 2;
        int cz = depth / 2;
        box(level, chunkBox, cx - 5, 1, cz - 5, cx + 5, 1, cz + 5,
                Blocks.POLISHED_BLACKSTONE.defaultBlockState());
        for (int y = 2; y <= 7; y++) {
            set(level, chunkBox, cx - 5, y, cz - 5, ModBlocks.BOUNDARY_BRICKS.get().defaultBlockState());
            set(level, chunkBox, cx + 5, y, cz - 5, ModBlocks.BOUNDARY_BRICKS.get().defaultBlockState());
            set(level, chunkBox, cx - 5, y, cz + 5, ModBlocks.BOUNDARY_BRICKS.get().defaultBlockState());
            set(level, chunkBox, cx + 5, y, cz + 5, ModBlocks.BOUNDARY_BRICKS.get().defaultBlockState());
        }
        set(level, chunkBox, cx, 2, cz, ModBlocks.RIFT_CORE.get().defaultBlockState());
        placeChest(level, chunkBox, random, cx - 3, 2, cz, DUNGEON_VAULT, Direction.EAST);
        placeChest(level, chunkBox, random, cx + 3, 2, cz, DUNGEON_VAULT, Direction.WEST);
        spawnMob(level, chunkBox, ModEntities.SHADOW_WALKER.get(), cx, 2, cz, 1);
        spawnMob(level, chunkBox, ModEntities.CRYSTAL_PARASITE.get(), cx, 2, cz, 1);
    }

    private void spawnWatcher(WorldGenLevel level, BoundingBox chunkBox, int x, int y, int z) {
        BlockPos spawnPos = worldPos(x, y, z);
        if (!chunkBox.isInside(spawnPos)) {
            return;
        }
        AABB search = new AABB(spawnPos).inflate(28.0D, 12.0D, 28.0D);
        if (!level.getLevel().getEntitiesOfClass(BoundaryWatcherEntity.class, search).isEmpty()) {
            return;
        }
        spawnMob(level, chunkBox, ModEntities.BOUNDARY_WATCHER.get(), x, y, z, 1);
    }

    private void spawnMob(WorldGenLevel level, BoundingBox chunkBox,
                          EntityType<? extends Mob> type, int x, int y, int z, int count) {
        BlockPos center = worldPos(x, y, z);
        if (!chunkBox.isInside(center)) {
            return;
        }
        AABB search = new AABB(center).inflate(12.0D, 6.0D, 12.0D);
        long existing = level.getLevel().getEntitiesOfClass(Mob.class, search,
                mob -> mob.getType().equals(type)).size();
        for (int i = (int) existing; i < count; i++) {
            Mob mob = type.create(level.getLevel());
            if (mob == null) {
                continue;
            }
            BlockPos pos = center.offset((i % 2) * 3 - 1, 0, (i / 2) * 3 - 1);
            mob.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                    level.getRandom().nextFloat() * 360.0F, 0.0F);
            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos),
                    MobSpawnType.STRUCTURE, null, null);
            mob.setPersistenceRequired();
            level.addFreshEntity(mob);
        }
    }

    private void ritualRing(WorldGenLevel level, BoundingBox chunkBox,
                            int centerX, int centerZ, int radius) {
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                int distance = Math.abs(x - centerX) + Math.abs(z - centerZ);
                if (distance == radius) {
                    set(level, chunkBox, x, 1, z, (x + z) % 4 == 0
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

    private static BlockState foundationPalette(BlockPos pos) {
        return hash(pos) % 5 == 0
                ? Blocks.COBBLED_DEEPSLATE.defaultBlockState()
                : ModBlocks.BOUNDARY_STONE.get().defaultBlockState();
    }

    private static BlockState wallPalette(BlockPos pos) {
        return switch (hash(pos) % 13) {
            case 0 -> Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState();
            case 1 -> Blocks.CRYING_OBSIDIAN.defaultBlockState();
            case 2 -> ModBlocks.BOUNDARY_STONE.get().defaultBlockState();
            default -> ModBlocks.BOUNDARY_BRICKS.get().defaultBlockState();
        };
    }

    private static int hash(BlockPos pos) {
        long value = pos.asLong() * 31L + pos.getX() * 73428767L + pos.getZ() * 912367L;
        return (int) Math.floorMod(value ^ (value >>> 32), 101L);
    }

    public enum Kind {
        OBSERVATORY_GATE("observatory_gate", true),
        OBSERVATORY_COURT("observatory_court", true),
        OBSERVATORY_LAB("observatory_lab", true),
        OBSERVATORY_ARCHIVE("observatory_archive", true),
        OBSERVATORY_TOWER("observatory_tower", true),
        OBSERVATORY_SUBLEVEL("observatory_sublevel", true),
        DUNGEON_HUB("dungeon_hub", false),
        DUNGEON_NEST("dungeon_nest", false),
        DUNGEON_LAB("dungeon_lab", false),
        DUNGEON_CRYSTAL("dungeon_crystal", false),
        DUNGEON_BONE("dungeon_bone", false),
        DUNGEON_VAULT("dungeon_vault", false);

        private final String serializedName;
        private final boolean observatory;

        Kind(String serializedName, boolean observatory) {
            this.serializedName = serializedName;
            this.observatory = observatory;
        }

        private boolean isObservatory() {
            return observatory;
        }

        private static Kind byName(String name) {
            for (Kind value : values()) {
                if (value.serializedName.equals(name)) {
                    return value;
                }
            }
            return DUNGEON_HUB;
        }
    }
}
