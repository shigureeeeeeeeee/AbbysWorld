package com.abyssworld.entity;

import com.abyssworld.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
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

public class FleshHunterEntity extends AbyssMonsterEntity {
    public FleshHunterEntity(EntityType<? extends FleshHunterEntity> type, Level level) {
        super(type, level);
        this.xpReward = 24;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbyssMonsterEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 58.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.1D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.45D)
                .add(Attributes.ARMOR, 7.0D)
                .add(Attributes.FOLLOW_RANGE, 36.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && random.nextInt(7) == 0) {
            level().addParticle(ParticleTypes.DAMAGE_INDICATOR,
                    getX() + (random.nextDouble() - 0.5D),
                    getY() + random.nextDouble() * 1.6D,
                    getZ() + (random.nextDouble() - 0.5D),
                    0.0D, 0.02D, 0.0D);
        }
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean hit = super.doHurtTarget(entity);
        if (hit && entity instanceof LivingEntity target) {
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
            heal(2.0F);
        }
        return hit;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        spawnAtLocation(new ItemStack(ModItems.LIVING_SINEW.get(), 1 + random.nextInt(1 + looting + 1)));
    }
}
