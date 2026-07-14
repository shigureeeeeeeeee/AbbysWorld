package com.abyssworld.block;

import com.abyssworld.magic.AbyssInfusionRecipes;
import com.abyssworld.magic.AbyssManaEndpoint;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AbyssInfusionAltarBlock extends Block implements AbyssManaEndpoint {
    public AbyssInfusionAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        return AbyssInfusionRecipes.tryInfuse(level, pos, player, hand) ? InteractionResult.CONSUME : InteractionResult.FAIL;
    }
}
