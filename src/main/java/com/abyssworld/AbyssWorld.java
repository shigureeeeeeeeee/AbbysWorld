package com.abyssworld;

import com.abyssworld.registry.ModBlockEntities;
import com.abyssworld.registry.ModBlocks;
import com.abyssworld.registry.ModCreativeTabs;
import com.abyssworld.registry.ModEntities;
import com.abyssworld.registry.ModFeatures;
import com.abyssworld.registry.ModItems;
import com.abyssworld.registry.ModMenus;
import com.abyssworld.registry.ModStructures;
import com.abyssworld.registry.ModFluids;
import com.abyssworld.worldgen.AbyssOverworldRegion;
import com.abyssworld.worldgen.AbyssSurfaceRules;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

@Mod(AbyssWorld.MODID)
public class AbyssWorld {
    public static final String MODID = "abyssworld";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AbyssWorld() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modEventBus);
        ModFluids.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModFeatures.FEATURES.register(modEventBus);
        ModStructures.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Regions.register(new AbyssOverworldRegion(
                    ResourceLocation.fromNamespaceAndPath(MODID, "boundary_scar_region"), 4));
            SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD,
                    MODID, AbyssSurfaceRules.makeRules());
        });
    }
}
