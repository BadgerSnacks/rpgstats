package com.bsnacks.rpgstats.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Arrays;

public final class RpgStats implements Component<EntityStore> {

    public static final String COMPONENT_ID = "rpgstats";
    public static final int CURRENT_VERSION = 18;
    public static final int DEFAULT_MAX_LEVEL = 25;
    public static final int BASE_XP = 100;
    public static final int LINEAR_XP = 50;
    public static final int QUADRATIC_XP = 20;
    public static final int BASE_STAT = 10;
    public static final int DEFAULT_ABILITY_POINTS_PER_LEVEL = 2;
    public static final int DEFAULT_MAX_ABILITY_LEVEL = 3;
    public static final int MIN_MAX_ABILITY_LEVEL = 1;
    public static final int MAX_MAX_ABILITY_LEVEL = 10;

    // Configurable max levels for each ability (default 3, configurable 1-10)
    private static int lightFootMaxLevel = DEFAULT_MAX_ABILITY_LEVEL;
    private static int armorProficiencyMaxLevel = DEFAULT_MAX_ABILITY_LEVEL;
    private static int healthRegenMaxLevel = DEFAULT_MAX_ABILITY_LEVEL;
    private static int staminaRegenMaxLevel = DEFAULT_MAX_ABILITY_LEVEL;
    private static int glancingBlowMaxLevel = DEFAULT_MAX_ABILITY_LEVEL;
    private static int strongLungsMaxLevel = DEFAULT_MAX_ABILITY_LEVEL;
    private static int luckyShotMaxLevel = DEFAULT_MAX_ABILITY_LEVEL;
    private static int criticalStrikeMaxLevel = DEFAULT_MAX_ABILITY_LEVEL;
    private static int lifestealMaxLevel = DEFAULT_MAX_ABILITY_LEVEL;
    private static int thornsMaxLevel = DEFAULT_MAX_ABILITY_LEVEL;
    private static int toolProficiencyMaxLevel = DEFAULT_MAX_ABILITY_LEVEL;
    private static int luckyMinerMaxLevel = DEFAULT_MAX_ABILITY_LEVEL;
    private static int gourmandMaxLevel = DEFAULT_MAX_ABILITY_LEVEL;
    private static int flameTouchMaxLevel = DEFAULT_MAX_ABILITY_LEVEL;

    public static final BuilderCodec<RpgStats> CODEC =
            BuilderCodec.builder(RpgStats.class, RpgStats::new)
                    .append(new KeyedCodec<>("Version", Codec.INTEGER), (d, v) -> d.version = v, d -> d.version).add()
                    .append(new KeyedCodec<>("Level", Codec.INTEGER), (d, v) -> d.level = v, d -> d.level).add()
                    .append(new KeyedCodec<>("Xp", Codec.LONG), (d, v) -> d.xp = v, d -> d.xp).add()
                    .append(new KeyedCodec<>("StatHistory", Codec.STRING_ARRAY), (d, v) -> d.statHistory = v, d -> d.statHistory).add()
                    .append(new KeyedCodec<>("AbilityPointsBonus", Codec.INTEGER),
                            (d, v) -> d.abilityPointsBonus = v, d -> d.abilityPointsBonus).add()
                    .append(new KeyedCodec<>("LightFootLevel", Codec.INTEGER),
                            (d, v) -> d.lightFootLevel = v, d -> d.lightFootLevel).add()
                    .append(new KeyedCodec<>("ArmorProficiencyLevel", Codec.INTEGER),
                            (d, v) -> d.armorProficiencyLevel = v, d -> d.armorProficiencyLevel).add()
                    .append(new KeyedCodec<>("HealthRegenLevel", Codec.INTEGER),
                            (d, v) -> d.healthRegenLevel = v, d -> d.healthRegenLevel).add()
                    .append(new KeyedCodec<>("StaminaRegenLevel", Codec.INTEGER),
                            (d, v) -> d.staminaRegenLevel = v, d -> d.staminaRegenLevel).add()
                    .append(new KeyedCodec<>("GlancingBlowLevel", Codec.INTEGER),
                            (d, v) -> d.glancingBlowLevel = v, d -> d.glancingBlowLevel).add()
                    .append(new KeyedCodec<>("StrongLungsLevel", Codec.INTEGER),
                            (d, v) -> d.strongLungsLevel = v, d -> d.strongLungsLevel).add()
                    .append(new KeyedCodec<>("LuckyShotLevel", Codec.INTEGER),
                            (d, v) -> d.luckyShotLevel = v, d -> d.luckyShotLevel).add()
                    .append(new KeyedCodec<>("CriticalStrikeLevel", Codec.INTEGER),
                            (d, v) -> d.criticalStrikeLevel = v, d -> d.criticalStrikeLevel).add()
                    .append(new KeyedCodec<>("LifestealLevel", Codec.INTEGER),
                            (d, v) -> d.lifestealLevel = v, d -> d.lifestealLevel).add()
                    .append(new KeyedCodec<>("ThornsLevel", Codec.INTEGER),
                            (d, v) -> d.thornsLevel = v, d -> d.thornsLevel).add()
                    .append(new KeyedCodec<>("ToolProficiencyLevel", Codec.INTEGER),
                            (d, v) -> d.toolProficiencyLevel = v, d -> d.toolProficiencyLevel).add()
                    .append(new KeyedCodec<>("LuckyMinerLevel", Codec.INTEGER),
                            (d, v) -> d.luckyMinerLevel = v, d -> d.luckyMinerLevel).add()
                    .append(new KeyedCodec<>("GourmandLevel", Codec.INTEGER),
                            (d, v) -> d.gourmandLevel = v, d -> d.gourmandLevel).add()
                    .append(new KeyedCodec<>("FlameTouchLevel", Codec.INTEGER),
                            (d, v) -> d.flameTouchLevel = v, d -> d.flameTouchLevel).add()

