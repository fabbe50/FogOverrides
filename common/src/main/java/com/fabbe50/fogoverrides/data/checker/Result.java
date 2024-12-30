package com.fabbe50.fogoverrides.data.checker;

public enum Result {
    DO_RENDER, // Uses the mod renderer.
    ALLOW_NEXT, // Tries the next mod renderer.
    CUSTOM_CHECKER,
    SKIP_STACK; // Skip to use vanilla renderer.
}
