package com.abyssworld.magic;

import com.abyssworld.AbyssWorld;
import com.abyssworld.item.AbyssMachineUpgradeItem;
import com.abyssworld.menu.LeylineMinerMenu;
import com.abyssworld.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

public class LeylineMinerBlockEntity extends BlockEntity implements Container, MenuProvider, SideConfigurable {
    public static final int FILTER_START = 0;
    public static final int FILTER_COUNT = 4;
    public static final int REPLACEMENT_SLOT = 4;
    public static final int OUTPUT_START = 5;
    public static final int OUTPUT_COUNT = 9;
    public static final int UPGRADE_START = 14;
    public static final int UPGRADE_COUNT = 3;
    public static final int TOTAL_SLOTS = 17;

    private static final int BASE_NETWORK_RANGE = 64;
    private static final int BASE_MAX_RADIUS = 16;
    private static final int MAX_DEPTH = 96;
    private static final TagKey<Block> FORGE_ORES = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("forge", "ores"));
    private static final UUID MINER_UUID = UUID.nameUUIDFromBytes(
            "abyssworld:leyline_miner".getBytes(StandardCharsets.UTF_8));

    private final NonNullList<ItemStack> items = NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY);
    private final EnumMap<Direction, MachineSideMode> sideModes = new EnumMap<>(Direction.class);
    private final EnumMap<Direction, LazyOptional<IItemHandler>> handlers = new EnumMap<>(Direction.class);
    private int workMana;
    private int nearbyMana;
    private int throughput;
    private int currentCost;
    private int radius = 8;
    private int depth = 32;
    private FilterMode filterMode = FilterMode.ORES;
    private boolean silkTouch;
    private boolean replaceMined;
    private boolean running;
    private long scanIndex;
    private long targetPosition = Long.MIN_VALUE;
    private int minedBlocks;
    private Status status = Status.IDLE;

    private final ContainerData data = new ContainerData() {
        @Override public int get(int index) {
            return switch (index) {
                case 0 -> workMana;
                case 1 -> currentCost;
                case 2 -> nearbyMana & 0xFFFF;
                case 3 -> nearbyMana >>> 16;
                case 4 -> throughput;
                case 5 -> running ? 1 : 0;
                case 6 -> radius;
                case 7 -> depth;
                case 8 -> filterMode.ordinal();
                case 9 -> silkTouch ? 1 : 0;
                case 10 -> replaceMined ? 1 : 0;
                case 11 -> minedBlocks;
                case 12 -> scanProgress();
                case 13 -> status.ordinal();
                default -> 0;
            };
        }
        @Override public void set(int index, int value) {
            switch (index) {
                case 0 -> workMana = value;
                case 1 -> currentCost = value;
                case 2 -> nearbyMana = (nearbyMana & 0xFFFF0000) | (value & 0xFFFF);
                case 3 -> nearbyMana = (nearbyMana & 0xFFFF) | ((value & 0xFFFF) << 16);
                case 4 -> throughput = value;
                case 5 -> running = value != 0;
                case 6 -> radius = value;
                case 7 -> depth = value;
                case 8 -> filterMode = FilterMode.byOrdinal(value);
                case 9 -> silkTouch = value != 0;
                case 10 -> replaceMined = value != 0;
                case 11 -> minedBlocks = value;
                case 13 -> status = Status.byOrdinal(value);
                default -> { }
            }
        }
        @Override public int getCount() { return 14; }
    };

    public LeylineMinerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LEYLINE_MINER.get(), pos, state);
        for (Direction direction : Direction.values()) sideModes.put(direction, MachineSideMode.BOTH);
    }

    public ContainerData dataAccess() { return data; }

    @Override public Component getDisplayName() {
        return Component.translatable("block.abyssworld.leyline_miner");
    }

    @Nullable @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new LeylineMinerMenu(id, inventory, this, data);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, LeylineMinerBlockEntity miner) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        List<AbyssManaPoolBlockEntity> pools = AbyssManaNetwork.pools(level, pos,
                BASE_NETWORK_RANGE + miner.upgradeLevel(AbyssMachineUpgradeItem.Type.RANGE) * 16);
        miner.nearbyMana = AbyssManaNetwork.storedMana(pools);
        miner.throughput = miner.calculateThroughput();
        miner.autoExport();
        if (!miner.running) { miner.status = Status.IDLE; return; }

        if (miner.targetPosition == Long.MIN_VALUE) {
            if (!miner.findTarget(serverLevel)) return;
        }

        BlockPos target = BlockPos.of(miner.targetPosition);
        if (!level.hasChunkAt(target)) { miner.clearTarget(); return; }
        BlockState targetState = level.getBlockState(target);
        if (!miner.isMineableTarget(targetState, target)) { miner.clearTarget(); return; }

        List<ItemStack> drops = miner.dropsFor(serverLevel, target, targetState);
        if (!miner.canFit(drops)) { miner.status = Status.OUTPUT_FULL; return; }
        if (miner.replaceMined && !(miner.items.get(REPLACEMENT_SLOT).getItem() instanceof BlockItem)) {
            miner.status = Status.NEEDS_REPLACEMENT; return;
        }
        miner.currentCost = miner.manaCost(targetState, target);
        int consumed = AbyssManaNetwork.consumeUpTo(pools, Math.min(miner.throughput,
                Math.max(0, miner.currentCost - miner.workMana)));
        if (consumed <= 0) { miner.status = Status.NO_MANA; return; }
        miner.workMana += consumed;
        miner.status = Status.MINING;
        if (miner.workMana < miner.currentCost) { miner.setChanged(); return; }

        FakePlayer fakePlayer = FakePlayerFactory.get(serverLevel,
                new com.mojang.authlib.GameProfile(MINER_UUID, "[AbyssMiner]"));
        if (ForgeHooks.onBlockBreakEvent(level, GameType.SURVIVAL, fakePlayer, target) == -1) {
            miner.status = Status.PROTECTED; miner.clearTarget(); return;
        }

        BlockState replacement = net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        if (miner.replaceMined) {
            BlockItem blockItem = (BlockItem) miner.items.get(REPLACEMENT_SLOT).getItem();
            replacement = blockItem.getBlock().defaultBlockState();
        }
        level.destroyBlock(target, false, fakePlayer);
        if (!replacement.isAir()) {
            level.setBlock(target, replacement, Block.UPDATE_ALL);
            miner.items.get(REPLACEMENT_SLOT).shrink(1);
        }
        miner.insertDrops(drops);
        miner.workMana -= miner.currentCost;
        miner.minedBlocks++;
        miner.clearTarget();
        miner.status = Status.SCANNING;
        miner.setChanged();
    }

    private boolean findTarget(ServerLevel level) {
        int scans = Math.min(768, 96 * (1 + upgradeLevel(AbyssMachineUpgradeItem.Type.SPEED)));
        long total = totalScanBlocks();
        for (int attempt = 0; attempt < scans; attempt++) {
            if (scanIndex >= total) {
                running = false; status = Status.COMPLETE; currentCost = 0; setChanged(); return false;
            }
            BlockPos check = positionFor(scanIndex++);
            if (!level.hasChunkAt(check)) continue;
            BlockState state = level.getBlockState(check);
            if (isMineableTarget(state, check)) {
                targetPosition = check.asLong(); currentCost = manaCost(state, check); status = Status.MINING;
                setChanged(); return true;
            }
        }
        status = Status.SCANNING; setChanged(); return false;
    }

    private BlockPos positionFor(long index) {
        int width = radius * 2 + 1;
        int layerSize = width * width;
        int layer = (int) (index / layerSize);
        int horizontal = (int) (index % layerSize);
        int x = horizontal % width - radius;
        int z = horizontal / width - radius;
        return worldPosition.offset(x, -1 - layer, z);
    }

    private long totalScanBlocks() {
        long width = radius * 2L + 1L;
        return width * width * depth;
    }

    private int scanProgress() {
        long total = totalScanBlocks();
        return total <= 0 ? 0 : (int) Math.min(1000L, scanIndex * 1000L / total);
    }

    private boolean isMineableTarget(BlockState state, BlockPos pos) {
        if (state.isAir() || state.hasBlockEntity() || state.is(BlockTags.WITHER_IMMUNE)
                || state.getDestroySpeed(level, pos) < 0.0F) return false;
        boolean listed = false;
        for (int slot = FILTER_START; slot < FILTER_START + FILTER_COUNT; slot++) {
            ItemStack filter = items.get(slot);
            if (filter.getItem() instanceof BlockItem blockItem && state.is(blockItem.getBlock())) {
                listed = true; break;
            }
        }
        return switch (filterMode) {
            case ORES -> state.is(FORGE_ORES);
            case WHITELIST -> listed;
            case BLACKLIST -> !listed;
        };
    }

    private List<ItemStack> dropsFor(ServerLevel level, BlockPos pos, BlockState state) {
        ItemStack tool = new ItemStack(Items.DIAMOND_PICKAXE);
        if (silkTouch) tool.enchant(Enchantments.SILK_TOUCH, 1);
        return Block.getDrops(state, level, pos, null, null, tool);
    }

    private int manaCost(BlockState state, BlockPos pos) {
        float hardness = Math.max(0.0F, state.getDestroySpeed(level, pos));
        int base = 160 + Math.round(hardness * 80.0F);
        if (silkTouch) base *= 6;
        int efficiency = upgradeLevel(AbyssMachineUpgradeItem.Type.EFFICIENCY);
        return Math.max(40, base * Math.max(40, 100 - efficiency * 10) / 100);
    }

    private int calculateThroughput() {
        if (nearbyMana <= 0) return 0;
        int speed = upgradeLevel(AbyssMachineUpgradeItem.Type.SPEED);
        return Math.min(128 * (1 + speed), (1 + nearbyMana / 500) * (1 + speed));
    }

    private int upgradeLevel(AbyssMachineUpgradeItem.Type type) {
        int value = 0;
        for (int slot = UPGRADE_START; slot < UPGRADE_START + UPGRADE_COUNT; slot++) {
            ItemStack stack = items.get(slot);
            if (stack.getItem() instanceof AbyssMachineUpgradeItem upgrade && upgrade.type() == type)
                value += upgrade.level() * stack.getCount();
        }
        return value;
    }

    private boolean canFit(List<ItemStack> drops) {
        List<ItemStack> simulated = new ArrayList<>();
        for (int slot = OUTPUT_START; slot < OUTPUT_START + OUTPUT_COUNT; slot++) simulated.add(items.get(slot).copy());
        for (ItemStack drop : drops) {
            ItemStack remaining = drop.copy();
            for (ItemStack existing : simulated) {
                if (!existing.isEmpty() && ItemStack.isSameItemSameTags(existing, remaining)) {
                    int moved = Math.min(remaining.getCount(), existing.getMaxStackSize() - existing.getCount());
                    existing.grow(moved); remaining.shrink(moved);
                }
            }
            for (int i = 0; i < simulated.size() && !remaining.isEmpty(); i++) {
                if (simulated.get(i).isEmpty()) {
                    int moved = Math.min(remaining.getCount(), remaining.getMaxStackSize());
                    simulated.set(i, remaining.copyWithCount(moved)); remaining.shrink(moved);
                }
            }
            if (!remaining.isEmpty()) return false;
        }
        return true;
    }

    private void insertDrops(List<ItemStack> drops) {
        for (ItemStack drop : drops) {
            ItemStack remaining = drop.copy();
            for (int slot = OUTPUT_START; slot < OUTPUT_START + OUTPUT_COUNT && !remaining.isEmpty(); slot++) {
                ItemStack existing = items.get(slot);
                if (!existing.isEmpty() && ItemStack.isSameItemSameTags(existing, remaining)) {
                    int moved = Math.min(remaining.getCount(), existing.getMaxStackSize() - existing.getCount());
                    existing.grow(moved); remaining.shrink(moved);
                }
            }
            for (int slot = OUTPUT_START; slot < OUTPUT_START + OUTPUT_COUNT && !remaining.isEmpty(); slot++) {
                if (items.get(slot).isEmpty()) {
                    int moved = Math.min(remaining.getCount(), remaining.getMaxStackSize());
                    items.set(slot, remaining.copyWithCount(moved)); remaining.shrink(moved);
                }
            }
        }
    }

    public boolean handleButton(int id) {
        switch (id) {
            case 0 -> running = !running;
            case 1 -> resetScan();
            case 2 -> { radius = Math.max(4, radius - 4); resetScan(); }
            case 3 -> { radius = Math.min(maxRadius(), radius + 4); resetScan(); }
            case 4 -> { depth = Math.max(8, depth - 8); resetScan(); }
            case 5 -> { depth = Math.min(MAX_DEPTH, depth + 8); resetScan(); }
            case 6 -> { filterMode = filterMode.next(); resetScan(); }
            case 7 -> { silkTouch = !silkTouch; clearTarget(); }
            case 8 -> replaceMined = !replaceMined;
            default -> { return false; }
        }
        setChanged(); return true;
    }

    private int maxRadius() {
        return Math.min(32, BASE_MAX_RADIUS + upgradeLevel(AbyssMachineUpgradeItem.Type.RANGE) * 4);
    }

    private void resetScan() {
        scanIndex = 0; minedBlocks = 0; clearTarget(); status = running ? Status.SCANNING : Status.IDLE;
    }

    private void clearTarget() {
        targetPosition = Long.MIN_VALUE; currentCost = 0; workMana = 0;
    }

    private void autoExport() {
        if (level == null || upgradeLevel(AbyssMachineUpgradeItem.Type.AUTO_EXPORT) <= 0) return;
        for (Direction direction : Direction.values()) {
            if (!sideMode(direction).allowsOutput()) continue;
            BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(direction));
            if (neighbor == null) continue;
            neighbor.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite()).ifPresent(handler -> {
                for (int slot = OUTPUT_START; slot < OUTPUT_START + OUTPUT_COUNT; slot++) {
                    ItemStack stack = items.get(slot); if (stack.isEmpty()) continue;
                    ItemStack remainder = ItemHandlerHelper.insertItemStacked(handler, stack.copy(), false);
                    if (remainder.getCount() != stack.getCount()) { items.set(slot, remainder); setChanged(); }
                }
            });
        }
    }

    @Override public int getContainerSize() { return TOTAL_SLOTS; }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(items, slot, amount);
        if (!removed.isEmpty()) { if (slot < FILTER_COUNT) resetScan(); setChanged(); }
        return removed;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack.copy());
        if (items.get(slot).getCount() > getMaxStackSize()) items.get(slot).setCount(getMaxStackSize());
        if (slot < FILTER_COUNT) { items.get(slot).setCount(Math.min(1, items.get(slot).getCount())); resetScan(); }
        setChanged();
    }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot >= FILTER_START && slot < FILTER_START + FILTER_COUNT) return stack.getItem() instanceof BlockItem;
        if (slot == REPLACEMENT_SLOT) return stack.getItem() instanceof BlockItem;
        return slot >= UPGRADE_START && slot < UPGRADE_START + UPGRADE_COUNT
                && stack.getItem() instanceof AbyssMachineUpgradeItem;
    }
    @Override public void clearContent() { items.clear(); resetScan(); setChanged(); }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag); ContainerHelper.saveAllItems(tag, items);
        tag.putInt("WorkMana", workMana); tag.putInt("Radius", radius); tag.putInt("Depth", depth);
        tag.putInt("FilterMode", filterMode.ordinal()); tag.putBoolean("SilkTouch", silkTouch);
        tag.putBoolean("ReplaceMined", replaceMined); tag.putBoolean("Running", running);
        tag.putLong("ScanIndex", scanIndex); tag.putLong("TargetPosition", targetPosition);
        tag.putInt("MinedBlocks", minedBlocks); tag.putInt("Status", status.ordinal());
        CompoundTag sides = new CompoundTag();
        sideModes.forEach((direction, mode) -> sides.putString(direction.getName(), mode.getSerializedName()));
        tag.put("SideModes", sides);
    }

    @Override public void load(CompoundTag tag) {
        super.load(tag); items.clear(); ContainerHelper.loadAllItems(tag, items);
        workMana = Math.max(0, tag.getInt("WorkMana")); radius = Math.max(4, tag.getInt("Radius"));
        depth = Math.max(8, tag.getInt("Depth")); filterMode = FilterMode.byOrdinal(tag.getInt("FilterMode"));
        silkTouch = tag.getBoolean("SilkTouch"); replaceMined = tag.getBoolean("ReplaceMined");
        running = tag.getBoolean("Running"); scanIndex = Math.max(0L, tag.getLong("ScanIndex"));
        targetPosition = tag.contains("TargetPosition") ? tag.getLong("TargetPosition") : Long.MIN_VALUE;
        minedBlocks = Math.max(0, tag.getInt("MinedBlocks")); status = Status.byOrdinal(tag.getInt("Status"));
        CompoundTag sides = tag.getCompound("SideModes");
        for (Direction direction : Direction.values()) for (MachineSideMode mode : MachineSideMode.values())
            if (mode.getSerializedName().equals(sides.getString(direction.getName()))) sideModes.put(direction, mode);
    }

    @Override public MachineSideMode cycleSideMode(Direction direction) {
        MachineSideMode next = sideMode(direction).next(); sideModes.put(direction, next);
        LazyOptional<IItemHandler> removed = handlers.remove(direction); if (removed != null) removed.invalidate();
        setChanged(); return next;
    }
    @Override public MachineSideMode sideMode(Direction direction) {
        return sideModes.getOrDefault(direction, MachineSideMode.BOTH);
    }
    @Override public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER && side != null && sideMode(side) != MachineSideMode.DISABLED)
            return handlers.computeIfAbsent(side, direction -> LazyOptional.of(() -> new MinerHandler(direction))).cast();
        return super.getCapability(capability, side);
    }
    @Override public void invalidateCaps() { super.invalidateCaps(); handlers.values().forEach(LazyOptional::invalidate); handlers.clear(); }

    private final class MinerHandler implements IItemHandler {
        private final Direction side; private MinerHandler(Direction side) { this.side = side; }
        @Override public int getSlots() { return TOTAL_SLOTS; }
        @Override public ItemStack getStackInSlot(int slot) { return items.get(slot); }
        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!sideMode(side).allowsInput() || !canPlaceItem(slot, stack)) return stack;
            ItemStack existing = items.get(slot);
            if (!existing.isEmpty() && !ItemStack.isSameItemSameTags(existing, stack)) return stack;
            int limit = getSlotLimit(slot); int accepted = Math.min(stack.getCount(), limit - existing.getCount());
            if (accepted <= 0) return stack;
            if (!simulate) setItem(slot, existing.isEmpty() ? stack.copyWithCount(accepted)
                    : existing.copyWithCount(existing.getCount() + accepted));
            ItemStack remainder = stack.copy(); remainder.shrink(accepted); return remainder;
        }
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!sideMode(side).allowsOutput() || slot < OUTPUT_START || slot >= OUTPUT_START + OUTPUT_COUNT)
                return ItemStack.EMPTY;
            ItemStack current = items.get(slot); int count = Math.min(amount, current.getCount());
            if (count <= 0) return ItemStack.EMPTY;
            ItemStack result = current.copyWithCount(count); if (!simulate) removeItem(slot, count); return result;
        }
        @Override public int getSlotLimit(int slot) { return slot < FILTER_COUNT ? 1 : slot >= UPGRADE_START ? 8 : 64; }
        @Override public boolean isItemValid(int slot, ItemStack stack) { return canPlaceItem(slot, stack); }
    }

    public enum FilterMode {
        ORES, WHITELIST, BLACKLIST;
        public FilterMode next() { return values()[(ordinal() + 1) % values().length]; }
        public static FilterMode byOrdinal(int value) { return values()[Math.floorMod(value, values().length)]; }
    }

    public enum Status {
        IDLE, SCANNING, MINING, NO_MANA, OUTPUT_FULL, NEEDS_REPLACEMENT, PROTECTED, COMPLETE;
        public static Status byOrdinal(int value) { return values()[Math.floorMod(value, values().length)]; }
    }
}
