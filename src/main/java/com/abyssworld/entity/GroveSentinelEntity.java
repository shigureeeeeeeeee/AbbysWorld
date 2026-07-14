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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GroveSentinelEntity extends AbyssBossEntity {
    public GroveSentinelEntity(EntityType<? extends GroveSentinelEntity> type, Level level) {
        super(type, level, "entity.abyssworld.grove_sentinel", BossEvent.BossBarColor.GREEN, 80);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbyssBossEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 110.0D)
                .add(Attributes.ATTACK_DAMAGE, 9.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.29D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.55D)
                .add(Attributes.ARMOR, 7.0D)
                .add(Attributes.FOLLOW_RANGE, 36.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && random.nextInt(4) == 0) {
            level().addParticle(ParticleTypes.FALLING_SPORE_BLOSSOM,
                    getX() + (random.nextDouble() - 0.5D) * 1.2D,
                    getY() + random.nextDouble() * 2.4D,
                    getZ() + (random.nextDouble() - 0.5D) * 1.2D,
                    0.0D, -0.01D, 0.0D);
        }
    }

    @Override
    protected void onPhaseChanged(int newPhase) {
        super.onPhaseChanged(newPhase);
        addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 160, newPhase - 2));
        addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 160, 0));
        spawnMinions(ModEntities.ROOTBOUND_THRALL.get(), newPhase >= 3 ? 3 : 2, 5.0D);
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR,
                    getX(), getY() + 1.0D, getZ(), 40, 1.6D, 1.0D, 1.6D, 0.02D);
        }
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean hit = super.doHurtTarget(entity);
        if (hit && entity instanceof LivingEntity target) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, phase() >= 3 ? 2 : 1));
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 0));
        }
        return hit;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        spawnAtLocation(new ItemStack(ModItems.GROVE_HEART_KEY.get()));
        spawnAtLocation(new ItemStack(ModItems.PRIMORDIAL_SAP.get(), 2 + looting));
        playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 1.1F, 0.8F);
    }

    @Override
    public boolean isSunBurnTick() {
        return false;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }
}
