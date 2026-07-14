package com.abyssworld.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class SpellGlyphItem extends Item {
    public enum Category {
        FORM,
        EFFECT,
        AUGMENT
    }

    public enum Glyph {
        FORM_SELF("form_self", Category.FORM, 4, 1),
        FORM_BOLT("form_bolt", Category.FORM, 8, 2),
        FORM_AREA("form_area", Category.FORM, 14, 4),

        EFFECT_FIRE("effect_fire", Category.EFFECT, 12, 4),
        EFFECT_FROST("effect_frost", Category.EFFECT, 12, 4),
        EFFECT_HEAL("effect_heal", Category.EFFECT, 18, 5),
        EFFECT_BREAK("effect_break", Category.EFFECT, 16, 5),
        EFFECT_PULL("effect_pull", Category.EFFECT, 12, 3),
        EFFECT_BLINK("effect_blink", Category.EFFECT, 20, 6),

        AUGMENT_POWER("augment_power", Category.AUGMENT, 10, 3),
        AUGMENT_RANGE("augment_range", Category.AUGMENT, 6, 2),
        AUGMENT_DURATION("augment_duration", Category.AUGMENT, 6, 2),
        AUGMENT_EFFICIENCY("augment_efficiency", Category.AUGMENT, 0, 1),
        AUGMENT_CHAIN("augment_chain", Category.AUGMENT, 12, 4),
        AUGMENT_AREA("augment_area", Category.AUGMENT, 10, 3);

        private final String id;
        private final Category category;
        private final int manaCost;
        private final int strain;

        Glyph(String id, Category category, int manaCost, int strain) {
            this.id = id;
            this.category = category;
            this.manaCost = manaCost;
            this.strain = strain;
        }

        public String id() {
            return id;
        }

        public Category category() {
            return category;
        }

        public int manaCost() {
            return manaCost;
        }

        public int strain() {
            return strain;
        }
    }

    private final Glyph glyph;

    public SpellGlyphItem(Glyph glyph, Properties properties) {
        super(properties);
        this.glyph = glyph;
    }

    public Glyph glyph() {
        return glyph;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.abyssworld.spell_glyph.category." +
                glyph.category().name().toLowerCase()).withStyle(categoryColor()));
        tooltip.add(Component.translatable("item.abyssworld.spell_glyph." + glyph.id() + ".desc")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.abyssworld.spell_glyph.cost",
                glyph.manaCost(), glyph.strain()).withStyle(ChatFormatting.DARK_PURPLE));
    }

    private ChatFormatting categoryColor() {
        return switch (glyph.category()) {
            case FORM -> ChatFormatting.AQUA;
            case EFFECT -> ChatFormatting.LIGHT_PURPLE;
            case AUGMENT -> ChatFormatting.GOLD;
        };
    }
}
