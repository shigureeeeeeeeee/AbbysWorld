package com.abyssworld.registry;

import com.abyssworld.AbyssWorld;
import com.abyssworld.worldgen.LayerAltarFeature;
import com.abyssworld.worldgen.BoundaryScarFeature;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, AbyssWorld.MODID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> FORGOTTEN_FOREST_ALTAR =
            FEATURES.register("forgotten_forest_altar",
                    () -> new LayerAltarFeature(NoneFeatureConfiguration.CODEC,
                            ModBlocks.FORGOTTEN_FOREST_ALTAR,
                            Blocks.MOSSY_COBBLESTONE.defaultBlockState(),
                            Blocks.MOSSY_STONE_BRICKS.defaultBlockState()));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ASH_WASTELAND_ALTAR =
            FEATURES.register("ash_wasteland_altar",
                    () -> new LayerAltarFeature(NoneFeatureConfiguration.CODEC,
                            ModBlocks.ASH_WASTELAND_ALTAR,
                            Blocks.POLISHED_BLACKSTONE.defaultBlockState(),
                            Blocks.BASALT.defaultBlockState()));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> FROZEN_CAVERN_ALTAR =
            FEATURES.register("frozen_cavern_altar",
                    () -> new LayerAltarFeature(NoneFeatureConfiguration.CODEC,
                            ModBlocks.FROZEN_CAVERN_ALTAR,
                            Blocks.PACKED_ICE.defaultBlockState(),
                            Blocks.BLUE_ICE.defaultBlockState()));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> FLESH_MINE_ALTAR =
            FEATURES.register("flesh_mine_altar",
                    () -> new LayerAltarFeature(NoneFeatureConfiguration.CODEC,
                            ModBlocks.FLESH_MINE_ALTAR,
                            Blocks.NETHER_BRICKS.defaultBlockState(),
                            Blocks.CRIMSON_NYLIUM.defaultBlockState()));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> VOID_CITY_ALTAR =
            FEATURES.register("void_city_altar",
                    () -> new LayerAltarFeature(NoneFeatureConfiguration.CODEC,
                            ModBlocks.VOID_CITY_ALTAR,
                            Blocks.END_STONE_BRICKS.defaultBlockState(),
                            Blocks.PURPUR_BLOCK.defaultBlockState()));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> BOUNDARY_SCAR_DECORATION =
            FEATURES.register("boundary_scar_decoration",
                    () -> new BoundaryScarFeature(NoneFeatureConfiguration.CODEC));
}
