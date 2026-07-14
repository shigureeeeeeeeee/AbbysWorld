package com.abyssworld.block;

import com.abyssworld.magic.AbyssStorageTerminalBlockEntity;
import com.abyssworld.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class AbyssStorageTerminalBlock extends BaseEntityBlock {
    public AbyssStorageTerminalBlock(Properties properties) { super(properties); }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AbyssStorageTerminalBlockEntity(pos, state);
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer
                && level.getBlockEntity(pos) instanceof AbyssStorageTerminalBlockEntity terminal) {
            NetworkHooks.openScreen(serverPlayer, terminal, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving) {
        if (!state.is(next.getBlock()) && level.getBlockEntity(pos) instanceof AbyssStorageTerminalBlockEntity terminal) {
            for (int slot = 0; slot < terminal.getSlots(); slot++) {
                ItemStack stack = terminal.getStackInSlot(slot);
                if (!stack.isEmpty()) net.minecraft.world.Containers.dropItemStack(level,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
            }
        }
        super.onRemove(state, level, pos, next, moving);
    }
}
