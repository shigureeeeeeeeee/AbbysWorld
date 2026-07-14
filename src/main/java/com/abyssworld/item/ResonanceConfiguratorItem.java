package com.abyssworld.item;

import com.abyssworld.magic.MachineSideMode;
import com.abyssworld.magic.SideConfigurable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ResonanceConfiguratorItem extends Item {
    public ResonanceConfiguratorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (!(blockEntity instanceof SideConfigurable machine)) {
            return InteractionResult.PASS;
        }
        MachineSideMode mode = machine.cycleSideMode(context.getClickedFace());
        if (context.getPlayer() != null) {
            context.getPlayer().displayClientMessage(Component.translatable(
                    "item.abyssworld.resonance_configurator.mode",
                    Component.translatable("side.abyssworld." + context.getClickedFace().getName()),
                    Component.translatable("mode.abyssworld." + mode.getSerializedName()))
                    .withStyle(ChatFormatting.AQUA), true);
        }
        return InteractionResult.CONSUME;
    }
}
