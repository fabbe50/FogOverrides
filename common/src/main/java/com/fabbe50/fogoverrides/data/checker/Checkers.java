package com.fabbe50.fogoverrides.data.checker;

import com.fabbe50.fogoverrides.Utilities;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.FogUtils;
import com.fabbe50.fogoverrides.data.ModFogData;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.FogRenderer.MobEffectFogFunction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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
        checkers.add(new ThickFogChecker());
        checkers.add(new SkyChecker());
        checkers.add(new TerrainChecker());
    }

    public static List<IChecker> getCheckers() {
        return checkers;
    }

    public static class SpectatorChecker implements IChecker {
        @Override
        public Mode getMode() {
            return Mode.SPECTATOR;
        }

        @Override
        public Result getResult(CurrentDataStorage settings, Entity entity, FogRenderer.FogMode fogMode, FogType fogType) {
            if (entity.isSpectator()) {
                if (fogType == FogType.WATER) {
                    if (FogUtils.validate(settings.getSpectatorWaterNearDistance(), settings.getSpectatorWaterFarDistance())) {
                        return Result.DO_RENDER;
                    }
                }
                if (fogType == FogType.LAVA) {
                    if (FogUtils.validate(settings.getSpectatorLavaNearDistance(), settings.getSpectatorLavaFarDistance())) {
                        return Result.DO_RENDER;
                    }
                }
                if (fogMode == FogRenderer.FogMode.FOG_TERRAIN) {
                    if (FogUtils.validate(settings.getSpectatorNearDistance(), settings.getSpectatorFarDistance())) {
                        return Result.DO_RENDER;
                    }
                }
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
                if (fogType == FogType.WATER) {
                    if (FogUtils.validate(settings.getCreativeWaterNearDistance(), settings.getCreativeWaterFarDistance())) {
                        return Result.DO_RENDER;
                    }
                }
                if (fogType == FogType.LAVA) {
                    if (FogUtils.validate(settings.getCreativeLavaNearDistance(), settings.getCreativeLavaFarDistance())) {
                        return Result.DO_RENDER;
                    }
                }
                if (fogMode == FogRenderer.FogMode.FOG_TERRAIN) {
                    if (FogUtils.validate(settings.getCreativeNearDistance(), settings.getCreativeFarDistance())) {
                        return Result.DO_RENDER;
                    }
                }
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
            if (entity.isInLiquid()) {
                ModFogData biomeData = settings.getBiomeFogData(Utilities.getCurrentBiomeLocation());
                ModFogData dimensionData = settings.getFogDataFromDimension(Utilities.getCurrentDimensionLocation());
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
                    case null, default -> {
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
            ModFogData biomeData = settings.getBiomeFogData(Utilities.getCurrentBiomeLocation());
            if (biomeData.isOverrideGameFog() && biomeData.hasValidFogDistance()) {
                return Result.DO_RENDER;
            } else {
                ModFogData dimensionData = settings.getFogDataFromDimension(Utilities.getCurrentDimensionLocation());
                if (dimensionData != null && dimensionData.isOverrideGameFog() && dimensionData.hasValidFogDistance()) {
                    return Result.DO_RENDER;
                }
            }
            return Result.ALLOW_NEXT;
        }
    }
}
