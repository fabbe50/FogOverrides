package com.fabbe50.fogoverrides.fabric;

import com.fabbe50.fogoverrides.FogOverrides;
import net.fabricmc.api.ClientModInitializer;

public class FogOverridesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
//        FogOverrides.clientInit();
        FogOverrides.debugScreenInit();
    }
}
