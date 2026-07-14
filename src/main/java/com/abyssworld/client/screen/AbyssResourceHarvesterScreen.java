package com.abyssworld.client.screen;

import com.abyssworld.menu.AbyssResourceHarvesterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AbyssResourceHarvesterScreen extends AbstractContainerScreen<AbyssResourceHarvesterMenu> {
    private static final int PANEL = 0xFF17141F;
    private static final int INNER = 0xFF24202E;
    private static final int BORDER = 0xFF65547A;
    private static final int SLOT = 0xFF0F0D14;
    private static final int MANA = 0xFF8D65D6;
    private static final int PROGRESS = 0xFF55B8C9;

    public AbyssResourceHarvesterScreen(AbyssResourceHarvesterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 208;
        titleLabelX = 8;
        titleLabelY = 7;
        inventoryLabelX = 8;
        inventoryLabelY = 114;
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
        graphics.fill(left + 6, top + 20, left + imageWidth - 6, top + 109, INNER);

        slotFrame(graphics, left + 25, top + 42);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                slotFrame(graphics, left + 79 + column * 18, top + 24 + row * 18);
            }
        }
        for (int slot = 0; slot < 3; slot++) slotFrame(graphics, left + 149, top + 24 + slot * 20);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                slotFrame(graphics, left + 7 + column * 18, top + 125 + row * 18);
            }
        }
        for (int column = 0; column < 9; column++) {
            slotFrame(graphics, left + 7 + column * 18, top + 183);
        }

        int cost = menu.manaCost();
        int progressWidth = cost <= 0 ? 0 : Math.min(118, menu.workMana() * 118 / cost);
        bar(graphics, left + 49, top + 83, 118, progressWidth, PROGRESS);

        int manaWidth = menu.nearbyMana() == Integer.MAX_VALUE
                ? 118
                : Math.min(118, (int) ((long) menu.nearbyMana() * 118 / 10000));
        bar(graphics, left + 49, top + 99, 118, manaWidth, MANA);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 0xEADFFF, false);
        graphics.drawString(font, Component.translatable("container.abyssworld.resource_harvester.target"),
                10, 30, 0xC8B9D8, false);
        graphics.drawString(font, Component.translatable("container.abyssworld.resource_harvester.output"),
                80, 13, 0xC8B9D8, false);
        graphics.drawString(font, Component.translatable("container.abyssworld.mana_machine.upgrades"),
                141, 13, 0xC8B9D8, false);
        graphics.drawString(font, Component.translatable("container.abyssworld.resource_harvester.progress",
                        menu.workMana(), menu.manaCost()),
                8, 82, 0xD7F8FF, false);
        Component manaStatus = menu.nearbyMana() == Integer.MAX_VALUE
                ? Component.translatable("container.abyssworld.resource_harvester.mana_infinite")
                : Component.translatable("container.abyssworld.resource_harvester.mana", menu.nearbyMana());
        graphics.drawString(font, manaStatus, 8, 98, 0xE8D8FF, false);
        graphics.drawString(font, Component.translatable("container.abyssworld.resource_harvester.speed",
                        menu.throughput()),
                8, 106, 0xBFA9D2, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xC8B9D8, false);
    }

    private static void slotFrame(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, BORDER);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, SLOT);
    }

    private static void bar(GuiGraphics graphics, int x, int y, int width, int filled, int color) {
        graphics.fill(x, y, x + width, y + 6, SLOT);
        if (filled > 0) {
            graphics.fill(x, y, x + filled, y + 6, color);
        }
    }
}
