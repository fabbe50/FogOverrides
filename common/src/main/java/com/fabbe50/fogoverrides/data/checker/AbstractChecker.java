package com.fabbe50.fogoverrides.data.checker;

import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;

public abstract class AbstractChecker<T> implements IChecker {
    @Override
    public Result getResult(CurrentDataStorage settings, Entity entity, FogRenderer.FogMode fogMode, FogType fogType) {
        return Result.CUSTOM_CHECKER;
    }

    public Result getResult(T object) {
        return Result.ALLOW_NEXT;
    }
}
