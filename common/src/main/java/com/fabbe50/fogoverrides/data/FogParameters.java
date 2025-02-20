package com.fabbe50.fogoverrides.data;

import com.mojang.blaze3d.shaders.FogShape;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record FogParameters(float start, float end, FogShape shape, float red, float green, float blue, float alpha) {
    public static final FogParameters NO_FOG;

    public float start() {
        return this.start;
    }

    public float end() {
        return this.end;
    }

    public FogShape shape() {
        return this.shape;
    }

    public float red() {
        return this.red;
    }

    public float green() {
        return this.green;
    }

    public float blue() {
        return this.blue;
    }

    public float alpha() {
        return this.alpha;
    }

    static {
        NO_FOG = new FogParameters(Float.MAX_VALUE, 0.0F, FogShape.SPHERE, 0.0F, 0.0F, 0.0F, 0.0F);
    }
}
