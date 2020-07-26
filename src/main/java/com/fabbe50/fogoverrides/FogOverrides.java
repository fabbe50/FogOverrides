package com.fabbe50.fogoverrides;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

@Mod("fogoverrides")
@Mod.EventBusSubscriber
public class FogOverrides {
    public static final Config config = new Config();

    private static final Logger LOGGER = LogManager.getLogger();

    public FogOverrides() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, config.getSpec(), "FogOverrides.toml");
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void densityAdjust(EntityViewRenderEvent.FogDensity event) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (((player.isCreative() || player.isSpectator()) && config.getGeneral().getCreativeOverrides().get())) {
            event.setDensity(0);
            event.setCanceled(true);
        } else if (checkFluidConditions(player, FluidTags.LAVA, Effects.FIRE_RESISTANCE)) {
            event.setDensity(config.getGeneral().getLavaFogDistance().get().floatValue());
            if (config.getGeneral().getLavaFogRemove().get())
                event.setDensity(0);
            event.setCanceled(true);
        } else if (checkFluidConditions(player, FluidTags.WATER, Effects.WATER_BREATHING)) {
            event.setDensity(config.getGeneral().getWaterFogDistance().get().floatValue());
            if (config.getGeneral().getWaterFogRemove().get())
                event.setDensity(0);
            event.setCanceled(true);
        } else if (!(player.areEyesInFluid(FluidTags.LAVA) || player.areEyesInFluid(FluidTags.WATER))) {
            if (checkDimensionConditions(player, DimensionType.OVERWORLD)) {
                event.setDensity(config.getGeneral().getOverworldFogDistance().get().floatValue());
                if (config.getGeneral().getOverworldFogRemove().get())
                    event.setDensity(0);
                event.setCanceled(true);
            } else if (checkDimensionConditions(player, DimensionType.THE_NETHER)) {
                event.setDensity(config.getGeneral().getNetherFogDistance().get().floatValue());
                if (config.getGeneral().getNetherFogRemove().get())
                    event.setDensity(0);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void overlayAdjust(RenderBlockOverlayEvent event) {
        PlayerEntity player = event.getPlayer();

        if (event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.WATER && config.getGeneral().getDisableWaterOverlay().get())
            event.setCanceled(true);

        if (event.getOverlayType() != RenderBlockOverlayEvent.OverlayType.FIRE) {
            return;
        }

        if ((player.isCreative() && config.getGeneral().getCreativeOverrides().get()) || config.getGeneral().getDisableFireOverlay().get())
            event.setCanceled(true);
        else if (player.isBurning() && player.isPotionActive(Effects.FIRE_RESISTANCE))
            event.getMatrixStack().translate(0, config.getGeneral().getFireOverlayOffset().get(), 0);
    }

    public static boolean checkFluidConditions(PlayerEntity player, Tag<Fluid> tag, Effect effect) {
        return player.areEyesInFluid(tag) && (config.getGeneral().getPotionAffectsVision().get() ? player.isPotionActive(effect) : true);
    }

    public static boolean checkDimensionConditions(PlayerEntity player, DimensionType dimension) {
        return player.dimension == dimension;
    }
}