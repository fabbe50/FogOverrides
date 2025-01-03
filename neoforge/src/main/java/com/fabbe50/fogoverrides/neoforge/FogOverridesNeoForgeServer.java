package com.fabbe50.fogoverrides.neoforge;

import com.fabbe50.fogoverrides.FogOverrides;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = FogOverrides.MOD_ID, dist = Dist.DEDICATED_SERVER)
public class FogOverridesNeoForgeServer {
    public FogOverridesNeoForgeServer(IEventBus bus) {
        FogOverrides.serverInit();
    }
}
