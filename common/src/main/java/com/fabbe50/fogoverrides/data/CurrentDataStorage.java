package com.fabbe50.fogoverrides.data;

import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.ModConfig.CalculationSetting;
import com.fabbe50.fogoverrides.Utilities;
import dev.architectury.registry.level.biome.BiomeModifications;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;

public class CurrentDataStorage {
    public static CurrentDataStorage INSTANCE = new CurrentDataStorage();
    private static ModConfig SERVER_SETTINGS = new ModConfig();

    private boolean isOnFogOverridesEnabledServer = false;
    private boolean integratedServer = false;

    public static void init() {
    }

    public void setOnFogOverridesEnabledServer(boolean onFogOverridesEnabledServer) {
        isOnFogOverridesEnabledServer = onFogOverridesEnabledServer;
    }

    public boolean isOnFogOverridesEnabledServer() {
        return isOnFogOverridesEnabledServer;
    }

    public void setIntegratedServer(boolean integratedServer) {
        this.integratedServer = integratedServer;
    }

    public boolean isIntegratedServer() {
        return integratedServer;
    }

    public ModFogData getBiomeFogData(ResourceLocation location) {
        return isOnFogOverridesEnabledServer && !integratedServer ? (SERVER_SETTINGS.biomeStorage.get(location.toString()) == null ? ModConfig.INSTANCE.getFogDataFromBiomeLocation(location.toString()) : SERVER_SETTINGS.biomeStorage.get(location.toString())) : ModConfig.INSTANCE.getFogDataFromBiomeLocation(location.toString());
    }

    public ModFogData getDimensionFogData(ResourceLocation dimension) {
        if (dimension == null) {
            return Utilities.getDefaultFogData();
        }
//        System.out.println(dimension.getNamespace() + ":" + dimension.getPath());
        if (dimension.equals(Utilities.getOverworld())) {
            return isOnFogOverridesEnabledServer && !integratedServer ? SERVER_SETTINGS.overworldFogData : ModConfig.INSTANCE.overworldFogData;
        } else if (dimension.equals(Utilities.getNether())) {
            return isOnFogOverridesEnabledServer && !integratedServer ? SERVER_SETTINGS.netherFogData : ModConfig.INSTANCE.netherFogData;
        } else if (dimension.equals(Utilities.getTheEnd())) {
            return isOnFogOverridesEnabledServer && !integratedServer ? SERVER_SETTINGS.theEndFogData : ModConfig.INSTANCE.theEndFogData;
        }
        return Utilities.getDefaultFogData();
    }

    public CalculationSetting getCalculationSetting() {
        return ModConfig.INSTANCE.calculationSetting;
    }
    
    public GameModeSettings getSpectatorSettings() {
        return isOnFogOverridesEnabledServer && !integratedServer ? SERVER_SETTINGS.spectatorSettings : ModConfig.INSTANCE.spectatorSettings;
    }

    public GameModeSettings getCreativeSettings() {
        return isOnFogOverridesEnabledServer && !integratedServer ? SERVER_SETTINGS.creativeSettings : ModConfig.INSTANCE.creativeSettings;
    }

    public boolean isWaterFogEnabled() {
        return isOnFogOverridesEnabledServer && !integratedServer ? SERVER_SETTINGS.waterFogEnabled : ModConfig.INSTANCE.waterFogEnabled;
    }

    public boolean isLavaFogEnabled() {
        return isOnFogOverridesEnabledServer && !integratedServer ? SERVER_SETTINGS.lavaFogEnabled : ModConfig.INSTANCE.lavaFogEnabled;
    }

    public int getCloudHeight() {
        return isOnFogOverridesEnabledServer && !integratedServer ? SERVER_SETTINGS.cloudHeight : ModConfig.INSTANCE.cloudHeight;
    }

    public boolean isRenderWaterOverlay() {
        return isOnFogOverridesEnabledServer && !integratedServer ? SERVER_SETTINGS.renderWaterOverlay : ModConfig.INSTANCE.renderWaterOverlay;
    }

