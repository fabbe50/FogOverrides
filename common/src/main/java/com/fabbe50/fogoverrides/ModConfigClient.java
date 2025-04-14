package com.fabbe50.fogoverrides;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

public class ModConfigClient {
    public static ModConfigClient INSTANCE = new ModConfigClient();

    public final KeyMapping OPEN_CONFIG = new KeyMapping(
            "text.fogoverrides.keybinds.open_menu",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_F8,
            "text.fogoverrides.keybinds"
    );
    public final KeyMapping TOGGLE_MOD = new KeyMapping(
            "text.fogoverrides.keybinds.toggle_mod",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_F7,
            "text.fogoverrides.keybinds"
    );
}
