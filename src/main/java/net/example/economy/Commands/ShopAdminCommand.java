package net.example.economy.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.example.economy.EconomyMod;
import net.example.economy.shop.ShopConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ShopAdminCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("shopadmin")
            .requires(src -> src.hasPermission(2)) // permission admin (OP 2+)
            .then(Commands.literal("reload").executes(ctx -> {
                if (EconomyMod.shopConfig == null) {
                    ctx.getSource().sendFailure(Component.literal("Shop non initialisé."));
                    return 0;
                }
                EconomyMod.shopConfig.loadOrCreateDefaults();
                ctx.getSource().sendSuccess(() -> Component.literal("Shop rechargé depuis le fichier."), true);
                return 1;
            }))
            .then(Commands.literal("save").executes(ctx -> {
                if (EconomyMod.shopConfig == null) {
                    ctx.getSource().sendFailure(Component.literal("Shop non initialisé."));
                    return 0;
                }
                EconomyMod.shopConfig.save();
                ctx.getSource().sendSuccess(() -> Component.literal("Shop sauvegardé."), true);
                return 1;
            }))
            .then(Commands.literal("addcategory")
                .then(Commands.argument("id", net.minecraft.commands.arguments.StringArgumentType.string())
                    .then(Commands.argument("displayName", net.minecraft.commands.arguments.StringArgumentType.string())
                        .then(Commands.argument("icon", net.minecraft.commands.arguments.StringArgumentType.string())
                            .executes(ctx -> {
                                var id = net.minecraft.commands.arguments.StringArgumentType.getString(ctx, "id");
                                var dn = net.minecraft.commands.arguments.StringArgumentType.getString(ctx, "displayName");
                                var icon = net.minecraft.commands.arguments.StringArgumentType.getString(ctx, "icon");
                                if (EconomyMod.shopConfig == null) return fail(ctx, "Shop non initialisé.");
                                boolean ok = EconomyMod.shopConfig.addCategory(id, dn, icon);
                                if (ok) {
                                    EconomyMod.shopConfig.save();
                                    ctx.getSource().sendSuccess(() -> Component.literal("Catégorie ajoutée: " + id), true);
                                    return 1;
                                } else {
                                    return fail(ctx, "Catégorie déjà existante.");
                                }
                            })
                        )
                    )
                )
            )
            .then(Commands.literal("delcategory")
                .then(Commands.argument("id", net.minecraft.commands.arguments.StringArgumentType.string())
                    .executes(ctx -> {
                        var id = net.minecraft.commands.arguments.StringArgumentType.getString(ctx, "id");
                        if (EconomyMod.shopConfig == null) return fail(ctx, "Shop non initialisé.");
                        boolean ok = EconomyMod.shopConfig.removeCategory(id);
                        if (ok) {
                            EconomyMod.shopConfig.save();
                            ctx.getSource().sendSuccess(() -> Component.literal("Catégorie supprimée: " + id), true);
                            return 1;
                        } else {
                            return fail(ctx, "Catégorie introuvable.");
                        }
                    })
                )
            )
            .then(Commands.literal("additem")
                .then(Commands.argument("category", net.minecraft.commands.arguments.StringArgumentType.string())
                    .then(Commands.argument("item", net.minecraft.commands.arguments.StringArgumentType.string())
                        .then(Commands.argument("buy", LongArgumentType.longArg(0))
                            .then(Commands.argument("sell", LongArgumentType.longArg(0))
                                .executes(ctx -> {
                                    var cat = net.minecraft.commands.arguments.StringArgumentType.getString(ctx, "category");
                                    var item = net.minecraft.commands.arguments.StringArgumentType.getString(ctx, "item");
                                    long buy = LongArgumentType.getLong(ctx, "buy");
                                    long sell = LongArgumentType.getLong(ctx, "sell");
                                    if (EconomyMod.shopConfig == null) return fail(ctx, "Shop non initialisé.");
                                    boolean ok = EconomyMod.shopConfig.setItem(cat, item, buy, sell);
                                    if (ok) {
                                        EconomyMod.shopConfig.save();
                                        ctx.getSource().sendSuccess(() -> Component.literal("Item ajouté/mis à jour: " + item + " (cat: " + cat + ")"), true);
                                        return 1;
                                    } else {
                                        return fail(ctx, "Catégorie introuvable.");
                                    }
                                })
                            )
                        )
                    )
                )
            )
            .then(Commands.literal("delitem")
                .then(Commands.argument("category", net.minecraft.commands.arguments.StringArgumentType.string())
                    .then(Commands.argument("item", net.minecraft.commands.arguments.StringArgumentType.string())
                        .executes(ctx -> {
                            var cat = net.minecraft.commands.arguments.StringArgumentType.getString(ctx, "category");
                            var item = net.minecraft.commands.arguments.StringArgumentType.getString(ctx, "item");
                            if (EconomyMod.shopConfig == null) return fail(ctx, "Shop non initialisé.");
                            boolean ok = EconomyMod.shopConfig.removeItem(cat, item);
                            if (ok) {
                                EconomyMod.shopConfig.save();
                                ctx.getSource().sendSuccess(() -> Component.literal("Item supprimé: " + item + " (cat: " + cat + ")"), true);
                                return 1;
                            } else {
                                return fail(ctx, "Catégorie ou item introuvable.");
                            }
                        })
                    )
                )
            )
            .then(Commands.literal("list")
                .then(Commands.argument("category", net.minecraft.commands.arguments.StringArgumentType.string())
                    .executes(ctx -> {
                        if (EconomyMod.shopConfig == null) return fail(ctx, "Shop non initialisé.");
                        var cat = net.minecraft.commands.arguments.StringArgumentType.getString(ctx, "category");
                        List<ShopConfig.ItemEntry> items = EconomyMod.shopConfig.getItems(cat);
                        if (items.isEmpty()) {
                            ctx.getSource().sendSuccess(() -> Component.literal("Catégorie vide ou inexistante."), true);
                            return 1;
                        }
                        ctx.getSource().sendSuccess(() -> Component.literal("Items (" + cat + "):"), true);
                        for (ShopConfig.ItemEntry e : items) {
                            final String line = "- " + e.item + " | buy=" + e.buy_price + " sell=" + e.sell_price;
                            ctx.getSource().sendSuccess(() -> Component.literal(line), false);
                        }
                        return 1;
                    })
                )
            )
        );
    }

    private static int fail(CommandContext<CommandSourceStack> ctx, String msg) {
        ctx.getSource().sendFailure(Component.literal(msg));
        return 0;
    }
}
