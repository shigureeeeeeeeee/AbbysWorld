package com.abyssworld.registry;

import com.abyssworld.AbyssWorld;
import com.abyssworld.block.AbyssInfusionAltarBlock;
import com.abyssworld.block.AbyssManaPoolBlock;
import com.abyssworld.block.AbyssManaConduitBlock;
import com.abyssworld.block.AbyssManaMachineBlock;
import com.abyssworld.block.AbyssResourceHarvesterBlock;
import com.abyssworld.block.AbyssOreAmplifierBlock;
import com.abyssworld.block.LayerAltarBlock;
import com.abyssworld.block.AbyssItemConduitBlock;
import com.abyssworld.block.AbyssFluidConduitBlock;
import com.abyssworld.block.AbyssFluidReservoirBlock;
import com.abyssworld.block.AbyssFunctionalNexusBlock;
import com.abyssworld.block.GroveSealBlock;
import com.abyssworld.block.AbyssStorageTerminalBlock;
import com.abyssworld.block.LeylineMinerBlock;
import com.abyssworld.block.WorldReconstructionFurnaceBlock;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, AbyssWorld.MODID);

    public static final RegistryObject<Block> ABYSS_IRON_ORE = BLOCKS.register("abyss_iron_ore",
            () -> new DropExperienceBlock(
                    BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE).strength(5.0F, 4.0F),
                    UniformInt.of(1, 3)));

    public static final RegistryObject<Block> ABYSS_CRYSTAL_ORE = BLOCKS.register("abyss_crystal_ore",
            () -> new DropExperienceBlock(
                    BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE).strength(6.0F, 4.0F),
                    UniformInt.of(3, 7)));

    public static final RegistryObject<Block> ABYSS_IRON_BLOCK = BLOCKS.register("abyss_iron_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(8.0F, 8.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)));

    public static final RegistryObject<LiquidBlock> ABYSSAL_ESSENCE_BLOCK = BLOCKS.register("abyssal_essence",
            () -> new LiquidBlock(ModFluids.ABYSSAL_ESSENCE, BlockBehaviour.Properties.copy(Blocks.WATER)
                    .mapColor(MapColor.COLOR_PURPLE).lightLevel(state -> 7).noLootTable()));

    public static final RegistryObject<Block> WORLD_RECONSTRUCTION_FURNACE = BLOCKS.register("world_reconstruction_furnace",
            () -> new WorldReconstructionFurnaceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(50.0F, 1200.0F)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 9)
                    .sound(SoundType.NETHERITE_BLOCK)));

    public static final RegistryObject<Block> ABYSS_MANA_POOL = BLOCKS.register("abyss_mana_pool",
            () -> new AbyssManaPoolBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(9.0F, 12.0F)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 7)
                    .sound(SoundType.AMETHYST), AbyssManaPoolBlock.Tier.BASIC));

    public static final RegistryObject<Block> ABYSS_MANA_CONDENSER = BLOCKS.register("abyss_mana_condenser",
            () -> new AbyssManaPoolBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(14.0F, 30.0F)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 11)
                    .sound(SoundType.NETHERITE_BLOCK), AbyssManaPoolBlock.Tier.CONDENSER));

    public static final RegistryObject<Block> CREATIVE_MANA_SOURCE = BLOCKS.register("creative_mana_source",
            () -> new AbyssManaPoolBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(-1.0F, 3_600_000.0F)
                    .noLootTable()
                    .lightLevel(state -> 15)
                    .sound(SoundType.AMETHYST), AbyssManaPoolBlock.Tier.CREATIVE));

    public static final RegistryObject<Block> VERDANT_MANA_BLOOM = manaPool("verdant_mana_bloom",
            MapColor.PLANT, 8, AbyssManaPoolBlock.Tier.LIFE);
    public static final RegistryObject<Block> INFERNO_MANA_CRUCIBLE = manaPool("inferno_mana_crucible",
            MapColor.FIRE, 11, AbyssManaPoolBlock.Tier.INFERNO);
    public static final RegistryObject<Block> CRYO_MANA_SIPHON = manaPool("cryo_mana_siphon",
            MapColor.ICE, 10, AbyssManaPoolBlock.Tier.CRYO);
    public static final RegistryObject<Block> VOID_MANA_TAP = manaPool("void_mana_tap",
            MapColor.COLOR_BLACK, 13, AbyssManaPoolBlock.Tier.VOID);
    public static final RegistryObject<Block> ABYSS_MANA_RESERVOIR = manaPool("abyss_mana_reservoir",
            MapColor.COLOR_PURPLE, 6, AbyssManaPoolBlock.Tier.STORAGE);
    public static final RegistryObject<Block> WIRELESS_MANA_RELAY = manaPool("wireless_mana_relay",
            MapColor.COLOR_CYAN, 12, AbyssManaPoolBlock.Tier.WIRELESS);
    public static final RegistryObject<Block> MANA_VORTEX_REACTOR = manaPool("mana_vortex_reactor",
            MapColor.COLOR_PURPLE, 15, AbyssManaPoolBlock.Tier.REACTOR);

    public static final RegistryObject<Block> VERDANT_NEXUS = functionalNexus("verdant_nexus",
            MapColor.PLANT, AbyssFunctionalNexusBlock.Kind.VERDANT);
    public static final RegistryObject<Block> GATHERING_NEXUS = functionalNexus("gathering_nexus",
            MapColor.COLOR_CYAN, AbyssFunctionalNexusBlock.Kind.GATHERING);
    public static final RegistryObject<Block> WARDING_NEXUS = functionalNexus("warding_nexus",
            MapColor.COLOR_PURPLE, AbyssFunctionalNexusBlock.Kind.WARDING);

    public static final RegistryObject<Block> ABYSS_MANA_CONDUIT = BLOCKS.register("abyss_mana_conduit",
            () -> new AbyssManaConduitBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(3.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 3)
                    .sound(SoundType.AMETHYST)
                    .noOcclusion()));

    public static final RegistryObject<Block> ABYSS_ITEM_CONDUIT = BLOCKS.register("abyss_item_conduit",
            () -> new AbyssItemConduitBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN).strength(3.0F, 6.0F).requiresCorrectToolForDrops()
                    .lightLevel(state -> 2).sound(SoundType.COPPER).noOcclusion()));
    public static final RegistryObject<Block> ABYSS_FLUID_CONDUIT = BLOCKS.register("abyss_fluid_conduit",
            () -> new AbyssFluidConduitBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE).strength(4.0F, 8.0F).requiresCorrectToolForDrops()
                    .lightLevel(state -> 5).sound(SoundType.AMETHYST).noOcclusion()));
    public static final RegistryObject<Block> ABYSS_ESSENCE_RESERVOIR = BLOCKS.register("abyss_essence_reservoir",
            () -> new AbyssFluidReservoirBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE).strength(10.0F, 24.0F).requiresCorrectToolForDrops()
                    .lightLevel(state -> 8).sound(SoundType.AMETHYST).noOcclusion()));
    public static final RegistryObject<Block> ABYSS_STORAGE_TERMINAL = BLOCKS.register("abyss_storage_terminal",
            () -> new AbyssStorageTerminalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK).strength(12.0F, 30.0F).requiresCorrectToolForDrops()
                    .lightLevel(state -> 7).sound(SoundType.NETHERITE_BLOCK)));

    public static final RegistryObject<Block> ABYSS_MANA_HEATER = BLOCKS.register("abyss_mana_heater",
            () -> new AbyssManaMachineBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(7.0F, 12.0F)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 8)
                    .sound(SoundType.NETHERITE_BLOCK), AbyssManaMachineBlock.Kind.HEATER));

    public static final RegistryObject<Block> ABYSS_ORE_CRUSHER = BLOCKS.register("abyss_ore_crusher",
            () -> new AbyssManaMachineBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(8.0F, 14.0F)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 4)
                    .sound(SoundType.NETHERITE_BLOCK), AbyssManaMachineBlock.Kind.CRUSHER));

    public static final RegistryObject<Block> ABYSS_ORE_REFINERY = BLOCKS.register("abyss_ore_refinery",
            () -> new AbyssManaMachineBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(12.0F, 24.0F)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 9)
                    .sound(SoundType.NETHERITE_BLOCK), AbyssManaMachineBlock.Kind.REFINERY));

    public static final RegistryObject<Block> ABYSS_ESSENCE_EXTRACTOR = BLOCKS.register("abyss_essence_extractor",
            () -> new AbyssManaMachineBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN).strength(10.0F, 20.0F).requiresCorrectToolForDrops()
                    .lightLevel(state -> 10).sound(SoundType.AMETHYST),
                    AbyssManaMachineBlock.Kind.ESSENCE_EXTRACTOR));

    public static final RegistryObject<Block> ABYSS_MACHINE_CASING = BLOCKS.register("abyss_machine_casing",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(10.0F, 30.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)));

    public static final RegistryObject<Block> ABYSS_ORE_AMPLIFIER = BLOCKS.register("abyss_ore_amplifier",
            () -> new AbyssOreAmplifierBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(12.0F, 32.0F)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 7)
                    .sound(SoundType.NETHERITE_BLOCK), AbyssOreAmplifierBlock.Stage.FOUR));

    public static final RegistryObject<Block> ABYSS_ORE_CRYSTALLIZER = BLOCKS.register("abyss_ore_crystallizer",
            () -> new AbyssOreAmplifierBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(14.0F, 40.0F)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 9)
                    .sound(SoundType.AMETHYST), AbyssOreAmplifierBlock.Stage.SIX));

    public static final RegistryObject<Block> ABYSS_ORE_RESONATOR = BLOCKS.register("abyss_ore_resonator",
            () -> new AbyssOreAmplifierBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(16.0F, 50.0F)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 11)
                    .sound(SoundType.NETHERITE_BLOCK), AbyssOreAmplifierBlock.Stage.EIGHT));

    public static final RegistryObject<Block> ABYSS_ORE_SINGULARITY_SEPARATOR =
            BLOCKS.register("abyss_ore_singularity_separator",
                    () -> new AbyssOreAmplifierBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLACK)
                            .strength(20.0F, 80.0F)
                            .requiresCorrectToolForDrops()
                            .lightLevel(state -> 14)
                            .sound(SoundType.NETHERITE_BLOCK), AbyssOreAmplifierBlock.Stage.TEN));

    public static final RegistryObject<Block> ABYSS_INFUSION_ALTAR = BLOCKS.register("abyss_infusion_altar",
            () -> new AbyssInfusionAltarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(10.0F, 16.0F)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 5)
                    .sound(SoundType.NETHERITE_BLOCK)));

    public static final RegistryObject<Block> ABYSS_RESOURCE_HARVESTER = BLOCKS.register("abyss_resource_harvester",
            () -> new AbyssResourceHarvesterBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(12.0F, 24.0F)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 6)
                    .sound(SoundType.NETHERITE_BLOCK)));

    public static final RegistryObject<Block> LEYLINE_MINER = BLOCKS.register("leyline_miner",
            () -> new LeylineMinerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN).strength(16.0F, 48.0F).requiresCorrectToolForDrops()
                    .lightLevel(state -> 9).sound(SoundType.NETHERITE_BLOCK)));

    // ---- 深淵地形ブロック ----
    public static final RegistryObject<Block> ABYSS_STONE = BLOCKS.register("abyss_stone",
            () -> terrainBlock(MapColor.COLOR_PURPLE, 3.5F, SoundType.DEEPSLATE));
    public static final RegistryObject<Block> BOUNDARY_SOIL = BLOCKS.register("boundary_soil",
            () -> terrainBlock(MapColor.COLOR_BLACK, 1.6F, SoundType.ROOTED_DIRT));
    public static final RegistryObject<Block> BOUNDARY_STONE = BLOCKS.register("boundary_stone",
            () -> terrainBlock(MapColor.COLOR_BLACK, 4.2F, SoundType.DEEPSLATE));
    public static final RegistryObject<Block> BOUNDARY_BRICKS = BLOCKS.register("boundary_bricks",
            () -> terrainBlock(MapColor.TERRACOTTA_PURPLE, 5.0F, SoundType.DEEPSLATE_BRICKS));
    public static final RegistryObject<Block> RIFT_CORE = BLOCKS.register("rift_core",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(6.0F, 18.0F)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 12)
                    .sound(SoundType.AMETHYST)));
    public static final RegistryObject<Block> FORGOTTEN_SOIL = BLOCKS.register("forgotten_soil",
            () -> terrainBlock(MapColor.PLANT, 1.2F, SoundType.ROOTED_DIRT));
    public static final RegistryObject<Block> FORGOTTEN_STONE = BLOCKS.register("forgotten_stone",
            () -> terrainBlock(MapColor.COLOR_GREEN, 3.0F, SoundType.DEEPSLATE));
    public static final RegistryObject<Block> GROVE_SEAL = BLOCKS.register("grove_seal",
            () -> new GroveSealBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(-1.0F, 3_600_000.0F)
                    .lightLevel(state -> 7)
                    .noLootTable()
                    .sound(SoundType.AMETHYST)));
    public static final RegistryObject<Block> ASH_CRUST = BLOCKS.register("ash_crust",
            () -> terrainBlock(MapColor.TERRACOTTA_GRAY, 1.8F, SoundType.BASALT));
    public static final RegistryObject<Block> ASH_STONE = BLOCKS.register("ash_stone",
            () -> terrainBlock(MapColor.COLOR_BLACK, 3.4F, SoundType.BASALT));
    public static final RegistryObject<Block> FROZEN_SURFACE = BLOCKS.register("frozen_surface",
            () -> terrainBlock(MapColor.ICE, 1.8F, SoundType.GLASS));
    public static final RegistryObject<Block> FROZEN_STONE = BLOCKS.register("frozen_stone",
            () -> terrainBlock(MapColor.COLOR_LIGHT_BLUE, 3.2F, SoundType.DEEPSLATE));
    public static final RegistryObject<Block> FLESH_MASS = BLOCKS.register("flesh_mass",
            () -> terrainBlock(MapColor.COLOR_RED, 1.5F, SoundType.SLIME_BLOCK));
    public static final RegistryObject<Block> FLESH_STONE = BLOCKS.register("flesh_stone",
            () -> terrainBlock(MapColor.TERRACOTTA_RED, 3.0F, SoundType.NETHERRACK));
    public static final RegistryObject<Block> VOID_SURFACE = BLOCKS.register("void_surface",
            () -> terrainBlock(MapColor.COLOR_PURPLE, 2.4F, SoundType.AMETHYST));
    public static final RegistryObject<Block> VOID_STONE = BLOCKS.register("void_stone",
            () -> terrainBlock(MapColor.COLOR_BLACK, 4.0F, SoundType.DEEPSLATE));

    // ---- 階層別採取ブロック ----
    public static final RegistryObject<Block> PRIMORDIAL_BLOOM = BLOCKS.register("primordial_bloom",
            () -> depositBlock(MapColor.PLANT, 2.0F, SoundType.GRASS));
    public static final RegistryObject<Block> ASH_VEIN = BLOCKS.register("ash_vein",
            () -> depositBlock(MapColor.TERRACOTTA_GRAY, 3.0F, SoundType.BASALT));
    public static final RegistryObject<Block> FROZEN_CLUSTER = BLOCKS.register("frozen_cluster",
            () -> depositBlock(MapColor.ICE, 2.5F, SoundType.GLASS));
    public static final RegistryObject<Block> FLESH_DEPOSIT = BLOCKS.register("flesh_deposit",
            () -> depositBlock(MapColor.COLOR_RED, 2.0F, SoundType.SLIME_BLOCK));
    public static final RegistryObject<Block> VOID_CRYSTAL = BLOCKS.register("void_crystal",
            () -> depositBlock(MapColor.COLOR_PURPLE, 4.0F, SoundType.AMETHYST));

    // ---- 階層ボス祭壇 ----
    public static final RegistryObject<Block> FORGOTTEN_FOREST_ALTAR = BLOCKS.register("forgotten_forest_altar",
            () -> altarBlock(MapColor.COLOR_GREEN, ModEntities.ROTTEN_FOREST_GUARDIAN,
                    "entity.abyssworld.rotten_forest_guardian", "item.abyssworld.perfect_life_core"));
    public static final RegistryObject<Block> ASH_WASTELAND_ALTAR = BLOCKS.register("ash_wasteland_altar",
            () -> altarBlock(MapColor.COLOR_BLACK, ModEntities.ASH_KING,
                    "entity.abyssworld.ash_king", "item.abyssworld.superheated_core"));
    public static final RegistryObject<Block> FROZEN_CAVERN_ALTAR = BLOCKS.register("frozen_cavern_altar",
            () -> altarBlock(MapColor.ICE, ModEntities.FROSTBOUND_WARDEN,
                    "entity.abyssworld.frostbound_warden", "item.abyssworld.frozen_time_shard"));
    public static final RegistryObject<Block> FLESH_MINE_ALTAR = BLOCKS.register("flesh_mine_altar",
            () -> altarBlock(MapColor.COLOR_RED, ModEntities.FLESH_COLOSSUS,
                    "entity.abyssworld.flesh_colossus", "item.abyssworld.undying_cell"));
    public static final RegistryObject<Block> VOID_CITY_ALTAR = BLOCKS.register("void_city_altar",
            () -> altarBlock(MapColor.COLOR_PURPLE, ModEntities.VOID_ARCHON,
                    "entity.abyssworld.void_archon", "item.abyssworld.world_law_fragment"));

    private static Block depositBlock(MapColor color, float hardness, SoundType sound) {
        return new Block(BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(hardness, hardness + 2.0F)
                .requiresCorrectToolForDrops()
                .sound(sound));
    }

    private static RegistryObject<Block> manaPool(String name, MapColor color, int light,
                                                   AbyssManaPoolBlock.Tier tier) {
        return BLOCKS.register(name, () -> new AbyssManaPoolBlock(BlockBehaviour.Properties.of()
                .mapColor(color).strength(14.0F, 40.0F).requiresCorrectToolForDrops()
                .lightLevel(state -> light).sound(SoundType.AMETHYST), tier));
    }

    private static RegistryObject<Block> functionalNexus(String name, MapColor color,
                                                          AbyssFunctionalNexusBlock.Kind kind) {
        return BLOCKS.register(name, () -> new AbyssFunctionalNexusBlock(BlockBehaviour.Properties.of()
                .mapColor(color).strength(8.0F, 18.0F).requiresCorrectToolForDrops()
                .lightLevel(state -> 9).sound(SoundType.AMETHYST).noOcclusion(), kind));
    }

    private static Block terrainBlock(MapColor color, float hardness, SoundType sound) {
        return new Block(BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(hardness, hardness + 1.5F)
                .requiresCorrectToolForDrops()
                .sound(sound));
    }

    private static Block altarBlock(MapColor color, Supplier<? extends EntityType<? extends Mob>> bossType,
                                    String bossTranslationKey, String requiredItemTranslationKey) {
        return new LayerAltarBlock(BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(12.0F, 80.0F)
                .requiresCorrectToolForDrops()
                .lightLevel(state -> 6)
                .sound(SoundType.NETHERITE_BLOCK),
                bossType, bossTranslationKey, requiredItemTranslationKey);
    }
}
