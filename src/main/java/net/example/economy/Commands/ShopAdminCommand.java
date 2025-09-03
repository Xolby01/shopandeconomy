package net.example.economy.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
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
                                    String id = StringArgumentType.getString(ctx, "id");
                                    String dn = StringArgumentType.getString(ctx, "displayName");
                                    String icon = StringArgumentType.getString(ctx, "icon");
                                    boolean ok = ShopItemsManager.addCategory(id, dn, icon);
                                    ctx.getSource().sendSuccess(() -> Component.literal(ok ? "Catégorie ajoutée." : "Échec d'ajout (déjà existante?)"), false);
                                    return ok ? 1 : 0;
                                })
                            )
                        )
                    )
                )
                .then(Commands.literal("removeCategory")
                    .then(Commands.argument("id", StringArgumentType.string())
                        .executes(ctx -> {
                            String id = StringArgumentType.getString(ctx, "id");
                            boolean ok = ShopItemsManager.removeCategory(id);
                            ctx.getSource().sendSuccess(() -> Component.literal(ok ? "Catégorie supprimée." : "Échec (introuvable)"), false);
                            return ok ? 1 : 0;
                        })
                    )
                )
                .then(Commands.literal("addItem")
                    .then(Commands.argument("category", StringArgumentType.string())
                        .then(Commands.argument("item", StringArgumentType.string())
                            .executes(ctx -> {
                                String cat = StringArgumentType.getString(ctx, "category");
                                String item = StringArgumentType.getString(ctx, "item");
                                boolean ok = ShopItemsManager.addItem(cat, item, 100, 50);
                                ctx.getSource().sendSuccess(() -> Component.literal(ok ? "Item ajouté." : "Échec d'ajout"), false);
                                return ok ? 1 : 0;
                            })
                        )
                    )
                )
                .then(Commands.literal("removeItem")
                    .then(Commands.argument("category", StringArgumentType.string())
                        .then(Commands.argument("item", StringArgumentType.string())
                            .executes(ctx -> {
                                String cat = StringArgumentType.getString(ctx, "category");
                                String item = StringArgumentType.getString(ctx, "item");
                                boolean ok = ShopItemsManager.removeItem(cat, item);
                                ctx.getSource().sendSuccess(() -> Component.literal(ok ? "Item supprimé." : "Échec (introuvable)"), false);
                                return ok ? 1 : 0;
                            })
                        )
                    )
                )
                .then(Commands.literal("reload")
                    .executes(ctx -> {
                        boolean ok = ShopItemsManager.reload(ctx.getSource().getServer());
                        ctx.getSource().sendSuccess(() -> Component.literal(ok ? "Shop rechargé." : "Échec de rechargement"), false);
                        return ok ? 1 : 0;
                    })
                )
        );
    }
}