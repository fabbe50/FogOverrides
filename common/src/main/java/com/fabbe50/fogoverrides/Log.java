package com.fabbe50.fogoverrides;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Log {
    public static void debug(String msg) {
        out("DEBUG", msg);
    }

    public static void info(String msg) {
        out("INFO", msg);
    }

    public static void warn(String msg) {
        err("WARN", msg);
    }

    public static void error(String msg) {
        err("ERROR", msg);
    }

    private static void out(String level, String msg) {
        System.out.println("[" + LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) + "] [Fog Overrides/" + level + "] " + msg);
    }

    private static void err(String level, String msg) {
        System.err.println("[" + LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) + "] [Fog Overrides/" + level + "] " + msg);
    }
}
