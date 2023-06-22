package com.fabbe50.fogoverrides.animations;

import com.fabbe50.fogoverrides.Util;
import net.minecraft.util.Mth;

import java.awt.*;

public class Transitions {
    public static class RGBAnimator {
        private final Color rgbFrom;
        private final Color rgbTo;
        private int currentFrame;
        private final int targetFrames;

        public RGBAnimator(int fromColor, int toColor, int frames) {
            this.currentFrame = 0;
            this.targetFrames = frames;
            this.rgbFrom = new Color(fromColor);
            this.rgbTo = new Color(toColor);
        }

        public int getColor() {
            double alpha = 1.0 * currentFrame / targetFrames;
            Color temp = combine(rgbTo, rgbFrom, alpha);
            currentFrame++;
            return temp.getRGB();
        }

        public static Color combine(Color c1, Color c2, double alpha) {
            int r = Mth.clamp((int) (alpha * c1.getRed()   + (1 - alpha) * c2.getRed()), 0, 255);
            int g = Mth.clamp((int) (alpha * c1.getGreen() + (1 - alpha) * c2.getGreen()), 0, 255);
            int b = Mth.clamp((int) (alpha * c1.getBlue()  + (1 - alpha) * c2.getBlue()), 0, 255);
            return new Color(r, g, b);
        }
    }
}
