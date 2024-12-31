package com.fabbe50.fogoverrides;

import com.fabbe50.fogoverrides.ModConfig.CalculationSetting;
import com.fabbe50.fogoverrides.data.FogSetting;
import com.fabbe50.fogoverrides.data.GameModeSettings;
import com.fabbe50.fogoverrides.data.ModFogData;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.*;
import me.shedaniel.clothconfig2.impl.builders.IntSliderBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.fabbe50.fogoverrides.ModConfig.*;

public class ClothScreen {
    private static List<IntegerSliderEntry> distanceSliders = new ArrayList<>();
    private static CalculationSetting calculationSetting = CalculationSetting.BLOCKS;
    private static float renderDistance = 192;

    public static Screen getConfigScreen(Screen parent) {
        var builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("text.fogoverrides.title"));
        if (parent == null)
            builder.setTransparentBackground(true);

        distanceSliders = new ArrayList<>();
        calculationSetting = ModConfig.calculationSetting;
        renderDistance = Minecraft.getInstance().options.renderDistance().get() * 16;

        var entryBuilder = builder.entryBuilder();
        var general = builder.getOrCreateCategory(Component.translatable("text.fogoverrides.category.general"));
        KeyCodeEntry openMenuKeyBind = entryBuilder.startKeyCodeField(Component.translatable("text.fogoverrides.key.open_menu"), ModConfigClient.OPEN_CONFIG.key)
                .setDefaultValue(ModConfigClient.OPEN_CONFIG.getDefaultKey())
                .setTooltip(Component.translatable("text.fogoverrides.key.open_menu.tooltip"))
                .setKeySaveConsumer(ModConfigClient.OPEN_CONFIG::setKey)
                .build();
        general.addEntry(openMenuKeyBind);

        EnumListEntry<CalculationSetting> calculationType = entryBuilder.startEnumSelector(Component.translatable("text.fogoverrides.option.calculation-setting"), CalculationSetting.class, ModConfig.calculationSetting)
                .setDefaultValue(CalculationSetting.BLOCKS)
                .setTooltip(
                        Component.translatable("text.fogoverrides.option.calculation-setting.tooltip[0]"),
                        Component.translatable("text.fogoverrides.option.calculation-setting.tooltip[1]"),
                        Component.translatable("text.fogoverrides.option.calculation-setting.tooltip[2]"),
                        Component.translatable("text.fogoverrides.option.calculation-setting.tooltip[3]")
                )
                .setEnumNameProvider(anEnum -> {
                    calculationSetting = (CalculationSetting) anEnum;
                    updateDistanceSliders();
                    return calculationSetting.getName();
                })
                .setSaveConsumer(anEnum -> ModConfig.calculationSetting = anEnum)
                .build();
        general.addEntry(calculationType);

        SubCategoryBuilder spectatorCategory = createGameModeSettingsSubCat(entryBuilder, "spectator", spectatorSettings);
        SubCategoryBuilder creativeCategory = createGameModeSettingsSubCat(entryBuilder, "creative", creativeSettings);
        general.addEntry(spectatorCategory.build());
        general.addEntry(creativeCategory.build());

        SubCategoryBuilder overworldSubCat = createModFogDataSubCat(entryBuilder, Utilities.getOverworld(), ModConfig.overworldFogData, false);
        general.addEntry(overworldSubCat.build());
        SubCategoryBuilder netherSubCat = createModFogDataSubCat(entryBuilder, Utilities.getNether(), ModConfig.netherFogData, false);
        general.addEntry(netherSubCat.build());
        SubCategoryBuilder theEndSubCat = createModFogDataSubCat(entryBuilder, Utilities.getTheEnd(), ModConfig.theEndFogData, false);
        general.addEntry(theEndSubCat.build());

        SubCategoryBuilder liquidSubCat = entryBuilder.startSubCategory(Component.translatable("text.fogoverrides.subcat.liquids"));
        BooleanListEntry waterFogEnabled = entryBuilder.startBooleanToggle(Component.translatable("text.fogoverrides.option.water_fog_enabled"), ModConfig.waterFogEnabled)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.fogoverrides.option.water_fog_enabled.tooltip"))
                .setSaveConsumer(aBoolean -> ModConfig.waterFogEnabled = aBoolean)
                .build();
        BooleanListEntry lavaFogEnabled = entryBuilder.startBooleanToggle(Component.translatable("text.fogoverrides.option.lava_fog_enabled"), ModConfig.lavaFogEnabled)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.fogoverrides.option.lava_fog_enabled.tooltip"))
                .setSaveConsumer(aBoolean -> ModConfig.lavaFogEnabled = aBoolean)
                .build();
        liquidSubCat.addAll(List.of(waterFogEnabled, lavaFogEnabled));
        general.addEntry(liquidSubCat.build());

