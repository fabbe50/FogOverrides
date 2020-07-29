package com.fabbe50.fogoverrides;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
            event.setDensity(config.getLava().getLavaFogDistance().get().floatValue());
            if (config.getLava().getLavaFogRemove().get())
                event.setDensity(0);
            event.setCanceled(true);
        } else if (checkFluidConditions(player, FluidTags.WATER, Effects.WATER_BREATHING)) {
            event.setDensity(config.getWater().getWaterFogDistance().get().floatValue());
            if (config.getWater().getWaterFogRemove().get())
                event.setDensity(0);
            event.setCanceled(true);
        } else if (!(player.areEyesInFluid(FluidTags.LAVA) || player.areEyesInFluid(FluidTags.WATER))) {
            if (checkDimensionConditions(player, DimensionType.OVERWORLD)) {
                event.setDensity(config.getOverworld().getOverworldFogDistance().get().floatValue());
                if (config.getOverworld().getOverworldFogRemove().get())
                    event.setDensity(0);
                if (config.getOverworld().getEnableVoidFog().get() && player.getPosition().getY() < (config.getOverworld().getyLevelActivate().get()))
                    event.setDensity(config.getOverworld().getVoidFogDensity().get().floatValue());
                event.setCanceled(true);
            } else if (checkDimensionConditions(player, DimensionType.THE_NETHER)) {
                event.setDensity(config.getNether().getNetherFogDistance().get().floatValue());
                if (config.getNether().getNetherFogRemove().get())
                    event.setDensity(0);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void colorAdjust(EntityViewRenderEvent.FogColors event) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (((player.isCreative() || player.isSpectator()) && config.getGeneral().getCreativeOverrides().get())) {}
        else if (player.getPosition().getY() < config.getOverworld().getyLevelActivate().get() && config.getOverworld().getEnableVoidFog().get() && checkDimensionConditions(player, DimensionType.OVERWORLD)) {
            event.setRed(0);
            event.setGreen(0);
            event.setBlue(0);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void particles(TickEvent.WorldTickEvent event) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        World world = Minecraft.getInstance().world;
        if (player == null || world == null)
            return;

        if (((player.isCreative() || player.isSpectator()) && config.getGeneral().getCreativeOverrides().get())) {}
        else if (player.getPosition().getY() < config.getOverworld().getyLevelActivate().get() && config.getOverworld().getEnableVoidParticles().get() && checkDimensionConditions(player, DimensionType.OVERWORLD)) {
            int x = MathHelper.floor(player.getPosX());
            int y = MathHelper.floor(player.getPosY());
            int z = MathHelper.floor(player.getPosZ());
            byte b = config.getOverworld().getyLevelActivate().get().byteValue();

            for (int i = 0; i < 1000; i++) {
                int j = x + world.rand.nextInt(b) - world.rand.nextInt(b);
                int k = y + world.rand.nextInt(b) - world.rand.nextInt(b);
                int l = z + world.rand.nextInt(b) - world.rand.nextInt(b);
                BlockState block = world.getBlockState(new BlockPos(j, k, l));

                if (block.getMaterial() == Material.AIR) {
                    if (world.rand.nextInt(8) > k && (world.getWorldType() != WorldType.FLAT && checkDimensionConditions(player, DimensionType.OVERWORLD))) {
                        float h = k + world.rand.nextFloat();
                        if (h >= 0 && config.getOverworld().getyLevelActivate().get() > h)
                            world.addParticle(ParticleTypes.MYCELIUM, j + world.rand.nextFloat(), h, l + world.rand.nextFloat(), 0, 0, 0);
                    }
                }
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