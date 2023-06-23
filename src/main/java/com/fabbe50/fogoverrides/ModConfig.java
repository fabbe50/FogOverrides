package com.fabbe50.fogoverrides;

import com.fabbe50.fogoverrides.handlers.GuiHandler;
import com.fabbe50.fogoverrides.holders.*;
import com.fabbe50.fogoverrides.holders.data.BiomeData;
import com.fabbe50.fogoverrides.holders.data.IHolder;
import com.fabbe50.fogoverrides.holders.data.IOverrideHolder;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;
import me.shedaniel.clothconfig2.gui.entries.StringListListEntry;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import me.shedaniel.clothconfig2.impl.builders.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.util.*;
import java.util.function.Supplier;

public class ModConfig {
    public static File configFile;
    
    static ConfigBuilder builder;
    static ConfigEntryBuilder entryBuilder;

    static String biomeResourceKey = "";
    private int selectedCategory = 0;
    private double listWidgetScroll = 0;
    private int mouseX = -1;
    private int mouseY = -1;
    private static Map<Component, Boolean> subCatExpanded = new HashMap<>();

    //TODO: Re-add block-fog functionality. I.E. Frostbite etc.


    public Screen init(Screen parent) {
        builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("title.fogoverrides.config"));
        if (parent == null) {
            builder.setTransparentBackground(true);
        }
        builder.setAfterInitConsumer(screen -> {
            if (screen instanceof ClothConfigScreen configScreen) {
                int buttonWidth = Math.min(200, (screen.width) / 4);
                Renderable toRemove = null;
                for (Renderable renderable : configScreen.renderables) {
                    if (renderable instanceof Button button) {
                        if (button.getMessage().equals(Component.empty())) {
                            button.active = false;
                            toRemove = renderable;
                        }
                        if (button.getMessage().getContents() instanceof TranslatableContents contents) {
                            if (contents.getKey().equals("text.cloth-config.cancel_discard") || contents.getKey().equals("gui.cancel")) {
                                button.setWidth(buttonWidth);
                                button.setX((int)(configScreen.width / 2 - (buttonWidth * 1.5) - 3));
                            }
                        }
                    }
                }
                if (toRemove != null) {
                    configScreen.renderables.remove(toRemove);
                }

                configScreen.addRenderableWidget(new SaveButton((int)(configScreen.width / 2 - (buttonWidth * 0.5)), configScreen.height - 26, buttonWidth, 20, Component.empty(), (button) -> {
                    saveCurrentScreenState(configScreen);
                    configScreen.saveAll(true);
                    configScreen.getMinecraft().setScreen(init(parent));
                }, Supplier::get) {
                    @Override
                    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float delta) {
                        boolean hasErrors = false;
                        for (List<AbstractConfigEntry<?>> entries : Lists.newArrayList(configScreen.getCategorizedEntries().values())) {
                            for (AbstractConfigEntry<?> entry : entries) {
                                if (entry.getConfigError().isPresent()) {
                                    hasErrors = true;
                                    break;
                                }
                            }
                            if (hasErrors) {
                                break;
                            }
                        }
                        this.active = configScreen.isEdited() && !hasErrors;
                        this.setMessage(hasErrors ? Component.translatable("text.cloth-config.error_cannot_save") : Component.translatable("option.fogoverrides.setting.save"));
                        super.render(poseStack, mouseX, mouseY, delta);
                    }
                });
                configScreen.addRenderableWidget(new SaveButton((int)(configScreen.width / 2 + (buttonWidth * 0.5) + 3), configScreen.height - 26, buttonWidth, 20, Component.empty(), (button) -> {
                    saveCurrentScreenState(configScreen);
                    configScreen.saveAll(true);
                }, Supplier::get) {
                    @Override
                    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float delta) {
                        boolean hasErrors = false;
                        for (List<AbstractConfigEntry<?>> entries : Lists.newArrayList(configScreen.getCategorizedEntries().values())) {
                            for (AbstractConfigEntry<?> entry : entries) {
                                if (entry.getConfigError().isPresent()) {
                                    hasErrors = true;
                                    break;
                                }
                            }
                            if (hasErrors) {
                                break;
                            }
                        }
                        this.active = configScreen.isEdited() && !hasErrors;
                        this.setMessage(hasErrors ? Component.translatable("text.cloth-config.error_cannot_save") : Component.translatable("text.cloth-config.save_and_done"));
                        super.render(poseStack, mouseX, mouseY, delta);
                    }
                });

                if (mouseX == -1 && mouseY == -1) {
                    mouseX = (int) configScreen.getMinecraft().mouseHandler.xpos();
                    mouseY = (int) configScreen.getMinecraft().mouseHandler.ypos();
                }
                GLFW.glfwSetCursorPos(configScreen.getMinecraft().getWindow().getWindow(), mouseX, mouseY);

                configScreen.listWidget.scrollTo(listWidgetScroll, false);
                if (configScreen.listWidget.getScroll() == 0 && listWidgetScroll != 0) { //Dumb fix for nested subcategories.
                    new Thread(() -> {
                        try {
                            Thread.sleep(20);
                            configScreen.listWidget.scrollTo(listWidgetScroll, false);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                }
            }
        });

        entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("category.fogoverrides.general"));
        general.addEntry(entryBuilder.startKeyCodeField(Component.translatable("key.fogoverrides.config_menu"), GuiHandler.OPEN_SETTINGS.getKey())
                .setDefaultValue(InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_F8))
                .setErrorSupplier(key -> Arrays.stream(Minecraft.getInstance().options.keyMappings)
                        .anyMatch(keyMapping -> keyMapping.isActiveAndMatches(key)) ? Optional.of(Component.translatable("key.fogoverrides.config_menu.inuse")) : Optional.empty()
                )
                .setKeySaveConsumer(GuiHandler.OPEN_SETTINGS::setKey)
                .build());
        general.addEntry(entryBuilder.startEnumSelector(Component.translatable("option.fogoverrides.general.creative-overrides"), ConfigHolder.General.CreativeModeSettings.class, ConfigHolder.getGeneral().getCreativeModeSettings())
                .setDefaultValue(ConfigHolder.General.CreativeModeSettings.OVERRIDE)
                .setTooltip(
                        Component.translatable("option.fogoverrides.general.creative-overrides.tooltip[0]"),
                        Component.translatable("option.fogoverrides.general.creative-overrides.tooltip[1]"),
                        Component.translatable("option.fogoverrides.general.creative-overrides.tooltip[2]"),
                        Component.translatable("option.fogoverrides.general.creative-overrides.tooltip[3]")
                )
                .setSaveConsumer(creativeModeSettings -> ConfigHolder.getGeneral().setCreativeModeSettings(creativeModeSettings))
                .build());
        general.addEntry(createBooleanConfigOption(entryBuilder, "general", "adjust-distance", ConfigHolder.getGeneral().isAdjustDistance(), true)
                .setYesNoTextSupplier(aBoolean -> {
                    if (aBoolean) {
                        return Component.translatable("option.fogoverrides.setting.enabled").withStyle(ChatFormatting.GREEN);
                    } else {
                        return Component.translatable("option.fogoverrides.setting.disabled").withStyle(ChatFormatting.RED);
                    }
                })
                .setSaveConsumer(aBoolean -> ConfigHolder.getGeneral().setAdjustDistance(aBoolean))
                .build());
        general.addEntry(createBooleanConfigOption(entryBuilder, "general", "adjust-colors", ConfigHolder.getGeneral().isAdjustColors(), false)
                .setYesNoTextSupplier(aBoolean -> {
                    if (aBoolean) {
                        return Component.translatable("option.fogoverrides.setting.enabled").withStyle(ChatFormatting.GREEN);
                    } else {
                        return Component.translatable("option.fogoverrides.setting.disabled").withStyle(ChatFormatting.RED);
                    }
                })
                .setSaveConsumer(aBoolean -> ConfigHolder.getGeneral().setAdjustColors(aBoolean))
                .build());
        general.addEntry(createBooleanConfigOption(entryBuilder, "special", "potion-affects-vision", ConfigHolder.getSpecial().doesPotionAffectVision(), true)
                .setSaveConsumer(aBoolean -> ConfigHolder.getSpecial().setPotionAffectsVision(aBoolean))
                .build());
        Component subCatOverlayComponent = Component.translatable("subcategory.fogoverrides.overlay");
        SubCategoryBuilder subCatOverlay = entryBuilder.startSubCategory(subCatOverlayComponent).setExpanded(subCatExpanded.getOrDefault(subCatOverlayComponent, false));
        BooleanListEntry disableFireOverlay = (createBooleanConfigOption(entryBuilder, "special", "fire-overlay-enabled", ConfigHolder.getSpecial().isDisableFireOverlay(), true)
                .setSaveConsumer(aBoolean -> ConfigHolder.getSpecial().setDisableFireOverlay(aBoolean))
                .build());
        IntegerSliderEntry fireOverlayOffset = (createIntSliderConfigOption(entryBuilder, "special", "fire-overlay-offset", ConfigHolder.getSpecial().getFireOverlayOffset(), -25, -100, 100)
                .setSaveConsumer(integer -> ConfigHolder.getSpecial().setFireOverlayOffset(integer))
                .build());
        BooleanListEntry disableWaterOverlay = (createBooleanConfigOption(entryBuilder, "special", "water-overlay-enabled", ConfigHolder.getSpecial().isDisableWaterOverlay(), true)
                .setSaveConsumer(aBoolean -> ConfigHolder.getSpecial().setDisableWaterOverlay(aBoolean))
                .build());
        subCatOverlay.addAll(Lists.newArrayList(disableFireOverlay, fireOverlayOffset, disableWaterOverlay));
        general.addEntry(subCatOverlay.build());
        Component subCatCloudsComponent = Component.translatable("subcategory.fogoverrides.clouds");
        SubCategoryBuilder subCatClouds = entryBuilder.startSubCategory(subCatCloudsComponent).setExpanded(subCatExpanded.getOrDefault(subCatCloudsComponent, false));
        IntegerSliderEntry cloudHeight = (createIntSliderConfigOption(entryBuilder, "general", "cloud-height", (int) ConfigHolder.getGeneral().getCloudHeight(), 192, -64, 319)
                .setSaveConsumer(integer -> ConfigHolder.getGeneral().setCloudHeight(integer))
                .build());
        BooleanListEntry cloudColorOverride = (createBooleanConfigOption(entryBuilder, "general", "cloud-color-override", ConfigHolder.getGeneral().isAdjustCloudColors(), false)
                .setSaveConsumer(aBoolean -> ConfigHolder.getGeneral().setAdjustCloudColors(aBoolean))
                .setYesNoTextSupplier(aBoolean -> {
                    if (aBoolean) {
                        return Component.translatable("option.fogoverrides.setting.enabled").withStyle(ChatFormatting.GREEN);
                    } else {
                        return Component.translatable("option.fogoverrides.setting.disabled").withStyle(ChatFormatting.RED);
                    }
                })
                .build());
        IntegerSliderEntry cloudColorBlend = (createIntSliderConfigOption(entryBuilder, "general", "cloud-color-blend", ConfigHolder.getGeneral().getCloudColorBlendRatio(), 50, 0, 100)
                .setTooltip(
                        Component.translatable("option.fogoverrides.general.cloud-color-blend.tooltip[0]"),
                        Component.translatable("option.fogoverrides.general.cloud-color-blend.tooltip[1]")
                )
                .setSaveConsumer(integer -> ConfigHolder.getGeneral().setCloudColorBlendRatio(integer))
                .build());
        subCatClouds.addAll(Arrays.asList(cloudHeight, cloudColorOverride, cloudColorBlend));
        general.addEntry(subCatClouds.build());

        Component subCatVoidFogComponent = Component.translatable("subcategory.fogoverrides.effects.void");
        SubCategoryBuilder voidFog = entryBuilder.startSubCategory(subCatVoidFogComponent).setExpanded(subCatExpanded.getOrDefault(subCatVoidFogComponent, false));
        BooleanListEntry voidFogEnabled = (createBooleanConfigOption(entryBuilder, "fog-enabled", ConfigHolder.getVoid_().isEnableVoidFog(), false)
                .setSaveConsumer(aBoolean -> ConfigHolder.getVoid_().setEnableVoidFog(aBoolean))
                .setYesNoTextSupplier(aBoolean -> {
                    if (aBoolean) {
                        return Component.translatable("option.fogoverrides.setting.enabled").withStyle(ChatFormatting.GREEN);
                    } else {
                        return Component.translatable("option.fogoverrides.setting.disabled").withStyle(ChatFormatting.RED);
                    }
                })
                .build());
        IntegerSliderEntry voidFogStartDistance = (createIntSliderConfigOption(entryBuilder, "fog-start-distance", ConfigHolder.getVoid_().getVoidFogStartDistance(), 7, 0, 511)
                .setSaveConsumer(integer -> ConfigHolder.getVoid_().setVoidFogStartDistance(integer))
                .setTextGetter(integer -> Component.translatable("option.fogoverrides.fog-start-distance.text", integer))
                .build());
        IntegerSliderEntry voidFogEndDistance = (createIntSliderConfigOption(entryBuilder, "fog-end-distance", ConfigHolder.getVoid_().getVoidFogEndDistance(), 80, 1, 512)
                .setSaveConsumer(integer -> ConfigHolder.getVoid_().setVoidFogEndDistance(integer))
                .setTextGetter(integer -> Component.translatable("option.fogoverrides.fog-end-distance.text", integer))
                .build());
        IntegerSliderEntry voidFogYLevelActivate = (createIntSliderConfigOption(entryBuilder, "void", "y-level-activate", ConfigHolder.getVoid_().getyLevelActivate(), 0, -64, 319)
                .setTextGetter(integer -> Component.translatable("option.fogoverrides.void.y-level-activate.text", integer))
                .setSaveConsumer(integer -> ConfigHolder.getVoid_().setyLevelActivate(integer))
                .build());
        BooleanListEntry voidParticlesEnabled = (createBooleanConfigOption(entryBuilder, "void", "enable-void-particles", ConfigHolder.getVoid_().isEnableVoidParticles(), false)
                .setSaveConsumer(aBoolean -> ConfigHolder.getVoid_().setEnableVoidParticles(aBoolean))
                .setYesNoTextSupplier(aBoolean -> {
                    if (aBoolean) {
                        return Component.translatable("option.fogoverrides.setting.enabled").withStyle(ChatFormatting.GREEN);
                    } else {
                        return Component.translatable("option.fogoverrides.setting.disabled").withStyle(ChatFormatting.RED);
                    }
                })
                .build());
        BooleanListEntry voidFogAffectedBySkyLight = (createBooleanConfigOption(entryBuilder, "void", "affected-by-sky-light", ConfigHolder.getVoid_().isVoidFogAffectedBySkylight(), true)
                .setSaveConsumer(aBoolean -> ConfigHolder.getVoid_().setVoidFogAffectedBySkylight(aBoolean))
                .build());
        voidFog.addAll(Lists.newArrayList(voidFogEnabled, voidFogStartDistance, voidFogEndDistance, voidFogYLevelActivate, voidParticlesEnabled, voidFogAffectedBySkyLight));
        voidFogStartDistance.setErrorSupplier(() -> {
            if (voidFogStartDistance.getValue() >= voidFogEndDistance.getValue()) {
                return Optional.of(Component.translatable("option.fogoverrides.fog-distance.error"));
            } else {
                return Optional.empty();
            }
        });
        voidFogEndDistance.setErrorSupplier(() -> {
            if (voidFogStartDistance.getValue() >= voidFogEndDistance.getValue()) {
                return Optional.of(Component.translatable("option.fogoverrides.fog-distance.error"));
            } else {
                return Optional.empty();
            }
        });
        voidFogStartDistance.requestReferenceRebuilding();
        voidFogEndDistance.requestReferenceRebuilding();

        ConfigCategory fogs = builder.getOrCreateCategory(Component.translatable("category.fogoverrides.fogs"));
        Component subCatVanillaDimensionsComponent = Component.translatable("subcategory.fogoverrides.vanilla-dimensions");
        SubCategoryBuilder vanillaDimensions = entryBuilder.startSubCategory(subCatVanillaDimensionsComponent).setExpanded(subCatExpanded.getOrDefault(subCatVanillaDimensionsComponent, true));
        for (Dimensions dim : Dimensions.values()) {
            if (dim.showOnConfig()) {
                SubCategoryBuilder subCat = buildFogSubCategory(dim);
                if (dim == Dimensions.OVERWORLD)
                    subCat.add(voidFog.build());
                vanillaDimensions.add(subCat.build());
            }
        }
        fogs.addEntry(vanillaDimensions.build());

        Component subCatVanillaLiquidsComponent = Component.translatable("subcategory.fogoverrides.vanilla-liquids");
        SubCategoryBuilder vanillaLiquids = entryBuilder.startSubCategory(subCatVanillaLiquidsComponent).setExpanded(subCatExpanded.getOrDefault(subCatVanillaLiquidsComponent, false));
        for (Liquids liquid : Liquids.values()) {
            if (liquid.showOnConfig()) {
                vanillaLiquids.add(buildFogSubCategory(liquid).build());
            }
        }
        fogs.addEntry(vanillaLiquids.build());

        Component subCatVanillaPotionsComponent = Component.translatable("subcategory.fogoverrides.vanilla-potions");
        SubCategoryBuilder vanillaPotions = entryBuilder.startSubCategory(subCatVanillaPotionsComponent).setExpanded(subCatExpanded.getOrDefault(subCatVanillaPotionsComponent, false));
        for (PotionEffects potionEffects : PotionEffects.values()) {
            if (potionEffects.showOnConfig()) {
                vanillaPotions.add(buildFogSubCategory(potionEffects).build());
            }
        }
        fogs.addEntry(vanillaPotions.build());

        ConfigCategory biomeOverrides = builder.getOrCreateCategory(Component.translatable("category.fogoverrides.biome-overrides"));
        Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
        if (cameraEntity instanceof Player player) {
            Level level = player.getLevel();
            if (level != null) {
                biomeResourceKey = level.getBiome(player.getOnPos()).unwrapKey().orElse(Biomes.PLAINS).location().toString();
                Component biomeComponent = Component.translatable("biome." + biomeResourceKey.replace(":", "."));
                TextDescriptionBuilder currentBiome = entryBuilder.startTextDescription(Component.translatable("biome.fogoverrides.current", biomeComponent, Component.literal("[\"" + biomeResourceKey + "\"]")));
                biomeOverrides.addEntry(currentBiome.build());
            }
        }

        biomeOverrides.addEntry(entryBuilder.startStrList(Component.translatable("option.fogoverrides.biomes-to-override"), BiomeHolder.getBiomesToOverrideConfig())
                .setTooltip(Component.translatable("option.fogoverrides.biomes-to-override.tooltip"))
                .setCreateNewInstance(stringListListEntry -> new StringListListEntry.StringListCell(biomeResourceKey.equals("") ? "minecraft:" : biomeResourceKey, stringListListEntry))
                .setSaveConsumer(strings -> {
                    BiomeHolder.setBiomesToOverrideConfig(strings);
                    BiomeHolder.updateBiomeData();
                })
                .build());
        for (BiomeData biome : BiomeHolder.getBiomeDataList()) {
            if (biome.showOnConfig()) {
                biomeOverrides.addEntry(buildFogSubCategory(biome).build());
            }
        }

        Screen screen = builder.setSavingRunnable(() -> {
            try {
                saveConfig(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadConfig(configFile);
        }).build();
        if (screen instanceof ClothConfigScreen configScreen) {
            configScreen.selectedCategoryIndex = selectedCategory;
            return configScreen;
        }
        return screen;
    }

    private void saveCurrentScreenState(ClothConfigScreen configScreen) {
        selectedCategory = configScreen.selectedCategoryIndex;
        listWidgetScroll = configScreen.listWidget.getScroll();
        mouseX = (int) configScreen.getMinecraft().mouseHandler.xpos();
        mouseY = (int) configScreen.getMinecraft().mouseHandler.ypos();
        System.out.println("Current Scroll: " + (listWidgetScroll) + "/" + (configScreen.listWidget.height) + " (" + configScreen.listWidget.getScrollBottom() + ") ");
        subCatExpanded = new HashMap<>();
        for (List<AbstractConfigEntry<?>> entries : configScreen.getCategorizedEntries().values()) {
            for (AbstractConfigEntry<?> entry : entries) {
                saveRecursiveEntryState(entry);
            }
        }
    }

    private void saveRecursiveEntryState(AbstractConfigEntry<?> entry) {
        if (entry instanceof SubCategoryListEntry subCatEntry) {
            subCatExpanded.put(subCatEntry.getFieldName(), subCatEntry.isExpanded());
            for (AbstractConfigEntry<?> subEntry : subCatEntry.getValue()) {
                saveRecursiveEntryState(subEntry);
            }
        }
    }

    private static SubCategoryBuilder buildFogSubCategory(IHolder holder) {
        Component subCatFogComponent = Component.translatable(holder.getTranslationKey().equals("") ? "subcategory.fogoverrides." + holder.getHolderType() + "." + holder.getName() : holder.getTranslationKey());
        SubCategoryBuilder subCategory = entryBuilder.startSubCategory(subCatFogComponent).setExpanded(subCatExpanded.getOrDefault(subCatFogComponent, false));
        BooleanToggleBuilder fogEnabled = createBooleanConfigOption(entryBuilder, "fog-enabled", holder.isEnabled(), true)
                .setYesNoTextSupplier(aBoolean -> {
                            if (aBoolean) {
                                return Component.translatable("option.fogoverrides.setting.enabled").withStyle(ChatFormatting.GREEN);
                            } else {
                                return Component.translatable("option.fogoverrides.setting.disabled").withStyle(ChatFormatting.RED);
                            }
                        })
                .setSaveConsumer(holder::setEnabled);
        IntegerSliderEntry fogStartDistance = createIntSliderConfigOption(entryBuilder, "fog-start-distance", holder.getStartDistance(), holder.getDefaultStartDistance(), holder.getMinDistance(), holder.getMaxDistance() - 1)
                .setTextGetter(integer -> Component.translatable("option.fogoverrides.fog-start-distance.text", integer))
                .setSaveConsumer(holder::setStartDistance)
                .build();
        IntegerSliderEntry fogEndDistance = createIntSliderConfigOption(entryBuilder, "fog-end-distance", holder.getEndDistance(), holder.getDefaultEndDistance(), holder.getMinDistance() + 1, holder.getMaxDistance())
                .setTextGetter(integer -> Component.translatable("option.fogoverrides.fog-end-distance.text", integer))
                .setSaveConsumer(holder::setEndDistance)
                .build();
        BooleanToggleBuilder fogOverrideColor = createBooleanConfigOption(entryBuilder, "override-fog-color", holder.shouldOverrideColor(), holder.getDefaultShouldOverrideColor())
                .setSaveConsumer(holder::setOverrideColor);
        ColorFieldBuilder fogColor = createColorFieldConfigOption(entryBuilder, "", "fog-color", holder.getColor(), holder.getDefaultColor())
                .setSaveConsumer(holder::setColor);
        if (holder instanceof IOverrideHolder overrideHolder) {
            IntSliderBuilder fogColorBlendLevel = createIntSliderConfigOption(entryBuilder, "fog-color-blend", overrideHolder.getBlendPercentage(), overrideHolder.getDefaultBlendPercentage(), 0, 100)
                    .setTextGetter(integer -> Component.translatable("option.fogoverrides.fog-color-blend.text", integer))
                    .setSaveConsumer(overrideHolder::setBlendPercentage);
            subCategory.addAll(Lists.newArrayList(fogEnabled.build(), fogStartDistance, fogEndDistance, fogOverrideColor.build(), fogColor.build(), fogColorBlendLevel.build()));
        } else {
            subCategory.addAll(Lists.newArrayList(fogEnabled.build(), fogStartDistance, fogEndDistance, fogOverrideColor.build(), fogColor.build()));
        }
        fogStartDistance.setErrorSupplier(() -> {
            if (fogStartDistance.getValue() >= fogEndDistance.getValue()) {
                return Optional.of(Component.translatable("option.fogoverrides.fog-distance.error"));
            } else {
                return Optional.empty();
            }
        });
        fogEndDistance.setErrorSupplier(() -> {
            if (fogStartDistance.getValue() >= fogEndDistance.getValue()) {
                return Optional.of(Component.translatable("option.fogoverrides.fog-distance.error"));
            } else {
                return Optional.empty();
            }
        });
        fogStartDistance.requestReferenceRebuilding();
        fogEndDistance.requestReferenceRebuilding();
        return subCategory;
    }

    private static BooleanToggleBuilder createBooleanConfigOption(ConfigEntryBuilder entryBuilder, String optionName, boolean value, boolean defaultValue) {
        return createBooleanConfigOption(entryBuilder, "", optionName, value, defaultValue);
    }

    private static BooleanToggleBuilder createBooleanConfigOption(ConfigEntryBuilder entryBuilder, String categoryName, String optionName, boolean value, boolean defaultValue) {
        String optionTranslationKey = "option.fogoverrides." + categoryName + (categoryName.equals("") ? "" : ".") + optionName;
        return entryBuilder.startBooleanToggle(Component.translatable(optionTranslationKey), value)
                .setDefaultValue(defaultValue)
                .setTooltip(Component.translatable(optionTranslationKey + ".tooltip"));
    }

    private static IntSliderBuilder createIntSliderConfigOption(ConfigEntryBuilder entryBuilder, String optionName, int value, int defaultValue, int min, int max) {
        return createIntSliderConfigOption(entryBuilder, "", optionName, value, defaultValue, min, max);
    }

    private static IntSliderBuilder createIntSliderConfigOption(ConfigEntryBuilder entryBuilder, String categoryName, String optionName, int value, int defaultValue, int min, int max) {
        String optionTranslationKey = "option.fogoverrides." + categoryName + (categoryName.equals("") ? "" : ".") + optionName;
        return entryBuilder.startIntSlider(Component.translatable(optionTranslationKey), value, min, max)
                .setDefaultValue(defaultValue)
                .setTooltip(Component.translatable(optionTranslationKey + ".tooltip"));
    }

    private static ColorFieldBuilder createColorFieldConfigOption(ConfigEntryBuilder entryBuilder, String categoryName, String optionName, int value, int defaultValue) {
        String optionTranslationKey = "option.fogoverrides." + categoryName + (categoryName.equals("") ? "" : ".") + optionName;
        return entryBuilder.startColorField(Component.translatable(optionTranslationKey), value)
                .setDefaultValue(defaultValue)
                .setTooltip(Component.translatable(optionTranslationKey + ".tooltip"));
    }

    public static void register() {
        configFile = new File(FMLPaths.CONFIGDIR.get().toFile(), "fogoverrides.properties");
        loadConfig(configFile);
    }

    private static Properties properties;
    public static void loadConfig(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            properties = new Properties();
            properties.load(fis);
            fis.close();

            //General Config
            ConfigHolder.getGeneral().setCreativeModeSettings(ConfigHolder.General.CreativeModeSettings.getFromID(readInt("creativeOverrides", 3)));
            ConfigHolder.getGeneral().setAdjustDistance(readBoolean("adjustDistance", false));
            ConfigHolder.getGeneral().setAdjustColors(readBoolean("adjustColors", false));
            ConfigHolder.getGeneral().setCloudHeight(readInt("cloudHeight", 192));
            ConfigHolder.getGeneral().setAdjustCloudColors(readBoolean("adjustCloudColor", false));
            ConfigHolder.getGeneral().setCloudColorBlendRatio(readInt("cloudColorBlendRatio", 50));

            //Special Config
            ConfigHolder.getSpecial().setPotionAffectsVision(readBoolean("potionAffectsVision", true));
            ConfigHolder.getSpecial().setDisableFireOverlay(readBoolean("enableFireOverlay", true));
            ConfigHolder.getSpecial().setFireOverlayOffset(readInt("fireOverlayOffset", -25));
            ConfigHolder.getSpecial().setDisableWaterOverlay(readBoolean("enableWaterOverlay", true));

            //Void Config
            ConfigHolder.getVoid_().setEnableVoidFog(readBoolean("enableVoidFog", false));
            ConfigHolder.getVoid_().setVoidFogStartDistance(readInt("voidFogStartDistance", 7));
            ConfigHolder.getVoid_().setVoidFogEndDistance(readInt("voidFogEndDistance", 80));
            ConfigHolder.getVoid_().setyLevelActivate(readInt("yLevelActivate", 0));
            ConfigHolder.getVoid_().setEnableVoidParticles(readBoolean("enableVoidParticles", false));
            ConfigHolder.getVoid_().setVoidFogAffectedBySkylight(readBoolean("voidFogAffectedBySkylight", true));

            //Dimension Config
            for (Dimensions dim : Dimensions.values()) {
                if (dim.showOnConfig()) {
                    readFogConfig(dim, true);
                }
            }

            //Liquid Config
            for (Liquids liquid : Liquids.values()) {
                if (liquid.showOnConfig()) {
                    readFogConfig(liquid, true);
                }
            }

            for (PotionEffects potionEffects : PotionEffects.values()) {
                if (potionEffects.showOnConfig()) {
                    readFogConfig(potionEffects, true);
                }
            }

            BiomeHolder.setBiomesToOverrideConfig(readStringList("biome_override_list", BiomeHolder.getBiomesToOverrideConfig()));
            BiomeHolder.updateBiomeData();
            //Biome Overrides Config
            for (BiomeData biome : BiomeHolder.getBiomeDataList()) {
                if (biome.showOnConfig()) {
                    readFogConfig(biome, false);
                }
            }

            properties = null;
        } catch (IOException e) {
            e.printStackTrace();
            ConfigHolder.getGeneral().setCreativeModeSettings(ConfigHolder.General.CreativeModeSettings.OVERRIDE);
            ConfigHolder.getGeneral().setAdjustDistance(true);
            ConfigHolder.getGeneral().setAdjustColors(false);
            ConfigHolder.getGeneral().setCloudHeight(192);
            ConfigHolder.getGeneral().setAdjustCloudColors(false);
            ConfigHolder.getGeneral().setCloudColorBlendRatio(50);

            ConfigHolder.getSpecial().setPotionAffectsVision(true);
            ConfigHolder.getSpecial().setDisableFireOverlay(true);
            ConfigHolder.getSpecial().setFireOverlayOffset(-25);
            ConfigHolder.getSpecial().setDisableWaterOverlay(true);

            ConfigHolder.getVoid_().setEnableVoidFog(false);
            ConfigHolder.getVoid_().setVoidFogStartDistance(7);
            ConfigHolder.getVoid_().setVoidFogEndDistance(80);
            ConfigHolder.getVoid_().setyLevelActivate(0);
            ConfigHolder.getVoid_().setEnableVoidParticles(false);
            ConfigHolder.getVoid_().setVoidFogAffectedBySkylight(true);

            for (Dimensions dim : Dimensions.values()) {
                if (dim.showOnConfig()) {
                    readFogDefaultConfig(dim, true);
                }
            }

            for (Liquids liquid : Liquids.values()) {
                if (liquid.showOnConfig()) {
                    readFogDefaultConfig(liquid, true);
                }
            }

            for (PotionEffects potionEffects : PotionEffects.values()) {
                if (potionEffects.showOnConfig()) {
                    readFogDefaultConfig(potionEffects, true);
                }
            }

            BiomeHolder.setBiomesToOverrideConfig(new ArrayList<>());
            for (BiomeData biome : BiomeHolder.getBiomeDataList()) {
                if (biome.showOnConfig()) {
                    readFogDefaultConfig(biome, false);
                }
            }
        }
    }

    private static void readFogConfig(IHolder holder, boolean defaultEnabled) {
        holder.setEnabled(readBoolean(holder.getHolderType() + "_" + holder.getName() + "_enabled", defaultEnabled));
        holder.setStartDistance(readInt(holder.getHolderType() + "_" + holder.getName() + "_start_distance", holder.getDefaultStartDistance()));
        holder.setEndDistance(readInt(holder.getHolderType() + "_" + holder.getName() + "_end_distance", holder.getDefaultEndDistance()));
        holder.setOverrideColor(readBoolean(holder.getHolderType() + "_" + holder.getName() + "_override_color", holder.getDefaultShouldOverrideColor()));
        holder.setColor(readInt(holder.getHolderType() + "_" + holder.getName() + "_color", holder.getDefaultColor()));
        if (holder instanceof IOverrideHolder overrideHolder) {
            overrideHolder.setBlendPercentage(readInt(holder.getHolderType() + "_" + holder.getName() + "_blend_percentage", overrideHolder.getDefaultBlendPercentage()));
        }
    }

    private static void readFogDefaultConfig(IHolder holder, boolean enabled) {
        holder.setEnabled(enabled);
        holder.setStartDistance(holder.getDefaultStartDistance());
        holder.setEndDistance(holder.getDefaultEndDistance());
        holder.setOverrideColor(holder.getDefaultShouldOverrideColor());
        holder.setColor(holder.getDefaultColor());
        if (holder instanceof IOverrideHolder overrideHolder) {
            overrideHolder.setBlendPercentage(overrideHolder.getDefaultBlendPercentage());
        }
    }

    private static boolean readBoolean(String key, boolean defaultValue) {
        return ((String) properties.computeIfAbsent(key, o -> String.valueOf(defaultValue))).equalsIgnoreCase("true");
    }

    private static int readInt(String key, int defaultValue) {
        return Integer.parseInt((String)properties.computeIfAbsent(key, o -> String.valueOf(defaultValue)));
    }

    private static List<String> readStringList(String key, List<String> defaultList) {
        List<String> list = Arrays.asList(properties.computeIfAbsent(key, o -> Arrays.toString(defaultList.toArray())).toString()
                .replace("[", "")
                .replace("]", "")
                .replaceAll(" ", "")
                .split(","));
        return list.isEmpty() || list.get(0).equals("") ? new ArrayList<>() : list;
    }

    public static void saveConfig(File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, false);
        writeData(fos, "creativeOverrides", ConfigHolder.getGeneral().getCreativeModeSettings().getId());
        writeNewLine(fos);
        writeData(fos, "adjustDistance", ConfigHolder.getGeneral().isAdjustDistance());
        writeNewLine(fos);
        writeData(fos, "adjustColors", ConfigHolder.getGeneral().isAdjustColors());
        writeNewLine(fos);
        writeData(fos, "cloudHeight", (int) ConfigHolder.getGeneral().getCloudHeight());
        writeNewLine(fos);
        writeData(fos, "adjustCloudColor", ConfigHolder.getGeneral().isAdjustCloudColors());
        writeNewLine(fos);
        writeData(fos, "cloudColorBlendRatio", ConfigHolder.getGeneral().getCloudColorBlendRatio());
        writeNewLine(fos);
        writeData(fos, "potionAffectsVision", ConfigHolder.getSpecial().doesPotionAffectVision());
        writeNewLine(fos);
        writeData(fos, "enableFireOverlay", ConfigHolder.getSpecial().isDisableFireOverlay());
        writeNewLine(fos);
        writeData(fos, "fireOverlayOffset", ConfigHolder.getSpecial().getFireOverlayOffset());
        writeNewLine(fos);
        writeData(fos, "enableWaterOverlay", ConfigHolder.getSpecial().isDisableWaterOverlay());
        writeNewLine(fos);
        writeData(fos, "enableVoidFog", ConfigHolder.getVoid_().isEnableVoidFog());
        writeNewLine(fos);
        writeData(fos, "voidFogStartDistance", ConfigHolder.getVoid_().getVoidFogStartDistance());
        writeNewLine(fos);
        writeData(fos, "voidFogEndDistance", ConfigHolder.getVoid_().getVoidFogEndDistance());
        writeNewLine(fos);
        writeData(fos, "yLevelActivate", ConfigHolder.getVoid_().getyLevelActivate());
        writeNewLine(fos);
        writeData(fos, "enableVoidParticles", ConfigHolder.getVoid_().isEnableVoidParticles());
        writeNewLine(fos);
        writeData(fos, "voidFogAffectedBySkylight", ConfigHolder.getVoid_().isVoidFogAffectedBySkylight());
        writeNewLine(fos);
        for (Dimensions dim : Dimensions.values()) {
            if (dim.showOnConfig()) {
                writeConfig(fos, dim);
            }
        }
        for (Liquids liquid : Liquids.values()) {
            if (liquid.showOnConfig()) {
                writeConfig(fos, liquid);
            }
        }
        for (PotionEffects potionEffects : PotionEffects.values()) {
            if (potionEffects.showOnConfig()) {
                writeConfig(fos, potionEffects);
            }
        }
        writeData(fos, "biome_override_list", Arrays.toString(BiomeHolder.getBiomesToOverrideConfig().toArray()));
        writeNewLine(fos);
        for (BiomeData biome : BiomeHolder.getBiomeDataList()) {
            if (biome.showOnConfig()) {
                writeConfig(fos, biome);
            }
        }
        fos.close();
    }

    private static void writeConfig(FileOutputStream fos, IHolder holder) throws IOException {
        writeData(fos, holder.getHolderType() + "_" + holder.getName() + "_enabled", holder.isEnabled());
        writeNewLine(fos);
        writeData(fos, holder.getHolderType() + "_" + holder.getName() + "_start_distance", holder.getStartDistance());
        writeNewLine(fos);
        writeData(fos, holder.getHolderType() + "_" + holder.getName() + "_end_distance", holder.getEndDistance());
        writeNewLine(fos);
        writeData(fos, holder.getHolderType() + "_" + holder.getName() + "_override_color", holder.shouldOverrideColor());
        writeNewLine(fos);
        writeData(fos, holder.getHolderType() + "_" + holder.getName() + "_color", holder.getColor());
        writeNewLine(fos);
        if (holder instanceof IOverrideHolder overrideHolder) {
            writeData(fos, holder.getHolderType() + "_" + holder.getName() + "_blend_percentage", overrideHolder.getBlendPercentage());
            writeNewLine(fos);
        }
    }

    private static void writeData(FileOutputStream fos, String key, Object value) throws IOException {
        fos.write((key + "=" + value).getBytes());
    }

    private static void writeNewLine(FileOutputStream fos) throws IOException {
        fos.write("\n".getBytes());
    }

    private static class SaveButton extends Button {
        public SaveButton(int x, int y, int width, int height, Component buttonText, OnPress onPress, CreateNarration createNarration) {
            super(x, y, width, height, buttonText, onPress, createNarration);
        }
    }
}
