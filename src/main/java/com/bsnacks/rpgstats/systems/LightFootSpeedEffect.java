package com.bsnacks.rpgstats.systems;

import com.bsnacks.rpgstats.RpgStatsPlugin;
import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.MovementSettings;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public final class LightFootSpeedEffect {

    private static final double DEFAULT_SPEED_PER_LEVEL_PCT = 5.0;

    private LightFootSpeedEffect() {
    }

    public static void apply(Ref<EntityStore> ref, Store<EntityStore> store, Player player,
                             RpgStats stats, RpgStatsConfig config, RpgStatsPlugin plugin) {
        if (ref == null || store == null || player == null || stats == null) {
            return;
        }

        MovementManager movementManager = store.ensureAndGetComponent(ref, MovementManager.getComponentType());
        MovementSettings defaults = movementManager.getDefaultSettings();
        if (defaults == null) {
            movementManager.refreshDefaultSettings(ref, store);
            defaults = movementManager.getDefaultSettings();
        }
        if (defaults == null) {
            if (plugin != null) {
                plugin.logDebug("Light foot skipped: missing movement defaults for " + player.getDisplayName());
            }
            return;
        }

        MovementSettings settings = movementManager.getSettings();
        if (settings == null) {
            movementManager.applyDefaultSettings();
            settings = movementManager.getSettings();
        }
        if (settings == null) {
            if (plugin != null) {
                plugin.logDebug("Light foot skipped: missing movement settings for " + player.getDisplayName());
            }
            return;
        }

        float multiplier = 1.0f + getSpeedBonus(stats.getLightFootLevel(), config);
        settings.baseSpeed = defaults.baseSpeed * multiplier;
        settings.climbSpeed = defaults.climbSpeed * multiplier;
        settings.climbSpeedLateral = defaults.climbSpeedLateral * multiplier;
        settings.climbUpSprintSpeed = defaults.climbUpSprintSpeed * multiplier;
        settings.climbDownSprintSpeed = defaults.climbDownSprintSpeed * multiplier;
        settings.horizontalFlySpeed = defaults.horizontalFlySpeed * multiplier;
        settings.verticalFlySpeed = defaults.verticalFlySpeed * multiplier;

        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef != null) {
            movementManager.update(playerRef.getPacketHandler());
        }
    }

    public static float getSpeedBonus(int level, RpgStatsConfig config) {
        int clampedLevel = Math.max(0, level);
        double perLevelPct = config == null ? DEFAULT_SPEED_PER_LEVEL_PCT : config.getLightFootSpeedPerLevelPct();
        if (perLevelPct < 0.0) {
            perLevelPct = 0.0;
        } else if (perLevelPct > 100.0) {
            perLevelPct = 100.0;
        }
        double bonus = (perLevelPct / 100.0) * clampedLevel;
        return (float) bonus;
    }
}