                    .append(new KeyedCodec<>("Str", Codec.INTEGER), (d, v) -> d.str = v, d -> d.str).add()
                    .append(new KeyedCodec<>("Dex", Codec.INTEGER), (d, v) -> d.dex = v, d -> d.dex).add()
                    .append(new KeyedCodec<>("Con", Codec.INTEGER), (d, v) -> d.con = v, d -> d.con).add()
                    .append(new KeyedCodec<>("Intl", Codec.INTEGER), (d, v) -> d.intl = v, d -> d.intl).add()
                    .append(new KeyedCodec<>("End", Codec.INTEGER), (d, v) -> d.end = v, d -> d.end).add()
                    .append(new KeyedCodec<>("Cha", Codec.INTEGER), (d, v) -> d.cha = v, d -> d.cha).add()
                    .build();

    private int version =  CURRENT_VERSION;

    private static int maxLevel = DEFAULT_MAX_LEVEL;
    private static int abilityPointsPerLevel = DEFAULT_ABILITY_POINTS_PER_LEVEL;
    private static int abilityRank1Cost = 1;
    private static int abilityRank2Cost = 2;
    private static int abilityRank3Cost = 3;

    private int level = 1;
    private long xp = 0L;
    private String[] statHistory = new String[0];
    private int abilityPointsBonus = 0;
    private int lightFootLevel = 0;
    private int armorProficiencyLevel = 0;
    private int healthRegenLevel = 0;
    private int staminaRegenLevel = 0;
    private int glancingBlowLevel = 0;
    private int strongLungsLevel = 0;
    private int luckyShotLevel = 0;
    private int criticalStrikeLevel = 0;
    private int lifestealLevel = 0;
    private int thornsLevel = 0;
    private int toolProficiencyLevel = 0;
    private int luckyMinerLevel = 0;
    private int gourmandLevel = 0;
    private int flameTouchLevel = 0;
    private boolean syncingLevel = false;

    private int str = BASE_STAT, dex = BASE_STAT, con = BASE_STAT, intl = BASE_STAT, end = BASE_STAT, cha = BASE_STAT;

    public void migrateIfNeeded() {
        if (version < 1) {
            version = 1;
        }
        if (version < 2) {
            syncLevelToXp();
            version = 2;
        }
        if (version < 3) {
            statHistory = new String[0];
            syncLevelToXp();
            version = 3;
        }
        if (version < 4) {
            migrateWisToEnd();
            syncLevelToXp();
            version = 4;
        }
        if (version < 5) {
            abilityPointsBonus = 0;
            version = 5;
        }
        if (version < 6) {
            lightFootLevel = 0;
            version = 6;
        }
        if (version < 7) {
            armorProficiencyLevel = 0;
            version = 7;
        }
        if (version < 8) {
            healthRegenLevel = 0;
            staminaRegenLevel = 0;
            version = 8;
        }
        if (version < 9) {
            glancingBlowLevel = 0;
            version = 9;
        }
        if (version < 10) {
            strongLungsLevel = 0;
            version = 10;
        }
        if (version < 11) {
            luckyShotLevel = 0;
            version = 11;
        }
        if (version < 12) {
            criticalStrikeLevel = 0;
            version = 12;
        }
        if (version < 13) {
            lifestealLevel = 0;
            version = 13;
        }
        if (version < 14) {
            thornsLevel = 0;
            version = 14;
        }
        if (version < 15) {
            toolProficiencyLevel = 0;
            version = 15;
        }
        if (version < 16) {
            luckyMinerLevel = 0;
            version = 16;
        }
        if (version < 18) {
            gourmandLevel = 0;
            version = 18;
        }
    }

