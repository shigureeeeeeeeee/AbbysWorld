package com.abyssworld.entity;

import com.abyssworld.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AbyssHoundEntity extends AbyssMonsterEntity {
    public AbyssHoundEntity(EntityType<? extends AbyssHoundEntity> type, Level level) {
        super(type, level);
        xpReward = 18;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbyssMonsterEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 42.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new LeapAtTargetGoal(this, 0.45F));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && tickCount % 40 == 0) {
            int packSize = level().getEntitiesOfClass(AbyssHoundEntity.class,
                    getBoundingBox().inflate(10.0D), hound -> hound.isAlive()).size();
            if (packSize >= 3) {
                addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 1, true, false));
                addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 0, true, false));
            }
        } else if (level().isClientSide) {
            if (random.nextInt(5) == 0) {
                level().addParticle(ParticleTypes.CRIMSON_SPORE, getRandomX(0.65D), getY() + 0.85D,
                        getRandomZ(0.65D), 0.0D, 0.008D, 0.0D);
            }
            if ((!onGround() || isAggressive()) && random.nextInt(7) == 0) {
                level().addParticle(ParticleTypes.SMOKE, getRandomX(0.8D), getY() + 0.25D,
                        getRandomZ(0.8D), 0.0D, 0.012D, 0.0D);
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            playSound(SoundEvents.RAVAGER_ATTACK, 0.85F, 1.25F + random.nextFloat() * 0.12F);
        }
        if (hit && level().getEntitiesOfClass(AbyssHoundEntity.class,
                getBoundingBox().inflate(8.0D), hound -> hound.isAlive()).size() >= 3
                && target instanceof net.minecraft.world.entity.LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0));
        }
        return hit;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isAggressive() ? SoundEvents.WOLF_GROWL : SoundEvents.RAVAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        playSound(SoundEvents.WOLF_STEP, 0.3F, 0.68F + random.nextFloat() * 0.08F);
    }

    @Override
    protected float getSoundVolume() {
        return 0.9F;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        if (random.nextFloat() < 0.35F + looting * 0.08F) {
            spawnAtLocation(new ItemStack(ModItems.RAW_ABYSS_IRON.get()));
        }
    }
}
