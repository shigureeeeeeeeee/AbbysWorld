package com.abyssworld.magic;

import com.abyssworld.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class AbyssManaHeaterBlockEntity extends AbstractAbyssManaMachineBlockEntity {
    private static final int COST = 80;

    public AbyssManaHeaterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ABYSS_MANA_HEATER.get(), pos, state);
    }

    @Override
    protected Component machineName() {
        return Component.translatable("block.abyssworld.abyss_mana_heater");
    }

    @Override
    protected ItemStack resultFor(Level level, ItemStack input) {
        if (input.isEmpty()) {
            return ItemStack.EMPTY;
        }
        SimpleContainer container = new SimpleContainer(input.copyWithCount(1));
        Optional<SmeltingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, container, level);
        return recipe.map(value -> value.assemble(container, level.registryAccess()))
                .orElse(ItemStack.EMPTY);
    }

    @Override
    protected int manaCost(ItemStack input) {
        return input.isEmpty() ? 0 : COST;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AbyssManaHeaterBlockEntity heater) {
        heater.serverTick(level, pos, state);
    }
}
