package com.fabbe50.fogoverrides.forge;

import dev.architectury.platform.forge.EventBuses;
import com.fabbe50.fogoverrides.FogOverrides;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FogOverrides.MOD_ID)
public class FogOverridesForge {
    public FogOverridesForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(FogOverrides.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        FogOverrides.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onServerSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {

    }

    private void onClientSetup(FMLClientSetupEvent event) {
        FogOverridesForgeClient.initClient();
    }

    private void onServerSetup(FMLDedicatedServerSetupEvent event) {
        FogOverridesForgeServer.initServer();
    }
}
