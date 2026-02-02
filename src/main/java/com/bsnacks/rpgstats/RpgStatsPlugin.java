package com.bsnacks.rpgstats;

import com.bsnacks.rpgstats.commands.StatsCommand;
import com.bsnacks.rpgstats.components.NpcLevelData;
import com.bsnacks.rpgstats.components.PartyMemberComponent;
import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.NpcLevelingConfig;
import com.bsnacks.rpgstats.config.RpgStatsConfig;
import com.bsnacks.rpgstats.services.NpcLevelCalculator;
import com.bsnacks.rpgstats.logging.RpgStatsFileLogger;
import com.bsnacks.rpgstats.listeners.PlayerListeners;
import com.bsnacks.rpgstats.listeners.PartyDisconnectListener;
import com.bsnacks.rpgstats.party.PartyService;
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
import com.bsnacks.rpgstats.systems.GourmandSystem;
import com.bsnacks.rpgstats.systems.FlameTouchSystem;
import com.bsnacks.rpgstats.systems.DamageDebugSystem;
import com.bsnacks.rpgstats.systems.ThornsSystem;
import com.bsnacks.rpgstats.systems.ToolProficiencySystem;
import com.bsnacks.rpgstats.systems.LuckyMinerSystem;
import com.bsnacks.rpgstats.systems.MiningExperienceSystem;
import com.bsnacks.rpgstats.systems.HudRefreshSystem;
import com.bsnacks.rpgstats.systems.PartyHudRefreshSystem;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
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
    private ComponentType<EntityStore, com.bsnacks.rpgstats.components.FlameTouchAttribution> flameTouchAttributionType;
    private ComponentType<EntityStore, NpcLevelData> npcLevelDataType;
    private ComponentType<EntityStore, PartyMemberComponent> partyMemberType;
    private RpgStatsFileLogger fileLogger;
    private RpgStatsConfig config;
    private NpcLevelingConfig npcLevelingConfig;
    private NpcLevelCalculator npcLevelCalculator;
    private Path configPath;
    private HudRefreshSystem hudRefreshSystem;
    private PartyHudRefreshSystem partyHudRefreshSystem;
    private PartyService partyService;

    public RpgStatsPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        String pluginVersion = getManifest().getVersion() != null
                ? getManifest().getVersion().toString() : "unknown";
        fileLogger = new RpgStatsFileLogger(getDataDirectory(), getLogger(), pluginVersion);
        logInfo("Diagnostics log: " + fileLogger.getLogFile());
        logInfo("Permission root: " + RpgStatsPermissions.ROOT);
        configPath = RpgStatsConfig.resolveConfigPath(getDataDirectory());
        logInfo("Config path: " + configPath.toAbsolutePath());
        reloadConfig("startup");

        // Register the component so it can be stored on players and persisted between sessions.
        rpgStatsType = getEntityStoreRegistry().registerComponent(RpgStats.class, RpgStats.COMPONENT_ID, RpgStats.CODEC);
        logInfo("Registered RPG stats component id: " + RpgStats.COMPONENT_ID);
        flameTouchAttributionType = getEntityStoreRegistry().registerComponent(
                com.bsnacks.rpgstats.components.FlameTouchAttribution.class,
                "rpgstats:flame_touch_attribution",
                com.bsnacks.rpgstats.components.FlameTouchAttribution.CODEC);
        logInfo("Registered Flame Touch attribution component");

        // NPC Level Data component for caching NPC levels
        npcLevelDataType = getEntityStoreRegistry().registerComponent(
                NpcLevelData.class,
                "rpgstats:npc_level_data",
                NpcLevelData.CODEC);
        logInfo("Registered NPC level data component");

        partyMemberType = getEntityStoreRegistry().registerComponent(
                PartyMemberComponent.class,
                PartyMemberComponent.COMPONENT_ID,
                PartyMemberComponent.CODEC);
        logInfo("Registered party member component");
        partyService = new PartyService(this, partyMemberType);
        applyPartyConfigToService();

        // Initialize NPC leveling system
        npcLevelingConfig = NpcLevelingConfig.load(getDataDirectory(), getLogger());
        npcLevelCalculator = new NpcLevelCalculator(npcLevelingConfig, this);
        logInfo("NPC leveling initialized: enabled=" + npcLevelingConfig.isEnabled()
                + " zones=" + npcLevelingConfig.getZoneCount()
                + " overrides=" + npcLevelingConfig.getEntityOverrideCount());

        //Register listeners + commands
        PlayerListeners listeners = new PlayerListeners(this, rpgStatsType, config, partyService);
        PartyDisconnectListener partyDisconnectListener = new PartyDisconnectListener(partyService);
        getEventRegistry().registerGlobal(PlayerReadyEvent.class, listeners::onPlayerReady);
        getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, partyDisconnectListener::onPlayerDisconnect);
        getCommandRegistry().registerCommand(new StatsCommand(this, rpgStatsType, config, partyService));
        getCommandRegistry().registerCommand(new com.bsnacks.rpgstats.commands.StatsPartyCommand(this, partyService));
        // Core systems
        getEntityStoreRegistry().registerSystem(new StrengthDamageSystem(rpgStatsType, config));
        getEntityStoreRegistry().registerSystem(new DexterityMiningSpeedSystem(rpgStatsType, config));
        getEntityStoreRegistry().registerSystem(new ExperienceOnKillSystem(rpgStatsType, flameTouchAttributionType, fileLogger, config, this));
        getEntityStoreRegistry().registerSystem(new ArmorProficiencySystem(rpgStatsType, config));
        getEntityStoreRegistry().registerSystem(new GlancingBlowSystem(rpgStatsType, config));
        getEntityStoreRegistry().registerSystem(new AbilityRegenSystem(rpgStatsType, config));
        getEntityStoreRegistry().registerSystem(new CriticalStrikeSystem(rpgStatsType, config));
        getEntityStoreRegistry().registerSystem(new LifestealSystem(rpgStatsType, config));
        getEntityStoreRegistry().registerSystem(new GourmandSystem(rpgStatsType, config, this));
        // FlameTouchSystem - uses Filter group damage modification + Burn effect
        getEntityStoreRegistry().registerSystem(new FlameTouchSystem(rpgStatsType, flameTouchAttributionType, config, this));
        getEntityStoreRegistry().registerSystem(new ThornsSystem(rpgStatsType, config));
        getEntityStoreRegistry().registerSystem(new ToolProficiencySystem(rpgStatsType, config, this));
        getEntityStoreRegistry().registerSystem(new LuckyMinerSystem(rpgStatsType, config, this));
        getEntityStoreRegistry().registerSystem(new MiningExperienceSystem(rpgStatsType, config, this));
        // Crafting XP - disabled until Hytale provides proper crafting events for bench crafting
        // The current API only fires events for instant crafts, not bench crafting with time
        hudRefreshSystem = new HudRefreshSystem(rpgStatsType, this);
        getEntityStoreRegistry().registerSystem(hudRefreshSystem);
        partyHudRefreshSystem = new PartyHudRefreshSystem(rpgStatsType, config);
        getEntityStoreRegistry().registerSystem(partyHudRefreshSystem);

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
        RpgStats.setMaxAbilityLevel(config.getMaxAbilityLevel());
        logInfo("Config reloaded (" + reason + "): xp_multiplier=" + config.getXpMultiplier()
                + " max_level=" + config.getMaxLevel()
                + " ability_points_per_level=" + config.getAbilityPointsPerLevel()
                + " max_ability_level=" + config.getMaxAbilityLevel()
                + " light_foot_speed_per_level_pct=" + config.getLightFootSpeedPerLevelPct()
                + " armor_proficiency_resistance_per_level_pct=" + config.getArmorProficiencyResistancePerLevelPct()
                + " health_regen_per_level_per_sec=" + config.getHealthRegenPerLevelPerSec()
                + " stamina_regen_per_level_per_sec=" + config.getStaminaRegenPerLevelPerSec()
                + " glancing_blow_chance_per_level_pct=" + config.getGlancingBlowChancePerLevelPct()
                + " lucky_shot_chance_per_level_pct=" + config.getLuckyShotChancePerLevelPct()
                + " gourmand_food_bonus_per_level_pct=" + config.getGourmandFoodBonusPerLevelPct()
                + " ability_rank_costs=" + config.getAbilityRank1Cost() + "/" + config.getAbilityRank2Cost() + "/" + config.getAbilityRank3Cost()
                + " hud_enabled=" + config.isHudEnabled()
                + " xp_chat_messages_enabled=" + config.isXpChatMessagesEnabled()
                + " party_enabled=" + config.isPartyEnabled()
                + " party_max_size=" + config.getPartyMaxSize()
                + " party_invite_timeout_sec=" + config.getPartyInviteTimeoutSec()
                + " party_xp_share_mode=" + config.getPartyXpShareMode()
                + " party_xp_share_radius_blocks=" + config.getPartyXpShareRadiusBlocks()
                + " party_hud_enabled=" + config.isPartyHudEnabled()
                + " party_hud_offset=" + config.getPartyHudOffsetX() + "," + config.getPartyHudOffsetY()
                + " party_hud_refresh_ticks=" + config.getPartyHudRefreshTicks()
                + " xp_blacklist_npc_types=" + config.getXpBlacklistNpcTypes().size()
                + " xp_blacklist_roles=" + config.getXpBlacklistRoles().size()
                + " mining_xp_entries=" + config.getMiningXpEntryCount()
                + " crafting_xp_entries=" + config.getCraftingXpEntryCount());
        logDebug("XP blacklist loaded: npc_types=" + config.getXpBlacklistNpcTypes().size()
                + " roles=" + config.getXpBlacklistRoles().size());
        applyPartyConfigToService();
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

    public void scheduleHudRefresh(Player player, String reason) {
        if (hudRefreshSystem != null && player != null) {
            hudRefreshSystem.schedule(player, reason);
        }
    }

    private void applyPartyConfigToService() {
        if (partyService == null || config == null) {
            return;
        }
        partyService.setMaxPartySize(config.getPartyMaxSize());
        partyService.setInviteTimeoutMs(config.getPartyInviteTimeoutSec() * 1000L);
    }

    /**
     * Gets the NPC level calculator service.
     */
    public NpcLevelCalculator getNpcLevelCalculator() {
        return npcLevelCalculator;
    }

    /**
     * Gets the NPC leveling configuration.
     */
    public NpcLevelingConfig getNpcLevelingConfig() {
        return npcLevelingConfig;
    }

    /**
     * Gets the NPC level data component type.
     */
    public ComponentType<EntityStore, NpcLevelData> getNpcLevelDataType() {
        return npcLevelDataType;
    }

    /**
     * Gets the party service.
     */
    public PartyService getPartyService() {
        return partyService;
    }

}
