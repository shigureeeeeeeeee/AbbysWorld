package com.abyssworld.client.screen;

import com.abyssworld.menu.AbyssStorageTerminalMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AbyssStorageTerminalScreen extends AbstractContainerScreen<AbyssStorageTerminalMenu> {
    public AbyssStorageTerminalScreen(AbyssStorageTerminalMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title); imageWidth = 176; imageHeight = 222; inventoryLabelY = 128;
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics); super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY);
    }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF67537E);
        graphics.fill(leftPos + 2, topPos + 2, leftPos + imageWidth - 2, topPos + imageHeight - 2, 0xFF17141F);
        for (int row = 0; row < 6; row++) for (int col = 0; col < 9; col++) slot(graphics, leftPos + 7 + col*18, topPos + 17 + row*18);
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) slot(graphics, leftPos + 7 + col*18, topPos + 139 + row*18);
        for (int col = 0; col < 9; col++) slot(graphics, leftPos + 7 + col*18, topPos + 197);
    }
    private static void slot(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x+18, y+18, 0xFF67537E); graphics.fill(x+1, y+1, x+17, y+17, 0xFF0F0D14);
    }
}
