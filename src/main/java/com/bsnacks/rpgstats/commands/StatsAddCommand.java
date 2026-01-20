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
import com.hypixel.hytale.server.core.command.system.CommandUtil;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public final class StatsAddCommand extends CommandBase {

    private final RpgStatsPlugin plugin;
    private final ComponentType<EntityStore, RpgStats> rpgStatsType;
    private final RequiredArg<String> attributeArg;
    private final RpgStatsConfig config;

    public StatsAddCommand(RpgStatsPlugin plugin, ComponentType<EntityStore, RpgStats> rpgStatsType,
                           RpgStatsConfig config) {
        super("add", "Spend a stat point to increase an attribute.");
        setPermissionGroup(GameMode.Adventure);
        requirePermission(RpgStatsPermissions.STATS_ADD);
        this.plugin = plugin;
        this.rpgStatsType = rpgStatsType;
        this.config = config;
        attributeArg = withRequiredArg("attribute", "str, dex, con, int, end, cha", ArgTypes.STRING);
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void executeSync(CommandContext ctx) {
        CommandUtil.requirePermission(ctx.sender(), RpgStatsPermissions.STATS_ADD);
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("You must be a player to use this command."));
            return;
        }

        String attributeRaw = attributeArg.get(ctx);
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

        Player player = ctx.senderAs(Player.class);
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

            RpgStats stats = worldStore.ensureAndGetComponent(selfRef, rpgStatsType);
            stats.migrateIfNeeded();

            if (stats.getAvailableStatPoints() <= 0) {
                ctx.sendMessage(Message.raw("You do not have any stat points to spend."));
                return;
            }

            String attribute = normalizeAttribute(attributeRaw);
            if (attribute == null) {
                ctx.sendMessage(Message.raw("Unknown attribute '" + attributeRaw + "'. Try: str, dex, con, int, end, cha."));
                return;
            }
            int cap = getStatCap(attribute);
            int current = getStatValue(stats, attribute);
            if (current >= cap) {
                ctx.sendMessage(Message.raw(attribute.toUpperCase() + " cap for server is set to " + cap + "."));
                return;
            }

            if (!stats.spendStatPoint(attribute)) {
                ctx.sendMessage(Message.raw("Unknown attribute '" + attributeRaw + "'. Try: str, dex, con, int, end, cha."));
                return;
            }

            EntityStatMap statMap = worldStore.ensureAndGetComponent(selfRef, EntityStatMap.getComponentType());
            ConstitutionHealthEffect.apply(statMap, stats, config);
            IntellectManaEffect.apply(statMap, stats, config);
            EnduranceStaminaEffect.apply(statMap, stats, config);

            ctx.sendMessage(Message.raw("Added 1 point to " + attributeRaw.toUpperCase()
                    + ". Remaining points: " + stats.getAvailableStatPoints() + "."));
            plugin.logInfo("Player spent a point on " + attributeRaw + ": " + player.getDisplayName());
        });
    }

    private String normalizeAttribute(String attributeRaw) {
        if (attributeRaw == null) {
            return null;
        }
        String attribute = attributeRaw.trim().toLowerCase();
        switch (attribute) {
            case "str":
            case "dex":
            case "con":
            case "end":
            case "cha":
                return attribute;
            case "endurance":
                return "end";
            case "int":
            case "intl":
                return "int";
            default:
                return null;
        }
    }

    private int getStatCap(String attribute) {
        if (config == null) {
            return 25;
        }
        return config.getStatCap(attribute);
    }

    private int getStatValue(RpgStats stats, String attribute) {
        switch (attribute) {
            case "str":
                return stats.getStr();
            case "dex":
                return stats.getDex();
            case "con":
                return stats.getCon();
            case "int":
                return stats.getIntl();
            case "end":
                return stats.getEnd();
            case "cha":
                return stats.getCha();
            default:
                return 0;
        }
    }
}
