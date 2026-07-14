package com.abyssworld.entity;

import com.abyssworld.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.phys.Vec3;

public class ShadowWalkerEntity extends AbyssMonsterEntity {
    private int teleportCooldown = 60;

    public ShadowWalkerEntity(EntityType<? extends ShadowWalkerEntity> type, Level level) {
        super(type, level);
        xpReward = 24;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbyssMonsterEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 38.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.ARMOR, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (random.nextInt(4) == 0) {
                level().addParticle(ParticleTypes.REVERSE_PORTAL, getRandomX(0.7D),
                        getY() + random.nextDouble() * getBbHeight(), getRandomZ(0.7D),
                        0.0D, 0.01D, 0.0D);
            }
            return;
        }
        if (teleportCooldown > 0) teleportCooldown--;
        LivingEntity target = getTarget();
        if (target != null && target.isAlive() && teleportCooldown <= 0
                && distanceToSqr(target) > 16.0D && distanceToSqr(target) < 400.0D
                && level().getMaxLocalRawBrightness(blockPosition()) < 9) {
            Vec3 destination = target.position().subtract(target.getLookAngle().scale(2.2D));
            double dx = destination.x - getX();
            double dy = destination.y - getY();
            double dz = destination.z - getZ();
            if (level().noCollision(this, getBoundingBox().move(dx, dy, dz))) {
                if (level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.PORTAL, getX(), getY() + 1.0D, getZ(),
                            24, 0.4D, 0.8D, 0.4D, 0.04D);
                }
                teleportTo(destination.x, destination.y, destination.z);
                teleportCooldown = 90;
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean hit = super.doHurtTarget(entity);
        if (hit && entity instanceof LivingEntity target) {
            target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 50, 0));
        }
        return hit;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        if (random.nextFloat() < 0.25F + looting * 0.08F) {
            spawnAtLocation(new ItemStack(ModItems.ABYSS_CRYSTAL.get()));
        }
    }
}
