package com.fabbe50.fogoverrides.neoforge;

import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class FogOverridesExpectPlatformImpl {
    /**
     * This is our actual method to {@link com.fabbe50.fogoverrides.FogOverridesExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
