package com.fabbe50.fogoverrides.mixin;

import com.fabbe50.fogoverrides.FogOverrides;
import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.Utilities;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.ModFogData;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public abstract class MixinFogRenderer {

    @Shadow
    @Nullable
    private static FogRenderer.MobEffectFogFunction getPriorityFogFunction(Entity arg, float f) {
        return null;
    }

    @Inject(at = @At(value = "HEAD"), method = "setupFog", cancellable = true)
    private static void injectSetupFog(Camera camera, FogRenderer.FogMode fogMode, float renderDistance, boolean isSpecialFog, float smoothingVar, CallbackInfo ci) {
        CurrentDataStorage settings = CurrentDataStorage.INSTANCE;
        FogType fogType = camera.getFluidInCamera();
        Entity entity = camera.getEntity();
        ModFogData dimensionFogData = settings.getFogDataFromDimension(Utilities.getCurrentDimensionLocation());
        ModFogData modFogData = settings.getBiomeFogData(Utilities.getCurrentBiomeLocation());
        FogRenderer.FogData fogData = new FogRenderer.FogData(fogMode);
        FogRenderer.MobEffectFogFunction mobEffectFogFunction = getPriorityFogFunction(entity, smoothingVar);
        if (fogType == FogType.LAVA) {
            if (entity.isSpectator()) {
                fogData.start = -8.0f;
                fogData.end = renderDistance * 0.5f;
                if (settings.getSpectatorHasModFog()) {
                    if (settings.getSpectatorLavaNearDistance() != -1) {
                        fogData.start = settings.getSpectatorLavaNearDistance();
                    }
                    if (settings.getSpectatorLavaFarDistance() != -1) {
                        fogData.end = settings.getSpectatorLavaFarDistance();
                    }
                }
            } else if (entity instanceof Player && ((Player)entity).isCreative() && settings.getCreativeHasModFog() && settings.getCreativeLavaNearDistance() != -1 && settings.getCreativeLavaFarDistance() != -1) {
                fogData.start = settings.getCreativeLavaNearDistance();
                fogData.end = settings.getCreativeLavaFarDistance();
            } else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(MobEffects.FIRE_RESISTANCE)) {
                fogData.start = 0.0f;
                fogData.end = 3.0f;
                if (modFogData.isLavaPotionEffect() && modFogData.hasValidLavaPotionFogDistance()) {
                    fogData.start = modFogData.getLavaPotionNearDistance();
                    fogData.end = modFogData.getLavaPotionFarDistance();
                } else if (dimensionFogData != null && dimensionFogData.isLavaPotionEffect() && dimensionFogData.hasValidLavaPotionFogDistance()) {
                    fogData.start = dimensionFogData.getLavaPotionNearDistance();
                    fogData.end = dimensionFogData.getLavaPotionFarDistance();
                }
            } else {
                fogData.start = 0.25f;
                fogData.end = 1.0f;
                if (modFogData.isOverrideLavaFog() && modFogData.hasValidLavaFogDistance()) {
                    fogData.start = modFogData.getLavaNearDistance();
                    fogData.end = modFogData.getLavaFarDistance();
                } else if (dimensionFogData != null && dimensionFogData.isOverrideLavaFog() && dimensionFogData.hasValidLavaFogDistance()) {
                    fogData.start = dimensionFogData.getLavaNearDistance();
                    fogData.end = dimensionFogData.getLavaFarDistance();
                }
            }
        } else if (fogType == FogType.POWDER_SNOW) {
            // TODO: Implement powder snow overrides
            if (entity.isSpectator()) {
                fogData.start = -8.0f;
                fogData.end = renderDistance * 0.5f;
            } else {
                fogData.start = 0.0f;
                fogData.end = 2.0f;
            }
        } else if (mobEffectFogFunction != null) {
            // TODO: Mob effect overrides
            LivingEntity livingEntity = (LivingEntity) entity;
            MobEffectInstance mobEffectInstance = livingEntity.getEffect(mobEffectFogFunction.getMobEffect());
            if (mobEffectInstance != null) {
                mobEffectFogFunction.setupFog(fogData, livingEntity, mobEffectInstance, renderDistance, smoothingVar);
            }
        } else if (fogType == FogType.WATER) {
            fogData.start = -8.0f;
            fogData.end = 96.0f;
            if (entity.isSpectator() && settings.getSpectatorHasModFog() && settings.getSpectatorWaterNearDistance() != -1 && settings.getSpectatorWaterFarDistance() != -1) {
                fogData.start = settings.getSpectatorWaterNearDistance();
                fogData.end = settings.getSpectatorWaterFarDistance();
            } else if (entity instanceof Player && ((Player)entity).isCreative() && settings.getCreativeHasModFog() && settings.getCreativeWaterNearDistance() != -1 && settings.getCreativeWaterFarDistance() != -1) {
                fogData.start = settings.getCreativeWaterNearDistance();
                fogData.end = settings.getCreativeWaterFarDistance();
            } else if ((modFogData.isWaterPotionEffect() || (dimensionFogData != null && dimensionFogData.isWaterPotionEffect())) && entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(MobEffects.WATER_BREATHING)) {
                if (modFogData.isWaterPotionEffect() && modFogData.hasValidWaterPotionFogDistance()) {
                    fogData.start = modFogData.getWaterPotionNearDistance();
                    fogData.end = modFogData.getWaterPotionFarDistance();
                } else if (dimensionFogData != null && dimensionFogData.isWaterPotionEffect() && dimensionFogData.hasValidWaterPotionFogDistance()) {
                    fogData.start = dimensionFogData.getWaterPotionNearDistance();
                    fogData.end = dimensionFogData.getWaterPotionFarDistance();
                }
            } else {
                if (modFogData.isOverrideWaterFog() && modFogData.hasValidWaterFogDistance()) {
                    fogData.start = modFogData.getWaterNearDistance();
                    fogData.end = modFogData.getWaterFarDistance();
                } else if (dimensionFogData != null && dimensionFogData.isOverrideWaterFogColor() && dimensionFogData.hasValidWaterFogDistance()) {
                    fogData.start = dimensionFogData.getWaterNearDistance();
                    fogData.end = dimensionFogData.getWaterFarDistance();
                }
                if (entity instanceof LocalPlayer localPlayer) {
                    if (fogData.start > 0)
                        fogData.start *= Math.max(0.25f, localPlayer.getWaterVision());
                    fogData.end *= Math.max(0.25f, localPlayer.getWaterVision());
                    Holder<Biome> holder = localPlayer.level().getBiome(localPlayer.blockPosition());
                    if (holder.is(BiomeTags.HAS_CLOSER_WATER_FOG)) {
                        if (fogData.start > 0)
                            fogData.start *= 0.85f;
                        fogData.end *= 0.85f;
                    }
                }
                if (fogData.end > renderDistance) {
                    fogData.end = renderDistance;
                    fogData.shape = FogShape.CYLINDER;
                }
            }
        } else if (entity.isSpectator() && settings.getSpectatorHasModFog() && settings.getSpectatorNearDistance() != -1 && settings.getSpectatorFarDistance() != -1) {
            fogData.start = settings.getSpectatorNearDistance();
            fogData.end = settings.getSpectatorFarDistance();
        } else if (entity instanceof Player && ((Player) entity).isCreative() && settings.getCreativeHasModFog() && settings.getCreativeNearDistance() != -1 && settings.getCreativeFarDistance() != -1) {
            fogData.start = settings.getCreativeNearDistance();
            fogData.end = settings.getCreativeFarDistance();
        } else if (isSpecialFog) {
            fogData.start = renderDistance * 0.05f;
            fogData.end = Math.min(renderDistance, 192.0f) * 0.5f;
            if (modFogData.isOverrideGameFog()) {
                if (modFogData.isFogEnabled()) {
                    if (modFogData.hasValidFogDistance()) {
                        fogData.start = modFogData.getNearDistance();
                        fogData.end = modFogData.getFarDistance();
                    }
                } else {
                    fogData.start = Integer.MAX_VALUE - 1;
                    fogData.end = Integer.MAX_VALUE;
                }
            } else if (dimensionFogData != null && dimensionFogData.isOverrideGameFog()) {
                if (dimensionFogData.isFogEnabled()) {
                    if (dimensionFogData.hasValidFogDistance()) {
                        fogData.start = dimensionFogData.getNearDistance();
                        fogData.end = dimensionFogData.getFarDistance();
                    }
                } else {
                    fogData.start = Integer.MAX_VALUE - 1;
                    fogData.end = Integer.MAX_VALUE;
                }
            }
            fogData.shape = FogShape.CYLINDER;
        } else if (fogMode == FogRenderer.FogMode.FOG_SKY) {
            fogData.start = 0.0f;
            fogData.end = renderDistance;
            if (modFogData.isOverrideGameFog()) {
                if (modFogData.isFogEnabled()) {
                    if (modFogData.hasValidFogDistance()) {
                        fogData.start = modFogData.getNearDistance();
                        fogData.end = modFogData.getFarDistance();
                    }
                } else {
                    fogData.start = Integer.MAX_VALUE - 1;
                    fogData.end = Integer.MAX_VALUE;
                }
            } else if (dimensionFogData != null && dimensionFogData.isOverrideGameFog()) {
                if (dimensionFogData.isFogEnabled()) {
                    if (dimensionFogData.hasValidFogDistance()) {
                        fogData.start = dimensionFogData.getNearDistance();
                        fogData.end = dimensionFogData.getFarDistance();
                    }
                } else {
                    fogData.start = Integer.MAX_VALUE - 1;
                    fogData.end = Integer.MAX_VALUE;
                }
            }
            fogData.shape = FogShape.CYLINDER;
        } else {
            float h = Mth.clamp(renderDistance / 10.0f, 4.0f, 64.0f);
            fogData.start = renderDistance - h;
            fogData.end = renderDistance;
            if (modFogData.isOverrideGameFog()) {
                if (modFogData.isFogEnabled()) {
                    if (modFogData.hasValidFogDistance()) {
                        float h1 = Mth.clamp(modFogData.getFarDistance() / 10.0f, 4.0f, 64.0f);
                        fogData.start = modFogData.getFarDistance() - h1;
                        fogData.end = modFogData.getFarDistance();
                    }
                } else {
                    fogData.start = Integer.MAX_VALUE - 1;
                    fogData.end = Integer.MAX_VALUE;
                }
            } else if (dimensionFogData != null && dimensionFogData.isOverrideGameFog()) {
                if (dimensionFogData.isFogEnabled()) {
                    if (dimensionFogData.hasValidFogDistance()) {
                        float h1 = Mth.clamp(dimensionFogData.getFarDistance() / 10.0f, 4.0f, 64.0f);
                        fogData.start = dimensionFogData.getNearDistance() - h1;
                        fogData.end = dimensionFogData.getFarDistance();
                    }
                } else {
                    fogData.start = Integer.MAX_VALUE - 1;
                    fogData.end = Integer.MAX_VALUE;
                }
            }
            fogData.shape = FogShape.CYLINDER;
        }

        FogOverrides.setCurrentFogData(fogData);
        RenderSystem.setShaderFogStart(fogData.start);
        RenderSystem.setShaderFogEnd(fogData.end);
        RenderSystem.setShaderFogShape(fogData.shape);
        ci.cancel();
    }
}
