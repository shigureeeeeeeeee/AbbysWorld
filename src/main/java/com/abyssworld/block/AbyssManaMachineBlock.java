package com.abyssworld.block;

import com.abyssworld.magic.AbstractAbyssManaMachineBlockEntity;
import com.abyssworld.magic.AbyssManaEndpoint;
import com.abyssworld.magic.AbyssManaHeaterBlockEntity;
import com.abyssworld.magic.AbyssOreCrusherBlockEntity;
import com.abyssworld.magic.AbyssOreRefineryBlockEntity;
import com.abyssworld.magic.AbyssEssenceExtractorBlockEntity;
import com.abyssworld.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class AbyssManaMachineBlock extends BaseEntityBlock implements AbyssManaEndpoint {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public enum Kind { HEATER, CRUSHER, REFINERY, ESSENCE_EXTRACTOR }

    private final Kind kind;

    public AbyssManaMachineBlock(Properties properties, Kind kind) {
        super(properties);
        this.kind = kind;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return switch (kind) {
            case HEATER -> new AbyssManaHeaterBlockEntity(pos, state);
            case CRUSHER -> new AbyssOreCrusherBlockEntity(pos, state);
            case REFINERY -> new AbyssOreRefineryBlockEntity(pos, state);
            case ESSENCE_EXTRACTOR -> new AbyssEssenceExtractorBlockEntity(pos, state);
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer
                && level.getBlockEntity(pos) instanceof AbstractAbyssManaMachineBlockEntity machine) {
            NetworkHooks.openScreen(serverPlayer, machine, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof AbstractAbyssManaMachineBlockEntity machine) {
                Containers.dropContents(level, pos, machine);
            }
            super.onRemove(state, level, pos, newState, moving);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                   BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        if (kind == Kind.HEATER) {
            return createTickerHelper(type, ModBlockEntities.ABYSS_MANA_HEATER.get(),
                    AbyssManaHeaterBlockEntity::serverTick);
        }
        if (kind == Kind.CRUSHER) {
            return createTickerHelper(type, ModBlockEntities.ABYSS_ORE_CRUSHER.get(),
                    AbyssOreCrusherBlockEntity::serverTick);
        }
        if (kind == Kind.ESSENCE_EXTRACTOR) {
            return createTickerHelper(type, ModBlockEntities.ABYSS_ESSENCE_EXTRACTOR.get(),
                    AbyssEssenceExtractorBlockEntity::serverTick);
        }
        return createTickerHelper(type, ModBlockEntities.ABYSS_ORE_REFINERY.get(),
                AbyssOreRefineryBlockEntity::serverTick);
    }
}
