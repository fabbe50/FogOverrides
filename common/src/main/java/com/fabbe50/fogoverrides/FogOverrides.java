package com.fabbe50.fogoverrides;

import com.fabbe50.fogoverrides.commands.CommandFogOverrides;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.F3Information;
import com.fabbe50.fogoverrides.data.checker.Checkers;
import com.fabbe50.fogoverrides.network.NetworkHandler;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;

public class FogOverrides {
    public static final String MOD_ID = "fogoverrides";

    public static void init() {
        ModConfig.register();
        CurrentDataStorage.init();
        NetworkHandler.registerHandlers();
        NetworkHandler.registerServerHandshake();
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> {
            CommandFogOverrides.register(dispatcher);
        });
    }

    public static void clientInit() {
        Checkers.init();
        NetworkHandler.registerClientHandlers();
        NetworkHandler.registerClientHandshake();
        KeyMappingRegistry.register(ModConfigClient.INSTANCE.OPEN_CONFIG);
        ClientTickEvent.CLIENT_POST.register(instance -> {
            while (ModConfigClient.INSTANCE.OPEN_CONFIG.consumeClick()) {
                instance.setScreen(ClothScreen.getConfigScreen(null));
            }
        });
    }

    public static void serverInit() {
        NetworkHandler.registerServerHandlers();
    }

    public static void debugScreenInit() {
        ClientGuiEvent.DEBUG_TEXT_LEFT.register(strings -> {
            strings.addAll(F3Information.getDebugInformation());
        });
    }
}
