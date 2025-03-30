package com.fabbe50.fogoverrides.mixin;

import com.fabbe50.fogoverrides.Log;
import com.fabbe50.fogoverrides.data.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ResourceKey.class, priority = 2000)
public class MixinResourceKey {
    static {
        Log.info("ResourceKey-Mixin initialized.");
    }

    @Inject(at = @At("HEAD"), method = "create(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/resources/ResourceKey;")
    private static <T> void injectCreate(ResourceLocation resourceLocation, ResourceLocation resourceLocation2, CallbackInfoReturnable<ResourceKey<T>> cir) {
        if (Registries.BIOME != null) {
            if (resourceLocation.equals(Registries.BIOME.location())) {
                Registry.addBiomeToList(resourceLocation2);
            }
        }
        if (Registries.DIMENSION != null) {
            if (resourceLocation.equals(Registries.DIMENSION.location())) {
                Registry.addDimensionToList(resourceLocation2);
            }
        }
    }
}
