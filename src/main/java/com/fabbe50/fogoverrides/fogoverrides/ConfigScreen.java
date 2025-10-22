package com.fabbe50.fogoverrides.fogoverrides;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ConfigScreen extends Screen {

    public ConfigScreen(Screen parent) {
        super(Component.empty());
    }

    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawStringWithBackdrop(this.font,
                Component.literal("Hello, world"),
                width / 2,
                height / 2,
                0xFFFFFFFF,
                0xFFFFFFFF);
    }

    public static ConfigScreen createConfigScreen(Screen parent) {
        return new ConfigScreen(parent);
    }
}
