package com.fabbe50.fogoverrides.data;

import net.minecraft.network.FriendlyByteBuf;

public class ModFogData {
    private boolean overrideGameFog;

    private boolean overrideFogColor;
    private FogSetting terrain;
    private FogSetting rain;
    private boolean overrideSkyColor;
    private int skyColor;

    private boolean overrideWaterFogColor;
    private FogSetting water;
    private FogSetting waterPotion;
    private boolean overrideWaterColor;
    private int waterColor;

    private FogSetting lava;
    private FogSetting lavaPotion;

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

        this.overrideFogColor = overrideFogColor;
        terrain = new FogSetting(isFogEnabled, nearDistance, farDistance, fogColor);
        rain = new FogSetting(false, nearDistance, farDistance, 0xFFFFFF);
        this.overrideSkyColor = overrideSkyColor;
        this.skyColor = skyColor;

        this.overrideWaterFogColor = overrideWaterFogColor;
        water = new FogSetting(overrideWaterFog, waterNearDistance, waterFarDistance, waterFogColor);
        waterPotion = new FogSetting(false, waterNearDistance, waterFarDistance);
        this.overrideWaterColor = overrideWaterColor;
        this.waterColor = waterColor;

        lava = new FogSetting(false, 0.25f, 1);
        lavaPotion = new FogSetting(false, 0, 3);
    }

    public void setOverrideGameFog(boolean overrideGameFog) {
        this.overrideGameFog = overrideGameFog;
    }

    public boolean isOverrideGameFog() {
        return overrideGameFog;
    }

    public void setTerrain(FogSetting terrain) {
        this.terrain = terrain;
    }

    public FogSetting getTerrain() {
        return terrain;
    }

    public void setRain(FogSetting rain) {
        this.rain = rain;
    }

    public FogSetting getRain() {
        return rain;
    }

    public void setWater(FogSetting water) {
        this.water = water;
    }

    public FogSetting getWater() {
        return water;
    }

    public void setWaterPotion(FogSetting waterPotion) {
        this.waterPotion = waterPotion;
    }

    public FogSetting getWaterPotion() {
        return waterPotion;
    }

    public void setLava(FogSetting lava) {
        this.lava = lava;
    }

    public FogSetting getLava() {
        return lava;
    }

    public void setLavaPotion(FogSetting lavaPotion) {
        this.lavaPotion = lavaPotion;
    }

    public FogSetting getLavaPotion() {
        return lavaPotion;
    }

    public void setFogEnabled(boolean fogEnabled) {
        terrain.setEnabled(fogEnabled);
    }

    public boolean isFogEnabled() {
        return terrain.isEnabled();
    }

    public void setNearDistance(float nearDistance) {
        terrain.setNearDistance(nearDistance);
    }

    public float getNearDistance() {
        return terrain.getNearDistance();
    }

    public void setFarDistance(float farDistance) {
        terrain.setFarDistance(farDistance);
    }

    public float getFarDistance() {
        return terrain.getFarDistance();
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
        terrain.setColor(fogColor);
    }

    public int getFogColor() {
        return terrain.getColor();
    }

    public void setOverrideWaterFog(boolean overrideWaterFog) {
        water.setEnabled(overrideWaterFog);
    }

    public boolean isOverrideWaterFog() {
        return water.isEnabled();
    }

    public void setWaterNearDistance(float waterNearDistance) {
        water.setNearDistance(waterNearDistance);
    }

    public float getWaterNearDistance() {
        return water.getNearDistance();
    }

    public void setWaterFarDistance(float waterFarDistance) {
        water.setFarDistance(waterFarDistance);
    }

    public float getWaterFarDistance() {
        return water.getFarDistance();
    }

    public void setWaterPotionEffect(boolean waterPotionEffect) {
        waterPotion.setEnabled(waterPotionEffect);
    }

    public boolean isWaterPotionEffect() {
        return waterPotion.isEnabled();
    }

    public void setWaterPotionNearDistance(float waterPotionNearDistance) {
        waterPotion.setNearDistance(waterPotionNearDistance);
    }

    public float getWaterPotionNearDistance() {
        return waterPotion.getNearDistance();
    }

    public void setWaterPotionFarDistance(float waterPotionFarDistance) {
        waterPotion.setFarDistance(waterPotionFarDistance);
    }

    public float getWaterPotionFarDistance() {
        return waterPotion.getFarDistance();
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
        water.setColor(waterFogColor);
    }

    public int getWaterFogColor() {
        return water.getColor();
    }

    public void setOverrideLavaFog(boolean overrideLavaFog) {
        lava.setEnabled(overrideLavaFog);
    }

    public boolean isOverrideLavaFog() {
        return lava.isEnabled();
    }

    public void setLavaNearDistance(float lavaNearDistance) {
        lava.setNearDistance(lavaNearDistance);
    }

    public float getLavaNearDistance() {
        return lava.getNearDistance();
    }

    public void setLavaFarDistance(float lavaFarDistance) {
        lava.setFarDistance(lavaFarDistance);
    }

    public float getLavaFarDistance() {
        return lava.getFarDistance();
    }

    public void setLavaPotionEffect(boolean lavaPotionEffect) {
        lavaPotion.setEnabled(lavaPotionEffect);
    }

    public boolean isLavaPotionEffect() {
        return lavaPotion.isEnabled();
    }

    public void setLavaPotionNearDistance(float lavaPotionNearDistance) {
        lavaPotion.setNearDistance(lavaPotionNearDistance);
    }

    public float getLavaPotionNearDistance() {
        return lavaPotion.getNearDistance();
    }

    public void setLavaPotionFarDistance(float lavaPotionFarDistance) {
        lavaPotion.setFarDistance(lavaPotionFarDistance);
    }

    public float getLavaPotionFarDistance() {
        return lavaPotion.getFarDistance();
    }

    public FriendlyByteBuf writeBuffer(FriendlyByteBuf buf) {
        buf.writeBoolean(isOverrideGameFog());
        writeFogSettings(buf, getTerrain());
        buf.writeBoolean(isOverrideSkyColor());
        buf.writeInt(getSkyColor());
        buf.writeBoolean(isOverrideFogColor());
        buf.writeInt(getFogColor());
        writeFogSettings(buf, getWater());
        writeFogSettings(buf, getWaterPotion());
        buf.writeBoolean(isOverrideWaterColor());
        buf.writeInt(getWaterColor());
        buf.writeBoolean(isOverrideWaterFogColor());
        buf.writeInt(getWaterFogColor());
        writeFogSettings(buf, getLava());
        writeFogSettings(buf, getLavaPotion());
        writeFogSettings(buf, getRain());
        buf.writeInt(getRain().getColor());
        return buf;
    }

    private void writeFogSettings(FriendlyByteBuf buf, FogSetting fogSetting) {
        buf.writeBoolean(fogSetting.isEnabled());
        buf.writeFloat(fogSetting.getNearDistance());
        buf.writeFloat(fogSetting.getFarDistance());
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
