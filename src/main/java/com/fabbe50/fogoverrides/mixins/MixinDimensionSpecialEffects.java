package com.fabbe50.fogoverrides.mixins;

import com.fabbe50.fogoverrides.holders.ConfigHolder;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionSpecialEffects.class)
public class MixinDimensionSpecialEffects {
    @Inject(at = @At("RETURN"), method = "getCloudHeight", cancellable = true)
    private void injectGetCloudHeight(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(ConfigHolder.getGeneral().getCloudHeight());
    }
}
