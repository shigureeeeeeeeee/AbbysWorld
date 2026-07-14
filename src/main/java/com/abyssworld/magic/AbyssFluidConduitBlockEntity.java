package com.abyssworld.magic;

import com.abyssworld.block.AbyssFluidConduitBlock;
import com.abyssworld.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AbyssFluidConduitBlockEntity extends BlockEntity implements SideConfigurable {
    private final EnumMap<Direction, MachineSideMode> modes = new EnumMap<>(Direction.class);
    public AbyssFluidConduitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ABYSS_FLUID_CONDUIT.get(), pos, state);
        for (Direction direction : Direction.values()) modes.put(direction, MachineSideMode.BOTH);
    }
    public static void serverTick(Level level, BlockPos pos, BlockState state, AbyssFluidConduitBlockEntity conduit) {
        if (level.getGameTime() % 5L != Math.floorMod(pos.asLong(), 5)) return;
        List<IFluidHandler> sources = new ArrayList<>();
        List<IFluidHandler> targets = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>(); ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        visited.add(pos); queue.add(pos);
        while (!queue.isEmpty() && visited.size() < 2048) {
            BlockPos current = queue.removeFirst();
            if (!(level.getBlockEntity(current) instanceof AbyssFluidConduitBlockEntity node)) continue;
            for (Direction direction : Direction.values()) {
                BlockPos next = current.relative(direction);
                if (level.getBlockState(next).getBlock() instanceof AbyssFluidConduitBlock) {
                    if (visited.add(next.immutable())) queue.add(next.immutable());
                    continue;
                }
                BlockEntity endpoint = level.getBlockEntity(next);
                if (endpoint == null) continue;
                endpoint.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).ifPresent(handler -> {
                    if (node.sideMode(direction).allowsInput()) sources.add(handler);
                    if (node.sideMode(direction).allowsOutput()) targets.add(handler);
                });
            }
        }
        for (IFluidHandler source : sources) {
            FluidStack offered = source.drain(500, IFluidHandler.FluidAction.SIMULATE);
            if (offered.isEmpty()) continue;
            int remaining = offered.getAmount();
            for (IFluidHandler target : targets) {
                if (source == target) continue;
                FluidStack portion = offered.copy(); portion.setAmount(remaining);
                remaining -= target.fill(portion, IFluidHandler.FluidAction.EXECUTE);
                if (remaining <= 0) break;
            }
            int moved = offered.getAmount() - remaining;
            if (moved > 0) source.drain(moved, IFluidHandler.FluidAction.EXECUTE);
            return;
        }
    }
    @Override public MachineSideMode cycleSideMode(Direction direction) {
        MachineSideMode next = sideMode(direction).next(); modes.put(direction, next); setChanged(); return next;
    }
    @Override public MachineSideMode sideMode(Direction direction) { return modes.getOrDefault(direction, MachineSideMode.BOTH); }
    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag); CompoundTag sides = new CompoundTag();
        modes.forEach((direction, mode) -> sides.putString(direction.getName(), mode.getSerializedName())); tag.put("Sides", sides);
    }
    @Override public void load(CompoundTag tag) {
        super.load(tag); CompoundTag sides = tag.getCompound("Sides");
        for (Direction direction : Direction.values()) for (MachineSideMode mode : MachineSideMode.values())
            if (mode.getSerializedName().equals(sides.getString(direction.getName()))) modes.put(direction, mode);
    }
}
