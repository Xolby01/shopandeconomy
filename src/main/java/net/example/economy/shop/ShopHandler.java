package net.example.economy.shop;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.example.economy.MoneyManager;
import java.util.List;
import java.util.Optional;

/**
 * Server-side shop handler. Uses ShopItemsManager and MoneyManager.
 */
public class ShopHandler {
    private static final ShopItemsManager itemsManager = new ShopItemsManager();

    public static void openShopFor(ServerPlayer player) {
        List<ShopItemsManager.Entry> entries = itemsManager.getItems();
        // send a packet to client to open the screen with entries (simple approach)
        // Here we open a menu provider so the client can trigger client-side screen if desired
        player.openMenu(new net.minecraft.world.inventory.MenuProvider() {
            @Override
            public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int windowId, net.minecraft.world.entity.player.Inventory playerInventory, FriendlyByteBuf data) {
                return new ShopMenu(windowId, playerInventory, entries);
            }

            @Override
            public Component getDisplayName() {
                return Component.literal("Economy Shop");
            }
        });
    }

    public static void handleBuy(ServerPlayer player, String registryName, int amount) {
        Optional<ItemStack> maybe = ShopItemsManager.toStack(registryName);
        if (maybe.isEmpty()) {
            player.sendSystemMessage(Component.literal("Article introuvable."));
            return;
        }
        ItemStack stack = maybe.get();
        long pricePer = findBuyPrice(registryName);
        if (pricePer <= 0) {
            player.sendSystemMessage(Component.literal("Cet item n'est pas en vente."));
            return;
        }
        long total = pricePer * amount;
        MoneyManager mm = new MoneyManager(); // recommend singleton instead
        if (!mm.withdraw(player.getUUID(), total)) {
            player.sendSystemMessage(Component.literal("Tu n'as pas assez d'argent."));
            return;
        }
        stack.setCount(Math.min(stack.getMaxStackSize(), amount));
        player.addItem(stack);
        player.sendSystemMessage(Component.literal("Achat réussi : " + stack.getHoverName().getString() + " x" + amount + " pour " + total));
        mm.save();
    }

    public static void handleSell(ServerPlayer player, String registryName, int amount) {
        Optional<ItemStack> maybe = ShopItemsManager.toStack(registryName);
        if (maybe.isEmpty()) {
            player.sendSystemMessage(Component.literal("Article introuvable."));
            return;
        }
        ItemStack proto = maybe.get();
        long sellPrice = findSellPrice(registryName);
        if (sellPrice <= 0) {
            player.sendSystemMessage(Component.literal("Cet item ne peut pas être vendu."));
            return;
        }
        int removed = player.getInventory().removeItem(proto.getItem(), amount);
        if (removed == 0) {
            player.sendSystemMessage(Component.literal("Tu n'as pas cet item en inventaire."));
            return;
        }
        long total = sellPrice * removed;
        MoneyManager mm = new MoneyManager();
        mm.deposit(player.getUUID(), total);
        player.sendSystemMessage(Component.literal("Vendu x" + removed + " pour " + total));
        mm.save();
    }

    private static long findBuyPrice(String registryName) {
        for (ShopItemsManager.Entry e : itemsManager.getItems()) if (e.item.equals(registryName)) return e.buy_price;
        return -1;
    }

    private static long findSellPrice(String registryName) {
        for (ShopItemsManager.Entry e : itemsManager.getItems()) if (e.item.equals(registryName)) return e.sell_price;
        return -1;
    }
}
