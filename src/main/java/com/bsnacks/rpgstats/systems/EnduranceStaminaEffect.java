package com.bsnacks.rpgstats.systems;

import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;

public final class EnduranceStaminaEffect {

    private static final String MODIFIER_KEY = "rpgstats:end_stamina";
    private EnduranceStaminaEffect() {
    }

    public static void apply(EntityStatMap statMap, RpgStats stats, RpgStatsConfig config) {
        int pointsSpent = Math.max(0, stats.getEnd() - RpgStats.BASE_STAT);
        double perPoint = config == null ? 1.0 : config.getStaminaPerPoint();
        float bonusStamina = (float) (pointsSpent * perPoint);
        int staminaStatId = DefaultEntityStatTypes.getStamina();

        if (bonusStamina <= 0f) {
            statMap.removeModifier(staminaStatId, MODIFIER_KEY);
            return;
        }

        StaticModifier modifier = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                bonusStamina
        );
        statMap.putModifier(staminaStatId, MODIFIER_KEY, modifier);
    }
}
