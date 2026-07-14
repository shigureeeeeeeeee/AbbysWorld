package com.abyssworld.block;

import com.abyssworld.registry.ModBlocks;
import com.abyssworld.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class GroveSealBlock extends Block {
    private static final int MAX_SEAL_SIZE = 160;

    public GroveSealBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (!held.is(ModItems.GROVE_HEART_KEY.get())) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("block.abyssworld.grove_seal.locked")
                        .withStyle(ChatFormatting.DARK_GREEN), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        Set<BlockPos> seal = collectConnectedSeal(level, pos);
        for (BlockPos sealPos : seal) {
            level.setBlock(sealPos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
        }
        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }
        if (level instanceof ServerLevel serverLevel) {
            BlockPos center = seal.stream().reduce(pos, (left, right) ->
                    left.distSqr(pos) < right.distSqr(pos) ? left : right);
            serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                    center.getX() + 0.5D, center.getY() + 2.5D, center.getZ() + 0.5D,
                    60, 2.5D, 3.5D, 0.5D, 0.08D);
        }
        level.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(),
                SoundSource.BLOCKS, 1.4F, 0.65F);
        player.displayClientMessage(Component.translatable("block.abyssworld.grove_seal.opened")
                .withStyle(ChatFormatting.GREEN), true);
        return InteractionResult.CONSUME;
    }

    private static Set<BlockPos> collectConnectedSeal(Level level, BlockPos start) {
        Set<BlockPos> result = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start.immutable());
        while (!queue.isEmpty() && result.size() < MAX_SEAL_SIZE) {
            BlockPos current = queue.removeFirst();
            if (!result.add(current) || !level.getBlockState(current).is(ModBlocks.GROVE_SEAL.get())) {
                result.remove(current);
                continue;
            }
            for (Direction direction : Direction.values()) {
                BlockPos next = current.relative(direction);
                if (!result.contains(next) && next.distManhattan(start) <= 18) {
                    queue.addLast(next.immutable());
                }
            }
        }
        return result;
    }
}
