package com.fabbe50.fogoverrides;

import net.minecraftforge.common.ForgeConfig;
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
    private static final End END;
    private static final ForgeConfigSpec END_SPEC;
    private static final Lava LAVA;
    private static final ForgeConfigSpec LAVA_SPEC;
    private static final Water WATER;
    private static final ForgeConfigSpec WATER_SPEC;
    private static final Frostbite FROSTBITE;
    private static final ForgeConfigSpec FROSTBITE_SPEC;

    private static final Blindness BLINDNESS;
    private static final ForgeConfigSpec BLINDNESS_SPEC;

    public Config() {
    }

    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, GENERAL_SPEC, "FogOverrides/general.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, OVERWORLD_SPEC, "FogOverrides/overworld.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, NETHER_SPEC, "FogOverrides/nether.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, END_SPEC, "FogOverrides/end.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, LAVA_SPEC, "FogOverrides/lava.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, WATER_SPEC, "FogOverrides/water.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FROSTBITE_SPEC, "FogOverrides/frostbite.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BLINDNESS_SPEC, "FogOverrides/blindness.toml");
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
        final Pair<End, ForgeConfigSpec> endSpec = new ForgeConfigSpec.Builder().configure(End::new);
        END = endSpec.getLeft();
        END_SPEC = endSpec.getRight();
        final Pair<Lava, ForgeConfigSpec> lavaSpec = new ForgeConfigSpec.Builder().configure(Lava::new);
        LAVA = lavaSpec.getLeft();
        LAVA_SPEC = lavaSpec.getRight();
        final Pair<Water, ForgeConfigSpec> waterSpec = new ForgeConfigSpec.Builder().configure(Water::new);
        WATER = waterSpec.getLeft();
        WATER_SPEC = waterSpec.getRight();
        final Pair<Frostbite, ForgeConfigSpec> frostbiteSpec = new ForgeConfigSpec.Builder().configure(Frostbite::new);
        FROSTBITE = frostbiteSpec.getLeft();
        FROSTBITE_SPEC = frostbiteSpec.getRight();
        final Pair<Blindness, ForgeConfigSpec> blindnessSpec = new ForgeConfigSpec.Builder().configure(Blindness::new);
        BLINDNESS = blindnessSpec.getLeft();
        BLINDNESS_SPEC = blindnessSpec.getRight();
    }

    public static class General {
        ForgeConfigSpec.BooleanValue potionAffectsVision;
        ForgeConfigSpec.BooleanValue creativeOverrides;
        ForgeConfigSpec.BooleanValue disableFireOverlay;
        ForgeConfigSpec.DoubleValue fireOverlayOffset;
        ForgeConfigSpec.BooleanValue disableWaterOverlay;
        ForgeConfigSpec.BooleanValue disableFrostbiteOverlay;
        ForgeConfigSpec.DoubleValue frostbiteOverlayOpacity;

        General(ForgeConfigSpec.Builder builder) {
            builder.push("general");

            potionAffectsVision = builder.comment("Overrides are dependent on potion effects").define("Potion Active", true);
            creativeOverrides = builder.comment("Creative Mode Always Removes Fog").define("Creative Overrides", false);
            disableFireOverlay = builder.comment("Disable the fire overlay when you're burning").define("Disable Fire Overlay", false);
            fireOverlayOffset = builder.comment("Offset the fire overlay, negative values means down and positive is up.").defineInRange("Fire Overlay Offset", -0.25, -1, 1);
            disableWaterOverlay = builder.comment("Disable the water overlay when you're in water").define("Disable Water Overlay", false);
            disableFrostbiteOverlay = builder.comment("Disable the frostbite overlay when you're in powdered snow").define("Disable Frostbite Overlay", false);
            frostbiteOverlayOpacity = builder.comment("Set the frostbite overlay opacity").defineInRange("Frostbite Overlay Opacity", 0.5, 0, 1);

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

        public ForgeConfigSpec.BooleanValue getDisableFrostbiteOverlay() {
            return disableFrostbiteOverlay;
        }

        public ForgeConfigSpec.DoubleValue getFrostbiteOverlayOpacity() {
            return frostbiteOverlayOpacity;
        }
    }

    public static class Overworld {
        ForgeConfigSpec.BooleanValue overworldFogRemove;
        ForgeConfigSpec.IntValue overworldFogDistance;
        ForgeConfigSpec.IntValue overworldFogEndDistance;
        ForgeConfigSpec.BooleanValue enableVoidFog;
        ForgeConfigSpec.IntValue voidFogDensity;
        ForgeConfigSpec.IntValue voidFogEndDensity;
        ForgeConfigSpec.IntValue yLevelActivate;
        ForgeConfigSpec.BooleanValue enableVoidParticles;
        ForgeConfigSpec.BooleanValue voidFogAffectedBySkylight;

        Overworld(ForgeConfigSpec.Builder builder) {
            builder.push("overworld");

            overworldFogRemove = builder.comment("Disable Overworld Fog?").define("Disable Overworld Fog", false);
            overworldFogDistance = builder.comment("Overworld Fog Starting Distance").defineInRange("Overworld Fog Starting Distance", 80, 1, 500);
            overworldFogEndDistance = builder.comment("Overworld Fog End Distance").defineInRange("Overworld Fog End Distance", 100, 1, 500);
            enableVoidFog = builder.comment("Readds the void fog that was removed in 1.8 [wip]").define("Enable Void Fog", false);
            voidFogDensity = builder.comment("This is so you can adjust the starting distance of the fog [wip]").defineInRange("Void Fog Starting Distance", 7, 1, 100);
            voidFogEndDensity = builder.comment("This is so you can adjust the ending distance of the fog [wip]").defineInRange("Void Fog End Distance", 16, 1, 100);
            yLevelActivate = builder.comment("At what Y-level should the fog active on [wip]").defineInRange("Y-Level Activation", 0, -64, 319);
            enableVoidParticles = builder.comment("Readds the void particles that was removed in 1.8 [wip]").define("Enable Void Particles", false);
            voidFogAffectedBySkylight = builder.comment("If the player is in a skylight level of 8, the fog disappears [wip]").define("Void scared of sky", true);

            builder.pop();
        }

        public ForgeConfigSpec.BooleanValue getOverworldFogRemove() {
            return overworldFogRemove;
        }

        public ForgeConfigSpec.IntValue getOverworldFogDistance() {
            return overworldFogDistance;
        }

        public ForgeConfigSpec.IntValue getOverworldFogEndDistance() {
            return overworldFogEndDistance;
        }

        public ForgeConfigSpec.BooleanValue getEnableVoidFog() {
            return enableVoidFog;
        }

        public ForgeConfigSpec.IntValue getVoidFogDensity() {
            return voidFogDensity;
        }
        public ForgeConfigSpec.IntValue getVoidFogEndDensity() {
            return voidFogDensity;
        }

        public ForgeConfigSpec.IntValue getYLevelActivate() {
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
        ForgeConfigSpec.IntValue netherFogDistance;
        ForgeConfigSpec.IntValue netherFogEndDistance;

        Nether(ForgeConfigSpec.Builder builder) {
            builder.push("nether");

            netherFogRemove = builder.comment("Disable Nether Fog?").define("Disable Nether Fog", false);
            netherFogDistance = builder.comment("Nether Fog Starting Distance").defineInRange("Nether Fog Starting Distance", 32, 1, 500);
            netherFogEndDistance = builder.comment("Nether Fog End Distance").defineInRange("Nether Fog End Distance", 64, 1, 500);

            builder.pop();
        }

        public ForgeConfigSpec.BooleanValue getNetherFogRemove() {
            return netherFogRemove;
        }

        public ForgeConfigSpec.IntValue getNetherFogDistance() {
            return netherFogDistance;
        }

        public ForgeConfigSpec.IntValue getNetherFogEndDistance() {
            return netherFogEndDistance;
        }
    }
    
    public static class End {
        ForgeConfigSpec.BooleanValue endFogRemove;
        ForgeConfigSpec.IntValue endFogDistance;
        ForgeConfigSpec.IntValue endFogEndDistance;

        End(ForgeConfigSpec.Builder builder) {
            builder.push("end");

            endFogRemove = builder.comment("Disable End Fog?").define("Disable End Fog", false);
            endFogDistance = builder.comment("End Fog Starting Distance").defineInRange("End Fog Starting Distance", 32, 1, 500);
            endFogEndDistance = builder.comment("End Fog End Distance").defineInRange("End Fog End Distance", 64, 1, 500);

            builder.pop();
        }

        public ForgeConfigSpec.BooleanValue getEndFogRemove() {
            return endFogRemove;
        }

        public ForgeConfigSpec.IntValue getEndFogDistance() {
            return endFogDistance;
        }

        public ForgeConfigSpec.IntValue getEndFogEndDistance() {
            return endFogEndDistance;
        }
    }

    public static class Lava {
        ForgeConfigSpec.BooleanValue lavaFogRemove;
        ForgeConfigSpec.IntValue lavaFogDistance;
        ForgeConfigSpec.IntValue lavaFogEndDistance;

        Lava(ForgeConfigSpec.Builder builder) {
            builder.push("lava");

            lavaFogRemove = builder.comment("Disable Lava Fog?").define("Disable Lava Fog", false);
            lavaFogDistance = builder.comment("Lava Fog Starting Distance").defineInRange("Lava Fog Starting Distance", 10, 1, 500);
            lavaFogEndDistance = builder.comment("Lava Fog End Distance").defineInRange("Lava Fog End Distance", 20, 1, 500);

            builder.pop();
        }

        public ForgeConfigSpec.BooleanValue getLavaFogRemove() {
            return lavaFogRemove;
        }

        public ForgeConfigSpec.IntValue getLavaFogDistance() {
            return lavaFogDistance;
        }

        public ForgeConfigSpec.IntValue getLavaFogEndDistance() {
            return lavaFogEndDistance;
        }
    }

    public static class Water {
        ForgeConfigSpec.BooleanValue waterFogRemove;
        ForgeConfigSpec.IntValue waterFogDistance;
        ForgeConfigSpec.IntValue waterFogEndDistance;

        Water(ForgeConfigSpec.Builder builder) {
            builder.push("water");

            waterFogRemove = builder.comment("Disable Water Fog?").define("Disable Water Fog", false);
            waterFogDistance = builder.comment("Water Fog Starting Distance").defineInRange("Water Fog Starting Distance", 20, 1, 500);
            waterFogEndDistance = builder.comment("Water Fog End Distance").defineInRange("Water Fog End Distance", 50, 1, 500);

            builder.pop();
        }

        public ForgeConfigSpec.BooleanValue getWaterFogRemove() {
            return waterFogRemove;
        }

        public ForgeConfigSpec.IntValue getWaterFogDistance() {
            return waterFogDistance;
        }

        public ForgeConfigSpec.IntValue getWaterFogEndDistance() {
            return waterFogEndDistance;
        }
    }

    public static class Frostbite {
        ForgeConfigSpec.BooleanValue frostbiteFogRemove;
        ForgeConfigSpec.IntValue frostbiteFogStartingDistance;
        ForgeConfigSpec.IntValue frostbiteFogEndingDistance;

        Frostbite(ForgeConfigSpec.Builder builder) {
            builder.push("frostbite");

            frostbiteFogRemove = builder.comment("Disable Frostbite Fog?").define("Disable Frostbite Fog", false);
            frostbiteFogStartingDistance = builder.comment("Set the frostbite fog starting distance").defineInRange("Frostbite Fog Starting Distance", 1, 0, 100);
            frostbiteFogEndingDistance = builder.comment("Set the frostbite fog ending distance").defineInRange("Frostbite Fog Ending Distance", 5, 0, 100);

            builder.pop();
        }

        public ForgeConfigSpec.BooleanValue getFrostbiteFogRemove() {
            return frostbiteFogRemove;
        }

        public ForgeConfigSpec.IntValue getFrostbiteFogStartingDistance() {
            return frostbiteFogStartingDistance;
        }

        public ForgeConfigSpec.IntValue getFrostbiteFogEndingDistance() {
            return frostbiteFogEndingDistance;
        }
    }

    public static class Blindness {
        ForgeConfigSpec.BooleanValue blindnessFogRemove;
        ForgeConfigSpec.IntValue blindnessFogStartingDistance;
        ForgeConfigSpec.IntValue blindnessFogEndingDistance;

        Blindness(ForgeConfigSpec.Builder builder) {
            builder.push("blindness");

            blindnessFogRemove = builder.comment("Disable Blindness Fog?").define("Disable Blindness Fog", false);
            blindnessFogStartingDistance = builder.comment("Set the blindness fog starting distance").defineInRange("Blindness Fog Starting Distance", 1, 0, 100);
            blindnessFogEndingDistance = builder.comment("Set the blindness fog ending distance").defineInRange("Blindness Fog Ending Distance", 5, 0, 100);

            builder.pop();
        }

        public ForgeConfigSpec.BooleanValue getBlindnessFogRemove() {
            return blindnessFogRemove;
        }

        public ForgeConfigSpec.IntValue getBlindnessFogStartingDistance() {
            return blindnessFogStartingDistance;
        }

        public ForgeConfigSpec.IntValue getBlindnessFogEndingDistance() {
            return blindnessFogEndingDistance;
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
    
    public static End getEnd() {
        return END;
    }

    public static Lava getLava() {
        return LAVA;
    }

    public static Water getWater() {
        return WATER;
    }

    public static Frostbite getFrostbite() {
        return FROSTBITE;
    }
    
    public static Blindness getBlindness() {
        return BLINDNESS;
    }
}
