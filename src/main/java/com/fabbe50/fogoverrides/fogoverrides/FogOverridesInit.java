package com.fabbe50.fogoverrides.fogoverrides;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FogOverridesInit
{
	public static final String MODID = "fogoverrides";
	public static final Logger LOGGER = LoggerFactory.getLogger("Fog Overrides");
	public static ModPlatform PLATFORM = null;

	public static void entrypoint(ModPlatform platform) {
		FogOverridesInit.PLATFORM = platform;
		LOGGER.info("Started mod in %s loader".formatted(FogOverridesInit.PLATFORM.getModloader()));
	}
}