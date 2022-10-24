package com.fabbe50.fogoverrides;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "fogoverrides")
public class ModConfig implements ConfigData {
    @ConfigEntry.Category(value = "General")
    General general = new General();
    @ConfigEntry.Category(value = "Overworld")
    Overworld overworld = new Overworld();
    @ConfigEntry.Category(value = "Void")
    Void void_ = new Void();
    @ConfigEntry.Category(value = "Nether")
    Nether nether = new Nether();
    @ConfigEntry.Category(value = "End")
    End end = new End();
    @ConfigEntry.Category(value = "Lava")
    Lava lava = new Lava();
    @ConfigEntry.Category(value = "Water")
    Water water = new Water();
    @ConfigEntry.Category(value = "Frostbite")
    Frostbite frostbite = new Frostbite();
    @ConfigEntry.Category(value = "Blindness")
    Blindness blindness = new Blindness();


    static class General {
        boolean potionAffectsVision = true;
        boolean creativeOverrides = false;
        boolean disableFireOverlay = false;

        @ConfigEntry.BoundedDiscrete(min = -100, max = 100)
        int fireOverlayOffset = -25;
    }

    static class Overworld {
        boolean overworldFogRemove = false;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 500)
        int overworldFogStartDistance = 80;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 500)
        int overworldFogEndDistance = 100;
    }

    static class Void {
        boolean enableVoidFog = false;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
        int voidFogStartDistance = 7;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
        int voidFogEndDistance = 80;
        @ConfigEntry.BoundedDiscrete(min = -64, max = 319)
        int yLevelActivate = 0;
        boolean enableVoidParticles = false;
        boolean voidFogAffectedBySkylight = true;
    }

    static class Nether {
        boolean netherFogRemove = false;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 500)
        int netherFogStartDistance = 32;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 500)
        int netherFogEndDistance = 64;
    }

    static class End {
        boolean endFogRemove = false;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 500)
        int endFogStartDistance = 32;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 500)
        int endFogEndDistance = 64;
    }

    static class Lava {
        boolean lavaFogRemove = false;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 500)
        int lavaFogStartDistance = 10;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 500)
        int lavaFogEndDistance = 20;
    }

    static class Water {
        boolean waterFogRemove = false;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 500)
        int waterFogStartDistance = 20;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 500)
        int waterFogEndDistance = 50;
        boolean disableWaterOverlay = false;
    }

    static class Frostbite {
        boolean frostbiteFogRemove = false;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
        int frostbiteFogStartDistance = 1;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
        int frostbiteFogEndDistance = 5;
        boolean disableFrostbiteOverlay = false;
        //boolean frostbiteOpacity = 0.5;
    }

    static class Blindness {
        boolean blindnessFogRemove = false;
        /*@ConfigEntry.BoundedDiscrete(min = 1, max = 100)
        int blindnessFogStartDistance = 1;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
        int blindnessFogEndDistance = 5;*/
    }
}