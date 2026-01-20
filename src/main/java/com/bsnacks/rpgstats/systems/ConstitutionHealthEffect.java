package com.bsnacks.rpgstats.systems;

import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;

public final class ConstitutionHealthEffect {

    private static final String MODIFIER_KEY = "rpgstats:con_health";
    private ConstitutionHealthEffect() {
    }

    public static void apply(EntityStatMap statMap, RpgStats stats, RpgStatsConfig config) {
        int pointsSpent = stats.getCon() - RpgStats.BASE_STAT;
        double perPoint = config == null ? 10.0 : config.getHealthPerPoint();
        float bonusHealth = (float) (pointsSpent * perPoint);
        int healthStatId = DefaultEntityStatTypes.getHealth();
        float minHealth = 10f;

        if (statMap.getModifier(healthStatId, MODIFIER_KEY) != null) {
            statMap.removeModifier(healthStatId, MODIFIER_KEY);
        }
        EntityStatValue healthStat = statMap.get(healthStatId);
        float baseMaxHealth = healthStat == null ? minHealth : healthStat.getMax();
        float minModifier = minHealth - baseMaxHealth;
        float clampedBonus = Math.max(bonusHealth, minModifier);
        if (clampedBonus == 0f) {
            return;
        }

        StaticModifier modifier = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                clampedBonus
        );
        statMap.putModifier(healthStatId, MODIFIER_KEY, modifier);
    }
}
