package com.abyssworld.entity;

import com.abyssworld.registry.ModEntities;
import com.abyssworld.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FallenResearcherEntity extends AbyssMonsterEntity implements RangedAttackMob {
    private int casts;

    public FallenResearcherEntity(EntityType<? extends FallenResearcherEntity> type, Level level) {
        super(type, level);
        xpReward = 32;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbyssMonsterEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 58.0D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.27D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0D, 45, 24.0F));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (!(level() instanceof ServerLevel serverLevel) || !getSensing().hasLineOfSight(target)) return;
        swing(InteractionHand.MAIN_HAND);
        casts++;
        Vec3 start = position().add(0.0D, 1.5D, 0.0D);
        Vec3 end = target.getEyePosition();
        for (int i = 0; i <= 18; i++) {
            Vec3 point = start.lerp(end, i / 18.0D);
            serverLevel.sendParticles(ParticleTypes.WITCH, point.x, point.y, point.z,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        target.hurt(damageSources().mobAttack(this), 7.0F);
        target.addEffect(new MobEffectInstance(casts % 2 == 0 ? MobEffects.WEAKNESS : MobEffects.CONFUSION,
                100, casts % 3 == 0 ? 1 : 0));
        if (casts % 4 == 0 && serverLevel.getEntitiesOfClass(ManaLeechEntity.class,
                getBoundingBox().inflate(10.0D)).size() < 2) {
            ManaLeechEntity leech = ModEntities.MANA_LEECH.get().create(serverLevel);
            if (leech != null) {
                leech.moveTo(getX() + random.nextInt(3) - 1, getY(), getZ() + random.nextInt(3) - 1,
                        random.nextFloat() * 360.0F, 0.0F);
                leech.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(blockPosition()),
                        MobSpawnType.MOB_SUMMONED, null, null);
                leech.setTarget(target);
                serverLevel.addFreshEntity(leech);
            }
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        if (random.nextFloat() < 0.20F + looting * 0.06F) {
            ItemStack glyph = new ItemStack(random.nextBoolean()
                    ? ModItems.GLYPH_EFFECT_PULL.get() : ModItems.GLYPH_AUGMENT_RANGE.get());
            spawnAtLocation(glyph);
        }
    }
}
