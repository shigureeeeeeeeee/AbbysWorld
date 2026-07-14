package com.abyssworld.entity;

import com.abyssworld.magic.AbyssMagic;
import com.abyssworld.registry.ModEntities;
import com.abyssworld.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BoundaryWatcherEntity extends AbyssBossEntity {
    public BoundaryWatcherEntity(EntityType<? extends BoundaryWatcherEntity> type, Level level) {
        super(type, level, "entity.abyssworld.boundary_watcher", BossEvent.BossBarColor.PURPLE, 260);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbyssBossEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 240.0D)
                .add(Attributes.ATTACK_DAMAGE, 14.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.29D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.85D)
                .add(Attributes.FOLLOW_RANGE, 56.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (random.nextInt(3) == 0) {
                level().addParticle(ParticleTypes.ENCHANT, getRandomX(1.3D),
                        getY() + random.nextDouble() * getBbHeight(), getRandomZ(1.3D),
                        0.0D, 0.03D, 0.0D);
            }
            return;
        }
        LivingEntity target = getTarget();
        int interval = phase() >= 3 ? 35 : phase() == 2 ? 50 : 70;
        if (target != null && target.isAlive() && tickCount % interval == 0
                && distanceToSqr(target) < 625.0D && getSensing().hasLineOfSight(target)) {
            swing(InteractionHand.MAIN_HAND);
            target.hurt(damageSources().mobAttack(this), phase() >= 3 ? 10.0F : 7.0F);
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, phase() >= 2 ? 1 : 0));
            if (target instanceof ServerPlayer player) {
                int drained = Math.min(25, AbyssMagic.mana(player));
                if (drained > 0) AbyssMagic.consumeMana(player, drained);
                AbyssMagic.addStrain(player, phase() >= 3 ? 12 : 8);
            }
            if (level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                        target.getX(), target.getY() + 1.0D, target.getZ(),
                        36, 0.8D, 0.8D, 0.8D, 0.06D);
            }
        }
    }

    @Override
    protected void onPhaseChanged(int newPhase) {
        super.onPhaseChanged(newPhase);
        addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, newPhase >= 3 ? 1 : 0));
        if (newPhase == 2) {
            spawnMinions(ModEntities.SHADOW_WALKER.get(), 3, 8.0D);
        } else {
            spawnMinions(ModEntities.MANA_LEECH.get(), 4, 8.0D);
            spawnMinions(ModEntities.CRYSTAL_PARASITE.get(), 2, 10.0D);
        }
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean hit = super.doHurtTarget(entity);
        if (hit && entity instanceof ServerPlayer player) {
            AbyssMagic.addStrain(player, 10);
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0));
        }
        return hit;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        spawnAtLocation(new ItemStack(ModItems.COMPRESSED_ABYSS_CRYSTAL.get(), 2 + looting));
        spawnAtLocation(new ItemStack(ModItems.HIGH_DENSITY_ABYSS_ALLOY.get(), 1 + looting));
        playSound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 1.5F, 0.55F);
    }
}
