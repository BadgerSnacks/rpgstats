package com.bsnacks.rpgstats.listeners;

import com.bsnacks.rpgstats.RpgStatsPlugin;
import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;
import com.bsnacks.rpgstats.systems.ConstitutionHealthEffect;
import com.bsnacks.rpgstats.systems.IntellectManaEffect;
import com.bsnacks.rpgstats.systems.EnduranceStaminaEffect;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public final class PlayerListeners {

    private final RpgStatsPlugin plugin;
    private final ComponentType<EntityStore, RpgStats> rpgStatsType;
    private final RpgStatsConfig config;

    public PlayerListeners(RpgStatsPlugin plugin, ComponentType<EntityStore, RpgStats> rpgStatsType,
                           RpgStatsConfig config) {
        this.plugin = plugin;
        this.rpgStatsType = rpgStatsType;
        this.config = config;
    }

    public void onPlayerReady(PlayerReadyEvent event) {
        // The holder is the player entity storage container.
        var holder = event.getPlayer().toHolder();
        RpgStats stats = holder.ensureAndGetComponent(rpgStatsType);
        stats.migrateIfNeeded();
        EntityStatMap statMap = holder.ensureAndGetComponent(EntityStatMap.getComponentType());
        ConstitutionHealthEffect.apply(statMap, stats, config);
        IntellectManaEffect.apply(statMap, stats, config);
        EnduranceStaminaEffect.apply(statMap, stats, config);

        plugin.logInfo("Loaded stats for player: " + event.getPlayer().getDisplayName() + " | Level=" + stats.getLevel() + " XP=" + stats.getXp());
    }
}
