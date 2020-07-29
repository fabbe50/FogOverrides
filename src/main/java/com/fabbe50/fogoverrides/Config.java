package com.fabbe50.fogoverrides;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    private ForgeConfigSpec spec;

    public ForgeConfigSpec getSpec() {
        return spec;
    }

    private General general;
    private Overworld overworld;
    private Nether nether;
    private Lava lava;
    private Water water;

    public Config() {
        general = new General();
        overworld = new Overworld();
        nether = new Nether();
        lava = new Lava();
        water = new Water();

        spec = builder.build();
    }

    public General getGeneral() {
        return general;
    }

    public Overworld getOverworld() {
        return overworld;
    }

    public Nether getNether() {
        return nether;
    }

    public Lava getLava() {
        return lava;
    }

    public Water getWater() {
        return water;
    }

    public class General {
        ForgeConfigSpec.BooleanValue potionAffectsVision;
        ForgeConfigSpec.BooleanValue creativeOverrides;
        ForgeConfigSpec.BooleanValue disableFireOverlay;
        ForgeConfigSpec.DoubleValue fireOverlayOffset;
        ForgeConfigSpec.BooleanValue disableWaterOverlay;

        public General() {
            builder.push("general");

            potionAffectsVision = builder.comment("Overrides are dependent on potion effects").define("Potion Active", true);
            creativeOverrides = builder.comment("Creative Mode Always Removes Fog").define("Creative Overrides", true);
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

    public class Overworld {
        ForgeConfigSpec.BooleanValue overworldFogRemove;
        ForgeConfigSpec.DoubleValue overworldFogDistance;
        ForgeConfigSpec.BooleanValue enableVoidFog;
        ForgeConfigSpec.DoubleValue voidFogDensity;
        ForgeConfigSpec.IntValue yLevelActivate;
        ForgeConfigSpec.BooleanValue enableVoidParticles;

        public Overworld() {
            builder.push("overworld");

            overworldFogRemove = builder.comment("Disable Overworld Fog?").define("Disable Overworld Fog", false);
            overworldFogDistance = builder.comment("Overworld Fog Density").defineInRange("Overworld Fog Density", 0.0015, 0.001, 0.5);
            enableVoidFog = builder.comment("Readds the void fog that was removed in 1.8").define("Enable Void Fog", false);
            voidFogDensity = builder.comment("This is so you can adjust the density of the fog").defineInRange("Void Fog Density", 0.3, 0.001, 0.5);
            yLevelActivate = builder.comment("At what Y-level should the fog active on").defineInRange("Y-Level Activation", 16, 0, 255);
            enableVoidParticles = builder.comment("Readds the void particles that was removed in 1.8").define("Enable Void Particles", false);

            builder.pop();
        }

        public ForgeConfigSpec.BooleanValue getOverworldFogRemove() {
            return overworldFogRemove;
        }

        public ForgeConfigSpec.DoubleValue getOverworldFogDistance() {
            return overworldFogDistance;
        }

        public ForgeConfigSpec.BooleanValue getEnableVoidFog() {
            return enableVoidFog;
        }

        public ForgeConfigSpec.DoubleValue getVoidFogDensity() {
            return voidFogDensity;
        }

        public ForgeConfigSpec.IntValue getyLevelActivate() {
            return yLevelActivate;
        }

        public ForgeConfigSpec.BooleanValue getEnableVoidParticles() {
            return enableVoidParticles;
        }
    }

    public class Nether {
        ForgeConfigSpec.BooleanValue netherFogRemove;
        ForgeConfigSpec.DoubleValue netherFogDistance;

        public Nether() {
            builder.push("nether");

            netherFogRemove = builder.comment("Disable Nether Fog?").define("Disable Nether Fog", false);
            netherFogDistance = builder.comment("Nether Fog Density").defineInRange("Nether Fog Density", 0.01, 0.001, 0.5);

            builder.pop();
        }

        public ForgeConfigSpec.BooleanValue getNetherFogRemove() {
            return netherFogRemove;
        }

        public ForgeConfigSpec.DoubleValue getNetherFogDistance() {
            return netherFogDistance;
        }
    }

    public class Lava {
        ForgeConfigSpec.BooleanValue lavaFogRemove;
        ForgeConfigSpec.DoubleValue lavaFogDistance;

        public Lava() {
            builder.push("lava");

            lavaFogRemove = builder.comment("Disable Lava Fog?").define("Disable Lava Fog", false);
            lavaFogDistance = builder.comment("Lava Fog Density").defineInRange("Lava Fog Density", 0.03, 0.001, 0.5);

            builder.pop();
        }

        public ForgeConfigSpec.BooleanValue getLavaFogRemove() {
            return lavaFogRemove;
        }

        public ForgeConfigSpec.DoubleValue getLavaFogDistance() {
            return lavaFogDistance;
        }
    }

    public class Water {
        ForgeConfigSpec.BooleanValue waterFogRemove;
        ForgeConfigSpec.DoubleValue waterFogDistance;

        public Water() {
            builder.push("water");

            waterFogRemove = builder.comment("Disable Water Fog?").define("Disable Water Fog", false);
            waterFogDistance = builder.comment("Water Fog Density").defineInRange("Water Fog Density", 0.01, 0.001, 0.5);

            builder.pop();
        }

        public ForgeConfigSpec.BooleanValue getWaterFogRemove() {
            return waterFogRemove;
        }

        public ForgeConfigSpec.DoubleValue getWaterFogDistance() {
            return waterFogDistance;
        }
    }
}
