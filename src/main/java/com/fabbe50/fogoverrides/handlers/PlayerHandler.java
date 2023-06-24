package com.fabbe50.fogoverrides.handlers;

import com.fabbe50.fogoverrides.FogOverrides;
import com.fabbe50.fogoverrides.Util;
import com.fabbe50.fogoverrides.holders.*;
import com.fabbe50.fogoverrides.holders.data.BiomeData;
import com.fabbe50.fogoverrides.holders.data.DefaultVariables;
import com.fabbe50.fogoverrides.holders.data.IHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerHandler {
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void checkPlayerState(TickEvent.PlayerTickEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        FogOverrides.data.setCurrentBiome(player.level().getBiome(player.getOnPos()));
        if (!applyGameModeOverrides(player)) {
            if (!applyBiomeOverrides(player))
                if (!applyDimensionOverrides(player))
                    applyDefaults();

            if (!applyPotionOverrides(player))
                if (!applyLiquidOverrides(player))
                    if (!applyBlockOverrides(player))
                        if (!applyWeatherConditions(player)) {
                            FogOverrides.data.setTargetBlendColor(-1);
                            FogOverrides.data.setCurrentFogBlendType(null);
                        }
        }
    }

    private boolean applyGameModeOverrides(LocalPlayer player) {
        if (player.isCreative() || player.isSpectator()) {
            if (ConfigHolder.getGeneral().getCreativeModeSettings() == ConfigHolder.General.CreativeModeSettings.DISABLED) {
                FogOverrides.data.setBaseTargets(OTHER_FOGS.NO_FOG);
                return true;
            } else if (ConfigHolder.getGeneral().getCreativeModeSettings() == ConfigHolder.General.CreativeModeSettings.VANILLA) {
                FogOverrides.data.setBaseTargets(OTHER_FOGS.DEFAULT);
                return true;
            }
        }
        return false;
    }

    private boolean applyPotionOverrides(LocalPlayer player) {
        for (PotionEffects effect : PotionEffects.values()) {
            if (player.hasEffect(effect.getMobEffect()) && effect.isEnabled()) {
                FogOverrides.data.setBlendTargets(effect);
                return true;
            }
        }
        return false;
    }

    private boolean applyLiquidOverrides(LocalPlayer player) {
        for (Liquids liquid : Liquids.values()) {
            if (liquid != Liquids.EMPTY) {
                if (Util.checkFluidConditions(player, liquid)) {
                    if (Util.areEyesInFluid(player.level(), player)) {
                        FogOverrides.data.setBlendTargets(liquid);
                        return true;
                    }
                } else if (player.isEyeInFluidType(liquid.getFluidType()) && Util.areEyesInFluid(player.level(), player)) {
                    FogOverrides.data.setTargetStartDistance(liquid.getDefaultFogStartDistance());
                    FogOverrides.data.setTargetEndDistance(liquid.getDefaultFogEndDistance());
                    if (liquid.shouldOverrideColor()) {
                        FogOverrides.data.setTargetBlendPercentage(liquid.getBlendPercentage() / 100f);
                        FogOverrides.data.setTargetBlendColor(liquid.getColor());
                    } else {
                        FogOverrides.data.setTargetBlendPercentage(0.5f);
                        FogOverrides.data.setTargetBlendColor(-1);
                    }
                    FogOverrides.data.setCurrentFogBlendType(liquid.getHolderType());
                    return true;
                }
            }
        }
        return false;
    }

    private boolean applyBlockOverrides(LocalPlayer player) {
        for (BlockEffects blockEffects : BlockEffects.values()) {
            if (Util.checkBlockConditions(player, player.level(), blockEffects.getBlock())) {
                FogOverrides.data.setBlendTargets(blockEffects);
                return true;
            }
        }
        return false;
    }

    private boolean applyWeatherConditions(LocalPlayer player) {
        if (player.clientLevel.isRaining() && FogOverrides.data.getTargetBaseColor() != -1) {
            //TODO: Use rain level to adjust color blend.
            FogOverrides.data.setTargetBlendColor(0x000000);
            FogOverrides.data.setTargetBlendPercentage(0.35F);
            return true;
        }
        return false;
    }

    private boolean applyBiomeOverrides(LocalPlayer player) {
        for (BiomeData biomeData : BiomeHolder.getBiomeDataList()) {
            if (Util.checkBiomeConditions(player, biomeData.getResourceLocation()) && biomeData.isEnabled()) {
                if (!applyVoidFog(player, player.level().dimensionTypeId().equals(Dimensions.OVERWORLD.getResourceKey()) ? Dimensions.OVERWORLD : Dimensions.NETHER)) {
                    FogOverrides.data.setBaseTargets(biomeData);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean applyDimensionOverrides(LocalPlayer player) {
        for (Dimensions dimension : Dimensions.values()) {
            if (Util.checkDimensionConditions(player, dimension.getResourceKey()) && dimension.isEnabled()) {
                if (!applyVoidFog(player, dimension)) {
                    FogOverrides.data.setBaseTargets(dimension);
                }
                return true;
            }
        }
        return false;
    }

    private boolean applyVoidFog(LocalPlayer player, Dimensions dimension) {
        if (ConfigHolder.getVoid_().isEnableVoidFog() && dimension == Dimensions.OVERWORLD) {
            if ((player.getOnPos().getY() < (ConfigHolder.getVoid_().getyLevelActivate())) &&
                    (player.clientLevel.getBrightness(LightLayer.SKY, player.getOnPos().above()) < 8 || !ConfigHolder.getVoid_().isVoidFogAffectedBySkylight())) {
                FogOverrides.data.setTargetStartDistance(ConfigHolder.getVoid_().getVoidFogStartDistance());
                FogOverrides.data.setTargetEndDistance(ConfigHolder.getVoid_().getVoidFogEndDistance());
                FogOverrides.data.setTargetBaseColor(0x000);
                return true;
            }
        }
        return false;
    }

    private void applyDefaults() {
        FogOverrides.data.setBaseTargets(OTHER_FOGS.DEFAULT);
    }

    public enum OTHER_FOGS implements IHolder {
        NO_FOG("no-fog", Integer.MAX_VALUE, Integer.MAX_VALUE),
        DEFAULT("default", -1, -1); //-1 disables the custom fog and reverts it to the regular Minecraft Fog Rendering.

        private final DefaultVariables variables;
        OTHER_FOGS(String name, int startDistance, int endDistance) {
            variables = new DefaultVariables(name, "", false, true, startDistance, endDistance,0,1000, false, -1);
        }

        @Override
        public String getHolderType() {
            return this.getName();
        }

        @Override
        public DefaultVariables getVariables() {
            return variables;
        }
    }
}
