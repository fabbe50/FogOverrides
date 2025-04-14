package com.fabbe50.fogoverrides.mixin;

import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, priority = 1500)
public abstract class MixinLevelRenderer {
    @Shadow private int ticks;

    @Shadow @Nullable
    private ClientLevel level;

    @Shadow private int prevCloudX;

    @Shadow private int prevCloudY;

    @Shadow private int prevCloudZ;

    @Shadow @Final private Minecraft minecraft;

    @Shadow @Nullable private CloudStatus prevCloudsType;

    @Shadow private Vec3 prevCloudColor;

    @Shadow private boolean generateClouds;

    @Shadow @Nullable private VertexBuffer cloudBuffer;

    @Shadow protected abstract BufferBuilder.RenderedBuffer buildClouds(BufferBuilder bufferBuilder, double d, double e, double f, Vec3 vec3);

    @Shadow @Final private static ResourceLocation CLOUDS_LOCATION;

    @Inject(at = @At(value = "HEAD"), method = "renderClouds", cancellable = true)
    private void injectRenderClouds(PoseStack poseStack, Matrix4f matrix4f, float f, double d, double e, double g, CallbackInfo ci) {
        if (ModConfig.INSTANCE.modActive) {
            if (level != null && Float.isNaN(level.effects().getCloudHeight())) {
                ci.cancel();
                return;
            }
            float cloudHeight = CurrentDataStorage.INSTANCE.getCloudHeight();
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.depthMask(true);
            float blockDiameter = 12f;
            float blockHeight = 4f;
            double colorDifference = 2.0E-4;
            double l = (((float) this.ticks + f) * 0.03f);
            double m = (d + l) / blockDiameter;
            double n = (cloudHeight - (float) e + 0.33f);
            double o = g / blockDiameter + 0.33000001311302185;
            m -= Mth.floor(m / 2048.0) * 2048;
            o -= Mth.floor(o / 2048.0) * 2048;
            float posX = (float) (m - (double) Mth.floor(m));
            float posY = (float) (n / blockHeight - (double) Mth.floor(n / blockHeight)) * blockHeight;
            float posZ = (float) (o - (double) Mth.floor(o));
            if (level == null) {
                ci.cancel();
                return;
            }
            Vec3 cloudColor = this.level.getCloudColor(f);
            int cloudX = (int) Math.floor(m);
            int cloudY = (int) Math.floor(n / blockHeight);
            int cloudZ = (int) Math.floor(o);
            if (cloudX != this.prevCloudX || cloudY != this.prevCloudY || cloudZ != this.prevCloudZ || this.minecraft.options.getCloudsType() != this.prevCloudsType || this.prevCloudColor.distanceToSqr(cloudColor) > colorDifference) {
                this.prevCloudX = cloudX;
                this.prevCloudY = cloudY;
                this.prevCloudZ = cloudZ;
                this.prevCloudColor = cloudColor;
                this.prevCloudsType = this.minecraft.options.getCloudsType();
                this.generateClouds = true;
            }
            if (this.generateClouds) {
                this.generateClouds = false;
                if (this.cloudBuffer != null) {
                    this.cloudBuffer.close();
                }
                this.cloudBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
                this.cloudBuffer.bind();
                this.cloudBuffer.upload(this.buildClouds(Tesselator.getInstance().getBuilder(), m, n, o, cloudColor));
                VertexBuffer.unbind();
            }
            FogRenderer.levelFogColor();
            RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
            RenderSystem.setShaderTexture(0, CLOUDS_LOCATION);
            poseStack.pushPose();
            poseStack.scale(blockDiameter, 1.0f, blockDiameter);
            poseStack.translate(-posX, posY, -posZ);
            if (this.cloudBuffer != null) {
                this.cloudBuffer.bind();
                int v = this.prevCloudsType == CloudStatus.FANCY ? 0 : 1;
                for (int w = v; w < 2; w++) {
                    if (w == 0) {
                        RenderSystem.colorMask(false, false, false, false);
                    } else {
                        RenderSystem.colorMask(true, true, true, true);
                    }
                    ShaderInstance shaderInstance = RenderSystem.getShader();
                    this.cloudBuffer.drawWithShader(poseStack.last().pose(), matrix4f, shaderInstance);
                }
                VertexBuffer.unbind();
            }
            poseStack.popPose();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            ci.cancel();
        }
    }
}
