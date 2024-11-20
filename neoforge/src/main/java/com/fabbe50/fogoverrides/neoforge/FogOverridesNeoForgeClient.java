package com.fabbe50.fogoverrides.neoforge;

import com.fabbe50.fogoverrides.ClothScreen;
import com.fabbe50.fogoverrides.FogOverrides;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = FogOverrides.MOD_ID, dist = Dist.CLIENT)
public class FogOverridesNeoForgeClient {
    public FogOverridesNeoForgeClient(IEventBus bus, ModContainer container) {
        FogOverrides.clientInit();
        FogOverrides.debugScreenInit();
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, screen) -> ClothScreen.getConfigScreen(screen));
    }
}
