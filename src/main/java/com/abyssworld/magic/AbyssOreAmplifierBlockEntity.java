package com.abyssworld.magic;

import com.abyssworld.block.AbyssOreAmplifierBlock;
import com.abyssworld.item.AbyssOreConcentrateItem;
import com.abyssworld.registry.ModBlockEntities;
import com.abyssworld.registry.ModBlocks;
import com.abyssworld.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AbyssOreAmplifierBlockEntity extends AbstractAbyssManaMachineBlockEntity {
    public AbyssOreAmplifierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ABYSS_ORE_AMPLIFIER.get(), pos, state);
    }

    @Override
    protected Component machineName() {
        return Component.translatable(stage().translationKey());
    }

    @Override
    protected ItemStack resultFor(Level level, ItemStack input) {
        Item concentrate = concentrateFor(input.getItem());
        AbyssOreAmplifierBlock.Stage stage = stage();
        if (concentrate == null || !acceptsStageInput(input, stage)) {
            return ItemStack.EMPTY;
        }
        return AbyssOreConcentrateItem.create(concentrate, stage.outputGrade(), stage.outputGrade());
    }

    @Override
    protected ItemStack byproductFor(Level level, ItemStack input) {
        if (!acceptsStageInput(input, stage())) {
            return ItemStack.EMPTY;
        }
        return switch (stage()) {
            case SIX -> new ItemStack(ModItems.CRYSTALLIZATION_RESIDUE.get());
            case TEN -> new ItemStack(ModItems.SINGULARITY_RESIDUE.get());
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    protected int manaCost(ItemStack input) {
        AbyssOreAmplifierBlock.Stage stage = stage();
        return acceptsStageInput(input, stage) ? stage.manaCost() : 0;
    }

    @Override
    protected int requiredInputCount(ItemStack input) {
        return stage().inputGrade();
    }

    @Override
    protected int maxManaPerTick() {
        return stage().maxManaPerTick();
    }

    @Override
    protected ItemStack requiredCatalyst(ItemStack input) {
        return switch (stage()) {
            case EIGHT -> new ItemStack(ModItems.RESONANCE_MATRIX.get());
            case TEN -> new ItemStack(ModItems.THERMAL_MATRIX.get());
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    protected int essenceCost(ItemStack input) {
        if (!acceptsStageInput(input, stage())) return 0;
        return switch (stage()) {
            case FOUR -> 0;
            case SIX -> 250;
            case EIGHT -> 500;
            case TEN -> 1000;
        };
    }

    @Override
    protected boolean canRunMachine(Level level, BlockPos pos, BlockState state) {
        return isStructureFormed(level, pos, state);
    }

    public boolean isStructureFormed() {
        return level != null && isStructureFormed(level, worldPosition, getBlockState());
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                  AbyssOreAmplifierBlockEntity amplifier) {
        amplifier.serverTick(level, pos, state);
    }

    private static boolean isStructureFormed(Level level, BlockPos controllerPos, BlockState controllerState) {
        if (!(controllerState.getBlock() instanceof AbyssOreAmplifierBlock)
                || !controllerState.hasProperty(AbyssOreAmplifierBlock.FACING)) {
            return false;
        }
        AbyssOreAmplifierBlock amplifierBlock = (AbyssOreAmplifierBlock) controllerState.getBlock();
        AbyssOreAmplifierBlock.Stage stage = amplifierBlock.stage();
        Direction outward = controllerState.getValue(AbyssOreAmplifierBlock.FACING);
        Direction inward = outward.getOpposite();
        Direction right = outward.getClockWise();
        int lateralRadius = stage.width() / 2;

        for (int lateral = -lateralRadius; lateral <= lateralRadius; lateral++) {
            for (int height = 0; height < stage.height(); height++) {
                for (int depth = 0; depth < stage.depth(); depth++) {
                    if (lateral == 0 && height == 0 && depth == 0) {
                        continue;
                    }
                    BlockPos check = controllerPos.relative(right, lateral).above(height).relative(inward, depth);
                    boolean boundary = Math.abs(lateral) == lateralRadius || height == 0
                            || height == stage.height() - 1 || depth == 0 || depth == stage.depth() - 1;
                    if (boundary) {
                        if (!level.getBlockState(check).is(ModBlocks.ABYSS_MACHINE_CASING.get())) {
                            return false;
                        }
                    } else if (!level.getBlockState(check).isAir()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private AbyssOreAmplifierBlock.Stage stage() {
        if (getBlockState().getBlock() instanceof AbyssOreAmplifierBlock amplifierBlock) {
            return amplifierBlock.stage();
        }
        return AbyssOreAmplifierBlock.Stage.FOUR;
    }

    private static boolean acceptsStageInput(ItemStack input, AbyssOreAmplifierBlock.Stage stage) {
        if (stage == AbyssOreAmplifierBlock.Stage.FOUR) {
            return isPurifiedDust(input.getItem());
        }
        return input.getItem() instanceof AbyssOreConcentrateItem
                && AbyssOreConcentrateItem.grade(input) == stage.inputGrade();
    }

    private static boolean isPurifiedDust(Item item) {
        return item == ModItems.PURIFIED_IRON_DUST.get()
                || item == ModItems.PURIFIED_GOLD_DUST.get()
                || item == ModItems.PURIFIED_COPPER_DUST.get()
                || item == ModItems.PURIFIED_ABYSS_IRON_DUST.get();
    }

    private static Item concentrateFor(Item item) {
        if (item == ModItems.PURIFIED_IRON_DUST.get() || item == ModItems.AMPLIFIED_IRON_CONCENTRATE.get()) {
            return ModItems.AMPLIFIED_IRON_CONCENTRATE.get();
        }
        if (item == ModItems.PURIFIED_GOLD_DUST.get() || item == ModItems.AMPLIFIED_GOLD_CONCENTRATE.get()) {
            return ModItems.AMPLIFIED_GOLD_CONCENTRATE.get();
        }
        if (item == ModItems.PURIFIED_COPPER_DUST.get() || item == ModItems.AMPLIFIED_COPPER_CONCENTRATE.get()) {
            return ModItems.AMPLIFIED_COPPER_CONCENTRATE.get();
        }
        if (item == ModItems.PURIFIED_ABYSS_IRON_DUST.get()
                || item == ModItems.AMPLIFIED_ABYSS_IRON_CONCENTRATE.get()) {
            return ModItems.AMPLIFIED_ABYSS_IRON_CONCENTRATE.get();
        }
        return null;
    }
}
