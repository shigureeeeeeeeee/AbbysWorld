package com.abyssworld.event;

import com.abyssworld.AbyssWorld;
import com.abyssworld.item.AbyssArmorModuleItem;
import com.abyssworld.item.AbyssKeyItem;
import com.abyssworld.item.AbyssManaMultitoolItem;
import com.abyssworld.item.AbyssSpellbookItem;
import com.abyssworld.item.LayerWeaponItem;
import com.abyssworld.item.SingularityAbyssArmorItem;
import com.abyssworld.magic.AbyssMagic;
import com.abyssworld.magic.PortableMana;
import com.abyssworld.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = AbyssWorld.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AbyssGameplayEvents {
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack tool = event.getEntity().getMainHandItem();
        if (tool.getItem() instanceof AbyssManaMultitoolItem
                && AbyssManaMultitoolItem.isEffective(event.getState())
                && !PortableMana.has(event.getEntity(), AbyssManaMultitoolItem.MANA_PER_BLOCK)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        ItemStack tool = event.getEntity().getMainHandItem();
        if (tool.getItem() instanceof AbyssManaMultitoolItem
                && AbyssManaMultitoolItem.isEffective(event.getTargetBlock())) {
            event.setCanHarvest(PortableMana.has(event.getEntity(), AbyssManaMultitoolItem.MANA_PER_BLOCK));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide
                || !(event.player instanceof ServerPlayer player)) {
            return;
        }

        boolean abyss = AbyssKeyItem.isAbyssDimension(player.level().dimension());
        if (abyss && player.tickCount % 40 == 0) {
            int gain = 2;
            if (holdsAbyssFocus(player)) {
                gain++;
            }
            if (countLayerWeapons(player) >= 5) {
                gain += 2;
            }
            AbyssMagic.addMana(player, gain);
        } else if (!abyss && player.tickCount % 80 == 0) {
            AbyssMagic.reduceStrain(player, 1);
        }

        if (abyss && player.tickCount % 80 == 0 && !player.isCreative() && !player.isSpectator()) {
            applyEnvironment(player, player.level().dimension());
        }

        if (player.tickCount % 80 == 0) {
            applyEquipmentResonance(player);
        }

        if (player.tickCount % 40 == 0) {
            applySingularityModules(player);
        }

        if (player.tickCount % 100 == 0 && AbyssMagic.strain(player) >= 80) {
            player.hurt(player.damageSources().magic(), 1.0F);
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 0));
            player.displayClientMessage(Component.translatable("magic.abyssworld.overload")
                    .withStyle(ChatFormatting.RED), true);
        }

        if (player.tickCount % 40 == 0 && (holdsAbyssFocus(player)
                || player.getMainHandItem().getItem() instanceof LayerWeaponItem
                || player.getMainHandItem().getItem() instanceof AbyssSpellbookItem
                || player.getOffhandItem().getItem() instanceof AbyssSpellbookItem)) {
            player.displayClientMessage(AbyssMagic.status(player), true);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            AbyssMagic.copy(event.getOriginal(), event.getEntity());
        }
    }

    private static void applyEnvironment(ServerPlayer player, ResourceKey<Level> dimension) {
        if (dimension.equals(AbyssKeyItem.FORGOTTEN_FOREST)) {
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0));
            AbyssMagic.addStrain(player, 1);
            return;
        }
        if (dimension.equals(AbyssKeyItem.ASH_WASTELAND)) {
            if (!player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                player.setSecondsOnFire(3);
                AbyssMagic.addStrain(player, 2);
            }
            return;
        }
        if (dimension.equals(AbyssKeyItem.FROZEN_CAVERN)) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 0));
            AbyssMagic.addStrain(player, 1);
            return;
        }
        if (dimension.equals(AbyssKeyItem.FLESH_MINE)) {
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 120, 0));
            if (player.getHealth() < player.getMaxHealth() * 0.35F) {
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
            }
            AbyssMagic.addStrain(player, 1);
            return;
        }
        if (dimension.equals(AbyssKeyItem.VOID_CITY)) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 120, 0));
            if (player.getRandom().nextInt(3) == 0) {
                player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 25, 0));
                AbyssMagic.addStrain(player, 2);
            }
            return;
        }
        if (dimension.equals(AbyssKeyItem.ABYSS)) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0));
            AbyssMagic.addStrain(player, 1);
        }
    }

    private static void applyEquipmentResonance(ServerPlayer player) {
        ItemStack main = player.getMainHandItem();
        if (!(main.getItem() instanceof LayerWeaponItem weapon) || !holdsAbyssFocus(player)) {
            return;
        }
        if (!AbyssMagic.consumeMana(player, 3)) {
            AbyssMagic.addStrain(player, 2);
            return;
        }
        AbyssMagic.addStrain(player, 1);
        switch (weapon.effect()) {
            case FOREST -> player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 90, 0));
            case ASH -> player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 120, 0));
            case FROST -> player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 90, 0));
            case FLESH -> player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 120, 0));
            case VOID -> player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 90, 0));
        }
    }

    private static void applySingularityModules(ServerPlayer player) {
        Map<AbyssArmorModuleItem.Type, Integer> counts = new EnumMap<>(AbyssArmorModuleItem.Type.class);
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.getItem() instanceof SingularityAbyssArmorItem) {
                for (AbyssArmorModuleItem.Type module : SingularityAbyssArmorItem.modules(armor)) {
                    counts.merge(module, 1, Integer::sum);
                }
            }
        }

        int verdant = counts.getOrDefault(AbyssArmorModuleItem.Type.VERDANT, 0);
        if (verdant > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, Math.min(1, verdant - 1), true, false));
        }
        int cinder = counts.getOrDefault(AbyssArmorModuleItem.Type.CINDER, 0);
        if (cinder > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, true, false));
        }
        int frost = counts.getOrDefault(AbyssArmorModuleItem.Type.FROST, 0);
        if (frost > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60,
                    Math.min(1, frost - 1), true, false));
        }
        int flesh = counts.getOrDefault(AbyssArmorModuleItem.Type.FLESH, 0);
        if (flesh > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 60,
                    Math.min(3, flesh - 1), true, false));
        }
        int voidModules = counts.getOrDefault(AbyssArmorModuleItem.Type.VOID, 0);
        if (voidModules > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60,
                    Math.min(2, voidModules - 1), true, false));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 60,
                    Math.min(1, voidModules - 1), true, false));
            if (!player.onGround()) {
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, true, false));
            }
        }
    }

    private static boolean holdsAbyssFocus(Player player) {
        return player.getMainHandItem().getItem() == ModItems.ABYSS_FOCUS.get()
                || player.getOffhandItem().getItem() == ModItems.ABYSS_FOCUS.get();
    }

    private static int countLayerWeapons(Player player) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof LayerWeaponItem) {
                count++;
            }
        }
        return count;
    }
}
