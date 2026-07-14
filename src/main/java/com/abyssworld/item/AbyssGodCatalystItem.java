package com.abyssworld.item;

import com.abyssworld.entity.AbyssSovereignEntity;
import com.abyssworld.registry.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 深淵神核触媒 - 深淵世界で使用すると深淵覇王を召喚する。
 */
public class AbyssGodCatalystItem extends Item {

    public AbyssGodCatalystItem() {
        super(new Item.Properties().rarity(Rarity.EPIC).fireResistant().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return trySummon(context.getLevel(), context.getPlayer(), context.getHand(),
                context.getClickedPos().above()) ? InteractionResult.sidedSuccess(context.getLevel().isClientSide)
                : InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (trySummon(level, player, hand, player.blockPosition())) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return InteractionResultHolder.pass(stack);
    }

    private boolean trySummon(Level level, @Nullable Player player, InteractionHand hand, BlockPos spawnPos) {
        if (level.isClientSide || player == null || !AbyssKeyItem.isAbyssDimension(level.dimension())) {
            if (player != null && !level.isClientSide && !AbyssKeyItem.isAbyssDimension(level.dimension())) {
                player.displayClientMessage(Component.translatable("item.abyssworld.abyss_god_catalyst.wrong_dimension")
                        .withStyle(ChatFormatting.RED), true);
            }
            return false;
        }

        if (level.getEntitiesOfClass(AbyssSovereignEntity.class,
                new net.minecraft.world.phys.AABB(spawnPos).inflate(96.0D)).stream()
                .anyMatch(e -> e.isAlive())) {
            player.displayClientMessage(Component.translatable("item.abyssworld.abyss_god_catalyst.already_summoned")
                    .withStyle(ChatFormatting.YELLOW), true);
            return false;
        }

        AbyssSovereignEntity boss = ModEntities.ABYSS_SOVEREIGN.get().create(level);
        if (boss == null) {
            return false;
        }

        boss.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY() + 1.0D, spawnPos.getZ() + 0.5D,
                level.random.nextFloat() * 360.0F, 0.0F);
        level.addFreshEntity(boss);
        level.playSound(null, spawnPos, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.5F, 0.6F);

        if (level instanceof ServerLevel serverLevel && serverLevel.getServer() != null) {
            serverLevel.getServer().getPlayerList().broadcastSystemMessage(
                    Component.translatable("item.abyssworld.abyss_god_catalyst.summoned")
                            .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD),
                    false);
        }

        if (!player.getAbilities().instabuild) {
            player.getItemInHand(hand).shrink(1);
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.abyssworld.abyss_god_catalyst.desc").withStyle(ChatFormatting.GRAY));
    }
}
