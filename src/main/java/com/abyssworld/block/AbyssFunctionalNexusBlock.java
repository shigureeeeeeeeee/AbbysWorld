package com.abyssworld.block;

import com.abyssworld.magic.AbyssFunctionalNexusBlockEntity;
import com.abyssworld.magic.AbyssManaEndpoint;
import com.abyssworld.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class AbyssFunctionalNexusBlock extends BaseEntityBlock implements AbyssManaEndpoint {
    private static final VoxelShape SHAPE = Shapes.or(
            box(1, 0, 1, 15, 3, 15),
            box(4, 3, 4, 12, 10, 12),
            box(3, 10, 3, 13, 12, 13),
            box(5, 12, 5, 11, 16, 11));
    private final Kind kind;

    public AbyssFunctionalNexusBlock(Properties properties, Kind kind) {
        super(properties);
        this.kind = kind;
    }

    public Kind kind() {
        return kind;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AbyssFunctionalNexusBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                   BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.ABYSS_FUNCTIONAL_NEXUS.get(),
                AbyssFunctionalNexusBlockEntity::serverTick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof AbyssFunctionalNexusBlockEntity nexus)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            if (kind == Kind.GATHERING && !player.isShiftKeyDown() && player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, nexus, pos);
            } else {
                player.displayClientMessage(Component.translatable("block.abyssworld.functional_nexus.status",
                        Component.translatable("block.abyssworld.functional_nexus.status." +
                                nexus.status().name().toLowerCase()), nexus.operations(), kind.radius(), kind.manaCost()), true);
                nexus.showRange();
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving) {
        if (!state.is(next.getBlock()) && level.getBlockEntity(pos) instanceof AbyssFunctionalNexusBlockEntity nexus) {
            for (int slot = 0; slot < nexus.getContainerSize(); slot++) {
                ItemStack stack = nexus.getItem(slot);
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX() + 0.5D, pos.getY() + 0.5D,
                            pos.getZ() + 0.5D, stack);
                }
            }
        }
        super.onRemove(state, level, pos, next, moving);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return kind == Kind.GATHERING;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof AbyssFunctionalNexusBlockEntity nexus
                ? AbstractContainerMenu.getRedstoneSignalFromContainer(nexus) : 0;
    }

    public enum Kind {
        VERDANT(6, 12),
        GATHERING(8, 4),
        WARDING(8, 10);

        private final int radius;
        private final int manaCost;

        Kind(int radius, int manaCost) {
            this.radius = radius;
            this.manaCost = manaCost;
        }

        public int radius() {
            return radius;
        }

        public int manaCost() {
            return manaCost;
        }
    }
}
