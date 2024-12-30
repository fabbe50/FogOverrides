package com.fabbe50.fogoverrides;

import com.fabbe50.fogoverrides.ModConfig.CalculationSetting;
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

        EnumListEntry<CalculationSetting> respectRenderDistance = entryBuilder.startEnumSelector(Component.translatable("text.fogoverrides.option.calculation-setting"), CalculationSetting.class, ModConfig.calculationSetting)
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
        general.addEntry(respectRenderDistance);

        SubCategoryBuilder spectatorCategory = entryBuilder.startSubCategory(Component.translatable("text.fogoverrides.subcat.spectator"));
        BooleanListEntry spectatorHasModFog = entryBuilder.startBooleanToggle(Component.translatable("text.fogoverrides.option.spectator_fog"), ModConfig.spectatorHasModFog)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("text.fogoverrides.option.spectator_fog.tooltip"))
                .setYesNoTextSupplier(aBoolean -> Component.translatable("text.fogoverrides.setting" + (aBoolean ? ".enabled" : ".disabled")).withStyle(aBoolean ? ChatFormatting.GREEN : ChatFormatting.RED))
                .setSaveConsumer(aBoolean -> ModConfig.spectatorHasModFog = aBoolean)
                .build();
        IntegerSliderEntry spectatorFogStartDistance = buildDistanceSlider(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.spectator_fog_start_distance"), (int)ModConfig.spectatorNearDistance, -1, FOG_START_MAX)
                .setDefaultValue(-1)
                .setTooltip(Component.translatable("text.fogoverrides.option.spectator_fog_start_distance.tooltip"))
                .setSaveConsumer(integer -> ModConfig.spectatorNearDistance = integer));
        IntegerSliderEntry spectatorFogEndDistance = buildDistanceSlider(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.spectator_fog_end_distance"), (int)ModConfig.spectatorFarDistance, -1, FOG_END_MAX)
                .setDefaultValue(-1)
                .setTooltip(Component.translatable("text.fogoverrides.option.spectator_fog_end_distance.tooltip"))
                .setSaveConsumer(integer -> ModConfig.spectatorFarDistance = integer));
        spectatorFogStartDistance.setErrorSupplier(() -> spectatorFogStartDistance.getValue() >= spectatorFogEndDistance.getValue() && spectatorFogStartDistance.getValue() != -1 ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        spectatorFogStartDistance.requestReferenceRebuilding();
        spectatorFogEndDistance.setErrorSupplier(() -> spectatorFogStartDistance.getValue() >= spectatorFogEndDistance.getValue() && spectatorFogEndDistance.getValue() != -1 ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        spectatorFogEndDistance.requestReferenceRebuilding();   
        IntegerSliderEntry spectatorWaterFogStartDistance = buildDistanceSlider2(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.spectator_water_fog_start_distance"), (int)ModConfig.spectatorWaterNearDistance, -1, FOG_START_MAX)
                .setDefaultValue(-1)
                .setTooltip(Component.translatable("text.fogoverrides.option.spectator_water_fog_start_distance.tooltip"))
                .setSaveConsumer(integer -> ModConfig.spectatorWaterNearDistance = integer));
        IntegerSliderEntry spectatorWaterFogEndDistance = buildDistanceSlider2(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.spectator_water_fog_end_distance"), (int)ModConfig.spectatorWaterFarDistance, -1, FOG_END_MAX)
                .setDefaultValue(-1)
                .setTooltip(Component.translatable("text.fogoverrides.option.spectator_water_fog_end_distance.tooltip"))
                .setSaveConsumer(integer -> ModConfig.spectatorWaterFarDistance = integer));
        spectatorWaterFogStartDistance.setErrorSupplier(() -> spectatorWaterFogStartDistance.getValue() >= spectatorWaterFogEndDistance.getValue() && spectatorWaterFogStartDistance.getValue() != -1 ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        spectatorWaterFogStartDistance.requestReferenceRebuilding();
        spectatorWaterFogEndDistance.setErrorSupplier(() -> spectatorWaterFogStartDistance.getValue() >= spectatorWaterFogEndDistance.getValue() && spectatorWaterFogEndDistance.getValue() != -1 ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        spectatorWaterFogEndDistance.requestReferenceRebuilding();
        IntegerSliderEntry spectatorLavaFogStartDistance = buildDistanceSlider2(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.spectator_lava_fog_start_distance"), (int)ModConfig.spectatorLavaNearDistance, -1, FOG_START_MAX)
                .setDefaultValue(-1)
                .setTooltip(Component.translatable("text.fogoverrides.option.spectator_lava_fog_start_distance.tooltip"))
                .setSaveConsumer(integer -> ModConfig.spectatorLavaNearDistance = integer));
        IntegerSliderEntry spectatorLavaFogEndDistance = buildDistanceSlider2(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.spectator_lava_fog_end_distance"), (int)ModConfig.spectatorLavaFarDistance, -1, FOG_END_MAX)
                .setDefaultValue(-1)
                .setTooltip(Component.translatable("text.fogoverrides.option.spectator_lava_fog_end_distance.tooltip"))
                .setSaveConsumer(integer -> ModConfig.spectatorLavaFarDistance = integer));
        spectatorLavaFogStartDistance.setErrorSupplier(() -> spectatorLavaFogStartDistance.getValue() >= spectatorLavaFogEndDistance.getValue() && spectatorLavaFogStartDistance.getValue() != -1 ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        spectatorLavaFogStartDistance.requestReferenceRebuilding();
        spectatorLavaFogEndDistance.setErrorSupplier(() -> spectatorLavaFogStartDistance.getValue() >= spectatorLavaFogEndDistance.getValue() && spectatorLavaFogEndDistance.getValue() != -1 ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        spectatorLavaFogEndDistance.requestReferenceRebuilding();
        spectatorCategory.addAll(List.of(spectatorHasModFog, spectatorFogStartDistance, spectatorFogEndDistance, spectatorWaterFogStartDistance, spectatorWaterFogEndDistance, spectatorLavaFogStartDistance, spectatorLavaFogEndDistance));
        general.addEntry(spectatorCategory.build());
        SubCategoryBuilder creativeCategory = entryBuilder.startSubCategory(Component.translatable("text.fogoverrides.subcat.creative"));
        BooleanListEntry creativeHasModFog = entryBuilder.startBooleanToggle(Component.translatable("text.fogoverrides.option.creative_fog"), ModConfig.creativeHasModFog)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("text.fogoverrides.option.creative_fog.tooltip"))
                .setYesNoTextSupplier(aBoolean -> Component.translatable("text.fogoverrides.setting" + (aBoolean ? ".enabled" : ".disabled")).withStyle(aBoolean ? ChatFormatting.GREEN : ChatFormatting.RED))
                .setSaveConsumer(aBoolean -> ModConfig.creativeHasModFog = aBoolean)
                .build();
        IntegerSliderEntry creativeFogStartDistance = buildDistanceSlider(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.creative_fog_start_distance"), (int)ModConfig.creativeNearDistance, -1, FOG_START_MAX)
                .setDefaultValue(-1)
                .setTooltip(Component.translatable("text.fogoverrides.option.creative_fog_start_distance.tooltip"))
                .setSaveConsumer(integer -> ModConfig.creativeNearDistance = integer));
        IntegerSliderEntry creativeFogEndDistance = buildDistanceSlider(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.creative_fog_end_distance"), (int)ModConfig.creativeFarDistance, -1, FOG_END_MAX)
                .setDefaultValue(-1)
                .setTooltip(Component.translatable("text.fogoverrides.option.creative_fog_end_distance.tooltip"))
                .setSaveConsumer(integer -> ModConfig.creativeFarDistance = integer));
        creativeFogStartDistance.setErrorSupplier(() -> creativeFogStartDistance.getValue() >= creativeFogEndDistance.getValue() && creativeFogStartDistance.getValue() != -1 ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        creativeFogStartDistance.requestReferenceRebuilding();
        creativeFogEndDistance.setErrorSupplier(() -> creativeFogStartDistance.getValue() >= creativeFogEndDistance.getValue() && creativeFogEndDistance.getValue() != -1 ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        creativeFogEndDistance.requestReferenceRebuilding();   
        IntegerSliderEntry creativeWaterFogStartDistance = buildDistanceSlider2(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.creative_water_fog_start_distance"), (int)ModConfig.creativeWaterNearDistance, -1, FOG_START_MAX)
                .setDefaultValue(-1)
                .setTooltip(Component.translatable("text.fogoverrides.option.creative_water_fog_start_distance.tooltip"))
                .setSaveConsumer(integer -> ModConfig.creativeWaterNearDistance = integer));
        IntegerSliderEntry creativeWaterFogEndDistance = buildDistanceSlider2(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.creative_water_fog_end_distance"), (int)ModConfig.creativeWaterFarDistance, -1, FOG_END_MAX)
                .setDefaultValue(-1)
                .setTooltip(Component.translatable("text.fogoverrides.option.creative_water_fog_end_distance.tooltip"))
                .setSaveConsumer(integer -> ModConfig.creativeWaterFarDistance = integer));
        creativeWaterFogStartDistance.setErrorSupplier(() -> creativeWaterFogStartDistance.getValue() >= creativeWaterFogEndDistance.getValue() && creativeWaterFogStartDistance.getValue() != -1 ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        creativeWaterFogStartDistance.requestReferenceRebuilding();
        creativeWaterFogEndDistance.setErrorSupplier(() -> creativeWaterFogStartDistance.getValue() >= creativeWaterFogEndDistance.getValue() && creativeWaterFogEndDistance.getValue() != -1 ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        creativeWaterFogEndDistance.requestReferenceRebuilding();
        IntegerSliderEntry creativeLavaFogStartDistance = buildDistanceSlider2(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.creative_lava_fog_start_distance"), (int)ModConfig.creativeLavaNearDistance, -1, FOG_START_MAX)
                .setDefaultValue(-1)
                .setTooltip(Component.translatable("text.fogoverrides.option.creative_lava_fog_start_distance.tooltip"))
                .setSaveConsumer(integer -> ModConfig.creativeLavaNearDistance = integer));
        IntegerSliderEntry creativeLavaFogEndDistance = buildDistanceSlider2(entryBuilder.startIntSlider(Component.translatable("text.fogoverrides.option.creative_lava_fog_end_distance"), (int)ModConfig.creativeLavaFarDistance, -1, FOG_END_MAX)
                .setDefaultValue(-1)
                .setTooltip(Component.translatable("text.fogoverrides.option.creative_lava_fog_end_distance.tooltip"))
                .setSaveConsumer(integer -> ModConfig.creativeLavaFarDistance = integer));
        creativeLavaFogStartDistance.setErrorSupplier(() -> creativeLavaFogStartDistance.getValue() >= creativeLavaFogEndDistance.getValue() && creativeLavaFogStartDistance.getValue() != -1 ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        creativeLavaFogStartDistance.requestReferenceRebuilding();
        creativeLavaFogEndDistance.setErrorSupplier(() -> creativeLavaFogStartDistance.getValue() >= creativeLavaFogEndDistance.getValue() && creativeLavaFogEndDistance.getValue() != -1 ? Optional.of(Component.translatable("text.fogoverrides.error.fog_distance")) : Optional.empty());
        creativeLavaFogEndDistance.requestReferenceRebuilding();
        creativeCategory.addAll(List.of(creativeHasModFog, creativeFogStartDistance, creativeFogEndDistance, creativeWaterFogStartDistance, creativeWaterFogEndDistance, creativeLavaFogStartDistance, creativeLavaFogEndDistance));
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
        }).build();
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

        List<AbstractConfigListEntry<?>> waterSettingEntries;
        List<AbstractConfigListEntry<?>> lavaSettingEntries = List.of(overrideLavaFog, lavaFogStartDistance, lavaFogEndDistance, lavaPotionEffect, lavaPotionFogStartDistance, lavaPotionFogEndDistance);
        List<AbstractConfigListEntry<?>> settingEntries = List.of(overrideFog, fogEnabled, fogStartDistance, fogEndDistance, overrideSkyColor, skyColor, overrideFogColor, fogColor, waterSettings.build(), lavaSettings.build());

        if (overrideWaterColor != null) {
            waterSettingEntries = List.of(overrideWaterFog, waterFogStartDistance, waterFogEndDistance, waterPotionEffect, waterPotionFogStartDistance, waterPotionFogEndDistance, overrideWaterColor, waterColor, overrideWaterFogColor, waterFogColor);
        } else {
            waterSettingEntries = List.of(overrideWaterFog, waterFogStartDistance, waterFogEndDistance, waterPotionEffect, waterPotionFogStartDistance, waterPotionFogEndDistance, overrideWaterFogColor, waterFogColor);
        }

        List<AbstractConfigListEntry<?>> configListEntries = new ArrayList<>();
        configListEntries.addAll(waterSettingEntries);
        configListEntries.addAll(lavaSettingEntries);
        configListEntries.addAll(settingEntries);

        for (AbstractConfigListEntry<?> entry : configListEntries) {
            entry.appendSearchTags(List.of(name));
        }

        waterSettings.addAll(waterSettingEntries);
        lavaSettings.addAll(lavaSettingEntries);
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
