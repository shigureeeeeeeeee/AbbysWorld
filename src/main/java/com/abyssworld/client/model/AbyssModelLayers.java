package com.abyssworld.client.model;

import com.abyssworld.AbyssWorld;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public final class AbyssModelLayers {
    public static final ModelLayerLocation CRYSTALLINE_ABYSS_ARMOR = layer("crystalline_abyss_armor");
    public static final ModelLayerLocation SINGULARITY_ABYSS_ARMOR = layer("singularity_abyss_armor");
    public static final ModelLayerLocation ABYSS_HOUND = layer("abyss_hound");
    public static final ModelLayerLocation SHADOW_WALKER = layer("shadow_walker");
    public static final ModelLayerLocation MANA_LEECH = layer("mana_leech");
    public static final ModelLayerLocation CRYSTAL_PARASITE = layer("crystal_parasite");
    public static final ModelLayerLocation FALLEN_RESEARCHER = layer("fallen_researcher");
    public static final ModelLayerLocation BOUNDARY_WATCHER = layer("boundary_watcher");
    public static final ModelLayerLocation ABYSS_SOVEREIGN = layer("abyss_sovereign");
    public static final ModelLayerLocation ROTTEN_FOREST_GUARDIAN = layer("rotten_forest_guardian");
    public static final ModelLayerLocation GROVE_SENTINEL = layer("grove_sentinel");
    public static final ModelLayerLocation ASH_KING = layer("ash_king");
    public static final ModelLayerLocation FROSTBOUND_WARDEN = layer("frostbound_warden");
    public static final ModelLayerLocation FLESH_COLOSSUS = layer("flesh_colossus");
    public static final ModelLayerLocation VOID_ARCHON = layer("void_archon");
    public static final ModelLayerLocation FOREST_STALKER = layer("forest_stalker");
    public static final ModelLayerLocation ASH_REVENANT = layer("ash_revenant");
    public static final ModelLayerLocation FROST_MARAUDER = layer("frost_marauder");
    public static final ModelLayerLocation FLESH_HUNTER = layer("flesh_hunter");
    public static final ModelLayerLocation VOID_REAPER = layer("void_reaper");
    public static final ModelLayerLocation ROOTBOUND_THRALL = layer("rootbound_thrall");
    public static final ModelLayerLocation CINDER_IMP = layer("cinder_imp");
    public static final ModelLayerLocation GLACIAL_WRAITH = layer("glacial_wraith");
    public static final ModelLayerLocation MARROW_CRAWLER = layer("marrow_crawler");
    public static final ModelLayerLocation VOID_SHADE = layer("void_shade");

    private AbyssModelLayers() {
    }

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AbyssWorld.MODID, name), "main");
    }
}
