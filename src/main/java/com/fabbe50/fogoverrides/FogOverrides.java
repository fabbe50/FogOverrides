package com.fabbe50.fogoverrides;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
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

@Mod("fogoverrides")
@Mod.EventBusSubscriber
public class FogOverrides {
    public static ModConfig config;

    private static final Logger LOGGER = LogManager.getLogger();
    public static Logger getLOGGER() {
        return LOGGER;
    }

    public FogOverrides() {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void densityAdjust(ViewportEvent.RenderFog event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (((player.isCreative() || player.isSpectator()) && config.general.creativeOverrides)) {
            setMaxDensity(event);
            event.setCanceled(true);
        } else if (player.isInPowderSnow && checkBlockConditions(player, player.level)) {
            if (config.frostbite.frostbiteFogRemove) {
                setMaxDensity(event);
            } else {
                setDensity(event, config.frostbite.frostbiteFogStartDistance, config.frostbite.frostbiteFogEndDistance);
            }
            event.setCanceled(true);
        } else if (player.hasEffect(MobEffects.BLINDNESS)) {
            if (config.blindness.blindnessFogRemove) {
                setMaxDensity(event);
            }
            event.setCanceled(true);
        } else if (checkFluidConditions(player, Fluids.LAVA.getFluidType(), MobEffects.FIRE_RESISTANCE)) {
            if (config.lava.lavaFogRemove)
                setMaxDensity(event);
            else {
                setDensity(event, config.lava.lavaFogStartDistance, config.lava.lavaFogEndDistance);
            }
            event.setCanceled(true);
        } else if (checkFluidConditions(player, Fluids.WATER.getFluidType(), MobEffects.WATER_BREATHING)) {
            if (config.water.waterFogRemove) {
                setDensity(event, Integer.MAX_VALUE, Integer.MAX_VALUE);
            } else {
                setDensity(event, config.water.waterFogStartDistance, config.water.waterFogEndDistance);
            }
            event.setCanceled(true);
        } else if (player.isEyeInFluidType(Fluids.EMPTY.getFluidType())) {
            if (checkDimensionConditions(player, BuiltinDimensionTypes.OVERWORLD)) {
                if ((config.void_.enableVoidFog && player.getOnPos().getY() < (config.void_.yLevelActivate)) &&
                        (player.clientLevel.getBrightness(LightLayer.SKY, player.getOnPos().above()) < 8 || !config.void_.voidFogAffectedBySkylight)) {
                    float distanceFromBottom = (float)(player.getEyePosition().y + 64);
                    if (distanceFromBottom < 1) {
                        distanceFromBottom = 1;
                    }
                    setDensity(event, config.void_.voidFogStartDistance + (config.void_.voidFogStartDistance * (distanceFromBottom / 100)), config.void_.voidFogEndDistance + (config.void_.voidFogEndDistance * (distanceFromBottom / 10)));
                } else if (config.overworld.overworldFogRemove) {
                    setMaxDensity(event);
                } else {
                    setDensity(event, config.overworld.overworldFogStartDistance, config.overworld.overworldFogEndDistance);
                }
                event.setCanceled(true);
            } else if (checkDimensionConditions(player, BuiltinDimensionTypes.NETHER)) {
                if (config.nether.netherFogRemove) {
                    setMaxDensity(event);
                } else {
                    setDensity(event, config.nether.netherFogStartDistance, config.nether.netherFogEndDistance);
                }
                event.setCanceled(true);
            } else if (checkDimensionConditions(player, BuiltinDimensionTypes.END)) {
                if (config.end.endFogRemove) {
                    setMaxDensity(event);
                } else {
                    setDensity(event, config.end.endFogStartDistance, config.end.endFogEndDistance);
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

        if (((player.isCreative() || player.isSpectator()) && config.general.creativeOverrides)) {}
        else if (player.getOnPos().getY() < config.void_.yLevelActivate && config.void_.enableVoidFog &&
                checkDimensionConditions(player, BuiltinDimensionTypes.OVERWORLD) &&
                (player.level.getBrightness(LightLayer.SKY, player.getOnPos().above()) < 8 || !config.void_.voidFogAffectedBySkylight)) {
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

        if (((player.isCreative() || player.isSpectator()) && config.general.creativeOverrides)) {}
        else if (player.getOnPos().getY() < config.void_.yLevelActivate && config.void_.enableVoidParticles && checkDimensionConditions(player, BuiltinDimensionTypes.OVERWORLD) && (player.level.getBrightness(LightLayer.SKY, player.getOnPos().above()) < 8 && config.void_.voidFogAffectedBySkylight)) {
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
                        if (h >= -64 && config.void_.yLevelActivate >= h) {
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

        if (event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.WATER && config.water.disableWaterOverlay)
            event.setCanceled(true);

        if (event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.FIRE) {
            if ((player.isCreative() && config.general.creativeOverrides) || config.general.disableFireOverlay)
                event.setCanceled(true);
            else if (player.isOnFire() && (player.hasEffect(MobEffects.FIRE_RESISTANCE) || !config.general.potionAffectsVision))
                event.getPoseStack().translate(0, config.general.fireOverlayOffset / 100f, 0);
        }
    }

    public static boolean checkBlockConditions(Player player, Level level) {
        return level.getBlockState(new BlockPos(player.getEyePosition())).is(Blocks.POWDER_SNOW);
    }

    public static boolean checkFluidConditions(Player player, FluidType fluidType, MobEffect effect) {
        return player.isEyeInFluidType(fluidType) && (!config.general.potionAffectsVision || player.hasEffect(effect));
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