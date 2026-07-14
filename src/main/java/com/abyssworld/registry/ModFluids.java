package com.abyssworld.registry;

import com.abyssworld.AbyssWorld;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public final class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, AbyssWorld.MODID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, AbyssWorld.MODID);

    public static final RegistryObject<FluidType> ABYSSAL_ESSENCE_TYPE = FLUID_TYPES.register(
            "abyssal_essence", () -> new FluidType(FluidType.Properties.create()
                    .density(1350).viscosity(1800).temperature(330).lightLevel(7).rarity(Rarity.RARE)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)) {
                @Override public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        private final ResourceLocation still = ResourceLocation.fromNamespaceAndPath(
                                AbyssWorld.MODID, "block/abyssal_essence_still");
                        private final ResourceLocation flow = ResourceLocation.fromNamespaceAndPath(
                                AbyssWorld.MODID, "block/abyssal_essence_flow");
                        @Override public ResourceLocation getStillTexture() { return still; }
                        @Override public ResourceLocation getFlowingTexture() { return flow; }
                        @Override public int getTintColor() { return 0xFFB56CFF; }
                    });
                }
            });

    public static final RegistryObject<FlowingFluid> ABYSSAL_ESSENCE = FLUIDS.register(
            "abyssal_essence", () -> new ForgeFlowingFluid.Source(properties()));
    public static final RegistryObject<FlowingFluid> FLOWING_ABYSSAL_ESSENCE = FLUIDS.register(
            "flowing_abyssal_essence", () -> new ForgeFlowingFluid.Flowing(properties()));

    private static ForgeFlowingFluid.Properties properties() {
        return new ForgeFlowingFluid.Properties(ABYSSAL_ESSENCE_TYPE, ABYSSAL_ESSENCE, FLOWING_ABYSSAL_ESSENCE)
                .bucket(ModItems.ABYSSAL_ESSENCE_BUCKET).block(ModBlocks.ABYSSAL_ESSENCE_BLOCK)
                .slopeFindDistance(3).levelDecreasePerBlock(2).tickRate(10).explosionResistance(20.0F);
    }

    public static void register(IEventBus bus) {
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
    }

    private ModFluids() {}
}
