package com.fabbe50.fogoverrides.handlers;

import com.fabbe50.fogoverrides.FogOverrides;
import com.fabbe50.fogoverrides.Util;
import com.fabbe50.fogoverrides.holders.ConfigHolder;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class DebugHandler {
    public final DebugInfo debugInfo = new DebugInfo();

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void debugHandler(CustomizeGuiOverlayEvent.DebugText event) {
        if (Minecraft.getInstance().options.renderDebug) {
            event.getLeft().addAll(debugInfo.getDebugTexts());
        }
    }

    public class DebugInfo {
        private ColorInfo colorInfo = new ColorInfo();
        private DistanceInfo distanceInfo = new DistanceInfo();
        private ParticleInfo particleInfo = new ParticleInfo();

        public List<String> getDebugTexts() {
            List<String> debugTexts = new ArrayList<>();
            debugTexts.add("");
            debugTexts.add("Fog Overrides");
            debugTexts.add(getString());
            debugTexts.addAll(distanceInfo.getTexts());
            debugTexts.addAll(colorInfo.getTexts());
            return debugTexts;
        }

        public String getFogType() {
            if (ConfigHolder.getGeneral().isAdjustDistance() || ConfigHolder.getGeneral().isAdjustColors()) {
                return FogOverrides.data.getCurrentFogType();
            }
            return "MOD DISABLED";
        }

        public String getString() {
            return "Fog Type: " + getFogType();
        }

        private class ColorInfo {
            private boolean getActive() {
                return FogHandler.isColorActive();
            }

            private boolean getColorAdjusting() {
                return FogHandler.isColorAdjusting();
            }

            private boolean getTargetGame() {
                return FogHandler.isTargetGame();
            }

            private int getTargetColor() {
                return FogOverrides.data.getTargetColor();
            }

            private int getCurrentColor() {
                return FogOverrides.data.getCurrentColor();
            }

            private int getBaseColor() {
                return FogOverrides.data.getTargetBaseColor();
            }

            private int getBlendColor() {
                return FogOverrides.data.getTargetBlendColor();
            }

            private float getColorBlendPercentage() {
                return FogOverrides.data.getTargetBlendPercentage();
            }

            private String getCurrentColorStatus() {
                return (getColorAdjusting() ? "ANIMATING: " + (getTargetGame() ? "GAME-" : "MOD-") + "FOG" : "IDLE: " + (getTargetGame() ? "GAME-" : "MOD-") + "FOG");
            }

            public List<String> getTexts() {
                List<String> texts = new ArrayList<>();
                if (ConfigHolder.getGeneral().isAdjustColors()) {
                    texts.add("Fog Color Builder: " + (getActive() ? "ACTIVE" : "INACTIVE"));
                    texts.add("Fog Color Blend: #: " + Util.getFormattedColor(getBaseColor()) + ", &: " + Util.getFormattedColor(getBlendColor()) + ", R: " + getColorBlendPercentage());
                    texts.add("Fog Color: C: " + Util.getFormattedColor(getCurrentColor()) + ", T: " + Util.getFormattedColor(getTargetColor()) + ", S: " + getCurrentColorStatus());
                    texts.add("Cloud Color Adjust: " + (ConfigHolder.getGeneral().isAdjustCloudColors() ? "TRUE" : "FALSE"));
                } else {
                    texts.add("Fog Color Builder: DISABLED");
                }
                return texts;
            }
        }

        private class DistanceInfo {
            private boolean getActive() {
                return FogHandler.isFogActive();
            }
            private int getStartingTarget() {
                return FogOverrides.data.getTargetStartDistance();
            }

            private String getStartingCurrent() {
                double start = Util.getRoundedTwoDecimalPoints(FogOverrides.data.getCurrentStartDistance());
                return start > 1000 ? String.valueOf((int) start) : start % .1 == 0 ? start + "0" : String.valueOf(start);
            }

            private int getEndingTarget() {
                return FogOverrides.data.getTargetEndDistance();
            }

            private String getEndingCurrent() {
                double end = Util.getRoundedTwoDecimalPoints(FogOverrides.data.getCurrentEndDistance());
                return end > 1000 ? String.valueOf((int) end) : end % .1 == 0 ? end + "0" : String.valueOf(end);
            }

            private DIRECTION getCurrentStartStatus() {
                if (FogHandler.isStartAnimating()) {
                    if (FogHandler.isStartIncreasing()) {
                        return DIRECTION.INCREASING;
                    }
                    return DIRECTION.DECREASING;
                }
                return DIRECTION.IDLE;
            }

            private DIRECTION getCurrentEndStatus() {
                if (FogHandler.isEndAnimating()) {
                    if (FogHandler.isEndIncreasing()) {
                        return DIRECTION.INCREASING;
                    }
                    return DIRECTION.DECREASING;
                }
                return DIRECTION.IDLE;
            }

            public List<String> getTexts() {
                List<String> texts = new ArrayList<>();
                if (ConfigHolder.getGeneral().isAdjustDistance()) {
                    texts.add("Fog Distance Builder: " + (getActive() ? "ACTIVE" : "IDLE"));
                    texts.add("Starting Fog: " + "C: " + getStartingCurrent() + ", T: " + getStartingTarget() + ", S: " + getCurrentStartStatus().getLabel());
                    texts.add("Ending Fog: " + "C: " + getEndingCurrent() + ", T: " + getEndingTarget() + ", S: " + getCurrentEndStatus().getLabel());
                } else {
                    texts.add("Fog Distance Builder: DISABLED");
                }
                return texts;
            }

            private enum DIRECTION {
                INCREASING("INCREASING"),
                DECREASING("DECREASING"),
                IDLE("IDLE");

                private final String label;

                DIRECTION(String label) {
                    this.label = label;
                }

                public String getLabel() {
                    return label;
                }
            }
        }

        private class ParticleInfo {

        }
    }
}
