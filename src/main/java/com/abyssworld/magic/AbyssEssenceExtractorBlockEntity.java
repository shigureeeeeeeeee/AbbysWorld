package com.abyssworld.magic;

import com.abyssworld.registry.ModBlockEntities;
import com.abyssworld.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AbyssEssenceExtractorBlockEntity extends AbstractAbyssManaMachineBlockEntity {
    public AbyssEssenceExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ABYSS_ESSENCE_EXTRACTOR.get(), pos, state);
    }

    @Override protected Component machineName() {
        return Component.translatable("block.abyssworld.abyss_essence_extractor");
    }

    @Override protected ItemStack resultFor(Level level, ItemStack input) {
        return input.is(ModItems.ABYSS_CRYSTAL.get()) ? new ItemStack(ModItems.RESONANCE_MATRIX.get()) : ItemStack.EMPTY;
    }

    @Override protected int manaCost(ItemStack input) { return input.is(ModItems.ABYSS_CRYSTAL.get()) ? 500 : 0; }

    @Override protected int essenceProduced(ItemStack input) { return input.is(ModItems.ABYSS_CRYSTAL.get()) ? 1000 : 0; }

    @Override protected int maxManaPerTick() { return 32; }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AbyssEssenceExtractorBlockEntity entity) {
        entity.serverTick(level, pos, state);
    }
}
