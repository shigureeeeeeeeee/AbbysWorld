package com.abyssworld.registry;

import com.abyssworld.AbyssWorld;
import com.abyssworld.magic.AbyssManaPoolBlockEntity;
import com.abyssworld.magic.AbyssManaHeaterBlockEntity;
import com.abyssworld.magic.AbyssOreCrusherBlockEntity;
import com.abyssworld.magic.AbyssOreRefineryBlockEntity;
import com.abyssworld.magic.AbyssOreAmplifierBlockEntity;
import com.abyssworld.magic.AbyssResourceHarvesterBlockEntity;
import com.abyssworld.ritual.WorldReconstructionFurnaceBlockEntity;
import com.abyssworld.magic.AbyssItemConduitBlockEntity;
import com.abyssworld.magic.AbyssEssenceExtractorBlockEntity;
import com.abyssworld.magic.AbyssFluidConduitBlockEntity;
import com.abyssworld.magic.AbyssFluidReservoirBlockEntity;
import com.abyssworld.magic.AbyssStorageTerminalBlockEntity;
import com.abyssworld.magic.LeylineMinerBlockEntity;
import com.abyssworld.magic.AbyssFunctionalNexusBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AbyssWorld.MODID);

    public static final RegistryObject<BlockEntityType<WorldReconstructionFurnaceBlockEntity>> WORLD_RECONSTRUCTION_FURNACE =
            BLOCK_ENTITIES.register("world_reconstruction_furnace",
                    () -> BlockEntityType.Builder.of(
                            WorldReconstructionFurnaceBlockEntity::new,
                            ModBlocks.WORLD_RECONSTRUCTION_FURNACE.get()).build(null));

    public static final RegistryObject<BlockEntityType<AbyssManaPoolBlockEntity>> ABYSS_MANA_POOL =
            BLOCK_ENTITIES.register("abyss_mana_pool",
                    () -> BlockEntityType.Builder.of(
                            AbyssManaPoolBlockEntity::new,
                            ModBlocks.ABYSS_MANA_POOL.get(),
                            ModBlocks.ABYSS_MANA_CONDENSER.get(),
                            ModBlocks.VERDANT_MANA_BLOOM.get(),
                            ModBlocks.INFERNO_MANA_CRUCIBLE.get(),
                            ModBlocks.CRYO_MANA_SIPHON.get(),
                            ModBlocks.VOID_MANA_TAP.get(),
                            ModBlocks.ABYSS_MANA_RESERVOIR.get(),
                            ModBlocks.WIRELESS_MANA_RELAY.get(),
                            ModBlocks.MANA_VORTEX_REACTOR.get(),
                            ModBlocks.CREATIVE_MANA_SOURCE.get()).build(null));

    public static final RegistryObject<BlockEntityType<AbyssItemConduitBlockEntity>> ABYSS_ITEM_CONDUIT =
            BLOCK_ENTITIES.register("abyss_item_conduit", () -> BlockEntityType.Builder.of(
                    AbyssItemConduitBlockEntity::new, ModBlocks.ABYSS_ITEM_CONDUIT.get()).build(null));
    public static final RegistryObject<BlockEntityType<AbyssFluidConduitBlockEntity>> ABYSS_FLUID_CONDUIT =
            BLOCK_ENTITIES.register("abyss_fluid_conduit", () -> BlockEntityType.Builder.of(
                    AbyssFluidConduitBlockEntity::new, ModBlocks.ABYSS_FLUID_CONDUIT.get()).build(null));
    public static final RegistryObject<BlockEntityType<AbyssFluidReservoirBlockEntity>> ABYSS_FLUID_RESERVOIR =
            BLOCK_ENTITIES.register("abyss_essence_reservoir", () -> BlockEntityType.Builder.of(
                    AbyssFluidReservoirBlockEntity::new, ModBlocks.ABYSS_ESSENCE_RESERVOIR.get()).build(null));
    public static final RegistryObject<BlockEntityType<AbyssStorageTerminalBlockEntity>> ABYSS_STORAGE_TERMINAL =
            BLOCK_ENTITIES.register("abyss_storage_terminal", () -> BlockEntityType.Builder.of(
                    AbyssStorageTerminalBlockEntity::new, ModBlocks.ABYSS_STORAGE_TERMINAL.get()).build(null));
    public static final RegistryObject<BlockEntityType<AbyssFunctionalNexusBlockEntity>> ABYSS_FUNCTIONAL_NEXUS =
            BLOCK_ENTITIES.register("abyss_functional_nexus", () -> BlockEntityType.Builder.of(
                    AbyssFunctionalNexusBlockEntity::new, ModBlocks.VERDANT_NEXUS.get(),
                    ModBlocks.GATHERING_NEXUS.get(), ModBlocks.WARDING_NEXUS.get()).build(null));

    public static final RegistryObject<BlockEntityType<AbyssResourceHarvesterBlockEntity>> ABYSS_RESOURCE_HARVESTER =
            BLOCK_ENTITIES.register("abyss_resource_harvester",
                    () -> BlockEntityType.Builder.of(
                            AbyssResourceHarvesterBlockEntity::new,
                            ModBlocks.ABYSS_RESOURCE_HARVESTER.get()).build(null));
    public static final RegistryObject<BlockEntityType<LeylineMinerBlockEntity>> LEYLINE_MINER =
            BLOCK_ENTITIES.register("leyline_miner", () -> BlockEntityType.Builder.of(
                    LeylineMinerBlockEntity::new, ModBlocks.LEYLINE_MINER.get()).build(null));

    public static final RegistryObject<BlockEntityType<AbyssManaHeaterBlockEntity>> ABYSS_MANA_HEATER =
            BLOCK_ENTITIES.register("abyss_mana_heater",
                    () -> BlockEntityType.Builder.of(
                            AbyssManaHeaterBlockEntity::new,
                            ModBlocks.ABYSS_MANA_HEATER.get()).build(null));

    public static final RegistryObject<BlockEntityType<AbyssEssenceExtractorBlockEntity>> ABYSS_ESSENCE_EXTRACTOR =
            BLOCK_ENTITIES.register("abyss_essence_extractor", () -> BlockEntityType.Builder.of(
                    AbyssEssenceExtractorBlockEntity::new, ModBlocks.ABYSS_ESSENCE_EXTRACTOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<AbyssOreCrusherBlockEntity>> ABYSS_ORE_CRUSHER =
            BLOCK_ENTITIES.register("abyss_ore_crusher",
                    () -> BlockEntityType.Builder.of(
                            AbyssOreCrusherBlockEntity::new,
                            ModBlocks.ABYSS_ORE_CRUSHER.get()).build(null));

    public static final RegistryObject<BlockEntityType<AbyssOreRefineryBlockEntity>> ABYSS_ORE_REFINERY =
            BLOCK_ENTITIES.register("abyss_ore_refinery",
                    () -> BlockEntityType.Builder.of(
                            AbyssOreRefineryBlockEntity::new,
                            ModBlocks.ABYSS_ORE_REFINERY.get()).build(null));

    public static final RegistryObject<BlockEntityType<AbyssOreAmplifierBlockEntity>> ABYSS_ORE_AMPLIFIER =
            BLOCK_ENTITIES.register("abyss_ore_amplifier",
                    () -> BlockEntityType.Builder.of(
                            AbyssOreAmplifierBlockEntity::new,
                            ModBlocks.ABYSS_ORE_AMPLIFIER.get(),
                            ModBlocks.ABYSS_ORE_CRYSTALLIZER.get(),
                            ModBlocks.ABYSS_ORE_RESONATOR.get(),
                            ModBlocks.ABYSS_ORE_SINGULARITY_SEPARATOR.get()).build(null));
}
