package com.bsnacks.rpgstats.commands;

import com.bsnacks.rpgstats.RpgStatsPlugin;
import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;
import com.bsnacks.rpgstats.permissions.RpgStatsPermissions;
import com.bsnacks.rpgstats.systems.ConstitutionHealthEffect;
import com.bsnacks.rpgstats.systems.EnduranceStaminaEffect;
import com.bsnacks.rpgstats.systems.IntellectManaEffect;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandUtil;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public final class StatsResetCommand extends CommandBase {

    private final RpgStatsPlugin plugin;
    private final ComponentType<EntityStore, RpgStats> rpgStatsType;
    private final RequiredArg<String> targetArg;
    private final RpgStatsConfig config;

    public StatsResetCommand(RpgStatsPlugin plugin, ComponentType<EntityStore, RpgStats> rpgStatsType,
                             RpgStatsConfig config) {
        super("reset", "Reset a player's RPG stats.");
        setPermissionGroup(GameMode.Adventure);
        requirePermission(RpgStatsPermissions.STATS_RESET);
        this.plugin = plugin;
        this.rpgStatsType = rpgStatsType;
        this.config = config;
        targetArg = withRequiredArg("target", "self or player name", ArgTypes.STRING);
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void executeSync(CommandContext ctx) {
        CommandUtil.requirePermission(ctx.sender(), RpgStatsPermissions.STATS_RESET);
        String targetRaw = targetArg.get(ctx);
        if (!"self".equalsIgnoreCase(targetRaw)) {
            CommandUtil.requirePermission(ctx.sender(), RpgStatsPermissions.STATS_RESET_OTHERS);
        }
        Target target = resolveTarget(ctx, targetRaw);
        if (target == null) {
            return;
        }

        Store<EntityStore> store = target.ref.getStore();
        if (store == null) {
            ctx.sendMessage(Message.raw("That player is not available right now."));
            return;
        }
        EntityStore entityStore = store.getExternalData();
        if (entityStore == null) {
            ctx.sendMessage(Message.raw("That player is not available right now."));
            return;
        }
        World world = entityStore.getWorld();
        if (world == null) {
            ctx.sendMessage(Message.raw("That player is not in a world right now."));
            return;
        }

        world.execute(() -> {
            if (!target.ref.isValid()) {
                ctx.sendMessage(Message.raw("That player is not in the world right now."));
                return;
            }

            Store<EntityStore> worldStore = target.ref.getStore();
            if (worldStore == null) {
                ctx.sendMessage(Message.raw("That player is not available right now."));
                return;
            }

            RpgStats stats = worldStore.ensureAndGetComponent(target.ref, rpgStatsType);
            stats.resetToDefaults();
            EntityStatMap statMap = worldStore.ensureAndGetComponent(target.ref, EntityStatMap.getComponentType());
            ConstitutionHealthEffect.apply(statMap, stats, config);
            IntellectManaEffect.apply(statMap, stats, config);
            EnduranceStaminaEffect.apply(statMap, stats, config);

            ctx.sendMessage(Message.raw("Reset stats for " + target.name + "."));
            plugin.logInfo("Reset stats for " + target.name);
        });
    }

    private Target resolveTarget(CommandContext ctx, String targetRaw) {
        if ("self".equalsIgnoreCase(targetRaw)) {
            if (!ctx.isPlayer()) {
                ctx.sendMessage(Message.raw("You must be a player to target self."));
                return null;
            }
            Ref<EntityStore> selfRef = ctx.senderAsPlayerRef();
            if (selfRef == null || !selfRef.isValid()) {
                ctx.sendMessage(Message.raw("You are not in the world right now."));
                return null;
            }
            Player sender = ctx.senderAs(Player.class);
            return new Target(selfRef, sender.getDisplayName());
        }

        PlayerRef target = Universe.get().getPlayerByUsername(targetRaw, NameMatching.EXACT_IGNORE_CASE);
        if (target == null || !target.isValid()) {
            ctx.sendMessage(Message.raw("Player not found: " + targetRaw));
            return null;
        }
        return new Target(target.getReference(), target.getUsername());
    }

    private static final class Target {
        private final Ref<EntityStore> ref;
        private final String name;

        private Target(Ref<EntityStore> ref, String name) {
            this.ref = ref;
            this.name = name;
        }
    }
}
