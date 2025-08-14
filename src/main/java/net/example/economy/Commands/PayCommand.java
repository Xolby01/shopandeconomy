package net.example.economy.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.example.economy.EconomyMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class PayCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("pay")
                .then(Commands.argument("target", EntityArgument.player())
                .then(Commands.argument("amount", LongArgumentType.longArg(1))
                    .executes(ctx -> {
                        ServerPlayer sender = ctx.getSource().getPlayerOrException();
                        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                        long amount = LongArgumentType.getLong(ctx, "amount");

                        if (EconomyMod.moneyManager.withdraw(sender.getUUID(), amount)) {
                            EconomyMod.moneyManager.deposit(target.getUUID(), amount);
                            EconomyMod.moneyManager.save();

                            ctx.getSource().sendSuccess(
                                () -> Component.literal("Payé " + amount + " à " + target.getScoreboardName()),
                                false
                            );
                            target.sendSystemMessage(Component.literal("Tu as reçu " + amount + " de " + sender.getScoreboardName()));
                            return 1;
                        } else {
                            ctx.getSource().sendFailure(Component.literal("Fonds insuffisants."));
                            return 0;
                        }
                    })
                ))
        );
    }
}
