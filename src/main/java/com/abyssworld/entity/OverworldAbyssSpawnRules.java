package com.abyssworld.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public final class OverworldAbyssSpawnRules {
    private OverworldAbyssSpawnRules() {
    }

    public static boolean surface(EntityType<? extends Monster> type, ServerLevelAccessor level,
                                  MobSpawnType reason, BlockPos pos, RandomSource random) {
        return isOverworld(level) && pos.getY() >= 48
                && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }

    public static boolean anyDepth(EntityType<? extends Monster> type, ServerLevelAccessor level,
                                   MobSpawnType reason, BlockPos pos, RandomSource random) {
        return isOverworld(level) && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }

    public static boolean underground(EntityType<? extends Monster> type, ServerLevelAccessor level,
                                      MobSpawnType reason, BlockPos pos, RandomSource random) {
        return isOverworld(level) && pos.getY() <= 40
                && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }

    public static boolean deep(EntityType<? extends Monster> type, ServerLevelAccessor level,
                               MobSpawnType reason, BlockPos pos, RandomSource random) {
        return isOverworld(level) && pos.getY() <= 0
                && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }

    public static boolean watcher(EntityType<? extends Monster> type, ServerLevelAccessor level,
                                  MobSpawnType reason, BlockPos pos, RandomSource random) {
        return isOverworld(level) && pos.getY() <= -32
                && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }

    private static boolean isOverworld(ServerLevelAccessor level) {
        return level.getLevel().dimension().equals(Level.OVERWORLD);
    }
}
