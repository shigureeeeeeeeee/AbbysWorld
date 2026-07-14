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

public class FleshColossusEntity extends AbyssBossEntity {
    public FleshColossusEntity(EntityType<? extends FleshColossusEntity> type, Level level) {
        super(type, level, "entity.abyssworld.flesh_colossus", BossEvent.BossBarColor.PINK, 230);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbyssBossEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 260.0D)
                .add(Attributes.ATTACK_DAMAGE, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.24D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.6D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (random.nextInt(5) == 0) {
                level().addParticle(ParticleTypes.DAMAGE_INDICATOR,
                        getX() + (random.nextDouble() - 0.5D) * 1.8D,
                        getY() + random.nextDouble() * 1.8D,
                        getZ() + (random.nextDouble() - 0.5D) * 1.8D,
                        0.0D, 0.02D, 0.0D);
            }
            return;
        }

        int interval = phase() >= 3 ? 45 : 100;
        if (tickCount % interval == 0 && getHealth() < getMaxHealth()) {
            heal(phase() >= 2 ? 10.0F : 6.0F);
            if (level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HEART,
                        getX(), getY() + 1.0D, getZ(), 6, 0.8D, 0.6D, 0.8D, 0.02D);
            }
        }
    }

    @Override
    protected void onPhaseChanged(int newPhase) {
        super.onPhaseChanged(newPhase);
        addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 220, newPhase >= 3 ? 1 : 0));
        spawnMinions(newPhase >= 3 ? ModEntities.FLESH_HUNTER.get() : ModEntities.MARROW_CRAWLER.get(),
                newPhase >= 3 ? 2 : 5, 8.0D);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean hit = super.doHurtTarget(entity);
        if (hit && entity instanceof LivingEntity target) {
            heal(5.0F);
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
        }
        return hit;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        spawnAtLocation(new ItemStack(ModItems.PRIMORDIAL_NERVE_BUNDLE.get()));
        spawnAtLocation(new ItemStack(ModItems.UNDYING_CELL.get(), 3 + looting));
        playSound(SoundEvents.ZOGLIN_DEATH, 1.2F, 0.7F);
    }
}
