package com.abyssworld.event;

import com.abyssworld.AbyssWorld;
import com.abyssworld.entity.AbyssSovereignEntity;
import com.abyssworld.entity.AshRevenantEntity;
import com.abyssworld.entity.AshKingEntity;
import com.abyssworld.entity.CinderImpEntity;
import com.abyssworld.entity.FleshColossusEntity;
import com.abyssworld.entity.FleshHunterEntity;
import com.abyssworld.entity.ForestStalkerEntity;
import com.abyssworld.entity.FrostboundWardenEntity;
import com.abyssworld.entity.FrostMarauderEntity;
import com.abyssworld.entity.GlacialWraithEntity;
import com.abyssworld.entity.GroveSentinelEntity;
import com.abyssworld.entity.MarrowCrawlerEntity;
import com.abyssworld.entity.RootboundThrallEntity;
import com.abyssworld.entity.RottenForestGuardianEntity;
import com.abyssworld.entity.VoidArchonEntity;
import com.abyssworld.entity.VoidReaperEntity;
import com.abyssworld.entity.VoidShadeEntity;
import com.abyssworld.entity.AbyssHoundEntity;
import com.abyssworld.entity.ShadowWalkerEntity;
import com.abyssworld.entity.ManaLeechEntity;
import com.abyssworld.entity.CrystalParasiteEntity;
import com.abyssworld.entity.FallenResearcherEntity;
import com.abyssworld.entity.BoundaryWatcherEntity;
import com.abyssworld.entity.OverworldAbyssSpawnRules;
import com.abyssworld.registry.ModEntities;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AbyssWorld.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.ABYSS_HOUND.get(), AbyssHoundEntity.createAttributes().build());
        event.put(ModEntities.SHADOW_WALKER.get(), ShadowWalkerEntity.createAttributes().build());
        event.put(ModEntities.MANA_LEECH.get(), ManaLeechEntity.createAttributes().build());
        event.put(ModEntities.CRYSTAL_PARASITE.get(), CrystalParasiteEntity.createAttributes().build());
        event.put(ModEntities.FALLEN_RESEARCHER.get(), FallenResearcherEntity.createAttributes().build());
        event.put(ModEntities.BOUNDARY_WATCHER.get(), BoundaryWatcherEntity.createAttributes().build());
        event.put(ModEntities.ABYSS_SOVEREIGN.get(), AbyssSovereignEntity.createAttributes().build());
        event.put(ModEntities.ROTTEN_FOREST_GUARDIAN.get(), RottenForestGuardianEntity.createAttributes().build());
        event.put(ModEntities.GROVE_SENTINEL.get(), GroveSentinelEntity.createAttributes().build());
        event.put(ModEntities.ASH_KING.get(), AshKingEntity.createAttributes().build());
        event.put(ModEntities.FROSTBOUND_WARDEN.get(), FrostboundWardenEntity.createAttributes().build());
        event.put(ModEntities.FLESH_COLOSSUS.get(), FleshColossusEntity.createAttributes().build());
        event.put(ModEntities.VOID_ARCHON.get(), VoidArchonEntity.createAttributes().build());
        event.put(ModEntities.FOREST_STALKER.get(), ForestStalkerEntity.createAttributes().build());
        event.put(ModEntities.ASH_REVENANT.get(), AshRevenantEntity.createAttributes().build());
        event.put(ModEntities.FROST_MARAUDER.get(), FrostMarauderEntity.createAttributes().build());
        event.put(ModEntities.FLESH_HUNTER.get(), FleshHunterEntity.createAttributes().build());
        event.put(ModEntities.VOID_REAPER.get(), VoidReaperEntity.createAttributes().build());
        event.put(ModEntities.ROOTBOUND_THRALL.get(), RootboundThrallEntity.createAttributes().build());
        event.put(ModEntities.CINDER_IMP.get(), CinderImpEntity.createAttributes().build());
        event.put(ModEntities.GLACIAL_WRAITH.get(), GlacialWraithEntity.createAttributes().build());
        event.put(ModEntities.MARROW_CRAWLER.get(), MarrowCrawlerEntity.createAttributes().build());
        event.put(ModEntities.VOID_SHADE.get(), VoidShadeEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(ModEntities.ABYSS_HOUND.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, OverworldAbyssSpawnRules::surface,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.SHADOW_WALKER.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, OverworldAbyssSpawnRules::anyDepth,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.MANA_LEECH.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, OverworldAbyssSpawnRules::underground,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.CRYSTAL_PARASITE.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, OverworldAbyssSpawnRules::deep,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.FALLEN_RESEARCHER.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, OverworldAbyssSpawnRules::underground,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.BOUNDARY_WATCHER.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, OverworldAbyssSpawnRules::watcher,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.FOREST_STALKER.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.ASH_REVENANT.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.FROST_MARAUDER.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.FLESH_HUNTER.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.VOID_REAPER.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.ROOTBOUND_THRALL.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.CINDER_IMP.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.GLACIAL_WRAITH.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.MARROW_CRAWLER.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.VOID_SHADE.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
    }
}
