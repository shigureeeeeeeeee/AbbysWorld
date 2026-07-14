package com.abyssworld.entity;

import com.abyssworld.registry.ModEntities;
import com.abyssworld.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 深淵覇王 - ラスボス。深淵神核をドロップする。
 */
public class AbyssSovereignEntity extends AbyssBossEntity {
    public AbyssSovereignEntity(EntityType<? extends AbyssSovereignEntity> type, Level level) {
        super(type, level, "entity.abyssworld.abyss_sovereign", BossEvent.BossBarColor.PURPLE, 500);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbyssBossEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 320.0D)
                .add(Attributes.ATTACK_DAMAGE, 14.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.85D)
                .add(Attributes.ARMOR, 10.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (random.nextInt(6) == 0) {
                level().addParticle(ParticleTypes.WITCH,
                        getX() + (random.nextDouble() - 0.5D) * 1.2D,
                        getY() + random.nextDouble() * 3.0D,
                        getZ() + (random.nextDouble() - 0.5D) * 1.2D,
                        0.0D, 0.02D, 0.0D);
            }
            return;
        }

        if (tickCount % (phase() >= 3 ? 45 : 80) == 0) {
            for (Player player : level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(phase() >= 3 ? 14.0D : 9.0D),
                    target -> target.isAlive() && !target.isCreative() && !target.isSpectator())) {
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, phase() >= 2 ? 2 : 0));
                player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0));
                if (phase() >= 3) {
                    player.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 0));
                }
            }
        }
    }

    @Override
    protected void onPhaseChanged(int newPhase) {
        super.onPhaseChanged(newPhase);
        addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 240, newPhase >= 3 ? 1 : 0));
        if (newPhase == 2) {
            spawnMinions(ModEntities.VOID_REAPER.get(), 2, 10.0D);
            spawnMinions(ModEntities.FROST_MARAUDER.get(), 2, 10.0D);
        } else {
            spawnMinions(ModEntities.ASH_REVENANT.get(), 2, 10.0D);
            spawnMinions(ModEntities.FLESH_HUNTER.get(), 2, 10.0D);
            spawnMinions(ModEntities.FOREST_STALKER.get(), 2, 10.0D);
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        this.spawnAtLocation(new ItemStack(ModItems.ABYSS_GOD_CORE.get()));
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                    getX(), getY() + 1.0D, getZ(), 2, 0.4D, 0.4D, 0.4D, 0.0D);
        }
        this.playSound(SoundEvents.WITHER_DEATH, 1.5F, 0.7F);
    }

    @Override
    public boolean isSunBurnTick() {
        return false;
    }
}
