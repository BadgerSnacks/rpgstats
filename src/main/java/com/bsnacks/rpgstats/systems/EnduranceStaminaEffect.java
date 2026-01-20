package com.bsnacks.rpgstats.systems;

import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;

public final class EnduranceStaminaEffect {

    private static final String MODIFIER_KEY = "rpgstats:end_stamina";
    private EnduranceStaminaEffect() {
    }

    public static void apply(EntityStatMap statMap, RpgStats stats, RpgStatsConfig config) {
        int pointsSpent = stats.getEnd() - RpgStats.BASE_STAT;
        double perPoint = config == null ? 1.0 : config.getStaminaPerPoint();
        float bonusStamina = (float) (pointsSpent * perPoint);
        int staminaStatId = DefaultEntityStatTypes.getStamina();
        float minStamina = 1f;

        if (statMap.getModifier(staminaStatId, MODIFIER_KEY) != null) {
            statMap.removeModifier(staminaStatId, MODIFIER_KEY);
        }
        EntityStatValue staminaStat = statMap.get(staminaStatId);
        float baseMaxStamina = staminaStat == null ? minStamina : staminaStat.getMax();
        float minModifier = minStamina - baseMaxStamina;
        float clampedBonus = Math.max(bonusStamina, minModifier);
        if (clampedBonus == 0f) {
            return;
        }

        StaticModifier modifier = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                clampedBonus
        );
        statMap.putModifier(staminaStatId, MODIFIER_KEY, modifier);
    }
}
