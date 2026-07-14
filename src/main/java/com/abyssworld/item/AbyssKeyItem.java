package com.abyssworld.item;

import com.abyssworld.AbyssWorld;
import com.abyssworld.registry.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 深淵の鍵 - 使用すると現世と深淵階層を行き来する。
 */
public class AbyssKeyItem extends Item {
    public static final ResourceKey<Level> ABYSS =
            ResourceKey.create(Registries.DIMENSION,
                    ResourceLocation.fromNamespaceAndPath(AbyssWorld.MODID, "abyss"));
    public static final ResourceKey<Level> FORGOTTEN_FOREST =
            layer("forgotten_forest");
    public static final ResourceKey<Level> ASH_WASTELAND =
            layer("ash_wasteland");
    public static final ResourceKey<Level> FROZEN_CAVERN =
            layer("frozen_cavern");
    public static final ResourceKey<Level> FLESH_MINE =
            layer("flesh_mine");
    public static final ResourceKey<Level> VOID_CITY =
            layer("void_city");

    private static final List<ResourceKey<Level>> LAYERS = List.of(
            FORGOTTEN_FOREST,
            ASH_WASTELAND,
            FROZEN_CAVERN,
            FLESH_MINE,
            VOID_CITY);

    public AbyssKeyItem() {
        super(new Item.Properties().rarity(Rarity.RARE).stacksTo(1));
    }

    private static ResourceKey<Level> layer(String name) {
        return ResourceKey.create(Registries.DIMENSION,
                ResourceLocation.fromNamespaceAndPath(AbyssWorld.MODID, name));
    }

    public static boolean isAbyssDimension(ResourceKey<Level> dimension) {
        return dimension.equals(ABYSS) || LAYERS.contains(dimension);
    }

    public static Component dimensionName(ResourceKey<Level> dimension) {
        String path = dimension.location().getPath();
        return Component.translatable("dimension." + AbyssWorld.MODID + "." + path);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.success(stack);
        }

        ResourceKey<Level> targetKey = targetDimension(level.dimension(), player.isShiftKeyDown());
        ServerLevel target = serverPlayer.serverLevel().getServer().getLevel(targetKey);
        if (target == null) {
            player.displayClientMessage(
                    Component.translatable("item.abyssworld.abyss_key.missing_dimension")
                            .withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        BlockPos pos = serverPlayer.blockPosition();
        target.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
        BlockPos top = target.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
        if (top.getY() <= target.getMinBuildHeight() + 1) {
            top = new BlockPos(pos.getX(), 72, pos.getZ());
        }
        BlockPos arrival = prepareArrival(target, targetKey, top);

        level.playSound(null, pos, SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.5F, 1.2F);
        serverPlayer.teleportTo(target, arrival.getX() + 0.5, arrival.getY(), arrival.getZ() + 0.5,
                serverPlayer.getYRot(), serverPlayer.getXRot());
        target.playSound(null, arrival, SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.5F, 1.2F);
        target.sendParticles(ParticleTypes.REVERSE_PORTAL,
                arrival.getX() + 0.5D, arrival.getY() + 0.8D, arrival.getZ() + 0.5D,
                48, 1.2D, 0.7D, 1.2D, 0.03D);

        Component message = targetKey.equals(Level.OVERWORLD)
                ? Component.translatable("item.abyssworld.abyss_key.to_overworld")
                : Component.translatable("item.abyssworld.abyss_key.to_layer", dimensionName(targetKey));
        serverPlayer.displayClientMessage(message.copy().withStyle(ChatFormatting.DARK_PURPLE), true);
        player.getCooldowns().addCooldown(this, 100);
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    private static BlockPos prepareArrival(ServerLevel target, ResourceKey<Level> targetKey, BlockPos top) {
        if (!isAbyssDimension(targetKey)) {
            return top.above();
        }

        int platformY = Math.max(target.getMinBuildHeight() + 4,
                Math.min(target.getMaxBuildHeight() - 6, top.getY()));
        BlockPos center = new BlockPos(top.getX(), platformY, top.getZ());
        BlockState platform = platformBlock(targetKey);

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos floor = center.offset(dx, 0, dz);
                if (!target.getBlockState(floor).is(Blocks.BEDROCK)) {
                    target.setBlock(floor, platform, 3);
                }
                for (int dy = 1; dy <= 3; dy++) {
                    BlockPos clear = floor.above(dy);
                    if (!target.getBlockState(clear).is(Blocks.BEDROCK)) {
                        target.setBlock(clear, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
        target.setBlock(center, arrivalCenterBlock(targetKey), 3);
        return center.above();
    }

    private static BlockState platformBlock(ResourceKey<Level> targetKey) {
        if (targetKey.equals(FORGOTTEN_FOREST)) {
            return ModBlocks.FORGOTTEN_STONE.get().defaultBlockState();
        }
        if (targetKey.equals(ASH_WASTELAND)) {
            return ModBlocks.ASH_STONE.get().defaultBlockState();
        }
        if (targetKey.equals(FROZEN_CAVERN)) {
            return ModBlocks.FROZEN_STONE.get().defaultBlockState();
        }
        if (targetKey.equals(FLESH_MINE)) {
            return ModBlocks.FLESH_STONE.get().defaultBlockState();
        }
        if (targetKey.equals(VOID_CITY)) {
            return ModBlocks.VOID_STONE.get().defaultBlockState();
        }
        return ModBlocks.ABYSS_STONE.get().defaultBlockState();
    }

    private static BlockState arrivalCenterBlock(ResourceKey<Level> targetKey) {
        if (targetKey.equals(FORGOTTEN_FOREST)) {
            return ModBlocks.FORGOTTEN_SOIL.get().defaultBlockState();
        }
        if (targetKey.equals(ASH_WASTELAND)) {
            return ModBlocks.ASH_CRUST.get().defaultBlockState();
        }
        if (targetKey.equals(FROZEN_CAVERN)) {
            return ModBlocks.FROZEN_SURFACE.get().defaultBlockState();
        }
        if (targetKey.equals(FLESH_MINE)) {
            return ModBlocks.FLESH_MASS.get().defaultBlockState();
        }
        if (targetKey.equals(VOID_CITY)) {
            return ModBlocks.VOID_SURFACE.get().defaultBlockState();
        }
        return ModBlocks.ABYSS_STONE.get().defaultBlockState();
    }

    private static ResourceKey<Level> targetDimension(ResourceKey<Level> current, boolean returning) {
        if (!isAbyssDimension(current)) {
            return FORGOTTEN_FOREST;
        }
        if (returning) {
            return Level.OVERWORLD;
        }
        int index = LAYERS.indexOf(current);
        if (index < 0) {
            return FORGOTTEN_FOREST;
        }
        return LAYERS.get((index + 1) % LAYERS.size());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.abyssworld.abyss_key.desc").withStyle(ChatFormatting.GRAY));
    }
}
