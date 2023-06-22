package com.fabbe50.fogoverrides.holders;

import com.fabbe50.fogoverrides.holders.data.DefaultVariables;
import com.fabbe50.fogoverrides.holders.data.IHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;

public enum Dimensions implements IHolder {
    OVERWORLD("overworld", "", true, false, 80, 100, 1, 500, false, 0xFFFFFF, BuiltinDimensionTypes.OVERWORLD),
    NETHER("nether", "",true, false, 32, 64, 1, 500, false, 0xFFFFFF, BuiltinDimensionTypes.NETHER),
    THE_END("the_end", "",true, false, 32,64, 1, 500, false, 0xFFFFFF, BuiltinDimensionTypes.END);

    private final DefaultVariables defaultVariables;
    private final ResourceKey<DimensionType> resourceKey;

    Dimensions(String dimensionName, String translationKey, boolean showOnConfig, boolean fogRemove, int fogStartDistance, int fogEndDistance, int minDistance, int maxDistance, boolean overrideColor, int color, ResourceKey<DimensionType> resourceKey) {
        defaultVariables = new DefaultVariables(dimensionName, translationKey, showOnConfig, fogRemove, fogStartDistance, fogEndDistance, minDistance, maxDistance, overrideColor, color);
        this.resourceKey = resourceKey;
    }

    @Override
    public String getHolderType() {
        return "dimensions";
    }

    @Override
    public DefaultVariables getVariables() {
        return defaultVariables;
    }

    public ResourceKey<DimensionType> getResourceKey() {
        return this.resourceKey;
    }
}
