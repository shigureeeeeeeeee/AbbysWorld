package com.abyssworld.registry;

import com.abyssworld.AbyssWorld;
import com.abyssworld.worldgen.structure.ForgottenGroveCitadelPiece;
import com.abyssworld.worldgen.structure.ForgottenGroveCitadelStructure;
import com.abyssworld.worldgen.structure.BoundaryObservatoryStructure;
import com.abyssworld.worldgen.structure.InfestedVeinDungeonStructure;
import com.abyssworld.worldgen.structure.BoundaryStructurePiece;
import com.abyssworld.worldgen.structure.RiftfallCitadelPiece;
import com.abyssworld.worldgen.structure.RiftfallCitadelStructure;
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

    public static final RegistryObject<StructureType<BoundaryObservatoryStructure>> BOUNDARY_OBSERVATORY =
            STRUCTURE_TYPES.register("boundary_observatory",
                    () -> () -> BoundaryObservatoryStructure.CODEC);
    public static final RegistryObject<StructureType<InfestedVeinDungeonStructure>> INFESTED_VEIN_DUNGEON =
            STRUCTURE_TYPES.register("infested_vein_dungeon",
                    () -> () -> InfestedVeinDungeonStructure.CODEC);
    public static final RegistryObject<StructurePieceType> BOUNDARY_STRUCTURE_PIECE =
            PIECE_TYPES.register("boundary_structure_piece",
                    () -> (StructurePieceType.ContextlessType) BoundaryStructurePiece::new);
    public static final RegistryObject<StructureType<RiftfallCitadelStructure>> RIFTFALL_CITADEL =
            STRUCTURE_TYPES.register("riftfall_citadel",
                    () -> () -> RiftfallCitadelStructure.CODEC);
    public static final RegistryObject<StructurePieceType> RIFTFALL_CITADEL_PIECE =
            PIECE_TYPES.register("riftfall_citadel_piece",
                    () -> (StructurePieceType.ContextlessType) RiftfallCitadelPiece::new);

    private ModStructures() {
    }

    public static void register(IEventBus eventBus) {
        STRUCTURE_TYPES.register(eventBus);
        PIECE_TYPES.register(eventBus);
    }
}
