package com.abyssworld.magic;

import com.abyssworld.registry.ModBlockEntities;
import com.abyssworld.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AbyssOreCrusherBlockEntity extends AbstractAbyssManaMachineBlockEntity {
    private static final int COST = 120;

    public AbyssOreCrusherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ABYSS_ORE_CRUSHER.get(), pos, state);
    }

    @Override
    protected Component machineName() {
        return Component.translatable("block.abyssworld.abyss_ore_crusher");
    }

    @Override
    protected ItemStack resultFor(Level level, ItemStack input) {
        return crushedResult(input, 2);
    }

    static ItemStack crushedResult(ItemStack input, int count) {
        Item item = input.getItem();
        if (item == Items.RAW_IRON || item == Items.IRON_ORE || item == Items.DEEPSLATE_IRON_ORE) {
            return new ItemStack(ModItems.CRUSHED_IRON.get(), count);
        }
        if (item == Items.RAW_GOLD || item == Items.GOLD_ORE || item == Items.DEEPSLATE_GOLD_ORE) {
            return new ItemStack(ModItems.CRUSHED_GOLD.get(), count);
        }
        if (item == Items.RAW_COPPER || item == Items.COPPER_ORE || item == Items.DEEPSLATE_COPPER_ORE) {
            return new ItemStack(ModItems.CRUSHED_COPPER.get(), count);
        }
        if (item == ModItems.RAW_ABYSS_IRON.get() || item == ModItems.ABYSS_IRON_ORE_ITEM.get()) {
            return new ItemStack(ModItems.CRUSHED_ABYSS_IRON.get(), count);
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected int manaCost(ItemStack input) {
        return input.isEmpty() ? 0 : COST;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AbyssOreCrusherBlockEntity crusher) {
        crusher.serverTick(level, pos, state);
    }
}
