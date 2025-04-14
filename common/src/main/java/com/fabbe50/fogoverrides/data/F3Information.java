package com.fabbe50.fogoverrides.data;

import com.fabbe50.fogoverrides.ClientUtilities;
import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.Utilities;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class F3Information {
    public static final String title = ChatFormatting.AQUA + "Fog Overrides";
    public static final String client = ChatFormatting.DARK_AQUA + "Controller: " + ChatFormatting.DARK_GREEN;
    public static final String fogType = ChatFormatting.DARK_AQUA + "Fog Type: " + ChatFormatting.DARK_GREEN;
    public static final String location = ChatFormatting.DARK_AQUA + "Location: ";
    public static final String locationDim = ChatFormatting.GRAY + "- Dimension: " + ChatFormatting.DARK_GREEN;
    public static final String locationBiome = ChatFormatting.GRAY + "- Biome: " + ChatFormatting.DARK_GREEN;
    public static final String fogData = ChatFormatting.DARK_AQUA + "Fog Data: ";
    public static final String fogDataDistance = ChatFormatting.GRAY + "- Distance: " + ChatFormatting.DARK_GREEN;
    public static final String fogDataShapeMode = ChatFormatting.GRAY + "- Shape: " + ChatFormatting.DARK_GREEN;
    public static final String colors = ChatFormatting.DARK_AQUA + "Colors: ";
    public static final String colorFog = ChatFormatting.GRAY + "- Fog: " + ChatFormatting.DARK_GREEN;
    public static final String colorWaterFog = ChatFormatting.GRAY + "- Water Fog: " + ChatFormatting.DARK_GREEN;
    public static final String colorWeather = ChatFormatting.GRAY + "- Weather: " + ChatFormatting.DARK_GREEN;
    public static final String colorSky = ChatFormatting.GRAY + "- Sky: " + ChatFormatting.DARK_GREEN;
    public static final String colorWater = ChatFormatting.GRAY + "- Water: " + ChatFormatting.DARK_GREEN;

    private static CurrentDataStorage data;
    private static ModFogData biomeData;
    private static ModFogData dimensionData;
    private static FogRenderer.FogData currentFogData;
    private static String fogTypeData = "";
    private static int currentColor = 0;

    public static List<String> getDebugInformation() {
        List<String> strings = new ArrayList<>();
        if (!ModConfig.INSTANCE.modActive) {
            strings.add(title + ": " + ChatFormatting.RED + "DISABLED");
            return strings;
        }

        data = CurrentDataStorage.INSTANCE;
        ResourceLocation biome = ClientUtilities.getCurrentBiomeLocation();
        ResourceLocation dimension = ClientUtilities.getCurrentDimensionLocation();
        biomeData = data.getBiomeFogData(biome);
        dimensionData = data.getDimensionFogData(dimension);
        if (ModConfig.INSTANCE.advancedF3Info && data.isIntegratedServer()) {
            strings.add(title);
            strings.add(client + getClientInfo());
            strings.add(fogType + fogTypeData);
            strings.add(location);
            strings.add(locationDim + dimension);
            strings.add(locationBiome + biome);
            strings.add(fogData);
            strings.add(fogDataDistance + getDistance());
            strings.add(fogDataShapeMode + getShapeMode());
            strings.add(colors);
            strings.add(colorFog + getFogColor());
            strings.add(colorWeather + getWeatherFogColor());
            strings.add(colorWaterFog + getWaterFogColor());
            strings.add(colorSky + getSkyColor());
            strings.add(colorWater + getWaterColor());
        } else {
            strings.add(title + ": " + ChatFormatting.DARK_GREEN + getClientInfo());
        }

        return strings;
    }

    private static String getClientInfo() {
        if (biomeData.isOverrideGameFog()) {
            return "Biome (" + getClientSide() + ")";
        } else if (dimensionData.isOverrideGameFog()) {
            return "Dimension (" + getClientSide() + ")";
        } else {
            return "Vanilla (" + getClientSide() + ")";
        }
    }

    private static String getClientSide() {
        if (data.isOnFogOverridesEnabledServer()) {
            if (data.isIntegratedServer()) {
                return "Integrated Server";
            }
            return "Dedicated Server";
        }
        return "Client";
    }

    private static String getDistance() {
        return currentFogData.start + " - " + currentFogData.end;
    }

    private static String getShapeMode() {
        return currentFogData.shape + " & " + currentFogData.mode;
    }

    private static String getFogColor() {
        if (biomeData.isOverrideGameFog() && biomeData.isOverrideFogColor()) {
            return Utilities.getFormattedColor(biomeData.getFogColor());
        } else if (dimensionData.isOverrideGameFog() && dimensionData.isOverrideFogColor()) {
            return Utilities.getFormattedColor(dimensionData.getFogColor());
        }
        return Utilities.getFormattedColor(currentColor) + " [VANILLA]";
    }

    private static String getWeatherFogColor() {
        if (biomeData.isOverrideGameFog() && biomeData.getRain().isEnabled()) {
            return Utilities.getFormattedColor(biomeData.getRain().getColor());
        } else if (dimensionData.isOverrideGameFog() && dimensionData.getRain().isEnabled()) {
            return Utilities.getFormattedColor(dimensionData.getRain().getColor());
        }
        return "Not Available [VANILLA]";
    }

    private static String getWaterFogColor() {
        if (biomeData.isOverrideGameFog() && biomeData.isOverrideWaterFogColor()) {
            return Utilities.getFormattedColor(biomeData.getWaterFogColor());
        } else if (dimensionData.isOverrideGameFog() && dimensionData.isOverrideWaterFogColor()) {
            return Utilities.getFormattedColor(dimensionData.getWaterFogColor());
        }
        return "Not Available [VANILLA]";
    }

    private static String getSkyColor() {
        if (biomeData.isOverrideGameFog() && biomeData.isOverrideSkyColor()) {
            return Utilities.getFormattedColor(biomeData.getSkyColor());
        } else if (dimensionData.isOverrideGameFog() && dimensionData.isOverrideSkyColor()) {
            return Utilities.getFormattedColor(dimensionData.getSkyColor());
        }
        return "Not Available [VANILLA]";
    }

    private static String getWaterColor() {
        if (biomeData.isOverrideGameFog() && biomeData.isOverrideWaterFog()) {
            return Utilities.getFormattedColor(biomeData.getWaterColor());
        } else if (dimensionData.isOverrideGameFog() && dimensionData.isOverrideWaterColor()) {
            return Utilities.getFormattedColor(dimensionData.getWaterColor());
        }
        return "Not Available [VANILLA]";
    }

    public static void setCurrentFogData(FogRenderer.FogData fogData, String fogType) {
        currentFogData = fogData;
        fogTypeData = fogType;
    }

    public static void setCurrentColor(int newColor) {
        currentColor = newColor;
    }

    public static FogRenderer.FogData getCurrentFogData() {
        return currentFogData;
    }
}
