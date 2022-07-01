package com.fabbe50.fogoverrides;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("fogoverrides")
@Mod.EventBusSubscriber
public class FogOverrides {
    private static final Logger LOGGER = LogManager.getLogger();
    public static Logger getLOGGER() {
        return LOGGER;
    }

    public FogOverrides() {
        Config.init();
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void densityAdjust(EntityViewRenderEvent.RenderFogEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (((player.isCreative() || player.isSpectator()) && Config.getGeneral().getCreativeOverrides().get())) {
            setDensity(event, Integer.MAX_VALUE, Integer.MAX_VALUE);
            event.setCanceled(true);
        } else if (checkFluidConditions(player, FluidTags.LAVA, MobEffects.FIRE_RESISTANCE)) {
            if (Config.getLava().getLavaFogRemove().get())
                setDensity(event, Integer.MAX_VALUE, Integer.MAX_VALUE);
            else {
                setDensity(event, Config.getLava().getLavaFogDistance().get().floatValue(), Config.getLava().getLavaFogEndDistance().get().floatValue());
            }
            event.setCanceled(true);
        } else if (checkFluidConditions(player, FluidTags.WATER, MobEffects.WATER_BREATHING)) {
            if (Config.getWater().getWaterFogRemove().get())
                setDensity(event,Integer.MAX_VALUE, Integer.MAX_VALUE);
            else
                setDensity(event, Config.getWater().getWaterFogDistance().get().floatValue(), Config.getWater().getWaterFogEndDistance().get().floatValue());
            event.setCanceled(true);
        } else if (!(player.isEyeInFluid(FluidTags.LAVA) || player.isEyeInFluid(FluidTags.WATER))) {
            if (checkDimensionConditions(player, DimensionType.DEFAULT_OVERWORLD)) {
                if ((Config.getOverworld().getEnableVoidFog().get() && player.getOnPos().getY() < (Config.getOverworld().getyLevelActivate().get())) &&
                        !ForgeHooksClient.hasPresetEditor(java.util.Optional.of(WorldPreset.FLAT)) &&
                        (player.level.getBrightness(LightLayer.SKY, player.getOnPos()) < 8 || !Config.getOverworld().getVoidFogAffectedBySkylight().get())) {
                    setDensity(event, Config.getOverworld().getVoidFogDensity().get().floatValue(), Config.getOverworld().getVoidFogEndDensity().get().floatValue());
                } else if (Config.getOverworld().getOverworldFogRemove().get()) {
                    setDensity(event, Integer.MAX_VALUE, Integer.MAX_VALUE);
                } else {
                    setDensity(event, Config.getOverworld().getOverworldFogDistance().get().floatValue(), Config.getOverworld().getOverworldFogEndDistance().get().floatValue());
                }
                event.setCanceled(true);
            } else if (checkDimensionConditions(player, DimensionType.DEFAULT_NETHER)) {
                if (Config.getNether().getNetherFogRemove().get())
                    setDensity(event,Integer.MAX_VALUE, Integer.MAX_VALUE);
                else
                    setDensity(event, Config.getNether().getNetherFogDistance().get().floatValue(), Config.getNether().getNetherFogEndDistance().get().floatValue());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void colorAdjust(EntityViewRenderEvent.FogColors event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (((player.isCreative() || player.isSpectator()) && Config.getGeneral().getCreativeOverrides().get())) {}
        else if (player.getOnPos().getY() < Config.getOverworld().getyLevelActivate().get() && Config.getOverworld().getEnableVoidFog().get() &&
                checkDimensionConditions(player, DimensionType.DEFAULT_OVERWORLD) && !ForgeHooksClient.hasPresetEditor(java.util.Optional.of(WorldPreset.FLAT)) &&
                (player.level.getBrightness(LightLayer.SKY, player.getOnPos()) < 8 || !Config.getOverworld().getVoidFogAffectedBySkylight().get())) {
            event.setRed(0);
            event.setGreen(0);
            event.setBlue(0);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void particles(TickEvent.WorldTickEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        Level world = Minecraft.getInstance().level;
        if (player == null || world == null)
            return;

        if (((player.isCreative() || player.isSpectator()) && Config.getGeneral().getCreativeOverrides().get())) {}
        else if (player.getOnPos().getY() < Config.getOverworld().getyLevelActivate().get() && Config.getOverworld().getEnableVoidParticles().get() && checkDimensionConditions(player, DimensionType.DEFAULT_OVERWORLD) && (player.level.getLightEmission(player.getOnPos()) < 8 && Config.getOverworld().getVoidFogAffectedBySkylight().get())) {
            int x = Mth.floor(player.getBlockX());
            int y = Mth.floor(player.getBlockY());
            int z = Mth.floor(player.getBlockZ());
            byte b = Config.getOverworld().getyLevelActivate().get().byteValue();

            for (int i = 0; i < 500; i++) {
                int j = x + world.random.nextInt(b) - world.random.nextInt(b);
                int k = y + world.random.nextInt(b) - world.random.nextInt(b);
                int l = z + world.random.nextInt(b) - world.random.nextInt(b);
                BlockState block = world.getBlockState(new BlockPos(j, k, l));

                if (block.getMaterial() == Material.AIR) {
                    if (world.random.nextInt(8) > k && !ForgeHooksClient.hasPresetEditor(java.util.Optional.of(WorldPreset.FLAT))) {
                        float h = k + world.random.nextFloat();
                        if (h >= -64 && Config.getOverworld().getyLevelActivate().get() > h) {
                            world.addParticle(ParticleTypes.MYCELIUM, j + world.random.nextFloat(), h, l + world.random.nextFloat(), 0, 0, 0);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void overlayAdjust(RenderBlockOverlayEvent event) {
        Player player = event.getPlayer();

        if (event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.WATER && Config.getGeneral().getDisableWaterOverlay().get())
            event.setCanceled(true);

        if (event.getOverlayType() != RenderBlockOverlayEvent.OverlayType.FIRE) {
            return;
        }

        if ((player.isCreative() && Config.getGeneral().getCreativeOverrides().get()) || Config.getGeneral().getDisableFireOverlay().get())
            event.setCanceled(true);
        else if (player.isOnFire() && (player.hasEffect(MobEffects.FIRE_RESISTANCE) || !Config.getGeneral().getPotionAffectsVision().get()))
            event.getPoseStack().translate(0, Config.getGeneral().getFireOverlayOffset().get(), 0);
    }

    public static boolean checkFluidConditions(Player player, TagKey<Fluid> tag, MobEffect effect) {
        return player.isEyeInFluid(tag) && (!Config.getGeneral().getPotionAffectsVision().get() || player.hasEffect(effect));
    }

    public static boolean checkDimensionConditions(Player player, DimensionType dimension) {
        return player.getLevel().dimensionType() == dimension;
    }

    public static void setDensity(EntityViewRenderEvent.RenderFogEvent event, float near, float far) {
        event.scaleNearPlaneDistance(near);
        event.scaleFarPlaneDistance(far);
    }
}