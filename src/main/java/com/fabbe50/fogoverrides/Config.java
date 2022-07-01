package com.fabbe50.fogoverrides;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    private static final General GENERAL;
    private static final ForgeConfigSpec GENERAL_SPEC;
    private static final Overworld OVERWORLD;
    private static final ForgeConfigSpec OVERWORLD_SPEC;
    private static final Nether NETHER;
    private static final ForgeConfigSpec NETHER_SPEC;
    private static final Lava LAVA;
    private static final ForgeConfigSpec LAVA_SPEC;
    private static final Water WATER;
    private static final ForgeConfigSpec WATER_SPEC;

    public Config() {
    }

    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, GENERAL_SPEC, "FogOverrides/general.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, OVERWORLD_SPEC, "FogOverrides/overworld.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, NETHER_SPEC, "FogOverrides/nether.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, LAVA_SPEC, "FogOverrides/lava.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, WATER_SPEC, "FogOverrides/water.toml");
    }

    static {
        final Pair<General, ForgeConfigSpec> generalSpec = new ForgeConfigSpec.Builder().configure(General::new);
        GENERAL = generalSpec.getLeft();
        GENERAL_SPEC = generalSpec.getRight();
        final Pair<Overworld, ForgeConfigSpec> overworldSpec = new ForgeConfigSpec.Builder().configure(Overworld::new);
        OVERWORLD = overworldSpec.getLeft();
        OVERWORLD_SPEC = overworldSpec.getRight();
        final Pair<Nether, ForgeConfigSpec> netherSpec = new ForgeConfigSpec.Builder().configure(Nether::new);
        NETHER = netherSpec.getLeft();
        NETHER_SPEC = netherSpec.getRight();
        final Pair<Lava, ForgeConfigSpec> lavaSpec = new ForgeConfigSpec.Builder().configure(Lava::new);
        LAVA = lavaSpec.getLeft();
        LAVA_SPEC = lavaSpec.getRight();
        final Pair<Water, ForgeConfigSpec> waterSpec = new ForgeConfigSpec.Builder().configure(Water::new);
        WATER = waterSpec.getLeft();
        WATER_SPEC = waterSpec.getRight();
    }

    public static class General {
        ForgeConfigSpec.BooleanValue potionAffectsVision;
        ForgeConfigSpec.BooleanValue creativeOverrides;
        ForgeConfigSpec.BooleanValue disableFireOverlay;
        ForgeConfigSpec.DoubleValue fireOverlayOffset;
        ForgeConfigSpec.BooleanValue disableWaterOverlay;

        General(ForgeConfigSpec.Builder builder) {
            builder.push("general");

            potionAffectsVision = builder.comment("Overrides are dependent on potion effects").define("Potion Active", true);
            creativeOverrides = builder.comment("Creative Mode Always Removes Fog").define("Creative Overrides", false);
            disableFireOverlay = builder.comment("Disable the fire overlay when you're burning").define("Disable Fire Overlay", false);
            fireOverlayOffset = builder.comment("Offset the fire overlay, negative values means down and positive is up.").defineInRange("Fire Overlay Offset", -0.25, -1, 1);
            disableWaterOverlay = builder.comment("Disable the water overlay when you're in water").define("Disable Water Overlay", false);

            builder.pop();
        }

        public ForgeConfigSpec.BooleanValue getPotionAffectsVision() {
            return potionAffectsVision;
        }

        public ForgeConfigSpec.BooleanValue getCreativeOverrides() {
            return creativeOverrides;
        }

        public ForgeConfigSpec.BooleanValue getDisableFireOverlay() {
            return disableFireOverlay;
        }

        public ForgeConfigSpec.DoubleValue getFireOverlayOffset() {
            return fireOverlayOffset;
        }

        public ForgeConfigSpec.BooleanValue getDisableWaterOverlay() {
            return disableWaterOverlay;
        }


    }

    public static class Overworld {
        ForgeConfigSpec.BooleanValue overworldFogRemove;
        ForgeConfigSpec.DoubleValue overworldFogDistance;
        ForgeConfigSpec.DoubleValue overworldFogEndDistance;
        ForgeConfigSpec.BooleanValue enableVoidFog;
        ForgeConfigSpec.DoubleValue voidFogDensity;
        ForgeConfigSpec.DoubleValue voidFogEndDensity;
        ForgeConfigSpec.IntValue yLevelActivate;
        ForgeConfigSpec.BooleanValue enableVoidParticles;
        ForgeConfigSpec.BooleanValue voidFogAffectedBySkylight;

        Overworld(ForgeConfigSpec.Builder builder) {
            builder.push("overworld");

            overworldFogRemove = builder.comment("Disable Overworld Fog?").define("Disable Overworld Fog", false);
            overworldFogDistance = builder.comment("Overworld Fog Starting Distance").defineInRange("Overworld Fog Starting Distance", 0.65, 0.1, 100);
            overworldFogEndDistance = builder.comment("Overworld Fog End Distance").defineInRange("Overworld Fog End Distance", 0.70, 0.1, 100);
            enableVoidFog = builder.comment("Readds the void fog that was removed in 1.8").define("Enable Void Fog", false);
            voidFogDensity = builder.comment("This is so you can adjust the starting distance of the fog").defineInRange("Void Fog Starting Distance", 0.02, 0.001, 10);
            voidFogEndDensity = builder.comment("This is so you can adjust the ending distance of the fog").defineInRange("Void Fog End Distance", 0.42, 0.001, 10);
            yLevelActivate = builder.comment("At what Y-level should the fog active on").defineInRange("Y-Level Activation", 0, -64, 319);
            enableVoidParticles = builder.comment("Readds the void particles that was removed in 1.8").define("Enable Void Particles", false);
            voidFogAffectedBySkylight = builder.comment("If the player is in a skylight level of 8, the fog disappears").define("Void scared of sky", true);

            builder.pop();
        }

        public ForgeConfigSpec.BooleanValue getOverworldFogRemove() {
            return overworldFogRemove;
        }

        public ForgeConfigSpec.DoubleValue getOverworldFogDistance() {
            return overworldFogDistance;
        }

        public ForgeConfigSpec.DoubleValue getOverworldFogEndDistance() {
            return overworldFogEndDistance;
        }

        public ForgeConfigSpec.BooleanValue getEnableVoidFog() {
            return enableVoidFog;
        }

        public ForgeConfigSpec.DoubleValue getVoidFogDensity() {
            return voidFogDensity;
        }
        public ForgeConfigSpec.DoubleValue getVoidFogEndDensity() {
            return voidFogDensity;
        }

        public ForgeConfigSpec.IntValue getyLevelActivate() {
            return yLevelActivate;
        }

        public ForgeConfigSpec.BooleanValue getEnableVoidParticles() {
            return enableVoidParticles;
        }

        public ForgeConfigSpec.BooleanValue getVoidFogAffectedBySkylight() {
            return voidFogAffectedBySkylight;
        }
    }

    public static class Nether {
        ForgeConfigSpec.BooleanValue netherFogRemove;
        ForgeConfigSpec.DoubleValue netherFogDistance;
        ForgeConfigSpec.DoubleValue netherFogEndDistance;

        Nether(ForgeConfigSpec.Builder builder) {
            builder.push("nether");

            netherFogRemove = builder.comment("Disable Nether Fog?").define("Disable Nether Fog", false);
            netherFogDistance = builder.comment("Nether Fog Starting Distance").defineInRange("Nether Fog Starting Distance", 1.0, 0.1, 100);
            netherFogEndDistance = builder.comment("Nether Fog End Distance").defineInRange("Nether Fog End Distance", 1.70, 0.1, 100);

            builder.pop();
        }

        public ForgeConfigSpec.BooleanValue getNetherFogRemove() {
            return netherFogRemove;
        }

        public ForgeConfigSpec.DoubleValue getNetherFogDistance() {
            return netherFogDistance;
        }

        public ForgeConfigSpec.DoubleValue getNetherFogEndDistance() {
            return netherFogEndDistance;
        }
    }

    public static class Lava {
        ForgeConfigSpec.BooleanValue lavaFogRemove;
        ForgeConfigSpec.DoubleValue lavaFogDistance;
        ForgeConfigSpec.DoubleValue lavaFogEndDistance;

        Lava(ForgeConfigSpec.Builder builder) {
            builder.push("lava");

            lavaFogRemove = builder.comment("Disable Lava Fog?").define("Disable Lava Fog", false);
            lavaFogDistance = builder.comment("Lava Fog Starting Distance").defineInRange("Lava Fog Starting Distance", 30, 0.1, 100);
            lavaFogEndDistance = builder.comment("Lava Fog End Distance").defineInRange("Lava Fog End Distance", 35, 0.1, 100);

            builder.pop();
        }

        public ForgeConfigSpec.BooleanValue getLavaFogRemove() {
            return lavaFogRemove;
        }

        public ForgeConfigSpec.DoubleValue getLavaFogDistance() {
            return lavaFogDistance;
        }

        public ForgeConfigSpec.DoubleValue getLavaFogEndDistance() {
            return lavaFogEndDistance;
        }
    }

    public static class Water {
        ForgeConfigSpec.BooleanValue waterFogRemove;
        ForgeConfigSpec.DoubleValue waterFogDistance;
        ForgeConfigSpec.DoubleValue waterFogEndDistance;

        Water(ForgeConfigSpec.Builder builder) {
            builder.push("water");

            waterFogRemove = builder.comment("Disable Water Fog?").define("Disable Water Fog", false);
            waterFogDistance = builder.comment("Water Fog Starting Distance").defineInRange("Water Fog Starting Distance", 18, 0.1, 50);
            waterFogEndDistance = builder.comment("Water Fog End Distance").defineInRange("Water Fog End Distance", 19, 0.1, 50);

            builder.pop();
        }

        public ForgeConfigSpec.BooleanValue getWaterFogRemove() {
            return waterFogRemove;
        }

        public ForgeConfigSpec.DoubleValue getWaterFogDistance() {
            return waterFogDistance;
        }

        public ForgeConfigSpec.DoubleValue getWaterFogEndDistance() {
            return waterFogEndDistance;
        }
    }

    public static General getGeneral() {
        return GENERAL;
    }

    public static Overworld getOverworld() {
        return OVERWORLD;
    }

    public static Nether getNether() {
        return NETHER;
    }

    public static Lava getLava() {
        return LAVA;
    }

    public static Water getWater() {
        return WATER;
    }
}
