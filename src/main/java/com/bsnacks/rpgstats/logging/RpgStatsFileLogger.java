package com.bsnacks.rpgstats.logging;

import com.hypixel.hytale.logger.HytaleLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

public final class RpgStatsFileLogger {

    private static final DateTimeFormatter FILE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final DateTimeFormatter LINE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path logFile;
    private final HytaleLogger logger;
    private final Object lock = new Object();

    public RpgStatsFileLogger(Path dataDirectory, HytaleLogger logger) {
        this.logger = logger;
        Path logDir = dataDirectory.resolve("logs");
        try {
            Files.createDirectories(logDir);
        } catch (IOException ex) {
            logger.at(Level.WARNING).log("[RPGStats] Failed to create log directory " + logDir + ": " + ex.getMessage());
        }
        String stamp = FILE_FORMAT.format(LocalDateTime.now());
        logFile = logDir.resolve("log-" + stamp + ".txt");
        log("Log started.");
    }

    public void log(String message) {
        String line = LINE_FORMAT.format(LocalDateTime.now()) + " " + message + System.lineSeparator();
        synchronized (lock) {
            try {
                Files.writeString(logFile, line, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            } catch (IOException ex) {
                logger.at(Level.WARNING).log("[RPGStats] Failed to write log line: " + ex.getMessage());
            }
        }
    }

    public Path getLogFile() {
        return logFile;
    }
}
