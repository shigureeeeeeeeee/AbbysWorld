package com.abyssworld.block;

import com.abyssworld.magic.AbstractAbyssManaMachineBlockEntity;
import com.abyssworld.magic.AbyssManaEndpoint;
import com.abyssworld.magic.AbyssOreAmplifierBlockEntity;
import com.abyssworld.registry.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
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

public class AbyssOreAmplifierBlock extends BaseEntityBlock implements AbyssManaEndpoint {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private final Stage stage;

    public AbyssOreAmplifierBlock(Properties properties, Stage stage) {
        super(properties);
        this.stage = stage;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public Stage stage() {
        return stage;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AbyssOreAmplifierBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer
                && level.getBlockEntity(pos) instanceof AbyssOreAmplifierBlockEntity amplifier) {
            if (!amplifier.isStructureFormed()) {
                player.displayClientMessage(Component.translatable(
                        "block.abyssworld.abyss_ore_amplifier.invalid_structure",
                        stage.width(), stage.height(), stage.depth())
                        .withStyle(ChatFormatting.YELLOW), true);
            }
            NetworkHooks.openScreen(serverPlayer, amplifier, pos);
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
        return createTickerHelper(type, ModBlockEntities.ABYSS_ORE_AMPLIFIER.get(),
                AbyssOreAmplifierBlockEntity::serverTick);
    }

    public enum Stage {
        FOUR(3, 3, 3, 3, 4, 600, 24, "block.abyssworld.abyss_ore_amplifier"),
        SIX(3, 4, 3, 4, 6, 1_200, 32, "block.abyssworld.abyss_ore_crystallizer"),
        EIGHT(5, 3, 3, 6, 8, 2_400, 48, "block.abyssworld.abyss_ore_resonator"),
        TEN(5, 4, 3, 8, 10, 4_800, 64, "block.abyssworld.abyss_ore_singularity_separator");

        private final int width;
        private final int height;
        private final int depth;
        private final int inputGrade;
        private final int outputGrade;
        private final int manaCost;
        private final int maxManaPerTick;
        private final String translationKey;

        Stage(int width, int height, int depth, int inputGrade, int outputGrade,
              int manaCost, int maxManaPerTick, String translationKey) {
            this.width = width;
            this.height = height;
            this.depth = depth;
            this.inputGrade = inputGrade;
            this.outputGrade = outputGrade;
            this.manaCost = manaCost;
            this.maxManaPerTick = maxManaPerTick;
            this.translationKey = translationKey;
        }

        public int width() {
            return width;
        }

        public int height() {
            return height;
        }

        public int depth() {
            return depth;
        }

        public int inputGrade() {
            return inputGrade;
        }

        public int outputGrade() {
            return outputGrade;
        }

        public int manaCost() {
            return manaCost;
        }

        public int maxManaPerTick() {
            return maxManaPerTick;
        }

        public String translationKey() {
            return translationKey;
        }
    }
}
