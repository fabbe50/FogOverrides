package com.fabbe50.fogoverrides.mixins;

import com.fabbe50.fogoverrides.FogOverrides;
import com.fabbe50.fogoverrides.Util;
import com.fabbe50.fogoverrides.holders.ConfigHolder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public class MixinClientLevel {
    @Inject(at = @At("RETURN"), method = "getCloudColor", cancellable = true)
    private void injectGetCloudColor(float p_104809_, CallbackInfoReturnable<Vec3> cir) {
        if (ConfigHolder.general.isAdjustCloudColors() && FogOverrides.data.getTargetColor() != -1) {
            cir.setReturnValue(Util.getVec3ColorFromInteger(Util.getBlendedColor(Util.getColorIntegerFromVec3(cir.getReturnValue()), FogOverrides.data.getTargetColor(), ConfigHolder.getGeneral().getCloudColorBlendRatio())));
        }
    }
}
