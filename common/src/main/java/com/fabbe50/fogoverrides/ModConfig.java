package com.fabbe50.fogoverrides;

import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.FogSetting;
import com.fabbe50.fogoverrides.data.GameModeSettings;
import com.fabbe50.fogoverrides.data.ModFogData;
import dev.architectury.platform.Platform;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ModConfig {
    public static final int FOG_START_MAX = 1023;
    public static final int FOG_END_MAX = 1024;
    public static final int CHUNK_SIZE = 16;
    public static final int PERCENTAGE_DIVIDER = 512;

    public static final float UNDERWATER_NEAR_DEFAULT = -8;
    public static final float UNDERWATER_FAR_DEFAULT = 96;
    public static final float LAVA_NEAR_SPECTATOR_DEFAULT = -8;
    public static final float LAVA_FAR_SPECTATOR_MULTIPLIER_DEFAULT = 0.5f;
    public static final float LAVA_NEAR_DEFAULT = 0.25f;
    public static final float LAVA_FAR_DEFAULT = 1;
    public static final float LAVA_NEAR_POTION_DEFAULT = 0;
    public static final float LAVA_FAR_POTION_DEFAULT = 3;
    public static final float POWDER_SNOW_NEAR_DEFAULT = 0;
    public static final float POWDER_SNOW_FAR_DEFAULT = 2;
    public static final float POWDER_SNOW_NEAR_SPECTATOR_DEFAULT = -8;
    public static final float POWDER_SNOW_FAR_SPECTATOR_MULTIPLIER_DEFAULT = 0.5f;
    public static final float SPECIAL_FOG_NEAR_MULTIPLIER_DEFAULT = 0.05f;
    public static final float SPECIAL_FOG_FAR_MAX_DISTANCE_DEFAULT = 192;
    public static final float SPECIAL_FOG_FAR_MULTIPLIER_DEFAULT = 0.5f;

    private static File configFile;

    private static long serverSettingsLastUpdated = 0L;

    private static final List<ResourceLocation> biomeList = new ArrayList<>();
    private static final Map<String, ModFogData> biomeStorage = new HashMap<>();

    public static List<String> presets = new ArrayList<>();

    // Config Values
    public static CalculationSetting calculationSetting = CalculationSetting.BLOCKS;

    public static GameModeSettings spectatorSettings = Utilities.getDefaultGameModeSettings();
    public static GameModeSettings creativeSettings = Utilities.getDefaultGameModeSettings();

    public static ModFogData overworldFogData = Utilities.getDefaultFogData();
    public static ModFogData netherFogData = Utilities.getDefaultFogData();
    public static ModFogData theEndFogData = Utilities.getDefaultFogData();

    public static boolean waterFogEnabled = true;
    public static boolean lavaFogEnabled = true;

    public static int cloudHeight = 192;

    public static boolean renderWaterOverlay = true;
    public static boolean renderFireOverlay = true;
    public static int fireOverlayOffset = 0;
    public static int firePotionOverlayOffset = -25;

    public static void register() {
        configFile = new File(Platform.getConfigFolder().toFile(), "fogoverrides.properties");
        load(configFile);
    }

    public static void load(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(fis);
            fis.close();

            serverSettingsLastUpdated = Long.parseLong((String)properties.computeIfAbsent("serverSettingsLastUpdated", o -> String.valueOf(System.currentTimeMillis())));

            calculationSetting = CalculationSetting.getSettingFromID((String) properties.computeIfAbsent("calculationSetting", o -> "blocks"));

            spectatorSettings = readGameModeSettingsFromProperties(properties, "spectator");
            creativeSettings = readGameModeSettingsFromProperties(properties, "creative");

            overworldFogData = readModFogDataFromProperties(properties, Utilities.getOverworld(), "dimension");
            netherFogData = readModFogDataFromProperties(properties, Utilities.getNether(), "dimension");
            theEndFogData = readModFogDataFromProperties(properties, Utilities.getTheEnd(), "dimension");

            waterFogEnabled = ((String) properties.computeIfAbsent("waterFogEnabled", o -> "true")).equalsIgnoreCase("true");
            lavaFogEnabled = ((String) properties.computeIfAbsent("lavaFogEnabled", o -> "true")).equalsIgnoreCase("true");

            cloudHeight = Integer.parseInt((String) properties.computeIfAbsent("cloudHeight", o -> "192"));

            renderWaterOverlay = ((String) properties.computeIfAbsent("waterOverlay", o -> "true")).equalsIgnoreCase("true");
            renderFireOverlay = ((String) properties.computeIfAbsent("fireOverlay", o -> "true")).equalsIgnoreCase("true");
            fireOverlayOffset = Integer.parseInt((String) properties.computeIfAbsent("fireOffset", o -> "0"));
            firePotionOverlayOffset = Integer.parseInt((String) properties.computeIfAbsent("firePotOffset", o -> "-25"));

            for (ResourceLocation location : biomeList) {
                ModFogData fogData = readModFogDataFromProperties(properties, location, "biome");
                updateFogData(location, fogData);
                if (!CurrentDataStorage.INSTANCE.isOnFogOverridesEnabledServer()) {
                    CurrentDataStorage.INSTANCE.refreshWaterColor(location, fogData);
                }
            }
        } catch (IOException e) {
            for (ResourceLocation location : biomeList) {
                updateFogData(location, Utilities.getDefaultFogData());
            }
            try {
                save(file);
                load(file);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            System.err.println(e.toString());
        }
    }

    public static void save(File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, false);

        Utilities.writeData(fos, "serverSettingsLastUpdated", String.valueOf(System.currentTimeMillis()));

        Utilities.writeData(fos, "calculationSetting", calculationSetting.getId());

        writeGameModeSettingsToProperties(fos, "spectator", spectatorSettings);
        writeGameModeSettingsToProperties(fos, "creative", creativeSettings);

        writeModFogDataToProperties(fos, Utilities.getOverworld(), overworldFogData, "dimension");
        writeModFogDataToProperties(fos, Utilities.getNether(), netherFogData, "dimension");
        writeModFogDataToProperties(fos, Utilities.getTheEnd(), theEndFogData, "dimension");

        Utilities.writeData(fos, "waterFogEnabled", String.valueOf(waterFogEnabled));
        Utilities.writeData(fos, "lavaFogEnabled", String.valueOf(lavaFogEnabled));

        Utilities.writeData(fos, "cloudHeight", String.valueOf(cloudHeight));

        Utilities.writeData(fos, "waterOverlay", String.valueOf(renderWaterOverlay));
        Utilities.writeData(fos, "fireOverlay", String.valueOf(renderFireOverlay));
        Utilities.writeData(fos, "fireOffset", String.valueOf(fireOverlayOffset));
        Utilities.writeData(fos, "firePotOffset", String.valueOf(firePotionOverlayOffset));

        for (String location : biomeStorage.keySet()) {
            ModFogData data = biomeStorage.get(location);
            writeModFogDataToProperties(fos, ResourceLocation.parse(location), data, "biome");
        }
        fos.close();
    }

    private static ModFogData readModFogDataFromProperties(Properties properties, ResourceLocation location, String prefix) {
        ModFogData defaults = Utilities.getDefaultFogData();

        if (location != null) {
            boolean overrideFog = ((String) properties.computeIfAbsent("overrideFog_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.isOverrideGameFog()))).equalsIgnoreCase("true");
            boolean isEnabled = ((String) properties.computeIfAbsent("fogEnabled_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.isFogEnabled()))).equalsIgnoreCase("true");
            float nearDistance = Float.parseFloat((String) properties.computeIfAbsent("nearDistance_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getNearDistance())));
            float farDistance = Float.parseFloat((String) properties.computeIfAbsent("farDistance_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getFarDistance())));
            boolean overrideSkyColor = ((String) properties.computeIfAbsent("overrideSkyColor_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.isOverrideSkyColor()))).equalsIgnoreCase("true");
            int skyColor = Integer.parseInt((String) properties.computeIfAbsent("skyColor_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getSkyColor())));
            boolean overrideFogColor = ((String) properties.computeIfAbsent("overrideFogColor_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.isOverrideFogColor()))).equalsIgnoreCase("true");
            int fogColor = Integer.parseInt((String) properties.computeIfAbsent("fogColor_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getFogColor())));
            boolean overrideWaterFog = ((String) properties.computeIfAbsent("overrideWaterFog_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.isOverrideWaterFog()))).equalsIgnoreCase("true");
            float waterNearDistance = Float.parseFloat((String) properties.computeIfAbsent("waterNearDistance_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getWaterNearDistance())));
            float waterFarDistance = Float.parseFloat((String) properties.computeIfAbsent("waterFarDistance_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getWaterFarDistance())));
            boolean waterPotionEffect = ((String) properties.computeIfAbsent("waterPotionEffect_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.isWaterPotionEffect()))).equalsIgnoreCase("true");
            float waterPotionNearDistance = Float.parseFloat((String) properties.computeIfAbsent("waterPotionNearDistance_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getWaterPotionNearDistance())));
            float waterPotionFarDistance = Float.parseFloat((String) properties.computeIfAbsent("waterPotionFarDistance_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getWaterPotionFarDistance())));
            boolean overrideWaterColor = ((String) properties.computeIfAbsent("overrideWaterColor_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.isOverrideWaterColor()))).equalsIgnoreCase("true");
            int waterColor = Integer.parseInt((String) properties.computeIfAbsent("waterColor_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getWaterColor())));
            boolean overrideWaterFogColor = ((String) properties.computeIfAbsent("overrideWaterFogColor_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getWaterFogColor()))).equalsIgnoreCase("true");
            int waterFogColor = Integer.parseInt((String) properties.computeIfAbsent("waterFogColor_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getWaterFogColor())));
            boolean overrideLavaFog = ((String) properties.computeIfAbsent("overrideLavaFog_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.isOverrideLavaFog()))).equalsIgnoreCase("true");
            float lavaNearDistance = Float.parseFloat((String) properties.computeIfAbsent("lavaNearDistance_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getLavaNearDistance())));
            float lavaFarDistance = Float.parseFloat((String) properties.computeIfAbsent("lavaFarDistance_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getLavaFarDistance())));
            boolean lavaPotionEffect = ((String) properties.computeIfAbsent("lavaPotionEffect_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.isLavaPotionEffect()))).equalsIgnoreCase("true");
            float lavaPotionNearDistance = Float.parseFloat((String) properties.computeIfAbsent("lavaPotionNearDistance_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getLavaPotionNearDistance())));
            float lavaPotionFarDistance = Float.parseFloat((String) properties.computeIfAbsent("lavaPotionFarDistance_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), o -> String.valueOf(defaults.getLavaPotionFarDistance())));

            ModFogData fogData = new ModFogData(overrideFog, isEnabled, nearDistance, farDistance, overrideSkyColor, skyColor, overrideFogColor, fogColor, overrideWaterFog, waterNearDistance, waterFarDistance, overrideWaterColor, waterColor, overrideWaterFogColor, waterFogColor);
            fogData.setWaterPotionEffect(waterPotionEffect);
            fogData.setWaterPotionNearDistance(waterPotionNearDistance);
            fogData.setWaterPotionFarDistance(waterPotionFarDistance);
            fogData.setOverrideLavaFog(overrideLavaFog);
            fogData.setLavaNearDistance(lavaNearDistance);
            fogData.setLavaFarDistance(lavaFarDistance);
            fogData.setLavaPotionEffect(lavaPotionEffect);
            fogData.setLavaPotionNearDistance(lavaPotionNearDistance);
            fogData.setLavaPotionFarDistance(lavaPotionFarDistance);
            return fogData;
        }
        return defaults;
    }

    private static void writeModFogDataToProperties(FileOutputStream fos, ResourceLocation location, ModFogData data, String prefix) throws IOException {
        Utilities.writeData(fos, "overrideFog_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.isOverrideGameFog()));
        Utilities.writeData(fos, "fogEnabled_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.isFogEnabled()));
        Utilities.writeData(fos, "nearDistance_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getNearDistance()));
        Utilities.writeData(fos, "farDistance_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getFarDistance()));
        Utilities.writeData(fos, "overrideSkyColor_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.isOverrideSkyColor()));
        Utilities.writeData(fos, "skyColor_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getSkyColor()));
        Utilities.writeData(fos, "overrideFogColor_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.isOverrideFogColor()));
        Utilities.writeData(fos, "fogColor_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getFogColor()));
        Utilities.writeData(fos, "overrideWaterFog_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.isOverrideWaterFog()));
        Utilities.writeData(fos, "waterNearDistance_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getWaterNearDistance()));
        Utilities.writeData(fos, "waterFarDistance_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getWaterFarDistance()));
        Utilities.writeData(fos, "waterPotionEffect_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.isWaterPotionEffect()));
        Utilities.writeData(fos, "waterPotionNearDistance_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getWaterPotionNearDistance()));
        Utilities.writeData(fos, "waterPotionFarDistance_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getWaterPotionFarDistance()));
        Utilities.writeData(fos, "overrideWaterColor_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.isOverrideWaterColor()));
        Utilities.writeData(fos, "waterColor_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getWaterColor()));
        Utilities.writeData(fos, "overrideWaterFogColor_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.isOverrideWaterFogColor()));
        Utilities.writeData(fos, "waterFogColor_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getWaterFogColor()));
        Utilities.writeData(fos, "overrideLavaFog_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.isOverrideLavaFog()));
        Utilities.writeData(fos, "lavaNearDistance_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getLavaNearDistance()));
        Utilities.writeData(fos, "lavaFarDistance_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getLavaFarDistance()));
        Utilities.writeData(fos, "lavaPotionEffect_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.isLavaPotionEffect()));
        Utilities.writeData(fos, "lavaPotionNearDistance_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getLavaPotionNearDistance()));
        Utilities.writeData(fos, "lavaPotionFarDistance_" + prefix + "_" + location.getNamespace() + "_" + location.getPath(), String.valueOf(data.getLavaPotionFarDistance()));
    }

    private static GameModeSettings readGameModeSettingsFromProperties(Properties properties, String prefix) {
        GameModeSettings.FogMode fogMode = Setting.readFogMode(properties, prefix + "FogMode", GameModeSettings.FogMode.MOD_FOG);
        FogSetting terrainFog = Setting.readSimpleFogSetting(properties, prefix + "Terrain", Utilities.getDefaultTerrain());
        FogSetting waterFog = Setting.readSimpleFogSetting(properties, prefix + "Water", Utilities.getDefaultWater());
        FogSetting lavaFog = Setting.readSimpleFogSetting(properties, prefix + "Lava", Utilities.getDefaultLava());
        return new GameModeSettings(fogMode, terrainFog, waterFog, lavaFog);
    }

    private static void writeGameModeSettingsToProperties(FileOutputStream fos, String prefix, GameModeSettings gameModeSettings) throws IOException {
        Utilities.writeData(fos, prefix + "FogMode", gameModeSettings.getFogMode().getId());
        writeFogSetting(fos, prefix + "Terrain", gameModeSettings.getTerrainFog());
        writeFogSetting(fos, prefix + "Water", gameModeSettings.getWaterFog());
        writeFogSetting(fos, prefix + "Lava", gameModeSettings.getLavaFog());
    }

    private static void writeFogSetting(FileOutputStream fos, String prefix, FogSetting fogSetting) throws IOException {
        Utilities.writeData(fos, prefix + "IsEnabled", String.valueOf(fogSetting.isEnabled()));
        Utilities.writeData(fos, prefix + "NearDistance", String.valueOf(fogSetting.getNearDistance()));
        Utilities.writeData(fos, prefix + "FarDistance", String.valueOf(fogSetting.getFarDistance()));
        Utilities.writeData(fos, prefix + "FogColor", String.valueOf(fogSetting.getColor()));
    }

    public static void addBiomeToList(ResourceLocation location) {
        biomeList.add(location);
    }

    private static void addBiomeToStorage(ResourceLocation location, ModFogData fogData) {
        biomeStorage.put(location.toString(), fogData);
    }

    private static void replaceBiomeInStorage(ResourceLocation location, ModFogData fogData) {
        biomeStorage.replace(location.toString(), fogData);
    }

    public static void updateFogData(ResourceLocation location, ModFogData fogData) {
        if (biomeStorage.containsKey(location.toString())) {
            replaceBiomeInStorage(location, fogData);
        } else {
            addBiomeToStorage(location, fogData);
        }
    }

    public static ModFogData getFogDataFromBiomeLocation(String biome) {
        return biomeStorage.getOrDefault(biome, Utilities.getDefaultFogData());
    }

    public static ModFogData getFogDataFromDimension(ResourceLocation dimension) {
        if (dimension.getPath().equals(Utilities.getOverworld().getPath())) {
            return ModConfig.overworldFogData;
        } else if (dimension.getPath().equals(Utilities.getNether().getPath())) {
            return ModConfig.netherFogData;
        } else if (dimension.getPath().equals(Utilities.getTheEnd().getPath())) {
            return ModConfig.theEndFogData;
        }
        return null;
    }

    public static Map<String, ModFogData> getBiomeStorage() {
        return biomeStorage;
    }

    public static long getServerSettingsLastUpdated() {
        return serverSettingsLastUpdated;
    }

    public static File getConfigFile() {
        return configFile;
    }

    public enum CalculationSetting {
        BLOCKS("blocks", Component.translatable("text.fogoverrides.option.calculation-setting.blocks")),
        PERCENT("percent", Component.translatable("text.fogoverrides.option.calculation-setting.percent")),
        PERCENT_BLOCKS("percent_blocks", Component.translatable("text.fogoverrides.option.calculation-setting.percent-blocks"));

        private final String id;
        private final Component name;
        CalculationSetting(String id, Component name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public Component getName() {
            return name;
        }

        public static CalculationSetting getSettingFromID(String id) {
            for (CalculationSetting setting : values()) {
                if (id.equals(setting.id)) {
                    return setting;
                }
            }
            return BLOCKS;
        }
    }
}
