package com.bsnacks.rpgstats.listeners;

import com.bsnacks.rpgstats.RpgStatsPlugin;
import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;
import com.bsnacks.rpgstats.systems.ConstitutionHealthEffect;
import com.bsnacks.rpgstats.systems.IntellectManaEffect;
import com.bsnacks.rpgstats.systems.EnduranceStaminaEffect;
import com.bsnacks.rpgstats.ui.RpgStatsHud;

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

        plugin.logInfo((created ? "Created" : "Loaded") + " stats for player: "
                + player.getDisplayName() + " | Level=" + stats.getLevel() + " XP=" + stats.getXp());
        initializeHud(player);
    }

    private void initializeHud(Player player) {
        if (player == null) {
            return;
        }
        PlayerRef playerRef = player.getPlayerRef();
        if (playerRef == null) {
            return;
        }
        var hudManager = player.getHudManager();
        if (config != null && !config.isHudEnabled()) {
            var existingHud = hudManager.getCustomHud();
            if (existingHud instanceof RpgStatsHud) {
                hudManager.setCustomHud(playerRef, null);
                plugin.logDebug("RPG stats HUD disabled by config for player: " + player.getDisplayName());
            }
            return;
        }
        var existingHud = hudManager.getCustomHud();
        if (!(existingHud instanceof RpgStatsHud)) {
            if (existingHud != null) {
                plugin.logDebug("Replacing custom HUD for player: " + player.getDisplayName());
            }
            hudManager.setCustomHud(playerRef, new RpgStatsHud(playerRef, rpgStatsType));
            plugin.logDebug("RPG stats HUD enabled for player: " + player.getDisplayName());
        }
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        if (store == null) {
            return;
        }
        RpgStatsHud.refreshIfActive(ref, store);
    }
}
