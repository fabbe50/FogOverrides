package com.fabbe50.fogoverrides;

import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.FogSetting;
import com.fabbe50.fogoverrides.data.GameModeSettings;
import com.fabbe50.fogoverrides.data.ModFogData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

import java.io.FileOutputStream;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static com.fabbe50.fogoverrides.ModConfig.*;

public class Utilities {
    private static final ResourceLocation OVERWORLD = Level.OVERWORLD.location();
    private static final ResourceLocation THE_NETHER = Level.NETHER.location();
    private static final ResourceLocation THE_END = Level.END.location();


    public static ResourceLocation getOverworld() {
        return Objects.requireNonNullElseGet(OVERWORLD, () -> new ResourceLocation("overworld"));
    }

    public static ResourceLocation getNether() {
        return Objects.requireNonNullElseGet(THE_NETHER, () -> new ResourceLocation("the_nether"));
    }

    public static ResourceLocation getTheEnd() {
        return Objects.requireNonNullElseGet(THE_END, () -> new ResourceLocation("the_end"));
    }

    public static int getColorIntegerFromRGB(int[] rgb) {
        return getColorIntegerFromRGB(rgb[0], rgb[1], rgb[2]);
    }

    public static int getColorIntegerFromVec3(Vec3 vec3) {
        return getColorIntegerFromRGB(vec3.x, vec3.y, vec3.z);
    }

    public static int getColorIntegerFromVec4F(Vector4f color) {
        return getColorIntegerFromRGB(color.x, color.y, color.z);
    }

    public static Color getColorFromColorInt(int color) {
        int[] rgb = getRGBFromColorInteger(color);
        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    public static int getColorIntegerFromRGB(double r, double g, double b) {
        return getColorIntegerFromRGB((int)r, (int)g, (int)b);
    }

    public static int getColorIntegerFromRGB(float r, float g, float b) {
        return getColorIntegerFromRGB((int)r, (int)g, (int)b);
    }

    public static int getColorIntNoAlpha(int color) {
        return getColorIntNoAlphaFromColor(getColorFromColorInt(color));
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

    public static Vector4f getVector4fColorFromInteger(int color) {
        int[] rgb = getRGBFromColorInteger(color);
        return new Vector4f(rgb[0] / 255f, rgb[1] / 255f, rgb[2] / 255f, 1);
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

    public static float getReversedBetweenDistanceByRatio(float fromValue, float toValue, float ratio) {
        float reversedRatio = 1 - ratio;
        return getBetweenDistanceByRatio(fromValue, toValue, reversedRatio);
    }

    public static float getBetweenDistanceByRatio(float fromValue, float toValue, float ratio) {
        float minRatio = 1 - ratio;
        return (fromValue * minRatio) + (toValue * ratio);
    }

    public static ModFogData getDefaultFogData() {
        return new ModFogData(-1, 0, 7907327, 12638463, -8.0f, 96.0f, 4159204, 329011);
    }

    public static GameModeSettings getDefaultGameModeSettings() {
        return new GameModeSettings();
    }

    public static FogSetting getDefaultTerrain() {
        return new FogSetting(true, -1, 0, 0xFFFFFF);
    }

    public static FogSetting getDefaultTerrainDisabled() {
        return new FogSetting(false, -1, 0, 0xFFFFFF);
    }

    public static FogSetting getDefaultWater() {
        return new FogSetting(true, UNDERWATER_NEAR_DEFAULT, UNDERWATER_FAR_DEFAULT, 0xFFFFFF);
    }

    public static FogSetting getDefaultLava() {
        return new FogSetting(true, LAVA_NEAR_DEFAULT, LAVA_FAR_DEFAULT, 0xFFFFFF);
    }

    public static void writeData(FileOutputStream fos, String key, String value) throws IOException {
        fos.write((key + "=" + value).getBytes());
        fos.write("\n".getBytes());
    }

    public static String capitalizeFirstInEveryWord(String input) {
        String[] words = input.split("\\s");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            result.append(Character.toTitleCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }
        return result.toString().trim();
    }
}
