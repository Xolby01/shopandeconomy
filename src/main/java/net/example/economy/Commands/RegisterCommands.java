package net.example.economy.Commands;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
