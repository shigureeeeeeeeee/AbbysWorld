package com.abyssworld.item;

import com.abyssworld.magic.SpellCasting;
import com.abyssworld.magic.SpellbookData;
import com.abyssworld.menu.SpellbookMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class AbyssSpellbookItem extends Item {
    public AbyssSpellbookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack book = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                int sourceSlot = hand == InteractionHand.MAIN_HAND
                        ? player.getInventory().selected
                        : player.getInventory().getContainerSize() - 1;
                NetworkHooks.openScreen(serverPlayer,
                        new SimpleMenuProvider((id, inventory, owner) ->
                                new SpellbookMenu(id, inventory, sourceSlot),
                                Component.translatable("container.abyssworld.spellbook")),
                        buffer -> buffer.writeVarInt(sourceSlot));
            }
            return InteractionResultHolder.sidedSuccess(book, level.isClientSide);
        }

        if (!level.isClientSide) {
            SpellCasting.cast((ServerPlayer) player, book);
        }
        return InteractionResultHolder.sidedSuccess(book, level.isClientSide);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return SpellbookData.isComplete(stack, SpellbookData.selectedSpell(stack));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        int selected = SpellbookData.selectedSpell(stack);
        tooltip.add(Component.translatable("item.abyssworld.abyss_spellbook.selected", selected + 1)
                .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("item.abyssworld.abyss_spellbook.desc")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.abyssworld.abyss_spellbook.configure")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
