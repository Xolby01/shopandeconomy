package net.example.economy.Shop;

import net.example.economy.EconomyMod;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Items;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class ShopHandler {
    private static final Component SHOP_TITLE = Component.literal("Economy Shop");

    public static void openShopFor(ServerPlayer player) {
        // Crée un inventaire 3 lignes (27 slots) en mémoire
        SimpleContainer container = new SimpleContainer(27);

        // Exemple : ajouter quelques items (dont un 'modded-like')
        ItemStack diamond = new ItemStack(Items.DIAMOND);
        setPrice(diamond, 100);
        diamond.setHoverName(Component.literal("Diamant — 100"));
        container.setItem(0, diamond);

        ItemStack emerald = new ItemStack(Items.EMERALD);
        setPrice(emerald, 250);
        emerald.setHoverName(Component.literal("Emerald — 250"));
        container.setItem(1, emerald);

        // Ouvrir l'inventaire sous forme de ChestMenu (3 rows) mais avec Custom menu to intercept clicks
        player.openMenu(new net.minecraft.world.inventory.MenuProvider() {
            @Override
            public Component getDisplayName() { return SHOP_TITLE; }

            @Override
            public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity) {
                return new CustomShopMenu(id, playerInventory, container);
            }
        });
    }

    public static void setPrice(ItemStack stack, long price) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putLong("EconomyPrice", price);
    }

    public static long getPrice(ItemStack stack) {
        if (!stack.hasTag()) return -1;
        CompoundTag tag = stack.getTag();
        if (tag.contains("EconomyPrice")) return tag.getLong("EconomyPrice");
        return -1;
    }

    @SubscribeEvent
    public static void onInventoryClick(PlayerContainerEvent.Open event) {
        // placeholder
    }
}
