package com.abyssworld.magic;

import com.abyssworld.item.AbyssKeyItem;
import com.abyssworld.menu.AbyssResourceHarvesterMenu;
import com.abyssworld.registry.ModBlockEntities;
import com.abyssworld.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.EnumMap;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import com.abyssworld.item.AbyssMachineUpgradeItem;

public class AbyssResourceHarvesterBlockEntity extends BlockEntity implements Container, MenuProvider, SideConfigurable {
    private static final int SLOTS = 9;
    private static final int UPGRADE_SLOTS = 3;
    private static final int MANA_NETWORK_RANGE = 64;
    private static final int MAX_MANA_PER_TICK = 20;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOTS + UPGRADE_SLOTS, ItemStack.EMPTY);
    private final EnumMap<Direction, MachineSideMode> sideModes = new EnumMap<>(Direction.class);
    private final EnumMap<Direction, LazyOptional<IItemHandler>> handlers = new EnumMap<>(Direction.class);
    private ItemStack target = ItemStack.EMPTY;
    private int workMana;
    private int nearbyMana;
    private int currentThroughput;
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> workMana;
                case 1 -> target.isEmpty() ? 0 : effectiveCost(target.getItem());
                case 2 -> nearbyMana & 0xFFFF;
                case 3 -> nearbyMana >>> 16;
                case 4 -> currentThroughput;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> workMana = value;
                case 2 -> nearbyMana = (nearbyMana & 0xFFFF0000) | (value & 0xFFFF);
                case 3 -> nearbyMana = (nearbyMana & 0xFFFF) | ((value & 0xFFFF) << 16);
                case 4 -> currentThroughput = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public AbyssResourceHarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ABYSS_RESOURCE_HARVESTER.get(), pos, state);
        for (Direction direction : Direction.values()) sideModes.put(direction, MachineSideMode.BOTH);
    }

    public ContainerData dataAccess() {
        return dataAccess;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.abyssworld.abyss_resource_harvester");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new AbyssResourceHarvesterMenu(containerId, inventory, this, dataAccess);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                  AbyssResourceHarvesterBlockEntity harvester) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        List<AbyssManaPoolBlockEntity> connectedPools =
                AbyssManaNetwork.pools(level, pos, MANA_NETWORK_RANGE
                        + harvester.upgradeLevel(AbyssMachineUpgradeItem.Type.RANGE) * 16);
        harvester.nearbyMana = AbyssManaNetwork.storedMana(connectedPools);
        harvester.currentThroughput = harvester.throughput(harvester.nearbyMana);
        if (harvester.target.isEmpty() || harvester.isFull()
                || !harvester.isHarvestableHere(harvester.target.getItem())) {
            harvester.autoExport();
            return;
        }
        if (!harvester.canAccept(harvester.target.copyWithCount(1))) {
            return;
        }

        int requested = harvester.currentThroughput;
        if (requested <= 0) {
            return;
        }

        int consumed = AbyssManaNetwork.consumeUpTo(connectedPools, requested);
        if (consumed <= 0) {
            return;
        }
        harvester.workMana += consumed;
        harvester.setChanged();

        int cost = harvester.effectiveCost(harvester.target.getItem());
        if (harvester.workMana < cost) {
            return;
        }

        int operations = Math.min(harvester.factoryLanes(), harvester.workMana / cost);
        operations = Math.min(operations, harvester.availableSpace(harvester.target));
        if (operations <= 0) return;
        ItemStack output = harvester.target.copyWithCount(operations);
        if (!harvester.canAccept(output)) {
            return;
        }

        if (harvester.addOutput(output)) {
            harvester.workMana -= cost * operations;
            harvester.setChanged();
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D,
                    16, 0.5D, 0.4D, 0.5D, 0.02D);
            serverLevel.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME,
                    SoundSource.BLOCKS, 0.8F, 0.7F);
        }
        harvester.autoExport();
    }

    private int throughput(int storedMana) {
        if (storedMana <= 0) {
            return 0;
        }
        int speed = upgradeLevel(AbyssMachineUpgradeItem.Type.SPEED);
        return Math.min(MAX_MANA_PER_TICK * (1 + speed), (1 + storedMana / 500) * (1 + speed));
    }

    private int effectiveCost(Item item) {
        int efficiency = upgradeLevel(AbyssMachineUpgradeItem.Type.EFFICIENCY);
        return Math.max(1, manaCost(item) * Math.max(40, 100 - efficiency * 10) / 100);
    }

    private int factoryLanes() {
        int lanes = upgradeLevel(AbyssMachineUpgradeItem.Type.FACTORY); return lanes <= 0 ? 1 : lanes;
    }

    private int upgradeLevel(AbyssMachineUpgradeItem.Type type) {
        int level = 0;
        for (int i = SLOTS; i < items.size(); i++) if (items.get(i).getItem() instanceof AbyssMachineUpgradeItem upgrade
                && upgrade.type() == type) level += upgrade.level() * items.get(i).getCount();
        return level;
    }

    public static int manaCost(Item item) {
        if (item == ModItems.RAW_ABYSS_IRON.get()) return 180;
        if (item == ModItems.ABYSS_CRYSTAL.get()) return 260;
        if (item == ModItems.PRIMORDIAL_SAP.get()
                || item == ModItems.ETERNAL_FLAME.get()
                || item == ModItems.UNMELTING_ICE_CRYSTAL.get()
                || item == ModItems.PRIMORDIAL_NERVE.get()
                || item == ModItems.SPATIAL_ANCHOR_CRYSTAL.get()) {
            return 300;
        }
        if (item == ModItems.AWAKENED_VINE.get()
                || item == ModItems.VERDANT_FANG.get()
                || item == ModItems.CINDER_HEART.get()
                || item == ModItems.ASH_KING_METAL.get()
                || item == ModItems.GLACIAL_PLATE.get()
                || item == ModItems.LIVING_SINEW.get()
                || item == ModItems.WORLD_PULSE_FLUID.get()
                || item == ModItems.VOID_EYE.get()) {
            return 420;
        }
        return 0;
    }

    public boolean isHarvestableHere(Item item) {
        if (level == null) {
            return false;
        }
        if (item == ModItems.RAW_ABYSS_IRON.get() || item == ModItems.ABYSS_CRYSTAL.get()) {
            return true;
        }
        if (level.dimension().equals(AbyssKeyItem.FORGOTTEN_FOREST)) {
            return item == ModItems.PRIMORDIAL_SAP.get()
                    || item == ModItems.AWAKENED_VINE.get()
                    || item == ModItems.VERDANT_FANG.get();
        }
        if (level.dimension().equals(AbyssKeyItem.ASH_WASTELAND)) {
            return item == ModItems.ETERNAL_FLAME.get()
                    || item == ModItems.CINDER_HEART.get()
                    || item == ModItems.ASH_KING_METAL.get();
        }
        if (level.dimension().equals(AbyssKeyItem.FROZEN_CAVERN)) {
            return item == ModItems.UNMELTING_ICE_CRYSTAL.get()
                    || item == ModItems.GLACIAL_PLATE.get();
        }
        if (level.dimension().equals(AbyssKeyItem.FLESH_MINE)) {
            return item == ModItems.PRIMORDIAL_NERVE.get()
                    || item == ModItems.LIVING_SINEW.get()
                    || item == ModItems.WORLD_PULSE_FLUID.get();
        }
        if (level.dimension().equals(AbyssKeyItem.VOID_CITY)) {
            return item == ModItems.SPATIAL_ANCHOR_CRYSTAL.get()
                    || item == ModItems.VOID_EYE.get();
        }
        return false;
    }

    private boolean canAccept(ItemStack stack) {
        for (int i = 0; i < SLOTS; i++) {
            ItemStack existing = items.get(i);
            if (existing.isEmpty()) {
                return true;
            }
            if (ItemStack.isSameItemSameTags(existing, stack)
                    && existing.getCount() < existing.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    private int availableSpace(ItemStack stack) {
        int room = 0;
        for (int i = 0; i < SLOTS; i++) {
            ItemStack existing = items.get(i);
            if (existing.isEmpty()) room += stack.getMaxStackSize();
            else if (ItemStack.isSameItemSameTags(existing, stack)) room += existing.getMaxStackSize() - existing.getCount();
        }
        return room;
    }

    private boolean addOutput(ItemStack stack) {
        for (int i = 0; i < SLOTS; i++) {
            ItemStack existing = items.get(i);
            if (!existing.isEmpty() && ItemStack.isSameItemSameTags(existing, stack)
                    && existing.getCount() < existing.getMaxStackSize()) {
                int move = Math.min(stack.getCount(), existing.getMaxStackSize() - existing.getCount());
                existing.grow(move);
                stack.shrink(move);
                if (stack.isEmpty()) {
                    return true;
                }
            }
        }
        for (int i = 0; i < SLOTS; i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, stack.copy());
                stack.setCount(0);
                return true;
            }
        }
        return false;
    }

    private boolean isFull() {
        for (int i = 0; i < SLOTS; i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getContainerSize() {
        return SLOTS + UPGRADE_SLOTS + 1;
    }

    @Override
    public boolean isEmpty() {
        if (!target.isEmpty()) {
            return false;
        }
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? target : items.get(slot - 1);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed;
        if (slot == 0) {
            removed = target.split(amount);
            if (target.isEmpty()) {
                workMana = 0;
            }
        } else {
            removed = ContainerHelper.removeItem(items, slot - 1, amount);
        }
        if (!removed.isEmpty()) {
            setChanged();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot == 0) {
            ItemStack removed = target;
            target = ItemStack.EMPTY;
            workMana = 0;
            return removed;
        }
        return ContainerHelper.takeItem(items, slot - 1);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            ItemStack next = stack.copy();
            next.setCount(Math.min(1, next.getCount()));
            if (!ItemStack.isSameItemSameTags(target, next)) {
                workMana = 0;
            }
            target = next;
        } else {
            items.set(slot - 1, stack);
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == 0) return manaCost(stack.getItem()) > 0 && isHarvestableHere(stack.getItem());
        return slot > SLOTS && slot <= SLOTS + UPGRADE_SLOTS
                && stack.getItem() instanceof AbyssMachineUpgradeItem;
    }

    @Override
    public void clearContent() {
        target = ItemStack.EMPTY;
        workMana = 0;
        items.clear();
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("WorkMana", workMana);
        if (!target.isEmpty()) {
            tag.put("Target", target.save(new CompoundTag()));
        }
        ContainerHelper.saveAllItems(tag, items);
        CompoundTag sides = new CompoundTag();
        sideModes.forEach((direction, mode) -> sides.putString(direction.getName(), mode.getSerializedName()));
        tag.put("SideModes", sides);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        workMana = Math.max(0, tag.getInt("WorkMana"));
        target = tag.contains("Target") ? ItemStack.of(tag.getCompound("Target")) : ItemStack.EMPTY;
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
        ContainerHelper.loadAllItems(tag, items);
        CompoundTag sides = tag.getCompound("SideModes");
        for (Direction direction : Direction.values()) for (MachineSideMode mode : MachineSideMode.values())
            if (mode.getSerializedName().equals(sides.getString(direction.getName()))) sideModes.put(direction, mode);
    }

    @Override public MachineSideMode cycleSideMode(Direction direction) {
        MachineSideMode next = sideMode(direction).next(); sideModes.put(direction, next);
        LazyOptional<IItemHandler> removed = handlers.remove(direction);
        if (removed != null) removed.invalidate();
        setChanged(); return next;
    }
    @Override public MachineSideMode sideMode(Direction direction) {
        return sideModes.getOrDefault(direction, MachineSideMode.BOTH);
    }
    @Override public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER && side != null && sideMode(side) != MachineSideMode.DISABLED)
            return handlers.computeIfAbsent(side, direction -> LazyOptional.of(() -> new HarvesterHandler(direction))).cast();
        return super.getCapability(capability, side);
    }
    @Override public void invalidateCaps() { super.invalidateCaps(); handlers.values().forEach(LazyOptional::invalidate); handlers.clear(); }

    private void autoExport() {
        if (level == null || upgradeLevel(AbyssMachineUpgradeItem.Type.AUTO_EXPORT) <= 0) return;
        for (Direction direction : Direction.values()) {
            if (!sideMode(direction).allowsOutput()) continue;
            BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(direction));
            if (neighbor == null) continue;
            neighbor.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite()).ifPresent(targetHandler -> {
                for (int i = 0; i < SLOTS; i++) {
                    ItemStack current = items.get(i); if (current.isEmpty()) continue;
                    ItemStack remainder = ItemHandlerHelper.insertItemStacked(targetHandler, current.copy(), false);
                    if (remainder.getCount() != current.getCount()) { items.set(i, remainder); setChanged(); }
                }
            });
        }
    }

    private final class HarvesterHandler implements IItemHandler {
        private final Direction side; private HarvesterHandler(Direction side) { this.side = side; }
        @Override public int getSlots() { return getContainerSize(); }
        @Override public ItemStack getStackInSlot(int slot) { return getItem(slot); }
        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!sideMode(side).allowsInput() || !canPlaceItem(slot, stack)) return stack;
            ItemStack current = getItem(slot);
            if (!current.isEmpty() && !ItemStack.isSameItemSameTags(current, stack)) return stack;
            int limit = getSlotLimit(slot); int accepted = Math.min(stack.getCount(), limit - current.getCount());
            if (accepted <= 0) return stack;
            if (!simulate) setItem(slot, current.isEmpty() ? stack.copyWithCount(accepted)
                    : current.copyWithCount(current.getCount() + accepted));
            ItemStack remainder = stack.copy(); remainder.shrink(accepted); return remainder;
        }
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!sideMode(side).allowsOutput() || slot < 1 || slot > SLOTS) return ItemStack.EMPTY;
            ItemStack current = getItem(slot); int count = Math.min(amount, current.getCount());
            if (count <= 0) return ItemStack.EMPTY; ItemStack result = current.copyWithCount(count);
            if (!simulate) removeItem(slot, count); return result;
        }
        @Override public int getSlotLimit(int slot) { return slot == 0 ? 1 : slot > SLOTS ? 8 : 64; }
        @Override public boolean isItemValid(int slot, ItemStack stack) { return canPlaceItem(slot, stack); }
    }
}
