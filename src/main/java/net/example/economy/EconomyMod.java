package net.example.economy;

import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@Mod(EconomyMod.MODID)
public class EconomyMod {
    public static final String MODID = "shopandeconomy";

    public static final MoneyManager moneyManager = new MoneyManager();
    public static final TradeManager tradeManager = new TradeManager();

    public EconomyMod() {
        // Pas d’enregistrement particulier côté mod bus pour l’instant.
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        moneyManager.loadAll(server);
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        MinecraftServer server = event.getServer();
        moneyManager.saveAll(server);
    }
}
