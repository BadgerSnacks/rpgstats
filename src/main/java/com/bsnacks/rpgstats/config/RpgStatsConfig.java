package com.bsnacks.rpgstats.config;

import com.hypixel.hytale.logger.HytaleLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public final class RpgStatsConfig {

    private static final DateTimeFormatter BACKUP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final String FILE_NAME = "config.toml";
    private static final String XP_BLACKLIST_FILE_NAME = "xp_blacklist.toml";
    private static final String MINING_XP_FILE_NAME = "mining_xp.toml";
    private static final int CURRENT_CONFIG_VERSION = 9;
    private static final double DEFAULT_XP_MULTIPLIER = 0.35;
    private static final int DEFAULT_MAX_LEVEL = 25;
    private static final int DEFAULT_ABILITY_POINTS_PER_LEVEL = 2;
    private static final int MIN_ABILITY_POINTS_PER_LEVEL = 0;
    private static final int DEFAULT_ABILITY_RANK1_COST = 1;
    private static final int DEFAULT_ABILITY_RANK2_COST = 2;
    private static final int DEFAULT_ABILITY_RANK3_COST = 3;
    private static final int MIN_ABILITY_RANK_COST = 0;
    private static final int DEFAULT_STAT_CAP = 25;
    private static final double DEFAULT_LIGHT_FOOT_SPEED_PER_LEVEL_PCT = 5.0;
    private static final double DEFAULT_ARMOR_PROFICIENCY_RESISTANCE_PER_LEVEL_PCT = 5.0;
    private static final double MIN_ABILITY_BONUS_PCT = 0.0;
    private static final double MAX_ABILITY_BONUS_PCT = 100.0;
    private static final double DEFAULT_HEALTH_REGEN_PER_LEVEL_PER_SEC = 1.0;
    private static final double DEFAULT_STAMINA_REGEN_PER_LEVEL_PER_SEC = 0.15;
    private static final double MIN_REGEN_PER_LEVEL_PER_SEC = 0.0;
    private static final double MAX_REGEN_PER_LEVEL_PER_SEC = 100.0;
    private static final double DEFAULT_GLANCING_BLOW_CHANCE_PER_LEVEL_PCT = 5.0;
    private static final double DEFAULT_STRONG_LUNGS_OXYGEN_PER_LEVEL_PCT = 100.0;
    private static final double DEFAULT_LUCKY_SHOT_CHANCE_PER_LEVEL_PCT = 10.0;
    private static final double DEFAULT_CRITICAL_STRIKE_CHANCE_PER_LEVEL_PCT = 5.0;
    private static final double DEFAULT_CRITICAL_STRIKE_BASE_CHANCE_PCT = 5.0;
    private static final double DEFAULT_CRITICAL_STRIKE_DAMAGE_MULTIPLIER = 1.5;
    private static final double DEFAULT_LIFESTEAL_PER_LEVEL_PCT = 3.0;
    private static final double DEFAULT_THORNS_REFLECT_PER_LEVEL_PCT = 25.0;
    private static final double DEFAULT_TOOL_PROFICIENCY_CHANCE_PER_LEVEL_PCT = 15.0;
    private static final double DEFAULT_LUCKY_MINER_CHANCE_PER_LEVEL_PCT = 10.0;
    private static final double DEFAULT_FLAME_TOUCH_DAMAGE_PER_LEVEL = 2.0;
    private static final double DEFAULT_GOURMAND_FOOD_BONUS_PER_LEVEL_PCT = 10.0;
    private static final double DEFAULT_DAMAGE_MULTIPLIER_BASE = 10.0;
    private static final double DEFAULT_MINING_SPEED_BASE = 1.0;
    private static final double DEFAULT_MINING_SPEED_PER_POINT = 0.10;
    private static final double DEFAULT_HEALTH_PER_POINT = 10.0;
    private static final double DEFAULT_MANA_PER_POINT = 10.0;
    private static final double DEFAULT_STAMINA_PER_POINT = 1.0;
    private static final boolean DEFAULT_HUD_ENABLED = true;

    private int configVersion;
    private double xpMultiplier;
    private int maxLevel;
    private int abilityPointsPerLevel;
    private double lightFootSpeedPerLevelPct;
    private double armorProficiencyResistancePerLevelPct;
    private double healthRegenPerLevelPerSec;
    private double staminaRegenPerLevelPerSec;
    private double glancingBlowChancePerLevelPct;
    private double damageMultiplierBase;
    private double miningSpeedBase;
    private double miningSpeedPerPoint;
    private double healthPerPoint;
    private double manaPerPoint;
    private double staminaPerPoint;
    private boolean hudEnabled;
    private int strCap;
    private int dexCap;
    private int conCap;
    private int intCap;
    private int endCap;
    private int chaCap;
    private Set<String> xpBlacklistNpcTypes;
    private Set<String> xpBlacklistRoles;
    private Map<String, Integer> miningXpByBlockId;
    private double strongLungsOxygenPerLevelPct;
    private double luckyShotChancePerLevelPct;
    private double criticalStrikeChancePerLevelPct;
    private double criticalStrikeBaseChancePct;
    private double criticalStrikeDamageMultiplier;
    private double lifestealPerLevelPct;
    private double thornsReflectPerLevelPct;
    private double toolProficiencyChancePerLevelPct;
    private double luckyMinerChancePerLevelPct;
    private double flameTouchDamagePerLevel;
    private double gourmandFoodBonusPerLevelPct;
    private int abilityRank1Cost;
    private int abilityRank2Cost;
    private int abilityRank3Cost;

    private RpgStatsConfig(int configVersion, double xpMultiplier, int maxLevel, int abilityPointsPerLevel,
                           double lightFootSpeedPerLevelPct, double armorProficiencyResistancePerLevelPct,
                           double healthRegenPerLevelPerSec, double staminaRegenPerLevelPerSec,
                           double glancingBlowChancePerLevelPct,
                           double damageMultiplierBase,
                           double miningSpeedBase, double miningSpeedPerPoint,
                           double healthPerPoint, double manaPerPoint, double staminaPerPoint,
                           boolean hudEnabled,
                           int strCap, int dexCap, int conCap, int intCap, int endCap, int chaCap,
                           Set<String> xpBlacklistNpcTypes, Set<String> xpBlacklistRoles,
                           Map<String, Integer> miningXpByBlockId,
                           double strongLungsOxygenPerLevelPct, double luckyShotChancePerLevelPct,
                           double criticalStrikeChancePerLevelPct, double criticalStrikeBaseChancePct,
                           double criticalStrikeDamageMultiplier, double lifestealPerLevelPct,
                           double thornsReflectPerLevelPct, double toolProficiencyChancePerLevelPct,
                           double luckyMinerChancePerLevelPct, double flameTouchDamagePerLevel,
                           double gourmandFoodBonusPerLevelPct,
                           int abilityRank1Cost, int abilityRank2Cost, int abilityRank3Cost) {
        this.configVersion = configVersion;
        this.xpMultiplier = xpMultiplier;
        this.maxLevel = maxLevel;
        this.abilityPointsPerLevel = abilityPointsPerLevel;
        this.lightFootSpeedPerLevelPct = lightFootSpeedPerLevelPct;
        this.armorProficiencyResistancePerLevelPct = armorProficiencyResistancePerLevelPct;
        this.healthRegenPerLevelPerSec = healthRegenPerLevelPerSec;
        this.staminaRegenPerLevelPerSec = staminaRegenPerLevelPerSec;
        this.glancingBlowChancePerLevelPct = glancingBlowChancePerLevelPct;
        this.damageMultiplierBase = damageMultiplierBase;
        this.miningSpeedBase = miningSpeedBase;
        this.miningSpeedPerPoint = miningSpeedPerPoint;
        this.healthPerPoint = healthPerPoint;
        this.manaPerPoint = manaPerPoint;
        this.staminaPerPoint = staminaPerPoint;
        this.hudEnabled = hudEnabled;
        this.strCap = strCap;
        this.dexCap = dexCap;
        this.conCap = conCap;
        this.intCap = intCap;
        this.endCap = endCap;
        this.chaCap = chaCap;
        this.xpBlacklistNpcTypes = xpBlacklistNpcTypes;
        this.xpBlacklistRoles = xpBlacklistRoles;
        this.miningXpByBlockId = miningXpByBlockId;
        this.strongLungsOxygenPerLevelPct = strongLungsOxygenPerLevelPct;
        this.luckyShotChancePerLevelPct = luckyShotChancePerLevelPct;
        this.criticalStrikeChancePerLevelPct = criticalStrikeChancePerLevelPct;
        this.criticalStrikeBaseChancePct = criticalStrikeBaseChancePct;
        this.criticalStrikeDamageMultiplier = criticalStrikeDamageMultiplier;
        this.lifestealPerLevelPct = lifestealPerLevelPct;
        this.thornsReflectPerLevelPct = thornsReflectPerLevelPct;
        this.toolProficiencyChancePerLevelPct = toolProficiencyChancePerLevelPct;
        this.luckyMinerChancePerLevelPct = luckyMinerChancePerLevelPct;
        this.flameTouchDamagePerLevel = flameTouchDamagePerLevel;
        this.gourmandFoodBonusPerLevelPct = gourmandFoodBonusPerLevelPct;
        this.abilityRank1Cost = abilityRank1Cost;
        this.abilityRank2Cost = abilityRank2Cost;
        this.abilityRank3Cost = abilityRank3Cost;
    }

    public double getXpMultiplier() {
        return xpMultiplier;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getAbilityPointsPerLevel() {
        return abilityPointsPerLevel;
    }

    public double getLightFootSpeedPerLevelPct() {
        return lightFootSpeedPerLevelPct;
    }

    public double getArmorProficiencyResistancePerLevelPct() {
        return armorProficiencyResistancePerLevelPct;
    }

    public double getHealthRegenPerLevelPerSec() {
        return healthRegenPerLevelPerSec;
    }

    public double getStaminaRegenPerLevelPerSec() {
        return staminaRegenPerLevelPerSec;
    }

    public double getGlancingBlowChancePerLevelPct() {
        return glancingBlowChancePerLevelPct;
    }

    public double getStrongLungsOxygenPerLevelPct() {
        return strongLungsOxygenPerLevelPct;
    }

    public double getLuckyShotChancePerLevelPct() {
        return luckyShotChancePerLevelPct;
    }

    public double getCriticalStrikeChancePerLevelPct() {
        return criticalStrikeChancePerLevelPct;
    }

    public double getCriticalStrikeBaseChancePct() {
        return criticalStrikeBaseChancePct;
    }

    public double getCriticalStrikeDamageMultiplier() {
        return criticalStrikeDamageMultiplier;
    }

    public double getLifestealPerLevelPct() {
        return lifestealPerLevelPct;
    }

    public double getThornsReflectPerLevelPct() {
        return thornsReflectPerLevelPct;
    }

    public double getToolProficiencyChancePerLevelPct() {
        return toolProficiencyChancePerLevelPct;
    }

    public double getLuckyMinerChancePerLevelPct() {
        return luckyMinerChancePerLevelPct;
    }

    public double getFlameTouchDamagePerLevel() {
        return flameTouchDamagePerLevel;
    }

    public double getGourmandFoodBonusPerLevelPct() {
        return gourmandFoodBonusPerLevelPct;
    }

    public int getAbilityRank1Cost() {
        return abilityRank1Cost;
    }

    public int getAbilityRank2Cost() {
        return abilityRank2Cost;
    }

    public int getAbilityRank3Cost() {
        return abilityRank3Cost;
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

    public boolean isHudEnabled() {
        return hudEnabled;
    }

    public int getStatCap(String attribute) {
        if (attribute == null) {
            return DEFAULT_STAT_CAP;
        }
        switch (attribute) {
            case "str":
                return strCap;
            case "dex":
                return dexCap;
            case "con":
                return conCap;
            case "int":
                return intCap;
            case "end":
                return endCap;
            case "cha":
                return chaCap;
            default:
                return DEFAULT_STAT_CAP;
        }
    }

    public Set<String> getXpBlacklistNpcTypes() {
        return xpBlacklistNpcTypes;
    }

    public Set<String> getXpBlacklistRoles() {
        return xpBlacklistRoles;
    }

    public int getMiningXpForBlock(String blockId) {
        if (blockId == null || miningXpByBlockId == null || miningXpByBlockId.isEmpty()) {
            return 0;
        }
        String key = blockId.toLowerCase();
        Integer xp = miningXpByBlockId.get(key);
        if (xp != null) {
            return xp;
        }
        for (Map.Entry<String, Integer> entry : miningXpByBlockId.entrySet()) {
            String configured = entry.getKey();
            if (configured.endsWith("*")) {
                String prefix = configured.substring(0, configured.length() - 1);
                if (!prefix.isEmpty() && key.startsWith(prefix)) {
                    return entry.getValue();
                }
            }
        }
        return xp == null ? 0 : xp;
    }

    public int getMiningXpEntryCount() {
        return miningXpByBlockId == null ? 0 : miningXpByBlockId.size();
    }

    public boolean isXpBlacklisted(String npcTypeId, String roleName) {
        if (npcTypeId != null && xpBlacklistNpcTypes != null
                && xpBlacklistNpcTypes.contains(npcTypeId.toLowerCase())) {
            return true;
        }
        if (roleName != null && xpBlacklistRoles != null
                && xpBlacklistRoles.contains(roleName.toLowerCase())) {
            return true;
        }
        return false;
    }

    public void applyFrom(RpgStatsConfig other) {
        if (other == null) {
            return;
        }
        this.configVersion = other.configVersion;
        this.xpMultiplier = other.xpMultiplier;
        this.maxLevel = other.maxLevel;
        this.abilityPointsPerLevel = other.abilityPointsPerLevel;
        this.lightFootSpeedPerLevelPct = other.lightFootSpeedPerLevelPct;
        this.armorProficiencyResistancePerLevelPct = other.armorProficiencyResistancePerLevelPct;
        this.healthRegenPerLevelPerSec = other.healthRegenPerLevelPerSec;
        this.staminaRegenPerLevelPerSec = other.staminaRegenPerLevelPerSec;
        this.glancingBlowChancePerLevelPct = other.glancingBlowChancePerLevelPct;
        this.strongLungsOxygenPerLevelPct = other.strongLungsOxygenPerLevelPct;
        this.luckyShotChancePerLevelPct = other.luckyShotChancePerLevelPct;
        this.damageMultiplierBase = other.damageMultiplierBase;
        this.miningSpeedBase = other.miningSpeedBase;
        this.miningSpeedPerPoint = other.miningSpeedPerPoint;
        this.healthPerPoint = other.healthPerPoint;
        this.manaPerPoint = other.manaPerPoint;
        this.staminaPerPoint = other.staminaPerPoint;
        this.hudEnabled = other.hudEnabled;
        this.strCap = other.strCap;
        this.dexCap = other.dexCap;
        this.conCap = other.conCap;
        this.intCap = other.intCap;
        this.endCap = other.endCap;
        this.chaCap = other.chaCap;
        this.xpBlacklistNpcTypes = other.xpBlacklistNpcTypes;
        this.xpBlacklistRoles = other.xpBlacklistRoles;
        this.miningXpByBlockId = other.miningXpByBlockId;
        this.criticalStrikeChancePerLevelPct = other.criticalStrikeChancePerLevelPct;
        this.criticalStrikeBaseChancePct = other.criticalStrikeBaseChancePct;
        this.criticalStrikeDamageMultiplier = other.criticalStrikeDamageMultiplier;
        this.lifestealPerLevelPct = other.lifestealPerLevelPct;
        this.thornsReflectPerLevelPct = other.thornsReflectPerLevelPct;
        this.toolProficiencyChancePerLevelPct = other.toolProficiencyChancePerLevelPct;
        this.luckyMinerChancePerLevelPct = other.luckyMinerChancePerLevelPct;
        this.flameTouchDamagePerLevel = other.flameTouchDamagePerLevel;
        this.gourmandFoodBonusPerLevelPct = other.gourmandFoodBonusPerLevelPct;
        this.abilityRank1Cost = other.abilityRank1Cost;
        this.abilityRank2Cost = other.abilityRank2Cost;
        this.abilityRank3Cost = other.abilityRank3Cost;
    }

    public static Path resolveConfigPath(Path dataDirectory) {
        return dataDirectory.resolve(FILE_NAME);
    }

    public static Path resolveXpBlacklistPath(Path dataDirectory) {
        return dataDirectory.resolve(XP_BLACKLIST_FILE_NAME);
    }

    public static Path resolveMiningXpPath(Path dataDirectory) {
        return dataDirectory.resolve(MINING_XP_FILE_NAME);
    }

    public static RpgStatsConfig load(Path dataDirectory, HytaleLogger logger) {
        try {
            Files.createDirectories(dataDirectory);
        } catch (IOException ex) {
            logger.at(Level.WARNING).log("[RPGStats] Failed to create data directory: " + ex.getMessage());
        }

        XpBlacklist xpBlacklist = readXpBlacklist(dataDirectory, logger);
        MiningXp miningXp = readMiningXp(dataDirectory, logger);

        Path configPath = resolveConfigPath(dataDirectory);
        if (!Files.exists(configPath)) {
            writeDefault(configPath, logger);
            return new RpgStatsConfig(
                    CURRENT_CONFIG_VERSION,
                    DEFAULT_XP_MULTIPLIER,
                    DEFAULT_MAX_LEVEL,
                    DEFAULT_ABILITY_POINTS_PER_LEVEL,
                    DEFAULT_LIGHT_FOOT_SPEED_PER_LEVEL_PCT,
                    DEFAULT_ARMOR_PROFICIENCY_RESISTANCE_PER_LEVEL_PCT,
                    DEFAULT_HEALTH_REGEN_PER_LEVEL_PER_SEC,
                    DEFAULT_STAMINA_REGEN_PER_LEVEL_PER_SEC,
                    DEFAULT_GLANCING_BLOW_CHANCE_PER_LEVEL_PCT,
                    DEFAULT_DAMAGE_MULTIPLIER_BASE,
                    DEFAULT_MINING_SPEED_BASE,
                    DEFAULT_MINING_SPEED_PER_POINT,
                    DEFAULT_HEALTH_PER_POINT,
                    DEFAULT_MANA_PER_POINT,
                    DEFAULT_STAMINA_PER_POINT,
                    DEFAULT_HUD_ENABLED,
                    DEFAULT_STAT_CAP,
                    DEFAULT_STAT_CAP,
                    DEFAULT_STAT_CAP,
                    DEFAULT_STAT_CAP,
                    DEFAULT_STAT_CAP,
                    DEFAULT_STAT_CAP,
                    xpBlacklist.npcTypes,
                    xpBlacklist.roles,
                    miningXp.blockXp,
                    DEFAULT_STRONG_LUNGS_OXYGEN_PER_LEVEL_PCT,
                    DEFAULT_LUCKY_SHOT_CHANCE_PER_LEVEL_PCT,
                    DEFAULT_CRITICAL_STRIKE_CHANCE_PER_LEVEL_PCT,
                    DEFAULT_CRITICAL_STRIKE_BASE_CHANCE_PCT,
                    DEFAULT_CRITICAL_STRIKE_DAMAGE_MULTIPLIER,
                    DEFAULT_LIFESTEAL_PER_LEVEL_PCT,
                    DEFAULT_THORNS_REFLECT_PER_LEVEL_PCT,
                    DEFAULT_TOOL_PROFICIENCY_CHANCE_PER_LEVEL_PCT,
                    DEFAULT_LUCKY_MINER_CHANCE_PER_LEVEL_PCT,
                    DEFAULT_FLAME_TOUCH_DAMAGE_PER_LEVEL,
                    DEFAULT_GOURMAND_FOOD_BONUS_PER_LEVEL_PCT,
                    DEFAULT_ABILITY_RANK1_COST,
                    DEFAULT_ABILITY_RANK2_COST,
                    DEFAULT_ABILITY_RANK3_COST
            );
        }

        Map<String, String> values = readKeyValues(configPath, logger);
        int configVersion = parseInt(values.get("config_version"), 0, logger, "config_version");
        if (configVersion < 1) {
            logger.at(Level.INFO).log("[RPGStats] config_version missing or invalid. Assuming 0.");
        }
        if (configVersion > 0 && configVersion < CURRENT_CONFIG_VERSION) {
            logger.at(Level.INFO).log("[RPGStats] config_version " + configVersion
                    + " is older than latest " + CURRENT_CONFIG_VERSION + ". New settings may be missing.");
        }
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

        int abilityPointsPerLevel = parseInt(values.get("ability_points_per_level"),
                DEFAULT_ABILITY_POINTS_PER_LEVEL, logger, "ability_points_per_level");
        if (abilityPointsPerLevel < MIN_ABILITY_POINTS_PER_LEVEL) {
            logger.at(Level.WARNING).log("[RPGStats] ability_points_per_level must be >= "
                    + MIN_ABILITY_POINTS_PER_LEVEL + ". Using default " + DEFAULT_ABILITY_POINTS_PER_LEVEL);
            abilityPointsPerLevel = DEFAULT_ABILITY_POINTS_PER_LEVEL;
        }
        int maxAbilityPointsPerLevel = maxAbilityPointsPerLevel(maxLevel);
        if (abilityPointsPerLevel > maxAbilityPointsPerLevel) {
            logger.at(Level.WARNING).log("[RPGStats] ability_points_per_level must be <= "
                    + maxAbilityPointsPerLevel + " for max_level " + maxLevel + ". Clamping.");
            abilityPointsPerLevel = maxAbilityPointsPerLevel;
        }

        double lightFootSpeedPerLevelPct = parseDouble(values.get("light_foot_speed_per_level_pct"),
                DEFAULT_LIGHT_FOOT_SPEED_PER_LEVEL_PCT, logger, "light_foot_speed_per_level_pct");
        lightFootSpeedPerLevelPct = clampAbilityPct(lightFootSpeedPerLevelPct, logger, "light_foot_speed_per_level_pct",
                DEFAULT_LIGHT_FOOT_SPEED_PER_LEVEL_PCT);

        double armorProficiencyResistancePerLevelPct = parseDouble(values.get("armor_proficiency_resistance_per_level_pct"),
                DEFAULT_ARMOR_PROFICIENCY_RESISTANCE_PER_LEVEL_PCT, logger, "armor_proficiency_resistance_per_level_pct");
        armorProficiencyResistancePerLevelPct = clampAbilityPct(armorProficiencyResistancePerLevelPct, logger,
                "armor_proficiency_resistance_per_level_pct", DEFAULT_ARMOR_PROFICIENCY_RESISTANCE_PER_LEVEL_PCT);

        double healthRegenPerLevelPerSec = parseDouble(values.get("health_regen_per_level_per_sec"),
                DEFAULT_HEALTH_REGEN_PER_LEVEL_PER_SEC, logger, "health_regen_per_level_per_sec");
        healthRegenPerLevelPerSec = clampRegenPerLevel(healthRegenPerLevelPerSec, logger,
                "health_regen_per_level_per_sec", DEFAULT_HEALTH_REGEN_PER_LEVEL_PER_SEC);

        double staminaRegenPerLevelPerSec = parseDouble(values.get("stamina_regen_per_level_per_sec"),
                DEFAULT_STAMINA_REGEN_PER_LEVEL_PER_SEC, logger, "stamina_regen_per_level_per_sec");
        staminaRegenPerLevelPerSec = clampRegenPerLevel(staminaRegenPerLevelPerSec, logger,
                "stamina_regen_per_level_per_sec", DEFAULT_STAMINA_REGEN_PER_LEVEL_PER_SEC);

        double glancingBlowChancePerLevelPct = parseDouble(values.get("glancing_blow_chance_per_level_pct"),
                DEFAULT_GLANCING_BLOW_CHANCE_PER_LEVEL_PCT, logger, "glancing_blow_chance_per_level_pct");
        glancingBlowChancePerLevelPct = clampAbilityPct(glancingBlowChancePerLevelPct, logger,
                "glancing_blow_chance_per_level_pct", DEFAULT_GLANCING_BLOW_CHANCE_PER_LEVEL_PCT);

        double strongLungsOxygenPerLevelPct = parseDouble(values.get("strong_lungs_oxygen_per_level_pct"),
                DEFAULT_STRONG_LUNGS_OXYGEN_PER_LEVEL_PCT, logger, "strong_lungs_oxygen_per_level_pct");
        strongLungsOxygenPerLevelPct = clampAbilityPct(strongLungsOxygenPerLevelPct, logger,
                "strong_lungs_oxygen_per_level_pct", DEFAULT_STRONG_LUNGS_OXYGEN_PER_LEVEL_PCT);

        double luckyShotChancePerLevelPct = parseDouble(values.get("lucky_shot_chance_per_level_pct"),
                DEFAULT_LUCKY_SHOT_CHANCE_PER_LEVEL_PCT, logger, "lucky_shot_chance_per_level_pct");
        luckyShotChancePerLevelPct = clampAbilityPct(luckyShotChancePerLevelPct, logger,
                "lucky_shot_chance_per_level_pct", DEFAULT_LUCKY_SHOT_CHANCE_PER_LEVEL_PCT);

        double criticalStrikeChancePerLevelPct = parseDouble(values.get("critical_strike_chance_per_level_pct"),
                DEFAULT_CRITICAL_STRIKE_CHANCE_PER_LEVEL_PCT, logger, "critical_strike_chance_per_level_pct");
        criticalStrikeChancePerLevelPct = clampAbilityPct(criticalStrikeChancePerLevelPct, logger,
                "critical_strike_chance_per_level_pct", DEFAULT_CRITICAL_STRIKE_CHANCE_PER_LEVEL_PCT);

        double criticalStrikeBaseChancePct = parseDouble(values.get("critical_strike_base_chance_pct"),
                DEFAULT_CRITICAL_STRIKE_BASE_CHANCE_PCT, logger, "critical_strike_base_chance_pct");
        criticalStrikeBaseChancePct = clampAbilityPct(criticalStrikeBaseChancePct, logger,
                "critical_strike_base_chance_pct", DEFAULT_CRITICAL_STRIKE_BASE_CHANCE_PCT);

        double criticalStrikeDamageMultiplier = parseDouble(values.get("critical_strike_damage_multiplier"),
                DEFAULT_CRITICAL_STRIKE_DAMAGE_MULTIPLIER, logger, "critical_strike_damage_multiplier");
        if (criticalStrikeDamageMultiplier < 1.0) {
            logger.at(Level.WARNING).log("[RPGStats] critical_strike_damage_multiplier must be >= 1.0. Using default " + DEFAULT_CRITICAL_STRIKE_DAMAGE_MULTIPLIER);
            criticalStrikeDamageMultiplier = DEFAULT_CRITICAL_STRIKE_DAMAGE_MULTIPLIER;
        }

        double lifestealPerLevelPct = parseDouble(values.get("lifesteal_per_level_pct"),
                DEFAULT_LIFESTEAL_PER_LEVEL_PCT, logger, "lifesteal_per_level_pct");
        lifestealPerLevelPct = clampAbilityPct(lifestealPerLevelPct, logger,
                "lifesteal_per_level_pct", DEFAULT_LIFESTEAL_PER_LEVEL_PCT);

        double thornsReflectPerLevelPct = parseDouble(values.get("thorns_reflect_per_level_pct"),
                DEFAULT_THORNS_REFLECT_PER_LEVEL_PCT, logger, "thorns_reflect_per_level_pct");
        thornsReflectPerLevelPct = clampAbilityPct(thornsReflectPerLevelPct, logger,
                "thorns_reflect_per_level_pct", DEFAULT_THORNS_REFLECT_PER_LEVEL_PCT);

        double toolProficiencyChancePerLevelPct = parseDouble(values.get("tool_proficiency_chance_per_level_pct"),
                DEFAULT_TOOL_PROFICIENCY_CHANCE_PER_LEVEL_PCT, logger, "tool_proficiency_chance_per_level_pct");
        toolProficiencyChancePerLevelPct = clampAbilityPct(toolProficiencyChancePerLevelPct, logger,
                "tool_proficiency_chance_per_level_pct", DEFAULT_TOOL_PROFICIENCY_CHANCE_PER_LEVEL_PCT);

        double luckyMinerChancePerLevelPct = parseDouble(values.get("lucky_miner_chance_per_level_pct"),
                DEFAULT_LUCKY_MINER_CHANCE_PER_LEVEL_PCT, logger, "lucky_miner_chance_per_level_pct");
        luckyMinerChancePerLevelPct = clampAbilityPct(luckyMinerChancePerLevelPct, logger,
                "lucky_miner_chance_per_level_pct", DEFAULT_LUCKY_MINER_CHANCE_PER_LEVEL_PCT);

        double flameTouchDamagePerLevel = parseDouble(values.get("flame_touch_damage_per_level"),
                DEFAULT_FLAME_TOUCH_DAMAGE_PER_LEVEL, logger, "flame_touch_damage_per_level");
        if (flameTouchDamagePerLevel < 0.0) {
            logger.at(Level.WARNING).log("[RPGStats] flame_touch_damage_per_level must be >= 0. Using default " + DEFAULT_FLAME_TOUCH_DAMAGE_PER_LEVEL);
            flameTouchDamagePerLevel = DEFAULT_FLAME_TOUCH_DAMAGE_PER_LEVEL;
        }

        double gourmandFoodBonusPerLevelPct = parseDouble(values.get("gourmand_food_bonus_per_level_pct"),
                DEFAULT_GOURMAND_FOOD_BONUS_PER_LEVEL_PCT, logger, "gourmand_food_bonus_per_level_pct");
        gourmandFoodBonusPerLevelPct = clampAbilityPct(gourmandFoodBonusPerLevelPct, logger,
                "gourmand_food_bonus_per_level_pct", DEFAULT_GOURMAND_FOOD_BONUS_PER_LEVEL_PCT);

        int abilityRank1Cost = parseInt(values.get("ability_rank1_cost"),
                DEFAULT_ABILITY_RANK1_COST, logger, "ability_rank1_cost");
        if (abilityRank1Cost < MIN_ABILITY_RANK_COST) {
            logger.at(Level.WARNING).log("[RPGStats] ability_rank1_cost must be >= "
                    + MIN_ABILITY_RANK_COST + ". Using default " + DEFAULT_ABILITY_RANK1_COST);
            abilityRank1Cost = DEFAULT_ABILITY_RANK1_COST;
        }

        int abilityRank2Cost = parseInt(values.get("ability_rank2_cost"),
                DEFAULT_ABILITY_RANK2_COST, logger, "ability_rank2_cost");
        if (abilityRank2Cost < MIN_ABILITY_RANK_COST) {
            logger.at(Level.WARNING).log("[RPGStats] ability_rank2_cost must be >= "
                    + MIN_ABILITY_RANK_COST + ". Using default " + DEFAULT_ABILITY_RANK2_COST);
            abilityRank2Cost = DEFAULT_ABILITY_RANK2_COST;
        }

        int abilityRank3Cost = parseInt(values.get("ability_rank3_cost"),
                DEFAULT_ABILITY_RANK3_COST, logger, "ability_rank3_cost");
        if (abilityRank3Cost < MIN_ABILITY_RANK_COST) {
            logger.at(Level.WARNING).log("[RPGStats] ability_rank3_cost must be >= "
                    + MIN_ABILITY_RANK_COST + ". Using default " + DEFAULT_ABILITY_RANK3_COST);
            abilityRank3Cost = DEFAULT_ABILITY_RANK3_COST;
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

        boolean hudEnabled = parseBoolean(values.get("hud_enabled"), DEFAULT_HUD_ENABLED, logger, "hud_enabled");

        int strCap = parseCap(values.get("str_cap"), "str_cap", logger);
        int dexCap = parseCap(values.get("dex_cap"), "dex_cap", logger);
        int conCap = parseCap(values.get("con_cap"), "con_cap", logger);
        int intCap = parseCap(values.get("int_cap"), "int_cap", logger);
        int endCap = parseCap(values.get("end_cap"), "end_cap", logger);
        int chaCap = parseCap(values.get("cha_cap"), "cha_cap", logger);

        Set<String> legacyNpcTypes = parseStringSet(values.get("xp_blacklist_npc_types"));
        Set<String> legacyRoles = parseStringSet(values.get("xp_blacklist_roles"));
        if (!legacyNpcTypes.isEmpty() || !legacyRoles.isEmpty()) {
            logger.at(Level.INFO).log("[RPGStats] xp_blacklist_npc_types/xp_blacklist_roles are deprecated. Use xp_blacklist.toml.");
        }
        Set<String> xpBlacklistNpcTypes = mergeSets(xpBlacklist.npcTypes, legacyNpcTypes);
        Set<String> xpBlacklistRoles = mergeSets(xpBlacklist.roles, legacyRoles);

        return new RpgStatsConfig(configVersion, multiplier, maxLevel, abilityPointsPerLevel,
                lightFootSpeedPerLevelPct, armorProficiencyResistancePerLevelPct,
                healthRegenPerLevelPerSec, staminaRegenPerLevelPerSec, glancingBlowChancePerLevelPct,
                damageBase, miningBase, miningPerPoint, healthPerPoint, manaPerPoint, staminaPerPoint,
                hudEnabled,
                strCap, dexCap, conCap, intCap, endCap, chaCap,
                xpBlacklistNpcTypes, xpBlacklistRoles, miningXp.blockXp,
                strongLungsOxygenPerLevelPct, luckyShotChancePerLevelPct,
                criticalStrikeChancePerLevelPct, criticalStrikeBaseChancePct, criticalStrikeDamageMultiplier,
                lifestealPerLevelPct, thornsReflectPerLevelPct, toolProficiencyChancePerLevelPct,
                luckyMinerChancePerLevelPct, flameTouchDamagePerLevel, gourmandFoodBonusPerLevelPct,
                abilityRank1Cost, abilityRank2Cost, abilityRank3Cost);
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

        for (int i = 0; i < lines.size(); i++) {
            String trimmed = stripComment(lines.get(i)).trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            int equalsIndex = trimmed.indexOf('=');
            if (equalsIndex <= 0) {
                continue;
            }
            String key = trimmed.substring(0, equalsIndex).trim();
            String value = trimmed.substring(equalsIndex + 1).trim();
            if (value.startsWith("[") && !value.contains("]")) {
                StringBuilder builder = new StringBuilder(value);
                while (i + 1 < lines.size()) {
                    i++;
                    String next = stripComment(lines.get(i)).trim();
                    if (next.isEmpty()) {
                        continue;
                    }
                    builder.append(' ').append(next);
                    if (next.contains("]")) {
                        break;
                    }
                }
                value = builder.toString();
            }
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

    private static XpBlacklist readXpBlacklist(Path dataDirectory, HytaleLogger logger) {
        Path blacklistPath = resolveXpBlacklistPath(dataDirectory);
        if (!Files.exists(blacklistPath)) {
            writeDefaultXpBlacklist(blacklistPath, logger);
            if (!Files.exists(blacklistPath)) {
                return new XpBlacklist(Collections.emptySet(), Collections.emptySet());
            }
        }
        Map<String, String> values = readKeyValues(blacklistPath, logger);
        Set<String> npcTypes = parseStringSet(values.get("npc_types"));
        Set<String> roles = parseStringSet(values.get("roles"));
        return new XpBlacklist(npcTypes, roles);
    }

    private static MiningXp readMiningXp(Path dataDirectory, HytaleLogger logger) {
        Path miningXpPath = resolveMiningXpPath(dataDirectory);
        if (!Files.exists(miningXpPath)) {
            writeDefaultMiningXp(miningXpPath, logger);
            if (!Files.exists(miningXpPath)) {
                return new MiningXp(Collections.emptyMap());
            }
        }
        Map<String, String> values = readKeyValues(miningXpPath, logger);
        Set<String> blockEntries = parseStringSet(values.get("block_xp"));
        Map<String, Integer> blockXp = parseMiningXpEntries(blockEntries, logger);
        return new MiningXp(blockXp);
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

    private static boolean parseBoolean(String raw, boolean fallback, HytaleLogger logger, String key) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        if ("true".equalsIgnoreCase(raw)) {
            return true;
        }
        if ("false".equalsIgnoreCase(raw)) {
            return false;
        }
        logger.at(Level.WARNING).log("[RPGStats] Invalid " + key + " value '" + raw + "'. Using default " + fallback);
        return fallback;
    }

    private static int parseCap(String raw, String key, HytaleLogger logger) {
        int cap = parseInt(raw, DEFAULT_STAT_CAP, logger, key);
        if (cap < 1) {
            logger.at(Level.WARNING).log("[RPGStats] " + key + " must be >= 1. Using default " + DEFAULT_STAT_CAP);
            return DEFAULT_STAT_CAP;
        }
        return cap;
    }

    private static double clampAbilityPct(double value, HytaleLogger logger, String key, double fallback) {
        if (value < MIN_ABILITY_BONUS_PCT) {
            logger.at(Level.WARNING).log("[RPGStats] " + key + " must be >= " + MIN_ABILITY_BONUS_PCT
                    + ". Using default " + fallback);
            return fallback;
        }
        if (value > MAX_ABILITY_BONUS_PCT) {
            logger.at(Level.WARNING).log("[RPGStats] " + key + " must be <= " + MAX_ABILITY_BONUS_PCT
                    + ". Clamping.");
            return MAX_ABILITY_BONUS_PCT;
        }
        return value;
    }

    private static double clampRegenPerLevel(double value, HytaleLogger logger, String key, double fallback) {
        if (value < MIN_REGEN_PER_LEVEL_PER_SEC) {
            logger.at(Level.WARNING).log("[RPGStats] " + key + " must be >= " + MIN_REGEN_PER_LEVEL_PER_SEC
                    + ". Using default " + fallback);
            return fallback;
        }
        if (value > MAX_REGEN_PER_LEVEL_PER_SEC) {
            logger.at(Level.WARNING).log("[RPGStats] " + key + " must be <= " + MAX_REGEN_PER_LEVEL_PER_SEC
                    + ". Clamping.");
            return MAX_REGEN_PER_LEVEL_PER_SEC;
        }
        return value;
    }

    private static int maxAbilityPointsPerLevel(int maxLevel) {
        int levels = Math.max(1, maxLevel - 1);
        return Integer.MAX_VALUE / levels;
    }

    private static Set<String> parseStringSet(String raw) {
        if (raw == null) {
            return Collections.emptySet();
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty() || "[]".equals(trimmed)) {
            return Collections.emptySet();
        }
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }
        String[] parts = trimmed.split(",");
        Set<String> values = new LinkedHashSet<>();
        for (String part : parts) {
            String entry = stripQuotes(part.trim());
            if (!entry.isEmpty()) {
                values.add(entry.toLowerCase());
            }
        }
        if (values.isEmpty()) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(values);
    }

    private static Map<String, Integer> parseMiningXpEntries(Set<String> entries, HytaleLogger logger) {
        if (entries == null || entries.isEmpty()) {
            return Collections.emptyMap();
        }
        LinkedHashMap<String, Integer> results = new LinkedHashMap<>();
        for (String entry : entries) {
            if (entry == null) {
                continue;
            }
            String trimmed = entry.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            int separator = trimmed.indexOf('=');
            if (separator < 0) {
                separator = trimmed.indexOf(':');
            }
            if (separator <= 0 || separator >= trimmed.length() - 1) {
                logger.at(Level.WARNING).log("[RPGStats] Invalid mining XP entry '" + entry
                        + "'. Expected format: \"block_id=XP\".");
                continue;
            }
            String blockId = trimmed.substring(0, separator).trim().toLowerCase();
            String xpRaw = trimmed.substring(separator + 1).trim();
            int xp = parseInt(xpRaw, -1, logger, "mining_xp");
            if (xp <= 0) {
                logger.at(Level.WARNING).log("[RPGStats] mining_xp entry '" + entry + "' must be > 0.");
                continue;
            }
            if (blockId.contains("/") && blockId.endsWith("_ore")) {
                int slash = blockId.lastIndexOf('/');
                String oreName = slash >= 0 ? blockId.substring(slash + 1, blockId.length() - 4) : "";
                if (!oreName.isBlank()) {
                    String normalized = "ore_" + oreName + "_*";
                    logger.at(Level.WARNING).log("[RPGStats] mining_xp entry '" + entry
                            + "' looks like an item ID. Using '" + normalized + "' to match ore blocks.");
                    blockId = normalized;
                }
            } else if (blockId.contains(":") || blockId.contains("/")) {
                logger.at(Level.WARNING).log("[RPGStats] mining_xp entry '" + entry
                        + "' looks like an item ID. Use block IDs like Ore_Iron_Stone or wildcard Ore_Iron_*.");
            }
            if (results.containsKey(blockId)) {
                logger.at(Level.WARNING).log("[RPGStats] Duplicate mining_xp entry for '" + blockId
                        + "'. Using last value.");
            }
            results.put(blockId, xp);
        }
        if (results.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(results);
    }

    private static String stripQuotes(String value) {
        String trimmed = value.trim();
        if (trimmed.length() >= 2) {
            char first = trimmed.charAt(0);
            char last = trimmed.charAt(trimmed.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return trimmed.substring(1, trimmed.length() - 1).trim();
            }
        }
        return trimmed;
    }

    private static Set<String> mergeSets(Set<String> left, Set<String> right) {
        if ((left == null || left.isEmpty()) && (right == null || right.isEmpty())) {
            return Collections.emptySet();
        }
        LinkedHashSet<String> merged = new LinkedHashSet<>();
        if (left != null) {
            merged.addAll(left);
        }
        if (right != null) {
            merged.addAll(right);
        }
        return Collections.unmodifiableSet(merged);
    }

    private static void writeDefault(Path configPath, HytaleLogger logger) {
        String content = ""
                + "# RPGStats configuration\n"
                + "#\n"
                + "# Config version (do not change).\n"
                + "config_version = " + CURRENT_CONFIG_VERSION + "\n"
                + "\n"
                + "# xp_multiplier controls how much XP a player gains from NPC kills (default " + DEFAULT_XP_MULTIPLIER + ").\n"
                + "# Example: 0.35 means XP = maxHealth * 0.35 (before boss multiplier and clamping, max xp gain is set to 1000).\n"
                + "xp_multiplier = " + DEFAULT_XP_MULTIPLIER + "\n"
                + "\n"
                + "# Maximum player level (default " + DEFAULT_MAX_LEVEL + ").\n"
                + "max_level = " + DEFAULT_MAX_LEVEL + "\n"
                + "\n"
                + "# Ability points earned per level (default " + DEFAULT_ABILITY_POINTS_PER_LEVEL + ").\n"
                + "# Valid range: " + MIN_ABILITY_POINTS_PER_LEVEL
                + " to floor(2147483647 / max(1, max_level - 1)).\n"
                + "ability_points_per_level = " + DEFAULT_ABILITY_POINTS_PER_LEVEL + "\n"
                + "\n"
                + "# Ability rank upgrade costs (points required to upgrade to each rank).\n"
                + "# Default: rank 1 costs 1 point, rank 2 costs 2 points, rank 3 costs 3 points.\n"
                + "# Total cost to max an ability = rank1 + rank2 + rank3 (default: 6 points).\n"
                + "ability_rank1_cost = " + DEFAULT_ABILITY_RANK1_COST + "\n"
                + "ability_rank2_cost = " + DEFAULT_ABILITY_RANK2_COST + "\n"
                + "ability_rank3_cost = " + DEFAULT_ABILITY_RANK3_COST + "\n"
                + "\n"
                + "# Light Foot bonus per level, in percent (default " + DEFAULT_LIGHT_FOOT_SPEED_PER_LEVEL_PCT + ").\n"
                + "light_foot_speed_per_level_pct = " + DEFAULT_LIGHT_FOOT_SPEED_PER_LEVEL_PCT + "\n"
                + "\n"
                + "# Armor Proficiency bonus per level, in percent (default " + DEFAULT_ARMOR_PROFICIENCY_RESISTANCE_PER_LEVEL_PCT + ").\n"
                + "armor_proficiency_resistance_per_level_pct = " + DEFAULT_ARMOR_PROFICIENCY_RESISTANCE_PER_LEVEL_PCT + "\n"
                + "\n"
                + "# Health regeneration bonus per level, in points per second (default " + DEFAULT_HEALTH_REGEN_PER_LEVEL_PER_SEC + ").\n"
                + "# Valid range: " + MIN_REGEN_PER_LEVEL_PER_SEC + " to " + MAX_REGEN_PER_LEVEL_PER_SEC + ".\n"
                + "health_regen_per_level_per_sec = " + DEFAULT_HEALTH_REGEN_PER_LEVEL_PER_SEC + "\n"
                + "\n"
                + "# Stamina regeneration bonus per level, in points per second (default " + DEFAULT_STAMINA_REGEN_PER_LEVEL_PER_SEC + ").\n"
                + "# Valid range: " + MIN_REGEN_PER_LEVEL_PER_SEC + " to " + MAX_REGEN_PER_LEVEL_PER_SEC + ".\n"
                + "stamina_regen_per_level_per_sec = " + DEFAULT_STAMINA_REGEN_PER_LEVEL_PER_SEC + "\n"
                + "\n"
                + "# Glancing Blow dodge chance per level, in percent (default " + DEFAULT_GLANCING_BLOW_CHANCE_PER_LEVEL_PCT + ").\n"
                + "# Valid range: " + MIN_ABILITY_BONUS_PCT + " to " + MAX_ABILITY_BONUS_PCT + ".\n"
                + "glancing_blow_chance_per_level_pct = " + DEFAULT_GLANCING_BLOW_CHANCE_PER_LEVEL_PCT + "\n"
                + "\n"
                + "# Strong Lungs oxygen bonus per level, in percent (default " + DEFAULT_STRONG_LUNGS_OXYGEN_PER_LEVEL_PCT + ").\n"
                + "# Valid range: " + MIN_ABILITY_BONUS_PCT + " to " + MAX_ABILITY_BONUS_PCT + ".\n"
                + "strong_lungs_oxygen_per_level_pct = " + DEFAULT_STRONG_LUNGS_OXYGEN_PER_LEVEL_PCT + "\n"
                + "\n"
                + "# Lucky Shot chance per level, in percent (default " + DEFAULT_LUCKY_SHOT_CHANCE_PER_LEVEL_PCT + ").\n"
                + "# Chance to not consume ammo when firing a bow or crossbow.\n"
                + "# Valid range: " + MIN_ABILITY_BONUS_PCT + " to " + MAX_ABILITY_BONUS_PCT + ".\n"
                + "lucky_shot_chance_per_level_pct = " + DEFAULT_LUCKY_SHOT_CHANCE_PER_LEVEL_PCT + "\n"
                + "\n"
                + "# Critical Strike chance per level, in percent (default " + DEFAULT_CRITICAL_STRIKE_CHANCE_PER_LEVEL_PCT + ").\n"
                + "# Total chance = base + (per_level * level). With defaults: 10%/15%/20% at levels 1-3.\n"
                + "# Valid range: " + MIN_ABILITY_BONUS_PCT + " to " + MAX_ABILITY_BONUS_PCT + ".\n"
                + "critical_strike_chance_per_level_pct = " + DEFAULT_CRITICAL_STRIKE_CHANCE_PER_LEVEL_PCT + "\n"
                + "critical_strike_base_chance_pct = " + DEFAULT_CRITICAL_STRIKE_BASE_CHANCE_PCT + "\n"
                + "\n"
                + "# Critical Strike damage multiplier (default " + DEFAULT_CRITICAL_STRIKE_DAMAGE_MULTIPLIER + ").\n"
                + "# When a critical strike occurs, damage is multiplied by this value.\n"
                + "critical_strike_damage_multiplier = " + DEFAULT_CRITICAL_STRIKE_DAMAGE_MULTIPLIER + "\n"
                + "\n"
                + "# Lifesteal percentage per level (default " + DEFAULT_LIFESTEAL_PER_LEVEL_PCT + ").\n"
                + "# Heals you for this percentage of damage dealt per level. 3%/6%/9% at levels 1-3.\n"
                + "# Valid range: " + MIN_ABILITY_BONUS_PCT + " to " + MAX_ABILITY_BONUS_PCT + ".\n"
                + "lifesteal_per_level_pct = " + DEFAULT_LIFESTEAL_PER_LEVEL_PCT + "\n"
                + "\n"
                + "# Thorns reflect percentage per level (default " + DEFAULT_THORNS_REFLECT_PER_LEVEL_PCT + ").\n"
                + "# Reflects this percentage of damage taken back to attackers. 25%/50%/75% at levels 1-3.\n"
                + "# Valid range: " + MIN_ABILITY_BONUS_PCT + " to " + MAX_ABILITY_BONUS_PCT + ".\n"
                + "thorns_reflect_per_level_pct = " + DEFAULT_THORNS_REFLECT_PER_LEVEL_PCT + "\n"
                + "\n"
                + "# Tool Proficiency chance per level (default " + DEFAULT_TOOL_PROFICIENCY_CHANCE_PER_LEVEL_PCT + ").\n"
                + "# Chance to not consume durability when using tools. 15%/30%/45% at levels 1-3.\n"
                + "# Valid range: " + MIN_ABILITY_BONUS_PCT + " to " + MAX_ABILITY_BONUS_PCT + ".\n"
                + "tool_proficiency_chance_per_level_pct = " + DEFAULT_TOOL_PROFICIENCY_CHANCE_PER_LEVEL_PCT + "\n"
                + "\n"
                + "# Lucky Miner chance per level (default " + DEFAULT_LUCKY_MINER_CHANCE_PER_LEVEL_PCT + ").\n"
                + "# Chance to receive bonus ore when mining ore blocks. 10%/20%/30% at levels 1-3.\n"
                + "# Valid range: " + MIN_ABILITY_BONUS_PCT + " to " + MAX_ABILITY_BONUS_PCT + ".\n"
                + "lucky_miner_chance_per_level_pct = " + DEFAULT_LUCKY_MINER_CHANCE_PER_LEVEL_PCT + "\n"
                + "\n"
                + "# Flame Touch bonus fire damage per level (default " + DEFAULT_FLAME_TOUCH_DAMAGE_PER_LEVEL + ").\n"
                + "# Adds flat fire damage on hit. 2/4/6 at levels 1-3.\n"
                + "flame_touch_damage_per_level = " + DEFAULT_FLAME_TOUCH_DAMAGE_PER_LEVEL + "\n"
                + "\n"
                + "# Gourmand bonus to food stat gains per level, in percent (default " + DEFAULT_GOURMAND_FOOD_BONUS_PER_LEVEL_PCT + ").\n"
                + "# Increases positive stat changes from consumable items. 10%/20%/30% at levels 1-3.\n"
                + "# Valid range: " + MIN_ABILITY_BONUS_PCT + " to " + MAX_ABILITY_BONUS_PCT + ".\n"
                + "gourmand_food_bonus_per_level_pct = " + DEFAULT_GOURMAND_FOOD_BONUS_PER_LEVEL_PCT + "\n"
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
                + "stamina_per_point = " + DEFAULT_STAMINA_PER_POINT + "\n"
                + "\n"
                + "# HUD XP bar (set false to disable the RPG stats HUD).\n"
                + "hud_enabled = " + DEFAULT_HUD_ENABLED + "\n"
                + "\n"
                + "# Stat caps (default " + DEFAULT_STAT_CAP + "). Values below 1 revert to default.\n"
                + "str_cap = " + DEFAULT_STAT_CAP + "\n"
                + "dex_cap = " + DEFAULT_STAT_CAP + "\n"
                + "con_cap = " + DEFAULT_STAT_CAP + "\n"
                + "int_cap = " + DEFAULT_STAT_CAP + "\n"
                + "end_cap = " + DEFAULT_STAT_CAP + "\n"
                + "cha_cap = " + DEFAULT_STAT_CAP + "\n"
                + "\n"
                + "# XP blacklist entries live in xp_blacklist.toml\n"
                + "# Mining XP entries live in mining_xp.toml\n";
        try {
            Files.writeString(configPath, content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            logger.at(Level.WARNING).log("[RPGStats] Failed to write default config.toml: " + ex.getMessage());
        }
    }

    private static void writeDefaultXpBlacklist(Path blacklistPath, HytaleLogger logger) {
        String content = ""
                + "# RPGStats XP blacklist\n"
                + "#\n"
                + "# Entries here never grant XP. Use NPC type IDs or role names.\n"
                + "# Values are case-insensitive.\n"
                + "#\n"
                + "# npc_types = [\"npc_deer\", \"npc_chicken\"]\n"
                + "# roles = [\"villager\", \"sheep\"]\n"
                + "npc_types = [\"antelope\", \"archaeopteryx\", \"armadillo\", \"bat\", \"bat_ice\", \"bison\",\n"
                + "    \"bison_calf\", \"bluebird\", \"bluegill\", \"boar\", \"boar_piglet\", \"bunny\", \"cactee\",\n"
                + "    \"camel\", \"camel_calf\", \"catfish\", \"chicken\", \"chicken_chick\", \"chicken_desert\",\n"
                + "    \"chicken_desert_chick\", \"clownfish\", \"cow\", \"cow_calf\", \"crab\", \"crow\", \"deer_doe\",\n"
                + "    \"deer_stag\", \"duck\", \"eel_moray\", \"empty_role\", \"feran_civilian\", \"feran_cub\",\n"
                + "    \"finch_green\", \"flamingo\", \"frog_blue\", \"frog_green\", \"frog_orange\", \"frostgill\",\n"
                + "    \"gecko\", \"goat\", \"goat_kid\", \"hatowrm\", \"hawk\", \"horse\", \"horse_foal\",\n"
                + "    \"jellyfish_blue\", \"jellyfish_cyan\", \"jellyfish_green\", \"jellyfish_man_of_war\",\n"
                + "    \"jellyfish_red\", \"jellyfish_yellow\", \"klops_gentleman\", \"klops_merchant\",\n"
                + "    \"klops_merchant_patrol\", \"klops_merchant_wandering\", \"klops_miner\",\n"
                + "    \"klops_miner_patrol\", \"kweebec_elder\", \"kweebec_merchant\", \"kweebeck_razorleaf\",\n"
                + "    \"kweebeck_razorleaf_patrol\", \"kweebec_rootling\", \"kweebec_sapling\",\n"
                + "    \"kweebec_sapling_orange\", \"kweebec_sapling_pink\", \"kweebec_seedling\",\n"
                + "    \"dweebec_sproutling\", \"kweebec_sproutling_patrol\", \"lizard_sand\", \"lobster\",\n"
                + "    \"meerkat\", \"minnow\", \"molerat\", \"moose_bull\", \"moose_cow\", \"mosshorn\",\n"
                + "    \"mosshorn_plain\", \"mouflon\", \"mouflon_lamb\", \"mouse\", \"owl_brown\", \"owl_snow\",\n"
                + "    \"parrot\", \"penguin\", \"pig\", \"pig_piglet\", \"pig_wild\", \"pig_wild_piglet\",\n"
                + "    \"pigeon\", \"pike\", \"pufferfish\", \"rabbit\", \"ram\", \"ram_lamb\", \"rat\", \"raven\",\n"
                + "    \"salmon\", \"sheep\", \"sheep_lamb\", \"skrill\", \"skrill_chick\", \"snail_frost\",\n"
                + "    \"snail_magma\", \"sparrow\", \"squirrel\", \"tang_blue\", \"tang_chevron\",\n"
                + "    \"tang_lemon_peel\", \"tang_sailfin\", \"temple_bluebird\", \"temple_bunny\",\n"
                + "    \"temple_deer_doe\", \"temple_deer_stag\", \"temple_duck\", \"temple_feran\",\n"
                + "    \"temple_feran_longtooth\", \"temple_frog_blue\", \"temple_frog_green\",\n"
                + "    \"temple_frog_orange\", \"temple_klops\", \"temple_klops_merchant\", \"temple_kweebec\",\n"
                + "    \"temple_kweebec_elder\", \"temple_kweebec_merchant\", \"temple_kweebec_razorleaf\",\n"
                + "    \"temple_kweebec_razorleaf_patrol\", \"temple_kweebec_razorleaf_patrol1\",\n"
                + "    \"temple_kweebec_razorleaf_patrol2\", \"temple_kweebec_razorleaf_patrol3\",\n"
                + "    \"temple_kweebec_razorleaf_patrol4\", \"temple_kweebec_razorleaf_patrol5\",\n"
                + "    \"temple_kweebec_rootling_static\", \"temple_kweebec_seedling\",\n"
                + "    \"temple_kweebec_seedling_static\", \"temple_kweebec_static\", \"temple_mithril_guard\",\n"
                + "    \"temple_owl_brown\", \"temple_squirrel\", \"tetrabird\", \"tortoise\", \"trilobite\",\n"
                + "    \"trilobite_black\", \"turkey\", \"trukey_chick\", \"vulture\", \"warthog\",\n"
                + "    \"warthog_piglet\", \"whale_humpback\", \"woodpecker\"]\n"
                + "roles = []\n";
        try {
            Files.writeString(blacklistPath, content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            logger.at(Level.WARNING).log("[RPGStats] Failed to write default xp_blacklist.toml: " + ex.getMessage());
        }
    }

    private static void writeDefaultMiningXp(Path miningXpPath, HytaleLogger logger) {
        String content = ""
                + "# RPGStats mining XP\n"
                + "#\n"
                + "# Entries here grant XP when the block is broken with the correct tool.\n"
                + "# Values are case-insensitive.\n"
                + "#\n"
                + "# Format: \"block_id=XP\"\n"
                + "# Use block IDs like Ore_Iron_Stone or wildcards like Ore_Iron_* to match all variants.\n"
                + "# Example: block_xp = [\"Ore_Copper_*=6\", \"Ore_Iron_*=10\"]\n"
                + "block_xp = [\n"
                + "    \"Ore_Copper_*=3\",\n"
                + "    \"Ore_Iron_*=5\",\n"
                + "    \"Ore_Silver_*=7\",\n"
                + "    \"Ore_Gold_*=9\",\n"
                + "    \"Ore_Cobalt_*=11\",\n"
                + "    \"Ore_Thorium_*=13\",\n"
                + "    \"Ore_Mithril_*=15\",\n"
                + "    \"Ore_Adamantite_*=17\",\n"
                + "    \"Ore_Onyxium_*=19\"\n"
                + "]\n";
        try {
            Files.writeString(miningXpPath, content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            logger.at(Level.WARNING).log("[RPGStats] Failed to write default mining_xp.toml: " + ex.getMessage());
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

    private static final class XpBlacklist {
        private final Set<String> npcTypes;
        private final Set<String> roles;

        private XpBlacklist(Set<String> npcTypes, Set<String> roles) {
            this.npcTypes = npcTypes == null ? Collections.emptySet() : npcTypes;
            this.roles = roles == null ? Collections.emptySet() : roles;
        }
    }

    private static final class MiningXp {
        private final Map<String, Integer> blockXp;

        private MiningXp(Map<String, Integer> blockXp) {
            this.blockXp = blockXp == null ? Collections.emptyMap() : blockXp;
        }
    }
}