    public int modifier(int score) {
        return (score - 10) / 2;
    }

    public void resetToDefaults() {
        version = CURRENT_VERSION;
        statHistory = new String[0];
        xp = 0L;
        syncLevelToXp();
        abilityPointsBonus = 0;
        lightFootLevel = 0;
        armorProficiencyLevel = 0;
        healthRegenLevel = 0;
        staminaRegenLevel = 0;
        glancingBlowLevel = 0;
        strongLungsLevel = 0;
        luckyShotLevel = 0;
        criticalStrikeLevel = 0;
        lifestealLevel = 0;
        thornsLevel = 0;
        toolProficiencyLevel = 0;
        luckyMinerLevel = 0;
        gourmandLevel = 0;
        str = BASE_STAT;
        dex = BASE_STAT;
        con = BASE_STAT;
        intl = BASE_STAT;
        end = BASE_STAT;
        cha = BASE_STAT;
    }

    /**
     * Refunds all spent attribute points, resetting STR/DEX/CON/INT/END/CHA to BASE_STAT (10).
     * The points are returned to the available pool.
     * @return the number of attribute points refunded
     */
    public int refundAttributes() {
        int refunded = getStatHistorySize();
        statHistory = new String[0];
        str = BASE_STAT;
        dex = BASE_STAT;
        con = BASE_STAT;
        intl = BASE_STAT;
        end = BASE_STAT;
        cha = BASE_STAT;
        return refunded;
    }

    /**
     * Refunds all spent ability points, resetting all ability levels to 0.
     * The points are returned to the available pool.
     * @return the number of ability points refunded
     */
    public int refundAbilities() {
        int refunded = getAbilityPointsSpent();
        lightFootLevel = 0;
        armorProficiencyLevel = 0;
        healthRegenLevel = 0;
        staminaRegenLevel = 0;
        glancingBlowLevel = 0;
        strongLungsLevel = 0;
        luckyShotLevel = 0;
        criticalStrikeLevel = 0;
        lifestealLevel = 0;
        thornsLevel = 0;
        toolProficiencyLevel = 0;
        luckyMinerLevel = 0;
        gourmandLevel = 0;
        return refunded;
    }

    public void setLevel(int level) {
        int clamped = clamp(level, 1, getMaxLevel());
        xp = totalXpForLevel(clamped);
        syncLevelToXp();
    }

    public void setXp(long xp) {
        this.xp = Math.max(0L, xp);
        syncLevelToXp();
    }
    public void setStr(int str) { this.str = clamp(str, 1, Integer.MAX_VALUE); }
    public void setDex(int dex) { this.dex = clamp(dex, 1, Integer.MAX_VALUE); }
    public void setCon(int con) { this.con = clamp(con, 1, Integer.MAX_VALUE); }
    public void setIntl(int intl) { this.intl = clamp(intl, 1, Integer.MAX_VALUE); }
    public void setEnd(int end) { this.end = clamp(end, 1, Integer.MAX_VALUE); }
    public void setCha(int cha) { this.cha = clamp(cha, 1, Integer.MAX_VALUE); }

    //minimal getters for /stats
    public int getLevel() {
        syncLevelToXp();
        return level;
    }

    public long getXp() { return xp; }

    public int getAvailableStatPoints() {
        syncLevelToXp();
        return Math.max(0, totalStatPointsEarned() - getStatHistorySize());
    }

    public int getAvailableAbilityPoints() {
        syncLevelToXp();
        long total = totalAbilityPointsEarned() + (long) abilityPointsBonus - getAbilityPointsSpent();
        return clampToInt(total);
    }

    public long getXpIntoLevel() {
        long totalForLevel = totalXpForLevel(getLevel());
        return Math.max(0L, xp - totalForLevel);
    }

    public long getXpToNextLevel() {
        int currentLevel = getLevel();
        if (currentLevel >= getMaxLevel()) {
            return 0L;
        }
        return xpToNext(currentLevel) - getXpIntoLevel();
    }

    public int getStr() { return str; }
    public int getDex() { return dex; }
    public int getCon() { return con; }
    public int getIntl() { return intl; }
    public int getEnd() { return end; }
    public int getCha() { return cha; }
    public int getLightFootLevel() {
        syncLevelToXp();
        return lightFootLevel;
    }

    public boolean upgradeLightFoot() {
        syncLevelToXp();
        if (lightFootLevel >= lightFootMaxLevel) {
            return false;
        }
        int cost = getAbilityUpgradeCost(lightFootLevel, lightFootMaxLevel);
        if (getAvailableAbilityPoints() < cost) {
            return false;
        }
        lightFootLevel++;
        return true;
    }

