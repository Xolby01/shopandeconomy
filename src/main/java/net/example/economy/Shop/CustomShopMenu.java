package net.example.economy.Shop;

import net.example.economy.EconomyMod;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;

public class CustomShopMenu extends ChestMenu {
    private final SimpleContainer shopContainer;

    public CustomShopMenu(int id, Inventory playerInventory, SimpleContainer shopContainer) {
        super(id, playerInventory, shopContainer);
        this.shopContainer = shopContainer;
    }

    @Override
    public ItemStack clicked(int slotId, int dragType, ClickType clickType, Player player) {
        // If the click is in the shop portion (slots 0..26), attempt purchase
        if (slotId >= 0 && slotId < shopContainer.getContainerSize()) {
            ItemStack clicked = shopContainer.getItem(slotId).copy();
            long price = ShopHandler.getPrice(clicked);
            if (price > 0) {
                java.util.UUID buyerId = player.getUUID();
                if (EconomyMod.moneyManager.withdraw(buyerId, price)) {
                    // give item to player
                    player.addItem(clicked);
                    player.sendMessage(Component.literal("Achat réussi : " + clicked.getHoverName().getString() + " pour " + price), player.getUUID());
                    return ItemStack.EMPTY;
                } else {
                    player.sendMessage(Component.literal("Tu n'as pas assez d'argent pour acheter ceci."), player.getUUID());
                    return ItemStack.EMPTY;
                }
            }
        }
        return super.clicked(slotId, dragType, clickType, player);
    }
}
