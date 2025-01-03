package com.fabbe50.fogoverrides;

import com.fabbe50.fogoverrides.data.*;
import com.fabbe50.fogoverrides.data.checker.Checkers;
import com.fabbe50.fogoverrides.data.checker.IChecker;
import com.fabbe50.fogoverrides.data.checker.Mode;
import com.fabbe50.fogoverrides.data.checker.Result;
import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.FogRenderer.FogData;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.client.renderer.FogRenderer.MobEffectFogFunction;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.fabbe50.fogoverrides.ModConfig.*;
import static com.fabbe50.fogoverrides.ModConfig.LAVA_FAR_DEFAULT;

public class FogUtils {
    private static int renderDistanceSetting;
    private static CalculationSetting calculationSetting;

    public static void setRenderDistance(int renderDistance) {
        FogUtils.renderDistanceSetting = renderDistance;
    }

    public static void setCalculationSetting(CalculationSetting calculationSetting) {
        FogUtils.calculationSetting = calculationSetting;
    }

    public static void processFog(Mode mode, Entity entity, MobEffectFogFunction effect, Vector4f color, float renderDistance, float partialTicks, FogType fogType, FogData fogData, CallbackInfoReturnable<FogParameters> cir) {
        CurrentDataStorage settings = CurrentDataStorage.INSTANCE;
        ModFogData biomeFogData = settings.getBiomeFogData(ClientUtilities.getCurrentBiomeLocation());
        ModFogData dimensionFogData = settings.getDimensionFogData(ClientUtilities.getCurrentDimensionLocation());
        switch (mode) {
            case SPECTATOR -> doSpectatorFog(color, renderDistance, fogType, fogData, settings, cir);
            case CREATIVE -> doCreativeFog(color, renderDistance, fogType, fogData, settings, cir);
            case LIQUID -> doLiquidFog(entity, color, renderDistance, fogType, fogData, settings, biomeFogData, dimensionFogData, cir);
            case EFFECT -> doMobEffectFog(entity, effect, color, renderDistance, fogData, partialTicks, cir);
            case WEATHER -> doWeatherFog(entity, color, renderDistance, fogData, biomeFogData, dimensionFogData, cir);
            case THICK -> doThickTerrainFog(color, renderDistance, fogData, biomeFogData, dimensionFogData, cir);
            case TERRAIN -> doNormalTerrainFog(color, renderDistance, fogData, biomeFogData, dimensionFogData, cir);
        }
    }

    // Group Setters
    public static void doNormalTerrainFog(Vector4f color, float renderDistance, FogData fogData, ModFogData biomeFogData, ModFogData dimensionFogData, CallbackInfoReturnable<FogParameters> cir) {
        setFogData(fogData, renderDistance, renderDistance, FogShape.CYLINDER);
        doTerrainFog(color, renderDistance, fogData, biomeFogData, dimensionFogData, "TERRAIN_FOG", cir);
    }

    public static void doThickTerrainFog(Vector4f color, float renderDistance, FogData fogData, ModFogData biomeFogData, ModFogData dimensionFogData, CallbackInfoReturnable<FogParameters> cir) {
        setFogData(fogData, renderDistance * SPECIAL_FOG_NEAR_MULTIPLIER_DEFAULT, Math.min(renderDistance, SPECIAL_FOG_FAR_MAX_DISTANCE_DEFAULT) * SPECIAL_FOG_FAR_MULTIPLIER_DEFAULT, FogShape.SPHERE);
        doTerrainFog(color, renderDistance, fogData, biomeFogData, dimensionFogData, "SPECIAL_FOG", cir);
    }

    public static void doTerrainFog(Vector4f color, float renderDistance, FogData fogData, ModFogData biomeFogData, ModFogData dimensionFogData, String fogTypeData, CallbackInfoReturnable<FogParameters> cir) {
        if (biomeFogData.isOverrideGameFog()) {
            setFogData(renderDistance, fogData, biomeFogData, FogShape.CYLINDER);
        } else if (dimensionFogData != null && dimensionFogData.isOverrideGameFog()) {
            setFogData(renderDistance, fogData, dimensionFogData, FogShape.CYLINDER);
        }
        F3Information.setCurrentFogData(fogData, fogTypeData);
        cir.setReturnValue(new FogParameters(fogData.start, fogData.end, fogData.shape, color.x(), color.y(), color.z(), color.w()));
    }

