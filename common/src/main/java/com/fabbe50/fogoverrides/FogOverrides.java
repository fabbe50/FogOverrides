package com.fabbe50.fogoverrides;

import com.fabbe50.fogoverrides.commands.CommandFogOverrides;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.F3Information;
import com.fabbe50.fogoverrides.data.ModRegistry;
import com.fabbe50.fogoverrides.data.checker.Checkers;
import com.fabbe50.fogoverrides.network.NetworkHandler;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

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
        LifecycleEvent.SERVER_STARTED.register(server -> {
            Level level = server.overworld();
            loadLevelRegistry(level);
            ModConfig.loadMainConfig();
        });
    }

    public static void clientInit() {
        Checkers.init();
        NetworkHandler.registerClientHandlers();
        NetworkHandler.registerClientHandshake();
        KeyMappingRegistry.register(ModConfigClient.INSTANCE.TOGGLE_MOD);
        KeyMappingRegistry.register(ModConfigClient.INSTANCE.OPEN_CONFIG);
        ClientTickEvent.CLIENT_POST.register(instance -> {
            while (ModConfigClient.INSTANCE.TOGGLE_MOD.consumeClick()) {
                ModConfig.INSTANCE.modActive = !ModConfig.INSTANCE.modActive;
                if (instance.player != null) {
                    Component state = ModConfig.INSTANCE.modActive ? Component.translatable("text.fogoverrides.setting.enabled").withStyle(ChatFormatting.GREEN) : Component.translatable("text.fogoverrides.setting.disabled").withStyle(ChatFormatting.RED);
                    instance.player.displayClientMessage(Component.translatable("text.fogoverrides.option.toggle_mod.changed", state), true);
                }
            }
            while (ModConfigClient.INSTANCE.OPEN_CONFIG.consumeClick()) {
                instance.setScreen(ClothScreen.getConfigScreen(null));
            }
        });
        ClientLifecycleEvent.CLIENT_STARTED.register(minecraft -> {
            ModConfig.loadMainConfig();
        });
    }

    public static void serverInit() {
        NetworkHandler.registerServerHandlers();
    }

    public static void loadLevelRegistry(Level level) {
        RegistryAccess registryAccess = level.registryAccess();
        try {
            Registry<Level> dimensions = registryAccess.lookupOrThrow(Registries.DIMENSION);
            for (ResourceLocation dimension : dimensions.keySet()) {
                ModRegistry.addDimensionToList(dimension);
            }
        } catch (Exception e) {
            Log.error("Dimensions couldn't load.");
        }
        try {
            Registry<Biome> biomes = registryAccess.lookupOrThrow(Registries.BIOME);
            for (ResourceLocation biome : biomes.keySet()) {
                ModRegistry.addBiomeToList(biome);
            }
        } catch (Exception e) {
            Log.error("Biomes couldn't load.");
        }
    }

    public static void debugScreenInit() {
        ClientGuiEvent.DEBUG_TEXT_LEFT.register(strings -> {
            strings.addAll(F3Information.getDebugInformation());
        });
    }

    public static ResourceLocation location(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }
}
