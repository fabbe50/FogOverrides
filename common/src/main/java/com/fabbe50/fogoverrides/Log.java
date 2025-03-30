package com.fabbe50.fogoverrides;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Log {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void info(String msg) {
        LOGGER.info("[{}] [Fog Overrides/INFO] {}", LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME), msg);
    }

    public static void debug(String msg) {
        LOGGER.info("[{}] [Fog Overrides/DEBUG] {}", LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME), msg);
    }

    public static void warn(String msg) {
        LOGGER.info("[{}] [Fog Overrides/WARN] {}", LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME), msg);
    }

    public static void error(String msg) {
        LOGGER.error("[{}}] [Fog Overrides/ERROR] {}", LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME), msg);
    }
}
