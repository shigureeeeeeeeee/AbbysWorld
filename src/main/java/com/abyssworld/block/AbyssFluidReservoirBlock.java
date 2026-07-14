package com.abyssworld.block;

import com.abyssworld.magic.AbyssFluidReservoirBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nullable;

public class AbyssFluidReservoirBlock extends BaseEntityBlock {
    public AbyssFluidReservoirBlock(Properties properties) { super(properties); }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AbyssFluidReservoirBlockEntity(pos, state);
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof AbyssFluidReservoirBlockEntity reservoir)) return InteractionResult.PASS;
        if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, hit.getDirection()))
            return InteractionResult.sidedSuccess(level.isClientSide);
        if (!level.isClientSide) player.displayClientMessage(Component.translatable(
                "block.abyssworld.abyss_essence_reservoir.status", reservoir.amount(), reservoir.capacity()), true);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
