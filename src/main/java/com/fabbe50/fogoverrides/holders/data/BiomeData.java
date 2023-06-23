package com.fabbe50.fogoverrides.holders.data;

import net.minecraft.resources.ResourceLocation;

public class BiomeData implements IHolder {
    private final DefaultVariables variables;

    public BiomeData(String name) {
        variables = new DefaultVariables(
                name,
                "biome." + name.replace(":", "."),
                true,
                true,
                80,
                100,
                0,
                512,
                false,
                0xFFFFFF
        );
    }

    public void updateSettings(boolean enabled, int startDistance, int endDistance, boolean overrideColor, int color) {
        variables.setEnabled(enabled);
        variables.setStartDistance(startDistance);
        variables.setEndDistance(endDistance);
        variables.setOverrideColor(overrideColor);
        variables.setColor(color);
    }

    @Override
    public String getHolderType() {
        return "biomes";
    }

    @Override
    public DefaultVariables getVariables() {
        return variables;
    }

    @Override
    public String getName() {
        String name = IHolder.super.getName();
        if (name.contains(":")) {
            return name.split(":")[1];
        }
        return name;
    }

    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IHolder.super.getName());
    }
}
