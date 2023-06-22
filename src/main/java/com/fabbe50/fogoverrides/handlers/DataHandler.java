package com.fabbe50.fogoverrides.handlers;

import com.fabbe50.fogoverrides.Util;
import com.fabbe50.fogoverrides.holders.data.IHolder;
import com.fabbe50.fogoverrides.holders.data.IOverrideHolder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public class DataHandler {
    private Holder<Biome> currentBiome = null;
    private int targetStartDistance = 0;
    private int targetEndDistance = 0;
    private int targetBaseColor = 0;
    private int targetBlendColor = 0;
    private float targetBlendPercentage = 0;
    private float currentStartDistance;
    private float currentEndDistance;
    private int currentColor;
    private String currentFogType;
    private String currentFogBlendType;

    public void setBaseTargets(IHolder holder) {
        setTargetStartDistance(holder.getStartDistance());
        setTargetEndDistance(holder.getEndDistance());
        if (holder.shouldOverrideColor()) {
            setTargetBaseColor(holder.getColor());
        } else {
            setTargetBaseColor(-1);
        }
        setCurrentFogType(holder.getHolderType());
    }

    public void setBlendTargets(IHolder holder) {
        setTargetStartDistance(holder.getStartDistance());
        setTargetEndDistance(holder.getEndDistance());
        if (holder.shouldOverrideColor() || holder.getHolderType().equals("liquids")) {
            setTargetBlendColor(holder.getColor());
            if (holder instanceof IOverrideHolder overrideHolder) {
                setTargetBlendPercentage(overrideHolder.getBlendPercentage() / 100f);
            } else {
                setTargetBlendPercentage(0.5f);
            }
        } else {
            setTargetBlendColor(-1);
        }
        setCurrentFogBlendType(holder.getHolderType());
    }

    public void setCurrentBiome(Holder<Biome> currentBiome) {
        this.currentBiome = currentBiome;
    }

    public void setTargetStartDistance(int targetStartDistance) {
        this.targetStartDistance = targetStartDistance;
    }

    public void setTargetEndDistance(int targetEndDistance) {
        this.targetEndDistance = targetEndDistance;
    }

    public void setTargetBaseColor(int targetBaseColor) {
        this.targetBaseColor = targetBaseColor;
    }

    public void setTargetBlendColor(int targetBlendColor) {
        this.targetBlendColor = targetBlendColor;
    }

    public void setCurrentStartDistance(float currentStartDistance) {
        this.currentStartDistance = currentStartDistance;
    }

    public void setCurrentEndDistance(float currentEndDistance) {
        this.currentEndDistance = currentEndDistance;
    }

    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
    }

    public void setCurrentFogType(String fogType) {
        this.currentFogType = fogType;
    }

    public void setCurrentFogBlendType(String currentFogBlendType) {
        this.currentFogBlendType = currentFogBlendType;
    }

    public void setTargetBlendPercentage(float targetBlendPercentage) {
        this.targetBlendPercentage = targetBlendPercentage;
    }

    public Holder<Biome> getCurrentBiome() {
        return currentBiome;
    }

    public int getTargetStartDistance() {
        return targetStartDistance;
    }

    public float getCurrentStartDistance() {
        return currentStartDistance;
    }

    public int getTargetEndDistance() {
        return targetEndDistance;
    }

    public float getCurrentEndDistance() {
        return currentEndDistance;
    }

    public int getTargetBaseColor() {
        return targetBaseColor;
    }

    public int getTargetBlendColor() {
        return targetBlendColor;
    }

    public int getTargetColor() {
        //FIXME: Return -1 if base color is -1 regardless of blend color value.
        return Util.getBlendedColor(getTargetBaseColor(), getTargetBlendColor(), targetBlendPercentage);
    }

    public int getCurrentColor() {
        return currentColor;
    }

    public String getCurrentFogType() {
        return currentFogType + " (" + currentFogBlendType + ")";
    }

    public float getTargetBlendPercentage() {
        return targetBlendPercentage;
    }
}
