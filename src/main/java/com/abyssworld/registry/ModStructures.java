package com.abyssworld.registry;

import com.abyssworld.AbyssWorld;
import com.abyssworld.worldgen.structure.ForgottenGroveCitadelPiece;
import com.abyssworld.worldgen.structure.ForgottenGroveCitadelStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModStructures {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, AbyssWorld.MODID);
    public static final DeferredRegister<StructurePieceType> PIECE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, AbyssWorld.MODID);

    public static final RegistryObject<StructureType<ForgottenGroveCitadelStructure>> FORGOTTEN_GROVE_CITADEL =
            STRUCTURE_TYPES.register("forgotten_grove_citadel",
                    () -> () -> ForgottenGroveCitadelStructure.CODEC);
    public static final RegistryObject<StructurePieceType> FORGOTTEN_GROVE_CITADEL_PIECE =
            PIECE_TYPES.register("forgotten_grove_citadel_piece",
                    () -> (StructurePieceType.ContextlessType) ForgottenGroveCitadelPiece::new);

    private ModStructures() {
    }

    public static void register(IEventBus eventBus) {
        STRUCTURE_TYPES.register(eventBus);
        PIECE_TYPES.register(eventBus);
    }
}
