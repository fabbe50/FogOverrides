package com.fabbe50.fogoverrides.mixin;

import com.fabbe50.fogoverrides.FogOverrides;
import com.fabbe50.fogoverrides.Utilities;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.ModFogData;
import com.mojang.blaze3d.shaders.FogShape;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FogRenderer.class, priority = 900)
public abstract class MixinFogRenderer {

    @Shadow
    @Nullable
    private static FogRenderer.MobEffectFogFunction getPriorityFogFunction(Entity arg, float f) {
        return null;
    }

    @Shadow private static boolean fogEnabled;

    @Inject(at = @At(value = "RETURN"), method = "setupFog", cancellable = true)
    private static void injectSetupFog(Camera camera, FogRenderer.FogMode fogMode, Vector4f color, float renderDistance, boolean isSpecialFog, float smoothingVar, CallbackInfoReturnable<FogParameters> cir) {
        if (!fogEnabled) {
            cir.setReturnValue(FogParameters.NO_FOG);
        }
        /*if (fogMode == FogRenderer.FogMode.FOG_SKY) {
            return;
        }*/
        CurrentDataStorage settings = CurrentDataStorage.INSTANCE;
        FogType fogType = camera.getFluidInCamera();
        Entity entity = camera.getEntity();
        ModFogData dimensionFogData = settings.getFogDataFromDimension(Utilities.getCurrentDimensionLocation());
        ModFogData biomeFogData = settings.getBiomeFogData(Utilities.getCurrentBiomeLocation());
        FogRenderer.FogData fogData = new FogRenderer.FogData(fogMode);
        FogRenderer.MobEffectFogFunction mobEffectFogFunction = getPriorityFogFunction(entity, smoothingVar);
        if (fogType == FogType.LAVA) {
            fogoverrides$overrideLavaFog(entity, settings, renderDistance, color, fogData, biomeFogData, dimensionFogData, cir);
        } else if (fogType == FogType.POWDER_SNOW) {
            // TODO: Implement powder snow overrides
            if (entity.isSpectator()) {
                fogData.start = -8.0f;
                fogData.end = renderDistance * 0.5f;
            } else {
                fogData.start = 0.0f;
                fogData.end = 2.0f;
            }
            FogOverrides.setCurrentFogData(fogData, "IN_POWDERED_SNOW");
            cir.setReturnValue(new FogParameters(fogData.start, fogData.end, fogData.shape, color.x, color.y, color.z, color.w));
        } else if (mobEffectFogFunction != null) {
            // TODO: Mob effect overrides
            LivingEntity livingEntity = (LivingEntity) entity;
            MobEffectInstance mobEffectInstance = livingEntity.getEffect(mobEffectFogFunction.getMobEffect());
            if (mobEffectInstance != null) {
                mobEffectFogFunction.setupFog(fogData, livingEntity, mobEffectInstance, renderDistance, smoothingVar);
            }
            FogOverrides.setCurrentFogData(fogData, "MOB_EFFECT");
            cir.setReturnValue(new FogParameters(fogData.start, fogData.end, fogData.shape, color.x, color.y, color.z, color.w));
        } else if (fogType == FogType.WATER) {
            fogoverrides$overrideUnderWaterFog(entity, settings, renderDistance, color, fogData, biomeFogData, dimensionFogData, cir);
        } else if (entity.isSpectator() && settings.getSpectatorHasModFog() && settings.getSpectatorNearDistance() != -1 && settings.getSpectatorFarDistance() != -1) {
            fogData.start = settings.getSpectatorNearDistance();
            fogData.end = settings.getSpectatorFarDistance();
            fogData.shape = FogShape.CYLINDER;
            FogOverrides.setCurrentFogData(fogData, "SPECTATOR_MODE");
            cir.setReturnValue(new FogParameters(fogData.start, fogData.end, fogData.shape, color.x, color.y, color.z, color.w));
        } else if (entity instanceof Player && ((Player) entity).isCreative() && settings.getCreativeHasModFog() && settings.getCreativeNearDistance() != -1 && settings.getCreativeFarDistance() != -1) {
            fogData.start = settings.getCreativeNearDistance();
            fogData.end = settings.getCreativeFarDistance();
            fogData.shape = FogShape.CYLINDER;
            FogOverrides.setCurrentFogData(fogData, "CREATIVE_MODE");
            cir.setReturnValue(new FogParameters(fogData.start, fogData.end, fogData.shape, color.x, color.y, color.z, color.w));
        } else if (isSpecialFog) {
            fogData.start = renderDistance * 0.05f;
            fogData.end = Math.min(renderDistance, 192.0f) * 0.5f;
            fogoverrides$overrideFog(color, renderDistance, fogData, biomeFogData, dimensionFogData, "SPECIAL_FOG", cir);
        } else if (fogMode == FogRenderer.FogMode.FOG_SKY) {
            /*fogData.start = 0.0F;
            fogData.end = renderDistance * 16;
            fogData.shape = FogShape.CYLINDER;
            cir.setReturnValue(new FogParameters(fogData.start, fogData.end, fogData.shape, color.x, color.y, color.z, color.w));*/
//            fogoverrides$overrideFog(color, renderDistance, fogData, biomeFogData, dimensionFogData, "SKY_FOG", cir);
        } else {
            //float h = Mth.clamp(renderDistance / 10.0f, 4.0f, 64.0f);
            fogoverrides$overrideFog(color, renderDistance, fogData, biomeFogData, dimensionFogData, "OTHER_FOG", cir);
        }
    }

