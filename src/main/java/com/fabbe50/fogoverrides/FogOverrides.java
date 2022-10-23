package com.fabbe50.fogoverrides;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderBlockScreenEffectEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

@Mod("fogoverrides")
@Mod.EventBusSubscriber
public class FogOverrides {
    public static final Config config = new Config();

    private static final Logger LOGGER = LogManager.getLogger();
    public static Logger getLOGGER() {
        return LOGGER;
    }

    public FogOverrides() {
        Config.init();
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void densityAdjust(ViewportEvent.RenderFog event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (((player.isCreative() || player.isSpectator()) && Config.getGeneral().getCreativeOverrides().get())) {
            setMaxDensity(event);
            event.setCanceled(true);
        } else if (player.isInPowderSnow && checkBlockConditions(player, player.level)) {
            if (Config.getFrostbite().getFrostbiteFogRemove().get()) {
                setMaxDensity(event);
            } else {
                setDensity(event, Config.getFrostbite().getFrostbiteFogStartingDistance().get(), Config.getFrostbite().getFrostbiteFogEndingDistance().get());
            }
            event.setCanceled(true);
        } else if (player.hasEffect(MobEffects.BLINDNESS)) {
            if (Config.getBlindness().getBlindnessFogRemove().get()) {
                setMaxDensity(event);
            } else {
            }
            event.setCanceled(true);
        } else if (checkFluidConditions(player, Fluids.LAVA.getFluidType(), MobEffects.FIRE_RESISTANCE)) {
            if (Config.getLava().getLavaFogRemove().get())
                setMaxDensity(event);
            else {
                setDensity(event, Config.getLava().getLavaFogDistance().get(), Config.getLava().getLavaFogEndDistance().get());
            }
            event.setCanceled(true);
        } else if (checkFluidConditions(player, Fluids.WATER.getFluidType(), MobEffects.WATER_BREATHING)) {
            if (Config.getWater().getWaterFogRemove().get())
                setDensity(event,Integer.MAX_VALUE, Integer.MAX_VALUE);
            else
                setDensity(event, Config.getWater().getWaterFogDistance().get(), Config.getWater().getWaterFogEndDistance().get());
            event.setCanceled(true);
        } else {
            if (checkDimensionConditions(player, BuiltinDimensionTypes.OVERWORLD)) {
                if ((Config.getOverworld().getEnableVoidFog().get() && player.getOnPos().getY() < (Config.getOverworld().getYLevelActivate().get())) &&
                        (player.clientLevel.getBrightness(LightLayer.SKY, player.getOnPos().above()) < 8 || !Config.getOverworld().getVoidFogAffectedBySkylight().get())) {
                    float distanceFromBottom = (float)(player.getEyePosition().y + 64);
                    if (distanceFromBottom < 1) {
                        distanceFromBottom = 1;
                    }
                    setDensity(event, Config.getOverworld().getVoidFogDensity().get().floatValue() + (Config.getOverworld().getVoidFogDensity().get().floatValue() * (distanceFromBottom / 100)), Config.getOverworld().getVoidFogEndDensity().get().floatValue() + (Config.getOverworld().getVoidFogEndDensity().get().floatValue() * (distanceFromBottom / 10)));
                } else if (Config.getOverworld().getOverworldFogRemove().get()) {
                    setMaxDensity(event);
                } else {
                    setDensity(event, Config.getOverworld().getOverworldFogDistance().get(), Config.getOverworld().getOverworldFogEndDistance().get());
                }
                event.setCanceled(true);
            } else if (checkDimensionConditions(player, BuiltinDimensionTypes.NETHER)) {
                if (Config.getNether().getNetherFogRemove().get()) {
                    setMaxDensity(event);
                } else {
                    setDensity(event, Config.getNether().getNetherFogDistance().get(), Config.getNether().getNetherFogEndDistance().get());
                }
                event.setCanceled(true);
            } else if (checkDimensionConditions(player, BuiltinDimensionTypes.END)) {
                if (Config.getEnd().getEndFogRemove().get()) {
                    setMaxDensity(event);
                } else {
                    setDensity(event, Config.getEnd().getEndFogDistance().get(), Config.getEnd().getEndFogEndDistance().get());
                }
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void colorAdjust(ViewportEvent.ComputeFogColor event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (((player.isCreative() || player.isSpectator()) && Config.getGeneral().getCreativeOverrides().get())) {}
        else if (player.getOnPos().getY() < Config.getOverworld().getYLevelActivate().get() && Config.getOverworld().getEnableVoidFog().get() &&
                checkDimensionConditions(player, BuiltinDimensionTypes.OVERWORLD) &&
                (player.level.getBrightness(LightLayer.SKY, player.getOnPos().above()) < 8 || !Config.getOverworld().getVoidFogAffectedBySkylight().get())) {
            event.setRed(0);
            event.setGreen(0);
            event.setBlue(0);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void guiOverlay(RenderGuiOverlayEvent event) {
        if (event.getOverlay() == VanillaGuiOverlay.FROSTBITE.type()) {
//            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void particles(RenderLevelStageEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        Level world = Minecraft.getInstance().level;
        if (player == null || world == null)
            return;

        if (((player.isCreative() || player.isSpectator()) && Config.getGeneral().getCreativeOverrides().get())) {}
        else if (player.getOnPos().getY() < Config.getOverworld().getYLevelActivate().get() && Config.getOverworld().getEnableVoidParticles().get() && checkDimensionConditions(player, BuiltinDimensionTypes.OVERWORLD) && (player.level.getBrightness(LightLayer.SKY, player.getOnPos().above()) < 8 && Config.getOverworld().getVoidFogAffectedBySkylight().get())) {
            int x = Mth.floor(player.getBlockX());
            int y = Mth.floor(player.getBlockY());
            int z = Mth.floor(player.getBlockZ());

            for (int i = 0; i < 20; i++) {
                int j = x + (-20 + world.random.nextInt(40));
                int k = y + (-10 + world.random.nextInt(20));
                int l = z + (-20 + world.random.nextInt(40));
                BlockState block = world.getBlockState(new BlockPos(j, k, l));

                if (block.getMaterial() == Material.AIR) {
                    if (world.random.nextInt(8) > k) {
                        float h = k + world.random.nextFloat();
                        if (h >= -64 && Config.getOverworld().getYLevelActivate().get() >= h) {
                            world.addParticle(ParticleTypes.MYCELIUM, j + world.random.nextFloat(), h, l + world.random.nextFloat(), 0, 0, 0);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void overlayAdjust(RenderBlockScreenEffectEvent event) {
        Player player = event.getPlayer();

        if (event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.WATER && Config.getGeneral().getDisableWaterOverlay().get())
            event.setCanceled(true);

        if (event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.FIRE) {
            if ((player.isCreative() && Config.getGeneral().getCreativeOverrides().get()) || Config.getGeneral().getDisableFireOverlay().get())
                event.setCanceled(true);
            else if (player.isOnFire() && (player.hasEffect(MobEffects.FIRE_RESISTANCE) || !Config.getGeneral().getPotionAffectsVision().get()))
                event.getPoseStack().translate(0, Config.getGeneral().getFireOverlayOffset().get(), 0);
        }
    }

    public static boolean checkBlockConditions(Player player, Level level) {
        return level.getBlockState(new BlockPos(player.getEyePosition())).is(Blocks.POWDER_SNOW);
    }

    public static boolean checkFluidConditions(Player player, FluidType fluidType, MobEffect effect) {
        return player.isEyeInFluidType(fluidType) && (!Config.getGeneral().getPotionAffectsVision().get() || player.hasEffect(effect));
    }

    public static boolean checkDimensionConditions(Player player, ResourceKey<DimensionType> dimension) {
        return checkDimensionConditions(player, dimension.location());
    }

    public static boolean checkDimensionConditions(Player player, ResourceLocation dimension) {
        return player.getLevel().dimensionTypeId().location() == dimension;
    }

    public static void setMaxDensity(ViewportEvent.RenderFog event) {
        setDensity(event, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public static void setDensity(ViewportEvent.RenderFog event, int near, int far) {
        event.setNearPlaneDistance(near);
        event.setFarPlaneDistance(far);
    }

    public static void setDensity(ViewportEvent.RenderFog event, float near, float far) {
        event.setNearPlaneDistance(near);
        event.setFarPlaneDistance(far);
    }
}