package com.fabbe50.fogoverrides.mixin;

import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.renderer.*;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, priority = 1500)
public abstract class MixinLevelRenderer {
    @Shadow @Final private LevelTargetBundle targets;

    @Shadow @Final private CloudRenderer cloudRenderer;

    @Inject(at = @At(value = "HEAD"), method = "addCloudsPass", cancellable = true)
    private void injectAddCloudPass(FrameGraphBuilder frameGraphBuilder, Matrix4f matrix4f, Matrix4f matrix4f2, CloudStatus cloudStatus, Vec3 vec3, float f, int i, float g, CallbackInfo ci) {
        if (ModConfig.INSTANCE.modActive) {
            int cloudHeight = CurrentDataStorage.INSTANCE.getCloudHeight();
            if (cloudHeight != 192) {
                FramePass framePass = frameGraphBuilder.addPass("clouds");
                if (this.targets.clouds != null) {
                    this.targets.clouds = framePass.readsAndWrites(this.targets.clouds);
                } else {
                    this.targets.main = framePass.readsAndWrites(this.targets.main);
                }

                ResourceHandle<RenderTarget> resourceHandle = this.targets.clouds;
                framePass.executes(() -> {
                    if (resourceHandle != null) {
                        resourceHandle.get().setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                        resourceHandle.get().clear();
                    }

                    this.cloudRenderer.render(i, cloudStatus, cloudHeight, matrix4f, matrix4f2, vec3, f);
                });
                ci.cancel();
            }
        }
    }
}
