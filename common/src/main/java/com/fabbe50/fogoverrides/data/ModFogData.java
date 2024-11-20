package com.fabbe50.fogoverrides.data;

public class ModFogData {
    private boolean overrideGameFog;
    private boolean isFogEnabled;
    private float nearDistance;
    private float farDistance;
    private boolean overrideSkyColor;
    private int skyColor;
    private boolean overrideFogColor;
    private int fogColor;
    private boolean overrideWaterFog;
    private float waterNearDistance;
    private float waterFarDistance;
    private boolean waterPotionEffect;
    private float waterPotionNearDistance;
    private float waterPotionFarDistance;
    private boolean overrideWaterColor;
    private int waterColor;
    private boolean overrideWaterFogColor;
    private int waterFogColor;
    private boolean overrideLavaFog;
    private float lavaNearDistance;
    private float lavaFarDistance;
    private boolean lavaPotionEffect;
    private float lavaPotionNearDistance;
    private float lavaPotionFarDistance;

    public ModFogData(float nearDistance, float farDistance, int color, float waterNearDistance, float waterFarDistance, int waterFogColor) {
        this(nearDistance, farDistance, color, color, waterNearDistance, waterFarDistance, -1, waterFogColor);
    }

    public ModFogData(float nearDistance, float farDistance, int skyColor, int fogColor, float waterNearDistance, float waterFarDistance, int waterColor, int waterFogColor) {
        this(false, true, nearDistance, farDistance, false, skyColor, false, fogColor, false, waterNearDistance, waterFarDistance, false, waterColor, false, waterFogColor);
    }

    public ModFogData(boolean overrideGameFog, boolean isFogEnabled, float nearDistance, float farDistance, boolean overrideSkyColor, int skyColor, boolean overrideFogColor, int fogColor,
                      boolean overrideWaterFog, float waterNearDistance, float waterFarDistance, boolean overrideWaterFogColor, int waterFogColor) {
        this(overrideGameFog, isFogEnabled, nearDistance, farDistance, overrideSkyColor, skyColor, overrideFogColor, fogColor, overrideWaterFog, waterNearDistance, waterFarDistance, false, -1, overrideWaterFogColor, waterFogColor);
    }

    public ModFogData(boolean overrideGameFog, boolean isFogEnabled, float nearDistance, float farDistance, boolean overrideSkyColor, int skyColor, boolean overrideFogColor, int fogColor,
                      boolean overrideWaterFog, float waterNearDistance, float waterFarDistance, boolean overrideWaterColor, int waterColor, boolean overrideWaterFogColor, int waterFogColor) {
        this.overrideGameFog = overrideGameFog;
        this.isFogEnabled = isFogEnabled;
        this.nearDistance = nearDistance;
        this.farDistance = farDistance;
        this.overrideSkyColor = overrideSkyColor;
        this.skyColor = skyColor;
        this.overrideFogColor = overrideFogColor;
        this.fogColor = fogColor;
        this.overrideWaterFog = overrideWaterFog;
        this.waterNearDistance = waterNearDistance;
        this.waterFarDistance = waterFarDistance;
        this.waterPotionEffect = false;
        this.waterPotionNearDistance = waterNearDistance;
        this.waterPotionFarDistance = waterFarDistance;
        this.overrideWaterColor = overrideWaterColor;
        this.waterColor = waterColor;
        this.overrideWaterFogColor = overrideWaterFogColor;
        this.waterFogColor = waterFogColor;
        this.overrideLavaFog = false;
        this.lavaNearDistance = 0.25f;
        this.lavaFarDistance = 1.0f;
        this.lavaPotionEffect = false;
        this.lavaPotionNearDistance = 0.0f;
        this.lavaPotionFarDistance = 3.0f;
    }

    public void setOverrideGameFog(boolean overrideGameFog) {
        this.overrideGameFog = overrideGameFog;
    }

    public boolean isOverrideGameFog() {
        return overrideGameFog;
    }

    public void setFogEnabled(boolean fogEnabled) {
        isFogEnabled = fogEnabled;
    }

    public boolean isFogEnabled() {
        return isFogEnabled;
    }

    public void setNearDistance(float nearDistance) {
        this.nearDistance = nearDistance;
    }

    public float getNearDistance() {
        return nearDistance;
    }

    public void setFarDistance(float farDistance) {
        this.farDistance = farDistance;
    }

    public float getFarDistance() {
        return farDistance;
    }

    public void setOverrideSkyColor(boolean overrideSkyColor) {
        this.overrideSkyColor = overrideSkyColor;
    }

    public boolean isOverrideSkyColor() {
        return overrideSkyColor;
    }

    public void setSkyColor(int skyColor) {
        this.skyColor = skyColor;
    }

    public int getSkyColor() {
        return skyColor;
    }

