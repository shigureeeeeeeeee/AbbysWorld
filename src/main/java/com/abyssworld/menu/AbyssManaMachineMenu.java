package com.abyssworld.menu;

import com.abyssworld.magic.AbstractAbyssManaMachineBlockEntity;
import com.abyssworld.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AbyssManaMachineMenu extends AbstractContainerMenu {
    private static final int MACHINE_SLOTS = 7;
    private static final int DATA_COUNT = 7;
    private final AbstractAbyssManaMachineBlockEntity machine;
    private final ContainerData data;

    public AbyssManaMachineMenu(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
        this(containerId, inventory, blockEntity(inventory, buffer), new SimpleContainerData(DATA_COUNT));
    }

    public AbyssManaMachineMenu(int containerId, Inventory inventory,
                                AbstractAbyssManaMachineBlockEntity machine, ContainerData data) {
        super(ModMenus.ABYSS_MANA_MACHINE.get(), containerId);
        this.machine = machine;
        this.data = data;
        checkContainerSize(machine, MACHINE_SLOTS);
        checkContainerDataCount(data, DATA_COUNT);

        addSlot(new Slot(machine, AbstractAbyssManaMachineBlockEntity.INPUT_SLOT, 27, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return machine.canPlaceItem(AbstractAbyssManaMachineBlockEntity.INPUT_SLOT, stack);
            }
        });
        addSlot(new Slot(machine, AbstractAbyssManaMachineBlockEntity.OUTPUT_SLOT, 109, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        addSlot(new Slot(machine, AbstractAbyssManaMachineBlockEntity.BYPRODUCT_SLOT, 143, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        addSlot(new Slot(machine, AbstractAbyssManaMachineBlockEntity.CATALYST_SLOT, 60, 35) {
            @Override public boolean mayPlace(ItemStack stack) { return machine.canPlaceItem(index, stack); }
        });
        for (int slot = AbstractAbyssManaMachineBlockEntity.UPGRADE_SLOT_START;
             slot < AbstractAbyssManaMachineBlockEntity.UPGRADE_SLOT_END; slot++) {
            addSlot(new Slot(machine, slot, 198, 26 + (slot - 4) * 20) {
                @Override public boolean mayPlace(ItemStack stack) { return machine.canPlaceItem(index, stack); }
                @Override public int getMaxStackSize() { return 8; }
            });
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9,
                        34 + column * 18, 100 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 34 + column * 18, 158));
        }
        addDataSlots(data);
    }

    private static AbstractAbyssManaMachineBlockEntity blockEntity(Inventory inventory, FriendlyByteBuf buffer) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(buffer.readBlockPos());
        if (blockEntity instanceof AbstractAbyssManaMachineBlockEntity machine) {
            return machine;
        }
        throw new IllegalStateException("Missing abyss mana machine block entity");
    }

    public int workMana() {
        return data.get(0);
    }

    public int manaCost() {
        return data.get(1);
    }

    public int connectedMana() {
        return (data.get(2) & 0xFFFF) | ((data.get(3) & 0xFFFF) << 16);
    }

    public int throughput() {
        return data.get(4);
    }

    public int storedEssence() { return data.get(5); }

    public int essenceCapacity() { return data.get(6); }

    @Override
    public boolean stillValid(Player player) {
        return machine.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack source = slot.getItem();
        ItemStack original = source.copy();
        if (index < MACHINE_SLOTS) {
            if (!moveItemStackTo(source, MACHINE_SLOTS, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (machine.canPlaceItem(AbstractAbyssManaMachineBlockEntity.INPUT_SLOT, source)) {
            if (!moveItemStackTo(source, AbstractAbyssManaMachineBlockEntity.INPUT_SLOT,
                    AbstractAbyssManaMachineBlockEntity.INPUT_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (machine.canPlaceItem(AbstractAbyssManaMachineBlockEntity.UPGRADE_SLOT_START, source)) {
            if (!moveItemStackTo(source, AbstractAbyssManaMachineBlockEntity.UPGRADE_SLOT_START,
                    AbstractAbyssManaMachineBlockEntity.UPGRADE_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (machine.canPlaceItem(AbstractAbyssManaMachineBlockEntity.CATALYST_SLOT, source)) {
            if (!moveItemStackTo(source, AbstractAbyssManaMachineBlockEntity.CATALYST_SLOT,
                    AbstractAbyssManaMachineBlockEntity.CATALYST_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < MACHINE_SLOTS + 27) {
            if (!moveItemStackTo(source, MACHINE_SLOTS + 27, slots.size(), false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(source, MACHINE_SLOTS, MACHINE_SLOTS + 27, false)) {
            return ItemStack.EMPTY;
        }

        if (source.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        slot.onTake(player, source);
        return original;
    }
}
