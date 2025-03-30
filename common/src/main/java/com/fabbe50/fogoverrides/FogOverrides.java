package com.fabbe50.fogoverrides;

import com.fabbe50.fogoverrides.commands.CommandFogOverrides;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.F3Information;
import com.fabbe50.fogoverrides.data.checker.Checkers;
import com.fabbe50.fogoverrides.network.NetworkHandler;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

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
        EnvExecutor.runInEnv(Env.CLIENT, () -> FogOverrides::clientInit);
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
        ClientLifecycleEvent.CLIENT_STARTED.register(minecraft -> {

            ModConfig.loadMainConfig();
        });
    }

    public static void serverInit() {
        LifecycleEvent.SERVER_STARTED.register(minecraftServer -> {
            ModConfig.loadMainConfig();
        });
    }

    public static void debugScreenInit() {
        ClientGuiEvent.DEBUG_TEXT_LEFT.register(strings -> {
            strings.addAll(F3Information.getDebugInformation());
        });
    }

    public static ResourceLocation location(String name) {
        return new ResourceLocation(MOD_ID, name);
    }
}
