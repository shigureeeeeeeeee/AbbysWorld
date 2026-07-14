package com.abyssworld.magic;

import com.abyssworld.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Supplier;

public final class AbyssInfusionRecipes {
    private static final int NETWORK_RANGE = 64;
    private static final List<Infusion> RECIPES = List.of(
            new Infusion(ModItems.RAW_ABYSS_IRON, ModItems.ABYSS_IRON_INGOT, 80, 1),
            new Infusion(ModItems.ABYSS_CRYSTAL, ModItems.COMPRESSED_ABYSS_CRYSTAL, 900, 1),
            new Infusion(ModItems.PRIMORDIAL_SAP, ModItems.AWAKENED_VINE, 350, 2),
            new Infusion(ModItems.ETERNAL_FLAME, ModItems.SUPERHEATED_CORE, 700, 1),
            new Infusion(ModItems.UNMELTING_ICE_CRYSTAL, ModItems.FROZEN_TIME_SHARD, 700, 1),
            new Infusion(ModItems.PRIMORDIAL_NERVE, ModItems.UNDYING_CELL, 700, 1),
            new Infusion(ModItems.SPATIAL_ANCHOR_CRYSTAL, ModItems.WORLD_LAW_FRAGMENT, 1000, 1)
    );

    private AbyssInfusionRecipes() {
    }

    public static boolean tryInfuse(Level level, BlockPos pos, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) {
            player.displayClientMessage(Component.translatable("block.abyssworld.abyss_infusion_altar.needs_item")
                    .withStyle(ChatFormatting.GRAY), true);
            return false;
        }

        Infusion recipe = find(held.getItem());
        if (recipe == null) {
            player.displayClientMessage(Component.translatable("block.abyssworld.abyss_infusion_altar.no_recipe")
                    .withStyle(ChatFormatting.YELLOW), true);
            return false;
        }

        int totalMana = AbyssManaNetwork.storedMana(level, pos, NETWORK_RANGE);
        if (totalMana < recipe.cost()) {
            player.displayClientMessage(Component.translatable("block.abyssworld.abyss_infusion_altar.no_mana",
                    totalMana, recipe.cost()).withStyle(ChatFormatting.RED), true);
            return false;
        }

        AbyssManaNetwork.consumeMana(level, pos, NETWORK_RANGE, recipe.cost());
        held.shrink(1);
        ItemStack result = new ItemStack(recipe.output().get(), recipe.count());
        if (!player.getInventory().add(result.copy())) {
            ItemEntity drop = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.2D, pos.getZ() + 0.5D, result);
            level.addFreshEntity(drop);
        }

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D,
                    48, 0.6D, 0.5D, 0.6D, 0.04D);
            serverLevel.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE,
                    SoundSource.BLOCKS, 1.0F, 0.55F);
        }
        player.displayClientMessage(Component.translatable("block.abyssworld.abyss_infusion_altar.success",
                result.getHoverName(), recipe.cost()).withStyle(ChatFormatting.DARK_PURPLE), true);
        return true;
    }

    private static Infusion find(Item input) {
        for (Infusion recipe : RECIPES) {
            if (recipe.input().get() == input) {
                return recipe;
            }
        }
        return null;
    }

    private record Infusion(Supplier<Item> input, Supplier<Item> output, int cost, int count) {
    }
}
