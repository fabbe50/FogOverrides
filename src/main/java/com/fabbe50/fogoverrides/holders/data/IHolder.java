package com.fabbe50.fogoverrides.holders.data;

import com.fabbe50.fogoverrides.holders.data.DefaultVariables;

public interface IHolder {

    String getHolderType();

    DefaultVariables getVariables();

    default String getName() {
        return getVariables().getName();
    }

    default String getTranslationKey() {
        return getVariables().getTranslationKey();
    }

    default boolean showOnConfig() {
        return getVariables().showOnConfig();
    }

    default boolean isEnabled() {
        return getVariables().isEnabled();
    }

    default void setEnabled(boolean enabled) {
        getVariables().setEnabled(enabled);
    }

    default int getStartDistance() {
        return getVariables().getStartDistance();
    }

    default void setStartDistance(int startDistance) {
        getVariables().setStartDistance(startDistance);
    }

    default int getDefaultStartDistance() {
        return getVariables().getDefaultStartDistance();
    }

    default int getEndDistance() {
        return getVariables().getEndDistance();
    }

    default void setEndDistance(int endDistance) {
        getVariables().setEndDistance(endDistance);
    }

    default int getDefaultEndDistance() {
        return getVariables().getDefaultEndDistance();
    }

    default int getMinDistance() {
        return getVariables().getMinDistance();
    }

    default int getMaxDistance() {
        return getVariables().getMaxDistance();
    }

    default boolean shouldOverrideColor() {
        return getVariables().shouldOverrideColor();
    }

    default void setOverrideColor(boolean overrideColor) {
        getVariables().setOverrideColor(overrideColor);
    }

    default boolean getDefaultShouldOverrideColor() {
        return getVariables().getDefaultShouldOverrideColor();
    }

    default int getColor() {
        return getVariables().getColor();
    }

    default void setColor(int color) {
        getVariables().setColor(color);
    }

    default int getDefaultColor() {
        return getVariables().getDefaultColor();
    }
}