        IntegerSliderEntry cloudHeight = entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.cloud_height"), ModConfig.cloudHeight, -64, 319)
                .setDefaultValue(192)
                .setTooltip(Component.translatable("text.fogoverrides.option.cloud_height.tooltip"))
                .setSaveConsumer(integer -> ModConfig.cloudHeight = integer)
                .build();
        general.addEntry(cloudHeight);

        SubCategoryBuilder overlays = entryBuilder.startSubCategory(Component.translatable("text.fogoverrides.subcat.overlays"));
        BooleanListEntry waterOverlayEnabled = entryBuilder.startBooleanToggle(Component.translatable("text.fogoverrides.option.water-overlay-enabled"), ModConfig.renderWaterOverlay)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.fogoverrides.option.water-overlay-enabled.tooltip"))
                .setYesNoTextSupplier(aBoolean -> Component.translatable("text.fogoverrides.setting" + (aBoolean ? ".enabled" : ".disabled")).withStyle(aBoolean ? ChatFormatting.GREEN : ChatFormatting.RED))
                .setSaveConsumer(aBoolean -> ModConfig.renderWaterOverlay = aBoolean)
                .build();
        BooleanListEntry fireOverlayEnabled = entryBuilder.startBooleanToggle(Component.translatable("text.fogoverrides.option.fire-overlay-enabled"), ModConfig.renderFireOverlay)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.fogoverrides.option.fire-overlay-enabled.tooltip"))
                .setYesNoTextSupplier(aBoolean -> Component.translatable("text.fogoverrides.setting" + (aBoolean ? ".enabled" : ".disabled")).withStyle(aBoolean ? ChatFormatting.GREEN : ChatFormatting.RED))
                .setSaveConsumer(aBoolean -> ModConfig.renderFireOverlay = aBoolean)
                .build();
        IntegerSliderEntry fireOverlayOffset = entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.fire-overlay-offset"), ModConfig.fireOverlayOffset, -100, 100)
                .setDefaultValue(0)
                .setTooltip(Component.translatable("text.fogoverrides.option.fire-overlay-offset.tooltip"))
                .setSaveConsumer(integer -> ModConfig.fireOverlayOffset = integer)
                .build();
        IntegerSliderEntry firePotionOverlayOffset = entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.fire-potion-overlay-offset"), ModConfig.firePotionOverlayOffset, -100, 100)
                .setDefaultValue(-25)
                .setTooltip(Component.translatable("text.fogoverrides.option.fire-potion-overlay-offset.tooltip"))
                .setSaveConsumer(integer -> ModConfig.firePotionOverlayOffset = integer)
                .build();
        overlays.addAll(List.of(waterOverlayEnabled, fireOverlayEnabled, fireOverlayOffset, firePotionOverlayOffset));
        general.addEntry(overlays.build());

        var biomeSettings = builder.getOrCreateCategory(Component.translatable("text.fogoverrides.category.biomes"));
        Map<String, ModFogData> biomes = ModConfig.getBiomeStorage();
        for (String location : biomes.keySet()) {
            ModFogData fogData = biomes.get(location);
            SubCategoryBuilder biomeSubCategory = createModFogDataSubCat(entryBuilder, ResourceLocation.parse(location), fogData, true);
            biomeSettings.addEntry(biomeSubCategory.build());
        }

