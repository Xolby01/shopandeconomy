package net.example.economy.Commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod.EventBusSubscriber(modid = "shopandeconomy", bus = Mod.EventBusSubscriber.Bus.GAME, value = Dist.DEDICATED_SERVER)
public class RegisterCommands {

    @SubscribeEvent
    public static void onRegisterCommands(final RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        ShopCommand.register(dispatcher);
        ShopAdminCommand.register(dispatcher);
    }
}
