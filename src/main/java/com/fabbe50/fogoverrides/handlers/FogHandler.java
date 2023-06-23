package com.fabbe50.fogoverrides.handlers;

import com.fabbe50.fogoverrides.FogOverrides;
import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.Util;
import com.fabbe50.fogoverrides.animations.Transitions;
import com.fabbe50.fogoverrides.holders.ConfigHolder;
import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.*;
import java.util.Objects;

public class FogHandler {
    private int oldStartDistance, oldEndDistance;
    private int targetStartDistance, targetEndDistance;
    private float minStart, maxStart;
    private float minEnd, maxEnd;
    private float currentNear, currentFar;
    private int currentColor, targetColor;
    private float currentBlendPercentage;
    private String oldFogType = "";


    private static boolean fogActive = false;
    private static boolean startAnimating = false;
    private static boolean startIncreasing = false;
    private static boolean endAnimating = false;
    private static boolean endIncreasing = false;

    public static boolean isFogActive() {
        return fogActive;
    }

    public static boolean isStartAnimating() {
        return startAnimating;
    }

    public static boolean isStartIncreasing() {
        return startIncreasing;
    }

    public static boolean isEndAnimating() {
        return endAnimating;
    }

    public static boolean isEndIncreasing() {
        return endIncreasing;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @OnlyIn(Dist.CLIENT)
    public void densityAdjust(ViewportEvent.RenderFog event) {
        fogActive = false;
        startAnimating = false;
        startIncreasing = false;
        endAnimating = false;
        endIncreasing = false;
        if (oldFogType == null) {
            oldFogType = "";
        }

        if (ConfigHolder.getGeneral().isAdjustDistance()) {
            if (FogOverrides.data.getCurrentFogType().contains("(null)")) {
                event.setFogShape(FogShape.CYLINDER);
            } else {
                event.setFogShape(FogShape.SPHERE);
            }

            // TODO: Make this more efficient.
            /* Start: Fog Start Animation */
            if (FogOverrides.data.getTargetStartDistance() == Integer.MAX_VALUE || FogOverrides.data.getTargetEndDistance() == Integer.MAX_VALUE) {
                event.setNearPlaneDistance((FogOverrides.data.getTargetStartDistance()));
                event.setFarPlaneDistance(FogOverrides.data.getTargetEndDistance());
                FogOverrides.data.setCurrentStartDistance(event.getNearPlaneDistance());
                FogOverrides.data.setCurrentEndDistance(event.getFarPlaneDistance());
                event.setCanceled(true);
            } else if (FogOverrides.data.getTargetStartDistance() != -1 || FogOverrides.data.getTargetEndDistance() != -1) {
                fogActive = true;
                if (FogOverrides.data.getTargetStartDistance() != targetStartDistance) {
                    oldStartDistance = targetStartDistance;
                    targetStartDistance = FogOverrides.data.getTargetStartDistance();
                    if (targetStartDistance > currentNear) {
                        minStart = currentNear;
                        maxStart = targetStartDistance;
                    } else {
                        minStart = targetStartDistance;
                        maxStart = currentNear;
                    }
                }
                if (currentNear != targetStartDistance) {
                    if (FogOverrides.data.getCurrentFogType().contains("liquids") && !oldFogType.contains("liquids")) {
                        currentNear = targetStartDistance;
                    } else {
                        startAnimating = true;
                        if (currentNear < targetStartDistance && currentNear < currentFar - 1) {
                            startIncreasing = true;
                            currentNear += animationCurve(oldStartDistance, currentNear);
                        } else if (currentNear > targetStartDistance) {
                            currentNear -= animationCurve(targetStartDistance, currentNear);
                        }
                        currentNear = Mth.clamp(currentNear, minStart, maxStart);
                    }
                    FogOverrides.data.setCurrentStartDistance(currentNear);
                }
                event.setNearPlaneDistance(FogOverrides.data.getCurrentStartDistance());
                /* End: Fog Start Animation */

                /* Start: Fog End Animation */
                if (FogOverrides.data.getTargetEndDistance() != targetEndDistance) {
                    oldEndDistance = targetEndDistance;
                    targetEndDistance = FogOverrides.data.getTargetEndDistance();
                    if (targetEndDistance > currentFar) {
                        minEnd = currentFar;
                        maxEnd = targetEndDistance;
                    } else {
                        minEnd = targetEndDistance;
                        maxEnd = currentFar;
                    }
                }
                if (currentFar != targetEndDistance) {
                    if (FogOverrides.data.getCurrentFogType().contains("liquids") && !oldFogType.contains("liquids")) {
                        currentFar = targetEndDistance;
                    } else {
                        endAnimating = true;
                        if (currentFar < targetEndDistance) {
                            endIncreasing = true;
                            currentFar += animationCurve(oldEndDistance, currentFar);
                        } else if (currentFar > targetEndDistance && currentFar > currentNear + 1) {
                            currentFar -= animationCurve(targetEndDistance, currentFar);
                        }
                        currentFar = Mth.clamp(currentFar, minEnd, maxEnd);
                    }
                    FogOverrides.data.setCurrentEndDistance(currentFar);
                }
                event.setFarPlaneDistance(FogOverrides.data.getCurrentEndDistance());
                /* End: Fog End Animation */

                if (!oldFogType.equals(FogOverrides.data.getCurrentFogType())) {
                    oldFogType = FogOverrides.data.getCurrentFogType();
                }
                event.setCanceled(true);
            } else {
                currentFar = event.getFarPlaneDistance();
                FogOverrides.data.setCurrentEndDistance(currentFar);
                currentNear = event.getNearPlaneDistance();
                FogOverrides.data.setCurrentStartDistance(currentNear);
            }
        }
    }

    public float animationCurve(int target, float current) {
        return ((current > target + 1000) ? 10F : ((current > target + 300) ? 0.075F : ((current > target + 100) ? 0.05F : ((current > target + 5F) ? 0.015F : 0.008F))));
    }


    private Transitions.RGBAnimator rgbAnimator;

    private int transitionTick;
    private static boolean colorActive = false;
    private static boolean targetGame = false;
    private static boolean colorAdjusting = false;
    private int oldDefaultColor = 0x000;

    public static boolean isColorActive() {
        return colorActive;
    }

    public static boolean isTargetGame() {
        return targetGame;
    }

    public static boolean isColorAdjusting() {
        return colorAdjusting;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @OnlyIn(Dist.CLIENT)
    public void colorAdjust(ViewportEvent.ComputeFogColor event) {
        colorActive = false;
        targetGame = false;
        colorAdjusting = false;

        if (ConfigHolder.getGeneral().isAdjustColors()) {
            colorActive = true;
            int defaultColor = new Color(Util.getColorIntegerFromRGB(event.getRed() * 255, event.getGreen() * 255, event.getBlue() * 255)).getRGB();
            if (oldDefaultColor != defaultColor) {
                oldDefaultColor = defaultColor;
            }
            int targetColorWrapped = new Color(FogOverrides.data.getTargetColor()).getRGB();
            //TODO: Add support for potion effects and such.
            if (FogOverrides.data.getTargetColor() == -1) {
                if (targetColor != defaultColor) {
                    targetColor = defaultColor;
                    rgbAnimator = new Transitions.RGBAnimator(currentColor, targetColor, getFrameCount());
                    transitionTick = 0;
                }
                targetGame = true;
            } else if (Objects.nonNull(FogOverrides.data.getCurrentFogType()) && FogOverrides.data.getCurrentFogType().contains("liquids") && FogOverrides.data.getTargetBlendColor() == -1) {
                currentBlendPercentage = FogOverrides.data.getTargetBlendPercentage();
                int tempColor = new Color(Util.getBlendedColor(FogOverrides.data.getTargetBaseColor(), oldDefaultColor, currentBlendPercentage)).getRGB();
                if (targetColor != tempColor) {
                    targetColor = tempColor;
                    rgbAnimator = new Transitions.RGBAnimator(currentColor, targetColor, getFrameCount());
                    transitionTick = 0;
                }
            } else if (targetColorWrapped != targetColor) {
                targetColor = targetColorWrapped;
                rgbAnimator = new Transitions.RGBAnimator(currentColor, targetColor, getFrameCount());
                transitionTick = 0;
            }
            if (currentColor != targetColor) {
                colorAdjusting = true;
                transitionTick++;
                if (transitionTick % 3 == 0) {
                    currentColor = rgbAnimator.getColor();
                    FogOverrides.data.setCurrentColor(currentColor);
                }
            }
            int[] color = Util.getRGBFromColorInteger(FogOverrides.data.getCurrentColor());
            event.setRed(color[0] / 255F);
            event.setGreen(color[1] / 255F);
            event.setBlue(color[2] / 255F);
            event.setCanceled(true);
        }
    }

    private int getFrameCount() {
        int baseFrames = 200;
        if (targetStartDistance > currentNear && targetEndDistance > currentFar) {
            return ((int)((targetStartDistance - currentNear) + (targetEndDistance - currentFar)) / 2) + baseFrames;
        } else if (targetStartDistance > currentNear && targetEndDistance < currentFar) {
            return ((int)((targetStartDistance - currentNear) + (currentFar - targetEndDistance)) / 2) + baseFrames;
        } else if (targetStartDistance < currentNear && targetEndDistance < currentFar) {
            return ((int)((currentNear - targetStartDistance) + (currentFar - targetEndDistance)) / 2) + baseFrames;
        } else if (targetStartDistance < currentNear && targetEndDistance > currentFar) {
            return ((int)((currentNear - targetStartDistance) + (targetEndDistance - currentFar)) / 2) + baseFrames;
        } else {
            return baseFrames;
        }
    }
}
