package net.example.economy.Commands;

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "shopandeconomy", value = Dist.DEDICATED_SERVER)
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
