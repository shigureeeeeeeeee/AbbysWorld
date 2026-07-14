package com.abyssworld.item;

import com.abyssworld.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class AbyssArmorModuleItem extends Item {
    private final Type type;

    public AbyssArmorModuleItem(Type type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public Type type() {
        return type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack moduleStack = player.getItemInHand(hand);
        InteractionHand otherHand = hand == InteractionHand.MAIN_HAND
                ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack armor = player.getItemInHand(otherHand);
        if (!(armor.getItem() instanceof SingularityAbyssArmorItem)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable(
                        "item.abyssworld.armor_module.needs_armor").withStyle(ChatFormatting.RED), true);
            }
            return InteractionResultHolder.fail(moduleStack);
        }
        if (level.isClientSide) {
            return InteractionResultHolder.success(moduleStack);
        }
        if (!SingularityAbyssArmorItem.installModule(armor, type)) {
            player.displayClientMessage(Component.translatable(
                    "item.abyssworld.armor_module.install_failed").withStyle(ChatFormatting.YELLOW), true);
            return InteractionResultHolder.fail(moduleStack);
        }

        if (!player.getAbilities().instabuild) {
            moduleStack.shrink(1);
        }
        player.displayClientMessage(Component.translatable("item.abyssworld.armor_module.installed",
                Component.translatable(type.translationKey())).withStyle(ChatFormatting.AQUA), true);
        level.playSound(null, player.blockPosition(), SoundEvents.SMITHING_TABLE_USE,
                SoundSource.PLAYERS, 0.9F, 1.45F);
        return InteractionResultHolder.success(moduleStack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(type.translationKey() + ".desc")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.abyssworld.armor_module.hint")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    public enum Type {
        VERDANT("verdant"),
        CINDER("cinder"),
        FROST("frost"),
        FLESH("flesh"),
        VOID("void");

        private final String serializedName;

        Type(String serializedName) {
            this.serializedName = serializedName;
        }

        public String serializedName() {
            return serializedName;
        }

        public String translationKey() {
            return "item.abyssworld.armor_module." + serializedName;
        }

        public static Optional<Type> byName(String name) {
            for (Type type : values()) {
                if (type.serializedName.equals(name)) {
                    return Optional.of(type);
                }
            }
            return Optional.empty();
        }
    }

    public static RegistryObject<Item> itemFor(Type type) {
        return switch (type) {
            case VERDANT -> ModItems.VERDANT_ARMOR_MODULE;
            case CINDER -> ModItems.CINDER_ARMOR_MODULE;
            case FROST -> ModItems.FROST_ARMOR_MODULE;
            case FLESH -> ModItems.FLESH_ARMOR_MODULE;
            case VOID -> ModItems.VOID_ARMOR_MODULE;
        };
    }
}
