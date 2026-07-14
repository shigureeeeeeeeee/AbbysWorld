package com.abyssworld.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

public abstract class AbyssBossEntity extends AbyssMonsterEntity {
    private final ServerBossEvent bossEvent;
    private int phase = 1;

    protected AbyssBossEntity(EntityType<? extends Monster> type, Level level, String translationKey,
                              BossEvent.BossBarColor barColor, int xpReward) {
        super(type, level);
        this.bossEvent = new ServerBossEvent(
                Component.translatable(translationKey),
                barColor,
                BossEvent.BossBarOverlay.NOTCHED_10);
        this.xpReward = xpReward;
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbyssMonsterEntity.createAttributes()
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            bossEvent.setProgress(getHealth() / getMaxHealth());
            updatePhase();
        }
    }

    protected int phase() {
        return phase;
    }

    protected void onPhaseChanged(int newPhase) {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                    getX(), getY() + 1.0D, getZ(), 12, 1.0D, 0.6D, 1.0D, 0.02D);
            serverLevel.playSound(null, blockPosition(), SoundEvents.WITHER_AMBIENT,
                    SoundSource.HOSTILE, 1.2F, 0.55F + newPhase * 0.1F);
        }
    }

    protected void spawnMinions(EntityType<? extends Mob> type, int count, double radius) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0D;
            BlockPos pos = blockPosition().offset(
                    (int) (Math.cos(angle) * radius), 0, (int) (Math.sin(angle) * radius));
            pos = serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
            Mob mob = type.create(serverLevel);
            if (mob == null) {
                continue;
            }
            mob.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                    random.nextFloat() * 360.0F, 0.0F);
            mob.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED,
                    null, null);
            mob.setTarget(getTarget());
            mob.setPersistenceRequired();
            serverLevel.addFreshEntity(mob);
        }
    }

    private void updatePhase() {
        float ratio = getHealth() / getMaxHealth();
        int nextPhase = ratio <= 0.25F ? 3 : ratio <= 0.60F ? 2 : 1;
        if (nextPhase > phase) {
            phase = nextPhase;
            onPhaseChanged(phase);
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    @Override
    public void remove(RemovalReason reason) {
        bossEvent.removeAllPlayers();
        super.remove(reason);
    }
}
