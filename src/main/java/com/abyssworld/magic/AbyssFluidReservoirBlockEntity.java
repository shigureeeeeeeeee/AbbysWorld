package com.abyssworld.magic;

import com.abyssworld.registry.ModBlockEntities;
import com.abyssworld.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class AbyssFluidReservoirBlockEntity extends BlockEntity {
    private final FluidTank tank = new FluidTank(256_000,
            stack -> stack.getFluid().isSame(ModFluids.ABYSSAL_ESSENCE.get())) {
        @Override protected void onContentsChanged() { setChanged(); }
    };
    private LazyOptional<IFluidHandler> handler = LazyOptional.of(() -> tank);
    public AbyssFluidReservoirBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ABYSS_FLUID_RESERVOIR.get(), pos, state);
    }
    public int amount() { return tank.getFluidAmount(); }
    public int capacity() { return tank.getCapacity(); }
    @Override public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
        return capability == ForgeCapabilities.FLUID_HANDLER ? handler.cast() : super.getCapability(capability, side);
    }
    @Override public void invalidateCaps() { super.invalidateCaps(); handler.invalidate(); }
    @Override public void reviveCaps() { super.reviveCaps(); handler = LazyOptional.of(() -> tank); }
    @Override protected void saveAdditional(CompoundTag tag) { super.saveAdditional(tag); tank.writeToNBT(tag); }
    @Override public void load(CompoundTag tag) { super.load(tag); tank.readFromNBT(tag); }
}
