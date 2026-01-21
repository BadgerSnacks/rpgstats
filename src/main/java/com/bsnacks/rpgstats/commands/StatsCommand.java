package com.bsnacks.rpgstats.commands;

import com.bsnacks.rpgstats.RpgStatsPlugin;
import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;
import com.bsnacks.rpgstats.commands.StatsAddCommand;
import com.bsnacks.rpgstats.commands.StatsReloadCommand;
import com.bsnacks.rpgstats.commands.StatsResetCommand;
import com.bsnacks.rpgstats.commands.StatsSetCommand;
import com.bsnacks.rpgstats.permissions.RpgStatsPermissions;
import com.bsnacks.rpgstats.ui.StatsPage;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandUtil;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
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
        Ref<EntityStore> selfRef = ctx.senderAsPlayerRef();
        if (selfRef == null || !selfRef.isValid()) {
            ctx.sendMessage(Message.raw("You are not in the world right now."));
            return;
        }

        Store<EntityStore> store = selfRef.getStore();
        if (store == null) {
            ctx.sendMessage(Message.raw("You are not in the world right now."));
            return;
        }

        EntityStore entityStore = store.getExternalData();
        if (entityStore == null) {
            ctx.sendMessage(Message.raw("You are not in the world right now."));
            return;
        }

        World world = entityStore.getWorld();
        if (world == null) {
            ctx.sendMessage(Message.raw("You are not in the world right now."));
            return;
        }

        world.execute(() -> {
            if (!selfRef.isValid()) {
                ctx.sendMessage(Message.raw("You are not in the world right now."));
                return;
            }

            Store<EntityStore> worldStore = selfRef.getStore();
            if (worldStore == null) {
                ctx.sendMessage(Message.raw("You are not in the world right now."));
                return;
            }

            StatsPage page = new StatsPage(player.getPlayerRef(), rpgStatsType, config, plugin);
            player.getPageManager().openCustomPage(selfRef, worldStore, page);
            plugin.logInfo("Player opened stats UI: " + player.getDisplayName());
        });
    }

}
