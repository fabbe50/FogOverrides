package com.fabbe50.fogoverrides.holders;

import com.fabbe50.fogoverrides.holders.data.DefaultVariables;
import com.fabbe50.fogoverrides.holders.data.IHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public enum BlockEffects implements IHolder {
    FROSTBITE("frostbite", "", true, false, 1, 5, 0, 512, false, 0x000000, Blocks.POWDER_SNOW);

    private final DefaultVariables variables;
    private final Block block;
    BlockEffects(String effectName, String translationKey, boolean showOnConfig, boolean fogRemove, int fogStartDistance, int fogEndDistance, int minDistance, int maxDistance, boolean overrideColor, int color, Block block) {
        variables = new DefaultVariables(effectName, translationKey, showOnConfig, fogRemove, fogStartDistance, fogEndDistance, minDistance, maxDistance, overrideColor, color);
        this.block = block;
    }


    @Override
    public String getHolderType() {
        return "block-effects";
    }

    @Override
    public DefaultVariables getVariables() {
        return variables;
    }

    public Block getBlock() {
        return block;
    }
}
