package com.fabbe50.fogoverrides.fabric;

import com.fabbe50.fogoverrides.FogOverrides;
import net.fabricmc.api.DedicatedServerModInitializer;

public class FogOverridesFabricServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        FogOverrides.serverInit();
    }
}