        return builder.setSavingRunnable(() -> {
            try {
                ModConfig.save(ModConfig.getConfigFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ModConfig.load(ModConfig.getConfigFile());
            // TODO: Send update packet on save.
        }).build();
    }

    private static List<AbstractConfigListEntry<?>> createTerrainFogSetting(ConfigEntryBuilder entryBuilder, String prefix, FogSetting fogSetting, FogSetting defaultSetting, boolean withColor) {
        BooleanListEntry isEnabled = entryBuilder.startBooleanToggle(Component.translatable("text.fogoverrides.option._fog_enabled", prefix), fogSetting.isEnabled())
                .setDefaultValue(defaultSetting.isEnabled())
                .setTooltip(Component.translatable("text.fogoverrides.option.fog_enabled.tooltip"))
                .setYesNoTextSupplier(aBoolean -> Component.translatable("text.fogoverrides.setting" + (aBoolean ? ".enabled" : ".disabled")).withStyle(aBoolean ? ChatFormatting.GREEN : ChatFormatting.RED))
                .setSaveConsumer(fogSetting::setEnabled)
                .build();
        IntegerSliderEntry nearDistance = buildDistanceSlider(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option._fog_start_distance", prefix), (int)fogSetting.getNearDistance(), -1, FOG_START_MAX)
                .setDefaultValue((int) defaultSetting.getNearDistance())
                .setTooltip(Component.translatable("text.fogoverrides.option.fog_start_distance.tooltip"))
                .setSaveConsumer(fogSetting::setNearDistance));
        IntegerSliderEntry farDistance = buildDistanceSlider(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option._fog_end_distance", prefix), (int)fogSetting.getFarDistance(), 0, FOG_END_MAX)
                .setDefaultValue((int) defaultSetting.getFarDistance())
                .setTooltip(Component.translatable("text.fogoverrides.option.fog_end_distance.tooltip"))
                .setSaveConsumer(fogSetting::setFarDistance));
        nearDistance.setErrorSupplier(() -> nearDistance.getValue() >= farDistance.getValue() && nearDistance.getValue() != -1 ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        nearDistance.requestReferenceRebuilding();
        farDistance.setErrorSupplier(() -> nearDistance.getValue() >= farDistance.getValue() && farDistance.getValue() != -1 ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        farDistance.requestReferenceRebuilding();
        if (withColor) {
            ColorEntry color = entryBuilder.startColorField(Component.translatable("text.fogoverrides.option.fog_color"), fogSetting.getColor())
                    .setDefaultValue(defaultSetting.getColor())
                    .setTooltip(Component.translatable("text.fogoverrides.option.fog_color.tooltip"))
                    .setSaveConsumer(fogSetting::setColor)
                    .build();
            return List.of(isEnabled, nearDistance, farDistance, color);
        }
        return List.of(isEnabled, nearDistance, farDistance);
    }

    private static List<AbstractConfigListEntry<?>> createOtherFogSetting(ConfigEntryBuilder entryBuilder, String prefix, FogSetting fogSetting, FogSetting defaultSetting, boolean withColor) {
        BooleanListEntry isEnabled = entryBuilder.startBooleanToggle(Component.translatable("text.fogoverrides.option._fog_enabled", prefix), fogSetting.isEnabled())
                .setDefaultValue(defaultSetting.isEnabled())
                .setTooltip(Component.translatable("text.fogoverrides.option.fog_enabled.tooltip"))
                .setYesNoTextSupplier(aBoolean -> Component.translatable("text.fogoverrides.setting" + (aBoolean ? ".enabled" : ".disabled")).withStyle(aBoolean ? ChatFormatting.GREEN : ChatFormatting.RED))
                .setSaveConsumer(fogSetting::setEnabled)
                .build();
        IntegerSliderEntry nearDistance = buildDistanceSlider2(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option._fog_start_distance", prefix), (int)fogSetting.getNearDistance(), -1, FOG_START_MAX)
                .setDefaultValue((int) defaultSetting.getNearDistance())
                .setTooltip(Component.translatable("text.fogoverrides.option.fog_start_distance.tooltip"))
                .setSaveConsumer(fogSetting::setNearDistance));
        IntegerSliderEntry farDistance = buildDistanceSlider2(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option._fog_end_distance", prefix), (int)fogSetting.getFarDistance(), 0, FOG_END_MAX)
                .setDefaultValue((int) defaultSetting.getFarDistance())
                .setTooltip(Component.translatable("text.fogoverrides.option.fog_end_distance.tooltip"))
                .setSaveConsumer(fogSetting::setFarDistance));
        nearDistance.setErrorSupplier(() -> nearDistance.getValue() >= farDistance.getValue() && nearDistance.getValue() != -1 ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        nearDistance.requestReferenceRebuilding();
        farDistance.setErrorSupplier(() -> nearDistance.getValue() >= farDistance.getValue() && farDistance.getValue() != -1 ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        farDistance.requestReferenceRebuilding();
        if (withColor) {
            ColorEntry color = entryBuilder.startColorField(Component.translatable("text.fogoverrides.option.fog_color"), fogSetting.getColor())
                    .setDefaultValue(defaultSetting.getColor())
                    .setTooltip(Component.translatable("text.fogoverrides.option.fog_color.tooltip"))
                    .setSaveConsumer(fogSetting::setColor)
                    .build();
            return List.of(isEnabled, nearDistance, farDistance, color);
        }
        return List.of(isEnabled, nearDistance, farDistance);
    }

    private static SubCategoryBuilder createGameModeSettingsSubCat(ConfigEntryBuilder entryBuilder, String gameMode, GameModeSettings settings) {
        GameModeSettings defaultSettings = Utilities.getDefaultGameModeSettings();
        SubCategoryBuilder gameModeSubCategory = entryBuilder.startSubCategory(Component.translatable("text.fogoverrides.subcat._settings", Utilities.capitalizeFirstInEveryWord(gameMode)));
        EnumListEntry<GameModeSettings.FogMode> fogMode = entryBuilder.startEnumSelector(Component.translatable("text.fogoverrides.option.fog_mode"), GameModeSettings.FogMode.class, settings.getFogMode())
                .setDefaultValue(GameModeSettings.FogMode.MOD_FOG)
                .setTooltip(Component.translatable("text.fogoverrides.option.fog_mode.tooltip", gameMode))
                .setEnumNameProvider(anEnum -> ((GameModeSettings.FogMode)anEnum).getName())
                .setSaveConsumer(settings::setFogMode)
                .build();
        gameModeSubCategory.add(fogMode);
        gameModeSubCategory.addAll(createTerrainFogSetting(entryBuilder, "Terrain", settings.getTerrainFog(), defaultSettings.getTerrainFog(), false));
        gameModeSubCategory.addAll(createOtherFogSetting(entryBuilder, "Water", settings.getWaterFog(), defaultSettings.getWaterFog(), false));
        gameModeSubCategory.addAll(createOtherFogSetting(entryBuilder, "Lava", settings.getLavaFog(), defaultSettings.getLavaFog(), false));
        return gameModeSubCategory;
    }

    private static SubCategoryBuilder createModFogDataSubCat(ConfigEntryBuilder entryBuilder, ResourceLocation location, ModFogData fogData, boolean hasWaterColorSettings) {
        ModFogData defaultFog = Utilities.getDefaultFogData();
        String name = Utilities.capitalizeFirstInEveryWord(location.getPath().replace("_", " "));
        SubCategoryBuilder modFogSubcategory = entryBuilder.startSubCategory(Component.literal(name));
        BooleanListEntry overrideFog = entryBuilder.startBooleanToggle(Component.translatable("text.fogoverrides.option.override_fog"), fogData.isOverrideGameFog())
                .setDefaultValue(defaultFog.isOverrideGameFog())
                .setTooltip(Component.translatable("text.fogoverrides.option.override_fog.tooltip"))
                .setYesNoTextSupplier(aBoolean -> Component.translatable("text.fogoverrides.setting" + (aBoolean ? ".enabled" : ".disabled")).withStyle(aBoolean ? ChatFormatting.GREEN : ChatFormatting.RED))
                .setSaveConsumer(fogData::setOverrideGameFog)
                .build();
        BooleanListEntry fogEnabled = entryBuilder.startBooleanToggle(Component.translatable("text.fogoverrides.option.fog_enabled"), fogData.isFogEnabled())
                .setDefaultValue(defaultFog.isFogEnabled())
                .setTooltip(Component.translatable("text.fogoverrides.option.fog_enabled.tooltip"))
                .setYesNoTextSupplier(aBoolean -> Component.translatable("text.fogoverrides.setting" + (aBoolean ? ".enabled" : ".disabled")).withStyle(aBoolean ? ChatFormatting.GREEN : ChatFormatting.RED))
                .setSaveConsumer(fogData::setFogEnabled)
                .build();
        IntegerSliderEntry fogStartDistance = buildDistanceSlider(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.fog_start_distance"), (int)fogData.getNearDistance(), -1, FOG_START_MAX)
                .setDefaultValue((int)defaultFog.getNearDistance())
                .setTooltip(Component.translatable("text.fogoverrides.option.fog_start_distance.tooltip"))
                .setSaveConsumer(fogData::setNearDistance));
        IntegerSliderEntry fogEndDistance = buildDistanceSlider(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.fog_end_distance"), (int)fogData.getFarDistance(), -1, FOG_END_MAX)
                .setDefaultValue((int)defaultFog.getFarDistance())
                .setTooltip(Component.translatable("text.fogoverrides.option.fog_end_distance.tooltip"))
                .setSaveConsumer(fogData::setFarDistance));
        fogStartDistance.setErrorSupplier(() -> fogStartDistance.getValue() >= fogEndDistance.getValue() && fogStartDistance.getValue() != -1 ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        fogStartDistance.requestReferenceRebuilding();
        fogEndDistance.setErrorSupplier(() -> fogStartDistance.getValue() >= fogEndDistance.getValue() && fogEndDistance.getValue() != -1 ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        fogEndDistance.requestReferenceRebuilding();
        BooleanListEntry overrideSkyColor = entryBuilder.startBooleanToggle(Component.translatable("text.fogoverrides.option.override_sky_color"), fogData.isOverrideSkyColor())
                .setDefaultValue(defaultFog.isOverrideSkyColor())
                .setTooltip(Component.translatable("text.fogoverrides.option.override_sky_color.tooltip"))
                .setYesNoTextSupplier(aBoolean -> Component.translatable("text.fogoverrides.setting" + (aBoolean ? ".enabled" : ".disabled")).withStyle(aBoolean ? ChatFormatting.GREEN : ChatFormatting.RED))
                .setSaveConsumer(fogData::setOverrideSkyColor)
                .build();
        ColorEntry skyColor = entryBuilder.startColorField(Component.translatable("text.fogoverrides.option.sky_color"), fogData.getSkyColor())
                .setDefaultValue(defaultFog.getSkyColor())
                .setTooltip(Component.translatable("text.fogoverrides.option.sky_color.tooltip"))
                .setSaveConsumer(fogData::setSkyColor)
                .build();
        BooleanListEntry overrideFogColor = entryBuilder.startBooleanToggle(Component.translatable("text.fogoverrides.option.override_fog_color"), fogData.isOverrideFogColor())
                .setDefaultValue(defaultFog.isOverrideFogColor())
                .setTooltip(Component.translatable("text.fogoverrides.option.override_fog_color.tooltip"))
                .setYesNoTextSupplier(aBoolean -> Component.translatable("text.fogoverrides.setting" + (aBoolean ? ".enabled" : ".disabled")).withStyle(aBoolean ? ChatFormatting.GREEN : ChatFormatting.RED))
                .setSaveConsumer(fogData::setOverrideFogColor)
                .build();
        ColorEntry fogColor = entryBuilder.startColorField(Component.translatable("text.fogoverrides.option.fog_color"), fogData.getFogColor())
                .setDefaultValue(defaultFog.getFogColor())
                .setTooltip(Component.translatable("text.fogoverrides.option.fog_color.tooltip"))
                .setSaveConsumer(fogData::setFogColor)
                .build();
        SubCategoryBuilder waterSettings = entryBuilder.startSubCategory(Component.translatable("text.fogoverrides.subcat.water_settings"));
        BooleanListEntry overrideWaterFog = entryBuilder.startBooleanToggle(Component.translatable("text.fogoverrides.option.override_water_fog"), fogData.isOverrideWaterFog())
                .setDefaultValue(defaultFog.isOverrideWaterFog())
                .setTooltip(Component.translatable("text.fogoverrides.option.override_water_fog.tooltip"))
                .setSaveConsumer(fogData::setOverrideWaterFog)
                .build();
        IntegerSliderEntry waterFogStartDistance = buildDistanceSlider2(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.water_fog_start_distance"), (int)fogData.getWaterNearDistance(), 0, FOG_START_MAX)
                .setDefaultValue((int)defaultFog.getNearDistance())
                .setTooltip(Component.translatable("text.fogoverrides.option.water_fog_start_distance.tooltip"))
                .setSaveConsumer(fogData::setWaterNearDistance));
        IntegerSliderEntry waterFogEndDistance = buildDistanceSlider2(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.water_fog_end_distance"), (int)fogData.getWaterFarDistance(), 1, FOG_END_MAX)
                .setDefaultValue((int)defaultFog.getFarDistance())
                .setTooltip(Component.translatable("text.fogoverrides.option.water_fog_end_distance.tooltip"))
                .setSaveConsumer(fogData::setWaterFarDistance));
        waterFogStartDistance.setErrorSupplier(() -> waterFogStartDistance.getValue() >= waterFogEndDistance.getValue() ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        waterFogStartDistance.requestReferenceRebuilding();
        waterFogEndDistance.setErrorSupplier(() -> waterFogStartDistance.getValue() >= waterFogEndDistance.getValue() ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        waterFogEndDistance.requestReferenceRebuilding();
        BooleanListEntry waterPotionEffect = entryBuilder.startBooleanToggle(Component.translatable("text.fogoverrides.option.water_potion_effect"), fogData.isOverrideWaterFog())
                .setDefaultValue(defaultFog.isOverrideWaterFog())
                .setTooltip(Component.translatable("text.fogoverrides.option.water_potion_effect.tooltip"))
                .setSaveConsumer(fogData::setWaterPotionEffect)
                .build();
        IntegerSliderEntry waterPotionFogStartDistance = buildDistanceSlider2(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.water_potion_fog_start_distance"), (int)fogData.getWaterPotionNearDistance(), 0, FOG_START_MAX)
                .setDefaultValue((int)defaultFog.getNearDistance())
                .setTooltip(Component.translatable("text.fogoverrides.option.water_potion_fog_start_distance.tooltip"))
                .setSaveConsumer(fogData::setWaterPotionNearDistance));
        IntegerSliderEntry waterPotionFogEndDistance = buildDistanceSlider2(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.water_potion_fog_end_distance"), (int)fogData.getWaterPotionFarDistance(), 1, FOG_END_MAX)
                .setDefaultValue((int)defaultFog.getFarDistance())
                .setTooltip(Component.translatable("text.fogoverrides.option.water_potion_fog_end_distance.tooltip"))
                .setSaveConsumer(fogData::setWaterPotionFarDistance));
        waterPotionFogStartDistance.setErrorSupplier(() -> waterPotionFogStartDistance.getValue() >= waterPotionFogEndDistance.getValue() ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        waterPotionFogStartDistance.requestReferenceRebuilding();
        waterPotionFogEndDistance.setErrorSupplier(() -> waterPotionFogStartDistance.getValue() >= waterPotionFogEndDistance.getValue() ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        waterPotionFogEndDistance.requestReferenceRebuilding();
        BooleanListEntry overrideWaterColor = null;
        ColorEntry waterColor = null;
        if (hasWaterColorSettings) {
            overrideWaterColor = entryBuilder.startBooleanToggle(Component.translatable("text.fogoverrides.option.override_water_color"), fogData.isOverrideWaterColor())
                    .setDefaultValue(defaultFog.isOverrideWaterColor())
                    .setTooltip(Component.translatable("text.fogoverrides.feature.experimental").withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW), Component.translatable("text.fogoverrides.option.override_water_color.tooltip"), Component.translatable("text.fogoverrides.requires_reloading_world.tooltip").withStyle(ChatFormatting.RED))
                    .setYesNoTextSupplier(aBoolean -> Component.translatable("text.fogoverrides.setting" + (aBoolean ? ".enabled" : ".disabled")).withStyle(aBoolean ? ChatFormatting.GREEN : ChatFormatting.RED))
                    .setSaveConsumer(fogData::setOverrideWaterColor)
                    .build();
            waterColor = entryBuilder.startColorField(Component.translatable("text.fogoverrides.option.water_color"), fogData.getWaterColor())
                    .setDefaultValue(defaultFog.getWaterColor())
                    .setTooltip(Component.translatable("text.fogoverrides.feature.experimental").withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW), Component.translatable("text.fogoverrides.option.water_color.tooltip"), Component.translatable("text.fogoverrides.requires_reloading_world.tooltip").withStyle(ChatFormatting.RED))
                    .setSaveConsumer(fogData::setWaterColor)
                    .build();
        }
        BooleanListEntry overrideWaterFogColor = entryBuilder.startBooleanToggle(Component.translatable("text.fogoverrides.option.override_water_fog_color"), fogData.isOverrideWaterFogColor())
                .setDefaultValue(defaultFog.isOverrideWaterFogColor())
                .setTooltip(Component.translatable("text.fogoverrides.option.override_water_fog_color.tooltip"))
                .setYesNoTextSupplier(aBoolean -> Component.translatable("text.fogoverrides.setting" + (aBoolean ? ".enabled" : ".disabled")).withStyle(aBoolean ? ChatFormatting.GREEN : ChatFormatting.RED))
                .setSaveConsumer(fogData::setOverrideWaterFogColor)
                .build();
        ColorEntry waterFogColor = entryBuilder.startColorField(Component.translatable("text.fogoverrides.option.water_fog_color"), fogData.getWaterFogColor())
                .setDefaultValue(defaultFog.getWaterFogColor())
                .setTooltip(Component.translatable("text.fogoverrides.option.water_fog_color.tooltip"))
                .setSaveConsumer(fogData::setWaterFogColor)
                .build();
        SubCategoryBuilder lavaSettings = entryBuilder.startSubCategory(Component.translatable("text.fogoverrides.subcat.lava_settings"));
        BooleanListEntry overrideLavaFog = entryBuilder.startBooleanToggle(Component.translatable("text.fogoverrides.option.override_lava_fog"), fogData.isOverrideLavaFog())
                .setDefaultValue(defaultFog.isOverrideLavaFog())
                .setTooltip(Component.translatable("text.fogoverrides.option.override_lava_fog.tooltip"))
                .setSaveConsumer(fogData::setOverrideLavaFog)
                .build();
        IntegerSliderEntry lavaFogStartDistance = buildDistanceSlider2(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.lava_fog_start_distance"), (int)fogData.getLavaNearDistance(), 0, FOG_START_MAX)
                .setDefaultValue((int)defaultFog.getNearDistance())
                .setTooltip(Component.translatable("text.fogoverrides.option.lava_fog_start_distance.tooltip"))
                .setSaveConsumer(fogData::setLavaNearDistance));
        IntegerSliderEntry lavaFogEndDistance = buildDistanceSlider2(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.lava_fog_end_distance"), (int)fogData.getLavaFarDistance(), 1, FOG_END_MAX)
                .setDefaultValue((int)defaultFog.getFarDistance())
                .setTooltip(Component.translatable("text.fogoverrides.option.lava_fog_end_distance.tooltip"))
                .setSaveConsumer(fogData::setLavaFarDistance));
        lavaFogStartDistance.setErrorSupplier(() -> lavaFogStartDistance.getValue() >= lavaFogEndDistance.getValue() ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        lavaFogStartDistance.requestReferenceRebuilding();
        lavaFogEndDistance.setErrorSupplier(() -> lavaFogStartDistance.getValue() >= lavaFogEndDistance.getValue() ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        lavaFogEndDistance.requestReferenceRebuilding();
        BooleanListEntry lavaPotionEffect = entryBuilder.startBooleanToggle(Component.translatable("text.fogoverrides.option.lava_potion_effect"), fogData.isLavaPotionEffect())
                .setDefaultValue(defaultFog.isLavaPotionEffect())
                .setTooltip(Component.translatable("text.fogoverrides.option.lava_potion_effect.tooltip"))
                .setSaveConsumer(fogData::setLavaPotionEffect)
                .build();
        IntegerSliderEntry lavaPotionFogStartDistance = buildDistanceSlider2(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.lava_potion_fog_start_distance"), (int)fogData.getLavaPotionNearDistance(), 0, FOG_START_MAX)
                .setDefaultValue((int)defaultFog.getNearDistance())
                .setTooltip(Component.translatable("text.fogoverrides.option.lava_potion_fog_start_distance.tooltip"))
                .setSaveConsumer(fogData::setLavaPotionNearDistance));
        IntegerSliderEntry lavaPotionFogEndDistance = buildDistanceSlider2(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.lava_potion_fog_end_distance"), (int)fogData.getLavaPotionFarDistance(), 1, FOG_END_MAX)
                .setDefaultValue((int)defaultFog.getFarDistance())
                .setTooltip(Component.translatable("text.fogoverrides.option.lava_potion_fog_end_distance.tooltip"))
                .setSaveConsumer(fogData::setLavaPotionFarDistance));
        lavaPotionFogStartDistance.setErrorSupplier(() -> lavaPotionFogStartDistance.getValue() >= lavaPotionFogEndDistance.getValue() ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        lavaPotionFogStartDistance.requestReferenceRebuilding();
        lavaPotionFogEndDistance.setErrorSupplier(() -> lavaPotionFogStartDistance.getValue() >= lavaPotionFogEndDistance.getValue() ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        lavaPotionFogEndDistance.requestReferenceRebuilding();

        SubCategoryBuilder rainSettings = entryBuilder.startSubCategory(Component.translatable("text.fogoverrides.subcat.rain"));

        List<AbstractConfigListEntry<?>> waterSettingEntries;
        List<AbstractConfigListEntry<?>> lavaSettingEntries = List.of(overrideLavaFog, lavaFogStartDistance, lavaFogEndDistance, lavaPotionEffect, lavaPotionFogStartDistance, lavaPotionFogEndDistance);
        List<AbstractConfigListEntry<?>> rainSettingEntries = createTerrainFogSetting(entryBuilder, "Rain", fogData.getRain(), Utilities.getDefaultTerrainDisabled(), true);

        if (overrideWaterColor != null) {
            waterSettingEntries = List.of(overrideWaterFog, waterFogStartDistance, waterFogEndDistance, waterPotionEffect, waterPotionFogStartDistance, waterPotionFogEndDistance, overrideWaterColor, waterColor, overrideWaterFogColor, waterFogColor);
        } else {
            waterSettingEntries = List.of(overrideWaterFog, waterFogStartDistance, waterFogEndDistance, waterPotionEffect, waterPotionFogStartDistance, waterPotionFogEndDistance, overrideWaterFogColor, waterFogColor);
        }

        waterSettings.addAll(waterSettingEntries);
        lavaSettings.addAll(lavaSettingEntries);
        rainSettings.addAll(rainSettingEntries);

        List<AbstractConfigListEntry<?>> settingEntries = List.of(overrideFog, fogEnabled, fogStartDistance, fogEndDistance, overrideSkyColor, skyColor, overrideFogColor, fogColor, waterSettings.build(), lavaSettings.build(), rainSettings.build());
        List<AbstractConfigListEntry<?>> configListEntries = new ArrayList<>();
        configListEntries.addAll(waterSettingEntries);
        configListEntries.addAll(lavaSettingEntries);
        configListEntries.addAll(rainSettingEntries);
        configListEntries.addAll(settingEntries);

        for (AbstractConfigListEntry<?> entry : configListEntries) {
            entry.appendSearchTags(List.of(name));
        }

        modFogSubcategory.addAll(settingEntries);
        return modFogSubcategory;
    }

    private static IntegerSliderEntry buildDistanceSlider(IntSliderBuilder sliderEntry) {
        sliderEntry.setTextGetter(ClothScreen::getDistanceSliderComponent);
        IntegerSliderEntry slider = sliderEntry.build();
        distanceSliders.add(slider);
        return slider;
    }

    private static IntegerSliderEntry buildDistanceSlider2(IntSliderBuilder sliderEntry) {
        sliderEntry.setTextGetter(value -> Component.translatable("text.fogoverrides.setting.blocks", value));
        return sliderEntry.build();
    }

    private static void updateDistanceSliders() {
        for (IntegerSliderEntry slider : distanceSliders) {
            slider.setTextGetter(ClothScreen::getDistanceSliderComponent);
        }
    }

    private static Component getDistanceSliderComponent(float value) {
        if (calculationSetting == CalculationSetting.PERCENT_BLOCKS) {
            float fractionalValue = value / renderDistance;
            int percentage = Math.round(fractionalValue * 100);
            if (fractionalValue < 0) {
                percentage = -1;
            }
            return Component.translatable("text.fogoverrides.setting.percent", percentage);
        } else if (calculationSetting == CalculationSetting.PERCENT) {
            float fractionalValue = value / PERCENTAGE_DIVIDER;
            int percentage = Math.round(fractionalValue * 100);
            if (fractionalValue < 0) {
                percentage = -1;
            }
            return Component.translatable("text.fogoverrides.setting.percent", percentage);
        }
        return Component.translatable("text.fogoverrides.setting.blocks", (int) value);
    }
}
