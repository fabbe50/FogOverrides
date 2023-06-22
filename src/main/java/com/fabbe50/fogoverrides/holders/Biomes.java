package com.fabbe50.fogoverrides.holders;

import com.fabbe50.fogoverrides.holders.data.DefaultVariables;
import com.fabbe50.fogoverrides.holders.data.IHolder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.Tags;

public enum Biomes implements IHolder {
    OCEAN("ocean", "biome.minecraft.ocean", true, false, 80, 100, 1, 500, false, 0xFFFFFF, BiomeTags.IS_OCEAN)/*,
    DEEP_OCEAN("deep_ocean", "biome.minecraft.deep_ocean", true, false, 80, 100, 1, 500, false, 0xFFF, BiomeTags.IS_DEEP_OCEAN),
    BEACH("beach", "biome.minecraft.beach", true, false, 80, 100, 1, 500, false, 0xFFFFFF, BiomeTags.IS_BEACH),
    RIVER("river", "biome.minecraft.river", true, false, 80, 100, 1, 500, false, 0xFFFFFF, BiomeTags.IS_RIVER),
    MOUNTAIN("mountain", "", true, false, 80, 100, 1, 500, false, 0xFFFFFF, BiomeTags.IS_MOUNTAIN),
    BADLANDS("badlands", "biome.minecraft.badlands", true, false, 80, 100, 1, 500, false, 0xFFFFFF, BiomeTags.IS_BADLANDS),
    HILL("hill", "", true, false, 80, 100, 1, 500, false, 0xFFFFFF, BiomeTags.IS_HILL),
    TAIGA("taiga", "biome.minecraft.taiga", true, false, 80, 100, 1, 500, false, 0xFFFFFF, BiomeTags.IS_TAIGA),
    JUNGLE("jungle", "biome.minecraft.jungle", true, false, 80, 100, 1, 500,false, 0xFFFFFF, BiomeTags.IS_JUNGLE),
    FOREST("forest", "biome.minecraft.forest", true, false, 80, 100, 1, 500, false, 0xFFFFFF, BiomeTags.IS_FOREST),
    SAVANNA("savanna", "biome.minecraft.savanna", true, false, 80, 100, 1, 500, false, 0xFFFFFF, BiomeTags.IS_SAVANNA),
    DESERT("desert", "biome.minecraft.desert", true, false, 80, 100, 1, 500, false, 0xFFFFFF, Tags.Biomes.IS_DESERT),
    PLAINS("plains", "biome.minecraft.plains", true, false, 80, 100, 1, 500, false, 0xFFFFFF, Tags.Biomes.IS_PLAINS),
    SWAMP("swamp", "biome.minecraft.swamp", true, false, 80, 100, 1, 500, false, 0xFFFFFF, Tags.Biomes.IS_SWAMP),
    VOID("void", "", true, false, 80, 100, 1, 500, false, 0xFFFFFF, Tags.Biomes.IS_VOID),
    UNDERGROUND("underground", "", true, false, 80, 100, 1, 500, false, 0xFFFFFF, Tags.Biomes.IS_UNDERGROUND)*/;


    private final TagKey<Biome>[] biomesTags;

    private final DefaultVariables variables;
    @SafeVarargs
    Biomes(String biomeName, String translationKey, boolean showOnConfig, boolean overrideDimension, int fogStartDistance, int fogEndDistance, int minDistance, int maxDistance, boolean overrideColor, int fogColor, TagKey<Biome>... biomesTags) {
        variables = new DefaultVariables(biomeName, translationKey, showOnConfig, overrideDimension, fogStartDistance, fogEndDistance, minDistance, maxDistance, overrideColor, fogColor);
        this.biomesTags = biomesTags;
    }

    @Override
    public String getHolderType() {
        return "biomes";
    }

    @Override
    public DefaultVariables getVariables() {
        return variables;
    }

    public TagKey<Biome>[] getBiomesTags() {
        return this.biomesTags;
    }
}
