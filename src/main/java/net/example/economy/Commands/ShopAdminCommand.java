package net.example.economy.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.example.economy.shop.ShopItemsManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ShopAdminCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("shopadmin")
                .requires(src -> src.hasPermission(4))
                .then(Commands.literal("addCategory")
                    .then(Commands.argument("id", StringArgumentType.string())
                        .then(Commands.argument("displayName", StringArgumentType.string())
                            .then(Commands.argument("icon", StringArgumentType.string())
                                .executes(ctx -> {
                                    var id = StringArgumentType.getString(ctx, "id");
                                    var dn = StringArgumentType.getString(ctx, "displayName");
                                    var icon = StringArgumentType.getString(ctx, "icon");
                                    boolean ok = ShopItemsManager.get().addCategory(id, dn, icon);
                                    if (ok) ctx.getSource().sendSuccess(() -> Component.literal("Catégorie ajoutée: " + id), false);
                                    else ctx.getSource().sendFailure(Component.literal("Impossible d'ajouter la catégorie (existe déjà?)"));
                                    return ok ? 1 : 0;
                                })
                            )
                        )
                    )
                )
                .then(Commands.literal("removeCategory")
                    .then(Commands.argument("id", StringArgumentType.string())
                        .executes(ctx -> {
                            var id = StringArgumentType.getString(ctx, "id");
                            boolean ok = ShopItemsManager.get().removeCategory(id);
                            if (ok) ctx.getSource().sendSuccess(() -> Component.literal("Catégorie supprimée: " + id), false);
                            else ctx.getSource().sendFailure(Component.literal("Catégorie introuvable: " + id));
                            return ok ? 1 : 0;
                        })
                    )
                )
                .then(Commands.literal("addItem")
                    .then(Commands.argument("category", StringArgumentType.string())
                        .then(Commands.argument("item", StringArgumentType.string())
                            .executes(ctx -> {
                                var cat = StringArgumentType.getString(ctx, "category");
                                var item = StringArgumentType.getString(ctx, "item");
                                var ok = ShopItemsManager.get().addItemToCategory(cat, item);
                                if (ok) ctx.getSource().sendSuccess(() -> Component.literal("Ajout de l'item " + item + " à " + cat), false);
                                else ctx.getSource().sendFailure(Component.literal("Impossible d'ajouter l'item."));
                                return ok ? 1 : 0;
                            })
                        )
                    )
                )
                .then(Commands.literal("removeItem")
                    .then(Commands.argument("category", StringArgumentType.string())
                        .then(Commands.argument("item", StringArgumentType.string())
                            .executes(ctx -> {
                                var cat = StringArgumentType.getString(ctx, "category");
                                var item = StringArgumentType.getString(ctx, "item");
                                var ok = ShopItemsManager.get().removeItemFromCategory(cat, item);
                                if (ok) ctx.getSource().sendSuccess(() -> Component.literal("Suppression de l'item " + item + " de " + cat), false);
                                else ctx.getSource().sendFailure(Component.literal("Item ou catégorie introuvable."));
                                return ok ? 1 : 0;
                            })
                        )
                    )
                )
                .then(Commands.literal("reload")
                    .executes(ctx -> {
                        ShopItemsManager.get().reload();
                        ctx.getSource().sendSuccess(() -> Component.literal("Shop rechargé depuis le JSON."), false);
                        return 1;
                    })
                )
        );
    }

    private static int fail(CommandContext<CommandSourceStack> ctx, String msg) {
        ctx.getSource().sendFailure(Component.literal(msg));
        return 0;
    }
}
