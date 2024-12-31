package com.fabbe50.fogoverrides.data;

import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.ModConfig.CalculationSetting;
import com.fabbe50.fogoverrides.Utilities;
import dev.architectury.registry.level.biome.BiomeModifications;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CurrentDataStorage {
    public static CurrentDataStorage INSTANCE = new CurrentDataStorage();

    private boolean isOnFogOverridesEnabledServer = false;
    private boolean integratedServer = false;
    
    private final Map<String, ModFogData> biomeStorage = new HashMap<>();

    private ModFogData overworldFogData = Utilities.getDefaultFogData();
    private ModFogData netherFogData = Utilities.getDefaultFogData();
    private ModFogData theEndFogData = Utilities.getDefaultFogData();

    private GameModeSettings spectatorSettings = Utilities.getDefaultGameModeSettings();
    private GameModeSettings creativeSettings = Utilities.getDefaultGameModeSettings();

    private boolean waterFogEnabled = true;
    private boolean lavaFogEnabled = true;

    private int cloudHeight = 192;

    private boolean renderWaterOverlay = true;
    private boolean renderFireOverlay = true;
    private int fireOverlayOffset = 0;
    private int firePotionOverlayOffset = -25;

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
        return isOnFogOverridesEnabledServer ? (biomeStorage.get(location.toString()) == null ? ModConfig.getFogDataFromBiomeLocation(location.toString()) : biomeStorage.get(location.toString())) : ModConfig.getFogDataFromBiomeLocation(location.toString());
    }

    public ModFogData getFogDataFromDimension(ResourceLocation dimension) {
        if (dimension == null) {
            return Utilities.getDefaultFogData();
        }
//        System.out.println(dimension.getNamespace() + ":" + dimension.getPath());
        if (dimension.equals(Utilities.getOverworld())) {
            return isOnFogOverridesEnabledServer && !integratedServer ? overworldFogData : ModConfig.overworldFogData;
        } else if (dimension.equals(Utilities.getNether())) {
            return isOnFogOverridesEnabledServer && !integratedServer ? netherFogData : ModConfig.netherFogData;
        } else if (dimension.equals(Utilities.getTheEnd())) {
            return isOnFogOverridesEnabledServer && !integratedServer ? theEndFogData : ModConfig.theEndFogData;
        }
        return Utilities.getDefaultFogData();
    }

    public CalculationSetting getCalculationSetting() {
        return ModConfig.calculationSetting;
    }
    
    public GameModeSettings getSpectatorSettings() {
        return isOnFogOverridesEnabledServer && !integratedServer ? spectatorSettings : ModConfig.spectatorSettings;
    }

    public GameModeSettings getCreativeSettings() {
        return isOnFogOverridesEnabledServer && !integratedServer ? creativeSettings : ModConfig.creativeSettings;
    }

    public boolean isWaterFogEnabled() {
        return isOnFogOverridesEnabledServer && !integratedServer ? waterFogEnabled : ModConfig.waterFogEnabled;
    }

    public boolean isLavaFogEnabled() {
        return isOnFogOverridesEnabledServer && !integratedServer ? lavaFogEnabled : ModConfig.lavaFogEnabled;
    }

    public int getCloudHeight() {
        return isOnFogOverridesEnabledServer && !integratedServer ? cloudHeight : ModConfig.cloudHeight;
    }

    public boolean isRenderWaterOverlay() {
        return isOnFogOverridesEnabledServer && !integratedServer ? renderWaterOverlay : ModConfig.renderWaterOverlay;
    }

    public boolean isRenderFireOverlay() {
        return isOnFogOverridesEnabledServer && !integratedServer ? renderFireOverlay : ModConfig.renderFireOverlay;
    }

    public int getFireOverlayOffset() {
        return isOnFogOverridesEnabledServer && !integratedServer ? fireOverlayOffset : ModConfig.fireOverlayOffset;
    }

    public int getFirePotionOverlayOffset() {
        return isOnFogOverridesEnabledServer && !integratedServer ? firePotionOverlayOffset : ModConfig.firePotionOverlayOffset;
    }

    public void addToBiomeStorage(ResourceLocation location, ModFogData fogData) {
        biomeStorage.put(location.toString(), fogData);
    }

    public void refreshBiomeStorage(Map<String, ModFogData> biomeStorage) {
        if (isOnFogOverridesEnabledServer && !integratedServer) {
            this.biomeStorage.clear();
            this.biomeStorage.putAll(biomeStorage);
        }
    }

    public void refreshWaterColor(ResourceLocation location, ModFogData fogData) {
        BiomeModifications.replaceProperties(biomeContext -> {
            Optional<ResourceLocation> optionalBiomeLocation = biomeContext.getKey();
            return optionalBiomeLocation.map(resourceLocation -> resourceLocation.equals(location)).orElse(false);
        }, (biomeContext, mutable) -> mutable.getEffectsProperties().setWaterColor(fogData.getWaterColor()));
    }

    public Map<String, ModFogData> getBiomeStorage() {
        return  isOnFogOverridesEnabledServer && !integratedServer ? biomeStorage : ModConfig.getBiomeStorage();
    }

    public void updateGameModeSettings(String gameMode, GameModeSettings settings) {
        switch (gameMode) {
            case "spectator" -> updateSpectatorSettings(settings);
            case "creative" -> updateCreativeSettings(settings);
        }
    }

    public void updateSpectatorSettings(GameModeSettings spectatorSettings) {
        this.spectatorSettings = spectatorSettings;
    }
    
    public void updateCreativeSettings(GameModeSettings creativeSettings) {
        this.creativeSettings = creativeSettings;
    }

    public void updateOverworldFogData(ModFogData overworldFogData) {
        this.overworldFogData = overworldFogData;
    }

    public void updateNetherFogData(ModFogData netherFogData) {
        this.netherFogData = netherFogData;
    }

    public void updateTheEndFogData(ModFogData theEndFogData) {
        this.theEndFogData = theEndFogData;
    }

    public void updateLiquids(boolean waterFogEnabled, boolean lavaFogEnabled) {
        this.waterFogEnabled = waterFogEnabled;
        this.lavaFogEnabled = lavaFogEnabled;
    }

    public void updateCloudHeight(int cloudHeight) {
        this.cloudHeight = cloudHeight;
    }

    public void updateOverlays(boolean renderWaterOverlay, boolean renderFireOverlay, int fireOverlayOffset, int firePotionOverlayOffset) {
        this.renderWaterOverlay = renderWaterOverlay;
        this.renderFireOverlay = renderFireOverlay;
        this.fireOverlayOffset = fireOverlayOffset;
        this.firePotionOverlayOffset = firePotionOverlayOffset;
    }
}
