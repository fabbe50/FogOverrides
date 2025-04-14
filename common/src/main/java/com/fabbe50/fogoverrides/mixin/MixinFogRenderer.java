package com.fabbe50.fogoverrides.mixin;

import com.fabbe50.fogoverrides.*;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.F3Information;
import com.fabbe50.fogoverrides.data.checker.Mode;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.FogRenderer.FogData;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.client.renderer.FogRenderer.MobEffectFogFunction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FogRenderer.class, priority = 1100)
public abstract class MixinFogRenderer {
    @Shadow
    @Nullable
    private static MobEffectFogFunction getPriorityFogFunction(Entity arg, float f) {
        return null;
    }

    @Inject(at = @At(value = "RETURN"), method = "setupFog", cancellable = true)
    private static void injectSetupFog(Camera camera, FogMode fogMode, Vector4f color, float renderDistance, boolean thickFog, float partialTicks, CallbackInfoReturnable<FogParameters> cir) {
        if (ModConfig.INSTANCE.modActive) {
            FogParameters parameters = cir.getReturnValue();
            if (parameters == FogParameters.NO_FOG) {
                return;
            }
            CurrentDataStorage settings = CurrentDataStorage.INSTANCE;
            FogUtils.setRenderDistance(Minecraft.getInstance().options.renderDistance().get());
            FogUtils.setCalculationSetting(settings.getCalculationSetting());

            FogType fogType = camera.getFluidInCamera();
            FogData fogData = new FogData(fogMode);
            Entity entity = camera.getEntity();
            MobEffectFogFunction effect = getPriorityFogFunction(entity, partialTicks);

            Mode mode = FogUtils.getMode(settings, entity, effect, fogMode, fogType, thickFog);

            if (mode != Mode.VANILLA) {
                FogParameters fogParameters = FogUtils.processFog(mode, entity, effect, color, renderDistance, partialTicks, fogType, fogData);
                if (fogParameters != null) {
                    cir.setReturnValue(fogParameters);
                }
            } else {
                F3Information.setCurrentFogData(fogData, "VANILLA");
            }
        }
    }

    @Inject(at = @At(value = "RETURN"), method = "computeFogColor", cancellable = true)
    private static void injectComputeFogColor(Camera camera, float gameTime, ClientLevel clientLevel, int i, float partialTicks, CallbackInfoReturnable<Vector4f> cir) {
        if (ModConfig.INSTANCE.modActive) {
            float rainLevel = clientLevel.getRainLevel(gameTime);
            if (rainLevel > 0) {
                CurrentDataStorage settings = CurrentDataStorage.INSTANCE;
                Vector4f color = ColorUtils.processColor(settings, rainLevel, camera, gameTime, clientLevel);
                if (color == null) {
                    color = cir.getReturnValue();
                }
                cir.setReturnValue(color);
            }
            F3Information.setCurrentColor(Utilities.getColorIntegerFromVec4F(cir.getReturnValue()));
        }
    }
}
