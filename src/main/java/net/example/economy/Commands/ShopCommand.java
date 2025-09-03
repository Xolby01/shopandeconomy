package net.example.economy.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.example.economy.shop.ShopHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ShopCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("shop")
                .requires(src -> src.hasPermission(0))
                .then(Commands.literal("open")
                    .executes(ctx -> open(ctx, null))
                    .then(Commands.argument("category", StringArgumentType.string())
                        .executes(ctx -> {
                            String cat = StringArgumentType.getString(ctx, "category");
                            return open(ctx, cat);
                        })
                    )
                )
                .then(Commands.literal("buy")
                    .then(Commands.argument("itemOrCategory", StringArgumentType.string())
                        .executes(ctx -> handleBuy(ctx, "maybeCategory")) // user passes just one arg; could be item id or category
                        .then(Commands.argument("item", StringArgumentType.string())
                            .executes(ctx -> handleBuy(ctx, "explicitItem"))
                        )
                    )
                )
                .then(Commands.literal("sell")
                    .then(Commands.argument("itemOrCategory", StringArgumentType.string())
                        .executes(ctx -> handleSell(ctx, "maybeCategory"))
                        .then(Commands.argument("item", StringArgumentType.string())
                            .executes(ctx -> handleSell(ctx, "explicitItem"))
                        )
                    )
                )
        );
    }

    private static int open(CommandContext<CommandSourceStack> ctx, String category) {
        ServerPlayer player;
        try {
            player = ctx.getSource().getPlayerOrException();
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            ctx.getSource().sendFailure(Component.literal("Commande uniquement pour les joueurs."));
            return 0;
        }
        ShopHandler.openShopFor(player, category);
        return 1;
    }

    private static int handleBuy(CommandContext<CommandSourceStack> ctx, String mode) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        String a = StringArgumentType.getString(ctx, "itemOrCategory");
        String itemId = "explicitItem".equals(mode)
                ? StringArgumentType.getString(ctx, "item")
                : a; // interpret arg as direct item id when only one provided
        ServerPlayer p = ctx.getSource().getPlayerOrException();
        boolean ok = ShopHandler.buyItem(p, itemId, 1);
        if (ok) {
            ctx.getSource().sendSuccess(() -> Component.literal("Achat de " + itemId + " réussi."), false);
            return 1;
        } else {
            ctx.getSource().sendFailure(Component.literal("Achat impossible pour: " + itemId));
            return 0;
        }
    }

    private static int handleSell(CommandContext<CommandSourceStack> ctx, String mode) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        String a = StringArgumentType.getString(ctx, "itemOrCategory");
        String itemId = "explicitItem".equals(mode)
                ? StringArgumentType.getString(ctx, "item")
                : a;
        ServerPlayer p = ctx.getSource().getPlayerOrException();
        boolean ok = ShopHandler.sellItem(p, itemId, 1);
        if (ok) {
            ctx.getSource().sendSuccess(() -> Component.literal("Vente de " + itemId + " effectuée."), false);
            return 1;
        } else {
            ctx.getSource().sendFailure(Component.literal("Vente impossible pour: " + itemId));
            return 0;
        }
    }
}
