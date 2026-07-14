package com.abyssworld.magic;

import com.abyssworld.block.AbyssManaConduitBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class AbyssManaNetwork {
    private static final int MAX_VISITED_CONDUITS = 4096;

    private AbyssManaNetwork() {
    }

    public static List<AbyssManaPoolBlockEntity> pools(Level level, BlockPos pos, int range) {
        int maxDistance = Math.max(1, range);
        Set<AbyssManaPoolBlockEntity> found = new LinkedHashSet<>();
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<Node> queue = new ArrayDeque<>();

        for (Direction direction : Direction.values()) {
            inspect(level, pos.relative(direction), 1, maxDistance, found, visited, queue);
        }

        while (!queue.isEmpty() && visited.size() <= MAX_VISITED_CONDUITS) {
            Node node = queue.removeFirst();
            if (node.distance() >= maxDistance) {
                continue;
            }
            for (Direction direction : Direction.values()) {
                inspect(level, node.pos().relative(direction), node.distance() + 1,
                        maxDistance, found, visited, queue);
            }
        }
        return List.copyOf(found);
    }

    public static int storedMana(Level level, BlockPos pos, int range) {
        return storedMana(pools(level, pos, range));
    }

    public static int storedMana(List<AbyssManaPoolBlockEntity> pools) {
        long stored = 0;
        boolean countedWireless = false;
        for (AbyssManaPoolBlockEntity pool : pools) {
            boolean wireless = pool.getBlockState().getBlock() instanceof com.abyssworld.block.AbyssManaPoolBlock block
                    && block.tier() == com.abyssworld.block.AbyssManaPoolBlock.Tier.WIRELESS;
            if (wireless && countedWireless) continue;
            stored += pool.mana();
            if (wireless) countedWireless = true;
            if (stored >= Integer.MAX_VALUE) return Integer.MAX_VALUE;
        }
        return (int) Math.min(Integer.MAX_VALUE, stored);
    }

    public static boolean consumeMana(Level level, BlockPos pos, int range, int amount) {
        if (storedMana(level, pos, range) < amount) {
            return false;
        }
        return consumeUpTo(level, pos, range, amount) == amount;
    }

    public static int consumeUpTo(Level level, BlockPos pos, int range, int amount) {
        return consumeUpTo(pools(level, pos, range), amount);
    }

    public static int consumeUpTo(List<AbyssManaPoolBlockEntity> pools, int amount) {
        int remaining = Math.max(0, amount);
        int consumed = 0;
        for (AbyssManaPoolBlockEntity pool : pools) {
            int drained = pool.consumeMana(remaining);
            remaining -= drained;
            consumed += drained;
            if (remaining <= 0) {
                break;
            }
        }
        return consumed;
    }

    private static void inspect(Level level, BlockPos pos, int distance, int maxDistance,
                                Set<AbyssManaPoolBlockEntity> found, Set<BlockPos> visited,
                                ArrayDeque<Node> queue) {
        if (distance > maxDistance || !level.hasChunkAt(pos)) {
            return;
        }
        if (level.getBlockEntity(pos) instanceof AbyssManaPoolBlockEntity pool && pool.mana() > 0) {
            found.add(pool);
            return;
        }
        if (!(level.getBlockState(pos).getBlock() instanceof AbyssManaConduitBlock)
                || visited.size() >= MAX_VISITED_CONDUITS) {
            return;
        }
        BlockPos immutable = pos.immutable();
        if (visited.add(immutable)) {
            queue.addLast(new Node(immutable, distance));
        }
    }

    private record Node(BlockPos pos, int distance) {
    }
}
