package com.bsnacks.rpgstats.systems;

import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;

public final class StrongLungsOxygenEffect {

    private static final String MODIFIER_KEY = "rpgstats:strong_lungs";
    private static final float DEFAULT_OXYGEN_PER_LEVEL_PCT = 50.0f;
    private StrongLungsOxygenEffect() {
    }

    public static void apply(EntityStatMap statMap, RpgStats stats, RpgStatsConfig config) {
        int level = stats.getStrongLungsLevel();
        if (level <= 0) {
            return;
        }

        double perLevelPct = config == null ? DEFAULT_OXYGEN_PER_LEVEL_PCT : config.getStrongLungsOxygenPerLevelPct();
        if (perLevelPct <= 0.0) {
            return;
        }

        int oxygenStatId = DefaultEntityStatTypes.getOxygen();

        if (statMap.getModifier(oxygenStatId, MODIFIER_KEY) != null) {
            statMap.removeModifier(oxygenStatId, MODIFIER_KEY);
        }

        EntityStatValue oxygenStat = statMap.get(oxygenStatId);
        float baseMaxOxygen = oxygenStat == null ? 0f : oxygenStat.getMax();
        if (baseMaxOxygen == 0f) {
            return;
        }

        float additiveBonus = baseMaxOxygen * (float) ((perLevelPct / 100.0) * level);
        if (additiveBonus == 0f) {
            return;
        }

        StaticModifier modifier = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                additiveBonus
        );
        statMap.putModifier(oxygenStatId, MODIFIER_KEY, modifier);
    }
}
