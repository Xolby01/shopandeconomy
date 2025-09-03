package net.example.economy.shop;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@Mod.EventBusSubscriber(modid = "shopandeconomy", bus = Mod.EventBusSubscriber.Bus.GAME, value = Dist.DEDICATED_SERVER)
public class ShopLifecycleHooks {

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        ShopItemsManager.get().setup(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        // ensure any unsaved edits are flushed
        ShopItemsManager.get().save();
    }
}
