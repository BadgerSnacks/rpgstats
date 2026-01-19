package com.bsnacks.rpgstats.systems;

import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;

public final class ConstitutionHealthEffect {

    private static final String MODIFIER_KEY = "rpgstats:con_health";
    private ConstitutionHealthEffect() {
    }

    public static void apply(EntityStatMap statMap, RpgStats stats, RpgStatsConfig config) {
        int pointsSpent = Math.max(0, stats.getCon() - RpgStats.BASE_STAT);
        double perPoint = config == null ? 10.0 : config.getHealthPerPoint();
        float bonusHealth = (float) (pointsSpent * perPoint);
        int healthStatId = DefaultEntityStatTypes.getHealth();

        if (bonusHealth <= 0f) {
            statMap.removeModifier(healthStatId, MODIFIER_KEY);
            return;
        }

        StaticModifier modifier = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                bonusHealth
        );
        statMap.putModifier(healthStatId, MODIFIER_KEY, modifier);
    }
}
