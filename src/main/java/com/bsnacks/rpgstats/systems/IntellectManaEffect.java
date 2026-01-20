package com.bsnacks.rpgstats.systems;

import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;

public final class IntellectManaEffect {

    private static final String MODIFIER_KEY = "rpgstats:int_mana";
    private IntellectManaEffect() {
    }

    public static void apply(EntityStatMap statMap, RpgStats stats, RpgStatsConfig config) {
        int pointsSpent = stats.getIntl() - RpgStats.BASE_STAT;
        double perPoint = config == null ? 10.0 : config.getManaPerPoint();
        float bonusMana = (float) (pointsSpent * perPoint);
        int manaStatId = DefaultEntityStatTypes.getMana();
        float minMana = 0f;

        if (statMap.getModifier(manaStatId, MODIFIER_KEY) != null) {
            statMap.removeModifier(manaStatId, MODIFIER_KEY);
        }
        EntityStatValue manaStat = statMap.get(manaStatId);
        float baseMaxMana = manaStat == null ? minMana : manaStat.getMax();
        float minModifier = minMana - baseMaxMana;
        float clampedBonus = Math.max(bonusMana, minModifier);
        if (clampedBonus == 0f) {
            return;
        }

        StaticModifier modifier = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                clampedBonus
        );
        statMap.putModifier(manaStatId, MODIFIER_KEY, modifier);
    }
}
