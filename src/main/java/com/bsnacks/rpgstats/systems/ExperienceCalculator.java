package com.bsnacks.rpgstats.systems;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;

final class ExperienceCalculator {

    private static final float BOSS_HEALTH_THRESHOLD = 200f;
    private static final float BOSS_MULTIPLIER = 1.5f;
    private static final int MIN_XP = 1;
    private static final int MAX_XP = 1000;

    private ExperienceCalculator() {
    }

    static int calculate(NPCEntity npc, EntityStatMap statMap, double xpMultiplier) {
        float maxHealth = getMaxHealth(npc, statMap);
        if (maxHealth <= 0f) {
            return 0;
        }

        float xp = (float) (maxHealth * xpMultiplier);
        if (maxHealth >= BOSS_HEALTH_THRESHOLD) {
            xp *= BOSS_MULTIPLIER;
        }

        int rounded = Math.round(xp);
        if (rounded < MIN_XP) {
            return MIN_XP;
        }
        if (rounded > MAX_XP) {
            return MAX_XP;
        }
        return rounded;
    }

    static int calculate(NPCEntity npc, EntityStatMap statMap) {
        return calculate(npc, statMap, 0.35d);
    }

    static float getMaxHealth(NPCEntity npc, EntityStatMap statMap) {
        if (statMap != null) {
            EntityStatValue health = statMap.get(DefaultEntityStatTypes.getHealth());
            if (health != null) {
                return health.getMax();
            }
        }

        Role role = npc.getRole();
        if (role != null) {
            return role.getInitialMaxHealth();
        }

        return 0f;
    }
}
