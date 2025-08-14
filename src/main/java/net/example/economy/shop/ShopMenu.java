package net.example.economy.shop;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class ShopMenu extends AbstractContainerMenu {
    private final Inventory playerInv;
    private final List<ShopItemsManager.Entry> entries;

    // Constructeur simple si tu n'as pas les entrées à ce moment-là
    public ShopMenu(int id, Inventory inv) {
        this(id, inv, Collections.emptyList());
    }

    // Constructeur principal
    public ShopMenu(int id, Inventory inv, List<ShopItemsManager.Entry> entries) {
        // Pour compiler sans type de menu enregistré, on utilise un type vanilla existant.
        // À terme tu peux enregistrer ton propre MenuType custom.
        super(MenuType.GENERIC_9x3, id);
        this.playerInv = inv;
        this.entries = entries != null ? entries : Collections.emptyList();
    }

    public List<ShopItemsManager.Entry> getEntries() {
        return entries;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
