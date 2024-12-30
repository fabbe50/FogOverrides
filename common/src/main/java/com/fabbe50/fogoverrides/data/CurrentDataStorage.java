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

    private boolean spectatorHasModFog = false;
    private float spectatorNearDistance = -1f;
    private float spectatorFarDistance = -1f;
    private float spectatorWaterNearDistance = -1f;
    private float spectatorWaterFarDistance = -1f;
    private float spectatorLavaNearDistance = -1f;
    private float spectatorLavaFarDistance = -1f;
    private boolean creativeHasModFog = false;
    private float creativeNearDistance = -1f;
    private float creativeFarDistance = -1f;
    private float creativeWaterNearDistance = -1f;
    private float creativeWaterFarDistance = -1f;
    private float creativeLavaNearDistance = -1f;
    private float creativeLavaFarDistance = -1f;

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
    
    public boolean getSpectatorHasModFog() {
        return isOnFogOverridesEnabledServer && !integratedServer ? spectatorHasModFog : ModConfig.spectatorHasModFog;
    }
    
    public float getSpectatorNearDistance() {
        return isOnFogOverridesEnabledServer && !integratedServer ? spectatorNearDistance : ModConfig.spectatorNearDistance;
    }
    
    public float getSpectatorFarDistance() {
        return isOnFogOverridesEnabledServer && !integratedServer ? spectatorFarDistance : ModConfig.spectatorFarDistance;
    }
    
    public float getSpectatorWaterNearDistance() {
        return isOnFogOverridesEnabledServer && !integratedServer ? spectatorWaterNearDistance : ModConfig.spectatorWaterNearDistance;
    }
    
    public float getSpectatorWaterFarDistance() {
        return isOnFogOverridesEnabledServer && !integratedServer ? spectatorWaterFarDistance : ModConfig.spectatorWaterFarDistance;
    }
    
    public float getSpectatorLavaNearDistance() {
        return isOnFogOverridesEnabledServer && !integratedServer ? spectatorLavaNearDistance : ModConfig.spectatorLavaNearDistance;
    }
    
    public float getSpectatorLavaFarDistance() {
        return isOnFogOverridesEnabledServer && !integratedServer ? spectatorLavaFarDistance : ModConfig.spectatorLavaFarDistance;
    }

    public boolean getCreativeHasModFog() {
        return isOnFogOverridesEnabledServer && !integratedServer ? creativeHasModFog : ModConfig.creativeHasModFog;
    }

    public float getCreativeNearDistance() {
        return isOnFogOverridesEnabledServer && !integratedServer ? creativeNearDistance : ModConfig.creativeNearDistance;
    }

    public float getCreativeFarDistance() {
        return isOnFogOverridesEnabledServer && !integratedServer ? creativeFarDistance : ModConfig.creativeFarDistance;
    }

    public float getCreativeWaterNearDistance() {
        return isOnFogOverridesEnabledServer && !integratedServer ? creativeWaterNearDistance : ModConfig.creativeWaterNearDistance;
    }

    public float getCreativeWaterFarDistance() {
        return isOnFogOverridesEnabledServer && !integratedServer ? creativeWaterFarDistance : ModConfig.creativeWaterFarDistance;
    }

    public float getCreativeLavaNearDistance() {
        return isOnFogOverridesEnabledServer && !integratedServer ? creativeLavaNearDistance : ModConfig.creativeLavaNearDistance;
    }

    public float getCreativeLavaFarDistance() {
        return isOnFogOverridesEnabledServer && !integratedServer ? creativeLavaFarDistance : ModConfig.creativeLavaFarDistance;
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

    public void updateSpectatorSettings(boolean spectatorHasModFog, float spectatorNearDistance, float spectatorFarDistance, float spectatorWaterNearDistance, float spectatorWaterFarDistance, float spectatorLavaNearDistance, float spectatorLavaFarDistance) {
        this.spectatorHasModFog = spectatorHasModFog;
        this.spectatorNearDistance = spectatorNearDistance;
        this.spectatorFarDistance = spectatorFarDistance;
        this.spectatorWaterNearDistance = spectatorWaterNearDistance;
        this.spectatorWaterFarDistance = spectatorWaterFarDistance;
        this.spectatorLavaNearDistance = spectatorLavaNearDistance;
        this.spectatorLavaFarDistance = spectatorLavaFarDistance;
    }
    
    public void updateCreativeSettings(boolean creativeHasModFog, float creativeNearDistance, float creativeFarDistance, float creativeWaterNearDistance, float creativeWaterFarDistance, float creativeLavaNearDistance, float creativeLavaFarDistance) {
        this.creativeHasModFog = creativeHasModFog;
        this.creativeNearDistance = creativeNearDistance;
        this.creativeFarDistance = creativeFarDistance;
        this.creativeWaterNearDistance = creativeWaterNearDistance;
        this.creativeWaterFarDistance = creativeWaterFarDistance;
        this.creativeLavaNearDistance = creativeLavaNearDistance;
        this.creativeLavaFarDistance = creativeLavaFarDistance;
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
