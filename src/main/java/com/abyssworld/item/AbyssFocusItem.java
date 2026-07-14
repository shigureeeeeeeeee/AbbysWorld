package com.abyssworld.item;

import com.abyssworld.magic.AbyssMagic;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class AbyssFocusItem extends Item {
    private static final int COST = 35;
    private static final int STRAIN = 8;

    public AbyssFocusItem() {
        super(new Properties().rarity(Rarity.RARE).stacksTo(1).fireResistant());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        if (!AbyssMagic.consumeMana(player, COST)) {
            AbyssMagic.addStrain(player, 4);
            player.displayClientMessage(Component.translatable("item.abyssworld.abyss_focus.no_mana")
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        AbyssMagic.addStrain(player, STRAIN);
        AbyssMagic.Aspect aspect = AbyssMagic.aspectFor(level.dimension());
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(7.0D),
                entity -> entity != player && entity.isAlive() && !entity.isAlliedTo(player));
        cast((ServerLevel) level, player, targets, aspect);
        player.getCooldowns().addCooldown(this, 100);
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    private static void cast(ServerLevel level, Player player, List<LivingEntity> targets, AbyssMagic.Aspect aspect) {
        ParticleOptions particle = switch (aspect) {
            case FOREST -> ParticleTypes.HAPPY_VILLAGER;
            case ASH -> ParticleTypes.FLAME;
            case FROST -> ParticleTypes.SNOWFLAKE;
            case FLESH -> ParticleTypes.DAMAGE_INDICATOR;
            case VOID -> ParticleTypes.REVERSE_PORTAL;
            case ABYSS -> ParticleTypes.WITCH;
        };
        level.sendParticles(particle, player.getX(), player.getY() + 1.0D, player.getZ(),
                48, 3.0D, 1.0D, 3.0D, 0.05D);
        level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE,
                SoundSource.PLAYERS, 1.0F, 0.6F);

        int hits = 0;
        for (LivingEntity target : targets) {
            if (target.distanceToSqr(player) > 49.0D) {
                continue;
            }
            hits++;
            switch (aspect) {
                case FOREST -> target.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 1));
                case ASH -> {
                    target.setSecondsOnFire(7);
                    target.hurt(player.damageSources().onFire(), 5.0F);
                }
                case FROST -> {
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 140, 3));
                    target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 1));
                }
                case FLESH -> {
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 140, 2));
                    player.heal(1.5F);
                }
                case VOID -> {
                    target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 45, 0));
                    target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 120, 0));
                }
                case ABYSS -> {
                    target.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 0));
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
                }
            }
        }
        if (aspect == AbyssMagic.Aspect.FOREST) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 120, hits > 2 ? 1 : 0));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.abyssworld.abyss_focus.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.abyssworld.abyss_focus.cost", COST, STRAIN)
                .withStyle(ChatFormatting.DARK_PURPLE));
    }
}