    public int getArmorProficiencyLevel() {
        syncLevelToXp();
        return armorProficiencyLevel;
    }

    public boolean upgradeArmorProficiency() {
        syncLevelToXp();
        if (armorProficiencyLevel >= armorProficiencyMaxLevel) {
            return false;
        }
        int cost = getAbilityUpgradeCost(armorProficiencyLevel, armorProficiencyMaxLevel);
        if (getAvailableAbilityPoints() < cost) {
            return false;
        }
        armorProficiencyLevel++;
        return true;
    }

    public int getHealthRegenLevel() {
        syncLevelToXp();
        return healthRegenLevel;
    }

    public boolean upgradeHealthRegen() {
        syncLevelToXp();
        if (healthRegenLevel >= healthRegenMaxLevel) {
            return false;
        }
        int cost = getAbilityUpgradeCost(healthRegenLevel, healthRegenMaxLevel);
        if (getAvailableAbilityPoints() < cost) {
            return false;
        }
        healthRegenLevel++;
        return true;
    }

    public int getStaminaRegenLevel() {
        syncLevelToXp();
        return staminaRegenLevel;
    }

    public boolean upgradeStaminaRegen() {
        syncLevelToXp();
        if (staminaRegenLevel >= staminaRegenMaxLevel) {
            return false;
        }
        int cost = getAbilityUpgradeCost(staminaRegenLevel, staminaRegenMaxLevel);
        if (getAvailableAbilityPoints() < cost) {
            return false;
        }
        staminaRegenLevel++;
        return true;
    }

    public int getGlancingBlowLevel() {
        syncLevelToXp();
        return glancingBlowLevel;
    }

    public boolean upgradeGlancingBlow() {
        syncLevelToXp();
        if (glancingBlowLevel >= glancingBlowMaxLevel) {
            return false;
        }
        int cost = getAbilityUpgradeCost(glancingBlowLevel, glancingBlowMaxLevel);
        if (getAvailableAbilityPoints() < cost) {
            return false;
        }
        glancingBlowLevel++;
        return true;
    }

    public int getStrongLungsLevel() {
        syncLevelToXp();
        return strongLungsLevel;
    }

    public boolean upgradeStrongLungs() {
        syncLevelToXp();
        if (strongLungsLevel >= strongLungsMaxLevel) {
            return false;
        }
        int cost = getAbilityUpgradeCost(strongLungsLevel, strongLungsMaxLevel);
        if (getAvailableAbilityPoints() < cost) {
            return false;
        }
        strongLungsLevel++;
        return true;
    }

    public int getLuckyShotLevel() {
        syncLevelToXp();
        return luckyShotLevel;
    }

    public boolean upgradeLuckyShot() {
        syncLevelToXp();
        if (luckyShotLevel >= luckyShotMaxLevel) {
            return false;
        }
        int cost = getAbilityUpgradeCost(luckyShotLevel, luckyShotMaxLevel);
        if (getAvailableAbilityPoints() < cost) {
            return false;
        }
        luckyShotLevel++;
        return true;
    }

    public int getCriticalStrikeLevel() {
        syncLevelToXp();
        return criticalStrikeLevel;
    }

    public boolean upgradeCriticalStrike() {
        syncLevelToXp();
        if (criticalStrikeLevel >= criticalStrikeMaxLevel) {
            return false;
        }
        int cost = getAbilityUpgradeCost(criticalStrikeLevel, criticalStrikeMaxLevel);
        if (getAvailableAbilityPoints() < cost) {
            return false;
        }
        criticalStrikeLevel++;
        return true;
    }

    public int getLifestealLevel() {
        syncLevelToXp();
        return lifestealLevel;
    }

    public boolean upgradeLifesteal() {
        syncLevelToXp();
        if (lifestealLevel >= lifestealMaxLevel) {
            return false;
        }
        int cost = getAbilityUpgradeCost(lifestealLevel, lifestealMaxLevel);
        if (getAvailableAbilityPoints() < cost) {
            return false;
        }
        lifestealLevel++;
        return true;
    }

    public int getThornsLevel() {
        syncLevelToXp();
        return thornsLevel;
    }

    public boolean upgradeThorns() {
        syncLevelToXp();
        if (thornsLevel >= thornsMaxLevel) {
            return false;
        }
        int cost = getAbilityUpgradeCost(thornsLevel, thornsMaxLevel);
        if (getAvailableAbilityPoints() < cost) {
            return false;
        }
        thornsLevel++;
        return true;
    }

