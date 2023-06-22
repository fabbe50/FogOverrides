package com.fabbe50.fogoverrides;

import com.fabbe50.fogoverrides.handlers.*;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod("fogoverrides")
@Mod.EventBusSubscriber
public class FogOverrides {
    private static final Logger LOGGER = LogManager.getLogger();
    public static Logger getLOGGER() {
        return LOGGER;
    }
    public static DataHandler data = new DataHandler();
    public static FogHandler fogHandler;


    public FogOverrides() {
        ModConfig.register();
        ConfigHelper.registerConfig();

        MinecraftForge.EVENT_BUS.register(new PlayerHandler());
        MinecraftForge.EVENT_BUS.register(fogHandler = new FogHandler());
        MinecraftForge.EVENT_BUS.register(new ParticleHandler());
        MinecraftForge.EVENT_BUS.register(new DebugHandler());
    }

    public static class ConfigHelper {
        static ModConfig config = new ModConfig();

        public static void registerConfig() {
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> config.init(parent)));
        }

        public static ModConfig getConfig() {
            return config;
        }
    }
}