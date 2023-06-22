package com.fabbe50.fogoverrides.holders.data;

public class DefaultVariables {
    private final String name;
    private final String translationKey;
    private final boolean showOnConfig;
    private boolean enabled;
    private int startDistance;
    private int endDistance;
    private final int defaultStartDistance;
    private final int defaultEndDistance;
    private final int minDistance;
    private final int maxDistance;
    private boolean overrideColor;
    private final boolean defaultOverrideColor;
    private int color;
    private final int defaultColor;
    private int blendPercentage;
    private final int defaultBlendPercentage;


    public DefaultVariables(String name, String translationKey, boolean showOnConfig, boolean enabled, int defaultStartDistance, int defaultEndDistance, int minDistance, int maxDistance, boolean overrideColor, int defaultColor) {
        this(name, translationKey, showOnConfig, enabled, defaultStartDistance, defaultEndDistance, minDistance, maxDistance, overrideColor, defaultColor, 0);
    }

    public DefaultVariables(String name, String translationKey, boolean showOnConfig, boolean enabled, int defaultStartDistance, int defaultEndDistance, int minDistance, int maxDistance, boolean overrideColor, int defaultColor, int defaultBlendPercentage) {
        this.name = name;
        this.translationKey = translationKey;
        this.showOnConfig = showOnConfig;
        this.enabled = enabled;
        this.startDistance = defaultStartDistance;
        this.endDistance = defaultEndDistance;
        this.defaultStartDistance = defaultStartDistance;
        this.defaultEndDistance = defaultEndDistance;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.overrideColor = overrideColor;
        this.defaultOverrideColor = overrideColor;
        this.color = defaultColor;
        this.defaultColor = defaultColor;
        this.blendPercentage = defaultBlendPercentage;
        this.defaultBlendPercentage = defaultBlendPercentage;
    }

    public String getName() {
        return name;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public boolean showOnConfig() {
        return showOnConfig;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getStartDistance() {
        return startDistance;
    }

    public void setStartDistance(int startDistance) {
        this.startDistance = startDistance;
    }

    public int getEndDistance() {
        return endDistance;
    }

    public void setEndDistance(int endDistance) {
        this.endDistance = endDistance;
    }

    public int getDefaultStartDistance() {
        return defaultStartDistance;
    }

    public int getDefaultEndDistance() {
        return defaultEndDistance;
    }

    public int getMinDistance() {
        return minDistance;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public boolean shouldOverrideColor() {
        return overrideColor;
    }

    public void setOverrideColor(boolean overrideColor) {
        this.overrideColor = overrideColor;
    }

    public boolean getDefaultShouldOverrideColor() {
        return defaultOverrideColor;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    public int getBlendPercentage() {
        return blendPercentage;
    }

    public void setBlendPercentage(int blendPercentage) {
        this.blendPercentage = blendPercentage;
    }

    public int getDefaultBlendPercentage() {
        return defaultBlendPercentage;
    }
}
