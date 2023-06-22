package com.fabbe50.fogoverrides.holders.data;

public interface IOverrideHolder extends IHolder {
    default int getBlendPercentage() {
        return getVariables().getBlendPercentage();
    }

    default void setBlendPercentage(int blendPercentage) {
        getVariables().setBlendPercentage(blendPercentage);
    }

    default int getDefaultBlendPercentage() {
        return getVariables().getDefaultBlendPercentage();
    }
}
