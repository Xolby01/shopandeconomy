package net.example.economy.shop;

import net.example.economy.EconomyMod;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class ShopHandler {

    public static void openShopFor(ServerPlayer player) {
        // Placeholder UI (texte) – l’UI graphique viendra après via networking.
        player.sendSystemMessage(Component.literal("Shop ouvert. Utilise /shop list, /shop buy, /shop sell"));
    }

    public static Optional<ShopConfig.ItemEntry> findItem(String categoryOrItem, String maybeItem) {
        if (EconomyMod.shopConfig == null) return Optional.empty();
        // /shop buy <item> -> categoryOrItem = item
        if (maybeItem == null) {
            return EconomyMod.shopConfig.findItemAnyCategory(categoryOrItem);
        }
        // /shop buy <category> <item>
        return EconomyMod.shopConfig.getCategory(categoryOrItem)
                .flatMap(cat -> cat.items.stream().filter(i -> i.item.equalsIgnoreCase(maybeItem)).findFirst());
    }

    public static boolean buyItem(ServerPlayer player, Item item, int amount, long unitPrice) {
        long total = Math.max(0L, unitPrice) * Math.max(1, amount);
        if (!EconomyMod.moneyManager.withdraw(player.getUUID(), total)) {
            player.sendSystemMessage(Component.literal("Fonds insuffisants."));
            return false;
        }
        player.getInventory().add(new ItemStack(item, amount));
        EconomyMod.moneyManager.save();
        player.sendSystemMessage(Component.literal("Achat réussi : " + item.toString() + " x" + amount + " pour " + total));
        return true;
    }

    public static boolean sellItem(ServerPlayer player, Item item, int amount, long unitPrice) {
        int removed = removeFromInventory(player.getInventory(), item, amount);
        if (removed <= 0) {
            player.sendSystemMessage(Component.literal("Tu n’as pas cet objet en inventaire."));
            return false;
        }
        long total = Math.max(0L, unitPrice) * removed;
        EconomyMod.moneyManager.deposit(player.getUUID(), total);
        EconomyMod.moneyManager.save();
        player.sendSystemMessage(Component.literal("Vente réussie : " + item.toString() + " x" + removed + " pour " + total));
        return true;
    }

    private static int removeFromInventory(Inventory inv, Item item, int amount) {
        int toRemove = Math.max(1, amount);
        int removed = 0;
        for (int slot = 0; slot < inv.getContainerSize() && toRemove > 0; slot++) {
            ItemStack s = inv.getItem(slot);
            if (!s.isEmpty() && s.getItem() == item) {
                int take = Math.min(s.getCount(), toRemove);
                s.shrink(take);
                if (s.getCount() <= 0) inv.setItem(slot, ItemStack.EMPTY);
                toRemove -= take;
                removed += take;
            }
        }
        return removed;
    }
}