    public static void doWeatherFog(Entity entity, Vector4f color, float renderDistance, FogData fogData, ModFogData biomeFogData, ModFogData dimensionFogData, CallbackInfoReturnable<FogParameters> cir) {
        String fogLocation = "BIOME";
        FogSetting terrain = biomeFogData.getTerrain();
        FogSetting rain = biomeFogData.getRain();
        if (terrain.isEnabled() && rain.isEnabled()) {
            doRainFog(entity, color, renderDistance, fogData, terrain, rain, fogLocation, cir);
        } else if (terrain.isEnabled()) {
            rain = dimensionFogData.getRain();
            if (rain.isEnabled()) {
                fogLocation = "DIMENSION";
                doRainFog(entity, color, renderDistance, fogData, terrain, rain, fogLocation, cir);
            }
        } else if (rain.isEnabled()) {
            terrain = dimensionFogData.getTerrain();
            if (terrain.isEnabled()) {
                doRainFog(entity, color, renderDistance, fogData, terrain, rain, fogLocation, cir);
            }
        } else {
            terrain = dimensionFogData.getTerrain();
            rain = dimensionFogData.getRain();
            if (terrain.isEnabled() && rain.isEnabled()) {
                fogLocation = "DIMENSION";
                doRainFog(entity, color, renderDistance, fogData, terrain, rain, fogLocation, cir);
            }
        }
    }

    public static void doRainFog(Entity entity, Vector4f color, float renderDistance, FogData fogData, FogSetting terrain, FogSetting rain, String fogLocation, CallbackInfoReturnable<FogParameters> cir) {
        float gameTimePartialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        float rainLevel = entity.level().getRainLevel(gameTimePartialTick);
        float near = Utilities.getBetweenDistanceByRatio(terrain.getNearDistance(), rain.getNearDistance(), rainLevel);
        float far = Utilities.getBetweenDistanceByRatio(terrain.getFarDistance(), rain.getFarDistance(), rainLevel);
        if (setFogData(renderDistance, fogData, near, far, FogShape.CYLINDER)) {
            finalizeFog(color, fogData, "WEATHER - " + fogLocation, cir);
        }
    }

    public static void doLiquidFog(Entity entity, Vector4f color, float renderDistance, FogType fogType, FogData fogData, CurrentDataStorage settings, ModFogData biomeFogData, ModFogData dimensionFogData, CallbackInfoReturnable<FogParameters> cir) {
        switch (fogType) {
            case WATER -> doWaterFog(entity, settings, renderDistance, color, fogData, biomeFogData, dimensionFogData, cir);
            case LAVA -> doLavaFog(entity, settings, renderDistance, color, fogData, biomeFogData, dimensionFogData, cir);
            case POWDER_SNOW -> doPowderSnowFog(color, fogData, cir);
            /*case null, default -> {
                if (entity.isInLiquid()) {
                    Level level = entity.level();
                    BlockState blockState = level.getBlockState(entity.blockPosition());
                    if (blockState.getBlock() instanceof LiquidBlock liquidBlock) {
                        Item item = liquidBlock.asItem();
                        ResourceLocation location = item.arch$registryName();
                        if (location != null) {
                            // TODO: Get fog settings for location.
                        }
                    }
                }
            }*/
        }
    }

