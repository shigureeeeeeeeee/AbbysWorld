package com.abyssworld.entity;

import com.abyssworld.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AshRevenantEntity extends AbyssMonsterEntity {
    public AshRevenantEntity(EntityType<? extends AshRevenantEntity> type, Level level) {
        super(type, level);
        this.xpReward = 22;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbyssMonsterEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 48.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.29D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.35D)
                .add(Attributes.ARMOR, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && random.nextInt(5) == 0) {
            level().addParticle(ParticleTypes.SMOKE,
                    getX() + (random.nextDouble() - 0.5D),
                    getY() + random.nextDouble() * 2.6D,
                    getZ() + (random.nextDouble() - 0.5D),
                    0.0D, 0.03D, 0.0D);
        }
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean hit = super.doHurtTarget(entity);
        if (hit) {
            entity.setSecondsOnFire(5);
        }
        return hit;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        spawnAtLocation(new ItemStack(ModItems.CINDER_HEART.get(), 1 + random.nextInt(1 + looting + 1)));
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean isSunBurnTick() {
        return false;
    }
}
