package com.abyssworld.client.screen;

import com.abyssworld.magic.SpellbookData;
import com.abyssworld.menu.SpellbookMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SpellbookScreen extends AbstractContainerScreen<SpellbookMenu> {
    private static final int PANEL = 0xFF16131D;
    private static final int INNER = 0xFF241D2D;
    private static final int BORDER = 0xFF795AA5;
    private static final int SELECTED = 0xFF4B8C8B;
    private static final int SLOT = 0xFF0E0C13;
    private final Button[] spellButtons = new Button[SpellbookData.SPELL_COUNT];

    public SpellbookScreen(SpellbookMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 214;
        imageHeight = 208;
        titleLabelX = 8;
        titleLabelY = 7;
        inventoryLabelX = 26;
        inventoryLabelY = 114;
    }

    @Override
    protected void init() {
        super.init();
        for (int spell = 0; spell < spellButtons.length; spell++) {
            int index = spell;
            spellButtons[spell] = addRenderableWidget(Button.builder(Component.literal(Integer.toString(spell + 1)),
                            button -> select(index))
                    .bounds(leftPos + 13, topPos + 29 + spell * 28, 26, 18)
                    .build());
        }
        updateButtons();
    }

    private void select(int spell) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, spell);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateButtons();
    }

    private void updateButtons() {
        for (int spell = 0; spell < spellButtons.length; spell++) {
            if (spellButtons[spell] != null) {
                spellButtons[spell].active = spell != menu.selectedSpell();
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = leftPos;
        int top = topPos;
        graphics.fill(left, top, left + imageWidth, top + imageHeight, BORDER);
        graphics.fill(left + 2, top + 2, left + imageWidth - 2, top + imageHeight - 2, PANEL);
        for (int spell = 0; spell < SpellbookData.SPELL_COUNT; spell++) {
            int y = top + 25 + spell * 28;
            int color = spell == menu.selectedSpell() ? SELECTED : INNER;
            graphics.fill(left + 8, y, left + imageWidth - 8, y + 26, color);
            slot(graphics, left + 52, top + 28 + spell * 28);
            slot(graphics, left + 80, top + 28 + spell * 28);
            slot(graphics, left + 120, top + 28 + spell * 28);
            slot(graphics, left + 148, top + 28 + spell * 28);
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                slot(graphics, left + 25 + col * 18, top + 125 + row * 18);
            }
        }
        for (int col = 0; col < 9; col++) {
            slot(graphics, left + 25 + col * 18, top + 183);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 0xF1E6FF, false);
        graphics.drawString(font, Component.translatable("gui.abyssworld.spellbook.form"), 51, 17,
                0x76D9DD, false);
        graphics.drawString(font, Component.translatable("gui.abyssworld.spellbook.effect"), 79, 17,
                0xD996EF, false);
        graphics.drawString(font, Component.translatable("gui.abyssworld.spellbook.augment"), 119, 17,
                0xE6BD62, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xC9BBD4, false);
    }

    private static void slot(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, BORDER);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, SLOT);
    }
}