    public static void doWaterFog(Entity entity, CurrentDataStorage settings, float renderDistance, Vector4f color, FogData fogData, ModFogData biomeFogData, ModFogData dimensionFogData, CallbackInfoReturnable<FogParameters> cir) {
        setFogData(fogData, ModConfig.UNDERWATER_NEAR_DEFAULT, ModConfig.UNDERWATER_FAR_DEFAULT, FogShape.SPHERE);
        float maxDistance = renderDistance;
        if (!settings.isWaterFogEnabled()) {
            disableFog(fogData);
        } else if ((biomeFogData.isWaterPotionEffect() || (dimensionFogData != null && dimensionFogData.isWaterPotionEffect())) && entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(MobEffects.WATER_BREATHING)) {
            if (biomeFogData.isWaterPotionEffect() && biomeFogData.hasValidWaterPotionFogDistance()) {
                setFogData(renderDistance, fogData, biomeFogData.getWaterPotionNearDistance(), biomeFogData.getWaterPotionFarDistance(), FogShape.CYLINDER);
            } else if (dimensionFogData != null && dimensionFogData.isWaterPotionEffect() && dimensionFogData.hasValidWaterPotionFogDistance()) {
                setFogData(renderDistance, fogData, dimensionFogData.getWaterPotionNearDistance(), dimensionFogData.getWaterPotionFarDistance(), FogShape.CYLINDER);
            }
        } else {
            if (biomeFogData.isOverrideWaterFog() && biomeFogData.hasValidWaterFogDistance()) {
                maxDistance = biomeFogData.getWaterFarDistance();
                setFogData(renderDistance, fogData, biomeFogData.getWaterNearDistance(), maxDistance, FogShape.SPHERE);
            } else if (dimensionFogData != null && dimensionFogData.isOverrideWaterFog() && dimensionFogData.hasValidWaterFogDistance()) {
                maxDistance = dimensionFogData.getWaterFarDistance();
                setFogData(renderDistance, fogData, dimensionFogData.getWaterNearDistance(), maxDistance, FogShape.SPHERE);
            }
            doUnderwaterFogCalculation(fogData, entity, maxDistance);
        }
        F3Information.setCurrentFogData(fogData, "UNDER_WATER");
        cir.setReturnValue(new FogParameters(fogData.start, fogData.end, fogData.shape, color.x(), color.y(), color.z(), color.w()));
    }

