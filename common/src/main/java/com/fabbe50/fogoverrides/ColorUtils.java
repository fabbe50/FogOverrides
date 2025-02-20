package com.fabbe50.fogoverrides;

import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.FogSetting;
import com.fabbe50.fogoverrides.data.ModFogData;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ColorUtils {
    public static Vector4f processColor(CurrentDataStorage settings, float rainLevel, Camera camera, float gameTime, ClientLevel clientLevel) {
        FogType fogType = camera.getFluidInCamera();
        if (!fogType.equals(FogType.NONE)) {
            return null;
        }
        ModFogData biomeData = settings.getBiomeFogData(ClientUtilities.getCurrentBiomeLocation());
        ModFogData dimensionData = settings.getDimensionFogData(ClientUtilities.getCurrentDimensionLocation());
        FogSetting biomeTerrain = biomeData.getTerrain();
        FogSetting biomeRain = biomeData.getRain();
        FogSetting dimensionTerrain = dimensionData.getTerrain();
        FogSetting dimensionRain = dimensionData.getRain();
        int terrainColor;
        if (biomeData.isOverrideFogColor() && biomeTerrain.getColor() != 0xFFFFFF) {
            terrainColor = biomeTerrain.getColor();
        } else if (dimensionData.isOverrideFogColor() && dimensionTerrain.getColor() != 0xFFFFFF) {
            terrainColor = dimensionTerrain.getColor();
        } else {
            float ac = Mth.clamp(Mth.cos(clientLevel.getTimeOfDay(gameTime) * 6.2831855F) * 2.0F + 0.5F, 0.0F, 1.0F);
            BiomeManager biomeManager = clientLevel.getBiomeManager();
            Vec3 cameraPosition = camera.getPosition().subtract(2.0, 2.0, 2.0).scale(0.25);
            Vec3 terrainColorVec3 = CubicSampler.gaussianSampleVec3(cameraPosition, (r, g, b) -> clientLevel.effects().getBrightnessDependentFogColor(Vec3.fromRGB24(biomeManager.getNoiseBiomeAtQuart(r, g, b).value().getFogColor()), ac));
            terrainColor = Utilities.getColorIntegerFromRGB(terrainColorVec3.x(), terrainColorVec3.y(), terrainColorVec3.z());
        }
        if (shouldProcess(biomeData)) {
            return process(terrainColor, biomeRain.getColor(), rainLevel);
        } else if (shouldProcess(dimensionData)) {
            return process(terrainColor, dimensionRain.getColor(), rainLevel);
        } else {
            currentColor = 0;
        }
        return null;
    }

    private static boolean shouldProcess(ModFogData data) {
        FogSetting rain = data.getRain();
        if (rain.isEnabled() && rain.getColor() != 0xFFFFFF) {
            return true;
        }
        return false;
    }

    private static int currentColor = 0;
    private static Vector4f process(int terrainColor, int rainColor, float rainLevel) {
        int blendColor = Utilities.getBlendedColor(terrainColor, rainColor, rainLevel);
        currentColor = blendColor;
        Vec3 blendedColor = Utilities.getVec3ColorFromInteger(blendColor);
        return new Vector4f((float) blendedColor.x(), (float) blendedColor.y(), (float) blendedColor.z(), 0);
    }

    public static int getCurrentColor() {
        return currentColor;
    }
}
