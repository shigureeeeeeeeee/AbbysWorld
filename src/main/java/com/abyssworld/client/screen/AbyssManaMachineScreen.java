package com.abyssworld.client.screen;

import com.abyssworld.menu.AbyssManaMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AbyssManaMachineScreen extends AbstractContainerScreen<AbyssManaMachineMenu> {
    private static final int PANEL = 0xFF17141F;
    private static final int INNER = 0xFF24202E;
    private static final int BORDER = 0xFF65547A;
    private static final int SLOT = 0xFF0F0D14;
    private static final int MANA = 0xFF8D65D6;
    private static final int PROGRESS = 0xFF55B8C9;
    private static final int ESSENCE = 0xFFC060E8;

    public AbyssManaMachineScreen(AbyssManaMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 230;
        imageHeight = 182;
        titleLabelX = 8;
        titleLabelY = 7;
        inventoryLabelX = 34;
        inventoryLabelY = 88;
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
        graphics.fill(left + 6, top + 20, left + imageWidth - 6, top + 83, INNER);
        slotFrame(graphics, left + 26, top + 34);
        slotFrame(graphics, left + 108, top + 34);
        slotFrame(graphics, left + 142, top + 34);
        slotFrame(graphics, left + 59, top + 34);
        for (int slot = 0; slot < 3; slot++) {
            slotFrame(graphics, left + 197, top + 25 + slot * 20);
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                slotFrame(graphics, left + 33 + column * 18, top + 99 + row * 18);
            }
        }
        for (int column = 0; column < 9; column++) {
            slotFrame(graphics, left + 33 + column * 18, top + 157);
        }

        int cost = menu.manaCost();
        int progressWidth = cost <= 0 ? 0 : Math.min(52, menu.workMana() * 52 / cost);
        bar(graphics, left + 50, top + 38, 52, progressWidth, PROGRESS);
        int manaWidth = menu.connectedMana() == Integer.MAX_VALUE
                ? 102
                : Math.min(102, (int) ((long) menu.connectedMana() * 102 / 10000));
        bar(graphics, left + 65, top + 67, 102, manaWidth, MANA);
        int essenceWidth = menu.essenceCapacity() <= 0 ? 0
                : Math.min(48, menu.storedEssence() * 48 / menu.essenceCapacity());
        bar(graphics, left + 176, top + 78, 48, essenceWidth, ESSENCE);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 0xEADFFF, false);
        graphics.drawString(font, Component.translatable("container.abyssworld.mana_machine.input"),
                18, 24, 0xC8B9D8, false);
        graphics.drawString(font, Component.translatable("container.abyssworld.mana_machine.output"),
                101, 24, 0xC8B9D8, false);
        graphics.drawString(font, Component.translatable("container.abyssworld.mana_machine.byproduct"),
                132, 24, 0xC8B9D8, false);
        graphics.drawString(font, Component.translatable("container.abyssworld.mana_machine.catalyst"),
                50, 24, 0xC8B9D8, false);
        graphics.drawString(font, Component.translatable("container.abyssworld.mana_machine.upgrades"),
                177, 13, 0xC8B9D8, false);
        graphics.drawString(font, Component.translatable("container.abyssworld.mana_machine.progress",
                        menu.workMana(), menu.manaCost()),
                8, 57, 0xD7F8FF, false);
        Component manaStatus = menu.connectedMana() == Integer.MAX_VALUE
                ? Component.translatable("container.abyssworld.mana_machine.mana_infinite", menu.throughput())
                : Component.translatable("container.abyssworld.mana_machine.mana",
                        menu.connectedMana(), menu.throughput());
        graphics.drawString(font, manaStatus, 8, 73, 0xE8D8FF, false);
        graphics.drawString(font, Component.translatable("container.abyssworld.mana_machine.essence",
                menu.storedEssence(), menu.essenceCapacity()), 148, 68, 0xEBC8FF, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xC8B9D8, false);
    }

    private static void slotFrame(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, BORDER);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, SLOT);
    }

    private static void bar(GuiGraphics graphics, int x, int y, int width, int filled, int color) {
        graphics.fill(x, y, x + width, y + 6, SLOT);
        if (filled > 0) graphics.fill(x, y, x + filled, y + 6, color);
    }
}
