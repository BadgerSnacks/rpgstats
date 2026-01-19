package com.bsnacks.rpgstats;

import com.bsnacks.rpgstats.commands.StatsCommand;
import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;
import com.bsnacks.rpgstats.logging.RpgStatsFileLogger;
import com.bsnacks.rpgstats.listeners.PlayerListeners;
import com.bsnacks.rpgstats.systems.DexterityMiningSpeedSystem;
import com.bsnacks.rpgstats.systems.ExperienceOnKillSystem;
import com.bsnacks.rpgstats.systems.StrengthDamageSystem;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public final class RpgStatsPlugin extends JavaPlugin {

    private ComponentType<EntityStore, RpgStats> rpgStatsType;
    private RpgStatsFileLogger fileLogger;
    private RpgStatsConfig config;

    public RpgStatsPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        fileLogger = new RpgStatsFileLogger(getDataDirectory(), getLogger());
        logInfo("Diagnostics log: " + fileLogger.getLogFile());
        config = RpgStatsConfig.load(getDataDirectory(), getLogger());
        logInfo("XP multiplier: " + config.getXpMultiplier());
        RpgStats.setMaxLevel(config.getMaxLevel());
        logInfo("Max level: " + RpgStats.getMaxLevel());

        //Register the component so  it can be stored on players
        rpgStatsType = getEntityStoreRegistry().registerComponent(RpgStats.class, RpgStats::new);

        //Register listeners + commands
        PlayerListeners listeners = new PlayerListeners(this, rpgStatsType, config);
        getEventRegistry().registerGlobal(PlayerReadyEvent.class, listeners::onPlayerReady);
        getCommandRegistry().registerCommand(new StatsCommand(this, rpgStatsType, config));
        getEntityStoreRegistry().registerSystem(new StrengthDamageSystem(rpgStatsType, config));
        getEntityStoreRegistry().registerSystem(new DexterityMiningSpeedSystem(rpgStatsType, config));
        getEntityStoreRegistry().registerSystem(new ExperienceOnKillSystem(rpgStatsType, fileLogger, config));
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
