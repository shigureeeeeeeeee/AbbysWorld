package com.abyssworld.entity;

import com.abyssworld.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VoidShadeEntity extends AbyssMonsterEntity {
    public VoidShadeEntity(EntityType<? extends VoidShadeEntity> type, Level level) {
        super(type, level);
        this.xpReward = 12;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbyssMonsterEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 34.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.ARMOR, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 42.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && random.nextInt(5) == 0) {
            level().addParticle(ParticleTypes.PORTAL,
                    getX() + (random.nextDouble() - 0.5D),
                    getY() + random.nextDouble() * 2.4D,
                    getZ() + (random.nextDouble() - 0.5D),
                    0.0D, 0.03D, 0.0D);
        }
        if (!level().isClientSide && getTarget() != null && tickCount % 120 == 0) {
            LivingEntity target = getTarget();
            if (distanceToSqr(target) < 100.0D) {
                target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 80, 0));
            }
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        spawnAtLocation(new ItemStack(ModItems.SPATIAL_ANCHOR_CRYSTAL.get(), 1 + random.nextInt(1 + looting + 1)));
    }
}
