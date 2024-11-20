package com.fabbe50.fogoverrides;

import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.ModFogData;
import com.fabbe50.fogoverrides.network.NetworkHandler;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.renderer.FogRenderer;

public class FogOverrides {
    public static final String MOD_ID = "fogoverrides";

    private static FogRenderer.FogData currentFogData;

    public static void init() {
        NetworkHandler.registerServerHandshake();
        ModConfig.register();
    }

    public static void clientInit() {
        NetworkHandler.registerHandlers();
        NetworkHandler.registerClientHandshake();
        KeyMappingRegistry.register(ModConfig.OPEN_CONFIG);
        ClientTickEvent.CLIENT_POST.register(instance -> {
            while (ModConfig.OPEN_CONFIG.consumeClick()) {
                instance.setScreen(ClothScreen.getConfigScreen(null));
            }
        });
    }

    public static void debugScreenInit() {
        ClientGuiEvent.DEBUG_TEXT_LEFT.register(strings -> {
            CurrentDataStorage dataStorage = CurrentDataStorage.INSTANCE;
            ModFogData dimensionData = dataStorage.getFogDataFromDimension(Utilities.getCurrentDimensionLocation());
            ModFogData fogData = dataStorage.getBiomeFogData(Utilities.getCurrentBiomeLocation());
            strings.add("Fog Overrides: " +
                    (fogData.isOverrideGameFog() ? "Biome" : (dimensionData.isOverrideGameFog() ? "Dimension" : "Vanilla")) +
                    (dataStorage.isOnFogOverridesEnabledServer() ? " (" + (dataStorage.isIntegratedServer() ? "Integrated " : "") + "Server)" : " (Client)"));
            strings.add("Location: {" + ("Dimension: " + Utilities.getCurrentDimensionLocation()) + "}, {Biome: " + Utilities.getCurrentBiomeLocation() + "}");
            strings.add("Fog Data: " +
                    "{Near: " + currentFogData.start + "}, " +
                    "{Far: " + currentFogData.end + "}, " +
                    "{Shape: " + currentFogData.shape.name() + "}, " +
                    "{Mode: " + currentFogData.mode.name() + "}");
            strings.add("Colors: " +
                    "{Fog: " + (fogData.isOverrideGameFog() && fogData.isOverrideFogColor() ? Utilities.getFormattedColor(fogData.getFogColor()) :
                    dimensionData.isOverrideGameFog() && dimensionData.isOverrideFogColor() ? Utilities.getFormattedColor(dimensionData.getFogColor()) : "Vanilla") + "}, " +
                    "{Sky: " + (fogData.isOverrideGameFog() && fogData.isOverrideSkyColor() ? Utilities.getFormattedColor(fogData.getSkyColor()) :
                    dimensionData.isOverrideGameFog() && dimensionData.isOverrideSkyColor() ? Utilities.getFormattedColor(dimensionData.getSkyColor()) : "Vanilla") + "}, " +
                    "{Water: " + (fogData.isOverrideGameFog() && fogData.isOverrideWaterColor() ? Utilities.getFormattedColor(fogData.getWaterColor()) :
                    dimensionData.isOverrideGameFog() && dimensionData.isOverrideWaterColor() ? Utilities.getFormattedColor(dimensionData.getWaterColor()) : "Vanilla") + "}, " +
                    "{Water Fog: " + (fogData.isOverrideGameFog() && fogData.isOverrideWaterFogColor() ? Utilities.getFormattedColor(fogData.getWaterFogColor()) :
                    dimensionData.isOverrideGameFog() && dimensionData.isOverrideWaterFogColor() ? Utilities.getFormattedColor(dimensionData.getWaterFogColor()) : "Vanilla") + "}");
        });
    }

    public static void setCurrentFogData(FogRenderer.FogData fogData) {
        currentFogData = fogData;
    }
}
