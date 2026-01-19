package com.bsnacks.rpgstats.systems;

import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;

public final class IntellectManaEffect {

    private static final String MODIFIER_KEY = "rpgstats:int_mana";
    private IntellectManaEffect() {
    }

    public static void apply(EntityStatMap statMap, RpgStats stats, RpgStatsConfig config) {
        int pointsSpent = Math.max(0, stats.getIntl() - RpgStats.BASE_STAT);
        double perPoint = config == null ? 10.0 : config.getManaPerPoint();
        float bonusMana = (float) (pointsSpent * perPoint);
        int manaStatId = DefaultEntityStatTypes.getMana();

        if (bonusMana <= 0f) {
            statMap.removeModifier(manaStatId, MODIFIER_KEY);
            return;
        }

        StaticModifier modifier = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                bonusMana
        );
        statMap.putModifier(manaStatId, MODIFIER_KEY, modifier);
    }
}
