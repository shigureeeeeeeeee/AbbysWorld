package com.abyssworld.magic;

import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class SpellbookContainer extends SimpleContainer {
    private final ItemStack book;
    private boolean loading;

    public SpellbookContainer(ItemStack book) {
        super(SpellbookData.TOTAL_GLYPH_SLOTS);
        this.book = book;
        loading = true;
        NonNullList<ItemStack> stored = SpellbookData.loadGlyphs(book);
        for (int slot = 0; slot < stored.size(); slot++) {
            super.setItem(slot, stored.get(slot));
        }
        loading = false;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (!loading) {
            save();
        }
    }

    public void save() {
        NonNullList<ItemStack> stored = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        for (int slot = 0; slot < getContainerSize(); slot++) {
            stored.set(slot, getItem(slot));
        }
        SpellbookData.saveGlyphs(book, stored);
    }
}
