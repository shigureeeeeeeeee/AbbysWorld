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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VoidArchonEntity extends AbyssBossEntity {
    public VoidArchonEntity(EntityType<? extends VoidArchonEntity> type, Level level) {
        super(type, level, "entity.abyssworld.void_archon", BossEvent.BossBarColor.PURPLE, 240);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbyssBossEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 210.0D)
                .add(Attributes.ATTACK_DAMAGE, 13.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7D)
                .add(Attributes.ARMOR, 7.0D)
                .add(Attributes.FOLLOW_RANGE, 56.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (random.nextInt(3) == 0) {
                level().addParticle(ParticleTypes.PORTAL,
                        getX() + (random.nextDouble() - 0.5D) * 1.6D,
                        getY() + random.nextDouble() * 3.0D,
                        getZ() + (random.nextDouble() - 0.5D) * 1.6D,
                        (random.nextDouble() - 0.5D) * 0.1D,
                        0.05D,
                        (random.nextDouble() - 0.5D) * 0.1D);
            }
            return;
        }

        LivingEntity target = getTarget();
        int interval = phase() >= 3 ? 45 : 80;
        double range = phase() >= 2 ? 324.0D : 196.0D;
        if (target != null && target.isAlive() && tickCount % interval == 0 && distanceToSqr(target) < range) {
            target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, phase() >= 3 ? 70 : 45, 0));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 140, phase() >= 2 ? 2 : 1));
            if (level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                        target.getX(), target.getY() + 1.0D, target.getZ(), 32, 0.7D, 0.8D, 0.7D, 0.05D);
            }
        }
    }

    @Override
    protected void onPhaseChanged(int newPhase) {
        super.onPhaseChanged(newPhase);
        addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0));
        spawnMinions(newPhase >= 3 ? ModEntities.VOID_REAPER.get() : ModEntities.VOID_SHADE.get(),
                newPhase >= 3 ? 2 : 5, 9.0D);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        spawnAtLocation(new ItemStack(ModItems.VOID_STABILIZER.get()));
        spawnAtLocation(new ItemStack(ModItems.WORLD_LAW_FRAGMENT.get(), 1 + looting));
        playSound(SoundEvents.ENDERMAN_DEATH, 1.2F, 0.6F);
    }
}
