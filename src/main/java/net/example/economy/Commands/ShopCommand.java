package net.example.economy.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.example.economy.shop.ShopHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ShopCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("shop")
                .then(Commands.literal("open")
                    .executes(ctx -> {
                        ServerPlayer p = ctx.getSource().getPlayerOrException();
                        ShopHandler.openShopFor(p);
                        return 1;
                    })
                    .then(Commands.argument("category", StringArgumentType.string())
                        .executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            String category = StringArgumentType.getString(ctx, "category");
                            ShopHandler.openShopFor(p, category);
                            return 1;
                        })
                    )
                )
                .then(Commands.literal("buy")
                    .then(Commands.argument("item", StringArgumentType.string())
                        .executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            String itemId = StringArgumentType.getString(ctx, "item");
                            boolean ok = ShopHandler.buyItem(p, itemId, 1);
                            return ok ? 1 : 0;
                        })
                    )
                )
                .then(Commands.literal("sell")
                    .then(Commands.argument("item", StringArgumentType.string())
                        .executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            String itemId = StringArgumentType.getString(ctx, "item");
                            boolean ok = ShopHandler.sellItem(p, itemId, 1);
                            return ok ? 1 : 0;
                        })
                    )
                )
        );
    }
}