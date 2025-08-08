package net.example.economy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraft.server.MinecraftServer;

@Mod("neoforgeeconomy")
public class EconomyMod {
    public static MoneyManager moneyManager;
    public static TradeManager tradeManager;

    public EconomyMod() {
        moneyManager = new MoneyManager();
        tradeManager = new TradeManager();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    }

    @SubscribeEvent
    public void onServerStart(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        moneyManager.loadAll(server);
        Commands.RegisterCommands.register(); // registers via event subscriber
        // Shop handler is event-subscribed
    }

    @SubscribeEvent
    public void onServerStop(ServerStoppingEvent event) {
        MinecraftServer server = event.getServer();
        moneyManager.saveAll(server);
    }
}
