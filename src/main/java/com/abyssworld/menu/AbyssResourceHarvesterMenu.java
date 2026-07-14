package com.abyssworld.menu;

import com.abyssworld.magic.AbyssResourceHarvesterBlockEntity;
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

public class AbyssResourceHarvesterMenu extends AbstractContainerMenu {
    private static final int DATA_COUNT = 5;
    private static final int MACHINE_SLOTS = 13;
    private final AbyssResourceHarvesterBlockEntity harvester;
    private final ContainerData data;

    public AbyssResourceHarvesterMenu(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
        this(containerId, inventory, blockEntity(inventory, buffer), new SimpleContainerData(DATA_COUNT));
    }

    public AbyssResourceHarvesterMenu(int containerId, Inventory inventory,
                                      AbyssResourceHarvesterBlockEntity harvester, ContainerData data) {
        super(ModMenus.ABYSS_RESOURCE_HARVESTER.get(), containerId);
        this.harvester = harvester;
        this.data = data;
        checkContainerSize(harvester, MACHINE_SLOTS);
        checkContainerDataCount(data, DATA_COUNT);

        addSlot(new Slot(harvester, 0, 26, 43) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return harvester.canPlaceItem(0, stack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                addSlot(new Slot(harvester, 1 + column + row * 3,
                        80 + column * 18, 25 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }
                });
            }
        }
        for (int slot = 10; slot < 13; slot++) {
            addSlot(new Slot(harvester, slot, 150, 25 + (slot - 10) * 20) {
                @Override public boolean mayPlace(ItemStack stack) { return harvester.canPlaceItem(index, stack); }
                @Override public int getMaxStackSize() { return 8; }
            });
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9,
                        8 + column * 18, 126 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 184));
        }
        addDataSlots(data);
    }

    private static AbyssResourceHarvesterBlockEntity blockEntity(Inventory inventory, FriendlyByteBuf buffer) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(buffer.readBlockPos());
        if (blockEntity instanceof AbyssResourceHarvesterBlockEntity harvester) {
            return harvester;
        }
        throw new IllegalStateException("Missing Abyss Resource Harvester block entity");
    }

    public int workMana() {
        return data.get(0);
    }

    public int manaCost() {
        return data.get(1);
    }

    public int nearbyMana() {
        return (data.get(2) & 0xFFFF) | ((data.get(3) & 0xFFFF) << 16);
    }

    public int throughput() {
        return data.get(4);
    }

    @Override
    public boolean stillValid(Player player) {
        return harvester.stillValid(player);
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
        } else if (harvester.canPlaceItem(0, source) && !slots.get(0).hasItem()) {
            if (!moveItemStackTo(source, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (harvester.canPlaceItem(10, source)) {
            if (!moveItemStackTo(source, 10, 13, false)) return ItemStack.EMPTY;
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
