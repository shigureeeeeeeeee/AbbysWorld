package com.abyssworld.client;

import com.abyssworld.AbyssWorld;
import com.abyssworld.client.model.AbyssModelLayers;
import com.abyssworld.client.model.AbyssArmorModel;
import com.abyssworld.client.model.AbyssMonsterModel;
import com.abyssworld.client.renderer.AbyssEntityRenderers;
import com.abyssworld.client.screen.AbyssResourceHarvesterScreen;
import com.abyssworld.client.screen.AbyssManaMachineScreen;
import com.abyssworld.client.screen.AbyssStorageTerminalScreen;
import com.abyssworld.client.screen.LeylineMinerScreen;
import com.abyssworld.client.screen.SpellbookScreen;
import com.abyssworld.registry.ModEntities;
import com.abyssworld.registry.ModMenus;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AbyssWorld.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientSetup {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.ABYSS_RESOURCE_HARVESTER.get(), AbyssResourceHarvesterScreen::new);
            MenuScreens.register(ModMenus.ABYSS_MANA_MACHINE.get(), AbyssManaMachineScreen::new);
            MenuScreens.register(ModMenus.ABYSS_STORAGE_TERMINAL.get(), AbyssStorageTerminalScreen::new);
            MenuScreens.register(ModMenus.LEYLINE_MINER.get(), LeylineMinerScreen::new);
            MenuScreens.register(ModMenus.SPELLBOOK.get(), SpellbookScreen::new);
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ABYSS_SOVEREIGN.get(),
                context -> new AbyssEntityRenderers.AbyssMonster<>(context, texture("abyss_sovereign"),
                        AbyssModelLayers.ABYSS_SOVEREIGN, 1.05F));
        event.registerEntityRenderer(ModEntities.ROTTEN_FOREST_GUARDIAN.get(),
                context -> new AbyssEntityRenderers.AbyssMonster<>(context, texture("rotten_forest_guardian"),
                        AbyssModelLayers.ROTTEN_FOREST_GUARDIAN, 1.0F));
        event.registerEntityRenderer(ModEntities.GROVE_SENTINEL.get(),
                context -> new AbyssEntityRenderers.AbyssMonster<>(context, texture("grove_sentinel"),
                        AbyssModelLayers.GROVE_SENTINEL, 1.02F));
        event.registerEntityRenderer(ModEntities.ASH_KING.get(),
                context -> new AbyssEntityRenderers.AbyssMonster<>(context, texture("ash_king"),
                        AbyssModelLayers.ASH_KING, 1.05F));
        event.registerEntityRenderer(ModEntities.FROSTBOUND_WARDEN.get(),
                context -> new AbyssEntityRenderers.AbyssMonster<>(context, texture("frostbound_warden"),
                        AbyssModelLayers.FROSTBOUND_WARDEN, 1.0F));
        event.registerEntityRenderer(ModEntities.FLESH_COLOSSUS.get(),
                context -> new AbyssEntityRenderers.AbyssMonster<>(context, texture("flesh_colossus"),
                        AbyssModelLayers.FLESH_COLOSSUS, 0.95F));
        event.registerEntityRenderer(ModEntities.VOID_ARCHON.get(),
                context -> new AbyssEntityRenderers.AbyssMonster<>(context, texture("void_archon"),
                        AbyssModelLayers.VOID_ARCHON, 1.0F));
        event.registerEntityRenderer(ModEntities.FOREST_STALKER.get(),
                context -> new AbyssEntityRenderers.AbyssMonster<>(context, texture("forest_stalker"),
                        AbyssModelLayers.FOREST_STALKER, 0.92F));
        event.registerEntityRenderer(ModEntities.ASH_REVENANT.get(),
                context -> new AbyssEntityRenderers.AbyssMonster<>(context, texture("ash_revenant"),
                        AbyssModelLayers.ASH_REVENANT, 1.0F));
        event.registerEntityRenderer(ModEntities.FROST_MARAUDER.get(),
                context -> new AbyssEntityRenderers.AbyssMonster<>(context, texture("frost_marauder"),
                        AbyssModelLayers.FROST_MARAUDER, 0.96F));
        event.registerEntityRenderer(ModEntities.FLESH_HUNTER.get(),
                context -> new AbyssEntityRenderers.AbyssMonster<>(context, texture("flesh_hunter"),
                        AbyssModelLayers.FLESH_HUNTER, 0.78F));
        event.registerEntityRenderer(ModEntities.VOID_REAPER.get(),
                context -> new AbyssEntityRenderers.AbyssMonster<>(context, texture("void_reaper"),
                        AbyssModelLayers.VOID_REAPER, 1.0F));
        event.registerEntityRenderer(ModEntities.ROOTBOUND_THRALL.get(),
                context -> new AbyssEntityRenderers.AbyssMonster<>(context, texture("rootbound_thrall"),
                        AbyssModelLayers.ROOTBOUND_THRALL, 0.9F));
        event.registerEntityRenderer(ModEntities.CINDER_IMP.get(),
                context -> new AbyssEntityRenderers.AbyssMonster<>(context, texture("cinder_imp"),
                        AbyssModelLayers.CINDER_IMP, 0.72F));
        event.registerEntityRenderer(ModEntities.GLACIAL_WRAITH.get(),
                context -> new AbyssEntityRenderers.AbyssMonster<>(context, texture("glacial_wraith"),
                        AbyssModelLayers.GLACIAL_WRAITH, 0.92F));
        event.registerEntityRenderer(ModEntities.MARROW_CRAWLER.get(),
                context -> new AbyssEntityRenderers.AbyssMonster<>(context, texture("marrow_crawler"),
                        AbyssModelLayers.MARROW_CRAWLER, 0.76F));
        event.registerEntityRenderer(ModEntities.VOID_SHADE.get(),
                context -> new AbyssEntityRenderers.AbyssMonster<>(context, texture("void_shade"),
                        AbyssModelLayers.VOID_SHADE, 0.9F));
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(AbyssModelLayers.CRYSTALLINE_ABYSS_ARMOR,
                AbyssArmorModel::crystallineLayer);
        event.registerLayerDefinition(AbyssModelLayers.SINGULARITY_ABYSS_ARMOR,
                AbyssArmorModel::singularityLayer);
        event.registerLayerDefinition(AbyssModelLayers.ABYSS_SOVEREIGN, AbyssMonsterModel::abyssSovereignLayer);
        event.registerLayerDefinition(AbyssModelLayers.ROTTEN_FOREST_GUARDIAN,
                AbyssMonsterModel::rottenForestGuardianLayer);
        event.registerLayerDefinition(AbyssModelLayers.GROVE_SENTINEL, AbyssMonsterModel::forestStalkerLayer);
        event.registerLayerDefinition(AbyssModelLayers.ASH_KING, AbyssMonsterModel::ashKingLayer);
        event.registerLayerDefinition(AbyssModelLayers.FROSTBOUND_WARDEN, AbyssMonsterModel::frostboundWardenLayer);
        event.registerLayerDefinition(AbyssModelLayers.FLESH_COLOSSUS, AbyssMonsterModel::fleshColossusLayer);
        event.registerLayerDefinition(AbyssModelLayers.VOID_ARCHON, AbyssMonsterModel::voidArchonLayer);
        event.registerLayerDefinition(AbyssModelLayers.FOREST_STALKER, AbyssMonsterModel::forestStalkerLayer);
        event.registerLayerDefinition(AbyssModelLayers.ASH_REVENANT, AbyssMonsterModel::ashRevenantLayer);
        event.registerLayerDefinition(AbyssModelLayers.FROST_MARAUDER, AbyssMonsterModel::frostMarauderLayer);
        event.registerLayerDefinition(AbyssModelLayers.FLESH_HUNTER, AbyssMonsterModel::fleshHunterLayer);
        event.registerLayerDefinition(AbyssModelLayers.VOID_REAPER, AbyssMonsterModel::voidReaperLayer);
        event.registerLayerDefinition(AbyssModelLayers.ROOTBOUND_THRALL, AbyssMonsterModel::rootboundThrallLayer);
        event.registerLayerDefinition(AbyssModelLayers.CINDER_IMP, AbyssMonsterModel::cinderImpLayer);
        event.registerLayerDefinition(AbyssModelLayers.GLACIAL_WRAITH, AbyssMonsterModel::glacialWraithLayer);
        event.registerLayerDefinition(AbyssModelLayers.MARROW_CRAWLER, AbyssMonsterModel::marrowCrawlerLayer);
        event.registerLayerDefinition(AbyssModelLayers.VOID_SHADE, AbyssMonsterModel::voidShadeLayer);
    }

    private static ResourceLocation texture(String name) {
        return ResourceLocation.fromNamespaceAndPath(AbyssWorld.MODID, "textures/entity/" + name + ".png");
    }
}
