package com.bsnacks.rpgstats.listeners;

import com.bsnacks.rpgstats.RpgStatsPlugin;
import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;
import com.bsnacks.rpgstats.systems.ConstitutionHealthEffect;
import com.bsnacks.rpgstats.systems.IntellectManaEffect;
import com.bsnacks.rpgstats.systems.EnduranceStaminaEffect;
import com.bsnacks.rpgstats.systems.LightFootSpeedEffect;
import com.bsnacks.rpgstats.ui.RpgStatsHud;
import com.bsnacks.rpgstats.utils.HudHelper;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.PlayerRef;
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
        Player player = event.getPlayer();
        // The holder is the player entity storage container.
        var holder = player.toHolder();
        RpgStats stats = holder.getComponent(rpgStatsType);
        boolean created = false;
        if (stats == null) {
            stats = holder.ensureAndGetComponent(rpgStatsType);
            created = true;
        }
        stats.migrateIfNeeded();
        EntityStatMap statMap = holder.ensureAndGetComponent(EntityStatMap.getComponentType());
        ConstitutionHealthEffect.apply(statMap, stats, config);
        IntellectManaEffect.apply(statMap, stats, config);
        EnduranceStaminaEffect.apply(statMap, stats, config);
        applyAbilityEffects(player, stats);

        plugin.logInfo((created ? "Created" : "Loaded") + " stats for player: "
                + player.getDisplayName() + " | Level=" + stats.getLevel() + " XP=" + stats.getXp());
        initializeHud(player, stats);
        plugin.scheduleHudRefresh(player, "player_ready");
    }

    private void initializeHud(Player player, RpgStats stats) {
        if (player == null) {
            return;
        }
        var holder = player.toHolder();
        PlayerRef playerRef = holder.getComponent(PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }
        if (config != null && !config.isHudEnabled()) {
            HudHelper.hideCustomHud(player, playerRef);
            plugin.logDebug("RPG stats HUD disabled by config for player: " + player.getDisplayName());
            return;
        }
        var existingHud = HudHelper.getCustomHud(player, playerRef);
        if (!(existingHud instanceof RpgStatsHud)) {
            if (existingHud != null && !HudHelper.isMultipleHudAvailable()) {
                plugin.logDebug("Replacing custom HUD for player: " + player.getDisplayName());
            }
            HudHelper.setCustomHud(player, playerRef, new RpgStatsHud(playerRef, rpgStatsType));
            plugin.logDebug("RPG stats HUD enabled for player: " + player.getDisplayName());
        }
        if (stats != null) {
            RpgStatsHud.refreshIfActive(player, stats);
        }
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        if (store == null) {
            return;
        }
        RpgStatsHud.refreshIfActive(player, ref, store);
    }

    private void applyAbilityEffects(Player player, RpgStats stats) {
        if (player == null) {
            return;
        }
        var holder = player.toHolder();
        PlayerRef playerRef = holder.getComponent(PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        if (store == null) {
            return;
        }
        LightFootSpeedEffect.apply(ref, store, player, stats, config, plugin);
    }
}
