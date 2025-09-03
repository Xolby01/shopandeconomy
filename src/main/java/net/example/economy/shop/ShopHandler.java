package net.example.economy.shop;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

public class ShopHandler {

    public static void openShopFor(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("Shop ouvert."));
        // TODO: open menu/screen via networking if you add a client GUI later
    }

    public static void openShopFor(ServerPlayer player, String category) {
        player.sendSystemMessage(Component.literal("Shop ouvert (catégorie: " + category + ")."));
        // TODO: same as above, filtered
    }

    public static boolean buyItem(ServerPlayer player, String itemId, int amount) {
        Item item = ShopItemsManager.getItemById(itemId);
        if (item == null) {
            player.sendSystemMessage(Component.literal("Item introuvable: " + itemId));
            return false;
        }
        long price = ShopItemsManager.getBuyPrice(itemId);
        if (price < 0) {
            player.sendSystemMessage(Component.literal("Cet item n'est pas achetable."));
            return false;
        }
        ItemStack stack = new ItemStack(item, Math.max(1, amount));
        boolean added = player.addItem(stack);
        if (added) {
            player.sendSystemMessage(Component.literal("Achat: " + itemId + " x" + amount + " pour " + (price * amount)));
            return true;
        } else {
            player.sendSystemMessage(Component.literal("Inventaire plein."));
            return false;
        }
    }

    public static boolean sellItem(ServerPlayer player, String itemId, int amount) {
        Item item = ShopItemsManager.getItemById(itemId);
        if (item == null) {
            player.sendSystemMessage(Component.literal("Item introuvable: " + itemId));
            return false;
        }
        long price = ShopItemsManager.getSellPrice(itemId);
        if (price < 0) {
            player.sendSystemMessage(Component.literal("Cet item n'est pas vendable."));
            return false;
        }
        // remove items from inventory
        int toRemove = Math.max(1, amount);
        int removed = 0;
        for (int i = 0; i < player.getInventory().items.size() && removed < toRemove; i++) {
            ItemStack s = player.getInventory().items.get(i);
            if (!s.isEmpty() && s.getItem() == item) {
                int take = Math.min(s.getCount(), toRemove - removed);
                s.shrink(take);
                if (s.getCount() <= 0) player.getInventory().items.set(i, ItemStack.EMPTY);
                removed += take;
            }
        }
        if (removed > 0) {
            player.sendSystemMessage(Component.literal("Vente: " + itemId + " x" + removed + " pour " + (price * removed)));
            return true;
        } else {
            player.sendSystemMessage(Component.literal("Tu n'as pas cet item."));
            return false;
        }
    }
}