package com.fabbe50.fogoverrides.mixin;

import com.fabbe50.fogoverrides.ColorUtils;
import com.fabbe50.fogoverrides.FogUtils;
import com.fabbe50.fogoverrides.Utilities;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.F3Information;
import com.fabbe50.fogoverrides.data.FogParameters;
import com.fabbe50.fogoverrides.data.checker.Mode;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector4f;
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

    @Shadow private static float fogRed;

    @Shadow private static float fogGreen;

    @Shadow private static float fogBlue;

    @Inject(at = @At(value = "RETURN"), method = "setupFog", cancellable = true)
    private static void injectSetupFog(Camera camera, FogRenderer.FogMode fogMode, float renderDistance, boolean thickFog, float partialTicks, CallbackInfo ci) {
        Vector4f color = new Vector4f(RenderSystem.getShaderFogColor());
        CurrentDataStorage settings = CurrentDataStorage.INSTANCE;
        FogUtils.setRenderDistance(Minecraft.getInstance().options.renderDistance().get());
        FogUtils.setCalculationSetting(settings.getCalculationSetting());

        FogType fogType = camera.getFluidInCamera();
        FogRenderer.FogData fogData = new FogRenderer.FogData(fogMode);
        Entity entity = camera.getEntity();
        FogRenderer.MobEffectFogFunction effect = getPriorityFogFunction(entity, partialTicks);

        Mode mode = FogUtils.getMode(settings, entity, effect, fogMode, fogType, thickFog);

        if (mode != Mode.VANILLA) {
            FogParameters fogParameters = FogUtils.processFog(mode, entity, effect, color, renderDistance, partialTicks, fogType, fogData);
            if (fogParameters != null) {
                RenderSystem.setShaderFogStart(fogParameters.start());
                RenderSystem.setShaderFogEnd(fogParameters.end());
                RenderSystem.setShaderFogShape(fogParameters.shape());
                RenderSystem.setShaderFogColor(fogParameters.red(), fogParameters.green(), fogParameters.blue(), fogParameters.alpha());
            }
        } else {
            F3Information.setCurrentFogData(fogData, "VANILLA");
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V"), method = "setupColor", cancellable = true)
    private static void injectSetupColor(Camera camera, float gameTime, ClientLevel clientLevel, int i, float partialTicks, CallbackInfo ci) {
        float rainLevel = clientLevel.getRainLevel(gameTime);
        Vector4f color = new Vector4f(fogRed, fogGreen, fogBlue, 0);
        if (rainLevel > 0) {
            CurrentDataStorage settings = CurrentDataStorage.INSTANCE;
            color = ColorUtils.processColor(settings, rainLevel, camera, gameTime, clientLevel);
            if (color == null) {
                color = new Vector4f(fogRed, fogGreen, fogBlue, 0);
            }
        }
        F3Information.setCurrentColor(Utilities.getColorIntegerFromVec4F(color));
        RenderSystem.clearColor(color.x, color.y, color.z, 0);
    }
}
