package com.abyssworld.client.screen;

import com.abyssworld.menu.LeylineMinerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class LeylineMinerScreen extends AbstractContainerScreen<LeylineMinerMenu> {
    private static final int PANEL = 0xFF141A1E;
    private static final int INNER = 0xFF202B30;
    private static final int BORDER = 0xFF4F7C84;
    private static final int SLOT = 0xFF0C1012;
    private static final int MANA = 0xFF9666DA;
    private static final int SCAN = 0xFF55C7C2;
    private Button runButton;
    private Button modeButton;
    private Button silkButton;
    private Button replaceButton;

    public LeylineMinerScreen(LeylineMinerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 230; imageHeight = 235;
        titleLabelX = 8; titleLabelY = 7; inventoryLabelX = 34; inventoryLabelY = 141;
    }

    @Override protected void init() {
        super.init();
        runButton = addRenderableWidget(Button.builder(Component.literal(">"), b -> press(0))
                .bounds(leftPos + 8, topPos + 88, 28, 18).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.abyssworld.leyline_miner.reset"), b -> press(1))
                .bounds(leftPos + 39, topPos + 88, 47, 18).build());
        addRenderableWidget(Button.builder(Component.literal("-"), b -> press(2))
                .bounds(leftPos + 8, topPos + 110, 18, 18).build());
        addRenderableWidget(Button.builder(Component.literal("+"), b -> press(3))
                .bounds(leftPos + 68, topPos + 110, 18, 18).build());
        addRenderableWidget(Button.builder(Component.literal("-"), b -> press(4))
                .bounds(leftPos + 91, topPos + 110, 18, 18).build());
        addRenderableWidget(Button.builder(Component.literal("+"), b -> press(5))
                .bounds(leftPos + 151, topPos + 110, 18, 18).build());
        modeButton = addRenderableWidget(Button.builder(Component.empty(), b -> press(6))
                .bounds(leftPos + 90, topPos + 88, 132, 18).build());
        silkButton = addRenderableWidget(Button.builder(Component.empty(), b -> press(7))
                .bounds(leftPos + 174, topPos + 110, 48, 18).build());
        replaceButton = addRenderableWidget(Button.builder(Component.empty(), b -> press(8))
                .bounds(leftPos + 174, topPos + 130, 48, 18).build());
        updateButtons();
    }

    private void press(int id) {
        if (minecraft != null && minecraft.gameMode != null)
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
    }

    @Override protected void containerTick() { super.containerTick(); updateButtons(); }

    private void updateButtons() {
        if (runButton == null) return;
        runButton.setMessage(Component.literal(menu.running() ? "||" : ">"));
        modeButton.setMessage(Component.translatable("gui.abyssworld.leyline_miner.mode." +
                menu.filterMode().name().toLowerCase()));
        silkButton.setMessage(Component.translatable(menu.silkTouch() ? "options.on" : "options.off"));
        replaceButton.setMessage(Component.translatable(menu.replacing() ? "options.on" : "options.off"));
    }

    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics); super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY);
    }

    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = leftPos, top = topPos;
        graphics.fill(left, top, left + imageWidth, top + imageHeight, BORDER);
        graphics.fill(left + 2, top + 2, left + imageWidth - 2, top + imageHeight - 2, PANEL);
        graphics.fill(left + 6, top + 20, left + imageWidth - 6, top + 84, INNER);
        for (int slot = 0; slot < 4; slot++) slot(graphics, left + 7 + slot * 20, top + 37);
        slot(graphics, left + 91, top + 37);
        for (int row = 0; row < 3; row++) for (int col = 0; col < 3; col++)
            slot(graphics, left + 125 + col * 18, top + 27 + row * 18);
        for (int slot = 0; slot < 3; slot++) slot(graphics, left + 203, top + 27 + slot * 20);
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            slot(graphics, left + 33 + col * 18, top + 152 + row * 18);
        for (int col = 0; col < 9; col++) slot(graphics, left + 33 + col * 18, top + 210);
        bar(graphics, left + 8, top + 72, 102, Math.min(102, menu.scanProgress() * 102 / 1000), SCAN);
        int manaWidth = menu.nearbyMana() == Integer.MAX_VALUE ? 102
                : Math.min(102, (int)((long) menu.nearbyMana() * 102 / 10000));
        bar(graphics, left + 120, top + 72, 102, manaWidth, MANA);
    }

    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 7, 0xE4F8FA, false);
        graphics.drawString(font, Component.translatable("gui.abyssworld.leyline_miner.filters"), 8, 25, 0xB9D7DB, false);
        graphics.drawString(font, Component.translatable("gui.abyssworld.leyline_miner.replacement"), 84, 25, 0xB9D7DB, false);
        graphics.drawString(font, Component.translatable("gui.abyssworld.leyline_miner.outputs"), 125, 16, 0xB9D7DB, false);
        graphics.drawString(font, Component.translatable("container.abyssworld.mana_machine.upgrades"), 193, 16, 0xB9D7DB, false);
        graphics.drawString(font, Component.translatable("gui.abyssworld.leyline_miner.status." +
                menu.status().name().toLowerCase()), 8, 61, 0xD7F8FF, false);
        graphics.drawString(font, Component.translatable("gui.abyssworld.leyline_miner.work",
                menu.workMana(), menu.manaCost()), 8, 78, 0xA9CFD2, false);
        graphics.drawString(font, Component.translatable("gui.abyssworld.leyline_miner.mana",
                menu.nearbyMana(), menu.throughput()), 120, 78, 0xDCCBFA, false);
        graphics.drawString(font, Component.translatable("gui.abyssworld.leyline_miner.radius", menu.radius()), 28, 115, 0xD7F8FF, false);
        graphics.drawString(font, Component.translatable("gui.abyssworld.leyline_miner.depth", menu.depth()), 111, 115, 0xD7F8FF, false);
        graphics.drawString(font, Component.translatable("gui.abyssworld.leyline_miner.silk"), 174, 103, 0xB9D7DB, false);
        graphics.drawString(font, Component.translatable("gui.abyssworld.leyline_miner.replace"), 174, 123, 0xB9D7DB, false);
        graphics.drawString(font, Component.translatable("gui.abyssworld.leyline_miner.mined", menu.minedBlocks()), 8, 132, 0xB9D7DB, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xB9D7DB, false);
    }

    private static void slot(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, BORDER); graphics.fill(x + 1, y + 1, x + 17, y + 17, SLOT);
    }
    private static void bar(GuiGraphics graphics, int x, int y, int width, int filled, int color) {
        graphics.fill(x, y, x + width, y + 6, SLOT); if (filled > 0) graphics.fill(x, y, x + filled, y + 6, color);
    }
}
