package com.bsnacks.rpgstats;

import com.bsnacks.rpgstats.commands.StatsCommand;
import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;
import com.bsnacks.rpgstats.logging.RpgStatsFileLogger;
import com.bsnacks.rpgstats.listeners.PlayerListeners;
import com.bsnacks.rpgstats.systems.DexterityMiningSpeedSystem;
import com.bsnacks.rpgstats.systems.ExperienceOnKillSystem;
import com.bsnacks.rpgstats.systems.StrengthDamageSystem;
import com.bsnacks.rpgstats.systems.AbilityRegenSystem;
import com.bsnacks.rpgstats.systems.ConstitutionHealthEffect;
import com.bsnacks.rpgstats.systems.EnduranceStaminaEffect;
import com.bsnacks.rpgstats.systems.IntellectManaEffect;
import com.bsnacks.rpgstats.systems.StrongLungsOxygenEffect;
import com.bsnacks.rpgstats.permissions.RpgStatsPermissions;
import com.bsnacks.rpgstats.systems.LightFootSpeedEffect;
import com.bsnacks.rpgstats.systems.ArmorProficiencySystem;
import com.bsnacks.rpgstats.systems.GlancingBlowSystem;
import com.bsnacks.rpgstats.systems.LuckyShotSystem;
import com.bsnacks.rpgstats.systems.CriticalStrikeSystem;
import com.bsnacks.rpgstats.systems.LifestealSystem;
import com.bsnacks.rpgstats.systems.ThornsSystem;
import com.bsnacks.rpgstats.systems.ToolProficiencySystem;
import com.bsnacks.rpgstats.systems.LuckyMinerSystem;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.entity.entities.Player;
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
        getEntityStoreRegistry().registerSystem(new ArmorProficiencySystem(rpgStatsType, config));
        getEntityStoreRegistry().registerSystem(new GlancingBlowSystem(rpgStatsType, config));
        getEntityStoreRegistry().registerSystem(new AbilityRegenSystem(rpgStatsType, config));
        getEntityStoreRegistry().registerSystem(new CriticalStrikeSystem(rpgStatsType, config));
        getEntityStoreRegistry().registerSystem(new LifestealSystem(rpgStatsType, config));
        getEntityStoreRegistry().registerSystem(new ThornsSystem(rpgStatsType, config));
        getEntityStoreRegistry().registerSystem(new ToolProficiencySystem(rpgStatsType, config, this));
        getEntityStoreRegistry().registerSystem(new LuckyMinerSystem(rpgStatsType, config, this));

        // Lucky Shot utility - ability tracking works, effect trigger needs proper Hytale API hook
        // The tryLuckyShot() method is ready to be called when the correct event is identified
        new LuckyShotSystem(rpgStatsType, config, this);
    }

    public void reloadConfig(String reason) {
        RpgStatsConfig loaded = RpgStatsConfig.load(getDataDirectory(), getLogger());
        if (config == null) {
            config = loaded;
        } else {
            config.applyFrom(loaded);
        }
        RpgStats.setMaxLevel(config.getMaxLevel());
        RpgStats.setAbilityPointsPerLevel(config.getAbilityPointsPerLevel());
        RpgStats.setAbilityRankCosts(config.getAbilityRank1Cost(), config.getAbilityRank2Cost(), config.getAbilityRank3Cost());
        logInfo("Config reloaded (" + reason + "): xp_multiplier=" + config.getXpMultiplier()
                + " max_level=" + config.getMaxLevel()
                + " ability_points_per_level=" + config.getAbilityPointsPerLevel()
                + " light_foot_speed_per_level_pct=" + config.getLightFootSpeedPerLevelPct()
                + " armor_proficiency_resistance_per_level_pct=" + config.getArmorProficiencyResistancePerLevelPct()
                + " health_regen_per_level_per_sec=" + config.getHealthRegenPerLevelPerSec()
                + " stamina_regen_per_level_per_sec=" + config.getStaminaRegenPerLevelPerSec()
                + " glancing_blow_chance_per_level_pct=" + config.getGlancingBlowChancePerLevelPct()
                + " lucky_shot_chance_per_level_pct=" + config.getLuckyShotChancePerLevelPct()
                + " ability_rank_costs=" + config.getAbilityRank1Cost() + "/" + config.getAbilityRank2Cost() + "/" + config.getAbilityRank3Cost()
                + " hud_enabled=" + config.isHudEnabled()
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
        StrongLungsOxygenEffect.apply(statMap, stats, config);
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player != null) {
            LightFootSpeedEffect.apply(ref, store, player, stats, config, this);
        }
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
