package com.abyssworld.item;

import com.abyssworld.client.AbyssArmorClientExtensions;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SingularityAbyssArmorItem extends ArmorItem {
    public static final int MODULE_SLOTS = 3;
    private static final String MODULES_TAG = "ArmorModules";

    public SingularityAbyssArmorItem(Type type, Properties properties) {
        super(AbyssArmorMaterial.SINGULARITY, type, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(AbyssArmorClientExtensions.singularity());
    }

    public static List<AbyssArmorModuleItem.Type> modules(ItemStack stack) {
        List<AbyssArmorModuleItem.Type> modules = new ArrayList<>();
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(MODULES_TAG, CompoundTag.TAG_LIST)) {
            return modules;
        }
        ListTag list = tag.getList(MODULES_TAG, CompoundTag.TAG_STRING);
        for (int index = 0; index < list.size(); index++) {
            AbyssArmorModuleItem.Type.byName(list.getString(index)).ifPresent(modules::add);
        }
        return modules;
    }

    public static boolean installModule(ItemStack stack, AbyssArmorModuleItem.Type type) {
        List<AbyssArmorModuleItem.Type> installed = modules(stack);
        if (installed.size() >= MODULE_SLOTS || installed.contains(type)) {
            return false;
        }
        ListTag list = stack.getOrCreateTag().getList(MODULES_TAG, CompoundTag.TAG_STRING);
        list.add(StringTag.valueOf(type.serializedName()));
        stack.getOrCreateTag().put(MODULES_TAG, list);
        return true;
    }

    @Nullable
    private static AbyssArmorModuleItem.Type removeLastModule(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(MODULES_TAG, CompoundTag.TAG_LIST)) {
            return null;
        }
        ListTag list = tag.getList(MODULES_TAG, CompoundTag.TAG_STRING);
        if (list.isEmpty()) {
            return null;
        }
        AbyssArmorModuleItem.Type removed = AbyssArmorModuleItem.Type.byName(
                list.getString(list.size() - 1)).orElse(null);
        list.remove(list.size() - 1);
        tag.put(MODULES_TAG, list);
        return removed;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown()) {
            return super.use(level, player, hand);
        }
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        AbyssArmorModuleItem.Type removed = removeLastModule(stack);
        if (removed == null) {
            player.displayClientMessage(Component.translatable(
                    "item.abyssworld.singularity_armor.no_modules").withStyle(ChatFormatting.GRAY), true);
            return InteractionResultHolder.fail(stack);
        }
        ItemStack module = new ItemStack(AbyssArmorModuleItem.itemFor(removed).get());
        if (!player.getInventory().add(module)) {
            player.drop(module, false);
        }
        player.displayClientMessage(Component.translatable(
                "item.abyssworld.singularity_armor.removed",
                Component.translatable(removed.translationKey())).withStyle(ChatFormatting.AQUA), true);
        level.playSound(null, player.blockPosition(), SoundEvents.SMITHING_TABLE_USE,
                SoundSource.PLAYERS, 0.7F, 1.25F);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return !modules(stack).isEmpty();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        List<AbyssArmorModuleItem.Type> installed = modules(stack);
        tooltip.add(Component.translatable("item.abyssworld.singularity_armor.modules",
                installed.size(), MODULE_SLOTS).withStyle(ChatFormatting.LIGHT_PURPLE));
        for (AbyssArmorModuleItem.Type module : installed) {
            tooltip.add(Component.literal("  ").append(Component.translatable(module.translationKey()))
                    .withStyle(ChatFormatting.AQUA));
        }
        tooltip.add(Component.translatable("item.abyssworld.singularity_armor.hint")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