    public boolean isRenderFireOverlay() {
        return isOnFogOverridesEnabledServer && !integratedServer ? SERVER_SETTINGS.renderFireOverlay : ModConfig.INSTANCE.renderFireOverlay;
    }

    public int getFireOverlayOffset() {
        return isOnFogOverridesEnabledServer && !integratedServer ? SERVER_SETTINGS.fireOverlayOffset : ModConfig.INSTANCE.fireOverlayOffset;
    }

    public int getFirePotionOverlayOffset() {
        return isOnFogOverridesEnabledServer && !integratedServer ? SERVER_SETTINGS.firePotionOverlayOffset : ModConfig.INSTANCE.firePotionOverlayOffset;
    }

    public void addToBiomeStorage(ResourceLocation location, ModFogData fogData) {
        SERVER_SETTINGS.biomeStorage.put(location.toString(), fogData);
    }

    public void refreshBiomeStorage(Map<String, ModFogData> biomeStorage) {
        if (isOnFogOverridesEnabledServer && !integratedServer) {
            SERVER_SETTINGS.biomeStorage.clear();
            SERVER_SETTINGS.biomeStorage.putAll(biomeStorage);
        }
    }

    public void refreshWaterColor(ResourceLocation location, ModFogData fogData) {
        BiomeModifications.replaceProperties(biomeContext -> {
            Optional<ResourceLocation> optionalBiomeLocation = biomeContext.getKey();
            return optionalBiomeLocation.map(resourceLocation -> resourceLocation.equals(location)).orElse(false);
        }, (biomeContext, mutable) -> mutable.getEffectsProperties().setWaterColor(fogData.getWaterColor()));
    }

    public Map<String, ModFogData> getBiomeStorage() {
        return  isOnFogOverridesEnabledServer && !integratedServer ? SERVER_SETTINGS.biomeStorage : INSTANCE.getBiomeStorage();
    }

    public ModConfig getServerSettings() {
        return SERVER_SETTINGS;
    }

    public void updateSettings(ModConfig config) {
        SERVER_SETTINGS = config;
    }

    public void updateGameModeSettings(String gameMode, GameModeSettings settings) {
        switch (gameMode) {
            case "spectator" -> updateSpectatorSettings(settings);
            case "creative" -> updateCreativeSettings(settings);
        }
    }

    public void updateSpectatorSettings(GameModeSettings spectatorSettings) {
        SERVER_SETTINGS.spectatorSettings = spectatorSettings;
    }
    
    public void updateCreativeSettings(GameModeSettings creativeSettings) {
        SERVER_SETTINGS.creativeSettings = creativeSettings;
    }

    public void updateOverworldFogData(ModFogData overworldFogData) {
        SERVER_SETTINGS.overworldFogData = overworldFogData;
    }

    public void updateNetherFogData(ModFogData netherFogData) {
        SERVER_SETTINGS.netherFogData = netherFogData;
    }

    public void updateTheEndFogData(ModFogData theEndFogData) {
        SERVER_SETTINGS.theEndFogData = theEndFogData;
    }

    public void updateLiquids(boolean waterFogEnabled, boolean lavaFogEnabled) {
        SERVER_SETTINGS.waterFogEnabled = waterFogEnabled;
        SERVER_SETTINGS.lavaFogEnabled = lavaFogEnabled;
    }

    public void updateCloudHeight(int cloudHeight) {
        SERVER_SETTINGS.cloudHeight = cloudHeight;
    }

    public void updateOverlays(boolean renderWaterOverlay, boolean renderFireOverlay, int fireOverlayOffset, int firePotionOverlayOffset) {
        SERVER_SETTINGS.renderWaterOverlay = renderWaterOverlay;
        SERVER_SETTINGS.renderFireOverlay = renderFireOverlay;
        SERVER_SETTINGS.fireOverlayOffset = fireOverlayOffset;
        SERVER_SETTINGS.firePotionOverlayOffset = firePotionOverlayOffset;
    }
}
