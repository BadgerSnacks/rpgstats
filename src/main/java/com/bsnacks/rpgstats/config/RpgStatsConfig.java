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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public final class RpgStatsConfig {

    private static final DateTimeFormatter BACKUP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final String FILE_NAME = "config.toml";
    private static final String XP_BLACKLIST_FILE_NAME = "xp_blacklist.toml";
    private static final double DEFAULT_XP_MULTIPLIER = 0.35;
    private static final int DEFAULT_MAX_LEVEL = 25;
    private static final int DEFAULT_STAT_CAP = 25;
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
    private int strCap;
    private int dexCap;
    private int conCap;
    private int intCap;
    private int endCap;
    private int chaCap;
    private Set<String> xpBlacklistNpcTypes;
    private Set<String> xpBlacklistRoles;

    private RpgStatsConfig(double xpMultiplier, int maxLevel, double damageMultiplierBase,
                           double miningSpeedBase, double miningSpeedPerPoint,
                           double healthPerPoint, double manaPerPoint, double staminaPerPoint,
                           int strCap, int dexCap, int conCap, int intCap, int endCap, int chaCap,
                           Set<String> xpBlacklistNpcTypes, Set<String> xpBlacklistRoles) {
        this.xpMultiplier = xpMultiplier;
        this.maxLevel = maxLevel;
        this.damageMultiplierBase = damageMultiplierBase;
        this.miningSpeedBase = miningSpeedBase;
        this.miningSpeedPerPoint = miningSpeedPerPoint;
        this.healthPerPoint = healthPerPoint;
        this.manaPerPoint = manaPerPoint;
        this.staminaPerPoint = staminaPerPoint;
        this.strCap = strCap;
        this.dexCap = dexCap;
        this.conCap = conCap;
        this.intCap = intCap;
        this.endCap = endCap;
        this.chaCap = chaCap;
        this.xpBlacklistNpcTypes = xpBlacklistNpcTypes;
        this.xpBlacklistRoles = xpBlacklistRoles;
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
        this.xpMultiplier = other.xpMultiplier;
        this.maxLevel = other.maxLevel;
        this.damageMultiplierBase = other.damageMultiplierBase;
        this.miningSpeedBase = other.miningSpeedBase;
        this.miningSpeedPerPoint = other.miningSpeedPerPoint;
        this.healthPerPoint = other.healthPerPoint;
        this.manaPerPoint = other.manaPerPoint;
        this.staminaPerPoint = other.staminaPerPoint;
        this.strCap = other.strCap;
        this.dexCap = other.dexCap;
        this.conCap = other.conCap;
        this.intCap = other.intCap;
        this.endCap = other.endCap;
        this.chaCap = other.chaCap;
        this.xpBlacklistNpcTypes = other.xpBlacklistNpcTypes;
        this.xpBlacklistRoles = other.xpBlacklistRoles;
    }

    public static Path resolveConfigPath(Path dataDirectory) {
        return dataDirectory.resolve(FILE_NAME);
    }

    public static Path resolveXpBlacklistPath(Path dataDirectory) {
        return dataDirectory.resolve(XP_BLACKLIST_FILE_NAME);
    }

    public static RpgStatsConfig load(Path dataDirectory, HytaleLogger logger) {
        try {
            Files.createDirectories(dataDirectory);
        } catch (IOException ex) {
            logger.at(Level.WARNING).log("[RPGStats] Failed to create data directory: " + ex.getMessage());
        }

        XpBlacklist xpBlacklist = readXpBlacklist(dataDirectory, logger);

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
                    DEFAULT_STAMINA_PER_POINT,
                    DEFAULT_STAT_CAP,
                    DEFAULT_STAT_CAP,
                    DEFAULT_STAT_CAP,
                    DEFAULT_STAT_CAP,
                    DEFAULT_STAT_CAP,
                    DEFAULT_STAT_CAP,
                    xpBlacklist.npcTypes,
                    xpBlacklist.roles
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

        return new RpgStatsConfig(multiplier, maxLevel, damageBase,
                miningBase, miningPerPoint, healthPerPoint, manaPerPoint, staminaPerPoint,
                strCap, dexCap, conCap, intCap, endCap, chaCap,
                xpBlacklistNpcTypes, xpBlacklistRoles);
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

    private static int parseCap(String raw, String key, HytaleLogger logger) {
        int cap = parseInt(raw, DEFAULT_STAT_CAP, logger, key);
        if (cap < 1) {
            logger.at(Level.WARNING).log("[RPGStats] " + key + " must be >= 1. Using default " + DEFAULT_STAT_CAP);
            return DEFAULT_STAT_CAP;
        }
        return cap;
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
                + "stamina_per_point = " + DEFAULT_STAMINA_PER_POINT + "\n"
                + "\n"
                + "# Stat caps (default " + DEFAULT_STAT_CAP + "). Values below 1 revert to default.\n"
                + "str_cap = " + DEFAULT_STAT_CAP + "\n"
                + "dex_cap = " + DEFAULT_STAT_CAP + "\n"
                + "con_cap = " + DEFAULT_STAT_CAP + "\n"
                + "int_cap = " + DEFAULT_STAT_CAP + "\n"
                + "end_cap = " + DEFAULT_STAT_CAP + "\n"
                + "cha_cap = " + DEFAULT_STAT_CAP + "\n"
                + "\n"
                + "# XP blacklist entries live in xp_blacklist.toml\n";
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
}
