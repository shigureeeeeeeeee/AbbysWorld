package com.abyssworld.magic;

import com.abyssworld.menu.AbyssStorageTerminalMenu;
import com.abyssworld.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class AbyssStorageTerminalBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler inventory = new ItemStackHandler(54) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }
    };
    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> inventory);
    public AbyssStorageTerminalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ABYSS_STORAGE_TERMINAL.get(), pos, state);
    }
    public int getSlots() { return inventory.getSlots(); }
    public net.minecraft.world.item.ItemStack getStackInSlot(int slot) { return inventory.getStackInSlot(slot); }
    public ItemStackHandler inventory() { return inventory; }
    @Override public Component getDisplayName() { return Component.translatable("block.abyssworld.abyss_storage_terminal"); }
    @Nullable @Override public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new AbyssStorageTerminalMenu(id, playerInventory, this);
    }
    @Override public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
        return capability == ForgeCapabilities.ITEM_HANDLER ? handler.cast() : super.getCapability(capability, side);
    }
    @Override public void invalidateCaps() { super.invalidateCaps(); handler.invalidate(); }
    @Override public void reviveCaps() { super.reviveCaps(); handler = LazyOptional.of(() -> inventory); }
    @Override protected void saveAdditional(CompoundTag tag) { super.saveAdditional(tag); tag.put("Inventory", inventory.serializeNBT()); }
    @Override public void load(CompoundTag tag) { super.load(tag); inventory.deserializeNBT(tag.getCompound("Inventory")); }
}