    @Unique
    private static void fogoverrides$overrideFog(Vector4f color, float renderDistance, FogRenderer.FogData fogData, ModFogData biomeFogData, ModFogData dimensionFogData, String fogTypeData, CallbackInfoReturnable<FogParameters> cir) {
        fogData.start = renderDistance;
        fogData.end = renderDistance;
        if (biomeFogData.isOverrideGameFog()) {
            if (biomeFogData.isFogEnabled()) {
                if (biomeFogData.hasValidFogDistance()) {
                    fogData.start = biomeFogData.getNearDistance();
                    fogData.end = biomeFogData.getFarDistance();
                }
            } else {
                fogData.start = Float.MAX_VALUE;
                fogData.end = Float.MAX_VALUE;
            }
        } else if (dimensionFogData != null && dimensionFogData.isOverrideGameFog()) {
            if (dimensionFogData.isFogEnabled()) {
                if (dimensionFogData.hasValidFogDistance()) {
                    fogData.start = dimensionFogData.getNearDistance();
                    fogData.end = dimensionFogData.getFarDistance();
                }
            } else {
                fogData.start = Float.MAX_VALUE;
                fogData.end = Float.MAX_VALUE;
            }
        }
        fogData.shape = FogShape.CYLINDER;
        FogOverrides.setCurrentFogData(fogData, fogTypeData);
        cir.setReturnValue(new FogParameters(fogData.start, fogData.end, fogData.shape, color.x(), color.y(), color.z(), color.w()));
    }

    @Unique
    private static void fogoverrides$overrideUnderWaterFog(Entity entity, CurrentDataStorage settings, float renderDistance, Vector4f color, FogRenderer.FogData fogData, ModFogData biomeFogData, ModFogData dimensionFogData, CallbackInfoReturnable<FogParameters> cir) {
        fogData.start = -8.0f;
        fogData.end = 96.0f;
        if (entity.isSpectator() && settings.getSpectatorHasModFog() && settings.getSpectatorWaterNearDistance() != -1 && settings.getSpectatorWaterFarDistance() != -1) {
            fogData.start = settings.getSpectatorWaterNearDistance();
            fogData.end = settings.getSpectatorWaterFarDistance();
            fogData.shape = FogShape.CYLINDER;
        } else if (entity instanceof Player && ((Player)entity).isCreative() && settings.getCreativeHasModFog() && settings.getCreativeWaterNearDistance() != -1 && settings.getCreativeWaterFarDistance() != -1) {
            fogData.start = settings.getCreativeWaterNearDistance();
            fogData.end = settings.getCreativeWaterFarDistance();
            fogData.shape = FogShape.CYLINDER;
        } else if ((biomeFogData.isWaterPotionEffect() || (dimensionFogData != null && dimensionFogData.isWaterPotionEffect())) && entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(MobEffects.WATER_BREATHING)) {
            if (biomeFogData.isWaterPotionEffect() && biomeFogData.hasValidWaterPotionFogDistance()) {
                fogData.start = biomeFogData.getWaterPotionNearDistance();
                fogData.end = biomeFogData.getWaterPotionFarDistance();
                fogData.shape = FogShape.CYLINDER;
            } else if (dimensionFogData != null && dimensionFogData.isWaterPotionEffect() && dimensionFogData.hasValidWaterPotionFogDistance()) {
                fogData.start = dimensionFogData.getWaterPotionNearDistance();
                fogData.end = dimensionFogData.getWaterPotionFarDistance();
                fogData.shape = FogShape.CYLINDER;
            }
        } else {
            if (biomeFogData.isOverrideWaterFog() && biomeFogData.hasValidWaterFogDistance()) {
                fogData.start = biomeFogData.getWaterNearDistance();
                fogData.end = biomeFogData.getWaterFarDistance();
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
        FogOverrides.setCurrentFogData(fogData, "UNDER_WATER");
        cir.setReturnValue(new FogParameters(fogData.start, fogData.end, fogData.shape, color.x(), color.y(), color.z(), color.w()));
    }

    @Unique
    private static void fogoverrides$overrideLavaFog(Entity entity, CurrentDataStorage settings, float renderDistance, Vector4f color, FogRenderer.FogData fogData, ModFogData biomeFogData, ModFogData dimensionFogData, CallbackInfoReturnable<FogParameters> cir) {
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
            if (biomeFogData.isLavaPotionEffect() && biomeFogData.hasValidLavaPotionFogDistance()) {
                fogData.start = biomeFogData.getLavaPotionNearDistance();
                fogData.end = biomeFogData.getLavaPotionFarDistance();
            } else if (dimensionFogData != null && dimensionFogData.isLavaPotionEffect() && dimensionFogData.hasValidLavaPotionFogDistance()) {
                fogData.start = dimensionFogData.getLavaPotionNearDistance();
                fogData.end = dimensionFogData.getLavaPotionFarDistance();
            }
        } else {
            fogData.start = 0.25f;
            fogData.end = 1.0f;
            if (biomeFogData.isOverrideLavaFog() && biomeFogData.hasValidLavaFogDistance()) {
                fogData.start = biomeFogData.getLavaNearDistance();
                fogData.end = biomeFogData.getLavaFarDistance();
            } else if (dimensionFogData != null && dimensionFogData.isOverrideLavaFog() && dimensionFogData.hasValidLavaFogDistance()) {
                fogData.start = dimensionFogData.getLavaNearDistance();
                fogData.end = dimensionFogData.getLavaFarDistance();
            }
        }
        FogOverrides.setCurrentFogData(fogData, "IN_LAVA");
        cir.setReturnValue(new FogParameters(fogData.start, fogData.end, fogData.shape, color.x(), color.y(), color.z(), color.w()));
    }
}
