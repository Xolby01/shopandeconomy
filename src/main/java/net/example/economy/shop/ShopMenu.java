package net.example.economy.shop;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.List;

public class ShopMenu extends AbstractContainerMenu {
    private final List<ShopItemsManager.Entry> entries;
    private final Player player;

    public ShopMenu(int id, Inventory playerInventory, List<ShopItemsManager.Entry> entries) {
        super(MenuType.GENERIC_9x1, id);
        this.entries = entries;
        this.player = playerInventory.player;
    }

    public ShopMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, new ShopItemsManager().getItems());
    }

    @Override
    public boolean stillValid(Player p) {
        return true;
    }

    public List<ShopItemsManager.Entry> getEntries() {
        return entries;
    }

    @Override
    public net.minecraft.world.item.ItemStack quickMoveStack(net.minecraft.world.entity.player.Player player, int index) {
        return net.minecraft.world.item.ItemStack.EMPTY;
    }
}