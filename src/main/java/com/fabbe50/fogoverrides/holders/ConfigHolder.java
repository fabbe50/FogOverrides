package com.fabbe50.fogoverrides.holders;

public class ConfigHolder {
    public static General general = new General();
    public static Special special = new Special();
    public static Void void_ = new Void();

    public static General getGeneral() {
        return general;
    }

    public static Special getSpecial() {
        return special;
    }

    public static Void getVoid_() {
        return void_;
    }

    public static class General {
        CreativeModeSettings creativeOverrides;
        boolean adjustDistance;
        boolean adjustColors;
        float cloudHeight;
        boolean adjustCloudColors;
        int cloudColorBlendRatio;

        public CreativeModeSettings getCreativeModeSettings() {
            return creativeOverrides;
        }

        public void setCreativeModeSettings(CreativeModeSettings creativeOverrides) {
            this.creativeOverrides = creativeOverrides;
        }

        public boolean isAdjustDistance() {
            return adjustDistance;
        }

        public void setAdjustDistance(boolean adjustDistance) {
            this.adjustDistance = adjustDistance;
        }

        public boolean isAdjustColors() {
            return adjustColors;
        }

        public void setAdjustColors(boolean adjustColors) {
            this.adjustColors = adjustColors;
        }

        public float getCloudHeight() {
            return cloudHeight;
        }

        public void setCloudHeight(float cloudHeight) {
            this.cloudHeight = cloudHeight;
        }

        public boolean isAdjustCloudColors() {
            return adjustCloudColors;
        }

        public void setAdjustCloudColors(boolean adjustCloudColors) {
            this.adjustCloudColors = adjustCloudColors;
        }

        public int getCloudColorBlendRatio() {
            return cloudColorBlendRatio;
        }

        public void setCloudColorBlendRatio(int cloudColorBlendRatio) {
            this.cloudColorBlendRatio = cloudColorBlendRatio;
        }

        public enum CreativeModeSettings {
            DISABLED(0),
            VANILLA(1),
            OVERRIDE(2);

            final int id;
            CreativeModeSettings(int id) {
                this.id = id;
            }

            public static CreativeModeSettings getFromID(int id) {
                for (CreativeModeSettings cms : CreativeModeSettings.values()) {
                    if (cms.id == id) {
                        return cms;
                    }
                }
                return OVERRIDE;
            }

            public int getId() {
                return id;
            }
        }
    }

    public static class Special {
        boolean potionAffectsVision;
        boolean disableFireOverlay;
        int fireOverlayOffset;
        boolean disableWaterOverlay;

        public boolean doesPotionAffectVision() {
            return potionAffectsVision;
        }

        public boolean isDisableFireOverlay() {
            return disableFireOverlay;
        }

        public int getFireOverlayOffset() {
            return fireOverlayOffset;
        }

        public boolean isDisableWaterOverlay() {
            return disableWaterOverlay;
        }

        public void setPotionAffectsVision(boolean potionAffectsVision) {
            this.potionAffectsVision = potionAffectsVision;
        }

        public void setDisableFireOverlay(boolean disableFireOverlay) {
            this.disableFireOverlay = disableFireOverlay;
        }

        public void setFireOverlayOffset(int fireOverlayOffset) {
            this.fireOverlayOffset = fireOverlayOffset;
        }

        public void setDisableWaterOverlay(boolean disableWaterOverlay) {
            this.disableWaterOverlay = disableWaterOverlay;
        }
    }

    public static class Void {
        boolean enableVoidFog;
        int voidFogStartDistance;
        int voidFogEndDistance;
        int yLevelActivate;
        boolean enableVoidParticles;
        boolean voidFogAffectedBySkylight;

        public boolean isEnableVoidFog() {
            return enableVoidFog;
        }

        public int getVoidFogStartDistance() {
            return voidFogStartDistance;
        }

        public int getVoidFogEndDistance() {
            return voidFogEndDistance;
        }

        public int getyLevelActivate() {
            return yLevelActivate;
        }

        public boolean isEnableVoidParticles() {
            return enableVoidParticles;
        }

        public boolean isVoidFogAffectedBySkylight() {
            return voidFogAffectedBySkylight;
        }

        public void setEnableVoidFog(boolean enableVoidFog) {
            this.enableVoidFog = enableVoidFog;
        }

        public void setVoidFogStartDistance(int voidFogStartDistance) {
            this.voidFogStartDistance = voidFogStartDistance;
        }

        public void setVoidFogEndDistance(int voidFogEndDistance) {
            this.voidFogEndDistance = voidFogEndDistance;
        }

        public void setyLevelActivate(int yLevelActivate) {
            this.yLevelActivate = yLevelActivate;
        }

        public void setEnableVoidParticles(boolean enableVoidParticles) {
            this.enableVoidParticles = enableVoidParticles;
        }

        public void setVoidFogAffectedBySkylight(boolean voidFogAffectedBySkylight) {
            this.voidFogAffectedBySkylight = voidFogAffectedBySkylight;
        }
    }
}