package net.example.economy.shop;

import net.example.economy.EconomyMod;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ShopHandler {

    /** Ouvre (placeholder) l’UI du shop. UI cliquable désactivée tant que le networking n’est pas remis. */
    public static void openShopFor(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("Shop: interface temporairement désactivée (pas de networking)."));
    }

    /** Prix d’un ItemStack pour la vente (exemple minimal) */
    public static long getPrice(ItemStack stack) {
        // Exemple minimal : 1 unité = 1 crédit ; adapte à partir de ta config si besoin
        return stack.getCount();
    }

    /** Achat (exemple minimal) : vérifie le solde puis ajoute l’item. */
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

    /** Vente (exemple minimal) : enlève exactement `amount` items correspondants puis crédite. */
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

    /** Helper 1.21.1 : suppression d’items par itération d’inventaire (évite la vieille signature removeItem(Item,int)). */
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