    public int getToolProficiencyLevel() {
        syncLevelToXp();
        return toolProficiencyLevel;
    }

    public boolean upgradeToolProficiency() {
        syncLevelToXp();
        if (toolProficiencyLevel >= toolProficiencyMaxLevel) {
            return false;
        }
        int cost = getAbilityUpgradeCost(toolProficiencyLevel, toolProficiencyMaxLevel);
        if (getAvailableAbilityPoints() < cost) {
            return false;
        }
        toolProficiencyLevel++;
        return true;
    }

    public int getLuckyMinerLevel() {
        syncLevelToXp();
        return luckyMinerLevel;
    }

    public boolean upgradeLuckyMiner() {
        syncLevelToXp();
        if (luckyMinerLevel >= luckyMinerMaxLevel) {
            return false;
        }
        int cost = getAbilityUpgradeCost(luckyMinerLevel, luckyMinerMaxLevel);
        if (getAvailableAbilityPoints() < cost) {
            return false;
        }
        luckyMinerLevel++;
        return true;
    }

    public int getGourmandLevel() {
        syncLevelToXp();
        return gourmandLevel;
    }

    public boolean upgradeGourmand() {
        syncLevelToXp();
        if (gourmandLevel >= gourmandMaxLevel) {
            return false;
        }
        int cost = getAbilityUpgradeCost(gourmandLevel, gourmandMaxLevel);
        if (getAvailableAbilityPoints() < cost) {
            return false;
        }
        gourmandLevel++;
        return true;
    }

    public int getFlameTouchLevel() {
        syncLevelToXp();
        return flameTouchLevel;
    }

    public boolean upgradeFlameTouch() {
        syncLevelToXp();
        if (flameTouchLevel >= flameTouchMaxLevel) {
            return false;
        }
        int cost = getAbilityUpgradeCost(flameTouchLevel, flameTouchMaxLevel);
        if (getAvailableAbilityPoints() < cost) {
            return false;
        }
        flameTouchLevel++;
        return true;
    }

    public boolean spendStatPoint(String attributeRaw) {
        if (getAvailableStatPoints() <= 0) {
            return false;
        }
        String attribute = normalizeAttribute(attributeRaw);
        if (attribute == null) {
            return false;
        }
        applyStatDelta(attribute, 1);
        pushStatHistory(attribute);
        return true;
    }

    @Override
    public Component<EntityStore> clone() {
        RpgStats copy = new RpgStats();
        copy.version = this.version;
        copy.level = this.level;
        copy.xp = this.xp;
        copy.statHistory = statHistory == null ? new String[0] : Arrays.copyOf(statHistory, statHistory.length);
        copy.abilityPointsBonus = this.abilityPointsBonus;
        copy.lightFootLevel = this.lightFootLevel;
        copy.armorProficiencyLevel = this.armorProficiencyLevel;
        copy.healthRegenLevel = this.healthRegenLevel;
        copy.staminaRegenLevel = this.staminaRegenLevel;
        copy.glancingBlowLevel = this.glancingBlowLevel;
        copy.strongLungsLevel = this.strongLungsLevel;
        copy.luckyShotLevel = this.luckyShotLevel;
        copy.criticalStrikeLevel = this.criticalStrikeLevel;
        copy.lifestealLevel = this.lifestealLevel;
        copy.thornsLevel = this.thornsLevel;
        copy.toolProficiencyLevel = this.toolProficiencyLevel;
        copy.luckyMinerLevel = this.luckyMinerLevel;
        copy.gourmandLevel = this.gourmandLevel;
        copy.flameTouchLevel = this.flameTouchLevel;
        copy.str = this.str;
        copy.dex = this.dex;
        copy.con = this.con;
        copy.intl = this.intl;
        copy.end = this.end;
        copy.cha = this.cha;
        return copy;
    }

    private void syncLevelToXp() {
        if (syncingLevel) {
            return;
        }
        syncingLevel = true;
        try {
            int newLevel = levelForTotalXp(xp);
            if (newLevel != level) {
                level = newLevel;
            }
            reconcileStatPoints();
            reconcileAbilityPoints();
        } finally {
            syncingLevel = false;
        }
    }

    private void reconcileStatPoints() {
        int totalPoints = totalStatPointsEarned();
        if (statHistory == null) {
            statHistory = new String[0];
        }
        while (statHistory.length > totalPoints) {
            undoLastStatPoint();
        }
    }

    private boolean undoLastStatPoint() {
        if (statHistory == null || statHistory.length == 0) {
            return false;
        }
        String attribute = statHistory[statHistory.length - 1];
        statHistory = Arrays.copyOf(statHistory, statHistory.length - 1);
        applyStatDelta(attribute, -1);
        return true;
    }

