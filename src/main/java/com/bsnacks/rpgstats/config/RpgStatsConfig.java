package com.bsnacks.rpgstats.config;

import com.hypixel.hytale.logger.HytaleLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public final class RpgStatsConfig {

    private static final DateTimeFormatter BACKUP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final String FILE_NAME = "config.toml";
    private static final String XP_BLACKLIST_FILE_NAME = "xp_blacklist.toml";
    private static final String MINING_XP_FILE_NAME = "mining_xp.toml";
    private static final String CRAFTING_XP_FILE_NAME = "crafting_xp.toml";
    private static final int CURRENT_CONFIG_VERSION = 16;
    // Crafting XP formula defaults
    private static final int DEFAULT_CRAFTING_BASE_XP = 5;
    private static final double DEFAULT_CRAFTING_INGREDIENT_XP = 2.0;
    private static final double DEFAULT_CRAFTING_TIME_XP_PER_SEC = 1.0;
    private static final double DEFAULT_BENCH_TIER_MULTIPLIER_BASE = 1.0;
    private static final double DEFAULT_BENCH_TIER_MULTIPLIER_PER_LEVEL = 0.25;
    private static final int DEFAULT_MAX_CRAFTING_XP = 500;
    private static final boolean DEFAULT_CRAFTING_FORMULA_ENABLED = true;
    private static final double DEFAULT_XP_MULTIPLIER = 0.35;
    private static final int DEFAULT_MAX_LEVEL = 25;
    private static final int DEFAULT_ABILITY_POINTS_PER_LEVEL = 2;
    private static final int MIN_ABILITY_POINTS_PER_LEVEL = 0;
    private static final int DEFAULT_ABILITY_RANK1_COST = 1;
    private static final int DEFAULT_ABILITY_RANK2_COST = 2;
    private static final int DEFAULT_ABILITY_RANK3_COST = 3;
    private static final int MIN_ABILITY_RANK_COST = 0;
    private static final int DEFAULT_MAX_ABILITY_LEVEL = 3;
    private static final int MIN_MAX_ABILITY_LEVEL = 1;
    private static final int MAX_MAX_ABILITY_LEVEL = 10;
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
    private static final double DEFAULT_GOURMAND_FOOD_BONUS_PER_LEVEL_PCT = 10.0;
    private static final double DEFAULT_FLAME_TOUCH_BONUS_DAMAGE_PER_LEVEL_PCT = 15.0;
    private static final String DEFAULT_FLAME_TOUCH_PARTICLE_SYSTEM = "Fire_AoE_Spawn";
    private static final double DEFAULT_DAMAGE_MULTIPLIER_BASE = 10.0;
    private static final double DEFAULT_MINING_SPEED_BASE = 1.0;
    private static final double DEFAULT_MINING_SPEED_PER_POINT = 0.10;
    private static final double DEFAULT_HEALTH_PER_POINT = 10.0;
    private static final double DEFAULT_MANA_PER_POINT = 10.0;
    private static final double DEFAULT_STAMINA_PER_POINT = 1.0;
    private static final boolean DEFAULT_HUD_ENABLED = true;
    private static final boolean DEFAULT_XP_CHAT_MESSAGES_ENABLED = false;
    private static final boolean DEFAULT_PARTY_ENABLED = true;
    private static final int DEFAULT_PARTY_MAX_SIZE = 5;
    private static final int MIN_PARTY_MAX_SIZE = 1;
    private static final int DEFAULT_PARTY_INVITE_TIMEOUT_SEC = 60;
    private static final int MIN_PARTY_INVITE_TIMEOUT_SEC = 1;
    private static final String DEFAULT_PARTY_XP_SHARE_MODE = "scaled_killer";
    private static final int DEFAULT_PARTY_XP_SHARE_RADIUS_BLOCKS = 256;
    private static final int[] DEFAULT_PARTY_KILLER_SHARE_BY_SIZE = new int[] {60, 50, 40, 30};
    private static final int DEFAULT_PARTY_EXTRA_MEMBER_PCT = 17;
    private static final int MIN_PARTY_SHARE_PCT = 0;
    private static final int MAX_PARTY_SHARE_PCT = 100;
    private static final boolean DEFAULT_PARTY_HUD_ENABLED = true;
    private static final int DEFAULT_PARTY_HUD_OFFSET_X = 20;
    private static final int DEFAULT_PARTY_HUD_OFFSET_Y = 20;
    private static final int DEFAULT_PARTY_HUD_REFRESH_TICKS = 20;
    private static final int MIN_PARTY_HUD_REFRESH_TICKS = 1;

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
    private boolean xpChatMessagesEnabled;
    private int strCap;
    private int dexCap;
    private int conCap;
    private int intCap;
    private int endCap;
    private int chaCap;
    private Set<String> xpBlacklistNpcTypes;
    private Set<String> xpBlacklistRoles;
    private Map<String, Integer> miningXpByBlockId;
    private Map<String, Integer> craftingXpByItemId;
    private int craftingBaseXp;
    private double craftingIngredientXp;
    private double craftingTimeXpPerSecond;
    private double benchTierMultiplierBase;
    private double benchTierMultiplierPerLevel;
    private int maxCraftingXp;
    private boolean craftingFormulaEnabled;
    private double strongLungsOxygenPerLevelPct;
    private double luckyShotChancePerLevelPct;
    private double criticalStrikeChancePerLevelPct;
    private double criticalStrikeBaseChancePct;
    private double criticalStrikeDamageMultiplier;
    private double lifestealPerLevelPct;
    private double thornsReflectPerLevelPct;
    private double toolProficiencyChancePerLevelPct;
    private double luckyMinerChancePerLevelPct;
    private double gourmandFoodBonusPerLevelPct;
    private double flameTouchBonusDamagePerLevelPct;
    private String flameTouchParticleSystem;
    private int abilityRank1Cost;
    private int abilityRank2Cost;
    private int abilityRank3Cost;
    private int maxAbilityLevel;
    private boolean partyEnabled;
    private int partyMaxSize;
    private int partyInviteTimeoutSec;
    private String partyXpShareMode;
    private int partyXpShareRadiusBlocks;
    private int[] partyKillerShareBySize;
    private int partyExtraMemberPct;
    private boolean partyHudEnabled;
    private int partyHudOffsetX;
    private int partyHudOffsetY;
    private int partyHudRefreshTicks;

    private RpgStatsConfig(int configVersion, double xpMultiplier, int maxLevel, int abilityPointsPerLevel,
                            double lightFootSpeedPerLevelPct, double armorProficiencyResistancePerLevelPct,
                            double healthRegenPerLevelPerSec, double staminaRegenPerLevelPerSec,
                            double glancingBlowChancePerLevelPct,
                            double damageMultiplierBase,
                            double miningSpeedBase, double miningSpeedPerPoint,
                            double healthPerPoint, double manaPerPoint, double staminaPerPoint,
                            boolean hudEnabled, boolean xpChatMessagesEnabled,
                            int strCap, int dexCap, int conCap, int intCap, int endCap, int chaCap,
                            Set<String> xpBlacklistNpcTypes, Set<String> xpBlacklistRoles,
                            Map<String, Integer> miningXpByBlockId,
                            Map<String, Integer> craftingXpByItemId,
                            int craftingBaseXp, double craftingIngredientXp, double craftingTimeXpPerSecond,
                            double benchTierMultiplierBase, double benchTierMultiplierPerLevel,
                            int maxCraftingXp, boolean craftingFormulaEnabled,
                            double strongLungsOxygenPerLevelPct, double luckyShotChancePerLevelPct,
                            double criticalStrikeChancePerLevelPct, double criticalStrikeBaseChancePct,
                            double criticalStrikeDamageMultiplier, double lifestealPerLevelPct,
                            double thornsReflectPerLevelPct, double toolProficiencyChancePerLevelPct,
                            double luckyMinerChancePerLevelPct,
                            double gourmandFoodBonusPerLevelPct,
                            double flameTouchBonusDamagePerLevelPct,
                            String flameTouchParticleSystem,
                            int abilityRank1Cost, int abilityRank2Cost, int abilityRank3Cost,
                            int maxAbilityLevel,
                            boolean partyEnabled, int partyMaxSize, int partyInviteTimeoutSec,
                            String partyXpShareMode, int partyXpShareRadiusBlocks,
                            int[] partyKillerShareBySize, int partyExtraMemberPct,
                            boolean partyHudEnabled, int partyHudOffsetX, int partyHudOffsetY,
                            int partyHudRefreshTicks) {
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
        this.xpChatMessagesEnabled = xpChatMessagesEnabled;
        this.strCap = strCap;
        this.dexCap = dexCap;
        this.conCap = conCap;
        this.intCap = intCap;
        this.endCap = endCap;
        this.chaCap = chaCap;
        this.xpBlacklistNpcTypes = xpBlacklistNpcTypes;
        this.xpBlacklistRoles = xpBlacklistRoles;
        this.miningXpByBlockId = miningXpByBlockId;
        this.craftingXpByItemId = craftingXpByItemId;
        this.craftingBaseXp = craftingBaseXp;
        this.craftingIngredientXp = craftingIngredientXp;
        this.craftingTimeXpPerSecond = craftingTimeXpPerSecond;
        this.benchTierMultiplierBase = benchTierMultiplierBase;
        this.benchTierMultiplierPerLevel = benchTierMultiplierPerLevel;
        this.maxCraftingXp = maxCraftingXp;
        this.craftingFormulaEnabled = craftingFormulaEnabled;
        this.strongLungsOxygenPerLevelPct = strongLungsOxygenPerLevelPct;
        this.luckyShotChancePerLevelPct = luckyShotChancePerLevelPct;
        this.criticalStrikeChancePerLevelPct = criticalStrikeChancePerLevelPct;
        this.criticalStrikeBaseChancePct = criticalStrikeBaseChancePct;
        this.criticalStrikeDamageMultiplier = criticalStrikeDamageMultiplier;
        this.lifestealPerLevelPct = lifestealPerLevelPct;
        this.thornsReflectPerLevelPct = thornsReflectPerLevelPct;
        this.toolProficiencyChancePerLevelPct = toolProficiencyChancePerLevelPct;
        this.luckyMinerChancePerLevelPct = luckyMinerChancePerLevelPct;
        this.gourmandFoodBonusPerLevelPct = gourmandFoodBonusPerLevelPct;
        this.flameTouchBonusDamagePerLevelPct = flameTouchBonusDamagePerLevelPct;
        this.flameTouchParticleSystem = flameTouchParticleSystem;
        this.abilityRank1Cost = abilityRank1Cost;
        this.abilityRank2Cost = abilityRank2Cost;
        this.abilityRank3Cost = abilityRank3Cost;
        this.maxAbilityLevel = maxAbilityLevel;
        this.partyEnabled = partyEnabled;
        this.partyMaxSize = partyMaxSize;
        this.partyInviteTimeoutSec = partyInviteTimeoutSec;
        this.partyXpShareMode = partyXpShareMode;
        this.partyXpShareRadiusBlocks = partyXpShareRadiusBlocks;
        this.partyKillerShareBySize = partyKillerShareBySize;
        this.partyExtraMemberPct = partyExtraMemberPct;
        this.partyHudEnabled = partyHudEnabled;
        this.partyHudOffsetX = partyHudOffsetX;
        this.partyHudOffsetY = partyHudOffsetY;
        this.partyHudRefreshTicks = partyHudRefreshTicks;
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

    public double getGourmandFoodBonusPerLevelPct() {
        return gourmandFoodBonusPerLevelPct;
    }

    public double getFlameTouchBonusDamagePerLevelPct() {
        return flameTouchBonusDamagePerLevelPct;
    }

    public String getFlameTouchParticleSystem() {
        return flameTouchParticleSystem == null ? DEFAULT_FLAME_TOUCH_PARTICLE_SYSTEM : flameTouchParticleSystem;
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

    public int getMaxAbilityLevel() {
        return maxAbilityLevel;
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

    public boolean isXpChatMessagesEnabled() {
        return xpChatMessagesEnabled;
    }

    public boolean isPartyEnabled() {
        return partyEnabled;
    }

    public int getPartyMaxSize() {
        return partyMaxSize;
    }

    public int getPartyInviteTimeoutSec() {
        return partyInviteTimeoutSec;
    }

    public String getPartyXpShareMode() {
        return partyXpShareMode;
    }

    public int getPartyXpShareRadiusBlocks() {
        return partyXpShareRadiusBlocks;
    }

    public int[] getPartyKillerShareBySize() {
        return partyKillerShareBySize == null ? new int[0] : partyKillerShareBySize.clone();
    }

    public int getPartyExtraMemberPct() {
        return partyExtraMemberPct;
    }

    public boolean isPartyHudEnabled() {
        return partyHudEnabled;
    }

    public int getPartyHudOffsetX() {
        return partyHudOffsetX;
    }

    public int getPartyHudOffsetY() {
        return partyHudOffsetY;
    }

    public int getPartyHudRefreshTicks() {
        return partyHudRefreshTicks;
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

    public int getCraftingXpForItem(String itemId) {
        if (itemId == null || craftingXpByItemId == null || craftingXpByItemId.isEmpty()) {
            return -1;
        }
        String key = itemId.toLowerCase();
        Integer xp = craftingXpByItemId.get(key);
        if (xp != null) {
            return xp;
        }
        for (Map.Entry<String, Integer> entry : craftingXpByItemId.entrySet()) {
            String configured = entry.getKey();
            if (configured.endsWith("*")) {
                String prefix = configured.substring(0, configured.length() - 1);
                if (!prefix.isEmpty() && key.startsWith(prefix)) {
                    return entry.getValue();
                }
            }
        }
        return -1;
    }

    public int getCraftingXpEntryCount() {
        return craftingXpByItemId == null ? 0 : craftingXpByItemId.size();
    }

    public int getCraftingBaseXp() {
        return craftingBaseXp;
    }

    public double getCraftingIngredientXp() {
        return craftingIngredientXp;
    }

    public double getCraftingTimeXpPerSecond() {
        return craftingTimeXpPerSecond;
    }

    public double getBenchTierMultiplierBase() {
        return benchTierMultiplierBase;
    }

    public double getBenchTierMultiplierPerLevel() {
        return benchTierMultiplierPerLevel;
    }

    public int getMaxCraftingXp() {
        return maxCraftingXp;
    }

    public boolean isCraftingFormulaEnabled() {
        return craftingFormulaEnabled;
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
        this.xpChatMessagesEnabled = other.xpChatMessagesEnabled;
        this.strCap = other.strCap;
        this.dexCap = other.dexCap;
        this.conCap = other.conCap;
        this.intCap = other.intCap;
        this.endCap = other.endCap;
        this.chaCap = other.chaCap;
        this.xpBlacklistNpcTypes = other.xpBlacklistNpcTypes;
        this.xpBlacklistRoles = other.xpBlacklistRoles;
        this.miningXpByBlockId = other.miningXpByBlockId;
        this.craftingXpByItemId = other.craftingXpByItemId;
        this.craftingBaseXp = other.craftingBaseXp;
        this.craftingIngredientXp = other.craftingIngredientXp;
        this.craftingTimeXpPerSecond = other.craftingTimeXpPerSecond;
        this.benchTierMultiplierBase = other.benchTierMultiplierBase;
        this.benchTierMultiplierPerLevel = other.benchTierMultiplierPerLevel;
        this.maxCraftingXp = other.maxCraftingXp;
        this.craftingFormulaEnabled = other.craftingFormulaEnabled;
        this.criticalStrikeChancePerLevelPct = other.criticalStrikeChancePerLevelPct;
        this.criticalStrikeBaseChancePct = other.criticalStrikeBaseChancePct;
        this.criticalStrikeDamageMultiplier = other.criticalStrikeDamageMultiplier;
        this.lifestealPerLevelPct = other.lifestealPerLevelPct;
        this.thornsReflectPerLevelPct = other.thornsReflectPerLevelPct;
        this.toolProficiencyChancePerLevelPct = other.toolProficiencyChancePerLevelPct;
        this.luckyMinerChancePerLevelPct = other.luckyMinerChancePerLevelPct;
        this.gourmandFoodBonusPerLevelPct = other.gourmandFoodBonusPerLevelPct;
        this.flameTouchBonusDamagePerLevelPct = other.flameTouchBonusDamagePerLevelPct;
        this.flameTouchParticleSystem = other.flameTouchParticleSystem;
        this.abilityRank1Cost = other.abilityRank1Cost;
        this.abilityRank2Cost = other.abilityRank2Cost;
        this.abilityRank3Cost = other.abilityRank3Cost;
        this.maxAbilityLevel = other.maxAbilityLevel;
        this.partyEnabled = other.partyEnabled;
        this.partyMaxSize = other.partyMaxSize;
        this.partyInviteTimeoutSec = other.partyInviteTimeoutSec;
        this.partyXpShareMode = other.partyXpShareMode;
        this.partyXpShareRadiusBlocks = other.partyXpShareRadiusBlocks;
        this.partyKillerShareBySize = other.partyKillerShareBySize == null ? null : other.partyKillerShareBySize.clone();
        this.partyExtraMemberPct = other.partyExtraMemberPct;
        this.partyHudEnabled = other.partyHudEnabled;
        this.partyHudOffsetX = other.partyHudOffsetX;
        this.partyHudOffsetY = other.partyHudOffsetY;
        this.partyHudRefreshTicks = other.partyHudRefreshTicks;
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

    public static Path resolveCraftingXpPath(Path dataDirectory) {
        return dataDirectory.resolve(CRAFTING_XP_FILE_NAME);
    }

    public static RpgStatsConfig load(Path dataDirectory, HytaleLogger logger) {
        try {
            Files.createDirectories(dataDirectory);
        } catch (IOException ex) {
            logger.at(Level.WARNING).log("[RPGStats] Failed to create data directory: " + ex.getMessage());
        }

        XpBlacklist xpBlacklist = readXpBlacklist(dataDirectory, logger);
        MiningXp miningXp = readMiningXp(dataDirectory, logger);
        CraftingXp craftingXp = readCraftingXp(dataDirectory, logger);

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
                    DEFAULT_XP_CHAT_MESSAGES_ENABLED,
                    DEFAULT_STAT_CAP,
                    DEFAULT_STAT_CAP,
                    DEFAULT_STAT_CAP,
                    DEFAULT_STAT_CAP,
                    DEFAULT_STAT_CAP,
                    DEFAULT_STAT_CAP,
                    xpBlacklist.npcTypes,
                    xpBlacklist.roles,
                    miningXp.blockXp,
                    craftingXp.itemXp,
                    craftingXp.baseXp,
                    craftingXp.ingredientXp,
                    craftingXp.timeXpPerSecond,
                    craftingXp.benchTierMultiplierBase,
                    craftingXp.benchTierMultiplierPerLevel,
                    craftingXp.maxXp,
                    craftingXp.formulaEnabled,
                    DEFAULT_STRONG_LUNGS_OXYGEN_PER_LEVEL_PCT,
                    DEFAULT_LUCKY_SHOT_CHANCE_PER_LEVEL_PCT,
                    DEFAULT_CRITICAL_STRIKE_CHANCE_PER_LEVEL_PCT,
                    DEFAULT_CRITICAL_STRIKE_BASE_CHANCE_PCT,
                    DEFAULT_CRITICAL_STRIKE_DAMAGE_MULTIPLIER,
                    DEFAULT_LIFESTEAL_PER_LEVEL_PCT,
                    DEFAULT_THORNS_REFLECT_PER_LEVEL_PCT,
                    DEFAULT_TOOL_PROFICIENCY_CHANCE_PER_LEVEL_PCT,
                    DEFAULT_LUCKY_MINER_CHANCE_PER_LEVEL_PCT,
                    DEFAULT_GOURMAND_FOOD_BONUS_PER_LEVEL_PCT,
                    DEFAULT_FLAME_TOUCH_BONUS_DAMAGE_PER_LEVEL_PCT,
                    DEFAULT_FLAME_TOUCH_PARTICLE_SYSTEM,
                    DEFAULT_ABILITY_RANK1_COST,
                    DEFAULT_ABILITY_RANK2_COST,
                    DEFAULT_ABILITY_RANK3_COST,
                    DEFAULT_MAX_ABILITY_LEVEL,
                    DEFAULT_PARTY_ENABLED,
                    DEFAULT_PARTY_MAX_SIZE,
                    DEFAULT_PARTY_INVITE_TIMEOUT_SEC,
                    DEFAULT_PARTY_XP_SHARE_MODE,
                    DEFAULT_PARTY_XP_SHARE_RADIUS_BLOCKS,
                    DEFAULT_PARTY_KILLER_SHARE_BY_SIZE.clone(),
                    DEFAULT_PARTY_EXTRA_MEMBER_PCT,
                    DEFAULT_PARTY_HUD_ENABLED,
                    DEFAULT_PARTY_HUD_OFFSET_X,
                    DEFAULT_PARTY_HUD_OFFSET_Y,
                    DEFAULT_PARTY_HUD_REFRESH_TICKS
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

        double gourmandFoodBonusPerLevelPct = parseDouble(values.get("gourmand_food_bonus_per_level_pct"),
                DEFAULT_GOURMAND_FOOD_BONUS_PER_LEVEL_PCT, logger, "gourmand_food_bonus_per_level_pct");
        gourmandFoodBonusPerLevelPct = clampAbilityPct(gourmandFoodBonusPerLevelPct, logger,
                "gourmand_food_bonus_per_level_pct", DEFAULT_GOURMAND_FOOD_BONUS_PER_LEVEL_PCT);

        double flameTouchBonusDamagePerLevelPct = parseDouble(values.get("flame_touch_bonus_damage_per_level_pct"),
                DEFAULT_FLAME_TOUCH_BONUS_DAMAGE_PER_LEVEL_PCT, logger, "flame_touch_bonus_damage_per_level_pct");
        flameTouchBonusDamagePerLevelPct = clampAbilityPct(flameTouchBonusDamagePerLevelPct, logger,
                "flame_touch_bonus_damage_per_level_pct", DEFAULT_FLAME_TOUCH_BONUS_DAMAGE_PER_LEVEL_PCT);

        String flameTouchParticleSystem = stripQuotes(values.get("flame_touch_particle_system"));
        if (flameTouchParticleSystem == null || flameTouchParticleSystem.isBlank()) {
            flameTouchParticleSystem = DEFAULT_FLAME_TOUCH_PARTICLE_SYSTEM;
        } else if ("none".equalsIgnoreCase(flameTouchParticleSystem) || "off".equalsIgnoreCase(flameTouchParticleSystem)) {
            flameTouchParticleSystem = "";
        }

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

        int maxAbilityLevel = parseInt(values.get("max_ability_level"),
                DEFAULT_MAX_ABILITY_LEVEL, logger, "max_ability_level");
        if (maxAbilityLevel < MIN_MAX_ABILITY_LEVEL) {
            logger.at(Level.WARNING).log("[RPGStats] max_ability_level must be >= "
                    + MIN_MAX_ABILITY_LEVEL + ". Using default " + DEFAULT_MAX_ABILITY_LEVEL);
            maxAbilityLevel = DEFAULT_MAX_ABILITY_LEVEL;
        } else if (maxAbilityLevel > MAX_MAX_ABILITY_LEVEL) {
            logger.at(Level.WARNING).log("[RPGStats] max_ability_level must be <= "
                    + MAX_MAX_ABILITY_LEVEL + ". Using default " + DEFAULT_MAX_ABILITY_LEVEL);
            maxAbilityLevel = DEFAULT_MAX_ABILITY_LEVEL;
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
        boolean xpChatMessagesEnabled = parseBoolean(values.get("xp_chat_messages_enabled"),
                DEFAULT_XP_CHAT_MESSAGES_ENABLED, logger, "xp_chat_messages_enabled");

        boolean partyEnabled = parseBoolean(values.get("party_enabled"), DEFAULT_PARTY_ENABLED, logger, "party_enabled");

        int partyMaxSize = parseInt(values.get("party_max_size"), DEFAULT_PARTY_MAX_SIZE, logger, "party_max_size");
        if (partyMaxSize < MIN_PARTY_MAX_SIZE) {
            logger.at(Level.WARNING).log("[RPGStats] party_max_size must be >= " + MIN_PARTY_MAX_SIZE
                    + ". Using default " + DEFAULT_PARTY_MAX_SIZE);
            partyMaxSize = DEFAULT_PARTY_MAX_SIZE;
        }

        int partyInviteTimeoutSec = parseInt(values.get("party_invite_timeout_sec"),
                DEFAULT_PARTY_INVITE_TIMEOUT_SEC, logger, "party_invite_timeout_sec");
        if (partyInviteTimeoutSec < MIN_PARTY_INVITE_TIMEOUT_SEC) {
            logger.at(Level.WARNING).log("[RPGStats] party_invite_timeout_sec must be >= " + MIN_PARTY_INVITE_TIMEOUT_SEC
                    + ". Using default " + DEFAULT_PARTY_INVITE_TIMEOUT_SEC);
            partyInviteTimeoutSec = DEFAULT_PARTY_INVITE_TIMEOUT_SEC;
        }

        String partyXpShareMode = stripQuotes(values.get("party_xp_share_mode"));
        if (partyXpShareMode == null || partyXpShareMode.isBlank()) {
            partyXpShareMode = DEFAULT_PARTY_XP_SHARE_MODE;
        } else {
            partyXpShareMode = partyXpShareMode.trim().toLowerCase(Locale.ROOT);
            if (!"scaled_killer".equals(partyXpShareMode)) {
                logger.at(Level.WARNING).log("[RPGStats] party_xp_share_mode '" + partyXpShareMode
                        + "' is invalid. Using default " + DEFAULT_PARTY_XP_SHARE_MODE);
                partyXpShareMode = DEFAULT_PARTY_XP_SHARE_MODE;
            }
        }

        int partyXpShareRadiusBlocks = parseInt(values.get("party_xp_share_radius_blocks"),
                DEFAULT_PARTY_XP_SHARE_RADIUS_BLOCKS, logger, "party_xp_share_radius_blocks");
        if (partyXpShareRadiusBlocks < 0) {
            logger.at(Level.WARNING).log("[RPGStats] party_xp_share_radius_blocks must be >= 0. Using default "
                    + DEFAULT_PARTY_XP_SHARE_RADIUS_BLOCKS);
            partyXpShareRadiusBlocks = DEFAULT_PARTY_XP_SHARE_RADIUS_BLOCKS;
        }

        int[] partyKillerShareBySize = parseIntArray(values.get("party_killer_share_by_size"),
                logger, "party_killer_share_by_size");
        partyKillerShareBySize = clampPartyShares(partyKillerShareBySize, logger, "party_killer_share_by_size");
        if (partyKillerShareBySize.length == 0) {
            partyKillerShareBySize = DEFAULT_PARTY_KILLER_SHARE_BY_SIZE.clone();
        }

        int partyExtraMemberPct = parseInt(values.get("party_extra_member_pct"),
                DEFAULT_PARTY_EXTRA_MEMBER_PCT, logger, "party_extra_member_pct");
        if (partyExtraMemberPct < MIN_PARTY_SHARE_PCT || partyExtraMemberPct > MAX_PARTY_SHARE_PCT) {
            logger.at(Level.WARNING).log("[RPGStats] party_extra_member_pct must be between "
                    + MIN_PARTY_SHARE_PCT + " and " + MAX_PARTY_SHARE_PCT + ". Using default "
                    + DEFAULT_PARTY_EXTRA_MEMBER_PCT);
            partyExtraMemberPct = DEFAULT_PARTY_EXTRA_MEMBER_PCT;
        }

        boolean partyHudEnabled = parseBoolean(values.get("party_hud_enabled"),
                DEFAULT_PARTY_HUD_ENABLED, logger, "party_hud_enabled");

        int partyHudOffsetX = parseInt(values.get("party_hud_offset_x"),
                DEFAULT_PARTY_HUD_OFFSET_X, logger, "party_hud_offset_x");
        int partyHudOffsetY = parseInt(values.get("party_hud_offset_y"),
                DEFAULT_PARTY_HUD_OFFSET_Y, logger, "party_hud_offset_y");
        int partyHudRefreshTicks = parseInt(values.get("party_hud_refresh_ticks"),
                DEFAULT_PARTY_HUD_REFRESH_TICKS, logger, "party_hud_refresh_ticks");
        if (partyHudRefreshTicks < MIN_PARTY_HUD_REFRESH_TICKS) {
            logger.at(Level.WARNING).log("[RPGStats] party_hud_refresh_ticks must be >= "
                    + MIN_PARTY_HUD_REFRESH_TICKS + ". Using default " + DEFAULT_PARTY_HUD_REFRESH_TICKS);
            partyHudRefreshTicks = DEFAULT_PARTY_HUD_REFRESH_TICKS;
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

        RpgStatsConfig config = new RpgStatsConfig(configVersion, multiplier, maxLevel, abilityPointsPerLevel,
                lightFootSpeedPerLevelPct, armorProficiencyResistancePerLevelPct,
                healthRegenPerLevelPerSec, staminaRegenPerLevelPerSec, glancingBlowChancePerLevelPct,
                damageBase, miningBase, miningPerPoint, healthPerPoint, manaPerPoint, staminaPerPoint,
                hudEnabled,
                xpChatMessagesEnabled,
                strCap, dexCap, conCap, intCap, endCap, chaCap,
                xpBlacklistNpcTypes, xpBlacklistRoles, miningXp.blockXp,
                craftingXp.itemXp,
                craftingXp.baseXp,
                craftingXp.ingredientXp,
                craftingXp.timeXpPerSecond,
                craftingXp.benchTierMultiplierBase,
                craftingXp.benchTierMultiplierPerLevel,
                craftingXp.maxXp,
                craftingXp.formulaEnabled,
                strongLungsOxygenPerLevelPct, luckyShotChancePerLevelPct,
                criticalStrikeChancePerLevelPct, criticalStrikeBaseChancePct, criticalStrikeDamageMultiplier,
                lifestealPerLevelPct, thornsReflectPerLevelPct, toolProficiencyChancePerLevelPct,
                luckyMinerChancePerLevelPct,
                gourmandFoodBonusPerLevelPct,
                flameTouchBonusDamagePerLevelPct,
                flameTouchParticleSystem,
                abilityRank1Cost, abilityRank2Cost, abilityRank3Cost,
                maxAbilityLevel,
                partyEnabled, partyMaxSize, partyInviteTimeoutSec,
                partyXpShareMode, partyXpShareRadiusBlocks,
                partyKillerShareBySize, partyExtraMemberPct,
                partyHudEnabled, partyHudOffsetX, partyHudOffsetY,
                partyHudRefreshTicks);
        return config;
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

    private static CraftingXp readCraftingXp(Path dataDirectory, HytaleLogger logger) {
        Path craftingXpPath = resolveCraftingXpPath(dataDirectory);
        if (!Files.exists(craftingXpPath)) {
            writeDefaultCraftingXp(craftingXpPath, logger);
            if (!Files.exists(craftingXpPath)) {
                return new CraftingXp(Collections.emptyMap(),
                        DEFAULT_CRAFTING_BASE_XP,
                        DEFAULT_CRAFTING_INGREDIENT_XP,
                        DEFAULT_CRAFTING_TIME_XP_PER_SEC,
                        DEFAULT_BENCH_TIER_MULTIPLIER_BASE,
                        DEFAULT_BENCH_TIER_MULTIPLIER_PER_LEVEL,
                        DEFAULT_MAX_CRAFTING_XP,
                        DEFAULT_CRAFTING_FORMULA_ENABLED);
            }
        }
        Map<String, String> values = readKeyValues(craftingXpPath, logger);
        Set<String> itemEntries = parseStringSet(values.get("item_xp"));
        Map<String, Integer> itemXp = parseCraftingXpEntries(itemEntries, logger);

        int baseXp = parseInt(values.get("base_xp"), DEFAULT_CRAFTING_BASE_XP, logger, "base_xp");
        double ingredientXp = parseDouble(values.get("ingredient_xp_per_item"), DEFAULT_CRAFTING_INGREDIENT_XP, logger, "ingredient_xp_per_item");
        double timeXpPerSec = parseDouble(values.get("time_xp_per_second"), DEFAULT_CRAFTING_TIME_XP_PER_SEC, logger, "time_xp_per_second");
        double benchBase = parseDouble(values.get("bench_tier_multiplier_base"), DEFAULT_BENCH_TIER_MULTIPLIER_BASE, logger, "bench_tier_multiplier_base");
        double benchPerLevel = parseDouble(values.get("bench_tier_multiplier_per_level"), DEFAULT_BENCH_TIER_MULTIPLIER_PER_LEVEL, logger, "bench_tier_multiplier_per_level");
        int maxXp = parseInt(values.get("max_xp_per_craft"), DEFAULT_MAX_CRAFTING_XP, logger, "max_xp_per_craft");
        boolean formulaEnabled = parseBoolean(values.get("formula_enabled"), DEFAULT_CRAFTING_FORMULA_ENABLED, logger, "formula_enabled");

        return new CraftingXp(itemXp, baseXp, ingredientXp, timeXpPerSec, benchBase, benchPerLevel, maxXp, formulaEnabled);
    }

    private static Map<String, Integer> parseCraftingXpEntries(Set<String> entries, HytaleLogger logger) {
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
                logger.at(Level.WARNING).log("[RPGStats] Invalid crafting XP entry '" + entry
                        + "'. Expected format: \"item_id=XP\".");
                continue;
            }
            String itemId = trimmed.substring(0, separator).trim().toLowerCase();
            String xpRaw = trimmed.substring(separator + 1).trim();
            int xp = parseInt(xpRaw, -1, logger, "crafting_xp");
            if (xp <= 0) {
                logger.at(Level.WARNING).log("[RPGStats] crafting_xp entry '" + entry + "' must be > 0.");
                continue;
            }
            if (results.containsKey(itemId)) {
                logger.at(Level.WARNING).log("[RPGStats] Duplicate crafting_xp entry for '" + itemId
                        + "'. Using last value.");
            }
            results.put(itemId, xp);
        }
        if (results.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(results);
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

    private static int[] parseIntArray(String raw, HytaleLogger logger, String key) {
        if (raw == null) {
            return new int[0];
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty() || "[]".equals(trimmed)) {
            return new int[0];
        }
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }
        String[] parts = trimmed.split(",");
        List<Integer> values = new ArrayList<>();
        for (String part : parts) {
            String entry = stripQuotes(part.trim());
            if (entry.isEmpty()) {
                continue;
            }
            try {
                values.add(Integer.parseInt(entry));
            } catch (NumberFormatException ex) {
                logger.at(Level.WARNING).log("[RPGStats] Invalid " + key + " entry '" + entry + "'.");
            }
        }
        if (values.isEmpty()) {
            return new int[0];
        }
        int[] result = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    private static int[] clampPartyShares(int[] values, HytaleLogger logger, String key) {
        if (values == null || values.length == 0) {
            return new int[0];
        }
        int[] clamped = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            int value = values[i];
            if (value < MIN_PARTY_SHARE_PCT || value > MAX_PARTY_SHARE_PCT) {
                logger.at(Level.WARNING).log("[RPGStats] " + key + " entry '" + value
                        + "' must be between " + MIN_PARTY_SHARE_PCT + " and " + MAX_PARTY_SHARE_PCT + ". Clamping.");
                value = Math.max(MIN_PARTY_SHARE_PCT, Math.min(MAX_PARTY_SHARE_PCT, value));
            }
            clamped[i] = value;
        }
        return clamped;
    }

    private static Map<String, Double> parseDoubleEntries(Set<String> entries, Map<String, Double> defaults,
                                                          HytaleLogger logger, String key) {
        if (entries == null || entries.isEmpty()) {
            return defaults == null ? Collections.emptyMap() : defaults;
        }
        LinkedHashMap<String, Double> results = new LinkedHashMap<>();
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
            if (separator < 0) {
                logger.at(Level.WARNING).log("[RPGStats] Invalid " + key + " entry '" + entry + "'. Expected format: \"stat=value\".");
                continue;
            }
            String id = stripQuotes(trimmed.substring(0, separator).trim()).toLowerCase();
            String valueRaw = trimmed.substring(separator + 1).trim();
            if (id.isEmpty()) {
                continue;
            }
            double value;
            try {
                value = Double.parseDouble(valueRaw);
            } catch (NumberFormatException ex) {
                logger.at(Level.WARNING).log("[RPGStats] Invalid " + key + " entry '" + entry + "'.");
                continue;
            }
            if (value < 0.0) {
                logger.at(Level.WARNING).log("[RPGStats] " + key + " entry '" + entry + "' must be >= 0.");
                continue;
            }
            results.put(id, value);
        }
        if (results.isEmpty()) {
            return defaults == null ? Collections.emptyMap() : defaults;
        }
        return Collections.unmodifiableMap(results);
    }

    private static Map<String, LevelRange> parseLevelRanges(Set<String> entries, int defaultMin,
                                                            int defaultMax, HytaleLogger logger) {
        if (entries == null || entries.isEmpty()) {
            return Collections.emptyMap();
        }
        LinkedHashMap<String, LevelRange> results = new LinkedHashMap<>();
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
            if (separator < 0) {
                logger.at(Level.WARNING).log("[RPGStats] Invalid npc_level_spawn_ranges entry '" + entry
                        + "'. Expected format: \"spawn_id=min-max\".");
                continue;
            }
            String id = stripQuotes(trimmed.substring(0, separator).trim()).toLowerCase();
            String rangeRaw = trimmed.substring(separator + 1).trim();
            if (id.isEmpty()) {
                continue;
            }
            int min = defaultMin;
            int max = defaultMax;
            String[] parts;
            if (rangeRaw.contains("..")) {
                parts = rangeRaw.split("\\.\\.");
            } else if (rangeRaw.contains("-")) {
                parts = rangeRaw.split("-");
            } else if (rangeRaw.contains(",")) {
                parts = rangeRaw.split(",");
            } else {
                parts = new String[]{rangeRaw};
            }
            try {
                if (parts.length >= 1) {
                    min = Integer.parseInt(parts[0].trim());
                }
                if (parts.length >= 2) {
                    max = Integer.parseInt(parts[1].trim());
                } else {
                    max = min;
                }
            } catch (NumberFormatException ex) {
                logger.at(Level.WARNING).log("[RPGStats] Invalid npc_level_spawn_ranges entry '" + entry + "'.");
                continue;
            }
            if (min < 1) {
                min = 1;
            }
            if (max < min) {
                max = min;
            }
            results.put(id, new LevelRange(min, max));
        }
        if (results.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(results);
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
        if (value == null) {
            return null;
        }
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
                + "# Maximum level for all abilities (default " + DEFAULT_MAX_ABILITY_LEVEL + ").\n"
                + "# Valid range: " + MIN_MAX_ABILITY_LEVEL + " to " + MAX_MAX_ABILITY_LEVEL + ".\n"
                + "# Increasing this allows abilities to be upgraded beyond rank 3.\n"
                + "# Note: Rank costs beyond rank 3 equal the rank number (rank 4 costs 4 points, etc.).\n"
                + "max_ability_level = " + DEFAULT_MAX_ABILITY_LEVEL + "\n"
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
                + "# Gourmand bonus to food stat gains per level, in percent (default " + DEFAULT_GOURMAND_FOOD_BONUS_PER_LEVEL_PCT + ").\n"
                + "# Increases positive stat changes from consumable items. 10%/20%/30% at levels 1-3.\n"
                + "# Valid range: " + MIN_ABILITY_BONUS_PCT + " to " + MAX_ABILITY_BONUS_PCT + ".\n"
                + "gourmand_food_bonus_per_level_pct = " + DEFAULT_GOURMAND_FOOD_BONUS_PER_LEVEL_PCT + "\n"
                + "\n"
                + "# Flame Touch bonus fire damage per level, in percent (default " + DEFAULT_FLAME_TOUCH_BONUS_DAMAGE_PER_LEVEL_PCT + ").\n"
                + "# Adds bonus fire damage when attacking. 15%/30%/45% at levels 1-3.\n"
                + "# Valid range: " + MIN_ABILITY_BONUS_PCT + " to " + MAX_ABILITY_BONUS_PCT + ".\n"
                + "flame_touch_bonus_damage_per_level_pct = " + DEFAULT_FLAME_TOUCH_BONUS_DAMAGE_PER_LEVEL_PCT + "\n"
                + "# Particle system for Flame Touch visuals.\n"
                + "# Set to \"none\" or \"off\" to disable particles.\n"
                + "flame_touch_particle_system = \"" + DEFAULT_FLAME_TOUCH_PARTICLE_SYSTEM + "\"\n"
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
                + "# XP chat messages (default " + DEFAULT_XP_CHAT_MESSAGES_ENABLED + ").\n"
                + "# When false, XP gain messages are hidden; level-up splash still appears.\n"
                + "xp_chat_messages_enabled = " + DEFAULT_XP_CHAT_MESSAGES_ENABLED + "\n"
                + "\n"
                + "# Party system settings.\n"
                + "party_enabled = " + DEFAULT_PARTY_ENABLED + "\n"
                + "# Max party size (default " + DEFAULT_PARTY_MAX_SIZE + ").\n"
                + "party_max_size = " + DEFAULT_PARTY_MAX_SIZE + "\n"
                + "# Invite timeout in seconds (default " + DEFAULT_PARTY_INVITE_TIMEOUT_SEC + ").\n"
                + "party_invite_timeout_sec = " + DEFAULT_PARTY_INVITE_TIMEOUT_SEC + "\n"
                + "# Party XP share mode (default \"" + DEFAULT_PARTY_XP_SHARE_MODE + "\").\n"
                + "# Supported: scaled_killer\n"
                + "party_xp_share_mode = \"" + DEFAULT_PARTY_XP_SHARE_MODE + "\"\n"
                + "# XP share radius in blocks (default " + DEFAULT_PARTY_XP_SHARE_RADIUS_BLOCKS + ").\n"
                + "# Set to 0 for no distance limit.\n"
                + "party_xp_share_radius_blocks = " + DEFAULT_PARTY_XP_SHARE_RADIUS_BLOCKS + "\n"
                + "# Killer XP share by party size (index 0 = size 2, index 1 = size 3, etc.).\n"
                + "# Default: [60, 50, 40, 30]\n"
                + "party_killer_share_by_size = [60, 50, 40, 30]\n"
                + "# Extra member XP percent for party sizes larger than the list above (default "
                + DEFAULT_PARTY_EXTRA_MEMBER_PCT + ").\n"
                + "# For sizes above the list, the killer uses the last list entry and each extra member\n"
                + "# gains party_extra_member_pct. This can increase total XP above 100%.\n"
                + "party_extra_member_pct = " + DEFAULT_PARTY_EXTRA_MEMBER_PCT + "\n"
                + "# Party HUD overlay enabled (default " + DEFAULT_PARTY_HUD_ENABLED + ").\n"
                + "party_hud_enabled = " + DEFAULT_PARTY_HUD_ENABLED + "\n"
                + "# Party HUD position offsets in pixels (top-left anchor).\n"
                + "party_hud_offset_x = " + DEFAULT_PARTY_HUD_OFFSET_X + "\n"
                + "party_hud_offset_y = " + DEFAULT_PARTY_HUD_OFFSET_Y + "\n"
                + "# Party HUD refresh interval in ticks (20 ticks = 1 second).\n"
                + "party_hud_refresh_ticks = " + DEFAULT_PARTY_HUD_REFRESH_TICKS + "\n"
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

    private static void writeDefaultCraftingXp(Path craftingXpPath, HytaleLogger logger) {
        String content = ""
                + "# RPGStats crafting XP\n"
                + "#\n"
                + "# Entries here grant XP when items are crafted.\n"
                + "# Values are case-insensitive.\n"
                + "#\n"
                + "# Format: \"item_id=XP\"\n"
                + "# Use item IDs or wildcards like Armor_Iron_* to match all iron armor variants.\n"
                + "# Items not listed will use the formula-based fallback calculation.\n"
                + "#\n"
                + "# Example: item_xp = [\"Armor_Iron_*=25\", \"Sword_Mithril=50\", \"Tool_Pickaxe_*=15\"]\n"
                + "item_xp = [\n"
                + "    \"Armor_*=15\",\n"
                + "    \"Weapon_*=20\",\n"
                + "    \"Tool_*=10\"\n"
                + "]\n"
                + "\n"
                + "# Formula settings for items not in the list above\n"
                + "# XP = (base_xp + ingredient_bonus + time_bonus) * bench_multiplier * batch_size\n"
                + "#\n"
                + "# Base XP awarded for any crafted item\n"
                + "base_xp = " + DEFAULT_CRAFTING_BASE_XP + "\n"
                + "\n"
                + "# Additional XP per ingredient in the recipe\n"
                + "ingredient_xp_per_item = " + DEFAULT_CRAFTING_INGREDIENT_XP + "\n"
                + "\n"
                + "# Additional XP per second of crafting time\n"
                + "time_xp_per_second = " + DEFAULT_CRAFTING_TIME_XP_PER_SEC + "\n"
                + "\n"
                + "# Multiplier applied based on bench tier requirement\n"
                + "# tier 0 = 1.0x, tier 1 = 1.25x, tier 2 = 1.5x, tier 3 = 2.0x\n"
                + "bench_tier_multiplier_base = " + DEFAULT_BENCH_TIER_MULTIPLIER_BASE + "\n"
                + "bench_tier_multiplier_per_level = " + DEFAULT_BENCH_TIER_MULTIPLIER_PER_LEVEL + "\n"
                + "\n"
                + "# Maximum XP that can be awarded for a single craft (0 = unlimited)\n"
                + "max_xp_per_craft = " + DEFAULT_MAX_CRAFTING_XP + "\n"
                + "\n"
                + "# Enable/disable formula fallback (if false, only configured items award XP)\n"
                + "formula_enabled = " + DEFAULT_CRAFTING_FORMULA_ENABLED + "\n";
        try {
            Files.writeString(craftingXpPath, content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            logger.at(Level.WARNING).log("[RPGStats] Failed to write default crafting_xp.toml: " + ex.getMessage());
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

    public static final class LevelRange {
        private final int min;
        private final int max;

        public LevelRange(int min, int max) {
            this.min = Math.max(1, min);
            this.max = Math.max(this.min, max);
        }

        public int min() {
            return min;
        }

        public int max() {
            return max;
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

    private static final class CraftingXp {
        private final Map<String, Integer> itemXp;
        private final int baseXp;
        private final double ingredientXp;
        private final double timeXpPerSecond;
        private final double benchTierMultiplierBase;
        private final double benchTierMultiplierPerLevel;
        private final int maxXp;
        private final boolean formulaEnabled;

        private CraftingXp(Map<String, Integer> itemXp, int baseXp, double ingredientXp,
                           double timeXpPerSecond, double benchTierMultiplierBase,
                           double benchTierMultiplierPerLevel, int maxXp, boolean formulaEnabled) {
            this.itemXp = itemXp == null ? Collections.emptyMap() : itemXp;
            this.baseXp = baseXp;
            this.ingredientXp = ingredientXp;
            this.timeXpPerSecond = timeXpPerSecond;
            this.benchTierMultiplierBase = benchTierMultiplierBase;
            this.benchTierMultiplierPerLevel = benchTierMultiplierPerLevel;
            this.maxXp = maxXp;
            this.formulaEnabled = formulaEnabled;
        }
    }
}
