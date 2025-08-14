package net.example.economy.Commands;

import com.mojang.brigadier.CommandDispatcher;
import net.example.economy.shop.ShopHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ShopCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("shop").executes(ctx -> {
            ServerPlayer p = ctx.getSource().getPlayerOrException();
            ShopHandler.openShopFor(p);
            return 1;
        }));
    }
}
