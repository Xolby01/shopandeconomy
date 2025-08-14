package net.example.economy.Commands;

import com.mojang.brigadier.CommandDispatcher;
import net.example.economy.EconomyMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class AcceptTradeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("accepttrade").executes(ctx -> {
            ServerPlayer accepter = ctx.getSource().getPlayerOrException();

            // Récupère l'invitant depuis le TradeManager
            java.util.UUID inviterId = EconomyMod.tradeManager.getInviterFor(accepter.getUUID());
            if (inviterId == null) {
                ctx.getSource().sendFailure(Component.literal("Aucune invitation trouvée."));
                return 0;
            }

            // Résout le joueur invitant s'il est en ligne
            ServerPlayer inviter = ctx.getSource().getServer().getPlayerList().getPlayer(inviterId);

            // Messages d’info
            if (inviter != null) {
                inviter.sendSystemMessage(Component.literal(accepter.getScoreboardName() + " a accepté ton invitation."));
            }
            accepter.sendSystemMessage(Component.literal("Tu as accepté l'invitation."));

            return 1;
        }));
    }
}
