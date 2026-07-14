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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class FrostboundWardenEntity extends AbyssBossEntity {
    public FrostboundWardenEntity(EntityType<? extends FrostboundWardenEntity> type, Level level) {
        super(type, level, "entity.abyssworld.frostbound_warden", BossEvent.BossBarColor.BLUE, 200);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbyssBossEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 190.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.26D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.65D)
                .add(Attributes.ARMOR, 9.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (random.nextInt(4) == 0) {
                level().addParticle(ParticleTypes.SNOWFLAKE,
                        getX() + (random.nextDouble() - 0.5D) * 1.2D,
                        getY() + random.nextDouble() * 2.5D,
                        getZ() + (random.nextDouble() - 0.5D) * 1.2D,
                        0.0D, 0.02D, 0.0D);
            }
            return;
        }

        int interval = phase() >= 3 ? 35 : 60;
        double radius = phase() >= 2 ? 11.0D : 8.0D;
        if (tickCount % interval == 0) {
            List<Player> players = level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(radius),
                    player -> player.isAlive() && !player.isCreative() && !player.isSpectator());
            for (Player player : players) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 140, phase() >= 3 ? 3 : 1));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 140, phase() >= 2 ? 1 : 0));
            }
            if (!players.isEmpty() && level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                        getX(), getY() + 1.0D, getZ(), 24, 1.2D, 0.8D, 1.2D, 0.02D);
            }
        }
    }

    @Override
    protected void onPhaseChanged(int newPhase) {
        super.onPhaseChanged(newPhase);
        addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 180, 0));
        spawnMinions(newPhase >= 3 ? ModEntities.FROST_MARAUDER.get() : ModEntities.GLACIAL_WRAITH.get(),
                newPhase >= 3 ? 2 : 5, 8.0D);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean hit = super.doHurtTarget(entity);
        if (hit && entity instanceof LivingEntity target) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 160, 2));
        }
        return hit;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        spawnAtLocation(new ItemStack(ModItems.PERMAFROST_CORE.get()));
        spawnAtLocation(new ItemStack(ModItems.UNMELTING_ICE_CRYSTAL.get(), 4 + looting));
        playSound(SoundEvents.GLASS_BREAK, 1.2F, 0.6F);
    }

    @Override
    public boolean isSunBurnTick() {
        return false;
    }
}
