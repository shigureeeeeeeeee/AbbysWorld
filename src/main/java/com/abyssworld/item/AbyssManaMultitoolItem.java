package com.abyssworld.item;

import com.abyssworld.magic.PortableMana;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AbyssManaMultitoolItem extends DiggerItem {
    public static final int MANA_PER_BLOCK = 12;
    private static final int MAX_VEIN_BLOCKS = 24;
    private static final String MODE_TAG = "MultitoolMode";
    private static final Set<UUID> EXTRA_BREAKING = new HashSet<>();

    public AbyssManaMultitoolItem(Tier tier, Properties properties) {
        super(4.0F, -2.4F, tier, BlockTags.MINEABLE_WITH_PICKAXE, properties);
    }

    public enum Mode {
        NORMAL,
        AREA,
        VEIN;

        private Mode next() {
            Mode[] modes = values();
            return modes[(ordinal() + 1) % modes.length];
        }

        public String translationKey() {
            return "item.abyssworld.abyss_mana_multitool.mode." + name().toLowerCase();
        }
    }

    public static Mode mode(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        int ordinal = tag == null ? 0 : tag.getInt(MODE_TAG);
        return Mode.values()[Mth.clamp(ordinal, 0, Mode.values().length - 1)];
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return isEffective(state) ? 12.0F : 1.0F;
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        return isEffective(state);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return isEffective(state);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state,
                             BlockPos pos, LivingEntity miner) {
        if (level.isClientSide || !(miner instanceof ServerPlayer player)) {
            return true;
        }
        if (!PortableMana.consume(player, MANA_PER_BLOCK)) {
            return true;
        }
        if (EXTRA_BREAKING.contains(player.getUUID())) {
            return true;
        }

        List<BlockPos> targets = switch (mode(stack)) {
            case NORMAL -> List.of();
            case AREA -> areaTargets(level, player, pos, state);
            case VEIN -> veinTargets(level, pos, state);
        };
        if (targets.isEmpty()) {
            return true;
        }

        EXTRA_BREAKING.add(player.getUUID());
        try {
            for (BlockPos target : targets) {
                if (!PortableMana.has(player, MANA_PER_BLOCK)) {
                    break;
                }
                player.gameMode.destroyBlock(target);
            }
        } finally {
            EXTRA_BREAKING.remove(player.getUUID());
        }
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(stack);
        }
        cycleMode(level, player, stack);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        cycleMode(context.getLevel(), player, context.getItemInHand());
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    private void cycleMode(Level level, Player player, ItemStack stack) {
        Mode next = mode(stack).next();
        stack.getOrCreateTag().putInt(MODE_TAG, next.ordinal());
        if (!level.isClientSide) {
            player.displayClientMessage(Component.translatable(
                    "item.abyssworld.abyss_mana_multitool.switched",
                    Component.translatable(next.translationKey())).withStyle(ChatFormatting.LIGHT_PURPLE), true);
            level.playSound(null, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.value(),
                    SoundSource.PLAYERS, 0.6F, 1.4F);
        }
    }

    private static List<BlockPos> areaTargets(Level level, ServerPlayer player,
                                               BlockPos origin, BlockState original) {
        List<BlockPos> targets = new ArrayList<>(8);
        float originalHardness = Math.max(0.0F, original.getDestroySpeed(level, origin));
        boolean horizontalPlane = Math.abs(player.getXRot()) >= 55.0F;
        Direction.Axis facingAxis = player.getDirection().getAxis();

        for (int first = -1; first <= 1; first++) {
            for (int second = -1; second <= 1; second++) {
                BlockPos target;
                if (horizontalPlane) {
                    target = origin.offset(first, 0, second);
                } else if (facingAxis == Direction.Axis.X) {
                    target = origin.offset(0, second, first);
                } else {
                    target = origin.offset(first, second, 0);
                }
                if (!target.equals(origin) && validAreaTarget(level, target, originalHardness)) {
                    targets.add(target.immutable());
                }
            }
        }
        return targets;
    }

    private static boolean validAreaTarget(Level level, BlockPos pos, float originalHardness) {
        if (!level.hasChunkAt(pos) || level.getBlockEntity(pos) != null) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        float hardness = state.getDestroySpeed(level, pos);
        return !state.isAir() && isEffective(state) && hardness >= 0.0F
                && hardness <= originalHardness * 3.0F + 1.0F;
    }

    private static List<BlockPos> veinTargets(Level level, BlockPos origin, BlockState original) {
        if (!original.is(Tags.Blocks.ORES)) {
            return List.of();
        }
        List<BlockPos> targets = new ArrayList<>(MAX_VEIN_BLOCKS - 1);
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(origin.immutable());
        visited.add(origin.immutable());

        while (!queue.isEmpty() && targets.size() < MAX_VEIN_BLOCKS - 1) {
            BlockPos current = queue.removeFirst();
            for (Direction direction : Direction.values()) {
                BlockPos next = current.relative(direction).immutable();
                if (!visited.add(next) || !level.hasChunkAt(next)
                        || next.distManhattan(origin) > 12) {
                    continue;
                }
                BlockState nextState = level.getBlockState(next);
                if (nextState.is(original.getBlock())) {
                    targets.add(next);
                    queue.addLast(next);
                    if (targets.size() >= MAX_VEIN_BLOCKS - 1) {
                        break;
                    }
                }
            }
        }
        return targets;
    }

    public static boolean isEffective(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE)
                || state.is(BlockTags.MINEABLE_WITH_AXE)
                || state.is(BlockTags.MINEABLE_WITH_SHOVEL)
                || state.is(BlockTags.MINEABLE_WITH_HOE);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.abyssworld.abyss_mana_multitool.current_mode",
                Component.translatable(mode(stack).translationKey())).withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("item.abyssworld.abyss_mana_multitool.desc")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.abyssworld.abyss_mana_multitool.cost", MANA_PER_BLOCK)
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("item.abyssworld.abyss_mana_multitool.hint")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
