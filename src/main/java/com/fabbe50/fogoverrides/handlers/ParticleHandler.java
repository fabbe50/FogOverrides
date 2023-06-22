package com.fabbe50.fogoverrides.handlers;

import com.fabbe50.fogoverrides.holders.ConfigHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.fabbe50.fogoverrides.Util.*;

public class ParticleHandler {
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void particles(RenderLevelStageEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        Level world = Minecraft.getInstance().level;
        if (player == null || world == null)
            return;

        if (((player.isCreative() || player.isSpectator()) && ConfigHolder.getGeneral().getCreativeModeSettings() == ConfigHolder.General.CreativeModeSettings.DISABLED)) {}
        else if (player.getOnPos().getY() < ConfigHolder.getVoid_().getyLevelActivate() && ConfigHolder.getVoid_().isEnableVoidParticles() && checkDimensionConditions(player, BuiltinDimensionTypes.OVERWORLD) && (player.level.getBrightness(LightLayer.SKY, player.getOnPos().above()) < 8 && ConfigHolder.getVoid_().isVoidFogAffectedBySkylight())) {
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
                        if (h >= -64 && ConfigHolder.getVoid_().getyLevelActivate() >= h) {
                            world.addParticle(ParticleTypes.MYCELIUM, j + world.random.nextFloat(), h, l + world.random.nextFloat(), 0, 0, 0);
                        }
                    }
                }
            }
        }
    }
}
