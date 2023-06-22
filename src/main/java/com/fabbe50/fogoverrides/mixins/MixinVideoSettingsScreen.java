package com.fabbe50.fogoverrides.mixins;

import com.fabbe50.fogoverrides.FogOverrides;
import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.handlers.GuiHandler;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VideoSettingsScreen.class)
public class MixinVideoSettingsScreen extends OptionsSubScreen {
    public MixinVideoSettingsScreen(Screen p_96284_, Options p_96285_, Component p_96286_) {
        super(p_96284_, p_96285_, p_96286_);
    }

    @Inject(at = @At("HEAD"), method = "init")
    private void injectInit(CallbackInfo ci) {
        this.addRenderableWidget(Button.builder(Component.translatable("category.fogoverrides.fogs"), button -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(FogOverrides.ConfigHelper.getConfig().init(this));
            }
        }).bounds(8, 8, 150, 20).build());
    }
}
