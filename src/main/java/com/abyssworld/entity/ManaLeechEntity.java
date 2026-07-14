package com.abyssworld.entity;

import com.abyssworld.magic.AbyssMagic;
import com.abyssworld.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ManaLeechEntity extends AbyssMonsterEntity {
    public ManaLeechEntity(EntityType<? extends ManaLeechEntity> type, Level level) {
        super(type, level);
        xpReward = 14;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbyssMonsterEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 26.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.36D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 36.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && random.nextInt(4) == 0) {
            level().addParticle(ParticleTypes.ENCHANT, getRandomX(0.7D),
                    getY() + random.nextDouble() * getBbHeight(), getRandomZ(0.7D),
                    0.0D, 0.03D, 0.0D);
        }
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean hit = super.doHurtTarget(entity);
        if (hit && entity instanceof ServerPlayer player) {
            int drained = Math.min(20, AbyssMagic.mana(player));
            if (drained > 0) {
                AbyssMagic.consumeMana(player, drained);
                heal(Math.max(2.0F, drained * 0.3F));
            }
            AbyssMagic.addStrain(player, 6);
            player.displayClientMessage(Component.translatable("entity.abyssworld.mana_leech.drained", drained)
                    .withStyle(ChatFormatting.DARK_PURPLE), true);
        }
        return hit;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        if (random.nextFloat() < 0.45F + looting * 0.08F) {
            spawnAtLocation(new ItemStack(ModItems.ABYSS_CRYSTAL.get()));
        }
    }
}
