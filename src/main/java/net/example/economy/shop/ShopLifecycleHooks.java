package net.example.economy.shop;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

public class ShopLifecycleHooks {

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        ShopItemsManager.ensureLoaded(event.getServer());
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        // Save on stop if needed
        ShopItemsManager.save();
    }
}