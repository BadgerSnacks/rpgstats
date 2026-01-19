package com.bsnacks.rpgstats.commands;

import com.bsnacks.rpgstats.RpgStatsPlugin;
import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;
import com.bsnacks.rpgstats.commands.StatsAddCommand;
import com.bsnacks.rpgstats.commands.StatsReloadCommand;
import com.bsnacks.rpgstats.commands.StatsResetCommand;
import com.bsnacks.rpgstats.commands.StatsSetCommand;
import com.bsnacks.rpgstats.permissions.RpgStatsPermissions;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandUtil;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public final class StatsCommand extends CommandBase {

    private final RpgStatsPlugin plugin;
    private final ComponentType<EntityStore, RpgStats> rpgStatsType;
    private final RpgStatsConfig config;

    public StatsCommand(RpgStatsPlugin plugin, ComponentType<EntityStore, RpgStats> rpgStatsType,
                        RpgStatsConfig config) {
        super("stats", "Show your RPG stats.");
        setPermissionGroup(GameMode.Adventure);
        requirePermission(RpgStatsPermissions.STATS_VIEW);
        this.plugin = plugin;
        this.rpgStatsType = rpgStatsType;
        this.config = config;
        addSubCommand(new StatsAddCommand(plugin, rpgStatsType, config));
        addSubCommand(new StatsSetCommand(plugin, rpgStatsType, config));
        addSubCommand(new StatsResetCommand(plugin, rpgStatsType, config));
        addSubCommand(new StatsReloadCommand(plugin));
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void executeSync(CommandContext ctx) {
        CommandUtil.requirePermission(ctx.sender(), RpgStatsPermissions.STATS_VIEW);
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("This command can only be used by a player."));
            return;
        }

        Player player = ctx.senderAs(Player.class);
        var holder = player.toHolder();
        RpgStats s = holder.ensureAndGetComponent(rpgStatsType);
        s.migrateIfNeeded();

        String msg = "Level " + s.getLevel() + " (XP " + s.getXp() + "), (Stat Points " + s.getAvailableStatPoints() + ")\n"
                + "STR " + s.getStr() + " (" + s.modifier(s.getStr()) + ")  "
                + "DEX " + s.getDex() + " (" + s.modifier(s.getDex()) + ")  "
                + "CON " + s.getCon() + " (" + s.modifier(s.getCon()) + ")\n"
                + "INT " + s.getIntl() + " (" + s.modifier(s.getIntl()) + ")  "
                + "END " + s.getEnd() + " (" + s.modifier(s.getEnd()) + ")  "
                + "CHA " + s.getCha() + " (" + s.modifier(s.getCha()) + ")";

        ctx.sendMessage(Message.raw(msg));
        plugin.logInfo("Player ran /stats: " + player.getDisplayName());
    }

}
