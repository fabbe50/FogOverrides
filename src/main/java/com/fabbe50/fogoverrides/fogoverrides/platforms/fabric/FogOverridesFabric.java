//? if fabric {
/*package com.fabbe50.fogoverrides.fogoverrides.platforms.fabric;

import com.fabbe50.fogoverrides.fogoverrides.ModPlatform;
import net.fabricmc.api.ModInitializer;
import com.fabbe50.fogoverrides.fogoverrides.FogOverridesInit;
import net.fabricmc.loader.api.FabricLoader;

public class FogOverridesFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		FogOverridesInit.entrypoint(new FabricPlatform());
	}
	public static class FabricPlatform implements ModPlatform{

		@Override
		public String getModloader() {
			return "Fabric";
		}

		@Override
		public boolean isModLoaded(String modloader) {
			return FabricLoader.getInstance().isModLoaded(modloader);
		}
	}
}
*///?}