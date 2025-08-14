package net.example.economy.Commands;

import com.mojang.brigadier.CommandDispatcher;
import net.example.economy.EconomyMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class TradeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("trade")
                .then(Commands.argument("target", EntityArgument.player())
                    .executes(ctx -> {
                        ServerPlayer sender = ctx.getSource().getPlayerOrException();
                        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");

                        // Enregistre l'invitation via le TradeManager
                        EconomyMod.tradeManager.invite(sender.getUUID(), target.getUUID());

                        // Messages d’info (sans UUID supplémentaire)
                        target.sendSystemMessage(Component.literal(
                            sender.getScoreboardName() + " te propose un trade. Tape /accepttrade pour accepter."
                        ));
                        sender.sendSystemMessage(Component.literal(
                            "Invitation envoyée à " + target.getScoreboardName()
                        ));

                        return 1;
                    })
                )
        );
    }
}
