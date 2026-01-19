package com.bsnacks.rpgstats.config;

import com.hypixel.hytale.logger.HytaleLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public final class RpgStatsConfig {

    private static final DateTimeFormatter BACKUP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final String FILE_NAME = "config.toml";
    private static final double DEFAULT_XP_MULTIPLIER = 0.35;
    private static final int DEFAULT_MAX_LEVEL = 25;
    private static final double DEFAULT_DAMAGE_MULTIPLIER_BASE = 10.0;
    private static final double DEFAULT_MINING_SPEED_BASE = 1.0;
    private static final double DEFAULT_MINING_SPEED_PER_POINT = 0.10;
    private static final double DEFAULT_HEALTH_PER_POINT = 10.0;
    private static final double DEFAULT_MANA_PER_POINT = 10.0;
    private static final double DEFAULT_STAMINA_PER_POINT = 1.0;

    private double xpMultiplier;
    private int maxLevel;
    private double damageMultiplierBase;
    private double miningSpeedBase;
    private double miningSpeedPerPoint;
    private double healthPerPoint;
    private double manaPerPoint;
    private double staminaPerPoint;

    private RpgStatsConfig(double xpMultiplier, int maxLevel, double damageMultiplierBase,
                           double miningSpeedBase, double miningSpeedPerPoint,
                           double healthPerPoint, double manaPerPoint, double staminaPerPoint) {
        this.xpMultiplier = xpMultiplier;
        this.maxLevel = maxLevel;
        this.damageMultiplierBase = damageMultiplierBase;
        this.miningSpeedBase = miningSpeedBase;
        this.miningSpeedPerPoint = miningSpeedPerPoint;
        this.healthPerPoint = healthPerPoint;
        this.manaPerPoint = manaPerPoint;
        this.staminaPerPoint = staminaPerPoint;
    }

    public double getXpMultiplier() {
        return xpMultiplier;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public double getDamageMultiplierBase() {
        return damageMultiplierBase;
    }

    public double getMiningSpeedBase() {
        return miningSpeedBase;
    }

    public double getMiningSpeedPerPoint() {
        return miningSpeedPerPoint;
    }

    public double getHealthPerPoint() {
        return healthPerPoint;
    }

    public double getManaPerPoint() {
        return manaPerPoint;
    }

    public double getStaminaPerPoint() {
        return staminaPerPoint;
    }

    public void applyFrom(RpgStatsConfig other) {
        if (other == null) {
            return;
        }
        this.xpMultiplier = other.xpMultiplier;
        this.maxLevel = other.maxLevel;
        this.damageMultiplierBase = other.damageMultiplierBase;
        this.miningSpeedBase = other.miningSpeedBase;
        this.miningSpeedPerPoint = other.miningSpeedPerPoint;
        this.healthPerPoint = other.healthPerPoint;
        this.manaPerPoint = other.manaPerPoint;
        this.staminaPerPoint = other.staminaPerPoint;
    }

    public static Path resolveConfigPath(Path dataDirectory) {
        return dataDirectory.resolve(FILE_NAME);
    }

    public static RpgStatsConfig load(Path dataDirectory, HytaleLogger logger) {
        try {
            Files.createDirectories(dataDirectory);
        } catch (IOException ex) {
            logger.at(Level.WARNING).log("[RPGStats] Failed to create data directory: " + ex.getMessage());
        }

        Path configPath = resolveConfigPath(dataDirectory);
        if (!Files.exists(configPath)) {
            writeDefault(configPath, logger);
            return new RpgStatsConfig(
                    DEFAULT_XP_MULTIPLIER,
                    DEFAULT_MAX_LEVEL,
                    DEFAULT_DAMAGE_MULTIPLIER_BASE,
                    DEFAULT_MINING_SPEED_BASE,
                    DEFAULT_MINING_SPEED_PER_POINT,
                    DEFAULT_HEALTH_PER_POINT,
                    DEFAULT_MANA_PER_POINT,
                    DEFAULT_STAMINA_PER_POINT
            );
        }

        Map<String, String> values = readKeyValues(configPath, logger);
        double multiplier = parseDouble(values.get("xp_multiplier"), DEFAULT_XP_MULTIPLIER, logger, "xp_multiplier");
        if (multiplier <= 0) {
            logger.at(Level.WARNING).log("[RPGStats] xp_multiplier must be > 0. Using default " + DEFAULT_XP_MULTIPLIER);
            multiplier = DEFAULT_XP_MULTIPLIER;
        }

        int maxLevel = parseInt(values.get("max_level"), DEFAULT_MAX_LEVEL, logger, "max_level");
        if (maxLevel < 1) {
            logger.at(Level.WARNING).log("[RPGStats] max_level must be >= 1. Using default " + DEFAULT_MAX_LEVEL);
            maxLevel = DEFAULT_MAX_LEVEL;
        }

        double damageBase = parseDouble(values.get("damage_multiplier_base"), DEFAULT_DAMAGE_MULTIPLIER_BASE,
                logger, "damage_multiplier_base");
        if (damageBase <= 0) {
            logger.at(Level.WARNING).log("[RPGStats] damage_multiplier_base must be > 0. Using default " + DEFAULT_DAMAGE_MULTIPLIER_BASE);
            damageBase = DEFAULT_DAMAGE_MULTIPLIER_BASE;
        }

        double miningBase = parseDouble(values.get("mining_speed_base"), DEFAULT_MINING_SPEED_BASE,
                logger, "mining_speed_base");
        double miningPerPoint = parseDouble(values.get("mining_speed_per_point"), DEFAULT_MINING_SPEED_PER_POINT,
                logger, "mining_speed_per_point");

        double healthPerPoint = parseDouble(values.get("health_per_point"), DEFAULT_HEALTH_PER_POINT,
                logger, "health_per_point");
        if (healthPerPoint < 0) {
            logger.at(Level.WARNING).log("[RPGStats] health_per_point must be >= 0. Using default " + DEFAULT_HEALTH_PER_POINT);
            healthPerPoint = DEFAULT_HEALTH_PER_POINT;
        }

        double manaPerPoint = parseDouble(values.get("mana_per_point"), DEFAULT_MANA_PER_POINT,
                logger, "mana_per_point");
        if (manaPerPoint < 0) {
            logger.at(Level.WARNING).log("[RPGStats] mana_per_point must be >= 0. Using default " + DEFAULT_MANA_PER_POINT);
            manaPerPoint = DEFAULT_MANA_PER_POINT;
        }

        double staminaPerPoint = parseDouble(values.get("stamina_per_point"), DEFAULT_STAMINA_PER_POINT,
                logger, "stamina_per_point");
        if (staminaPerPoint < 0) {
            logger.at(Level.WARNING).log("[RPGStats] stamina_per_point must be >= 0. Using default " + DEFAULT_STAMINA_PER_POINT);
            staminaPerPoint = DEFAULT_STAMINA_PER_POINT;
        }

        return new RpgStatsConfig(multiplier, maxLevel, damageBase,
                miningBase, miningPerPoint, healthPerPoint, manaPerPoint, staminaPerPoint);
    }

    private static Map<String, String> readKeyValues(Path configPath, HytaleLogger logger) {
        Map<String, String> values = new HashMap<>();
        List<String> lines;
        try {
            lines = Files.readAllLines(configPath, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            logger.at(Level.WARNING).log("[RPGStats] Failed to read config.toml: " + ex.getMessage());
            return values;
        }

        for (String line : lines) {
            String trimmed = stripComment(line).trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            int equalsIndex = trimmed.indexOf('=');
            if (equalsIndex <= 0) {
                continue;
            }
            String key = trimmed.substring(0, equalsIndex).trim();
            String value = trimmed.substring(equalsIndex + 1).trim();
            if (!key.isEmpty()) {
                values.put(key, value);
            }
        }
        return values;
    }

    private static String stripComment(String line) {
        int hashIndex = line.indexOf('#');
        if (hashIndex >= 0) {
            return line.substring(0, hashIndex);
        }
        return line;
    }

    private static double parseDouble(String raw, double fallback, HytaleLogger logger, String key) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException ex) {
            logger.at(Level.WARNING).log("[RPGStats] Invalid " + key + " value '" + raw + "'. Using default " + fallback);
            return fallback;
        }
    }

    private static int parseInt(String raw, int fallback, HytaleLogger logger, String key) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            logger.at(Level.WARNING).log("[RPGStats] Invalid " + key + " value '" + raw + "'. Using default " + fallback);
            return fallback;
        }
    }

    private static void writeDefault(Path configPath, HytaleLogger logger) {
        String content = ""
                + "# RPGStats configuration\n"
                + "#\n"
                + "# xp_multiplier controls how much XP a player gains from NPC kills (default " + DEFAULT_XP_MULTIPLIER + ").\n"
                + "# Example: 0.35 means XP = maxHealth * 0.35 (before boss multiplier and clamping, max xp gain is set to 1000).\n"
                + "xp_multiplier = " + DEFAULT_XP_MULTIPLIER + "\n"
                + "\n"
                + "# Maximum player level (default " + DEFAULT_MAX_LEVEL + ").\n"
                + "max_level = " + DEFAULT_MAX_LEVEL + "\n"
                + "\n"
                + "# Strength damage multiplier: damage *= (STR / damage_multiplier_base) (default " + DEFAULT_DAMAGE_MULTIPLIER_BASE + ").\n"
                + "# Lower number = more damage. Each point spent into (STR) will add 0.10 to the multiplier.\n"
                + "damage_multiplier_base = " + DEFAULT_DAMAGE_MULTIPLIER_BASE + "\n"
                + "\n"
                + "# Mining speed multiplier: mining_speed_base + mining_speed_per_point * (DEX - base).\n"
                + "# Default base " + DEFAULT_MINING_SPEED_BASE + ", per point " + DEFAULT_MINING_SPEED_PER_POINT + ".\n"
                + "mining_speed_base = " + DEFAULT_MINING_SPEED_BASE + "\n"
                + "mining_speed_per_point = " + DEFAULT_MINING_SPEED_PER_POINT + "\n"
                + "\n"
                + "# Stat gains per point spent (defaults: health " + DEFAULT_HEALTH_PER_POINT
                + ", mana " + DEFAULT_MANA_PER_POINT + ", stamina " + DEFAULT_STAMINA_PER_POINT + ").\n"
                + "# (CON) = Health, (INT) = Mana, (END) = Stamina\n"
                + "health_per_point = " + DEFAULT_HEALTH_PER_POINT + "\n"
                + "mana_per_point = " + DEFAULT_MANA_PER_POINT + "\n"
                + "stamina_per_point = " + DEFAULT_STAMINA_PER_POINT + "\n";
        try {
            Files.writeString(configPath, content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            logger.at(Level.WARNING).log("[RPGStats] Failed to write default config.toml: " + ex.getMessage());
        }
    }

    public static void backup(Path dataDirectory, HytaleLogger logger) {
        Path configPath = dataDirectory.resolve(FILE_NAME);
        if (!Files.exists(configPath)) {
            return;
        }
        String stamp = BACKUP_FORMAT.format(LocalDateTime.now());
        Path backupPath = dataDirectory.resolve("config-" + stamp + ".toml.bak");
        try {
            Files.copy(configPath, backupPath);
        } catch (IOException ex) {
            logger.at(Level.WARNING).log("[RPGStats] Failed to back up config.toml: " + ex.getMessage());
        }
    }
}
