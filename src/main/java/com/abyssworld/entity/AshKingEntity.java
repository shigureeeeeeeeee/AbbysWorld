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
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class AshKingEntity extends AbyssBossEntity {
    public AshKingEntity(EntityType<? extends AshKingEntity> type, Level level) {
        super(type, level, "entity.abyssworld.ash_king", BossEvent.BossBarColor.RED, 220);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbyssBossEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 220.0D)
                .add(Attributes.ATTACK_DAMAGE, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.FLYING_SPEED, 0.70D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (random.nextInt(3) == 0) {
                level().addParticle(ParticleTypes.FLAME,
                        getX() + (random.nextDouble() - 0.5D) * 1.5D,
                        getY() + random.nextDouble() * 2.4D,
                        getZ() + (random.nextDouble() - 0.5D) * 1.5D,
                        0.0D, 0.03D, 0.0D);
            }
            return;
        }

        int interval = phase() >= 3 ? 35 : 60;
        double radius = phase() >= 2 ? 8.0D : 5.0D;
        if (tickCount % interval == 0) {
            List<Player> players = level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(radius),
                    player -> player.isAlive() && !player.isCreative() && !player.isSpectator());
            for (Player player : players) {
                player.setSecondsOnFire(phase() >= 3 ? 8 : 5);
                player.hurt(damageSources().mobAttack(this), phase() >= 2 ? 7.0F : 5.0F);
            }
            if (!players.isEmpty() && level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.LAVA,
                        getX(), getY() + 1.0D, getZ(), 16, 1.0D, 0.5D, 1.0D, 0.1D);
            }
        }
    }

    @Override
    protected void onPhaseChanged(int newPhase) {
        super.onPhaseChanged(newPhase);
        addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0));
        spawnMinions(newPhase >= 3 ? ModEntities.ASH_REVENANT.get() : ModEntities.CINDER_IMP.get(),
                newPhase >= 3 ? 2 : 6, 8.0D);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        spawnAtLocation(new ItemStack(ModItems.ETERNAL_FURNACE_CORE.get()));
        spawnAtLocation(new ItemStack(ModItems.ETERNAL_FLAME.get(), 4 + looting));
        playSound(SoundEvents.BLAZE_DEATH, 1.2F, 0.5F);
    }
}
