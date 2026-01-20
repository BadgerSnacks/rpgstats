package com.bsnacks.rpgstats;

import com.bsnacks.rpgstats.commands.StatsCommand;
import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;
import com.bsnacks.rpgstats.logging.RpgStatsFileLogger;
import com.bsnacks.rpgstats.listeners.PlayerListeners;
import com.bsnacks.rpgstats.systems.DexterityMiningSpeedSystem;
import com.bsnacks.rpgstats.systems.ExperienceOnKillSystem;
import com.bsnacks.rpgstats.systems.StrengthDamageSystem;
import com.bsnacks.rpgstats.systems.ConstitutionHealthEffect;
import com.bsnacks.rpgstats.systems.EnduranceStaminaEffect;
import com.bsnacks.rpgstats.systems.IntellectManaEffect;
import com.bsnacks.rpgstats.permissions.RpgStatsPermissions;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.List;

public final class RpgStatsPlugin extends JavaPlugin {

    private ComponentType<EntityStore, RpgStats> rpgStatsType;
    private RpgStatsFileLogger fileLogger;
    private RpgStatsConfig config;
    private Path configPath;

    public RpgStatsPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        fileLogger = new RpgStatsFileLogger(getDataDirectory(), getLogger());
        logInfo("Diagnostics log: " + fileLogger.getLogFile());
        logInfo("Permission root: " + RpgStatsPermissions.ROOT);
        configPath = RpgStatsConfig.resolveConfigPath(getDataDirectory());
        logInfo("Config path: " + configPath.toAbsolutePath());
        reloadConfig("startup");

        // Register the component so it can be stored on players and persisted between sessions.
        rpgStatsType = getEntityStoreRegistry().registerComponent(RpgStats.class, RpgStats.COMPONENT_ID, RpgStats.CODEC);
        logInfo("Registered RPG stats component id: " + RpgStats.COMPONENT_ID);

        //Register listeners + commands
        PlayerListeners listeners = new PlayerListeners(this, rpgStatsType, config);
        getEventRegistry().registerGlobal(PlayerReadyEvent.class, listeners::onPlayerReady);
        getCommandRegistry().registerCommand(new StatsCommand(this, rpgStatsType, config));
        getEntityStoreRegistry().registerSystem(new StrengthDamageSystem(rpgStatsType, config));
        getEntityStoreRegistry().registerSystem(new DexterityMiningSpeedSystem(rpgStatsType, config));
        getEntityStoreRegistry().registerSystem(new ExperienceOnKillSystem(rpgStatsType, fileLogger, config));
    }

    public void reloadConfig(String reason) {
        RpgStatsConfig loaded = RpgStatsConfig.load(getDataDirectory(), getLogger());
        if (config == null) {
            config = loaded;
        } else {
            config.applyFrom(loaded);
        }
        RpgStats.setMaxLevel(config.getMaxLevel());
        logInfo("Config reloaded (" + reason + "): xp_multiplier=" + config.getXpMultiplier()
                + " max_level=" + config.getMaxLevel()
                + " xp_blacklist_npc_types=" + config.getXpBlacklistNpcTypes().size()
                + " xp_blacklist_roles=" + config.getXpBlacklistRoles().size());
        logDebug("XP blacklist loaded: npc_types=" + config.getXpBlacklistNpcTypes().size()
                + " roles=" + config.getXpBlacklistRoles().size());
        if (rpgStatsType != null) {
            applyConfigToOnlinePlayers();
        }
    }

    private void applyConfigToOnlinePlayers() {
        Universe universe = Universe.get();
        if (universe == null) {
            return;
        }
        List<PlayerRef> players = universe.getPlayers();
        if (players == null || players.isEmpty()) {
            return;
        }
        for (PlayerRef playerRef : players) {
            if (playerRef == null || !playerRef.isValid()) {
                continue;
            }
            Ref<EntityStore> ref = playerRef.getReference();
            if (ref == null || !ref.isValid()) {
                continue;
            }
            Store<EntityStore> store = ref.getStore();
            if (store == null) {
                continue;
            }
            EntityStore entityStore = store.getExternalData();
            if (entityStore == null) {
                continue;
            }
            World world = entityStore.getWorld();
            if (world == null) {
                continue;
            }
            world.execute(() -> applyConfigToPlayer(ref));
        }
    }

    private void applyConfigToPlayer(Ref<EntityStore> ref) {
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        if (store == null) {
            return;
        }
        RpgStats stats = store.ensureAndGetComponent(ref, rpgStatsType);
        stats.migrateIfNeeded();
        stats.getLevel();
        EntityStatMap statMap = store.ensureAndGetComponent(ref, EntityStatMap.getComponentType());
        ConstitutionHealthEffect.apply(statMap, stats, config);
        IntellectManaEffect.apply(statMap, stats, config);
        EnduranceStaminaEffect.apply(statMap, stats, config);
    }

    public void logInfo(String msg) {
        getLogger().atInfo().log("[RPGStats] " + msg);
    }

    public void logDebug(String msg) {
        if (fileLogger != null) {
            fileLogger.log(msg);
        }
    }

}
