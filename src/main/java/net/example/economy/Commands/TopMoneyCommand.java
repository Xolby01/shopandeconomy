package net.example.economy.Commands;

import com.mojang.brigadier.CommandDispatcher;
import net.example.economy.EconomyMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.Map;
import java.util.UUID;

public class TopMoneyCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("topmoney").executes(ctx -> {
            Map<UUID, Long> top = EconomyMod.moneyManager.top(10);
            if (top.isEmpty()) {
                ctx.getSource().sendSuccess(() -> Component.literal("Aucun solde enregistré."), false);
                return 1;
            }
            int i = 1;
            for (Map.Entry<UUID, Long> e : top.entrySet()) {
                ctx.getSource().sendSuccess(
                    () -> Component.literal(i + ". " + e.getKey() + " -> " + e.getValue()),
                    false
                );
                i++;
            }
            return 1;
        }));
    }
}
