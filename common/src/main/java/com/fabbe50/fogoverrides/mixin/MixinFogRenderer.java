package com.fabbe50.fogoverrides.mixin;

import com.fabbe50.fogoverrides.ColorUtils;
import com.fabbe50.fogoverrides.Utilities;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.FogUtils;
import com.fabbe50.fogoverrides.data.FogSetting;
import com.fabbe50.fogoverrides.data.ModFogData;
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
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
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
            FogUtils.processFog(mode, entity, effect, color, renderDistance, partialTicks, fogType, fogData, cir);
        }
    }

    @Inject(at = @At(value = "RETURN"), method = "computeFogColor", cancellable = true)
    private static void injectComputeFogColor(Camera camera, float gameTime, ClientLevel clientLevel, int i, float partialTicks, CallbackInfoReturnable<Vector4f> cir) {
        float rainLevel = clientLevel.getRainLevel(gameTime);
        if (rainLevel > 0) {
            CurrentDataStorage settings = CurrentDataStorage.INSTANCE;
            ColorUtils.processColor(settings, rainLevel, camera, gameTime, clientLevel, cir);
        }
    }
}
