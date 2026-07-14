package com.abyssworld.magic;

import com.abyssworld.item.SpellGlyphItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class SpellCasting {
    private static final int MAX_AREA_BLOCKS = 64;

    private SpellCasting() {
    }

    public static void cast(ServerPlayer player, ItemStack book) {
        int spell = SpellbookData.selectedSpell(book);
        SpellGlyphItem.Glyph form = SpellbookData.glyph(book, spell, SpellbookData.FORM_OFFSET);
        SpellGlyphItem.Glyph effect = SpellbookData.glyph(book, spell, SpellbookData.EFFECT_OFFSET);
        if (form == null || effect == null) {
            message(player, "item.abyssworld.abyss_spellbook.incomplete", ChatFormatting.RED);
            return;
        }

        List<SpellGlyphItem.Glyph> augments = new ArrayList<>(2);
        addAugment(book, spell, SpellbookData.FIRST_AUGMENT_OFFSET, augments);
        addAugment(book, spell, SpellbookData.SECOND_AUGMENT_OFFSET, augments);
        SpellStats stats = SpellStats.create(form, effect, augments);
        if (!consumeMana(player, stats.manaCost())) {
            AbyssMagic.addStrain(player, 3);
            player.displayClientMessage(Component.translatable(
                    "item.abyssworld.abyss_spellbook.no_mana", stats.manaCost())
                    .withStyle(ChatFormatting.RED), true);
            return;
        }

        AbyssMagic.addStrain(player, stats.strain());
        boolean success = execute(player.serverLevel(), player, book, form, effect, stats);
        if (!success) {
            refundMana(player, stats.manaCost() / 2);
            message(player, "item.abyssworld.abyss_spellbook.no_target", ChatFormatting.YELLOW);
            return;
        }

        player.getCooldowns().addCooldown(book.getItem(), Math.max(8, 18 + stats.strain()));
        player.displayClientMessage(Component.translatable("item.abyssworld.abyss_spellbook.cast",
                stats.manaCost(), stats.strain(), AbyssMagic.strain(player))
                .withStyle(ChatFormatting.DARK_PURPLE), true);
    }

    private static boolean execute(ServerLevel level, ServerPlayer player, ItemStack book,
                                   SpellGlyphItem.Glyph form, SpellGlyphItem.Glyph effect,
                                   SpellStats stats) {
        Vec3 start = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Impact impact;
        if (form == SpellGlyphItem.Glyph.FORM_SELF) {
            impact = new Impact(player.position(), player, player.blockPosition(), Direction.UP);
        } else {
            impact = trace(level, player, start, look, stats.range());
            if (impact == null) {
                return false;
            }
            particleBeam(level, start, impact.position(), effectParticle(effect));
        }

        boolean applied = form == SpellGlyphItem.Glyph.FORM_AREA
                ? applyArea(level, player, book, effect, impact, stats)
                : applySingle(level, player, book, effect, impact, stats, form == SpellGlyphItem.Glyph.FORM_SELF);
        if (applied) {
            level.sendParticles(effectParticle(effect), impact.position().x, impact.position().y,
                    impact.position().z, form == SpellGlyphItem.Glyph.FORM_AREA ? 42 : 18,
                    stats.area() * 0.35D, 0.45D, stats.area() * 0.35D, 0.04D);
            level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE,
                    SoundSource.PLAYERS, 0.9F, 0.65F + player.getRandom().nextFloat() * 0.25F);
        }
        return applied;
    }

    private static boolean applySingle(ServerLevel level, ServerPlayer player, ItemStack book,
                                       SpellGlyphItem.Glyph effect, Impact impact,
                                       SpellStats stats, boolean selfForm) {
        LivingEntity target = impact.entity();
        switch (effect) {
            case EFFECT_FIRE -> {
                if (selfForm) {
                    player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,
                            stats.durationTicks(), Math.max(0, stats.power() - 1)));
                } else if (target != null) {
                    target.setSecondsOnFire(4 + stats.durationTicks() / 40);
                    target.hurt(player.damageSources().playerAttack(player), 3.0F + stats.power() * 2.0F);
                } else if (level.isEmptyBlock(impact.blockPos().relative(impact.direction()))) {
                    level.setBlockAndUpdate(impact.blockPos().relative(impact.direction()), Blocks.FIRE.defaultBlockState());
                } else {
                    return false;
                }
            }
            case EFFECT_FROST -> {
                if (selfForm) {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,
                            stats.durationTicks(), Math.max(0, stats.power() - 1)));
                } else if (target != null) {
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                            stats.durationTicks(), Math.min(5, stats.power() + 1)));
                    target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,
                            stats.durationTicks(), Math.min(3, stats.power())));
                } else if (level.getBlockState(impact.blockPos()).is(Blocks.WATER)) {
                    level.setBlockAndUpdate(impact.blockPos(), Blocks.ICE.defaultBlockState());
                } else {
                    return false;
                }
            }
            case EFFECT_HEAL -> {
                LivingEntity healed = target == null ? player : target;
                healed.heal(3.0F + stats.power() * 3.0F);
                healed.addEffect(new MobEffectInstance(MobEffects.REGENERATION,
                        Math.max(40, stats.durationTicks() / 2), Math.max(0, stats.power() - 1)));
            }
            case EFFECT_BREAK -> {
                if (selfForm || !breakBlock(level, player, impact.blockPos())) {
                    return false;
                }
            }
            case EFFECT_PULL -> {
                if (selfForm) {
                    return pullArea(level, player, player.position(), stats.area() + 3.0D, stats.power());
                }
                if (target == null) {
                    return false;
                }
                pullEntity(player, target, stats.power());
            }
            case EFFECT_BLINK -> {
                Vec3 destination = selfForm
                        ? traceBlinkDestination(level, player, stats.range())
                        : safeDestination(level, impact.position(), impact.blockPos(), impact.direction());
                if (destination == null) {
                    return false;
                }
                player.teleportTo(destination.x, destination.y, destination.z);
                player.resetFallDistance();
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    private static boolean applyArea(ServerLevel level, ServerPlayer player, ItemStack book,
                                     SpellGlyphItem.Glyph effect, Impact impact, SpellStats stats) {
        if (effect == SpellGlyphItem.Glyph.EFFECT_BLINK) {
            Vec3 destination = safeDestination(level, impact.position(), impact.blockPos(), impact.direction());
            if (destination == null) {
                return false;
            }
            player.teleportTo(destination.x, destination.y, destination.z);
            player.resetFallDistance();
            return true;
        }
        if (effect == SpellGlyphItem.Glyph.EFFECT_BREAK) {
            return breakArea(level, player, impact.blockPos(), Math.min(2, stats.areaBlocks()));
        }
        if (effect == SpellGlyphItem.Glyph.EFFECT_PULL) {
            return pullArea(level, player, impact.position(), stats.area(), stats.power());
        }

        AABB bounds = new AABB(impact.position(), impact.position()).inflate(stats.area());
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, bounds,
                entity -> entity.isAlive() && (effect == SpellGlyphItem.Glyph.EFFECT_HEAL
                        ? entity == player || entity.isAlliedTo(player)
                        : entity != player && !entity.isAlliedTo(player)));
        int limit = Math.min(targets.size(), 1 + stats.chain() * 2 + stats.areaBlocks() * 3);
        if (effect == SpellGlyphItem.Glyph.EFFECT_HEAL && !targets.contains(player)) {
            targets.add(0, player);
            limit++;
        }
        boolean applied = false;
        for (int i = 0; i < Math.min(limit, targets.size()); i++) {
            LivingEntity target = targets.get(i);
            applied |= applySingle(level, player, book, effect,
                    new Impact(target.position(), target, target.blockPosition(), Direction.UP), stats, false);
        }
        return applied;
    }

    private static Impact trace(ServerLevel level, ServerPlayer player, Vec3 start, Vec3 look, double range) {
        Vec3 end = start.add(look.scale(range));
        BlockHitResult blockHit = level.clip(new ClipContext(start, end,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, player));
        double blockDistance = blockHit.getType() == HitResult.Type.MISS
                ? range * range : start.distanceToSqr(blockHit.getLocation());
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(level, player, start, end,
                player.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0D),
                entity -> entity instanceof LivingEntity && entity.isAlive()
                        && entity != player && !entity.isSpectator(), (float) blockDistance);
        if (entityHit != null) {
            LivingEntity target = (LivingEntity) entityHit.getEntity();
            return new Impact(entityHit.getLocation(), target, target.blockPosition(), Direction.UP);
        }
        if (blockHit.getType() != HitResult.Type.MISS) {
            return new Impact(blockHit.getLocation(), null, blockHit.getBlockPos(), blockHit.getDirection());
        }
        return null;
    }

    private static boolean breakArea(ServerLevel level, ServerPlayer player, BlockPos center, int radius) {
        int broken = 0;
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius))) {
            if (broken >= MAX_AREA_BLOCKS) {
                break;
            }
            if (pos.distSqr(center) <= radius * radius + 0.5D && breakBlock(level, player, pos.immutable())) {
                broken++;
            }
        }
        return broken > 0;
    }

    private static boolean breakBlock(ServerLevel level, ServerPlayer player, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        float hardness = state.getDestroySpeed(level, pos);
        if (state.isAir() || hardness < 0.0F || hardness > 50.0F || !level.mayInteract(player, pos)) {
            return false;
        }
        return player.gameMode.destroyBlock(pos);
    }

    private static boolean pullArea(ServerLevel level, ServerPlayer player, Vec3 center,
                                    double radius, int power) {
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(center, center).inflate(radius),
                entity -> entity != player && entity.isAlive() && !entity.isAlliedTo(player));
        for (LivingEntity target : targets) {
            pullEntity(player, target, power);
        }
        return !targets.isEmpty();
    }

    private static void pullEntity(ServerPlayer player, LivingEntity target, int power) {
        Vec3 pull = player.position().add(0.0D, 0.8D, 0.0D).subtract(target.position());
        if (pull.lengthSqr() > 0.01D) {
            target.setDeltaMovement(target.getDeltaMovement().scale(0.25D)
                    .add(pull.normalize().scale(0.45D + power * 0.18D)));
            target.hurtMarked = true;
        }
    }

    private static Vec3 traceBlinkDestination(ServerLevel level, ServerPlayer player, double range) {
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getLookAngle().scale(range));
        BlockHitResult hit = level.clip(new ClipContext(start, end,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        Vec3 desired = hit.getType() == HitResult.Type.MISS ? end : hit.getLocation();
        BlockPos block = BlockPos.containing(desired);
        return safeDestination(level, desired, block,
                hit.getType() == HitResult.Type.MISS ? Direction.UP : hit.getDirection());
    }

    private static Vec3 safeDestination(ServerLevel level, Vec3 desired, BlockPos block, Direction face) {
        BlockPos candidate = face == Direction.UP ? block.above() : block.relative(face);
        for (int dy = 1; dy >= -3; dy--) {
            BlockPos feet = candidate.offset(0, dy, 0);
            if (level.getBlockState(feet).getCollisionShape(level, feet).isEmpty()
                    && level.getBlockState(feet.above()).getCollisionShape(level, feet.above()).isEmpty()
                    && !level.getBlockState(feet.below()).getCollisionShape(level, feet.below()).isEmpty()) {
                return Vec3.atBottomCenterOf(feet);
            }
        }
        return null;
    }

    private static void particleBeam(ServerLevel level, Vec3 start, Vec3 end, ParticleOptions particle) {
        double distance = start.distanceTo(end);
        int steps = Mth.clamp((int) (distance * 3.0D), 2, 48);
        for (int step = 0; step <= steps; step++) {
            Vec3 point = start.lerp(end, (double) step / steps);
            level.sendParticles(particle, point.x, point.y, point.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private static ParticleOptions effectParticle(SpellGlyphItem.Glyph effect) {
        return switch (effect) {
            case EFFECT_FIRE -> ParticleTypes.FLAME;
            case EFFECT_FROST -> ParticleTypes.SNOWFLAKE;
            case EFFECT_HEAL -> ParticleTypes.HEART;
            case EFFECT_BREAK -> ParticleTypes.CRIT;
            case EFFECT_PULL -> ParticleTypes.REVERSE_PORTAL;
            case EFFECT_BLINK -> ParticleTypes.PORTAL;
            default -> ParticleTypes.WITCH;
        };
    }

    private static boolean consumeMana(ServerPlayer player, int amount) {
        if (player.getAbilities().instabuild) {
            return true;
        }
        int personal = AbyssMagic.mana(player);
        if (personal >= amount) {
            return AbyssMagic.consumeMana(player, amount);
        }
        int portableCost = amount - personal;
        if (!PortableMana.has(player, portableCost)) {
            return false;
        }
        if (personal > 0) {
            AbyssMagic.consumeMana(player, personal);
        }
        return PortableMana.consume(player, portableCost);
    }

    private static void refundMana(ServerPlayer player, int amount) {
        AbyssMagic.addMana(player, amount);
    }

    private static void addAugment(ItemStack book, int spell, int offset,
                                   List<SpellGlyphItem.Glyph> augments) {
        SpellGlyphItem.Glyph glyph = SpellbookData.glyph(book, spell, offset);
        if (glyph != null && glyph.category() == SpellGlyphItem.Category.AUGMENT) {
            augments.add(glyph);
        }
    }

    private static void message(ServerPlayer player, String key, ChatFormatting color) {
        player.displayClientMessage(Component.translatable(key).withStyle(color), true);
    }

    private record Impact(Vec3 position, LivingEntity entity, BlockPos blockPos, Direction direction) {
    }

    private record SpellStats(int manaCost, int strain, int power, int durationTicks,
                              double range, double area, int chain, int areaBlocks) {
        private static SpellStats create(SpellGlyphItem.Glyph form, SpellGlyphItem.Glyph effect,
                                         List<SpellGlyphItem.Glyph> augments) {
            int mana = form.manaCost() + effect.manaCost();
            int strain = form.strain() + effect.strain();
            int power = 1;
            int duration = 100;
            double range = form == SpellGlyphItem.Glyph.FORM_SELF ? 8.0D : 10.0D;
            double area = form == SpellGlyphItem.Glyph.FORM_AREA ? 3.0D : 2.0D;
            int chain = 0;
            int areaBlocks = form == SpellGlyphItem.Glyph.FORM_AREA ? 1 : 0;
            int efficiency = 0;
            for (SpellGlyphItem.Glyph augment : augments) {
                mana += augment.manaCost();
                strain += augment.strain();
                switch (augment) {
                    case AUGMENT_POWER -> power++;
                    case AUGMENT_RANGE -> range += 6.0D;
                    case AUGMENT_DURATION -> duration += 120;
                    case AUGMENT_EFFICIENCY -> efficiency++;
                    case AUGMENT_CHAIN -> chain++;
                    case AUGMENT_AREA -> {
                        area += 2.0D;
                        areaBlocks++;
                    }
                    default -> {
                    }
                }
            }
            mana = Math.max(4, mana * Math.max(50, 100 - efficiency * 25) / 100);
            return new SpellStats(mana, Math.min(30, strain), Math.min(3, power),
                    Math.min(360, duration), Math.min(24.0D, range), Math.min(7.0D, area),
                    Math.min(2, chain), Math.min(2, areaBlocks));
        }
    }
}
