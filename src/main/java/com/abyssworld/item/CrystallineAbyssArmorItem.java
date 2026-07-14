package com.abyssworld.item;

import com.abyssworld.client.AbyssArmorClientExtensions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class CrystallineAbyssArmorItem extends ArmorItem {
    public CrystallineAbyssArmorItem(Type type, Properties properties) {
        super(AbyssArmorMaterial.CRYSTALLINE, type, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(AbyssArmorClientExtensions.crystalline());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.abyssworld.crystalline_abyss_armor.desc")
                .withStyle(ChatFormatting.AQUA));
    }
}
