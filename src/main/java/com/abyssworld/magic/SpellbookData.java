package com.abyssworld.magic;

import com.abyssworld.item.SpellGlyphItem;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public final class SpellbookData {
    public static final int SPELL_COUNT = 3;
    public static final int GLYPHS_PER_SPELL = 4;
    public static final int TOTAL_GLYPH_SLOTS = SPELL_COUNT * GLYPHS_PER_SPELL;
    public static final int FORM_OFFSET = 0;
    public static final int EFFECT_OFFSET = 1;
    public static final int FIRST_AUGMENT_OFFSET = 2;
    public static final int SECOND_AUGMENT_OFFSET = 3;

    private static final String INVENTORY_TAG = "SpellGlyphInventory";
    private static final String SELECTED_TAG = "SelectedSpell";

    private SpellbookData() {
    }

    public static NonNullList<ItemStack> loadGlyphs(ItemStack book) {
        NonNullList<ItemStack> glyphs = NonNullList.withSize(TOTAL_GLYPH_SLOTS, ItemStack.EMPTY);
        CompoundTag tag = book.getTag();
        if (tag != null && tag.contains(INVENTORY_TAG, CompoundTag.TAG_COMPOUND)) {
            ContainerHelper.loadAllItems(tag.getCompound(INVENTORY_TAG), glyphs);
        }
        return glyphs;
    }

    public static void saveGlyphs(ItemStack book, NonNullList<ItemStack> glyphs) {
        CompoundTag inventory = new CompoundTag();
        ContainerHelper.saveAllItems(inventory, glyphs, true);
        book.getOrCreateTag().put(INVENTORY_TAG, inventory);
    }

    public static int selectedSpell(ItemStack book) {
        CompoundTag tag = book.getTag();
        return tag == null ? 0 : Math.max(0, Math.min(SPELL_COUNT - 1, tag.getInt(SELECTED_TAG)));
    }

    public static void setSelectedSpell(ItemStack book, int selected) {
        book.getOrCreateTag().putInt(SELECTED_TAG, Math.max(0, Math.min(SPELL_COUNT - 1, selected)));
    }

    @Nullable
    public static SpellGlyphItem.Glyph glyph(ItemStack book, int spell, int offset) {
        int slot = spell * GLYPHS_PER_SPELL + offset;
        if (slot < 0 || slot >= TOTAL_GLYPH_SLOTS) {
            return null;
        }
        ItemStack stack = loadGlyphs(book).get(slot);
        return stack.getItem() instanceof SpellGlyphItem glyphItem ? glyphItem.glyph() : null;
    }

    public static boolean isComplete(ItemStack book, int spell) {
        SpellGlyphItem.Glyph form = glyph(book, spell, FORM_OFFSET);
        SpellGlyphItem.Glyph effect = glyph(book, spell, EFFECT_OFFSET);
        return form != null && form.category() == SpellGlyphItem.Category.FORM
                && effect != null && effect.category() == SpellGlyphItem.Category.EFFECT;
    }
}
