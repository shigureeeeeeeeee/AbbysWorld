package com.abyssworld.block;

import com.abyssworld.item.LayerBossCatalystItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

public class LayerAltarBlock extends Block {
    private final Supplier<? extends EntityType<? extends Mob>> bossType;
    private final String bossTranslationKey;
    private final String requiredItemTranslationKey;

    public LayerAltarBlock(Properties properties,
                           Supplier<? extends EntityType<? extends Mob>> bossType,
                           String bossTranslationKey,
                           String requiredItemTranslationKey) {
        super(properties);
        this.bossType = bossType;
        this.bossTranslationKey = bossTranslationKey;
        this.requiredItemTranslationKey = requiredItemTranslationKey;
    }

    public boolean isAltarFor(EntityType<?> type) {
        return bossType.get().equals(type);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.getItemInHand(hand).getItem() instanceof LayerBossCatalystItem) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            player.displayClientMessage(Component.translatable("block.abyssworld.layer_altar.requires",
                    Component.translatable(requiredItemTranslationKey),
                    Component.translatable(bossTranslationKey)).withStyle(ChatFormatting.GRAY), true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
