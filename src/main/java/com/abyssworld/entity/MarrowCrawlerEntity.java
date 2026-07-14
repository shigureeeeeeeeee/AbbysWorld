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

public class MarrowCrawlerEntity extends AbyssMonsterEntity {
    public MarrowCrawlerEntity(EntityType<? extends MarrowCrawlerEntity> type, Level level) {
        super(type, level);
        this.xpReward = 12;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbyssMonsterEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 36.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.27D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && random.nextInt(8) == 0) {
            level().addParticle(ParticleTypes.DAMAGE_INDICATOR,
                    getX() + (random.nextDouble() - 0.5D),
                    getY() + random.nextDouble() * 1.8D,
                    getZ() + (random.nextDouble() - 0.5D),
                    0.0D, 0.02D, 0.0D);
        }
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean hit = super.doHurtTarget(entity);
        if (hit && entity instanceof LivingEntity target) {
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0));
            heal(1.0F);
        }
        return hit;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        spawnAtLocation(new ItemStack(ModItems.PRIMORDIAL_NERVE.get(), 1 + random.nextInt(1 + looting + 1)));
    }

    @Override
    public boolean isSunBurnTick() {
        return false;
    }
}
