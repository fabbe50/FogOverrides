package com.fabbe50.fogoverrides;

import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.FogParameters;
import com.fabbe50.fogoverrides.data.ModFogData;
import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.io.IOException;
import java.util.Optional;

public class ClientUtilities {
    public static final FogParameters NO_FOG = new FogParameters(Float.MAX_VALUE, 0.0F, FogShape.CYLINDER, -1, -1, -1, -1);

    public static int getWaterColorAtLocation(BlockPos pos) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level != null) {
            Optional<ResourceKey<Biome>> biomeKey = level.getBiome(pos).unwrapKey();
            if (biomeKey.isPresent()) {
                ResourceLocation location = biomeKey.get().location();
                ModFogData data = CurrentDataStorage.INSTANCE.getBiomeFogData(location);
                if (data != null && data.getWaterColor() != -1) {
                    if (data.isOverrideWaterColor()) {
                        return data.getWaterColor();
                    }
                }
            }
            Biome biome = level.getBiome(pos).value();
            return biome.getWaterColor();
        }
        return -1;
    }

    public static Biome getCurrentBiome() {
        Minecraft minecraft = Minecraft.getInstance();
        Entity cameraEntity = minecraft.getCameraEntity();
        if (cameraEntity != null) {
            try (Level level = cameraEntity.level()) {
                BlockPos pos = cameraEntity.getOnPos();
                return level.getBiome(pos).value();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static ResourceLocation getCurrentDimensionLocation() {
        Minecraft minecraft = Minecraft.getInstance();
        Entity cameraEntity = minecraft.getCameraEntity();
        if (cameraEntity != null) {
            try (Level level = cameraEntity.level()) {
                return level.dimension().location();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static ResourceLocation getCurrentBiomeLocation() {
        Minecraft minecraft = Minecraft.getInstance();
        Entity cameraEntity = minecraft.getCameraEntity();
        if (cameraEntity != null) {
            try (Level level = cameraEntity.level()) {
                BlockPos pos = cameraEntity.getOnPos();
                Optional<ResourceKey<Biome>> biomeKey = level.getBiome(pos).unwrapKey();
                if (biomeKey.isPresent()) {
                    return biomeKey.get().location();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static int getCurrentSkyColor() {
        Biome biome = getCurrentBiome();
        if (biome != null) {
            ModFogData data = CurrentDataStorage.INSTANCE.getBiomeFogData(getCurrentBiomeLocation());
            if (data != null && data.isOverrideSkyColor() && data.getSkyColor() != -1) {
                return data.getSkyColor();
            } else {
                ModFogData dimensionData = CurrentDataStorage.INSTANCE.getDimensionFogData(getCurrentDimensionLocation());
                if (dimensionData != null && dimensionData.getSkyColor() != -1 && dimensionData.isOverrideSkyColor()) {
                    return dimensionData.getSkyColor();
                }
            }
//            return biome.getSpecialEffects().getSkyColor();
        }
        return -1;
    }

    public static int getCurrentFogColor() {
        Biome biome = getCurrentBiome();
        if (biome != null) {
            ModFogData data = CurrentDataStorage.INSTANCE.getBiomeFogData(getCurrentBiomeLocation());
            if (data != null && data.isOverrideFogColor() && data.getFogColor() != -1) {
                return data.getFogColor();
            } else {
                ModFogData dimensionData = CurrentDataStorage.INSTANCE.getDimensionFogData(getCurrentDimensionLocation());
                if (dimensionData != null && dimensionData.getFogColor() != -1 && dimensionData.isOverrideFogColor()) {
                    return dimensionData.getFogColor();
                }
            }
//            return biome.getSpecialEffects().getFogColor();
        }
        return -1;
    }

    public static int getCurrentWaterColor() {
        ModFogData data = CurrentDataStorage.INSTANCE.getBiomeFogData(getCurrentBiomeLocation());
        //TODO: Set water color per block location instead.
        if (data != null && data.getWaterColor() != -1) {
            if (data.isOverrideWaterColor()) {
                return data.getWaterColor();
            }
        }
        return -1;
    }

    public static int getCurrentWaterFogColor() {
        Biome biome = getCurrentBiome();
        if (biome != null) {
            ModFogData data = CurrentDataStorage.INSTANCE.getBiomeFogData(getCurrentBiomeLocation());
            if (data != null && data.isOverrideWaterFogColor() && data.getWaterFogColor() != -1) {
                return data.getWaterFogColor();
            } else {
                ModFogData dimensionData = CurrentDataStorage.INSTANCE.getDimensionFogData(getCurrentDimensionLocation());
                if (dimensionData != null && dimensionData.getWaterFogColor() != -1 && dimensionData.isOverrideWaterFog()) {
                    return dimensionData.getWaterFogColor();
                }
            }
//            return biome.getSpecialEffects().getWaterFogColor();
        }
        return -1;
    }

    public static boolean isIntegratedServer() {
        return Minecraft.getInstance().isLocalServer();
    }

    public static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static ClientLevel getClientLevel() {
        return Minecraft.getInstance().level;
    }
}