    public static void doLavaFog(Entity entity, CurrentDataStorage settings, float renderDistance, Vector4f color, FogData fogData, ModFogData biomeFogData, ModFogData dimensionFogData, CallbackInfoReturnable<FogParameters> cir) {
        if (!settings.isLavaFogEnabled()) {
            disableFog(fogData);
        } else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(MobEffects.FIRE_RESISTANCE)) {
            setFogData(fogData, LAVA_NEAR_POTION_DEFAULT, LAVA_FAR_POTION_DEFAULT, FogShape.SPHERE);
            if (biomeFogData.isLavaPotionEffect() && biomeFogData.hasValidLavaPotionFogDistance()) {
                setFogData(renderDistance, fogData, biomeFogData.getLavaPotionNearDistance(), biomeFogData.getLavaPotionFarDistance(), FogShape.SPHERE);
            } else if (dimensionFogData != null && dimensionFogData.isLavaPotionEffect() && dimensionFogData.hasValidLavaPotionFogDistance()) {
                setFogData(renderDistance, fogData, dimensionFogData.getLavaPotionNearDistance(), dimensionFogData.getLavaPotionFarDistance(), FogShape.SPHERE);
            }
        } else {
            setFogData(fogData, LAVA_NEAR_DEFAULT, LAVA_FAR_DEFAULT, FogShape.SPHERE);
            if (biomeFogData.isOverrideLavaFog() && biomeFogData.hasValidLavaFogDistance()) {
                setFogData(renderDistance, fogData, biomeFogData.getLavaNearDistance(), biomeFogData.getLavaFarDistance(), FogShape.SPHERE);
            } else if (dimensionFogData != null && dimensionFogData.isOverrideLavaFog() && dimensionFogData.hasValidLavaFogDistance()) {
                setFogData(renderDistance, fogData, dimensionFogData.getLavaNearDistance(), dimensionFogData.getLavaFarDistance(), FogShape.SPHERE);
            }
        }
        F3Information.setCurrentFogData(fogData, "IN_LAVA");
        cir.setReturnValue(new FogParameters(fogData.start, fogData.end, fogData.shape, color.x(), color.y(), color.z(), color.w()));
    }

    public static void doPowderSnowFog(Vector4f color, FogData fogData, CallbackInfoReturnable<FogParameters> cir) {
        // TODO: Powder Snow Options
        if (setFogData(fogData, POWDER_SNOW_NEAR_DEFAULT, POWDER_SNOW_FAR_DEFAULT)) {
            finalizeFog(color, fogData, "IN_POWDERED_SNOW", cir);
        }
    }

    public static void doMobEffectFog(Entity entity, MobEffectFogFunction mobEffectFogFunction, Vector4f color, float renderDistance, FogData fogData, float smoothingVar, CallbackInfoReturnable<FogParameters> cir) {
        // TODO: Mob effect overrides
        LivingEntity livingEntity = (LivingEntity) entity;
        MobEffectInstance mobEffectInstance = livingEntity.getEffect(mobEffectFogFunction.getMobEffect());
        if (mobEffectInstance != null) {
            mobEffectFogFunction.setupFog(fogData, livingEntity, mobEffectInstance, renderDistance, smoothingVar);
        }
        F3Information.setCurrentFogData(fogData, "MOB_EFFECT");
        cir.setReturnValue(new FogParameters(fogData.start, fogData.end, fogData.shape, color.x, color.y, color.z, color.w));
    }

    public static void doSpectatorFog(Vector4f color, float renderDistance, FogType fogType, FogData fogData, CurrentDataStorage settings, CallbackInfoReturnable<FogParameters> cir) {
        GameModeSettings gameModeSettings = settings.getSpectatorSettings();
        if (gameModeSettings.getFogMode() == GameModeSettings.FogMode.NO_FOG) {
            if (disableFog(fogData)) {
                finalizeFog(color, fogData, "SPECTATOR_MODE", cir);
            }
        } else if (gameModeSettings.getFogMode() == GameModeSettings.FogMode.OVERRIDES) {
            switch (fogType) {
                case WATER -> {
                    FogSetting fog = gameModeSettings.getWaterFog();
                    if (fog.isEnabled()) {
                        float near = fog.getNearDistance();
                        float far = fog.getFarDistance();
                        if (setFogData(renderDistance, fogData, near, far, FogShape.CYLINDER)) {
                            finalizeFog(color, fogData, "SPECTATOR_MODE", cir);
                        }
                    } else {
                        if (disableFog(fogData)) {
                            finalizeFog(color, fogData, "SPECTATOR_MODE", cir);
                        }
                    }
                }
                case LAVA -> {
                    FogSetting fog = gameModeSettings.getLavaFog();
                    if (fog.isEnabled()) {
                        float near = fog.getNearDistance();
                        float far = fog.getFarDistance();
                        if (setFogData(renderDistance, fogData, near, far, FogShape.CYLINDER)) {
                            finalizeFog(color, fogData, "SPECTATOR_MODE", cir);
                        } else {
                            near = LAVA_NEAR_SPECTATOR_DEFAULT;
                            far = renderDistance * LAVA_FAR_SPECTATOR_MULTIPLIER_DEFAULT;
                            if (setFogData(renderDistance, fogData, near, far, FogShape.CYLINDER)) {
                                finalizeFog(color, fogData, "SPECTATOR_MODE", cir);
                            }
                        }
                    } else {
                        if (disableFog(fogData)) {
                            finalizeFog(color, fogData, "SPECTATOR_MODE", cir);
                        }
                    }
                }
                case POWDER_SNOW -> {
                    float near = POWDER_SNOW_NEAR_SPECTATOR_DEFAULT;
                    float far = renderDistance * POWDER_SNOW_FAR_SPECTATOR_MULTIPLIER_DEFAULT;
                    if (setFogData(renderDistance, fogData, near, far, FogShape.CYLINDER)) {
                        finalizeFog(color, fogData, "SPECTATOR_MODE", cir);
                    }
                }
                case NONE -> {
                    FogSetting fog = gameModeSettings.getTerrainFog();
                    if (fog.isEnabled()) {
                        float near = fog.getNearDistance();
                        float far = fog.getFarDistance();
                        if (setFogData(renderDistance, fogData, near, far, FogShape.CYLINDER)) {
                            finalizeFog(color, fogData, "SPECTATOR_MODE", cir);
                        }
                    } else {
                        if (disableFog(fogData)) {
                            finalizeFog(color, fogData, "SPECTATOR_MODE", cir);
                        }
                    }
                }
            }
        }
    }

    public static void doCreativeFog(Vector4f color, float renderDistance, FogType fogType, FogData fogData, CurrentDataStorage settings, CallbackInfoReturnable<FogParameters> cir) {
        GameModeSettings gameModeSettings = settings.getCreativeSettings();
        if (gameModeSettings.getFogMode() == GameModeSettings.FogMode.NO_FOG) {
            if (disableFog(fogData)) {
                finalizeFog(color, fogData, "SPECTATOR_MODE", cir);
            }
        } else if (gameModeSettings.getFogMode() == GameModeSettings.FogMode.OVERRIDES) {
            switch (fogType) {
                case WATER -> {
                    FogSetting fog = gameModeSettings.getWaterFog();
                    if (fog.isEnabled()) {
                        float near = fog.getNearDistance();
                        float far = fog.getFarDistance();
                        if (setFogData(renderDistance, fogData, near, far, FogShape.CYLINDER)) {
                            finalizeFog(color, fogData, "CREATIVE_MODE", cir);
                        }
                    } else {
                        if (disableFog(fogData)) {
                            finalizeFog(color, fogData, "CREATIVE_MODE", cir);
                        }
                    }
                }
                case LAVA -> {
                    FogSetting fog = gameModeSettings.getLavaFog();
                    if (fog.isEnabled()) {
                        float near = fog.getNearDistance();
                        float far = fog.getFarDistance();
                        if (setFogData(renderDistance, fogData, near, far, FogShape.CYLINDER)) {
                            finalizeFog(color, fogData, "CREATIVE_MODE", cir);
                        }
                    } else {
                        if (disableFog(fogData)) {
                            finalizeFog(color, fogData, "CREATIVE_MODE", cir);
                        }
                    }
                }
                case NONE -> {
                    FogSetting fog = gameModeSettings.getTerrainFog();
                    if (fog.isEnabled()) {
                        float near = fog.getNearDistance();
                        float far = fog.getFarDistance();
                        if (setFogData(renderDistance, fogData, near, far, FogShape.CYLINDER)) {
                            finalizeFog(color, fogData, "CREATIVE_MODE", cir);
                        }
                    } else {
                        if (disableFog(fogData)) {
                            finalizeFog(color, fogData, "CREATIVE_MODE", cir);
                        }
                    }
                }
            }
        }
    }


    // Setters
    public static boolean setFogData(float renderDistance, FogData fogData, ModFogData modFogData) {
        return setFogData(renderDistance, fogData, modFogData, FogShape.SPHERE);
    }

    public static boolean setFogData(float renderDistance, FogData fogData, ModFogData modFogData, FogShape fogShape) {
        if (modFogData.isFogEnabled()) {
            if (modFogData.hasValidFogDistance()) {
                return setFogData(renderDistance, fogData, modFogData.getNearDistance(), modFogData.getFarDistance(), fogShape);
            }
        } else {
            return setFogData(renderDistance, fogData, Float.MAX_VALUE, Float.MAX_VALUE, fogShape);
        }
        return false;
    }

    public static boolean setFogData(float renderDistance, FogData fogData, FogSetting fogSetting, FogShape fogShape) {
        if (fogSetting.isEnabled()) {
            float near = fogSetting.getNearDistance();
            float far = fogSetting.getFarDistance();
            return setFogData(renderDistance, fogData, near, far, fogShape);
        }
        return false;
    }

    public static boolean setFogData(float renderDistance, FogData fogData, float near, float far, FogShape fogShape) {
        if (validate(near, far)) {
            if (calculationSetting == CalculationSetting.PERCENT_BLOCKS) {
                return setFogData(fogData, renderDistance * (near / (renderDistanceSetting * CHUNK_SIZE)), renderDistance * (far / (renderDistanceSetting * CHUNK_SIZE)), fogShape);
            } else if (calculationSetting == CalculationSetting.PERCENT) {
                return setFogData(fogData, renderDistance * (near / PERCENTAGE_DIVIDER), renderDistance * (far / PERCENTAGE_DIVIDER), fogShape);
            } else {
                return setFogData(fogData, near, far, fogShape);
            }
        }
        return false;
    }

    public static boolean setFogData(FogData fogData, float near, float far) {
        return setFogData(fogData, near, far, FogShape.SPHERE);
    }

    public static boolean setFogData(FogData fogData, float near, float far, FogShape fogShape) {
        if (validate(near, far)) {
            setFogDistance(fogData, near, far);
            setFogShape(fogData, fogShape);
            return true;
        }
        return false;
    }

    public static boolean disableFog(FogData fogData) {
        return setFogData(fogData, Float.MAX_VALUE, Float.MAX_VALUE, FogShape.CYLINDER);
    }

    public static void setFogDistance(FogData fogData, float near, float far) {
        fogData.start = near;
        fogData.end = far;
    }

    public static void setFogShape(FogData fogData, FogShape fogShape) {
        fogData.shape = fogShape;
    }

    public static void doUnderwaterFogCalculation(FogData fogData, Entity entity, float maxDistance) {
        if (entity instanceof LocalPlayer localPlayer) {
            fogData.end *= Math.max(0.25F, localPlayer.getWaterVision());
            Holder<Biome> holder = localPlayer.level().getBiome(localPlayer.blockPosition());
            if (holder.is(BiomeTags.HAS_CLOSER_WATER_FOG)) {
                fogData.end *= 0.85F;
            }
        }
        if (fogData.end > maxDistance) {
            fogData.end = maxDistance;
            fogData.shape = FogShape.CYLINDER;
        }
    }

    private static void finalizeFog(Vector4f color, FogData fogData, String fogType, CallbackInfoReturnable<FogParameters> cir) {
        FogData current = F3Information.getCurrentFogData();
        if (current != null && INSTANCE.transitionFog) {
            FogData mod = new FogData(fogData.mode);
            mod.start = Utilities.getBetweenDistanceByRatio(current.start, fogData.start, 0.01f);
            if (!(fogData.start - mod.start < 0.1f && fogData.start - mod.start > -0.1f)) {
                fogData.start = mod.start;
            }
            mod.end = Utilities.getBetweenDistanceByRatio(current.end, fogData.end, 0.01f);
            if (!(fogData.end - mod.end < 0.1f && fogData.end - mod.end > -0.1f)) {
                fogData.end = mod.end;
            }
        }
        F3Information.setCurrentFogData(fogData, fogType);
        cir.setReturnValue(new FogParameters(fogData.start, fogData.end, fogData.shape, color.x, color.y, color.z, color.w));
    }

    // Checkers
    public static Mode getMode(CurrentDataStorage settings, Entity entity, MobEffectFogFunction effect, FogMode fogMode, FogType fogType, boolean thickFog) {
        for (IChecker checker : Checkers.getCheckers()) {
            Result result = checker.getResult(settings, entity, fogMode, fogType);
            if (result == Result.CUSTOM_CHECKER) {
                Result customResult = checkCustomResults(checker, effect, thickFog);
                if (customResult == Result.DO_RENDER) {
                    return checker.getMode();
                }
            }
            Mode mode = getModeFromResult(checker, result);
            if (mode != Mode.UNSET) {
                return mode;
            }
        }

        // Return VANILLA if no other result got selected to tell the renderer that it should skip modded rendering.
        return Mode.VANILLA;
    }

    private static Mode getModeFromResult(IChecker checker, Result result) {
        if (result == Result.DO_RENDER) {
            return checker.getMode();
        } else if (result == Result.SKIP_STACK) {
            return Mode.VANILLA;
        }
        return Mode.UNSET;
    }


    // Custom Checkers
    private static Result checkCustomResults(IChecker checker, MobEffectFogFunction effect, boolean thickFog) {
        Result result = checkCustomResult(checker, effect);
        if (result != Result.ALLOW_NEXT) {
            return result;
        }
        result = checkCustomResult(checker, thickFog);
        if (result != Result.ALLOW_NEXT) {
            return result;
        }
        return Result.ALLOW_NEXT;
    }

    private static Result checkCustomResult(IChecker checker, MobEffectFogFunction effect) {
        if (checker instanceof Checkers.EffectChecker effectChecker) {
            return effectChecker.getResult(effect);
        }
        return Result.ALLOW_NEXT;
    }

    private static Result checkCustomResult(IChecker checker, boolean thickFog) {
        if (checker instanceof Checkers.ThickFogChecker thickFogChecker) {
            return thickFogChecker.getResult(thickFog);
        }
        return Result.ALLOW_NEXT;
    }


    // Utility
    public static boolean validate(float near, float far) {
        return (near != -1 && far != -1 && near < far) || (near == Float.MAX_VALUE && far == Float.MAX_VALUE);
    }
}
