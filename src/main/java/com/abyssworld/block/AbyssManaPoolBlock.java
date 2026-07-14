package com.abyssworld.block;

import com.abyssworld.magic.AbyssManaPoolBlockEntity;
import com.abyssworld.magic.AbyssManaEndpoint;
import com.abyssworld.registry.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
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

import javax.annotation.Nullable;

public class AbyssManaPoolBlock extends BaseEntityBlock implements AbyssManaEndpoint {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private final Tier tier;

    public AbyssManaPoolBlock(Properties properties, Tier tier) {
        super(properties);
        this.tier = tier;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public Tier tier() {
        return tier;
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
        return new AbyssManaPoolBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof AbyssManaPoolBlockEntity pool) {
            if (!pool.tryChargeFrom(player, hand)) {
                Component status = pool.isInfinite()
                        ? Component.translatable("block.abyssworld.creative_mana_source.status")
                        : Component.translatable("block.abyssworld.abyss_mana_pool.status",
                                pool.mana(), pool.capacity());
                player.displayClientMessage(status.copy().withStyle(ChatFormatting.DARK_PURPLE), true);
            }
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return createTickerHelper(type, ModBlockEntities.ABYSS_MANA_POOL.get(), AbyssManaPoolBlockEntity::serverTick);
    }

    public enum Tier {
        BASIC(10_000, 40, 1, 4, 2, 28, 1, false),
        CONDENSER(100_000, 20, 8, 32, 8, 256, 4, false),
        LIFE(150_000, 10, 0, 0, 4, 96, 2, false),
        INFERNO(300_000, 10, 0, 0, 8, 192, 3, false),
        CRYO(600_000, 10, 0, 0, 12, 320, 4, false),
        VOID(1_200_000, 5, 0, 0, 0, 512, 6, false),
        STORAGE(4_000_000, 20, 0, 0, 0, 0, 8, false),
        WIRELESS(10_000_000, 1, 0, 0, 0, 0, 8, false),
        REACTOR(16_000_000, 1, 0, 0, 0, 4096, 10, false),
        CREATIVE(Integer.MAX_VALUE, 1, 0, 0, 0, 0, 1, true);

        private final int capacity;
        private final int generationInterval;
        private final int normalBaseGain;
        private final int abyssBaseGain;
        private final int nearbyDepositGain;
        private final int maximumGain;
        private final int chargeMultiplier;
        private final boolean infinite;

        Tier(int capacity, int generationInterval, int normalBaseGain, int abyssBaseGain,
             int nearbyDepositGain, int maximumGain, int chargeMultiplier, boolean infinite) {
            this.capacity = capacity;
            this.generationInterval = generationInterval;
            this.normalBaseGain = normalBaseGain;
            this.abyssBaseGain = abyssBaseGain;
            this.nearbyDepositGain = nearbyDepositGain;
            this.maximumGain = maximumGain;
            this.chargeMultiplier = chargeMultiplier;
            this.infinite = infinite;
        }

        public int capacity() {
            return capacity;
        }

        public int generationInterval() {
            return generationInterval;
        }

        public int baseGain(boolean abyssDimension) {
            return abyssDimension ? abyssBaseGain : normalBaseGain;
        }

        public int nearbyDepositGain() {
            return nearbyDepositGain;
        }

        public int maximumGain() {
            return maximumGain;
        }

        public int chargeMultiplier() {
            return chargeMultiplier;
        }

        public boolean infinite() {
            return infinite;
        }
    }
}
