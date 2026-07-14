package com.abyssworld.menu;

import com.abyssworld.item.AbyssSpellbookItem;
import com.abyssworld.item.SpellGlyphItem;
import com.abyssworld.magic.SpellbookContainer;
import com.abyssworld.magic.SpellbookData;
import com.abyssworld.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SpellbookMenu extends AbstractContainerMenu {
    private static final int DATA_COUNT = 1;

    private final Inventory playerInventory;
    private final int sourceSlot;
    private final ItemStack book;
    private final SpellbookContainer glyphs;
    private final ContainerData data;

    public SpellbookMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, buffer.readVarInt(), new SimpleContainerData(DATA_COUNT));
    }

    public SpellbookMenu(int id, Inventory inventory, int sourceSlot) {
        this(id, inventory, sourceSlot, serverData(inventory, sourceSlot));
    }

    private SpellbookMenu(int id, Inventory inventory, int sourceSlot, ContainerData data) {
        super(ModMenus.SPELLBOOK.get(), id);
        this.playerInventory = inventory;
        this.sourceSlot = sourceSlot;
        this.book = sourceBook(inventory, sourceSlot);
        this.glyphs = new SpellbookContainer(book);
        this.data = data;
        checkContainerSize(glyphs, SpellbookData.TOTAL_GLYPH_SLOTS);
        checkContainerDataCount(data, DATA_COUNT);

        for (int spell = 0; spell < SpellbookData.SPELL_COUNT; spell++) {
            int y = 29 + spell * 28;
            addGlyphSlot(spell, SpellbookData.FORM_OFFSET, 53, y, SpellGlyphItem.Category.FORM);
            addGlyphSlot(spell, SpellbookData.EFFECT_OFFSET, 81, y, SpellGlyphItem.Category.EFFECT);
            addGlyphSlot(spell, SpellbookData.FIRST_AUGMENT_OFFSET, 121, y, SpellGlyphItem.Category.AUGMENT);
            addGlyphSlot(spell, SpellbookData.SECOND_AUGMENT_OFFSET, 149, y, SpellGlyphItem.Category.AUGMENT);
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int inventorySlot = col + row * 9 + 9;
                addPlayerSlot(inventorySlot, 26 + col * 18, 126 + row * 18);
            }
        }
        for (int col = 0; col < 9; col++) {
            addPlayerSlot(col, 26 + col * 18, 184);
        }
        addDataSlots(data);
    }

    private void addGlyphSlot(int spell, int offset, int x, int y, SpellGlyphItem.Category category) {
        int glyphSlot = spell * SpellbookData.GLYPHS_PER_SPELL + offset;
        addSlot(new Slot(glyphs, glyphSlot, x, y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof SpellGlyphItem glyphItem
                        && glyphItem.glyph().category() == category;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
    }

    private void addPlayerSlot(int inventorySlot, int x, int y) {
        addSlot(new Slot(playerInventory, inventorySlot, x, y) {
            @Override
            public boolean mayPickup(Player player) {
                return inventorySlot != sourceSlot;
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return inventorySlot != sourceSlot;
            }
        });
    }

    public int selectedSpell() {
        return data.get(0);
    }

    public ItemStack glyph(int spell, int offset) {
        return glyphs.getItem(spell * SpellbookData.GLYPHS_PER_SPELL + offset);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id < 0 || id >= SpellbookData.SPELL_COUNT) {
            return false;
        }
        data.set(0, id);
        SpellbookData.setSelectedSpell(book, id);
        playerInventory.setChanged();
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        if (sourceSlot < 0 || sourceSlot >= playerInventory.getContainerSize()) {
            return false;
        }
        return playerInventory.getItem(sourceSlot).getItem() instanceof AbyssSpellbookItem;
    }

    @Override
    public void removed(Player player) {
        glyphs.save();
        playerInventory.setChanged();
        super.removed(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack source = slot.getItem();
        ItemStack original = source.copy();
        int glyphSlots = SpellbookData.TOTAL_GLYPH_SLOTS;
        if (index < glyphSlots) {
            if (!moveItemStackTo(source, glyphSlots, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (source.getItem() instanceof SpellGlyphItem glyphItem) {
            int offsetStart = switch (glyphItem.glyph().category()) {
                case FORM -> SpellbookData.FORM_OFFSET;
                case EFFECT -> SpellbookData.EFFECT_OFFSET;
                case AUGMENT -> SpellbookData.FIRST_AUGMENT_OFFSET;
            };
            boolean moved = false;
            for (int spell = 0; spell < SpellbookData.SPELL_COUNT && !moved; spell++) {
                int from = spell * SpellbookData.GLYPHS_PER_SPELL + offsetStart;
                int to = glyphItem.glyph().category() == SpellGlyphItem.Category.AUGMENT ? from + 2 : from + 1;
                moved = moveItemStackTo(source, from, to, false);
            }
            if (!moved) {
                return ItemStack.EMPTY;
            }
        } else if (index < glyphSlots + 27) {
            if (!moveItemStackTo(source, glyphSlots + 27, slots.size(), false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(source, glyphSlots, glyphSlots + 27, false)) {
            return ItemStack.EMPTY;
        }

        if (source.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        slot.onTake(player, source);
        return original;
    }

    private static ItemStack sourceBook(Inventory inventory, int sourceSlot) {
        if (sourceSlot >= 0 && sourceSlot < inventory.getContainerSize()) {
            ItemStack stack = inventory.getItem(sourceSlot);
            if (stack.getItem() instanceof AbyssSpellbookItem) {
                return stack;
            }
        }
        throw new IllegalStateException("Missing abyss spellbook");
    }

    private static ContainerData serverData(Inventory inventory, int sourceSlot) {
        ItemStack book = sourceBook(inventory, sourceSlot);
        return new ContainerData() {
            @Override
            public int get(int index) {
                return index == 0 ? SpellbookData.selectedSpell(book) : 0;
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) {
                    SpellbookData.setSelectedSpell(book, value);
                }
            }

            @Override
            public int getCount() {
                return DATA_COUNT;
            }
        };
    }
}
