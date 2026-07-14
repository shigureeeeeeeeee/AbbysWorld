package com.abyssworld.item;

import com.abyssworld.magic.AbyssMagic;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class LayerWeaponItem extends SwordItem {
    private static final int EFFECT_MANA_COST = 6;
    private static final int EFFECT_STRAIN = 2;

    private final String effectKey;
    private final Effect effect;

    public enum Effect {
        FOREST,
        ASH,
        FROST,
        FLESH,
        VOID
    }

    public LayerWeaponItem(Tier tier, int damage, float speed, Properties properties,
                           String effectKey, Effect effect) {
        super(tier, damage, speed, properties);
        this.effectKey = effectKey;
        this.effect = effect;
    }

    public Effect effect() {
        return effect;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!target.level().isClientSide) {
            if (attacker instanceof Player player) {
                if (!AbyssMagic.consumeMana(player, EFFECT_MANA_COST)) {
                    AbyssMagic.addStrain(player, 3);
                    player.hurt(player.damageSources().magic(), 1.0F);
                    player.displayClientMessage(Component.translatable("item.abyssworld.layer_weapon.backlash")
                            .withStyle(ChatFormatting.RED), true);
                    return super.hurtEnemy(stack, target, attacker);
                }
                AbyssMagic.addStrain(player, EFFECT_STRAIN);
            }
            switch (effect) {
                case FOREST -> {
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 0));
                    if (attacker instanceof Player player) {
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0));
                    }
                }
                case ASH -> {
                    target.setSecondsOnFire(6);
                    target.hurt(target.damageSources().onFire(), 3.0F);
                }
                case FROST -> {
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 2));
                    target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 80, 0));
                }
                case FLESH -> {
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
                    attacker.heal(2.0F);
                }
                case VOID -> {
                    target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 35, 0));
                    target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 80, 0));
                }
            }
            target.level().playSound(null, target.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE,
                    SoundSource.PLAYERS, 0.35F, 1.3F);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(effectKey).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.abyssworld.layer_weapon.cost",
                EFFECT_MANA_COST, EFFECT_STRAIN).withStyle(ChatFormatting.DARK_PURPLE));
    }
}
