package net.example.economy.Commands;

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "shopandeconomy", bus = EventBusSubscriber.Bus.GAME, value = Dist.DEDICATED_SERVER)

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class RegisterCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        PayCommand.register(event.getDispatcher());
        TopMoneyCommand.register(event.getDispatcher());
        ShopCommand.register(event.getDispatcher());
        TradeCommand.register(event.getDispatcher());
        AcceptTradeCommand.register(event.getDispatcher());
    }
}