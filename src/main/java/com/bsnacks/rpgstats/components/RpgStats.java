package com.bsnacks.rpgstats.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Arrays;

public final class RpgStats implements Component<EntityStore> {

    public static final String COMPONENT_ID = "rpgstats";
    public static final int CURRENT_VERSION = 14;
    public static final int DEFAULT_MAX_LEVEL = 25;
    public static final int BASE_XP = 100;
    public static final int LINEAR_XP = 50;
    public static final int QUADRATIC_XP = 20;
    public static final int BASE_STAT = 10;
    public static final int DEFAULT_ABILITY_POINTS_PER_LEVEL = 2;
    public static final int LIGHT_FOOT_MAX_LEVEL = 3;
    public static final int ARMOR_PROFICIENCY_MAX_LEVEL = 3;
    public static final int HEALTH_REGEN_MAX_LEVEL = 3;
    public static final int STAMINA_REGEN_MAX_LEVEL = 3;
    public static final int GLANCING_BLOW_MAX_LEVEL = 3;
    public static final int STRONG_LUNGS_MAX_LEVEL = 3;
    public static final int LUCKY_SHOT_MAX_LEVEL = 3;
    public static final int CRITICAL_STRIKE_MAX_LEVEL = 3;
    public static final int LIFESTEAL_MAX_LEVEL = 3;
    public static final int THORNS_MAX_LEVEL = 3;

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
        str = BASE_STAT;
        dex = BASE_STAT;
        con = BASE_STAT;
        intl = BASE_STAT;
        end = BASE_STAT;
        cha = BASE_STAT;
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
        if (lightFootLevel >= LIGHT_FOOT_MAX_LEVEL) {
            return false;
        }
        int cost = getAbilityUpgradeCost(lightFootLevel, LIGHT_FOOT_MAX_LEVEL);
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
        if (armorProficiencyLevel >= ARMOR_PROFICIENCY_MAX_LEVEL) {
            return false;
        }
        int cost = getAbilityUpgradeCost(armorProficiencyLevel, ARMOR_PROFICIENCY_MAX_LEVEL);
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
        if (healthRegenLevel >= HEALTH_REGEN_MAX_LEVEL) {
            return false;
        }
        int cost = getAbilityUpgradeCost(healthRegenLevel, HEALTH_REGEN_MAX_LEVEL);
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
        if (staminaRegenLevel >= STAMINA_REGEN_MAX_LEVEL) {
            return false;
        }
        int cost = getAbilityUpgradeCost(staminaRegenLevel, STAMINA_REGEN_MAX_LEVEL);
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
        if (glancingBlowLevel >= GLANCING_BLOW_MAX_LEVEL) {
            return false;
        }
        int cost = getAbilityUpgradeCost(glancingBlowLevel, GLANCING_BLOW_MAX_LEVEL);
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
        if (strongLungsLevel >= STRONG_LUNGS_MAX_LEVEL) {
            return false;
        }
        int cost = getAbilityUpgradeCost(strongLungsLevel, STRONG_LUNGS_MAX_LEVEL);
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
        if (luckyShotLevel >= LUCKY_SHOT_MAX_LEVEL) {
            return false;
        }
        int cost = getAbilityUpgradeCost(luckyShotLevel, LUCKY_SHOT_MAX_LEVEL);
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
        if (criticalStrikeLevel >= CRITICAL_STRIKE_MAX_LEVEL) {
            return false;
        }
        int cost = getAbilityUpgradeCost(criticalStrikeLevel, CRITICAL_STRIKE_MAX_LEVEL);
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
        if (lifestealLevel >= LIFESTEAL_MAX_LEVEL) {
            return false;
        }
        int cost = getAbilityUpgradeCost(lifestealLevel, LIFESTEAL_MAX_LEVEL);
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
        if (thornsLevel >= THORNS_MAX_LEVEL) {
            return false;
        }
        int cost = getAbilityUpgradeCost(thornsLevel, THORNS_MAX_LEVEL);
        if (getAvailableAbilityPoints() < cost) {
            return false;
        }
        thornsLevel++;
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
        lightFootLevel = clamp(lightFootLevel, 0, LIGHT_FOOT_MAX_LEVEL);
        armorProficiencyLevel = clamp(armorProficiencyLevel, 0, ARMOR_PROFICIENCY_MAX_LEVEL);
        healthRegenLevel = clamp(healthRegenLevel, 0, HEALTH_REGEN_MAX_LEVEL);
        staminaRegenLevel = clamp(staminaRegenLevel, 0, STAMINA_REGEN_MAX_LEVEL);
        glancingBlowLevel = clamp(glancingBlowLevel, 0, GLANCING_BLOW_MAX_LEVEL);
        strongLungsLevel = clamp(strongLungsLevel, 0, STRONG_LUNGS_MAX_LEVEL);
        luckyShotLevel = clamp(luckyShotLevel, 0, LUCKY_SHOT_MAX_LEVEL);
        criticalStrikeLevel = clamp(criticalStrikeLevel, 0, CRITICAL_STRIKE_MAX_LEVEL);
        lifestealLevel = clamp(lifestealLevel, 0, LIFESTEAL_MAX_LEVEL);
        thornsLevel = clamp(thornsLevel, 0, THORNS_MAX_LEVEL);

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
        return currentLevel + 1;
    }

    private static int totalCostForLevels(int levels) {
        int clamped = Math.max(0, levels);
        return (clamped * (clamped + 1)) / 2;
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
            return;
        }
        int guard = LIGHT_FOOT_MAX_LEVEL + ARMOR_PROFICIENCY_MAX_LEVEL + HEALTH_REGEN_MAX_LEVEL + STAMINA_REGEN_MAX_LEVEL + GLANCING_BLOW_MAX_LEVEL + STRONG_LUNGS_MAX_LEVEL + LUCKY_SHOT_MAX_LEVEL + CRITICAL_STRIKE_MAX_LEVEL + LIFESTEAL_MAX_LEVEL + THORNS_MAX_LEVEL + 2;
        while (getAbilityPointsSpent() > maxAllowed && guard-- > 0) {
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
