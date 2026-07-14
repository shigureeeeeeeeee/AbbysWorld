package com.abyssworld.magic;

import com.abyssworld.block.AbyssItemConduitBlock;
import com.abyssworld.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AbyssItemConduitBlockEntity extends BlockEntity implements SideConfigurable {
    private static final int MAX_NETWORK = 2048;
    private final EnumMap<Direction, MachineSideMode> modes = new EnumMap<>(Direction.class);
    private ItemStack filter = ItemStack.EMPTY;

    public AbyssItemConduitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ABYSS_ITEM_CONDUIT.get(), pos, state);
        for (Direction direction : Direction.values()) modes.put(direction, MachineSideMode.BOTH);
    }

    public void installFilter(ItemStack stack) {
        filter = stack.copyWithCount(stack.isEmpty() ? 0 : 1);
        setChanged();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AbyssItemConduitBlockEntity conduit) {
        if (level.getGameTime() % 5L != Math.floorMod(pos.asLong(), 5)) return;
        List<Endpoint> sources = new ArrayList<>();
        List<Endpoint> targets = new ArrayList<>();
        List<ItemStack> networkFilters = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        visited.add(pos); queue.add(pos);
        while (!queue.isEmpty() && visited.size() <= MAX_NETWORK) {
            BlockPos current = queue.removeFirst();
            if (!(level.getBlockEntity(current) instanceof AbyssItemConduitBlockEntity node)) continue;
            if (!node.filter.isEmpty()) networkFilters.add(node.filter);
            for (Direction direction : Direction.values()) {
                BlockPos adjacent = current.relative(direction);
                if (level.getBlockState(adjacent).getBlock() instanceof AbyssItemConduitBlock) {
                    if (visited.add(adjacent.immutable())) queue.add(adjacent.immutable());
                    continue;
                }
                BlockEntity endpoint = level.getBlockEntity(adjacent);
                if (endpoint == null) continue;
                endpoint.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite()).ifPresent(handler -> {
                    Endpoint found = new Endpoint(handler);
                    if (node.sideMode(direction).allowsInput()) sources.add(found);
                    if (node.sideMode(direction).allowsOutput()) targets.add(found);
                });
            }
        }
        transfer(sources, targets, networkFilters);
    }

    private static void transfer(List<Endpoint> sources, List<Endpoint> targets, List<ItemStack> filters) {
        for (Endpoint source : sources) {
            for (int slot = 0; slot < source.handler.getSlots(); slot++) {
                ItemStack simulated = source.handler.extractItem(slot, 8, true);
                if (simulated.isEmpty() || !matches(filters, simulated)) continue;
                ItemStack remainder = simulated.copy();
                for (Endpoint target : targets) {
                    if (target.handler == source.handler) continue;
                    remainder = ItemHandlerHelper.insertItemStacked(target.handler, remainder, false);
                    if (remainder.isEmpty()) break;
                }
                int moved = simulated.getCount() - remainder.getCount();
                if (moved > 0) source.handler.extractItem(slot, moved, false);
                return;
            }
        }
    }

    private static boolean matches(List<ItemStack> filters, ItemStack stack) {
        return filters.isEmpty() || filters.stream().anyMatch(filter -> ItemStack.isSameItemSameTags(filter, stack));
    }

    @Override public MachineSideMode cycleSideMode(Direction direction) {
        MachineSideMode next = sideMode(direction).next(); modes.put(direction, next); setChanged(); return next;
    }
    @Override public MachineSideMode sideMode(Direction direction) { return modes.getOrDefault(direction, MachineSideMode.BOTH); }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        CompoundTag sideTag = new CompoundTag();
        modes.forEach((direction, mode) -> sideTag.putString(direction.getName(), mode.getSerializedName()));
        tag.put("SideModes", sideTag);
        if (!filter.isEmpty()) tag.put("Filter", filter.save(new CompoundTag()));
    }

    @Override public void load(CompoundTag tag) {
        super.load(tag);
        CompoundTag sideTag = tag.getCompound("SideModes");
        for (Direction direction : Direction.values()) for (MachineSideMode mode : MachineSideMode.values()) {
            if (mode.getSerializedName().equals(sideTag.getString(direction.getName()))) modes.put(direction, mode);
        }
        filter = tag.contains("Filter") ? ItemStack.of(tag.getCompound("Filter")) : ItemStack.EMPTY;
    }

    private record Endpoint(IItemHandler handler) {}
}