    private void pushStatHistory(String attribute) {
        if (statHistory == null) {
            statHistory = new String[0];
        }
        String[] next = Arrays.copyOf(statHistory, statHistory.length + 1);
        next[next.length - 1] = attribute;
        statHistory = next;
    }

    private int getStatHistorySize() {
        return statHistory == null ? 0 : statHistory.length;
    }

    private int totalStatPointsEarned() {
        return Math.max(0, level - 1);
    }

    private int getAbilityPointsSpent() {
        int spent = 0;
        if (lightFootLevel > 0) {
            spent += totalCostForLevels(lightFootLevel);
        }
        if (armorProficiencyLevel > 0) {
            spent += totalCostForLevels(armorProficiencyLevel);
        }
        if (healthRegenLevel > 0) {
            spent += totalCostForLevels(healthRegenLevel);
        }
        if (staminaRegenLevel > 0) {
            spent += totalCostForLevels(staminaRegenLevel);
        }
        if (glancingBlowLevel > 0) {
            spent += totalCostForLevels(glancingBlowLevel);
        }
        if (strongLungsLevel > 0) {
            spent += totalCostForLevels(strongLungsLevel);
        }
        if (luckyShotLevel > 0) {
            spent += totalCostForLevels(luckyShotLevel);
        }
        if (criticalStrikeLevel > 0) {
            spent += totalCostForLevels(criticalStrikeLevel);
        }
        if (lifestealLevel > 0) {
            spent += totalCostForLevels(lifestealLevel);
        }
        if (thornsLevel > 0) {
            spent += totalCostForLevels(thornsLevel);
        }
        if (toolProficiencyLevel > 0) {
            spent += totalCostForLevels(toolProficiencyLevel);
        }
        if (luckyMinerLevel > 0) {
            spent += totalCostForLevels(luckyMinerLevel);
        }
        if (gourmandLevel > 0) {
            spent += totalCostForLevels(gourmandLevel);
        }
        if (flameTouchLevel > 0) {
            spent += totalCostForLevels(flameTouchLevel);
        }
        return Math.max(0, spent);
    }

    private long totalAbilityPointsEarned() {
        if (abilityPointsPerLevel <= 0) {
            return 0L;
        }
        long levels = Math.max(0, level - 1);
        return levels * (long) abilityPointsPerLevel;
    }

    public int addAbilityPoints(int amount) {
        if (amount == 0) {
            return 0;
        }
        long next = (long) abilityPointsBonus + amount;
        if (next < 0L) {
            next = 0L;
        } else if (next > Integer.MAX_VALUE) {
            next = Integer.MAX_VALUE;
        }
        int applied = (int) next;
        int delta = applied - abilityPointsBonus;
        abilityPointsBonus = applied;
        reconcileAbilityPoints();
        return delta;
    }

    private void reconcileAbilityPoints() {
        long total = totalAbilityPointsEarned() + (long) abilityPointsBonus;
        int maxAllowed = clampToInt(total);
        lightFootLevel = clamp(lightFootLevel, 0, lightFootMaxLevel);
        armorProficiencyLevel = clamp(armorProficiencyLevel, 0, armorProficiencyMaxLevel);
        healthRegenLevel = clamp(healthRegenLevel, 0, healthRegenMaxLevel);
        staminaRegenLevel = clamp(staminaRegenLevel, 0, staminaRegenMaxLevel);
        glancingBlowLevel = clamp(glancingBlowLevel, 0, glancingBlowMaxLevel);
        strongLungsLevel = clamp(strongLungsLevel, 0, strongLungsMaxLevel);
        luckyShotLevel = clamp(luckyShotLevel, 0, luckyShotMaxLevel);
        criticalStrikeLevel = clamp(criticalStrikeLevel, 0, criticalStrikeMaxLevel);
        lifestealLevel = clamp(lifestealLevel, 0, lifestealMaxLevel);
        thornsLevel = clamp(thornsLevel, 0, thornsMaxLevel);
        toolProficiencyLevel = clamp(toolProficiencyLevel, 0, toolProficiencyMaxLevel);
        luckyMinerLevel = clamp(luckyMinerLevel, 0, luckyMinerMaxLevel);
        gourmandLevel = clamp(gourmandLevel, 0, gourmandMaxLevel);
        flameTouchLevel = clamp(flameTouchLevel, 0, flameTouchMaxLevel);

        if (getAbilityPointsSpent() > maxAllowed) {
            trimAbilityLevelsToPoints(maxAllowed);
        }
    }

    public static int getAbilityUpgradeCost(int currentLevel, int maxLevel) {
        if (currentLevel < 0) {
            currentLevel = 0;
        }
        if (currentLevel >= maxLevel) {
            return 0;
        }
        return getAbilityRankCost(currentLevel + 1);
    }

