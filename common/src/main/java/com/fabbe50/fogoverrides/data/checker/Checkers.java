package com.fabbe50.fogoverrides.data.checker;

import com.fabbe50.fogoverrides.ClientUtilities;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.FogUtils;
import com.fabbe50.fogoverrides.data.FogSetting;
import com.fabbe50.fogoverrides.data.GameModeSettings;
import com.fabbe50.fogoverrides.data.ModFogData;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.FogRenderer.MobEffectFogFunction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;

import java.util.ArrayList;
import java.util.List;

public class Checkers {
    private static List<IChecker> checkers = new ArrayList<>();

    public static void init() {
        checkers = new ArrayList<>();
        checkers.add(new SpectatorChecker());
        checkers.add(new CreativeChecker());
        checkers.add(new LiquidChecker());
        checkers.add(new EffectChecker());
        checkers.add(new WeatherChecker());
        checkers.add(new ThickFogChecker());
        checkers.add(new SkyChecker());
        checkers.add(new TerrainChecker());
    }

    public static List<IChecker> getCheckers() {
        return checkers;
    }

    public static class GameModeChecker {
        public static Result getResult(GameModeSettings settings, FogRenderer.FogMode fogMode, FogType fogType) {
            GameModeSettings.FogMode gameModeFogMode = settings.getFogMode();
            if (gameModeFogMode == GameModeSettings.FogMode.NO_FOG) {
                return Result.DISABLE_FOG;
            } else if (gameModeFogMode == GameModeSettings.FogMode.OVERRIDES) {
                if (fogType == FogType.WATER) {
                    FogSetting waterFog = settings.getWaterFog();
                    if (FogUtils.validate(waterFog.getNearDistance(), waterFog.getFarDistance())) {
                        return Result.DO_RENDER;
                    }
                }
                if (fogType == FogType.LAVA) {
                    FogSetting lavaFog = settings.getLavaFog();
                    if (FogUtils.validate(lavaFog.getNearDistance(), lavaFog.getFarDistance())) {
                        return Result.DO_RENDER;
                    }
                }
                if (fogMode == FogRenderer.FogMode.FOG_TERRAIN) {
                    FogSetting terrainFog = settings.getTerrainFog();
                    if (FogUtils.validate(terrainFog.getNearDistance(), terrainFog.getFarDistance())) {
                        return Result.DO_RENDER;
                    }
                }
            } else if (gameModeFogMode == GameModeSettings.FogMode.VANILLA_FOG) {
                return Result.SKIP_STACK;
            }
            return Result.ALLOW_NEXT;
        }
    }

    public static class SpectatorChecker implements IChecker {
        @Override
        public Mode getMode() {
            return Mode.SPECTATOR;
        }

        @Override
        public Result getResult(CurrentDataStorage settings, Entity entity, FogRenderer.FogMode fogMode, FogType fogType) {
            if (entity.isSpectator()) {
                return GameModeChecker.getResult(settings.getSpectatorSettings(), fogMode, fogType);
            }
            return Result.ALLOW_NEXT;
        }
    }

    public static class CreativeChecker implements IChecker {
        @Override
        public Mode getMode() {
            return Mode.CREATIVE;
        }

        @Override
        public Result getResult(CurrentDataStorage settings, Entity entity, FogRenderer.FogMode fogMode, FogType fogType) {
            if (entity instanceof Player player && player.isCreative()) {
                return GameModeChecker.getResult(settings.getCreativeSettings(), fogMode, fogType);
            }
            return Result.ALLOW_NEXT;
        }
    }

    public static class LiquidChecker implements IChecker {
        @Override
        public Mode getMode() {
            return Mode.LIQUID;
        }

