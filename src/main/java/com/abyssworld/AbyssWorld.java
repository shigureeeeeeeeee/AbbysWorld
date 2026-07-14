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
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

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
    }
}
