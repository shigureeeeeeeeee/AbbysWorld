package com.abyssworld.magic;

import com.abyssworld.menu.AbyssManaMachineMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.EnumMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import com.abyssworld.item.AbyssMachineUpgradeItem;
import com.abyssworld.registry.ModFluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public abstract class AbstractAbyssManaMachineBlockEntity extends BlockEntity implements Container, MenuProvider, SideConfigurable {
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    public static final int BYPRODUCT_SLOT = 2;
    public static final int CATALYST_SLOT = 3;
    public static final int UPGRADE_SLOT_START = 4;
    public static final int UPGRADE_SLOT_END = 7;
    private static final int BASE_NETWORK_RANGE = 64;

    private final NonNullList<ItemStack> items = NonNullList.withSize(7, ItemStack.EMPTY);
    private final EnumMap<Direction, MachineSideMode> sideModes = new EnumMap<>(Direction.class);
    private final EnumMap<Direction, LazyOptional<IItemHandler>> sidedHandlers = new EnumMap<>(Direction.class);
    private final FluidTank essenceTank = new FluidTank(64_000,
            stack -> stack.getFluid().isSame(ModFluids.ABYSSAL_ESSENCE.get())) {
        @Override protected void onContentsChanged() { setChanged(); }
        @Override public int getCapacity() {
            return 8_000 + upgradeLevel(AbyssMachineUpgradeItem.Type.CAPACITY) * 8_000;
        }
    };
    private final EnumMap<Direction, LazyOptional<IFluidHandler>> fluidHandlers = new EnumMap<>(Direction.class);
    private int workMana;
    private int connectedMana;
    private int currentThroughput;
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> workMana;
                case 1 -> currentCost();
                case 2 -> connectedMana & 0xFFFF;
                case 3 -> connectedMana >>> 16;
                case 4 -> currentThroughput;
                case 5 -> essenceTank.getFluidAmount();
                case 6 -> essenceTank.getCapacity();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> workMana = value;
                case 2 -> connectedMana = (connectedMana & 0xFFFF0000) | (value & 0xFFFF);
                case 3 -> connectedMana = (connectedMana & 0xFFFF) | ((value & 0xFFFF) << 16);
                case 4 -> currentThroughput = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 7;
        }
    };

    protected AbstractAbyssManaMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        for (Direction direction : Direction.values()) {
            sideModes.put(direction, MachineSideMode.BOTH);
        }
    }

    protected abstract Component machineName();

    protected abstract ItemStack resultFor(Level level, ItemStack input);

    protected ItemStack byproductFor(Level level, ItemStack input) {
        return ItemStack.EMPTY;
    }

    protected abstract int manaCost(ItemStack input);

    protected int requiredInputCount(ItemStack input) {
        return 1;
    }

    protected int maxManaPerTick() {
        return 12;
    }

    protected boolean canRunMachine(Level level, BlockPos pos, BlockState state) {
        return true;
    }

    protected ItemStack requiredCatalyst(ItemStack input) {
        return ItemStack.EMPTY;
    }

    protected boolean consumesCatalyst(ItemStack input) {
        return false;
    }

    protected int essenceCost(ItemStack input) { return 0; }

    protected int essenceProduced(ItemStack input) { return 0; }

    public int storedEssence() { return essenceTank.getFluidAmount(); }

    public int essenceCapacity() { return essenceTank.getCapacity(); }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel)) {
            return;
        }

        List<AbyssManaPoolBlockEntity> pools = AbyssManaNetwork.pools(level, pos,
                BASE_NETWORK_RANGE + upgradeLevel(AbyssMachineUpgradeItem.Type.RANGE) * 16);
        connectedMana = AbyssManaNetwork.storedMana(pools);
        currentThroughput = throughput(connectedMana);
        if (!canRunMachine(level, pos, state)) {
            return;
        }

        ItemStack input = items.get(INPUT_SLOT);
        ItemStack result = resultFor(level, input);
        ItemStack byproduct = byproductFor(level, input);
        ItemStack catalyst = requiredCatalyst(input);
        int essenceCost = essenceCost(input);
        int essenceOutput = essenceProduced(input);
        int cost = effectiveManaCost(input);
        int requiredInput = requiredInputCount(input);
        if (input.isEmpty() || input.getCount() < requiredInput || result.isEmpty()
                || cost <= 0 || !canAccept(OUTPUT_SLOT, result)
                || (!byproduct.isEmpty() && !canAccept(BYPRODUCT_SLOT, byproduct))
                || (!catalyst.isEmpty() && !matchesCatalyst(catalyst))
                || essenceTank.getFluidAmount() < essenceCost
                || (essenceOutput > 0 && essenceTank.getFluidAmount() + essenceOutput > essenceTank.getCapacity())
                || currentThroughput <= 0) {
            autoExport();
            return;
        }

        int consumed = AbyssManaNetwork.consumeUpTo(pools, currentThroughput);
        if (consumed <= 0) {
            return;
        }
        workMana += consumed;
        if (workMana >= cost) {
            int operations = Math.min(factoryLanes(), workMana / cost);
            operations = Math.min(operations, input.getCount() / requiredInput);
            operations = Math.min(operations, acceptedOperations(OUTPUT_SLOT, result));
            if (!byproduct.isEmpty()) {
                operations = Math.min(operations, acceptedOperations(BYPRODUCT_SLOT, byproduct));
            }
            if (consumesCatalyst(input) && !catalyst.isEmpty()) {
                operations = Math.min(operations, items.get(CATALYST_SLOT).getCount() / catalyst.getCount());
            }
            if (essenceCost > 0) {
                operations = Math.min(operations, essenceTank.getFluidAmount() / essenceCost);
            }
            if (essenceOutput > 0) {
                operations = Math.min(operations,
                        (essenceTank.getCapacity() - essenceTank.getFluidAmount()) / essenceOutput);
            }
            if (operations > 0) {
                input.shrink(requiredInput * operations);
                addOutput(OUTPUT_SLOT, multiplied(result, operations));
                if (!byproduct.isEmpty()) {
                    addOutput(BYPRODUCT_SLOT, multiplied(byproduct, operations));
                }
                if (consumesCatalyst(input) && !catalyst.isEmpty()) {
                    items.get(CATALYST_SLOT).shrink(catalyst.getCount() * operations);
                }
                if (essenceCost > 0) {
                    essenceTank.drain(essenceCost * operations, IFluidHandler.FluidAction.EXECUTE);
                }
                if (essenceOutput > 0) {
                    essenceTank.fill(new FluidStack(ModFluids.ABYSSAL_ESSENCE.get(), essenceOutput * operations),
                            IFluidHandler.FluidAction.EXECUTE);
                }
                workMana -= cost * operations;
            }
        }
        autoExport();
        setChanged();
    }

    private int throughput(int mana) {
        int speed = upgradeLevel(AbyssMachineUpgradeItem.Type.SPEED);
        return mana <= 0 ? 0 : Math.min(maxManaPerTick() * (1 + speed),
                (1 + mana / 500) * (1 + speed));
    }

    private int currentCost() {
        return level == null ? 0 : effectiveManaCost(items.get(INPUT_SLOT));
    }

    private int effectiveManaCost(ItemStack input) {
        int base = manaCost(input);
        int efficiency = upgradeLevel(AbyssMachineUpgradeItem.Type.EFFICIENCY);
        return base <= 0 ? 0 : Math.max(1, base * Math.max(40, 100 - efficiency * 10) / 100);
    }

    public int upgradeLevel(AbyssMachineUpgradeItem.Type type) {
        int level = 0;
        for (int slot = UPGRADE_SLOT_START; slot < UPGRADE_SLOT_END; slot++) {
            ItemStack stack = items.get(slot);
            if (stack.getItem() instanceof AbyssMachineUpgradeItem upgrade && upgrade.type() == type) {
                level += upgrade.level() * stack.getCount();
            }
        }
        return level;
    }

    private int factoryLanes() {
        int level = upgradeLevel(AbyssMachineUpgradeItem.Type.FACTORY);
        return level <= 0 ? 1 : level;
    }

    private boolean matchesCatalyst(ItemStack required) {
        ItemStack installed = items.get(CATALYST_SLOT);
        return !installed.isEmpty() && ItemStack.isSameItemSameTags(installed, required)
                && installed.getCount() >= required.getCount();
    }

    private int acceptedOperations(int slot, ItemStack result) {
        ItemStack output = items.get(slot);
        int room = output.isEmpty() ? result.getMaxStackSize()
                : ItemStack.isSameItemSameTags(output, result) ? output.getMaxStackSize() - output.getCount() : 0;
        return result.getCount() <= 0 ? 0 : room / result.getCount();
    }

    private static ItemStack multiplied(ItemStack stack, int amount) {
        ItemStack copy = stack.copy();
        copy.setCount(stack.getCount() * amount);
        return copy;
    }

    private void autoExport() {
        if (level == null || level.isClientSide || upgradeLevel(AbyssMachineUpgradeItem.Type.AUTO_EXPORT) <= 0) {
            return;
        }
        for (Direction direction : Direction.values()) {
            if (!sideModes.get(direction).allowsOutput()) continue;
            BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(direction));
            if (neighbor == null) continue;
            LazyOptional<IItemHandler> target = neighbor.getCapability(ForgeCapabilities.ITEM_HANDLER,
                    direction.getOpposite());
            target.ifPresent(handler -> {
                exportSlot(handler, OUTPUT_SLOT);
                exportSlot(handler, BYPRODUCT_SLOT);
            });
        }
    }

    private void exportSlot(IItemHandler target, int slot) {
        ItemStack current = items.get(slot);
        if (current.isEmpty()) return;
        ItemStack remainder = ItemHandlerHelper.insertItemStacked(target, current.copy(), false);
        if (remainder.getCount() != current.getCount()) {
            items.set(slot, remainder);
            setChanged();
        }
    }

    private boolean canAccept(int slot, ItemStack result) {
        ItemStack output = items.get(slot);
        return output.isEmpty() || (ItemStack.isSameItemSameTags(output, result)
                && output.getCount() + result.getCount() <= output.getMaxStackSize());
    }

    private void addOutput(int slot, ItemStack result) {
        ItemStack output = items.get(slot);
        if (output.isEmpty()) {
            items.set(slot, result.copy());
        } else {
            output.grow(result.getCount());
        }
    }

    @Override
    public Component getDisplayName() {
        return machineName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new AbyssManaMachineMenu(containerId, inventory, this, dataAccess);
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(items, slot, amount);
        if (!removed.isEmpty()) {
            if (slot == INPUT_SLOT) workMana = 0;
            setChanged();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack removed = ContainerHelper.takeItem(items, slot);
        if (slot == INPUT_SLOT) workMana = 0;
        return removed;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack previous = items.get(slot);
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        if (slot == INPUT_SLOT && !ItemStack.isSameItemSameTags(previous, stack)) {
            workMana = 0;
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == INPUT_SLOT) return level != null && !resultFor(level, stack).isEmpty();
        if (slot == CATALYST_SLOT) return !(stack.getItem() instanceof AbyssMachineUpgradeItem);
        return slot >= UPGRADE_SLOT_START && slot < UPGRADE_SLOT_END
                && stack.getItem() instanceof AbyssMachineUpgradeItem;
    }

    @Override
    public void clearContent() {
        items.clear();
        workMana = 0;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("WorkMana", workMana);
        ContainerHelper.saveAllItems(tag, items);
        tag.put("EssenceTank", essenceTank.writeToNBT(new CompoundTag()));
        CompoundTag sides = new CompoundTag();
        sideModes.forEach((direction, mode) -> sides.putString(direction.getName(), mode.getSerializedName()));
        tag.put("SideModes", sides);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        workMana = Math.max(0, tag.getInt("WorkMana"));
        items.clear();
        ContainerHelper.loadAllItems(tag, items);
        essenceTank.readFromNBT(tag.getCompound("EssenceTank"));
        CompoundTag sides = tag.getCompound("SideModes");
        for (Direction direction : Direction.values()) {
            String saved = sides.getString(direction.getName());
            for (MachineSideMode mode : MachineSideMode.values()) {
                if (mode.getSerializedName().equals(saved)) sideModes.put(direction, mode);
            }
        }
    }

    public MachineSideMode cycleSideMode(Direction direction) {
        MachineSideMode next = sideModes.getOrDefault(direction, MachineSideMode.BOTH).next();
        sideModes.put(direction, next);
        LazyOptional<IItemHandler> removed = sidedHandlers.remove(direction);
        if (removed != null) removed.invalidate();
        LazyOptional<IFluidHandler> removedFluid = fluidHandlers.remove(direction);
        if (removedFluid != null) removedFluid.invalidate();
        setChanged();
        return next;
    }

    public MachineSideMode sideMode(Direction direction) {
        return sideModes.getOrDefault(direction, MachineSideMode.BOTH);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER && side != null
                && sideMode(side) != MachineSideMode.DISABLED) {
            return sidedHandlers.computeIfAbsent(side,
                    direction -> LazyOptional.of(() -> new SidedHandler(direction))).cast();
        }
        if (capability == ForgeCapabilities.FLUID_HANDLER && side != null
                && sideMode(side) != MachineSideMode.DISABLED)
            return fluidHandlers.computeIfAbsent(side,
                    direction -> LazyOptional.of(() -> new SidedFluidHandler(direction))).cast();
        return super.getCapability(capability, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        sidedHandlers.values().forEach(LazyOptional::invalidate);
        sidedHandlers.clear();
        fluidHandlers.values().forEach(LazyOptional::invalidate);
        fluidHandlers.clear();
    }

    private final class SidedHandler implements IItemHandler {
        private final Direction side;

        private SidedHandler(Direction side) { this.side = side; }
        @Override public int getSlots() { return items.size(); }
        @Override public ItemStack getStackInSlot(int slot) { return items.get(slot); }
        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!sideMode(side).allowsInput() || !canPlaceItem(slot, stack) || stack.isEmpty()) return stack;
            ItemStack current = items.get(slot);
            if (!current.isEmpty() && !ItemStack.isSameItemSameTags(current, stack)) return stack;
            int limit = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            int accepted = Math.min(stack.getCount(), limit - current.getCount());
            if (accepted <= 0) return stack;
            if (!simulate) {
                if (current.isEmpty()) items.set(slot, stack.copyWithCount(accepted)); else current.grow(accepted);
                setChanged();
            }
            ItemStack remainder = stack.copy();
            remainder.shrink(accepted);
            return remainder;
        }
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!sideMode(side).allowsOutput() || (slot != OUTPUT_SLOT && slot != BYPRODUCT_SLOT)) return ItemStack.EMPTY;
            ItemStack current = items.get(slot);
            int extracted = Math.min(amount, current.getCount());
            if (extracted <= 0) return ItemStack.EMPTY;
            ItemStack result = current.copyWithCount(extracted);
            if (!simulate) { current.shrink(extracted); setChanged(); }
            return result;
        }
        @Override public int getSlotLimit(int slot) { return slot >= UPGRADE_SLOT_START ? 8 : 64; }
        @Override public boolean isItemValid(int slot, ItemStack stack) { return canPlaceItem(slot, stack); }
    }

    private final class SidedFluidHandler implements IFluidHandler {
        private final Direction side;
        private SidedFluidHandler(Direction side) { this.side = side; }
        @Override public int getTanks() { return essenceTank.getTanks(); }
        @Override public FluidStack getFluidInTank(int tank) { return essenceTank.getFluidInTank(tank); }
        @Override public int getTankCapacity(int tank) { return essenceTank.getTankCapacity(tank); }
        @Override public boolean isFluidValid(int tank, FluidStack stack) { return essenceTank.isFluidValid(tank, stack); }
        @Override public int fill(FluidStack resource, FluidAction action) {
            return sideMode(side).allowsInput() ? essenceTank.fill(resource, action) : 0;
        }
        @Override public FluidStack drain(FluidStack resource, FluidAction action) {
            return sideMode(side).allowsOutput() ? essenceTank.drain(resource, action) : FluidStack.EMPTY;
        }
        @Override public FluidStack drain(int maxDrain, FluidAction action) {
            return sideMode(side).allowsOutput() ? essenceTank.drain(maxDrain, action) : FluidStack.EMPTY;
        }
    }
}
