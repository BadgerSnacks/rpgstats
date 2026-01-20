package com.bsnacks.rpgstats.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Arrays;

public final class RpgStats implements Component<EntityStore> {

    public static final String COMPONENT_ID = "rpgstats";
    public static final int CURRENT_VERSION = 4;
    public static final int DEFAULT_MAX_LEVEL = 25;
    public static final int BASE_XP = 100;
    public static final int LINEAR_XP = 50;
    public static final int QUADRATIC_XP = 20;
    public static final int BASE_STAT = 10;

    public static final BuilderCodec<RpgStats> CODEC =
            BuilderCodec.builder(RpgStats.class, RpgStats::new)
                    .append(new KeyedCodec<>("Version", Codec.INTEGER), (d, v) -> d.version = v, d -> d.version).add()
                    .append(new KeyedCodec<>("Level", Codec.INTEGER), (d, v) -> d.level = v, d -> d.level).add()
                    .append(new KeyedCodec<>("Xp", Codec.LONG), (d, v) -> d.xp = v, d -> d.xp).add()
                    .append(new KeyedCodec<>("StatHistory", Codec.STRING_ARRAY), (d, v) -> d.statHistory = v, d -> d.statHistory).add()

                    .append(new KeyedCodec<>("Str", Codec.INTEGER), (d, v) -> d.str = v, d -> d.str).add()
                    .append(new KeyedCodec<>("Dex", Codec.INTEGER), (d, v) -> d.dex = v, d -> d.dex).add()
                    .append(new KeyedCodec<>("Con", Codec.INTEGER), (d, v) -> d.con = v, d -> d.con).add()
                    .append(new KeyedCodec<>("Intl", Codec.INTEGER), (d, v) -> d.intl = v, d -> d.intl).add()
                    .append(new KeyedCodec<>("End", Codec.INTEGER), (d, v) -> d.end = v, d -> d.end).add()
                    .append(new KeyedCodec<>("Cha", Codec.INTEGER), (d, v) -> d.cha = v, d -> d.cha).add()
                    .build();

    private int version =  CURRENT_VERSION;

    private static int maxLevel = DEFAULT_MAX_LEVEL;

    private int level = 1;
    private long xp = 0L;
    private String[] statHistory = new String[0];
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
    }

    public int modifier(int score) {
        return (score - 10) / 2;
    }

    public void resetToDefaults() {
        version = CURRENT_VERSION;
        statHistory = new String[0];
        xp = 0L;
        syncLevelToXp();
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

    private static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
