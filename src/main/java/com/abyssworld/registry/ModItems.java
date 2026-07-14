package com.abyssworld.registry;

import com.abyssworld.AbyssWorld;
import com.abyssworld.item.AbyssAttractionRingItem;
import com.abyssworld.item.AbyssArmorModuleItem;
import com.abyssworld.item.AbyssFocusItem;
import com.abyssworld.item.AbyssGodCatalystItem;
import com.abyssworld.item.AbyssKeyItem;
import com.abyssworld.item.AbyssManaMultitoolItem;
import com.abyssworld.item.AbyssOreConcentrateItem;
import com.abyssworld.item.AbyssReturnTalismanItem;
import com.abyssworld.item.CrystallineAbyssArmorItem;
import com.abyssworld.item.LayerBossCatalystItem;
import com.abyssworld.item.LayerWeaponItem;
import com.abyssworld.item.PortableManaCoreItem;
import com.abyssworld.item.PrimordialRelicItem;
import com.abyssworld.item.SingularityAbyssArmorItem;
import com.abyssworld.item.AbyssSpellbookItem;
import com.abyssworld.item.SpellGlyphItem;
import com.abyssworld.item.AbyssFunctionalNexusItem;
import com.abyssworld.block.AbyssFunctionalNexusBlock;
import com.abyssworld.item.AbyssMachineUpgradeItem;
import com.abyssworld.item.ResonanceConfiguratorItem;
import com.abyssworld.item.AbyssItemFilterItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AbyssWorld.MODID);

    // ---- 基礎素材 ----
    public static final RegistryObject<Item> RAW_ABYSS_IRON =
            simple("raw_abyss_iron", Rarity.COMMON);
    public static final RegistryObject<Item> ABYSS_IRON_INGOT =
            simple("abyss_iron_ingot", Rarity.COMMON);
    public static final RegistryObject<Item> ABYSS_CRYSTAL =
            simple("abyss_crystal", Rarity.UNCOMMON);
    public static final RegistryObject<Item> COMPRESSED_ABYSS_CRYSTAL =
            simple("compressed_abyss_crystal", Rarity.RARE);
    public static final RegistryObject<Item> COMPRESSED_ABYSS_IRON =
            simple("compressed_abyss_iron", Rarity.UNCOMMON);
    public static final RegistryObject<Item> HIGH_DENSITY_ABYSS_ALLOY =
            simple("high_density_abyss_alloy", Rarity.RARE);
    public static final RegistryObject<Item> CRUSHED_IRON = simple("crushed_iron", Rarity.COMMON);
    public static final RegistryObject<Item> CRUSHED_GOLD = simple("crushed_gold", Rarity.COMMON);
    public static final RegistryObject<Item> CRUSHED_COPPER = simple("crushed_copper", Rarity.COMMON);
    public static final RegistryObject<Item> CRUSHED_ABYSS_IRON = simple("crushed_abyss_iron", Rarity.UNCOMMON);
    public static final RegistryObject<Item> PURIFIED_IRON_DUST = simple("purified_iron_dust", Rarity.COMMON);
    public static final RegistryObject<Item> PURIFIED_GOLD_DUST = simple("purified_gold_dust", Rarity.COMMON);
    public static final RegistryObject<Item> PURIFIED_COPPER_DUST = simple("purified_copper_dust", Rarity.COMMON);
    public static final RegistryObject<Item> PURIFIED_ABYSS_IRON_DUST =
            simple("purified_abyss_iron_dust", Rarity.UNCOMMON);
    public static final RegistryObject<Item> AMPLIFIED_IRON_CONCENTRATE = ITEMS.register(
            "amplified_iron_concentrate",
            () -> new AbyssOreConcentrateItem(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> AMPLIFIED_GOLD_CONCENTRATE = ITEMS.register(
            "amplified_gold_concentrate",
            () -> new AbyssOreConcentrateItem(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> AMPLIFIED_COPPER_CONCENTRATE = ITEMS.register(
            "amplified_copper_concentrate",
            () -> new AbyssOreConcentrateItem(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> AMPLIFIED_ABYSS_IRON_CONCENTRATE = ITEMS.register(
            "amplified_abyss_iron_concentrate",
            () -> new AbyssOreConcentrateItem(new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> CRYSTALLIZATION_RESIDUE =
            simple("crystallization_residue", Rarity.RARE);
    public static final RegistryObject<Item> SINGULARITY_RESIDUE = ITEMS.register("singularity_residue",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> CRYSTALLINE_ARMOR_PLATE =
            simple("crystalline_armor_plate", Rarity.RARE);
    public static final RegistryObject<Item> SINGULARITY_ARMOR_PLATE = ITEMS.register("singularity_armor_plate",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> RESONANCE_MATRIX = simple("resonance_matrix", Rarity.RARE);
    public static final RegistryObject<Item> THERMAL_MATRIX = simple("thermal_matrix", Rarity.EPIC);
    public static final RegistryObject<Item> ABYSSAL_ESSENCE_BUCKET = ITEMS.register("abyssal_essence_bucket",
            () -> new BucketItem(ModFluids.ABYSSAL_ESSENCE,
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).rarity(Rarity.RARE)));

    // ---- 階層強敵素材 ----
    public static final RegistryObject<Item> VERDANT_FANG =
            simple("verdant_fang", Rarity.UNCOMMON);
    public static final RegistryObject<Item> CINDER_HEART = ITEMS.register("cinder_heart",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant()));
    public static final RegistryObject<Item> GLACIAL_PLATE =
            simple("glacial_plate", Rarity.UNCOMMON);
    public static final RegistryObject<Item> LIVING_SINEW =
            simple("living_sinew", Rarity.UNCOMMON);
    public static final RegistryObject<Item> VOID_EYE =
            simple("void_eye", Rarity.UNCOMMON);

    // ---- 忘却の森 ----
    public static final RegistryObject<Item> PRIMORDIAL_SAP =
            simple("primordial_sap", Rarity.UNCOMMON);
    public static final RegistryObject<Item> AWAKENED_VINE =
            simple("awakened_vine", Rarity.UNCOMMON);
    public static final RegistryObject<Item> GROVE_HEART_KEY = ITEMS.register("grove_heart_key",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
    public static final RegistryObject<Item> PERFECT_LIFE_CORE = ITEMS.register("perfect_life_core",
            () -> new LayerBossCatalystItem(new Item.Properties().rarity(Rarity.RARE),
                    ModEntities.ROTTEN_FOREST_GUARDIAN, "forgotten_forest",
                    "entity.abyssworld.rotten_forest_guardian"));
    public static final RegistryObject<Item> ROTTEN_FOREST_CORE =
            simple("rotten_forest_core", Rarity.EPIC);

    // ---- 灰の荒野 ----
    public static final RegistryObject<Item> ETERNAL_FLAME = ITEMS.register("eternal_flame",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant()));
    public static final RegistryObject<Item> SUPERHEATED_CORE = ITEMS.register("superheated_core",
            () -> new LayerBossCatalystItem(new Item.Properties().rarity(Rarity.RARE).fireResistant(),
                    ModEntities.ASH_KING, "ash_wasteland", "entity.abyssworld.ash_king"));
    public static final RegistryObject<Item> ASH_KING_METAL = ITEMS.register("ash_king_metal",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant()));
    public static final RegistryObject<Item> ETERNAL_FURNACE_CORE = ITEMS.register("eternal_furnace_core",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC).fireResistant()));

    // ---- 蒼氷洞窟 ----
    public static final RegistryObject<Item> UNMELTING_ICE_CRYSTAL =
            simple("unmelting_ice_crystal", Rarity.RARE);
    public static final RegistryObject<Item> FROZEN_TIME_SHARD = ITEMS.register("frozen_time_shard",
            () -> new LayerBossCatalystItem(new Item.Properties().rarity(Rarity.RARE),
                    ModEntities.FROSTBOUND_WARDEN, "frozen_cavern",
                    "entity.abyssworld.frostbound_warden"));
    public static final RegistryObject<Item> PERMAFROST_CORE =
            simple("permafrost_core", Rarity.EPIC);

    // ---- 肉体鉱山 ----
    public static final RegistryObject<Item> PRIMORDIAL_NERVE =
            simple("primordial_nerve", Rarity.UNCOMMON);
    public static final RegistryObject<Item> UNDYING_CELL = ITEMS.register("undying_cell",
            () -> new LayerBossCatalystItem(new Item.Properties().rarity(Rarity.RARE),
                    ModEntities.FLESH_COLOSSUS, "flesh_mine", "entity.abyssworld.flesh_colossus"));
    public static final RegistryObject<Item> WORLD_PULSE_FLUID =
            simple("world_pulse_fluid", Rarity.RARE);
    public static final RegistryObject<Item> PRIMORDIAL_NERVE_BUNDLE =
            simple("primordial_nerve_bundle", Rarity.EPIC);

    // ---- 虚無の都 ----
    public static final RegistryObject<Item> SPATIAL_ANCHOR_CRYSTAL =
            simple("spatial_anchor_crystal", Rarity.RARE);
    public static final RegistryObject<Item> VOID_STABILIZER =
            simple("void_stabilizer", Rarity.EPIC);
    public static final RegistryObject<Item> WORLD_LAW_FRAGMENT = ITEMS.register("world_law_fragment",
            () -> new LayerBossCatalystItem(new Item.Properties().rarity(Rarity.EPIC),
                    ModEntities.VOID_ARCHON, "void_city", "entity.abyssworld.void_archon"));

    // ---- 最終素材・究極アイテム ----
    public static final RegistryObject<Item> ABYSS_GOD_CORE = ITEMS.register("abyss_god_core",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC).fireResistant().stacksTo(1)));
    public static final RegistryObject<Item> FIVE_LAYER_UNIFIED_CORE = ITEMS.register("five_layer_unified_core",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC).fireResistant().stacksTo(1)));
    public static final RegistryObject<Item> PRIMORDIAL_RELIC = ITEMS.register("primordial_relic",
            PrimordialRelicItem::new);
    public static final RegistryObject<Item> ABYSS_KEY = ITEMS.register("abyss_key",
            AbyssKeyItem::new);
    public static final RegistryObject<Item> ABYSS_FOCUS = ITEMS.register("abyss_focus",
            AbyssFocusItem::new);
    public static final RegistryObject<Item> ABYSS_SPELLBOOK = ITEMS.register("abyss_spellbook",
            () -> new AbyssSpellbookItem(new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
    public static final RegistryObject<Item> GLYPH_FORM_SELF = glyph(SpellGlyphItem.Glyph.FORM_SELF, Rarity.UNCOMMON);
    public static final RegistryObject<Item> GLYPH_FORM_BOLT = glyph(SpellGlyphItem.Glyph.FORM_BOLT, Rarity.UNCOMMON);
    public static final RegistryObject<Item> GLYPH_FORM_AREA = glyph(SpellGlyphItem.Glyph.FORM_AREA, Rarity.RARE);
    public static final RegistryObject<Item> GLYPH_EFFECT_FIRE = glyph(SpellGlyphItem.Glyph.EFFECT_FIRE, Rarity.UNCOMMON);
    public static final RegistryObject<Item> GLYPH_EFFECT_FROST = glyph(SpellGlyphItem.Glyph.EFFECT_FROST, Rarity.UNCOMMON);
    public static final RegistryObject<Item> GLYPH_EFFECT_HEAL = glyph(SpellGlyphItem.Glyph.EFFECT_HEAL, Rarity.RARE);
    public static final RegistryObject<Item> GLYPH_EFFECT_BREAK = glyph(SpellGlyphItem.Glyph.EFFECT_BREAK, Rarity.RARE);
    public static final RegistryObject<Item> GLYPH_EFFECT_PULL = glyph(SpellGlyphItem.Glyph.EFFECT_PULL, Rarity.UNCOMMON);
    public static final RegistryObject<Item> GLYPH_EFFECT_BLINK = glyph(SpellGlyphItem.Glyph.EFFECT_BLINK, Rarity.RARE);
    public static final RegistryObject<Item> GLYPH_AUGMENT_POWER = glyph(SpellGlyphItem.Glyph.AUGMENT_POWER, Rarity.RARE);
    public static final RegistryObject<Item> GLYPH_AUGMENT_RANGE = glyph(SpellGlyphItem.Glyph.AUGMENT_RANGE, Rarity.UNCOMMON);
    public static final RegistryObject<Item> GLYPH_AUGMENT_DURATION = glyph(SpellGlyphItem.Glyph.AUGMENT_DURATION, Rarity.UNCOMMON);
    public static final RegistryObject<Item> GLYPH_AUGMENT_EFFICIENCY = glyph(SpellGlyphItem.Glyph.AUGMENT_EFFICIENCY, Rarity.RARE);
    public static final RegistryObject<Item> GLYPH_AUGMENT_CHAIN = glyph(SpellGlyphItem.Glyph.AUGMENT_CHAIN, Rarity.RARE);
    public static final RegistryObject<Item> GLYPH_AUGMENT_AREA = glyph(SpellGlyphItem.Glyph.AUGMENT_AREA, Rarity.RARE);
    public static final RegistryObject<Item> ABYSS_GOD_CATALYST = ITEMS.register("abyss_god_catalyst",
            AbyssGodCatalystItem::new);

    // ---- 携帯魔力装具 ----
    public static final RegistryObject<Item> PORTABLE_MANA_CORE = ITEMS.register("portable_mana_core",
            () -> new PortableManaCoreItem(new Item.Properties().rarity(Rarity.RARE)
                    .fireResistant().stacksTo(1)));
    public static final RegistryObject<Item> ABYSS_MANA_MULTITOOL = ITEMS.register("abyss_mana_multitool",
            () -> new AbyssManaMultitoolItem(Tiers.DIAMOND,
                    new Item.Properties().rarity(Rarity.RARE).fireResistant().stacksTo(1)));
    public static final RegistryObject<Item> ABYSS_ATTRACTION_RING = ITEMS.register("abyss_attraction_ring",
            () -> new AbyssAttractionRingItem(new Item.Properties().rarity(Rarity.RARE)
                    .fireResistant().stacksTo(1)));
    public static final RegistryObject<Item> ABYSS_RETURN_TALISMAN = ITEMS.register("abyss_return_talisman",
            () -> new AbyssReturnTalismanItem(new Item.Properties().rarity(Rarity.EPIC)
                    .fireResistant().stacksTo(1)));

    // ---- 工業制御・機械強化 ----
    public static final RegistryObject<Item> RESONANCE_CONFIGURATOR = ITEMS.register("resonance_configurator",
            () -> new ResonanceConfiguratorItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)));
    public static final RegistryObject<Item> ITEM_FILTER = ITEMS.register("item_filter",
            () -> new AbyssItemFilterItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)));
    public static final RegistryObject<Item> SPEED_UPGRADE = upgrade("speed_upgrade",
            AbyssMachineUpgradeItem.Type.SPEED, 1);
    public static final RegistryObject<Item> EFFICIENCY_UPGRADE = upgrade("efficiency_upgrade",
            AbyssMachineUpgradeItem.Type.EFFICIENCY, 1);
    public static final RegistryObject<Item> CAPACITY_UPGRADE = upgrade("capacity_upgrade",
            AbyssMachineUpgradeItem.Type.CAPACITY, 1);
    public static final RegistryObject<Item> AUTO_EXPORT_UPGRADE = upgrade("auto_export_upgrade",
            AbyssMachineUpgradeItem.Type.AUTO_EXPORT, 1);
    public static final RegistryObject<Item> RANGE_UPGRADE = upgrade("range_upgrade",
            AbyssMachineUpgradeItem.Type.RANGE, 1);
    public static final RegistryObject<Item> BASIC_FACTORY_CORE = upgrade("basic_factory_core",
            AbyssMachineUpgradeItem.Type.FACTORY, 3);
    public static final RegistryObject<Item> ADVANCED_FACTORY_CORE = upgrade("advanced_factory_core",
            AbyssMachineUpgradeItem.Type.FACTORY, 5);
    public static final RegistryObject<Item> ULTIMATE_FACTORY_CORE = upgrade("ultimate_factory_core",
            AbyssMachineUpgradeItem.Type.FACTORY, 7);

    // ---- 深淵防具 ----
    public static final RegistryObject<Item> CRYSTALLINE_ABYSS_HELMET = ITEMS.register(
            "crystalline_abyss_helmet", () -> crystallineArmor(ArmorItem.Type.HELMET));
    public static final RegistryObject<Item> CRYSTALLINE_ABYSS_CHESTPLATE = ITEMS.register(
            "crystalline_abyss_chestplate", () -> crystallineArmor(ArmorItem.Type.CHESTPLATE));
    public static final RegistryObject<Item> CRYSTALLINE_ABYSS_LEGGINGS = ITEMS.register(
            "crystalline_abyss_leggings", () -> crystallineArmor(ArmorItem.Type.LEGGINGS));
    public static final RegistryObject<Item> CRYSTALLINE_ABYSS_BOOTS = ITEMS.register(
            "crystalline_abyss_boots", () -> crystallineArmor(ArmorItem.Type.BOOTS));
    public static final RegistryObject<Item> SINGULARITY_ABYSS_HELMET = ITEMS.register(
            "singularity_abyss_helmet", () -> singularityArmor(ArmorItem.Type.HELMET));
    public static final RegistryObject<Item> SINGULARITY_ABYSS_CHESTPLATE = ITEMS.register(
            "singularity_abyss_chestplate", () -> singularityArmor(ArmorItem.Type.CHESTPLATE));
    public static final RegistryObject<Item> SINGULARITY_ABYSS_LEGGINGS = ITEMS.register(
            "singularity_abyss_leggings", () -> singularityArmor(ArmorItem.Type.LEGGINGS));
    public static final RegistryObject<Item> SINGULARITY_ABYSS_BOOTS = ITEMS.register(
            "singularity_abyss_boots", () -> singularityArmor(ArmorItem.Type.BOOTS));

    public static final RegistryObject<Item> ABYSS_ARMOR_MODULE_FRAME =
            simple("abyss_armor_module_frame", Rarity.RARE);
    public static final RegistryObject<Item> VERDANT_ARMOR_MODULE = armorModule(
            "verdant_armor_module", AbyssArmorModuleItem.Type.VERDANT);
    public static final RegistryObject<Item> CINDER_ARMOR_MODULE = armorModule(
            "cinder_armor_module", AbyssArmorModuleItem.Type.CINDER);
    public static final RegistryObject<Item> FROST_ARMOR_MODULE = armorModule(
            "frost_armor_module", AbyssArmorModuleItem.Type.FROST);
    public static final RegistryObject<Item> FLESH_ARMOR_MODULE = armorModule(
            "flesh_armor_module", AbyssArmorModuleItem.Type.FLESH);
    public static final RegistryObject<Item> VOID_ARMOR_MODULE = armorModule(
            "void_armor_module", AbyssArmorModuleItem.Type.VOID);

    // ---- スポーンエッグ ----
    public static final RegistryObject<Item> ABYSS_SOVEREIGN_SPAWN_EGG =
            spawnEgg("abyss_sovereign_spawn_egg", ModEntities.ABYSS_SOVEREIGN, 0x24143C, 0xD6A2FF);
    public static final RegistryObject<Item> ROTTEN_FOREST_GUARDIAN_SPAWN_EGG =
            spawnEgg("rotten_forest_guardian_spawn_egg", ModEntities.ROTTEN_FOREST_GUARDIAN, 0x30562A, 0x84DC54);
    public static final RegistryObject<Item> GROVE_SENTINEL_SPAWN_EGG =
            spawnEgg("grove_sentinel_spawn_egg", ModEntities.GROVE_SENTINEL, 0x263E27, 0xC5A64A);
    public static final RegistryObject<Item> ASH_KING_SPAWN_EGG =
            spawnEgg("ash_king_spawn_egg", ModEntities.ASH_KING, 0x582214, 0xFF7622);
    public static final RegistryObject<Item> FROSTBOUND_WARDEN_SPAWN_EGG =
            spawnEgg("frostbound_warden_spawn_egg", ModEntities.FROSTBOUND_WARDEN, 0x5280A0, 0xBCECFF);
    public static final RegistryObject<Item> FLESH_COLOSSUS_SPAWN_EGG =
            spawnEgg("flesh_colossus_spawn_egg", ModEntities.FLESH_COLOSSUS, 0x682230, 0xE85C70);
    public static final RegistryObject<Item> VOID_ARCHON_SPAWN_EGG =
            spawnEgg("void_archon_spawn_egg", ModEntities.VOID_ARCHON, 0x1C1236, 0xB07EFF);
    public static final RegistryObject<Item> FOREST_STALKER_SPAWN_EGG =
            spawnEgg("forest_stalker_spawn_egg", ModEntities.FOREST_STALKER, 0x264A24, 0x76C44A);
    public static final RegistryObject<Item> ASH_REVENANT_SPAWN_EGG =
            spawnEgg("ash_revenant_spawn_egg", ModEntities.ASH_REVENANT, 0x40302E, 0xEC5E28);
    public static final RegistryObject<Item> FROST_MARAUDER_SPAWN_EGG =
            spawnEgg("frost_marauder_spawn_egg", ModEntities.FROST_MARAUDER, 0x487696, 0x9CE0FF);
    public static final RegistryObject<Item> FLESH_HUNTER_SPAWN_EGG =
            spawnEgg("flesh_hunter_spawn_egg", ModEntities.FLESH_HUNTER, 0x5C1C2A, 0xD24C62);
    public static final RegistryObject<Item> VOID_REAPER_SPAWN_EGG =
            spawnEgg("void_reaper_spawn_egg", ModEntities.VOID_REAPER, 0x18122A, 0x9260E0);
    public static final RegistryObject<Item> ROOTBOUND_THRALL_SPAWN_EGG =
            spawnEgg("rootbound_thrall_spawn_egg", ModEntities.ROOTBOUND_THRALL, 0x384822, 0x78BC52);
    public static final RegistryObject<Item> CINDER_IMP_SPAWN_EGG =
            spawnEgg("cinder_imp_spawn_egg", ModEntities.CINDER_IMP, 0x4E2A1C, 0xFF7020);
    public static final RegistryObject<Item> GLACIAL_WRAITH_SPAWN_EGG =
            spawnEgg("glacial_wraith_spawn_egg", ModEntities.GLACIAL_WRAITH, 0x5C92B4, 0xD6F8FF);
    public static final RegistryObject<Item> MARROW_CRAWLER_SPAWN_EGG =
            spawnEgg("marrow_crawler_spawn_egg", ModEntities.MARROW_CRAWLER, 0x682C3A, 0xE27082);
    public static final RegistryObject<Item> VOID_SHADE_SPAWN_EGG =
            spawnEgg("void_shade_spawn_egg", ModEntities.VOID_SHADE, 0x161026, 0x7854D2);

    // ---- 階層武器 ----
    public static final RegistryObject<Item> VERDANT_BLADE = ITEMS.register("verdant_blade",
            () -> new LayerWeaponItem(Tiers.NETHERITE, 4, -2.2F,
                    new Item.Properties().rarity(Rarity.RARE),
                    "item.abyssworld.verdant_blade.desc", LayerWeaponItem.Effect.FOREST));
    public static final RegistryObject<Item> CINDER_CLEAVER = ITEMS.register("cinder_cleaver",
            () -> new LayerWeaponItem(Tiers.NETHERITE, 5, -2.6F,
                    new Item.Properties().rarity(Rarity.RARE).fireResistant(),
                    "item.abyssworld.cinder_cleaver.desc", LayerWeaponItem.Effect.ASH));
    public static final RegistryObject<Item> FROST_LANCE = ITEMS.register("frost_lance",
            () -> new LayerWeaponItem(Tiers.NETHERITE, 3, -2.1F,
                    new Item.Properties().rarity(Rarity.RARE),
                    "item.abyssworld.frost_lance.desc", LayerWeaponItem.Effect.FROST));
    public static final RegistryObject<Item> FLESH_SCYTHE = ITEMS.register("flesh_scythe",
            () -> new LayerWeaponItem(Tiers.NETHERITE, 4, -2.4F,
                    new Item.Properties().rarity(Rarity.RARE),
                    "item.abyssworld.flesh_scythe.desc", LayerWeaponItem.Effect.FLESH));
    public static final RegistryObject<Item> VOID_EDGE = ITEMS.register("void_edge",
            () -> new LayerWeaponItem(Tiers.NETHERITE, 4, -2.3F,
                    new Item.Properties().rarity(Rarity.RARE),
                    "item.abyssworld.void_edge.desc", LayerWeaponItem.Effect.VOID));

    // ---- ブロックアイテム ----
    public static final RegistryObject<Item> ABYSS_IRON_ORE_ITEM = ITEMS.register("abyss_iron_ore",
            () -> new BlockItem(ModBlocks.ABYSS_IRON_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ABYSS_CRYSTAL_ORE_ITEM = ITEMS.register("abyss_crystal_ore",
            () -> new BlockItem(ModBlocks.ABYSS_CRYSTAL_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ABYSS_IRON_BLOCK_ITEM = ITEMS.register("abyss_iron_block",
            () -> new BlockItem(ModBlocks.ABYSS_IRON_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> WORLD_RECONSTRUCTION_FURNACE_ITEM = ITEMS.register("world_reconstruction_furnace",
            () -> new BlockItem(ModBlocks.WORLD_RECONSTRUCTION_FURNACE.get(),
                    new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> ABYSS_MANA_POOL_ITEM = ITEMS.register("abyss_mana_pool",
            () -> new BlockItem(ModBlocks.ABYSS_MANA_POOL.get(),
                    new Item.Properties().rarity(Rarity.RARE).fireResistant()));
    public static final RegistryObject<Item> ABYSS_MANA_CONDENSER_ITEM = ITEMS.register("abyss_mana_condenser",
            () -> new BlockItem(ModBlocks.ABYSS_MANA_CONDENSER.get(),
                    new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> CREATIVE_MANA_SOURCE_ITEM = ITEMS.register("creative_mana_source",
            () -> new BlockItem(ModBlocks.CREATIVE_MANA_SOURCE.get(),
                    new Item.Properties().rarity(Rarity.EPIC).fireResistant().stacksTo(1)));
    public static final RegistryObject<Item> VERDANT_MANA_BLOOM_ITEM = industrialBlockItem(
            "verdant_mana_bloom", ModBlocks.VERDANT_MANA_BLOOM, Rarity.RARE);
    public static final RegistryObject<Item> INFERNO_MANA_CRUCIBLE_ITEM = industrialBlockItem(
            "inferno_mana_crucible", ModBlocks.INFERNO_MANA_CRUCIBLE, Rarity.RARE);
    public static final RegistryObject<Item> CRYO_MANA_SIPHON_ITEM = industrialBlockItem(
            "cryo_mana_siphon", ModBlocks.CRYO_MANA_SIPHON, Rarity.RARE);
    public static final RegistryObject<Item> VOID_MANA_TAP_ITEM = industrialBlockItem(
            "void_mana_tap", ModBlocks.VOID_MANA_TAP, Rarity.EPIC);
    public static final RegistryObject<Item> ABYSS_MANA_RESERVOIR_ITEM = industrialBlockItem(
            "abyss_mana_reservoir", ModBlocks.ABYSS_MANA_RESERVOIR, Rarity.EPIC);
    public static final RegistryObject<Item> WIRELESS_MANA_RELAY_ITEM = industrialBlockItem(
            "wireless_mana_relay", ModBlocks.WIRELESS_MANA_RELAY, Rarity.EPIC);
    public static final RegistryObject<Item> MANA_VORTEX_REACTOR_ITEM = industrialBlockItem(
            "mana_vortex_reactor", ModBlocks.MANA_VORTEX_REACTOR, Rarity.EPIC);
    public static final RegistryObject<Item> VERDANT_NEXUS_ITEM = functionalNexusItem(
            "verdant_nexus", ModBlocks.VERDANT_NEXUS, AbyssFunctionalNexusBlock.Kind.VERDANT, Rarity.RARE);
    public static final RegistryObject<Item> GATHERING_NEXUS_ITEM = functionalNexusItem(
            "gathering_nexus", ModBlocks.GATHERING_NEXUS, AbyssFunctionalNexusBlock.Kind.GATHERING, Rarity.RARE);
    public static final RegistryObject<Item> WARDING_NEXUS_ITEM = functionalNexusItem(
            "warding_nexus", ModBlocks.WARDING_NEXUS, AbyssFunctionalNexusBlock.Kind.WARDING, Rarity.EPIC);
    public static final RegistryObject<Item> ABYSS_MANA_CONDUIT_ITEM = ITEMS.register("abyss_mana_conduit",
            () -> new BlockItem(ModBlocks.ABYSS_MANA_CONDUIT.get(),
                    new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant()));
    public static final RegistryObject<Item> ABYSS_ITEM_CONDUIT_ITEM = ITEMS.register("abyss_item_conduit",
            () -> new BlockItem(ModBlocks.ABYSS_ITEM_CONDUIT.get(),
                    new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant()));
    public static final RegistryObject<Item> ABYSS_FLUID_CONDUIT_ITEM = ITEMS.register("abyss_fluid_conduit",
            () -> new BlockItem(ModBlocks.ABYSS_FLUID_CONDUIT.get(),
                    new Item.Properties().rarity(Rarity.RARE).fireResistant()));
    public static final RegistryObject<Item> ABYSS_ESSENCE_RESERVOIR_ITEM = ITEMS.register("abyss_essence_reservoir",
            () -> new BlockItem(ModBlocks.ABYSS_ESSENCE_RESERVOIR.get(),
                    new Item.Properties().rarity(Rarity.RARE).fireResistant()));
    public static final RegistryObject<Item> ABYSS_STORAGE_TERMINAL_ITEM = ITEMS.register("abyss_storage_terminal",
            () -> new BlockItem(ModBlocks.ABYSS_STORAGE_TERMINAL.get(),
                    new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> ABYSS_MANA_HEATER_ITEM = ITEMS.register("abyss_mana_heater",
            () -> new BlockItem(ModBlocks.ABYSS_MANA_HEATER.get(),
                    new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant()));
    public static final RegistryObject<Item> ABYSS_ORE_CRUSHER_ITEM = ITEMS.register("abyss_ore_crusher",
            () -> new BlockItem(ModBlocks.ABYSS_ORE_CRUSHER.get(),
                    new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant()));
    public static final RegistryObject<Item> ABYSS_ORE_REFINERY_ITEM = ITEMS.register("abyss_ore_refinery",
            () -> new BlockItem(ModBlocks.ABYSS_ORE_REFINERY.get(),
                    new Item.Properties().rarity(Rarity.RARE).fireResistant()));
    public static final RegistryObject<Item> ABYSS_ESSENCE_EXTRACTOR_ITEM = ITEMS.register("abyss_essence_extractor",
            () -> new BlockItem(ModBlocks.ABYSS_ESSENCE_EXTRACTOR.get(),
                    new Item.Properties().rarity(Rarity.RARE).fireResistant()));
    public static final RegistryObject<Item> ABYSS_MACHINE_CASING_ITEM = ITEMS.register("abyss_machine_casing",
            () -> new BlockItem(ModBlocks.ABYSS_MACHINE_CASING.get(),
                    new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant()));
    public static final RegistryObject<Item> ABYSS_ORE_AMPLIFIER_ITEM = ITEMS.register("abyss_ore_amplifier",
            () -> new BlockItem(ModBlocks.ABYSS_ORE_AMPLIFIER.get(),
                    new Item.Properties().rarity(Rarity.RARE).fireResistant()));
    public static final RegistryObject<Item> ABYSS_ORE_CRYSTALLIZER_ITEM = ITEMS.register("abyss_ore_crystallizer",
            () -> new BlockItem(ModBlocks.ABYSS_ORE_CRYSTALLIZER.get(),
                    new Item.Properties().rarity(Rarity.RARE).fireResistant()));
    public static final RegistryObject<Item> ABYSS_ORE_RESONATOR_ITEM = ITEMS.register("abyss_ore_resonator",
            () -> new BlockItem(ModBlocks.ABYSS_ORE_RESONATOR.get(),
                    new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> ABYSS_ORE_SINGULARITY_SEPARATOR_ITEM = ITEMS.register(
            "abyss_ore_singularity_separator",
            () -> new BlockItem(ModBlocks.ABYSS_ORE_SINGULARITY_SEPARATOR.get(),
                    new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> ABYSS_INFUSION_ALTAR_ITEM = ITEMS.register("abyss_infusion_altar",
            () -> new BlockItem(ModBlocks.ABYSS_INFUSION_ALTAR.get(),
                    new Item.Properties().rarity(Rarity.RARE).fireResistant()));
    public static final RegistryObject<Item> ABYSS_RESOURCE_HARVESTER_ITEM = ITEMS.register("abyss_resource_harvester",
            () -> new BlockItem(ModBlocks.ABYSS_RESOURCE_HARVESTER.get(),
                    new Item.Properties().rarity(Rarity.RARE).fireResistant()));
    public static final RegistryObject<Item> LEYLINE_MINER_ITEM = ITEMS.register("leyline_miner",
            () -> new BlockItem(ModBlocks.LEYLINE_MINER.get(),
                    new Item.Properties().rarity(Rarity.EPIC).fireResistant()));

    public static final RegistryObject<Item> ABYSS_STONE_ITEM = blockItem("abyss_stone", ModBlocks.ABYSS_STONE);
    public static final RegistryObject<Item> FORGOTTEN_SOIL_ITEM = blockItem("forgotten_soil", ModBlocks.FORGOTTEN_SOIL);
    public static final RegistryObject<Item> FORGOTTEN_STONE_ITEM = blockItem("forgotten_stone", ModBlocks.FORGOTTEN_STONE);
    public static final RegistryObject<Item> GROVE_SEAL_ITEM = blockItem("grove_seal", ModBlocks.GROVE_SEAL);
    public static final RegistryObject<Item> ASH_CRUST_ITEM = blockItem("ash_crust", ModBlocks.ASH_CRUST);
    public static final RegistryObject<Item> ASH_STONE_ITEM = blockItem("ash_stone", ModBlocks.ASH_STONE);
    public static final RegistryObject<Item> FROZEN_SURFACE_ITEM = blockItem("frozen_surface", ModBlocks.FROZEN_SURFACE);
    public static final RegistryObject<Item> FROZEN_STONE_ITEM = blockItem("frozen_stone", ModBlocks.FROZEN_STONE);
    public static final RegistryObject<Item> FLESH_MASS_ITEM = blockItem("flesh_mass", ModBlocks.FLESH_MASS);
    public static final RegistryObject<Item> FLESH_STONE_ITEM = blockItem("flesh_stone", ModBlocks.FLESH_STONE);
    public static final RegistryObject<Item> VOID_SURFACE_ITEM = blockItem("void_surface", ModBlocks.VOID_SURFACE);
    public static final RegistryObject<Item> VOID_STONE_ITEM = blockItem("void_stone", ModBlocks.VOID_STONE);

    public static final RegistryObject<Item> PRIMORDIAL_BLOOM_ITEM = blockItem("primordial_bloom", ModBlocks.PRIMORDIAL_BLOOM);
    public static final RegistryObject<Item> ASH_VEIN_ITEM = blockItem("ash_vein", ModBlocks.ASH_VEIN);
    public static final RegistryObject<Item> FROZEN_CLUSTER_ITEM = blockItem("frozen_cluster", ModBlocks.FROZEN_CLUSTER);
    public static final RegistryObject<Item> FLESH_DEPOSIT_ITEM = blockItem("flesh_deposit", ModBlocks.FLESH_DEPOSIT);
    public static final RegistryObject<Item> VOID_CRYSTAL_ITEM = blockItem("void_crystal", ModBlocks.VOID_CRYSTAL);

    public static final RegistryObject<Item> FORGOTTEN_FOREST_ALTAR_ITEM = blockItem("forgotten_forest_altar", ModBlocks.FORGOTTEN_FOREST_ALTAR);
    public static final RegistryObject<Item> ASH_WASTELAND_ALTAR_ITEM = blockItem("ash_wasteland_altar", ModBlocks.ASH_WASTELAND_ALTAR);
    public static final RegistryObject<Item> FROZEN_CAVERN_ALTAR_ITEM = blockItem("frozen_cavern_altar", ModBlocks.FROZEN_CAVERN_ALTAR);
    public static final RegistryObject<Item> FLESH_MINE_ALTAR_ITEM = blockItem("flesh_mine_altar", ModBlocks.FLESH_MINE_ALTAR);
    public static final RegistryObject<Item> VOID_CITY_ALTAR_ITEM = blockItem("void_city_altar", ModBlocks.VOID_CITY_ALTAR);

    private static RegistryObject<Item> blockItem(String name, RegistryObject<Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static RegistryObject<Item> industrialBlockItem(String name, RegistryObject<Block> block, Rarity rarity) {
        return ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().rarity(rarity).fireResistant()));
    }

    private static RegistryObject<Item> functionalNexusItem(String name, RegistryObject<Block> block,
                                                            AbyssFunctionalNexusBlock.Kind kind, Rarity rarity) {
        return ITEMS.register(name, () -> new AbyssFunctionalNexusItem(block.get(), kind,
                new Item.Properties().rarity(rarity).fireResistant()));
    }

    private static RegistryObject<Item> simple(String name, Rarity rarity) {
        return ITEMS.register(name, () -> new Item(new Item.Properties().rarity(rarity)));
    }

    private static RegistryObject<Item> upgrade(String name, AbyssMachineUpgradeItem.Type type, int level) {
        return ITEMS.register(name, () -> new AbyssMachineUpgradeItem(
                new Item.Properties().rarity(level >= 5 ? Rarity.EPIC : Rarity.RARE).stacksTo(level > 1 ? 1 : 8),
                type, level));
    }

    private static Item crystallineArmor(ArmorItem.Type type) {
        return new CrystallineAbyssArmorItem(type,
                new Item.Properties().rarity(Rarity.RARE).fireResistant());
    }

    private static Item singularityArmor(ArmorItem.Type type) {
        return new SingularityAbyssArmorItem(type,
                new Item.Properties().rarity(Rarity.EPIC).fireResistant());
    }

    private static RegistryObject<Item> armorModule(String name, AbyssArmorModuleItem.Type type) {
        return ITEMS.register(name, () -> new AbyssArmorModuleItem(type,
                new Item.Properties().rarity(Rarity.EPIC).fireResistant().stacksTo(1)));
    }

    private static RegistryObject<Item> glyph(SpellGlyphItem.Glyph glyph, Rarity rarity) {
        return ITEMS.register("glyph_" + glyph.id(), () -> new SpellGlyphItem(glyph,
                new Item.Properties().rarity(rarity).stacksTo(1)));
    }

    private static RegistryObject<Item> spawnEgg(
            String name,
            RegistryObject<? extends net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.Mob>> entity,
            int backgroundColor,
            int highlightColor) {
        return ITEMS.register(name,
                () -> new ForgeSpawnEggItem(entity, backgroundColor, highlightColor, new Item.Properties()));
    }
}
