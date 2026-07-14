package com.abyssworld.magic;

import com.abyssworld.registry.ModBlockEntities;
import com.abyssworld.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AbyssOreRefineryBlockEntity extends AbstractAbyssManaMachineBlockEntity {
    private static final int COST = 180;

    public AbyssOreRefineryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ABYSS_ORE_REFINERY.get(), pos, state);
    }

    @Override
    protected Component machineName() {
        return Component.translatable("block.abyssworld.abyss_ore_refinery");
    }

    @Override
    protected ItemStack resultFor(Level level, ItemStack input) {
        Item item = input.getItem();
        if (item == ModItems.CRUSHED_IRON.get()) {
            return new ItemStack(ModItems.PURIFIED_IRON_DUST.get(), 3);
        }
        if (item == ModItems.CRUSHED_GOLD.get()) {
            return new ItemStack(ModItems.PURIFIED_GOLD_DUST.get(), 3);
        }
        if (item == ModItems.CRUSHED_COPPER.get()) {
            return new ItemStack(ModItems.PURIFIED_COPPER_DUST.get(), 3);
        }
        if (item == ModItems.CRUSHED_ABYSS_IRON.get()) {
            return new ItemStack(ModItems.PURIFIED_ABYSS_IRON_DUST.get(), 3);
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected int manaCost(ItemStack input) {
        return input.isEmpty() ? 0 : COST;
    }

    @Override
    protected int requiredInputCount(ItemStack input) {
        return 2;
    }

    @Override
    protected int maxManaPerTick() {
        return 16;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AbyssOreRefineryBlockEntity refinery) {
        refinery.serverTick(level, pos, state);
    }
}
