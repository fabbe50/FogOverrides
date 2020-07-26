package com.fabbe50.fogoverrides;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    private ForgeConfigSpec spec;

    public ForgeConfigSpec getSpec() {
        return spec;
    }

    private General general;

    public Config() {
        general = new General();

        spec = builder.build();
    }

    public General getGeneral() {
        return general;
    }

    public class General {
        ForgeConfigSpec.BooleanValue netherFogRemove;
        ForgeConfigSpec.DoubleValue netherFogDistance;
        ForgeConfigSpec.BooleanValue lavaFogRemove;
        ForgeConfigSpec.DoubleValue lavaFogDistance;
        ForgeConfigSpec.BooleanValue waterFogRemove;
        ForgeConfigSpec.DoubleValue waterFogDistance;
        ForgeConfigSpec.BooleanValue overworldFogRemove;
        ForgeConfigSpec.DoubleValue overworldFogDistance;
        ForgeConfigSpec.BooleanValue potionAffectsVision;
        ForgeConfigSpec.BooleanValue creativeOverrides;
        ForgeConfigSpec.BooleanValue disableFireOverlay;
        ForgeConfigSpec.DoubleValue fireOverlayOffset;
        ForgeConfigSpec.BooleanValue disableWaterOverlay;

        public General() {
            builder.push("general");

            netherFogRemove = builder.comment("Disable Nether Fog?").define("Disable Nether Fog", false);
            netherFogDistance = builder.comment("Nether Fog Density").defineInRange("Nether Fog Density", 0.01, 0.001, 0.5);
            lavaFogRemove = builder.comment("Disable Lava Fog?").define("Disable Lava Fog", false);
            lavaFogDistance = builder.comment("Lava Fog Density").defineInRange("Lava Fog Density", 0.03, 0.001, 0.5);
            waterFogRemove = builder.comment("Disable Water Fog?").define("Disable Water Fog", false);
            waterFogDistance = builder.comment("Water Fog Density").defineInRange("Water Fog Density", 0.01, 0.001, 0.5);
            overworldFogRemove = builder.comment("Disable Overworld Fog?").define("Disable Overworld Fog", false);
            overworldFogDistance = builder.comment("Overworld Fog Density").defineInRange("Overworld Fog Density", 0.0015, 0.001, 0.5);
            potionAffectsVision = builder.comment("Overrides are dependent on potion effects").define("Potion Active", true);
            creativeOverrides = builder.comment("Creative Mode Always Removes Fog").define("Creative Overrides", true);
            disableFireOverlay = builder.comment("Disable the fire overlay when you're burning").define("Disable Fire Overlay", false);
            fireOverlayOffset = builder.comment("Offset the fire overlay, negative values means down and positive is up.").defineInRange("Fire Overlay Offset", -0.25, -1, 1);
            disableWaterOverlay = builder.comment("Disable the water overlay when you're in water").define("Disable Water Overlay", false);

            builder.pop();
        }

        public ForgeConfigSpec.BooleanValue getNetherFogRemove() {
            return netherFogRemove;
        }

        public ForgeConfigSpec.DoubleValue getNetherFogDistance() {
            return netherFogDistance;
        }

        public ForgeConfigSpec.BooleanValue getLavaFogRemove() {
            return lavaFogRemove;
        }

        public ForgeConfigSpec.DoubleValue getLavaFogDistance() {
            return lavaFogDistance;
        }

        public ForgeConfigSpec.BooleanValue getWaterFogRemove() {
            return waterFogRemove;
        }

        public ForgeConfigSpec.DoubleValue getWaterFogDistance() {
            return waterFogDistance;
        }

        public ForgeConfigSpec.BooleanValue getOverworldFogRemove() {
            return overworldFogRemove;
        }

        public ForgeConfigSpec.DoubleValue getOverworldFogDistance() {
            return overworldFogDistance;
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
}
