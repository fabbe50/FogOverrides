package com.fabbe50.fogoverrides.fogoverrides.mixin;

import com.fabbe50.fogoverrides.fogoverrides.FogOverridesInit;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class ExampleMixin {

    @Inject(method = "init",at=@At("HEAD"))
    void init(CallbackInfo ci){
        FogOverridesInit.LOGGER.info("Stonecutter example mixin init in %s".formatted(FogOverridesInit.PLATFORM.getModloader()));
    }

}
