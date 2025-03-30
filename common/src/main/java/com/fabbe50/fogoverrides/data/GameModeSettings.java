package com.fabbe50.fogoverrides.data;

import com.fabbe50.fogoverrides.Utilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;


public class GameModeSettings {
    private FogMode fogMode;
    private FogSetting terrainFog;
    private FogSetting waterFog;
    private FogSetting lavaFog;

    public GameModeSettings() {
        this(FogMode.MOD_FOG, Utilities.getDefaultTerrain(), Utilities.getDefaultWater(), Utilities.getDefaultLava());
    }

    public GameModeSettings(FogMode fogMode, FogSetting terrain, FogSetting water, FogSetting lava) {
        this.fogMode = fogMode;
        this.terrainFog = terrain;
        this.waterFog = water;
        this.lavaFog = lava;
    }

    public FogSetting getTerrainFog() {
        return terrainFog;
    }

    public void setTerrainFog(FogSetting terrainFog) {
        this.terrainFog = terrainFog;
    }

    public FogSetting getWaterFog() {
        return waterFog;
    }

    public void setWaterFog(FogSetting waterFog) {
        this.waterFog = waterFog;
    }

    public FogSetting getLavaFog() {
        return lavaFog;
    }

    public void setLavaFog(FogSetting lavaFog) {
        this.lavaFog = lavaFog;
    }

    public FogMode getFogMode() {
        return fogMode;
    }

    public void setFogMode(FogMode fogMode) {
        this.fogMode = fogMode;
    }

    public void writeBuffer(FriendlyByteBuf buf) {
        buf.writeUtf(getFogMode().getId());
        FogSetting terrain = getTerrainFog();
        buf.writeBoolean(terrain.isEnabled());
        buf.writeFloat(terrain.getNearDistance());
        buf.writeFloat(terrain.getFarDistance());
        FogSetting water = getWaterFog();
        buf.writeBoolean(water.isEnabled());
        buf.writeFloat(water.getNearDistance());
        buf.writeFloat(water.getFarDistance());
        FogSetting lava = getLavaFog();
        buf.writeBoolean(lava.isEnabled());
        buf.writeFloat(lava.getNearDistance());
        buf.writeFloat(lava.getFarDistance());
    }

    public enum FogMode {
        NO_FOG("no_fog", Component.translatable("text.fogoverrides.option.fog_mode.no_fog"), Component.translatable("text.fogoverrides.option.fog_mode.no_fog.tooltip")), // Disables fog completely for the game mode.
        MOD_FOG("mod_fog", Component.translatable("text.fogoverrides.option.fog_mode.mod_fog"), Component.translatable("text.fogoverrides.option.fog_mode.mod_fog.tooltip")), // DEFAULT, uses the mods fog rendering and falls back to vanilla if no mod fog is set.
        VANILLA_FOG("vanilla_fog", Component.translatable("text.fogoverrides.option.fog_mode.vanilla_fog"), Component.translatable("text.fogoverrides.option.fog_mode.vanilla_fog.tooltip")), // Vanilla fog rendering, disables the mod fog for the game mode.
        OVERRIDES("overrides", Component.translatable("text.fogoverrides.option.fog_mode.overrides"), Component.translatable("text.fogoverrides.option.fog_mode.overrides.tooltip")); // Uses the game mode specific settings first and falls back on mod fog if no setting is present.

        private final String id;
        private final Component name;
        private final Component description;
        FogMode(String id, Component name, @Nullable Component description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public Component getName() {
            return name;
        }

        public Component getDescription() {
            return description == null ? Component.empty() : description;
        }

        public static FogMode getModeFromID(String id) {
            for (FogMode mode : values()) {
                if (mode.getId().equals(id)) {
                    return mode;
                }
            }
            return MOD_FOG;
        }
    }
}
