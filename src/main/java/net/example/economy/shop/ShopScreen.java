package net.example.economy.shop;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ShopScreen extends Screen {
    private final List<ShopItemsManager.Entry> items;

    public ShopScreen(List<ShopItemsManager.Entry> items) {
        super(Component.literal("Economy Shop"));
        this.items = items;
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(gfx, mouseX, mouseY, partialTicks);
        super.render(gfx, mouseX, mouseY, partialTicks);
        int x = this.width / 2 - 140;
        int y = 30;
        for (int i = 0; i < items.size(); i++) {
            int yy = y + i * 14;
            ShopItemsManager.Entry e = items.get(i);
            gfx.drawString(this.font, e.item, x, yy, 0xFFFFFF);
            gfx.drawString(this.font, "$" + e.buy_price + " / $" + e.sell_price, x + 160, yy, 0xFFFF55);
        }
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
