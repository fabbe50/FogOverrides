package com.fabbe50.fogoverrides;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public class ModConfigClient {
    public static final KeyMapping OPEN_CONFIG = new KeyMapping(
            "text.fogoverrides.keybinds.open_menu",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_F8,
            "text.fogoverrides.keybinds"
    );
}
