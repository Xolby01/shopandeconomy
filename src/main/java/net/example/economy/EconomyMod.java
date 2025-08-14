package net.example.economy;

import net.minecraft.server.MinecraftServer;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.IEventBus;

@Mod("shopandeconomy")
public class EconomyMod {
    public static MoneyManager moneyManager;
    public static TradeManager tradeManager;

    public EconomyMod() {
        moneyManager = new MoneyManager();
        tradeManager = new TradeManager();
    }

    @SubscribeEvent
    public void onServerStart(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        moneyManager.loadAll(server);
    }

    @SubscribeEvent
    public void onServerStop(ServerStoppingEvent event) {
        MinecraftServer server = event.getServer();
        moneyManager.saveAll(server);
    }
}
