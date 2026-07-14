package com.abyssworld.registry;

import com.abyssworld.AbyssWorld;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AbyssWorld.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.abyssworld"))
                    .icon(() -> new ItemStack(ModItems.ABYSS_GOD_CORE.get()))
                    .displayItems((params, output) -> {
                        // 深淵世界への鍵
                        output.accept(ModItems.ABYSS_KEY.get());
                        output.accept(ModItems.ABYSS_FOCUS.get());
                        output.accept(ModItems.ABYSS_SPELLBOOK.get());
                        output.accept(ModItems.GLYPH_FORM_SELF.get());
                        output.accept(ModItems.GLYPH_FORM_BOLT.get());
                        output.accept(ModItems.GLYPH_FORM_AREA.get());
                        output.accept(ModItems.GLYPH_EFFECT_FIRE.get());
                        output.accept(ModItems.GLYPH_EFFECT_FROST.get());
                        output.accept(ModItems.GLYPH_EFFECT_HEAL.get());
                        output.accept(ModItems.GLYPH_EFFECT_BREAK.get());
                        output.accept(ModItems.GLYPH_EFFECT_PULL.get());
                        output.accept(ModItems.GLYPH_EFFECT_BLINK.get());
                        output.accept(ModItems.GLYPH_AUGMENT_POWER.get());
                        output.accept(ModItems.GLYPH_AUGMENT_RANGE.get());
                        output.accept(ModItems.GLYPH_AUGMENT_DURATION.get());
                        output.accept(ModItems.GLYPH_AUGMENT_EFFICIENCY.get());
                        output.accept(ModItems.GLYPH_AUGMENT_CHAIN.get());
                        output.accept(ModItems.GLYPH_AUGMENT_AREA.get());
                        output.accept(ModItems.ABYSS_GOD_CATALYST.get());
                        // 基礎素材と圧縮チェーン
                        output.accept(ModItems.RAW_ABYSS_IRON.get());
                        output.accept(ModItems.ABYSS_IRON_INGOT.get());
                        output.accept(ModItems.ABYSS_IRON_BLOCK_ITEM.get());
                        output.accept(ModItems.ABYSS_MANA_POOL_ITEM.get());
                        output.accept(ModItems.ABYSS_MANA_CONDENSER_ITEM.get());
                        output.accept(ModItems.CREATIVE_MANA_SOURCE_ITEM.get());
                        output.accept(ModItems.VERDANT_MANA_BLOOM_ITEM.get());
                        output.accept(ModItems.INFERNO_MANA_CRUCIBLE_ITEM.get());
                        output.accept(ModItems.CRYO_MANA_SIPHON_ITEM.get());
                        output.accept(ModItems.VOID_MANA_TAP_ITEM.get());
                        output.accept(ModItems.ABYSS_MANA_RESERVOIR_ITEM.get());
                        output.accept(ModItems.WIRELESS_MANA_RELAY_ITEM.get());
                        output.accept(ModItems.MANA_VORTEX_REACTOR_ITEM.get());
                        output.accept(ModItems.VERDANT_NEXUS_ITEM.get());
                        output.accept(ModItems.GATHERING_NEXUS_ITEM.get());
                        output.accept(ModItems.WARDING_NEXUS_ITEM.get());
                        output.accept(ModItems.ABYSS_MANA_CONDUIT_ITEM.get());
                        output.accept(ModItems.ABYSS_ITEM_CONDUIT_ITEM.get());
                        output.accept(ModItems.ABYSS_FLUID_CONDUIT_ITEM.get());
                        output.accept(ModItems.ABYSS_ESSENCE_RESERVOIR_ITEM.get());
                        output.accept(ModItems.ABYSS_STORAGE_TERMINAL_ITEM.get());
                        output.accept(ModItems.PORTABLE_MANA_CORE.get());
                        output.accept(ModItems.ABYSS_MANA_MULTITOOL.get());
                        output.accept(ModItems.ABYSS_ATTRACTION_RING.get());
                        output.accept(ModItems.ABYSS_RETURN_TALISMAN.get());
                        output.accept(ModItems.RESONANCE_CONFIGURATOR.get());
                        output.accept(ModItems.ITEM_FILTER.get());
                        output.accept(ModItems.SPEED_UPGRADE.get());
                        output.accept(ModItems.EFFICIENCY_UPGRADE.get());
                        output.accept(ModItems.CAPACITY_UPGRADE.get());
                        output.accept(ModItems.AUTO_EXPORT_UPGRADE.get());
                        output.accept(ModItems.RANGE_UPGRADE.get());
                        output.accept(ModItems.BASIC_FACTORY_CORE.get());
                        output.accept(ModItems.ADVANCED_FACTORY_CORE.get());
                        output.accept(ModItems.ULTIMATE_FACTORY_CORE.get());
                        output.accept(ModItems.ABYSS_MANA_HEATER_ITEM.get());
                        output.accept(ModItems.ABYSS_ORE_CRUSHER_ITEM.get());
                        output.accept(ModItems.ABYSS_ORE_REFINERY_ITEM.get());
                        output.accept(ModItems.ABYSS_ESSENCE_EXTRACTOR_ITEM.get());
                        output.accept(ModItems.ABYSS_MACHINE_CASING_ITEM.get());
                        output.accept(ModItems.ABYSS_ORE_AMPLIFIER_ITEM.get());
                        output.accept(ModItems.ABYSS_ORE_CRYSTALLIZER_ITEM.get());
                        output.accept(ModItems.ABYSS_ORE_RESONATOR_ITEM.get());
                        output.accept(ModItems.ABYSS_ORE_SINGULARITY_SEPARATOR_ITEM.get());
                        output.accept(ModItems.ABYSS_INFUSION_ALTAR_ITEM.get());
                        output.accept(ModItems.ABYSS_RESOURCE_HARVESTER_ITEM.get());
                        output.accept(ModItems.LEYLINE_MINER_ITEM.get());
                        output.accept(ModItems.COMPRESSED_ABYSS_IRON.get());
                        output.accept(ModItems.HIGH_DENSITY_ABYSS_ALLOY.get());
                        output.accept(ModItems.CRUSHED_IRON.get());
                        output.accept(ModItems.CRUSHED_GOLD.get());
                        output.accept(ModItems.CRUSHED_COPPER.get());
                        output.accept(ModItems.CRUSHED_ABYSS_IRON.get());
                        output.accept(ModItems.PURIFIED_IRON_DUST.get());
                        output.accept(ModItems.PURIFIED_GOLD_DUST.get());
                        output.accept(ModItems.PURIFIED_COPPER_DUST.get());
                        output.accept(ModItems.PURIFIED_ABYSS_IRON_DUST.get());
                        output.accept(ModItems.AMPLIFIED_IRON_CONCENTRATE.get());
                        output.accept(ModItems.AMPLIFIED_GOLD_CONCENTRATE.get());
                        output.accept(ModItems.AMPLIFIED_COPPER_CONCENTRATE.get());
                        output.accept(ModItems.AMPLIFIED_ABYSS_IRON_CONCENTRATE.get());
                        output.accept(ModItems.CRYSTALLIZATION_RESIDUE.get());
                        output.accept(ModItems.SINGULARITY_RESIDUE.get());
                        output.accept(ModItems.RESONANCE_MATRIX.get());
                        output.accept(ModItems.THERMAL_MATRIX.get());
                        output.accept(ModItems.ABYSSAL_ESSENCE_BUCKET.get());
                        output.accept(ModItems.CRYSTALLINE_ARMOR_PLATE.get());
                        output.accept(ModItems.SINGULARITY_ARMOR_PLATE.get());
                        output.accept(ModItems.CRYSTALLINE_ABYSS_HELMET.get());
                        output.accept(ModItems.CRYSTALLINE_ABYSS_CHESTPLATE.get());
                        output.accept(ModItems.CRYSTALLINE_ABYSS_LEGGINGS.get());
                        output.accept(ModItems.CRYSTALLINE_ABYSS_BOOTS.get());
                        output.accept(ModItems.SINGULARITY_ABYSS_HELMET.get());
                        output.accept(ModItems.SINGULARITY_ABYSS_CHESTPLATE.get());
                        output.accept(ModItems.SINGULARITY_ABYSS_LEGGINGS.get());
                        output.accept(ModItems.SINGULARITY_ABYSS_BOOTS.get());
                        output.accept(ModItems.ABYSS_ARMOR_MODULE_FRAME.get());
                        output.accept(ModItems.VERDANT_ARMOR_MODULE.get());
                        output.accept(ModItems.CINDER_ARMOR_MODULE.get());
                        output.accept(ModItems.FROST_ARMOR_MODULE.get());
                        output.accept(ModItems.FLESH_ARMOR_MODULE.get());
                        output.accept(ModItems.VOID_ARMOR_MODULE.get());
                        output.accept(ModItems.ABYSS_CRYSTAL.get());
                        output.accept(ModItems.COMPRESSED_ABYSS_CRYSTAL.get());
                        output.accept(ModItems.VERDANT_FANG.get());
                        output.accept(ModItems.CINDER_HEART.get());
                        output.accept(ModItems.GLACIAL_PLATE.get());
                        output.accept(ModItems.LIVING_SINEW.get());
                        output.accept(ModItems.VOID_EYE.get());
                        output.accept(ModItems.VERDANT_BLADE.get());
                        output.accept(ModItems.CINDER_CLEAVER.get());
                        output.accept(ModItems.FROST_LANCE.get());
                        output.accept(ModItems.FLESH_SCYTHE.get());
                        output.accept(ModItems.VOID_EDGE.get());
                        output.accept(ModItems.ROOTBOUND_THRALL_SPAWN_EGG.get());
                        output.accept(ModItems.CINDER_IMP_SPAWN_EGG.get());
                        output.accept(ModItems.GLACIAL_WRAITH_SPAWN_EGG.get());
                        output.accept(ModItems.MARROW_CRAWLER_SPAWN_EGG.get());
                        output.accept(ModItems.VOID_SHADE_SPAWN_EGG.get());
                        output.accept(ModItems.FOREST_STALKER_SPAWN_EGG.get());
                        output.accept(ModItems.ASH_REVENANT_SPAWN_EGG.get());
                        output.accept(ModItems.FROST_MARAUDER_SPAWN_EGG.get());
                        output.accept(ModItems.FLESH_HUNTER_SPAWN_EGG.get());
                        output.accept(ModItems.VOID_REAPER_SPAWN_EGG.get());
                        output.accept(ModItems.GROVE_SENTINEL_SPAWN_EGG.get());
                        output.accept(ModItems.ROTTEN_FOREST_GUARDIAN_SPAWN_EGG.get());
                        output.accept(ModItems.ASH_KING_SPAWN_EGG.get());
                        output.accept(ModItems.FROSTBOUND_WARDEN_SPAWN_EGG.get());
                        output.accept(ModItems.FLESH_COLOSSUS_SPAWN_EGG.get());
                        output.accept(ModItems.VOID_ARCHON_SPAWN_EGG.get());
                        output.accept(ModItems.ABYSS_SOVEREIGN_SPAWN_EGG.get());
                        output.accept(ModItems.ABYSS_IRON_ORE_ITEM.get());
                        output.accept(ModItems.ABYSS_CRYSTAL_ORE_ITEM.get());
                        output.accept(ModItems.ABYSS_STONE_ITEM.get());
                        output.accept(ModItems.FORGOTTEN_SOIL_ITEM.get());
                        output.accept(ModItems.FORGOTTEN_STONE_ITEM.get());
                        output.accept(ModItems.GROVE_SEAL_ITEM.get());
                        output.accept(ModItems.ASH_CRUST_ITEM.get());
                        output.accept(ModItems.ASH_STONE_ITEM.get());
                        output.accept(ModItems.FROZEN_SURFACE_ITEM.get());
                        output.accept(ModItems.FROZEN_STONE_ITEM.get());
                        output.accept(ModItems.FLESH_MASS_ITEM.get());
                        output.accept(ModItems.FLESH_STONE_ITEM.get());
                        output.accept(ModItems.VOID_SURFACE_ITEM.get());
                        output.accept(ModItems.VOID_STONE_ITEM.get());
                        output.accept(ModItems.PRIMORDIAL_BLOOM_ITEM.get());
                        output.accept(ModItems.ASH_VEIN_ITEM.get());
                        output.accept(ModItems.FROZEN_CLUSTER_ITEM.get());
                        output.accept(ModItems.FLESH_DEPOSIT_ITEM.get());
                        output.accept(ModItems.VOID_CRYSTAL_ITEM.get());
                        output.accept(ModItems.FORGOTTEN_FOREST_ALTAR_ITEM.get());
                        output.accept(ModItems.ASH_WASTELAND_ALTAR_ITEM.get());
                        output.accept(ModItems.FROZEN_CAVERN_ALTAR_ITEM.get());
                        output.accept(ModItems.FLESH_MINE_ALTAR_ITEM.get());
                        output.accept(ModItems.VOID_CITY_ALTAR_ITEM.get());
                        // 忘却の森
                        output.accept(ModItems.PRIMORDIAL_SAP.get());
                        output.accept(ModItems.AWAKENED_VINE.get());
                        output.accept(ModItems.GROVE_HEART_KEY.get());
                        output.accept(ModItems.PERFECT_LIFE_CORE.get());
                        output.accept(ModItems.ROTTEN_FOREST_CORE.get());
                        // 灰の荒野
                        output.accept(ModItems.ETERNAL_FLAME.get());
                        output.accept(ModItems.SUPERHEATED_CORE.get());
                        output.accept(ModItems.ASH_KING_METAL.get());
                        output.accept(ModItems.ETERNAL_FURNACE_CORE.get());
                        // 蒼氷洞窟
                        output.accept(ModItems.UNMELTING_ICE_CRYSTAL.get());
                        output.accept(ModItems.FROZEN_TIME_SHARD.get());
                        output.accept(ModItems.PERMAFROST_CORE.get());
                        // 肉体鉱山
                        output.accept(ModItems.PRIMORDIAL_NERVE.get());
                        output.accept(ModItems.UNDYING_CELL.get());
                        output.accept(ModItems.WORLD_PULSE_FLUID.get());
                        output.accept(ModItems.PRIMORDIAL_NERVE_BUNDLE.get());
                        // 虚無の都
                        output.accept(ModItems.SPATIAL_ANCHOR_CRYSTAL.get());
                        output.accept(ModItems.VOID_STABILIZER.get());
                        output.accept(ModItems.WORLD_LAW_FRAGMENT.get());
                        // 最終素材・設備・究極アイテム
                        output.accept(ModItems.ABYSS_GOD_CORE.get());
                        output.accept(ModItems.FIVE_LAYER_UNIFIED_CORE.get());
                        output.accept(ModItems.WORLD_RECONSTRUCTION_FURNACE_ITEM.get());
                        output.accept(ModItems.PRIMORDIAL_RELIC.get());
                    })
                    .build());
}
