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
import java.util.stream.Stream;

public final class RpgStatsFileLogger {

    private static final DateTimeFormatter FILE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final DateTimeFormatter LINE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String VERSION_FILE_NAME = "last_version.txt";

    private final Path logFile;
    private final HytaleLogger logger;
    private final Object lock = new Object();

    public RpgStatsFileLogger(Path dataDirectory, HytaleLogger logger, String currentVersion) {
        this.logger = logger;
        Path logDir = dataDirectory.resolve("logs");
        try {
            Files.createDirectories(logDir);
        } catch (IOException ex) {
            logger.at(Level.WARNING).log("[RPGStats] Failed to create log directory " + logDir + ": " + ex.getMessage());
        }

        // Check for version change and rotate logs if needed
        rotateLogsIfVersionChanged(dataDirectory, logDir, currentVersion);

        String stamp = FILE_FORMAT.format(LocalDateTime.now());
        logFile = logDir.resolve("log-" + stamp + ".txt");
        log("Log started. Version: " + currentVersion);
    }

    private void rotateLogsIfVersionChanged(Path dataDirectory, Path logDir, String currentVersion) {
        Path versionFile = dataDirectory.resolve(VERSION_FILE_NAME);
        String lastVersion = null;

        // Read last known version
        if (Files.exists(versionFile)) {
            try {
                lastVersion = Files.readString(versionFile, StandardCharsets.UTF_8).trim();
            } catch (IOException ex) {
                logger.at(Level.WARNING).log("[RPGStats] Failed to read version file: " + ex.getMessage());
            }
        }

        // Check if version changed
        if (lastVersion != null && !lastVersion.isEmpty() && !lastVersion.equals(currentVersion)) {
            logger.at(Level.INFO).log("[RPGStats] Version changed from " + lastVersion + " to " + currentVersion + ". Archiving old logs.");
            archiveOldLogs(logDir, lastVersion);
        }

        // Write current version
        try {
            Files.writeString(versionFile, currentVersion, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            logger.at(Level.WARNING).log("[RPGStats] Failed to write version file: " + ex.getMessage());
        }
    }

    private void archiveOldLogs(Path logDir, String oldVersion) {
        try (Stream<Path> files = Files.list(logDir)) {
            files.filter(Files::isRegularFile)
                 .filter(p -> p.getFileName().toString().startsWith("log-"))
                 .filter(p -> !p.getFileName().toString().contains("-old-"))
                 .forEach(logPath -> {
                     String fileName = logPath.getFileName().toString();
                     String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
                     String extension = fileName.substring(fileName.lastIndexOf('.'));
                     String newName = baseName + "-old-" + oldVersion + extension;
                     Path newPath = logDir.resolve(newName);
                     try {
                         Files.move(logPath, newPath);
                         logger.at(Level.INFO).log("[RPGStats] Archived log: " + fileName + " -> " + newName);
                     } catch (IOException ex) {
                         logger.at(Level.WARNING).log("[RPGStats] Failed to archive log " + fileName + ": " + ex.getMessage());
                     }
                 });
        } catch (IOException ex) {
            logger.at(Level.WARNING).log("[RPGStats] Failed to list log directory: " + ex.getMessage());
        }
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
