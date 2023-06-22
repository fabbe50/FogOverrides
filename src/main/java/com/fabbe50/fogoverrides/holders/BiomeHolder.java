package com.fabbe50.fogoverrides.holders;

import com.fabbe50.fogoverrides.holders.data.BiomeData;

import java.util.ArrayList;
import java.util.List;

public class BiomeHolder {
    private static List<String> biomesToOverrideConfig = new ArrayList<>();
    private static final List<BiomeData> biomeDataList = new ArrayList<>();

    public static void registerBiomeData(String name) {
        System.out.println("Registering BiomeData for: " + name);
        biomeDataList.add(new BiomeData(name));
    }

    public static void updateBiomeData() {
        for (String name : biomesToOverrideConfig) {
            boolean flag = false;
            for (BiomeData biomeData : biomeDataList) {
                if (biomeData.getResourceLocation().toString().equals(name)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                registerBiomeData(name);
            }
        }
    }

    public static List<BiomeData> getBiomeDataList() {
        return biomeDataList;
    }

    public static List<String> getBiomesToOverrideConfig() {
        return biomesToOverrideConfig;
    }

    public static void setBiomesToOverrideConfig(List<String> biomesToOverrideConfig) {
        BiomeHolder.biomesToOverrideConfig = biomesToOverrideConfig;
    }
}
