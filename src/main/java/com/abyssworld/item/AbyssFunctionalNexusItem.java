package com.abyssworld.item;

import com.abyssworld.block.AbyssFunctionalNexusBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;

public class AbyssFunctionalNexusItem extends BlockItem {
    private final AbyssFunctionalNexusBlock.Kind kind;

    public AbyssFunctionalNexusItem(Block block, AbyssFunctionalNexusBlock.Kind kind, Properties properties) {
        super(block, properties);
        this.kind = kind;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.abyssworld.functional_nexus." +
                kind.name().toLowerCase() + ".desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.abyssworld.functional_nexus.stats",
                kind.radius(), kind.manaCost()).withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("item.abyssworld.functional_nexus.redstone")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
