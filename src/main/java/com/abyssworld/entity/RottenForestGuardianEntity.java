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

public class RottenForestGuardianEntity extends AbyssBossEntity {
    public RottenForestGuardianEntity(EntityType<? extends RottenForestGuardianEntity> type, Level level) {
        super(type, level, "entity.abyssworld.rotten_forest_guardian", BossEvent.BossBarColor.GREEN, 180);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbyssBossEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 180.0D)
                .add(Attributes.ATTACK_DAMAGE, 11.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (random.nextInt(5) == 0) {
                level().addParticle(ParticleTypes.COMPOSTER,
                        getX() + (random.nextDouble() - 0.5D) * 1.4D,
                        getY() + random.nextDouble() * 2.8D,
                        getZ() + (random.nextDouble() - 0.5D) * 1.4D,
                        0.0D, 0.03D, 0.0D);
            }
            return;
        }

        int interval = phase() >= 3 ? 45 : 80;
        if (tickCount % interval == 0 && getHealth() < getMaxHealth()) {
            heal(phase() >= 2 ? 7.0F : 4.0F);
            if (level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        getX(), getY() + 1.0D, getZ(), 12, 0.8D, 0.8D, 0.8D, 0.02D);
            }
        }
    }

    @Override
    protected void onPhaseChanged(int newPhase) {
        super.onPhaseChanged(newPhase);
        addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, newPhase - 2));
        spawnMinions(newPhase >= 3 ? ModEntities.FOREST_STALKER.get() : ModEntities.ROOTBOUND_THRALL.get(),
                newPhase >= 3 ? 3 : 5, 7.0D);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean hit = super.doHurtTarget(entity);
        if (hit && entity instanceof LivingEntity target) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 0));
        }
        return hit;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        spawnAtLocation(new ItemStack(ModItems.ROTTEN_FOREST_CORE.get()));
        spawnAtLocation(new ItemStack(ModItems.PRIMORDIAL_SAP.get(), 4 + looting));
        playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 1.2F, 0.7F);
    }

    @Override
    public boolean isSunBurnTick() {
        return false;
    }
}