    public static int getAbilityRankCost(int rank) {
        switch (rank) {
            case 1:
                return abilityRank1Cost;
            case 2:
                return abilityRank2Cost;
            case 3:
                return abilityRank3Cost;
            default:
                return rank; // Fallback for ranks beyond 3
        }
    }

    private static int totalCostForLevels(int levels) {
        int clamped = Math.max(0, levels);
        int total = 0;
        for (int i = 1; i <= clamped; i++) {
            total += getAbilityRankCost(i);
        }
        return total;
    }

    private void trimAbilityLevelsToPoints(int maxAllowed) {
        if (maxAllowed <= 0) {
            lightFootLevel = 0;
            armorProficiencyLevel = 0;
            healthRegenLevel = 0;
            staminaRegenLevel = 0;
            glancingBlowLevel = 0;
            strongLungsLevel = 0;
            luckyShotLevel = 0;
            criticalStrikeLevel = 0;
            lifestealLevel = 0;
            thornsLevel = 0;
            toolProficiencyLevel = 0;
            luckyMinerLevel = 0;
            gourmandLevel = 0;
            flameTouchLevel = 0;
            return;
        }
        int guard = lightFootMaxLevel + armorProficiencyMaxLevel + healthRegenMaxLevel + staminaRegenMaxLevel + glancingBlowMaxLevel + strongLungsMaxLevel + luckyShotMaxLevel + criticalStrikeMaxLevel + lifestealMaxLevel + thornsMaxLevel + toolProficiencyMaxLevel + luckyMinerMaxLevel + gourmandMaxLevel + flameTouchMaxLevel + 2;
        while (getAbilityPointsSpent() > maxAllowed && guard-- > 0) {
            if (flameTouchLevel > 0) {
                flameTouchLevel--;
                continue;
            }
            if (gourmandLevel > 0) {
                gourmandLevel--;
                continue;
            }
            if (luckyMinerLevel > 0) {
                luckyMinerLevel--;
                continue;
            }
            if (toolProficiencyLevel > 0) {
                toolProficiencyLevel--;
                continue;
            }
            if (thornsLevel > 0) {
                thornsLevel--;
                continue;
            }
            if (lifestealLevel > 0) {
                lifestealLevel--;
                continue;
            }
            if (criticalStrikeLevel > 0) {
                criticalStrikeLevel--;
                continue;
            }
            if (luckyShotLevel > 0) {
                luckyShotLevel--;
                continue;
            }
            if (strongLungsLevel > 0) {
                strongLungsLevel--;
                continue;
            }
            if (glancingBlowLevel > 0) {
                glancingBlowLevel--;
                continue;
            }
            if (staminaRegenLevel > 0) {
                staminaRegenLevel--;
                continue;
            }
            if (healthRegenLevel > 0) {
                healthRegenLevel--;
                continue;
            }
            if (armorProficiencyLevel > 0) {
                armorProficiencyLevel--;
                continue;
            }
            if (lightFootLevel > 0) {
                lightFootLevel--;
            } else {
                break;
            }
        }
    }

    private void migrateWisToEnd() {
        if (statHistory == null || statHistory.length == 0) {
            return;
        }
        int converted = 0;
        for (int i = 0; i < statHistory.length; i++) {
            if ("wis".equals(statHistory[i])) {
                statHistory[i] = "end";
                converted++;
            }
        }
        if (converted > 0) {
            end = clamp(end + converted, 1, Integer.MAX_VALUE);
        }
    }

    private void applyStatDelta(String attribute, int delta) {
        switch (attribute) {
            case "str":
                str = clamp(str + delta, 1, Integer.MAX_VALUE);
                break;
            case "dex":
                dex = clamp(dex + delta, 1, Integer.MAX_VALUE);
                break;
            case "con":
                con = clamp(con + delta, 1, Integer.MAX_VALUE);
                break;
            case "int":
                intl = clamp(intl + delta, 1, Integer.MAX_VALUE);
                break;
            case "end":
                end = clamp(end + delta, 1, Integer.MAX_VALUE);
                break;
            case "cha":
                cha = clamp(cha + delta, 1, Integer.MAX_VALUE);
                break;
            default:
                break;
        }
    }

    private String normalizeAttribute(String attributeRaw) {
        if (attributeRaw == null) {
            return null;
        }
        String attribute = attributeRaw.trim().toLowerCase();
        switch (attribute) {
            case "str":
            case "dex":
            case "con":
            case "end":
            case "cha":
                return attribute;
            case "endurance":
                return "end";
            case "int":
            case "intl":
                return "int";
            default:
                return null;
        }
    }

