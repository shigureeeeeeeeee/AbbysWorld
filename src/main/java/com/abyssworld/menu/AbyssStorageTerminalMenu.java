package com.abyssworld.menu;

import com.abyssworld.magic.AbyssStorageTerminalBlockEntity;
import com.abyssworld.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class AbyssStorageTerminalMenu extends AbstractContainerMenu {
    private final AbyssStorageTerminalBlockEntity terminal;
    public AbyssStorageTerminalMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, terminal(inventory, buffer));
    }
    public AbyssStorageTerminalMenu(int id, Inventory playerInventory, AbyssStorageTerminalBlockEntity terminal) {
        super(ModMenus.ABYSS_STORAGE_TERMINAL.get(), id); this.terminal = terminal;
        for (int row = 0; row < 6; row++) for (int col = 0; col < 9; col++)
            addSlot(new SlotItemHandler(terminal.inventory(), col + row * 9, 8 + col * 18, 18 + row * 18));
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(playerInventory, col, 8 + col * 18, 198));
    }
    private static AbyssStorageTerminalBlockEntity terminal(Inventory inventory, FriendlyByteBuf buffer) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(buffer.readBlockPos());
        if (blockEntity instanceof AbyssStorageTerminalBlockEntity terminal) return terminal;
        throw new IllegalStateException("Missing abyss storage terminal");
    }
    @Override public boolean stillValid(Player player) {
        return !terminal.isRemoved() && player.distanceToSqr(terminal.getBlockPos().getCenter()) <= 64.0;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index); if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack source = slot.getItem(); ItemStack original = source.copy();
        if (index < 54) { if (!moveItemStackTo(source, 54, slots.size(), true)) return ItemStack.EMPTY; }
        else if (!moveItemStackTo(source, 0, 54, false)) return ItemStack.EMPTY;
        if (source.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        return original;
    }
}
