package com.bsnacks.rpgstats.systems;

import com.bsnacks.rpgstats.RpgStatsPlugin;
import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Random;

/**
 * Lucky Shot ability utility class.
 *
 * This ability gives a chance to not consume ammo when firing bows/crossbows.
 *
 * IMPLEMENTATION STATUS:
 * - Level tracking: WORKING (stored in RpgStats component)
 * - Config: WORKING (lucky_shot_chance_per_level_pct)
 * - UI: WORKING (upgrade buttons and display in abilities tab)
 * - Effect trigger: NEEDS API HOOK
 *
 * To fully implement, need to find the correct Hytale event/system for:
 * - Projectile creation (to identify shooter)
 * - Ammo consumption (to prevent/restore ammo)
 *
 * When the correct hook is found, call tryLuckyShot() to check if the
 * ability triggers, then handle ammo restoration accordingly.
 */
public final class LuckyShotSystem {

    private static final double DEFAULT_CHANCE_PER_LEVEL_PCT = 10.0;

    private final ComponentType<EntityStore, RpgStats> rpgStatsType;
    private final RpgStatsConfig config;
    private final RpgStatsPlugin plugin;
    private final Random random;

    public LuckyShotSystem(ComponentType<EntityStore, RpgStats> rpgStatsType,
                           RpgStatsConfig config,
                           RpgStatsPlugin plugin) {
        this.rpgStatsType = rpgStatsType;
        this.config = config;
        this.plugin = plugin;
        this.random = new Random();

        if (plugin != null) {
            plugin.logInfo("[LuckyShot] System initialized - awaiting proper event hook for projectile/ammo events");
        }
    }

    /**
     * Checks if Lucky Shot triggers for a player.
     * Call this when a player fires a bow/crossbow.
     *
     * @param ref The player's entity reference
     * @param store The entity store
     * @param player The player firing the weapon
     * @return true if Lucky Shot triggered (ammo should NOT be consumed), false otherwise
     */
    public boolean tryLuckyShot(Ref<EntityStore> ref, Store<EntityStore> store, Player player) {
        if (ref == null || !ref.isValid() || store == null || player == null) {
            logDebug("tryLuckyShot: invalid parameters");
            return false;
        }

        RpgStats stats = store.getComponent(ref, rpgStatsType);
        if (stats == null) {
            logDebug("tryLuckyShot: player has no RPG stats");
            return false;
        }

        int level = stats.getLuckyShotLevel();
        logDebug("tryLuckyShot: player=" + player.getDisplayName() + " level=" + level);

        if (level <= 0) {
            logDebug("tryLuckyShot: Lucky Shot not learned");
            return false;
        }

        // Calculate chance
        double chancePerLevel = config == null ? DEFAULT_CHANCE_PER_LEVEL_PCT : config.getLuckyShotChancePerLevelPct();
        double totalChance = Math.min(100.0, Math.max(0.0, chancePerLevel * level));

        logDebug("tryLuckyShot: chance=" + totalChance + "% (level " + level + " x " + chancePerLevel + "%)");

        // Roll for Lucky Shot
        double roll = random.nextDouble() * 100.0;
        logDebug("tryLuckyShot: roll=" + roll + " vs " + totalChance);

        if (roll < totalChance) {
            logDebug("tryLuckyShot: TRIGGERED for " + player.getDisplayName());
            player.sendMessage(Message.raw("Lucky shot! Ammo conserved."));
            return true;
        }

        logDebug("tryLuckyShot: did not trigger");
        return false;
    }

    private void logDebug(String message) {
        if (plugin != null) {
            plugin.logDebug("[LuckyShot] " + message);
        }
    }

    /**
     * Gets the Lucky Shot chance for a given level.
     * @param level The Lucky Shot ability level (0-3)
     * @param config The config to read the per-level percentage from
     * @return The total chance as a percentage (0-100)
     */
    public static float getLuckyShotChance(int level, RpgStatsConfig config) {
        int clampedLevel = Math.max(0, level);
        double perLevelPct = config == null ? DEFAULT_CHANCE_PER_LEVEL_PCT : config.getLuckyShotChancePerLevelPct();
        return (float) Math.min(100.0, Math.max(0.0, perLevelPct * clampedLevel));
    }

    public ComponentType<EntityStore, RpgStats> getRpgStatsType() {
        return rpgStatsType;
    }

    public RpgStatsConfig getConfig() {
        return config;
    }
}
