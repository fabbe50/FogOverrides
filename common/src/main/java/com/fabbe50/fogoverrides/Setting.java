package com.fabbe50.fogoverrides;

import com.fabbe50.fogoverrides.data.FogSetting;
import com.fabbe50.fogoverrides.data.GameModeSettings;

import java.util.Properties;

public class Setting {
    public static boolean readBoolean(Properties properties, String key, boolean defaultValue) {
        return properties.computeIfAbsent(key, o -> String.valueOf(defaultValue)).equals("true");
    }

    public static int readInt(Properties properties, String key, int defaultValue) {
        return Integer.parseInt((String) properties.computeIfAbsent(key, o -> String.valueOf(defaultValue)));
    }

    public static float readFloat(Properties properties, String key, float defaultValue) {
        return Float.parseFloat((String) properties.computeIfAbsent(key, o -> String.valueOf(defaultValue)));
    }

    public static FogSetting readFogSettingWithColor(Properties properties, String key, FogSetting defaultValue) {
        int color = readInt(properties, key + "FogColor", defaultValue.getColor());
        FogSetting setting = readSimpleFogSetting(properties, key, defaultValue);
        setting.setColor(color);
        return setting;
    }

    public static FogSetting readSimpleFogSetting(Properties properties, String key, FogSetting defaultValue) {
        boolean isEnabled = readBoolean(properties, key + "IsEnabled", defaultValue.isEnabled());
        float nearDistance = readFloat(properties, key + "NearDistance", defaultValue.getNearDistance());
        float farDistance = readFloat(properties, key + "FarDistance", defaultValue.getFarDistance());
        return new FogSetting(isEnabled, nearDistance, farDistance);
    }

    public static GameModeSettings.FogMode readFogMode(Properties properties, String key, GameModeSettings.FogMode defaultValue) {
        return GameModeSettings.FogMode.getModeFromID((String) properties.computeIfAbsent(key, o -> defaultValue.getId()));
    }
}