    public static long xpToNext(int level) {
        if (level >= getMaxLevel()) {
            return 0L;
        }
        int l = Math.max(0, level - 1);
        return BASE_XP + (long) LINEAR_XP * l + (long) QUADRATIC_XP * l * l;
    }

    public static long totalXpForLevel(int level) {
        int clamped = clamp(level, 1, getMaxLevel());
        long total = 0L;
        for (int i = 1; i < clamped; i++) {
            total += xpToNext(i);
        }
        return total;
    }

    public static int levelForTotalXp(long xp) {
        long remaining = Math.max(0L, xp);
        int level = 1;
        while (level < getMaxLevel()) {
            long next = xpToNext(level);
            if (remaining < next) {
                break;
            }
            remaining -= next;
            level++;
        }
        return level;
    }

    public static int getMaxLevel() {
        return maxLevel;
    }

    public static void setMaxLevel(int newMaxLevel) {
        maxLevel = clamp(newMaxLevel, 1, Integer.MAX_VALUE);
    }

    public static int getAbilityPointsPerLevel() {
        return abilityPointsPerLevel;
    }

    public static void setAbilityPointsPerLevel(int newAbilityPointsPerLevel) {
        int maxAllowed = maxAbilityPointsPerLevel(maxLevel);
        abilityPointsPerLevel = clamp(newAbilityPointsPerLevel, 0, maxAllowed);
    }

    public static void setAbilityRankCosts(int rank1Cost, int rank2Cost, int rank3Cost) {
        abilityRank1Cost = Math.max(0, rank1Cost);
        abilityRank2Cost = Math.max(0, rank2Cost);
        abilityRank3Cost = Math.max(0, rank3Cost);
    }

    public static int getAbilityRank1Cost() {
        return abilityRank1Cost;
    }

    public static int getAbilityRank2Cost() {
        return abilityRank2Cost;
    }

    public static int getAbilityRank3Cost() {
        return abilityRank3Cost;
    }

    /**
     * Sets the max level for all abilities to the same value.
     * @param newMaxLevel the new max level (clamped to 1-10)
     */
    public static void setMaxAbilityLevel(int newMaxLevel) {
        int clamped = clamp(newMaxLevel, MIN_MAX_ABILITY_LEVEL, MAX_MAX_ABILITY_LEVEL);
        lightFootMaxLevel = clamped;
        armorProficiencyMaxLevel = clamped;
        healthRegenMaxLevel = clamped;
        staminaRegenMaxLevel = clamped;
        glancingBlowMaxLevel = clamped;
        strongLungsMaxLevel = clamped;
        luckyShotMaxLevel = clamped;
        criticalStrikeMaxLevel = clamped;
        lifestealMaxLevel = clamped;
        thornsMaxLevel = clamped;
        toolProficiencyMaxLevel = clamped;
        luckyMinerMaxLevel = clamped;
        gourmandMaxLevel = clamped;
        flameTouchMaxLevel = clamped;
    }

    public static int getMaxAbilityLevel() {
        // Return the common max level (all are set to the same value)
        return lightFootMaxLevel;
    }

    // Individual ability max level getters for UI display
    public static int getLightFootMaxLevel() { return lightFootMaxLevel; }
    public static int getArmorProficiencyMaxLevel() { return armorProficiencyMaxLevel; }
    public static int getHealthRegenMaxLevel() { return healthRegenMaxLevel; }
    public static int getStaminaRegenMaxLevel() { return staminaRegenMaxLevel; }
    public static int getGlancingBlowMaxLevel() { return glancingBlowMaxLevel; }
    public static int getStrongLungsMaxLevel() { return strongLungsMaxLevel; }
    public static int getLuckyShotMaxLevel() { return luckyShotMaxLevel; }
    public static int getCriticalStrikeMaxLevel() { return criticalStrikeMaxLevel; }
    public static int getLifestealMaxLevel() { return lifestealMaxLevel; }
    public static int getThornsMaxLevel() { return thornsMaxLevel; }
    public static int getToolProficiencyMaxLevel() { return toolProficiencyMaxLevel; }
    public static int getLuckyMinerMaxLevel() { return luckyMinerMaxLevel; }
    public static int getGourmandMaxLevel() { return gourmandMaxLevel; }
    public static int getFlameTouchMaxLevel() { return flameTouchMaxLevel; }

    private static int maxAbilityPointsPerLevel(int maxLevel) {
        int levels = Math.max(1, maxLevel - 1);
        return Integer.MAX_VALUE / levels;
    }

    private static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    private static int clampToInt(long value) {
        if (value < 0L) {
            return 0;
        }
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) value;
    }
}
