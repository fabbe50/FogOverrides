package com.fabbe50.fogoverrides.commands;

import com.fabbe50.fogoverrides.network.NetworkHandler;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class CommandFogOverrides {
    public CommandFogOverrides() {}

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(Commands.literal("fogoverrides").requires(commandSourceStack -> commandSourceStack.hasPermission(2)).then(Commands.literal("reload").executes(context -> reload(context.getSource()))));
    }

    private static int reload(CommandSourceStack commandSourceStack) {
        int modUsersUpdated = NetworkHandler.sendSettingsToAllPlayers();

        if (modUsersUpdated == 1) {
            commandSourceStack.sendSuccess(() -> Component.translatable("text.fogoverrides.commands.reload.success.single", modUsersUpdated), true);
        } else {
            commandSourceStack.sendSuccess(() -> Component.translatable("text.fogoverrides.commands.reload.success.multiple", modUsersUpdated), true);
        }

        return modUsersUpdated;
    }
}
