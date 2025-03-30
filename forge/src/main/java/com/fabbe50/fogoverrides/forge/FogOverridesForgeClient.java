package com.fabbe50.fogoverrides.forge;

import com.fabbe50.fogoverrides.ClothScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;

public class FogOverridesForgeClient {
    public static void initClient() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (a, b) -> true));
        FogOverridesForgeClient.registerConfigScreen();
    }

    public static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> ClothScreen.getConfigScreen(screen)));
    }
}
