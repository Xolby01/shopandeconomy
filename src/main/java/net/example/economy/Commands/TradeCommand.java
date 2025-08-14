package net.example.economy.Commands;

import com.mojang.brigadier.CommandDispatcher;
import net.example.economy.EconomyMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

public class TradeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("trade")
            .then(Commands.argument("target", EntityArgument.player())
            .executes(ctx -> {
                ServerPlayer sender = ctx.getSource().getPlayerOrException();
                ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                EconomyMod.tradeManager.invite(sender.getUUID(), target.getUUID());
                target.sendSystemMessage(Component.literal(sender.getScoreboardName() + " te propose un trade. Tape /accepttrade pour accepter."), target.getUUID();
                sender.sendSystemMessage(Component.literal("Invitation envoyée à " + target.getScoreboardName());, sender.getUUID();
                return 1;
            })));
    }
}
