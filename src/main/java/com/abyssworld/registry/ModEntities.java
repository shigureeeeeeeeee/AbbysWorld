package com.abyssworld.registry;

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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AbyssWorld.MODID);

    public static final RegistryObject<EntityType<AbyssSovereignEntity>> ABYSS_SOVEREIGN =
            ENTITIES.register("abyss_sovereign",
                    () -> EntityType.Builder.of(AbyssSovereignEntity::new, MobCategory.MONSTER)
                            .sized(1.4F, 3.8F)
                            .clientTrackingRange(10)
                            .build("abyss_sovereign"));

    public static final RegistryObject<EntityType<RottenForestGuardianEntity>> ROTTEN_FOREST_GUARDIAN =
            ENTITIES.register("rotten_forest_guardian",
                    () -> EntityType.Builder.of(RottenForestGuardianEntity::new, MobCategory.MONSTER)
                            .sized(0.8F, 2.3F)
                            .clientTrackingRange(10)
                            .build("rotten_forest_guardian"));

    public static final RegistryObject<EntityType<GroveSentinelEntity>> GROVE_SENTINEL =
            ENTITIES.register("grove_sentinel",
                    () -> EntityType.Builder.of(GroveSentinelEntity::new, MobCategory.MONSTER)
                            .sized(0.85F, 2.35F)
                            .clientTrackingRange(10)
                            .build("grove_sentinel"));

    public static final RegistryObject<EntityType<AshKingEntity>> ASH_KING =
            ENTITIES.register("ash_king",
                    () -> EntityType.Builder.of(AshKingEntity::new, MobCategory.MONSTER)
                            .fireImmune()
                            .sized(1.0F, 2.4F)
                            .clientTrackingRange(10)
                            .build("ash_king"));

    public static final RegistryObject<EntityType<FrostboundWardenEntity>> FROSTBOUND_WARDEN =
            ENTITIES.register("frostbound_warden",
                    () -> EntityType.Builder.of(FrostboundWardenEntity::new, MobCategory.MONSTER)
                            .sized(0.8F, 2.3F)
                            .clientTrackingRange(10)
                            .build("frostbound_warden"));

    public static final RegistryObject<EntityType<FleshColossusEntity>> FLESH_COLOSSUS =
            ENTITIES.register("flesh_colossus",
                    () -> EntityType.Builder.of(FleshColossusEntity::new, MobCategory.MONSTER)
                            .sized(1.8F, 1.7F)
                            .clientTrackingRange(10)
                            .build("flesh_colossus"));

    public static final RegistryObject<EntityType<VoidArchonEntity>> VOID_ARCHON =
            ENTITIES.register("void_archon",
                    () -> EntityType.Builder.of(VoidArchonEntity::new, MobCategory.MONSTER)
                            .sized(0.7F, 2.9F)
                            .clientTrackingRange(10)
                            .build("void_archon"));

    public static final RegistryObject<EntityType<ForestStalkerEntity>> FOREST_STALKER =
            ENTITIES.register("forest_stalker",
                    () -> EntityType.Builder.of(ForestStalkerEntity::new, MobCategory.MONSTER)
                            .sized(0.75F, 2.1F)
                            .clientTrackingRange(8)
                            .build("forest_stalker"));

    public static final RegistryObject<EntityType<AshRevenantEntity>> ASH_REVENANT =
            ENTITIES.register("ash_revenant",
                    () -> EntityType.Builder.of(AshRevenantEntity::new, MobCategory.MONSTER)
                            .fireImmune()
                            .sized(0.8F, 2.5F)
                            .clientTrackingRange(8)
                            .build("ash_revenant"));

    public static final RegistryObject<EntityType<FrostMarauderEntity>> FROST_MARAUDER =
            ENTITIES.register("frost_marauder",
                    () -> EntityType.Builder.of(FrostMarauderEntity::new, MobCategory.MONSTER)
                            .sized(0.75F, 2.2F)
                            .clientTrackingRange(8)
                            .build("frost_marauder"));

    public static final RegistryObject<EntityType<FleshHunterEntity>> FLESH_HUNTER =
            ENTITIES.register("flesh_hunter",
                    () -> EntityType.Builder.of(FleshHunterEntity::new, MobCategory.MONSTER)
                            .sized(1.45F, 1.45F)
                            .clientTrackingRange(8)
                            .build("flesh_hunter"));

    public static final RegistryObject<EntityType<VoidReaperEntity>> VOID_REAPER =
            ENTITIES.register("void_reaper",
                    () -> EntityType.Builder.of(VoidReaperEntity::new, MobCategory.MONSTER)
                            .sized(0.7F, 2.8F)
                            .clientTrackingRange(8)
                            .build("void_reaper"));

    public static final RegistryObject<EntityType<RootboundThrallEntity>> ROOTBOUND_THRALL =
            ENTITIES.register("rootbound_thrall",
                    () -> EntityType.Builder.of(RootboundThrallEntity::new, MobCategory.MONSTER)
                            .sized(0.7F, 1.95F)
                            .clientTrackingRange(8)
                            .build("rootbound_thrall"));

    public static final RegistryObject<EntityType<CinderImpEntity>> CINDER_IMP =
            ENTITIES.register("cinder_imp",
                    () -> EntityType.Builder.of(CinderImpEntity::new, MobCategory.MONSTER)
                            .fireImmune()
                            .sized(0.65F, 1.65F)
                            .clientTrackingRange(8)
                            .build("cinder_imp"));

    public static final RegistryObject<EntityType<GlacialWraithEntity>> GLACIAL_WRAITH =
            ENTITIES.register("glacial_wraith",
                    () -> EntityType.Builder.of(GlacialWraithEntity::new, MobCategory.MONSTER)
                            .sized(0.7F, 2.0F)
                            .clientTrackingRange(8)
                            .build("glacial_wraith"));

    public static final RegistryObject<EntityType<MarrowCrawlerEntity>> MARROW_CRAWLER =
            ENTITIES.register("marrow_crawler",
                    () -> EntityType.Builder.of(MarrowCrawlerEntity::new, MobCategory.MONSTER)
                            .sized(0.85F, 1.85F)
                            .clientTrackingRange(8)
                            .build("marrow_crawler"));

    public static final RegistryObject<EntityType<VoidShadeEntity>> VOID_SHADE =
            ENTITIES.register("void_shade",
                    () -> EntityType.Builder.of(VoidShadeEntity::new, MobCategory.MONSTER)
                            .sized(0.7F, 2.6F)
                            .clientTrackingRange(8)
                            .build("void_shade"));
}
