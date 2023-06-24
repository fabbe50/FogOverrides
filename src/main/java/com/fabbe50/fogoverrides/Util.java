package com.fabbe50.fogoverrides;

import com.fabbe50.fogoverrides.holders.Biomes;
import com.fabbe50.fogoverrides.holders.ConfigHolder;
import com.fabbe50.fogoverrides.holders.Liquids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ViewportEvent;

import java.awt.*;

public class Util {
    public static boolean checkBlockConditions(Player player, Level level, Block block) {
        if (block instanceof LiquidBlock)
            return false;
        Vec3 vec3 = player.getEyePosition();
        double x = vec3.x;
        double y = vec3.y;
        double z = vec3.z;
        return level.getBlockState(new BlockPos(new Vec3i((int) x, (int) y, (int) z))).is(block);
    }

    public static boolean checkFluidConditions(Player player) {
        for (Liquids liquid : Liquids.values()) {
            if (liquid.isEnabled()) {
                if (checkFluidConditions(player, liquid)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkFluidConditions(Player player, Liquids liquids) {
        if (ConfigHolder.getSpecial().doesPotionAffectVision()) {
            return player.isEyeInFluidType(liquids.getFluidType()) && doesPlayerHavePotionEffect(player, liquids);
        }
        return player.isEyeInFluidType(liquids.getFluidType());
    }

    public static boolean areEyesInFluid(Level level, Player player) {
        return (!level.getBlockState(player.getOnPos().above().above()).is(Blocks.AIR) || (!(player.getEyePosition().y < player.getOnPos().above().above().getY() + 0.1F) || !(player.getEyePosition().y > player.getOnPos().above().getY() + 0.8888889F)));
    }

    public static boolean doesPlayerHavePotionEffect(Player player, Liquids liquids) {
        if (liquids.getMobEffect() != null) {
            return player.hasEffect(liquids.getMobEffect());
        }
        return true;
    }

    public static boolean checkDimensionConditions(Player player, ResourceKey<DimensionType> dimension) {
        return checkDimensionConditions(player, dimension.location());
    }

    public static boolean checkDimensionConditions(Player player, ResourceLocation dimension) {
        return player.level().dimensionTypeId().location() == dimension;
    }

    public static boolean checkBiomeConditions(Player player, Biomes biome) {
        for (TagKey<Biome> tagKey : biome.getBiomesTags()) {
            if (player.level().getBiome(player.getOnPos()).is(tagKey)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkBiomeConditions(Player player, TagKey<Biome> biome) {
        return player.level().getBiome(player.getOnPos()).is(biome);
    }

    public static boolean checkBiomeConditions(Player player, ResourceKey<Biome> biome) {
        return player.level().getBiome(player.getOnPos()).is(biome);
    }

    public static boolean checkBiomeConditions(Player player, ResourceLocation resourceLocation) {
        return player.level().getBiome(player.getOnPos()).is(resourceLocation);
    }

    public static void setMaxDensity(ViewportEvent.RenderFog event) {
        setDensity(event, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public static void setDensity(ViewportEvent.RenderFog event, int near, int far) {
        setDensity(event, (float)near, (float)far);
    }

    public static void setDensity(ViewportEvent.RenderFog event, float near, float far) {
        if (near == far)
            near--;
        event.setNearPlaneDistance(near);
        event.setFarPlaneDistance(far);
    }

    public static int getColorIntegerFromRGB(int[] rgb) {
        return getColorIntegerFromRGB(rgb[0], rgb[1], rgb[2]);
    }

    public static int getColorIntegerFromVec3(Vec3 vec3) {
        return getColorIntegerFromRGB(vec3.x, vec3.y, vec3.z);
    }

    public static int getColorIntegerFromRGB(double r, double g, double b) {
        return getColorIntegerFromRGB((int)r, (int)g, (int)b);
    }

    public static int getColorIntegerFromRGB(float r, float g, float b) {
        return getColorIntegerFromRGB((int)r, (int)g, (int)b);
    }

    public static int getColorIntNoAlphaFromColor(Color color) {
        return getColorIntegerFromRGB(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static int getColorIntegerFromRGB(int r, int g, int b) {
        return (r << 16) + (g << 8) + b;
    }

    public static int[] getRGBFromColorInteger(int color) {
        int r = ((color >> 16) & 255);
        int g = ((color >> 8) & 255);
        int b = (color & 255);
        return new int[] {r, g, b};
    }

    public static Vec3 getVec3ColorFromInteger(int color) {
        int[] rgb = getRGBFromColorInteger(color);
        return new Vec3(rgb[0] / 255D, rgb[1] / 255D, rgb[2] / 255D);
    }

    public static String getFormattedColor(int r, int g, int b) {
        return getFormattedColor(getColorIntegerFromRGB(r, g, b));
    }

    public static String getFormattedColor(int[] color) {
        return getFormattedColor(getColorIntegerFromRGB(color));
    }

    public static String getFormattedColor(int color) {
        int[] rgb = getRGBFromColorInteger(color);

        return color == -1 ? "{null}" : String.format("{R: %s, G: %s, B: %s}", rgb[0], rgb[1], rgb[2]);
    }

    public static int getBlendedColor(int originalColor, int blendColor, float blendingRatio) {
        if (blendColor != -1) {
            if (blendingRatio > 1f) {
                blendingRatio = 1f;
            } else if (blendingRatio < 0f) {
                blendingRatio = 0f;
            }
            float iRatio = 1.0f - blendingRatio;

            int oR = ((originalColor & 0xff0000) >> 16);
            int oG = ((originalColor & 0xff00) >> 8);
            int oB = (originalColor & 0xff);

            int tR = ((blendColor & 0xff0000) >> 16);
            int tG = ((blendColor & 0xff00) >> 8);
            int tB = (blendColor & 0xff);

            int newR = (int) ((oR * iRatio) + (tR * blendingRatio));
            int newG = (int) ((oG * iRatio) + (tG * blendingRatio));
            int newB = (int) ((oB * iRatio) + (tB * blendingRatio));

            return newR << 16 | newG << 8 | newB;
        }
        return originalColor;
    }

    public static double getRoundedTwoDecimalPoints(float a) {
        return Math.round(a * 100.0) / 100.0;
    }
}
