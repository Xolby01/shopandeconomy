package net.example.economy.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.example.economy.EconomyMod;
import net.example.economy.shop.ShopConfig;
import net.example.economy.shop.ShopHandler;
import net.example.economy.shop.ShopItemsManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Optional;

public class ShopCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("shop")
            .executes(ctx -> {
                // Ouvre (placeholder) le shop
                ServerPlayer p = ctx.getSource().getPlayerOrException();
                ShopHandler.openShopFor(p);
                return 1;
            })
            .then(Commands.literal("categories").executes(ctx -> {
                if (EconomyMod.shopConfig == null) {
                    ctx.getSource().sendFailure(Component.literal("Shop non initialisé."));
                    return 0;
                }
                List<ShopConfig.Category> cats = EconomyMod.shopConfig.getCategories();
                if (cats.isEmpty()) {
                    ctx.getSource().sendSuccess(() -> Component.literal("Aucune catégorie."), false);
                    return 1;
                }
                ctx.getSource().sendSuccess(() -> Component.literal("Catégories:"), false);
                for (ShopConfig.Category c : cats) {
                    ctx.getSource().sendSuccess(() -> Component.literal("- " + c.id + " (" + c.display_name + ")"), false);
                }
                return 1;
            }))
            .then(Commands.literal("list")
                .then(Commands.argument("category", net.minecraft.commands.arguments.StringArgumentType.string())
                    .executes(ctx -> {
                        if (EconomyMod.shopConfig == null) {
                            ctx.getSource().sendFailure(Component.literal("Shop non initialisé."));
                            return 0;
                        }
                        String cat = net.minecraft.commands.arguments.StringArgumentType.getString(ctx, "category");
                        List<ShopConfig.ItemEntry> items = EconomyMod.shopConfig.getItems(cat);
                        if (items.isEmpty()) {
                            ctx.getSource().sendSuccess(() -> Component.literal("Catégorie vide ou inexistante."), false);
                            return 1;
                        }
                        ctx.getSource().sendSuccess(() -> Component.literal("Items dans " + cat + ":"), false);
                        for (ShopConfig.ItemEntry e : items) {
                            ctx.getSource().sendSuccess(() -> Component.literal("- " + e.item + " | buy=" + e.buy_price + " sell=" + e.sell_price), false);
                        }
                        return 1;
                    })
                )
            )
            .then(Commands.literal("buy")
                // /shop buy <item> [amount]
                .then(Commands.argument("itemOrCategory", net.minecraft.commands.arguments.StringArgumentType.string())
                    .executes(ctx -> handleBuy(ctx, /*category*/null))
                    .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> handleBuy(ctx, /*category*/null))
                    )
                    // /shop buy <category> <item> [amount]
                    .then(Commands.argument("item", net.minecraft.commands.arguments.StringArgumentType.string())
                        .executes(ctx -> handleBuy(ctx, /*category*/"categoryFirst"))
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                            .executes(ctx -> handleBuy(ctx, /*category*/"categoryFirst"))
                        )
                    )
                )
            )
            .then(Commands.literal("sell")
                // /shop sell <item> [amount]
                .then(Commands.argument("itemOrCategory", net.minecraft.commands.arguments.StringArgumentType.string())
                    .executes(ctx -> handleSell(ctx, /*category*/null))
                    .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> handleSell(ctx, /*category*/null))
                    )
                    // /shop sell <category> <item> [amount]
                    .then(Commands.argument("item", net.minecraft.commands.arguments.StringArgumentType.string())
                        .executes(ctx -> handleSell(ctx, /*category*/"categoryFirst"))
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                            .executes(ctx -> handleSell(ctx, /*category*/"categoryFirst"))
                        )
                    )
                )
            )
        );
    }

    private static int handleBuy(CommandContext<CommandSourceStack> ctx, String mode) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer p = ctx.getSource().getPlayerOrException();
        if (EconomyMod.shopConfig == null) {
            ctx.getSource().sendFailure(Component.literal("Shop non initialisé."));
            return 0;
        }

        String a = net.minecraft.commands.arguments.StringArgumentType.getString(ctx, "itemOrCategory");
        String itemId = (mode == null) ? a
                : net.minecraft.commands.arguments.StringArgumentType.getString(ctx, "item");
        Optional<ShopConfig.ItemEntry> entry = (mode == null)
                ? ShopHandler.findItem(a, null)
                : ShopHandler.findItem(a, itemId);

        int amount = 1;
        if (ctx.getInput().contains(" amount ")) {
            amount = IntegerArgumentType.getInteger(ctx, "amount");
        }

        if (entry.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("Item non trouvé dans le shop."));
            return 0;
        }

        // Résolution de l'Item
        var stackOpt = ShopItemsManager.toStack(entry.get().item);
        if (stackOpt.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("Item invalide: " + entry.get().item));
            return 0;
        }
        Item item = stackOpt.get().getItem();
        return ShopHandler.buyItem(p, item, amount, entry.get().buy_price) ? 1 : 0;
    }

    private static int handleSell(CommandContext<CommandSourceStack> ctx, String mode) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer p = ctx.getSource().getPlayerOrException();
        if (EconomyMod.shopConfig == null) {
            ctx.getSource().sendFailure(Component.literal("Shop non initialisé."));
            return 0;
        }

        String a = net.minecraft.commands.arguments.StringArgumentType.getString(ctx, "itemOrCategory");
        String itemId = (mode == null) ? a
                : net.minecraft.commands.arguments.StringArgumentType.getString(ctx, "item");
        Optional<ShopConfig.ItemEntry> entry = (mode == null)
                ? ShopHandler.findItem(a, null)
                : ShopHandler.findItem(a, itemId);

        int amount = 1;
        if (ctx.getInput().contains(" amount ")) {
            amount = IntegerArgumentType.getInteger(ctx, "amount");
        }

        if (entry.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("Item non trouvé dans le shop."));
            return 0;
        }

        var stackOpt = ShopItemsManager.toStack(entry.get().item);
        if (stackOpt.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("Item invalide: " + entry.get().item));
            return 0;
        }
        Item item = stackOpt.get().getItem();
        return ShopHandler.sellItem(p, item, amount, entry.get().sell_price) ? 1 : 0;
    }
}
