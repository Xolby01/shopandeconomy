package net.example.economy;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(EconomyMod.MODID)
public class EconomyMod {
    public static final String MODID = "shopandeconomy";

    public EconomyMod() {
        // Register GAME-bus listeners
        NeoForge.EVENT_BUS.register(new net.example.economy.Commands.RegisterCommands());
        NeoForge.EVENT_BUS.register(new net.example.economy.shop.ShopLifecycleHooks());
    }
}