package com.fabbe50.fogoverrides.holders;

import com.fabbe50.fogoverrides.holders.data.DefaultVariables;
import com.fabbe50.fogoverrides.holders.data.IOverrideHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

public enum Liquids implements IOverrideHolder {
    WATER("water", "", true, true, 40, 80, -8, 32, 1, 500, false, 0x0000FF, 50, Fluids.WATER, MobEffects.WATER_BREATHING),
    LAVA("lava", "", true, true, 10, 20, 0, 1, 1, 500, false, 0xFF6600, 80, Fluids.LAVA, MobEffects.FIRE_RESISTANCE),
    EMPTY("empty", "", false, false, 10, 20, -1, -1, 1, 500, false, 0xFFFFFF, 0, Fluids.EMPTY, null);

    private final DefaultVariables variables;
    private final Fluid fluid;
    private final MobEffect mobEffect;
    private final int defaultFogStartDistance;
    private final int defaultFogEndDistance;

    Liquids(String liquidName, String translationKey, boolean showOnConfig, boolean enabled, int fogStartDistance, int fogEndDistance, int defaultFogStartDistance, int defaultFogEndDistance, int minDistance, int maxDistance, boolean overrideColor, int color, int percentage, Fluid fluid, @Nullable MobEffect mobEffect) {
        variables = new DefaultVariables(liquidName, translationKey, showOnConfig, enabled, fogStartDistance, fogEndDistance, minDistance, maxDistance, overrideColor, color, percentage);
        this.fluid = fluid;
        this.mobEffect = mobEffect;
        this.defaultFogStartDistance = defaultFogStartDistance;
        this.defaultFogEndDistance = defaultFogEndDistance;
    }

    @Override
    public String getHolderType() {
        return "liquids";
    }

    @Override
    public DefaultVariables getVariables() {
        return variables;
    }

    public FluidType getFluidType() {
        return fluid.getFluidType();
    }

    public MobEffect getMobEffect() {
        return mobEffect;
    }

    public int getDefaultFogStartDistance() {
        return defaultFogStartDistance;
    }

    public int getDefaultFogEndDistance() {
        return defaultFogEndDistance;
    }
}
