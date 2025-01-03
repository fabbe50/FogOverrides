package com.fabbe50.fogoverrides.data;

public class FogSetting {
    private boolean isEnabled;
    private float nearDistance;
    private float farDistance;
    private int color;

    public FogSetting(boolean isEnabled, float nearDistance, float farDistance) {
        this(isEnabled, nearDistance, farDistance, 0xFFFFFF);
    }

    public FogSetting(boolean isEnabled, float nearDistance, float farDistance, int color) {
        this.isEnabled = isEnabled;
        this.nearDistance = nearDistance;
        this.farDistance = farDistance;
        this.color = color;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public float getNearDistance() {
        return nearDistance;
    }

    public void setNearDistance(float nearDistance) {
        this.nearDistance = nearDistance;
    }

    public float getFarDistance() {
        return farDistance;
    }

    public void setFarDistance(float farDistance) {
        this.farDistance = farDistance;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
