package com.fabbe50.fogoverrides.mixin;

import com.fabbe50.fogoverrides.ClientUtilities;
import com.fabbe50.fogoverrides.ColorUtils;
import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.Utilities;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.ModFogData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class MixinClientLevel extends Level {
    protected MixinClientLevel(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
    }

    @Inject(at = @At("RETURN"), method = "getSkyColor", cancellable = true)
    private void injectGetSkyColor(Vec3 vec3, float f, CallbackInfoReturnable<Integer> cir) {
        if (ModConfig.INSTANCE.modActive) {
            float g = this.getTimeOfDay(f);
            Vec3 vec32 = vec3.subtract(2.0, 2.0, 2.0).scale(0.25);
            Vec3 vec33 = CubicSampler.gaussianSampleVec3(vec32, (ix, jx, kx) -> Vec3.fromRGB24(this.getBiomeManager().getNoiseBiomeAtQuart(ix, jx, kx).value().getSkyColor()));
            float h = Mth.cos(g * 6.2831855F) * 2.0F + 0.5F;
            h = Mth.clamp(h, 0.0F, 1.0F);
            vec33 = vec33.scale(h);
            int i = Utilities.getColorIntegerFromVec3(vec33);
            float rainLevel = getRainLevel(f);
            if (rainLevel > 0) {
                CurrentDataStorage settings = CurrentDataStorage.INSTANCE;
                ModFogData biomeData = settings.getBiomeFogData(ClientUtilities.getCurrentBiomeLocation());
                ModFogData dimensionData = settings.getDimensionFogData(ClientUtilities.getCurrentDimensionLocation());
                if ((biomeData.isOverrideGameFog() && biomeData.getRain().isEnabled()) || (dimensionData.isOverrideGameFog() && dimensionData.getRain().isEnabled())) {
                    if (ColorUtils.getCurrentColor() != 0) {
                        int rainSkyColor = Utilities.getBlendedColor(i, ColorUtils.getCurrentColor(), rainLevel);
                        cir.setReturnValue(rainSkyColor);
                    }
                }
            }
        }
    }
}
