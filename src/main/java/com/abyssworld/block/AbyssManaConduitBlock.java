package com.abyssworld.block;

import com.abyssworld.magic.AbyssManaEndpoint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AbyssManaConduitBlock extends Block {
    private static final VoxelShape CENTER = box(5, 5, 5, 11, 11, 11);
    private static final VoxelShape NORTH_SHAPE = box(5, 5, 0, 11, 11, 5);
    private static final VoxelShape SOUTH_SHAPE = box(5, 5, 11, 11, 11, 16);
    private static final VoxelShape WEST_SHAPE = box(0, 5, 5, 5, 11, 11);
    private static final VoxelShape EAST_SHAPE = box(11, 5, 5, 16, 11, 11);
    private static final VoxelShape DOWN_SHAPE = box(5, 0, 5, 11, 5, 11);
    private static final VoxelShape UP_SHAPE = box(5, 11, 5, 11, 16, 11);

    public AbyssManaConduitBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(BlockStateProperties.NORTH, false)
                .setValue(BlockStateProperties.SOUTH, false)
                .setValue(BlockStateProperties.WEST, false)
                .setValue(BlockStateProperties.EAST, false)
                .setValue(BlockStateProperties.DOWN, false)
                .setValue(BlockStateProperties.UP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.NORTH, BlockStateProperties.SOUTH,
                BlockStateProperties.WEST, BlockStateProperties.EAST,
                BlockStateProperties.DOWN, BlockStateProperties.UP);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        LevelAccessor level = context.getLevel();
        BlockState state = defaultBlockState();
        for (Direction direction : Direction.values()) {
            state = state.setValue(property(direction), connectsTo(level.getBlockState(pos.relative(direction))));
        }
        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return state.setValue(property(direction), connectsTo(neighborState));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CENTER;
        if (state.getValue(BlockStateProperties.NORTH)) shape = Shapes.or(shape, NORTH_SHAPE);
        if (state.getValue(BlockStateProperties.SOUTH)) shape = Shapes.or(shape, SOUTH_SHAPE);
        if (state.getValue(BlockStateProperties.WEST)) shape = Shapes.or(shape, WEST_SHAPE);
        if (state.getValue(BlockStateProperties.EAST)) shape = Shapes.or(shape, EAST_SHAPE);
        if (state.getValue(BlockStateProperties.DOWN)) shape = Shapes.or(shape, DOWN_SHAPE);
        if (state.getValue(BlockStateProperties.UP)) shape = Shapes.or(shape, UP_SHAPE);
        return shape;
    }

    private static boolean connectsTo(BlockState state) {
        return state.getBlock() instanceof AbyssManaConduitBlock
                || state.getBlock() instanceof AbyssManaEndpoint;
    }

    private static BooleanProperty property(Direction direction) {
        return switch (direction) {
            case NORTH -> BlockStateProperties.NORTH;
            case SOUTH -> BlockStateProperties.SOUTH;
            case WEST -> BlockStateProperties.WEST;
            case EAST -> BlockStateProperties.EAST;
            case DOWN -> BlockStateProperties.DOWN;
            case UP -> BlockStateProperties.UP;
        };
    }
}
