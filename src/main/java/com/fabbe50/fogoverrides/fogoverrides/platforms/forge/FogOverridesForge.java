//? if forge {
/*package com.fabbe50.fogoverrides.fogoverrides.platforms.forge;

import com.fabbe50.fogoverrides.fogoverrides.ConfigScreen;
import com.fabbe50.fogoverrides.fogoverrides.FogOverridesInit;
import com.fabbe50.fogoverrides.fogoverrides.ModPlatform;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod("fogoverrides")
public class FogOverridesForge {
	public FogOverridesForge() {
		FogOverridesInit.entrypoint(new ForgePlatform());
        MinecraftForge.registerConfigScreen(ConfigScreen::createConfigScreen);
	}
	public static class ForgePlatform implements ModPlatform {
		@Override
		public String getModloader() {
			return "LexForge";
		}

		@Override
		public boolean isModLoaded(String modId) {
			return ModList.get().isLoaded(modId);
		}
	}

}
*///?}