        @Override
        public Result getResult(CurrentDataStorage settings, Entity entity, FogRenderer.FogMode fogMode, FogType fogType) {
            if (entity.isEyeInFluid(FluidTags.WATER) || entity.isEyeInFluid(FluidTags.LAVA)) {
                ModFogData biomeData = settings.getBiomeFogData(ClientUtilities.getCurrentBiomeLocation());
                ModFogData dimensionData = settings.getDimensionFogData(ClientUtilities.getCurrentDimensionLocation());
                switch (fogType) {
                    case WATER -> {
                        if (biomeData.isOverrideWaterFog()) {
                            if (biomeData.hasValidWaterFogDistance()) {
                                return Result.DO_RENDER;
                            }
                        }
                        if (dimensionData.isOverrideWaterFog()) {
                            if (dimensionData.hasValidWaterFogDistance()) {
                                return Result.DO_RENDER;
                            }
                        }
                    }
                    case LAVA -> {
                        if (biomeData.isOverrideLavaFog()) {
                            if (biomeData.hasValidLavaFogDistance()) {
                                return Result.DO_RENDER;
                            }
                        }
                        if (dimensionData.isOverrideLavaFog()) {
                            if (dimensionData.hasValidLavaFogDistance()) {
                                return Result.DO_RENDER;
                            }
                        }
                    }
                    case POWDER_SNOW -> {
                        return Result.DO_RENDER;
                    }
                    case NONE -> {
                        return Result.ALLOW_NEXT;
                    }
                    default -> {
                        return Result.SKIP_STACK;
                    }
                }
            }
            return Result.ALLOW_NEXT;
        }
    }

    public static class EffectChecker extends AbstractChecker<MobEffectFogFunction> {
        @Override
        public Mode getMode() {
            return Mode.EFFECT;
        }

        @Override
        public Result getResult(MobEffectFogFunction object) {
            return object != null ? Result.DO_RENDER : Result.ALLOW_NEXT;
        }
    }

    public static class ThickFogChecker extends AbstractChecker<Boolean> {
        @Override
        public Mode getMode() {
            return Mode.THICK;
        }

        @Override
        public Result getResult(Boolean thickFog) {
            return thickFog ? Result.DO_RENDER : Result.ALLOW_NEXT;
        }
    }

    public static class SkyChecker implements IChecker {
        @Override
        public Mode getMode() {
            return Mode.SKY;
        }

        @Override
        public Result getResult(CurrentDataStorage settings, Entity entity, FogRenderer.FogMode fogMode, FogType fogType) {
            return fogMode == FogRenderer.FogMode.FOG_SKY ? Result.SKIP_STACK : Result.ALLOW_NEXT;
        }
    }

    public static class TerrainChecker implements IChecker {
        @Override
        public Mode getMode() {
            return Mode.TERRAIN;
        }

        @Override
        public Result getResult(CurrentDataStorage settings, Entity entity, FogRenderer.FogMode fogMode, FogType fogType) {
            ModFogData biomeData = settings.getBiomeFogData(ClientUtilities.getCurrentBiomeLocation());
            if (biomeData.isOverrideGameFog() && (!biomeData.isFogEnabled() || biomeData.hasValidFogDistance())) {
                return Result.DO_RENDER;
            } else {
                ModFogData dimensionData = settings.getDimensionFogData(ClientUtilities.getCurrentDimensionLocation());
                if (dimensionData != null && dimensionData.isOverrideGameFog() && (!dimensionData.isFogEnabled() || dimensionData.hasValidFogDistance())) {
                    return Result.DO_RENDER;
                }
            }
            return Result.ALLOW_NEXT;
        }
    }

    public static class WeatherChecker implements IChecker {
        @Override
        public Mode getMode() {
            return Mode.WEATHER;
        }

        @Override
        public Result getResult(CurrentDataStorage settings, Entity entity, FogRenderer.FogMode fogMode, FogType fogType) {
            Level level = entity.level();
            ModFogData biomeData = settings.getBiomeFogData(ClientUtilities.getCurrentBiomeLocation());
            ModFogData dimensionData = settings.getDimensionFogData(ClientUtilities.getCurrentDimensionLocation());
            if (level.isRaining() && (biomeData.getRain().isEnabled() || dimensionData.getRain().isEnabled())) {
                return Result.DO_RENDER;
            }
            return Result.ALLOW_NEXT;
        }
    }
}
