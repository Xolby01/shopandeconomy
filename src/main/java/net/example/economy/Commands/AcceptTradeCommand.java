package net.example.economy.Commands;

import com.mojang.brigadier.CommandDispatcher;
import net.example.economy.EconomyMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

public class AcceptTradeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("accepttrade").executes(ctx -> {
            ServerPlayer accepter = ctx.getSource().getPlayerOrException();
            java.util.UUID inviterId = EconomyMod.tradeManager.getInviterFor(accepter.getUUID());
            if (inviterId == null) {
                ctx.getSource().sendFailure(Component.literal("Aucune invitation trouvée.")); 
                return 0;
            }
            EconomyMod.tradeManager.removeInvitation(inviterId);
            ctx.getSource().sendSuccess(() -> Component.literal("Trade accepté."), false);
            net.minecraft.server.MinecraftServer server = accepter.getServer();
            if (server != null) {
                var inviter = server.getPlayerList().getPlayer(inviterId);
                if (inviter != null) inviter.sendSystemMessage(Component.literal(accepter.getScoreboardName() + " a accepté ton invitation."), inviter.getUUID();
            }
            return 1;
        }));
    }
}
