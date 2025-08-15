package net.example.economy;

import net.example.economy.shop.ShopConfig;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

import java.nio.file.Path;

@Mod(EconomyMod.MODID)
public class EconomyMod {
    public static final String MODID = "shopandeconomy";

    public static final MoneyManager moneyManager = new MoneyManager();
    public static final TradeManager tradeManager = new TradeManager();

    // Nouveau : config du shop
    public static ShopConfig shopConfig;

    public EconomyMod() {
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        moneyManager.loadAll(server);

        Path cfg = server.getServerDirectory().resolve("config").resolve(MODID).resolve("shop.json");
        shopConfig = new ShopConfig(cfg);
        shopConfig.loadOrCreateDefaults();
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        MinecraftServer server = event.getServer();
        moneyManager.saveAll(server);
        if (shopConfig != null) shopConfig.save();
    }
}
