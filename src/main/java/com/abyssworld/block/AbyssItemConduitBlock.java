package com.abyssworld.block;

import com.abyssworld.item.AbyssItemFilterItem;
import com.abyssworld.magic.AbyssItemConduitBlockEntity;
import com.abyssworld.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class AbyssItemConduitBlock extends BaseEntityBlock {
    private static final VoxelShape CORE = box(5, 5, 5, 11, 11, 11);

    public AbyssItemConduitBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(BlockStateProperties.NORTH, false).setValue(BlockStateProperties.SOUTH, false)
                .setValue(BlockStateProperties.WEST, false).setValue(BlockStateProperties.EAST, false)
                .setValue(BlockStateProperties.DOWN, false).setValue(BlockStateProperties.UP, false));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(BlockStateProperties.NORTH, BlockStateProperties.SOUTH, BlockStateProperties.WEST,
                BlockStateProperties.EAST, BlockStateProperties.DOWN, BlockStateProperties.UP);
    }

    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        for (Direction direction : Direction.values()) {
            state = state.setValue(property(direction), connects(context.getLevel(), context.getClickedPos().relative(direction)));
        }
        return state;
    }

    @Override public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor,
                                             LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return state.setValue(property(direction), connects(level, neighborPos));
    }

    private static boolean connects(LevelAccessor level, BlockPos pos) {
        if (level.getBlockState(pos).getBlock() instanceof AbyssItemConduitBlock) return true;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) return false;
        for (Direction direction : Direction.values()) {
            if (blockEntity.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER, direction).isPresent()) return true;
        }
        return false;
    }

    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CORE;
        if (state.getValue(BlockStateProperties.NORTH)) shape = Shapes.or(shape, box(5,5,0,11,11,5));
        if (state.getValue(BlockStateProperties.SOUTH)) shape = Shapes.or(shape, box(5,5,11,11,11,16));
        if (state.getValue(BlockStateProperties.WEST)) shape = Shapes.or(shape, box(0,5,5,5,11,11));
        if (state.getValue(BlockStateProperties.EAST)) shape = Shapes.or(shape, box(11,5,5,16,11,11));
        if (state.getValue(BlockStateProperties.DOWN)) shape = Shapes.or(shape, box(5,0,5,11,5,11));
        if (state.getValue(BlockStateProperties.UP)) shape = Shapes.or(shape, box(5,11,5,11,16,11));
        return shape;
    }

    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new AbyssItemConduitBlockEntity(pos, state); }

    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() instanceof AbyssItemFilterItem
                && level.getBlockEntity(pos) instanceof AbyssItemConduitBlockEntity conduit) {
            if (!level.isClientSide) conduit.installFilter(AbyssItemFilterItem.filter(held));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Nullable @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                                       BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.ABYSS_ITEM_CONDUIT.get(),
                AbyssItemConduitBlockEntity::serverTick);
    }

    private static BooleanProperty property(Direction direction) {
        return switch (direction) {
            case NORTH -> BlockStateProperties.NORTH; case SOUTH -> BlockStateProperties.SOUTH;
            case WEST -> BlockStateProperties.WEST; case EAST -> BlockStateProperties.EAST;
            case DOWN -> BlockStateProperties.DOWN; case UP -> BlockStateProperties.UP;
        };
    }
}
