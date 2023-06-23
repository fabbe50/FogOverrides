package com.fabbe50.fogoverrides.holders;

import com.fabbe50.fogoverrides.holders.data.DefaultVariables;
import com.fabbe50.fogoverrides.holders.data.IHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public enum PotionEffects implements IHolder {
    BLINDNESS("blindness", "", true, true, 2, 4, 0,  512, false, 0x000000, MobEffects.BLINDNESS),
    DARKNESS("darkness", "", true, true, 11, 15, 0,  512, false, 0x000000, MobEffects.DARKNESS);

    private final DefaultVariables variables;
    private final MobEffect mobEffect;
    PotionEffects(String effectName, String translationKey, boolean showOnConfig, boolean fogEnabled, int fogStartDistance, int fogEndDistance, int minDistance, int maxDistance, boolean overrideColor, int color, MobEffect mobEffect) {
        variables = new DefaultVariables(effectName, translationKey, showOnConfig, fogEnabled, fogStartDistance, fogEndDistance, minDistance, maxDistance, overrideColor, color);
        this.mobEffect = mobEffect;
    }

    @Override
    public String getHolderType() {
        return "potion-effects";
    }

    @Override
    public DefaultVariables getVariables() {
        return variables;
    }

    public MobEffect getMobEffect() {
        return mobEffect;
    }
}
