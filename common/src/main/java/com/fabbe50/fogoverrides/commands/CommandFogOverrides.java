package com.fabbe50.fogoverrides.commands;

import com.fabbe50.fogoverrides.Log;
import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.network.NetworkHandler;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class CommandFogOverrides {
    public CommandFogOverrides() {}

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(
                Commands.literal("fogoverrides").requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                        .then(Commands.literal("reload").executes(context -> reload(context.getSource())))
                        .then(Commands.literal("config").executes(context -> config(context.getSource())))
        );
    }

    private static int reload(CommandSourceStack commandSourceStack) {
        ModConfig.load(ModConfig.getConfigFile());
        int modUsersUpdated = NetworkHandler.sendSettingsToAllPlayers();

        if (modUsersUpdated == 1) {
            commandSourceStack.sendSuccess(() -> Component.translatable("text.fogoverrides.commands.reload.success.single", modUsersUpdated), true);
        } else {
            commandSourceStack.sendSuccess(() -> Component.translatable("text.fogoverrides.commands.reload.success.multiple", modUsersUpdated), true);
        }

        return modUsersUpdated;
    }

    private static int config(CommandSourceStack commandSourceStack) {
        if (CurrentDataStorage.INSTANCE.isIntegratedServer()) {
            commandSourceStack.sendFailure(Component.translatable("text.fogoverrides.commands.config.failure"));
        }
        NetworkHandler.openConfigScreenOnClient(commandSourceStack.getPlayer());

        Log.info("Opened Config");

        return 1;
    }
}
