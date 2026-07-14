package com.abyssworld.registry;

import com.abyssworld.AbyssWorld;
import com.abyssworld.menu.AbyssResourceHarvesterMenu;
import com.abyssworld.menu.AbyssManaMachineMenu;
import com.abyssworld.menu.AbyssStorageTerminalMenu;
import com.abyssworld.menu.LeylineMinerMenu;
import com.abyssworld.menu.SpellbookMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, AbyssWorld.MODID);

    public static final RegistryObject<MenuType<AbyssResourceHarvesterMenu>> ABYSS_RESOURCE_HARVESTER =
            MENUS.register("abyss_resource_harvester", () -> IForgeMenuType.create(AbyssResourceHarvesterMenu::new));
    public static final RegistryObject<MenuType<AbyssManaMachineMenu>> ABYSS_MANA_MACHINE =
            MENUS.register("abyss_mana_machine", () -> IForgeMenuType.create(AbyssManaMachineMenu::new));
    public static final RegistryObject<MenuType<AbyssStorageTerminalMenu>> ABYSS_STORAGE_TERMINAL =
            MENUS.register("abyss_storage_terminal", () -> IForgeMenuType.create(AbyssStorageTerminalMenu::new));
    public static final RegistryObject<MenuType<LeylineMinerMenu>> LEYLINE_MINER =
            MENUS.register("leyline_miner", () -> IForgeMenuType.create(LeylineMinerMenu::new));
    public static final RegistryObject<MenuType<SpellbookMenu>> SPELLBOOK =
            MENUS.register("spellbook", () -> IForgeMenuType.create(SpellbookMenu::new));

    private ModMenus() {
    }
}
