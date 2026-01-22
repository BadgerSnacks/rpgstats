package com.bsnacks.rpgstats.commands;

import com.bsnacks.rpgstats.RpgStatsPlugin;
import com.bsnacks.rpgstats.permissions.PermissionChecks;
import com.bsnacks.rpgstats.permissions.RpgStatsPermissions;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;

public final class StatsReloadCommand extends CommandBase {

    private final RpgStatsPlugin plugin;

    public StatsReloadCommand(RpgStatsPlugin plugin) {
        super("reload", "Reload the RPG stats configuration.");
        setPermissionGroup(GameMode.Adventure);
        requirePermission(RpgStatsPermissions.STATS_SET);
        this.plugin = plugin;
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void executeSync(CommandContext ctx) {
        if (!PermissionChecks.requirePrivileged(ctx, RpgStatsPermissions.STATS_SET)) {
            plugin.logDebug("Denied /stats reload: sender=" + ctx.sender().getDisplayName()
                    + " uuid=" + ctx.sender().getUuid());
            return;
        }
        String actor = "console";
        if (ctx.isPlayer()) {
            Player player = ctx.senderAs(Player.class);
            actor = player.getDisplayName();
        }
        plugin.reloadConfig("command by " + actor);
        ctx.sendMessage(Message.raw("RPGStats config reloaded."));
    }
}