    public void setOverrideFogColor(boolean overrideFogColor) {
        this.overrideFogColor = overrideFogColor;
    }

    public boolean isOverrideFogColor() {
        return overrideFogColor;
    }

    public void setFogColor(int fogColor) {
        this.fogColor = fogColor;
    }

    public int getFogColor() {
        return fogColor;
    }

    public void setOverrideWaterFog(boolean overrideWaterFog) {
        this.overrideWaterFog = overrideWaterFog;
    }

    public boolean isOverrideWaterFog() {
        return overrideWaterFog;
    }

    public void setWaterNearDistance(float waterNearDistance) {
        this.waterNearDistance = waterNearDistance;
    }

    public float getWaterNearDistance() {
        return waterNearDistance;
    }

    public void setWaterFarDistance(float waterFarDistance) {
        this.waterFarDistance = waterFarDistance;
    }

    public float getWaterFarDistance() {
        return waterFarDistance;
    }

    public void setWaterPotionEffect(boolean waterPotionEffect) {
        this.waterPotionEffect = waterPotionEffect;
    }

    public boolean isWaterPotionEffect() {
        return waterPotionEffect;
    }

    public void setWaterPotionNearDistance(float waterPotionNearDistance) {
        this.waterPotionNearDistance = waterPotionNearDistance;
    }

    public float getWaterPotionNearDistance() {
        return waterPotionNearDistance;
    }

    public void setWaterPotionFarDistance(float waterPotionFarDistance) {
        this.waterPotionFarDistance = waterPotionFarDistance;
    }

    public float getWaterPotionFarDistance() {
        return waterPotionFarDistance;
    }

    public boolean isOverrideWaterColor() {
        return overrideWaterColor;
    }

    public void setOverrideWaterColor(boolean overrideWaterColor) {
        this.overrideWaterColor = overrideWaterColor;
    }

    public void setWaterColor(int waterColor) {
        this.waterColor = waterColor;
    }

    public int getWaterColor() {
        return waterColor;
    }

    public void setOverrideWaterFogColor(boolean overrideWaterFogColor) {
        this.overrideWaterFogColor = overrideWaterFogColor;
    }

    public boolean isOverrideWaterFogColor() {
        return overrideWaterFogColor;
    }

    public void setWaterFogColor(int waterFogColor) {
        this.waterFogColor = waterFogColor;
    }

    public int getWaterFogColor() {
        return waterFogColor;
    }

    public void setOverrideLavaFog(boolean overrideLavaFog) {
        this.overrideLavaFog = overrideLavaFog;
    }

    public boolean isOverrideLavaFog() {
        return overrideLavaFog;
    }

    public void setLavaNearDistance(float lavaNearDistance) {
        this.lavaNearDistance = lavaNearDistance;
    }

    public float getLavaNearDistance() {
        return lavaNearDistance;
    }

    public void setLavaFarDistance(float lavaFarDistance) {
        this.lavaFarDistance = lavaFarDistance;
    }

    public float getLavaFarDistance() {
        return lavaFarDistance;
    }

    public void setLavaPotionEffect(boolean lavaPotionEffect) {
        this.lavaPotionEffect = lavaPotionEffect;
    }

    public boolean isLavaPotionEffect() {
        return lavaPotionEffect;
    }

    public void setLavaPotionNearDistance(float lavaPotionNearDistance) {
        this.lavaPotionNearDistance = lavaPotionNearDistance;
    }

    public float getLavaPotionNearDistance() {
        return lavaPotionNearDistance;
    }

    public void setLavaPotionFarDistance(float lavaPotionFarDistance) {
        this.lavaPotionFarDistance = lavaPotionFarDistance;
    }

    public float getLavaPotionFarDistance() {
        return lavaPotionFarDistance;
    }

    public boolean hasValidFogDistance() {
        return getNearDistance() < getFarDistance() && getNearDistance() != -1 && getFarDistance() != -1;
    }

    public boolean hasValidWaterFogDistance() {
        return getWaterNearDistance() < getWaterFarDistance() && getWaterNearDistance() != -1 && getWaterFarDistance() != -1;
    }

    public boolean hasValidWaterPotionFogDistance() {
        return getWaterPotionNearDistance() < getWaterPotionFarDistance() && getWaterPotionNearDistance() != -1 && getWaterPotionFarDistance() != -1;
    }

    public boolean hasValidLavaFogDistance() {
        return getLavaNearDistance() < getLavaFarDistance() && getWaterNearDistance() != -1 && getWaterFarDistance() != -1;
    }

    public boolean hasValidLavaPotionFogDistance() {
        return getLavaPotionNearDistance() < getLavaPotionFarDistance() && getLavaPotionNearDistance() != -1 && getLavaPotionFarDistance() != -1;
    }

}
