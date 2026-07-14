package com.abyssworld.menu;

import com.abyssworld.magic.LeylineMinerBlockEntity;
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

public class LeylineMinerMenu extends AbstractContainerMenu {
    private static final int DATA_COUNT = 14;
    private final LeylineMinerBlockEntity miner;
    private final ContainerData data;

    public LeylineMinerMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, blockEntity(inventory, buffer), new SimpleContainerData(DATA_COUNT));
    }

    public LeylineMinerMenu(int id, Inventory playerInventory, LeylineMinerBlockEntity miner, ContainerData data) {
        super(ModMenus.LEYLINE_MINER.get(), id);
        this.miner = miner; this.data = data;
        checkContainerSize(miner, LeylineMinerBlockEntity.TOTAL_SLOTS);
        checkContainerDataCount(data, DATA_COUNT);

        for (int slot = 0; slot < LeylineMinerBlockEntity.FILTER_COUNT; slot++) {
            addSlot(new Slot(miner, slot, 8 + slot * 20, 38) {
                @Override public boolean mayPlace(ItemStack stack) { return miner.canPlaceItem(index, stack); }
                @Override public int getMaxStackSize() { return 1; }
            });
        }
        addSlot(new Slot(miner, LeylineMinerBlockEntity.REPLACEMENT_SLOT, 92, 38) {
            @Override public boolean mayPlace(ItemStack stack) { return miner.canPlaceItem(index, stack); }
        });
        for (int row = 0; row < 3; row++) for (int col = 0; col < 3; col++) {
            int slot = LeylineMinerBlockEntity.OUTPUT_START + col + row * 3;
            addSlot(new Slot(miner, slot, 126 + col * 18, 28 + row * 18) {
                @Override public boolean mayPlace(ItemStack stack) { return false; }
            });
        }
        for (int slot = 0; slot < LeylineMinerBlockEntity.UPGRADE_COUNT; slot++) {
            addSlot(new Slot(miner, LeylineMinerBlockEntity.UPGRADE_START + slot, 204, 28 + slot * 20) {
                @Override public boolean mayPlace(ItemStack stack) { return miner.canPlaceItem(index, stack); }
                @Override public int getMaxStackSize() { return 8; }
            });
        }

        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            addSlot(new Slot(playerInventory, col + row * 9 + 9, 34 + col * 18, 153 + row * 18));
        for (int col = 0; col < 9; col++)
            addSlot(new Slot(playerInventory, col, 34 + col * 18, 211));
        addDataSlots(data);
    }

    private static LeylineMinerBlockEntity blockEntity(Inventory inventory, FriendlyByteBuf buffer) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(buffer.readBlockPos());
        if (blockEntity instanceof LeylineMinerBlockEntity miner) return miner;
        throw new IllegalStateException("Missing leyline miner block entity");
    }

    public int workMana() { return data.get(0); }
    public int manaCost() { return data.get(1); }
    public int nearbyMana() { return (data.get(2) & 0xFFFF) | ((data.get(3) & 0xFFFF) << 16); }
    public int throughput() { return data.get(4); }
    public boolean running() { return data.get(5) != 0; }
    public int radius() { return data.get(6); }
    public int depth() { return data.get(7); }
    public LeylineMinerBlockEntity.FilterMode filterMode() { return LeylineMinerBlockEntity.FilterMode.byOrdinal(data.get(8)); }
    public boolean silkTouch() { return data.get(9) != 0; }
    public boolean replacing() { return data.get(10) != 0; }
    public int minedBlocks() { return data.get(11); }
    public int scanProgress() { return data.get(12); }
    public LeylineMinerBlockEntity.Status status() { return LeylineMinerBlockEntity.Status.byOrdinal(data.get(13)); }

    @Override public boolean clickMenuButton(Player player, int id) {
        return id >= 0 && id <= 8 && miner.handleButton(id);
    }
    @Override public boolean stillValid(Player player) { return miner.stillValid(player); }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index); if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack source = slot.getItem(); ItemStack original = source.copy();
        int machineSlots = LeylineMinerBlockEntity.TOTAL_SLOTS;
        if (index < machineSlots) {
            if (!moveItemStackTo(source, machineSlots, slots.size(), true)) return ItemStack.EMPTY;
        } else if (miner.canPlaceItem(LeylineMinerBlockEntity.UPGRADE_START, source)) {
            if (!moveItemStackTo(source, LeylineMinerBlockEntity.UPGRADE_START,
                    LeylineMinerBlockEntity.UPGRADE_START + LeylineMinerBlockEntity.UPGRADE_COUNT, false))
                return ItemStack.EMPTY;
        } else if (miner.canPlaceItem(0, source)) {
            if (!moveItemStackTo(source, 0, LeylineMinerBlockEntity.FILTER_COUNT, false)) return ItemStack.EMPTY;
        } else if (index < machineSlots + 27) {
            if (!moveItemStackTo(source, machineSlots + 27, slots.size(), false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(source, machineSlots, machineSlots + 27, false)) return ItemStack.EMPTY;
        if (source.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        slot.onTake(player, source); return original;
    }
}
