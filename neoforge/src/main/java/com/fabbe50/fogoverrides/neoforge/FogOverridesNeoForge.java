package com.fabbe50.fogoverrides.neoforge;

import com.fabbe50.fogoverrides.FogOverrides;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(FogOverrides.MOD_ID)
public class FogOverridesNeoForge {
    public FogOverridesNeoForge(IEventBus bus) {
        FogOverrides.init();
    }
}
