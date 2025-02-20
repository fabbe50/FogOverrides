package com.fabbe50.fogoverrides.data;

import com.fabbe50.fogoverrides.Log;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class Registry {
    private static final List<ResourceLocation> BIOMES = new ArrayList<>();
    private static final List<ResourceLocation> DIMENSIONS = new ArrayList<>();

    public static void addBiomeToList(ResourceLocation location) {
        if (!BIOMES.contains(location)) {
            BIOMES.add(location);
            Log.info("Detected and added biome: " + location.toString());
        }
    }

    public static void addDimensionToList(ResourceLocation location) {
        if (!DIMENSIONS.contains(location)) {
            DIMENSIONS.add(location);
            Log.info("Detected and added dimension: " + location.toString());
        }
    }

    public static List<ResourceLocation> getBiomes() {
        return BIOMES;
    }

    public static List<ResourceLocation> getDimensions() {
        return DIMENSIONS;
    }
}
