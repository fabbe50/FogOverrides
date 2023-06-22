package com.fabbe50.fogoverrides.handlers;

import com.fabbe50.fogoverrides.FogOverrides;
import com.fabbe50.fogoverrides.holders.ConfigHolder;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderBlockScreenEffectEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.language.IModInfo;
import org.lwjgl.glfw.GLFW;

public class GuiHandler {
    public static final String CATEGORY_FOGOVERRIDES = "key.category.fogoverrides.controls";
    public static final String KEY_OPEN_CONFIG = "key.fogoverrides.config_menu";
    public static final KeyMapping OPEN_SETTINGS = new KeyMapping(
            KEY_OPEN_CONFIG,
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F8,
            CATEGORY_FOGOVERRIDES
    );

    @Mod.EventBusSubscriber(modid = "fogoverrides", value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void keyInput(InputEvent.Key event) {
            if (OPEN_SETTINGS.consumeClick()) {
                IModInfo info = ModList.get().getModFileById("fogoverrides").getMods().get(0);
                if (info != null) {
                    try {
                        ConfigScreenHandler.getScreenFactoryFor(info).map(f -> f.apply(Minecraft.getInstance(), null)).ifPresent(newScreen -> Minecraft.getInstance().setScreen(newScreen));
                    } catch (final Exception e) {
                        FogOverrides.getLOGGER().error("There was a critical issue trying to build the config GUI for {}", info.getModId());
                    }
                }
            }
        }

        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public static void overlayAdjust(RenderBlockScreenEffectEvent event) {
            Player player = event.getPlayer();

            if (event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.WATER && ConfigHolder.getSpecial().isDisableWaterOverlay())
                event.setCanceled(true);

            if (event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.FIRE) {
                if ((player.isCreative() && ConfigHolder.getGeneral().getCreativeModeSettings() == ConfigHolder.General.CreativeModeSettings.DISABLED) || ConfigHolder.getSpecial().isDisableFireOverlay())
                    event.setCanceled(true);
                else if (player.isOnFire() && (player.hasEffect(MobEffects.FIRE_RESISTANCE) || !ConfigHolder.getSpecial().doesPotionAffectVision()))
                    event.getPoseStack().translate(0, ConfigHolder.getSpecial().getFireOverlayOffset() / 100f, 0);
            }
        }

    @Mod.EventBusSubscriber(modid = "fogoverrides", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            event.register(OPEN_SETTINGS);
        }
    }
}
