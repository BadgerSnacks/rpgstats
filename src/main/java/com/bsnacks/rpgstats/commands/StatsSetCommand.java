package com.bsnacks.rpgstats.commands;

import com.bsnacks.rpgstats.RpgStatsPlugin;
import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;
import com.bsnacks.rpgstats.permissions.PermissionChecks;
import com.bsnacks.rpgstats.permissions.RpgStatsPermissions;
import com.bsnacks.rpgstats.systems.ConstitutionHealthEffect;
import com.bsnacks.rpgstats.systems.EnduranceStaminaEffect;
import com.bsnacks.rpgstats.systems.IntellectManaEffect;
import com.bsnacks.rpgstats.systems.LightFootSpeedEffect;
import com.bsnacks.rpgstats.ui.RpgStatsHud;
import com.bsnacks.rpgstats.ui.StatsPage;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.permissions.provider.PermissionProvider;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.List;
import java.util.Set;

public final class StatsSetCommand extends CommandBase {

    private final RpgStatsPlugin plugin;
    private final ComponentType<EntityStore, RpgStats> rpgStatsType;
    private final RequiredArg<String> attributeArg;
    private final RequiredArg<String> targetArg;
    private final RequiredArg<String> valueArg;
    private final RpgStatsConfig config;

    public StatsSetCommand(RpgStatsPlugin plugin, ComponentType<EntityStore, RpgStats> rpgStatsType,
                           RpgStatsConfig config) {
        super("set", "Set a player's RPG stat.");
        setPermissionGroup(GameMode.Adventure);
        requirePermission(RpgStatsPermissions.STATS_SET);
        this.plugin = plugin;
        this.rpgStatsType = rpgStatsType;
        this.config = config;
        attributeArg = withRequiredArg("attribute", "Stat name", ArgTypes.STRING);
        targetArg = withRequiredArg("target", "self or player name", ArgTypes.STRING);
        valueArg = withRequiredArg("value", "New value", ArgTypes.STRING);
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void executeSync(CommandContext ctx) {
        String attributeRaw = attributeArg.get(ctx);
        String targetRaw = targetArg.get(ctx);
        String valueRaw = valueArg.get(ctx);

        logPermissionDebug(ctx, targetRaw, attributeRaw);

        if (!PermissionChecks.requirePrivileged(ctx, RpgStatsPermissions.STATS_SET)) {
            plugin.logDebug("Denied /stats set: sender=" + ctx.sender().getDisplayName()
                    + " uuid=" + ctx.sender().getUuid());
            return;
        }

        Long value = parseLong(ctx, valueRaw);
        if (value == null) {
            return;
        }

        String attribute = attributeRaw.toLowerCase();
        final Integer levelValue;
        if ("level".equals(attribute)) {
            levelValue = toIntValue(ctx, "Level", value);
            if (levelValue == null) {
                return;
            }
            int maxLevel = config.getMaxLevel();
            if (levelValue > maxLevel) {
                ctx.sendMessage(Message.raw("Level cap for server is set to " + maxLevel));
                return;
            }
        } else {
            levelValue = null;
        }

        if (!"self".equalsIgnoreCase(targetRaw)) {
            if (!PermissionChecks.requirePrivileged(ctx, RpgStatsPermissions.STATS_SET_OTHERS)) {
                plugin.logDebug("Denied /stats set others: sender=" + ctx.sender().getDisplayName()
                        + " uuid=" + ctx.sender().getUuid()
                        + " target=" + targetRaw);
                return;
            }
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
            stats.migrateIfNeeded();

            switch (attribute) {
                case "level":
                    Integer level = levelValue;
                    if (level == null) {
                        return;
                    }
                    stats.setLevel(level);
                    break;
                case "xp":
                    stats.setXp(value);
                    break;
                case "str":
                    Integer str = toIntValue(ctx, "Str", value);
                    if (str == null) {
                        return;
                    }
                    if (str > getStatCap("str")) {
                        ctx.sendMessage(Message.raw("STR cap for server is set to " + getStatCap("str") + "."));
                        return;
                    }
                    stats.setStr(str);
                    break;
                case "dex":
                    Integer dex = toIntValue(ctx, "Dex", value);
                    if (dex == null) {
                        return;
                    }
                    if (dex > getStatCap("dex")) {
                        ctx.sendMessage(Message.raw("DEX cap for server is set to " + getStatCap("dex") + "."));
                        return;
                    }
                    stats.setDex(dex);
                    break;
                case "con":
                    Integer con = toIntValue(ctx, "Con", value);
                    if (con == null) {
                        return;
                    }
                    if (con > getStatCap("con")) {
                        ctx.sendMessage(Message.raw("CON cap for server is set to " + getStatCap("con") + "."));
                        return;
                    }
                    stats.setCon(con);
                    break;
                case "int":
                case "intl":
                    Integer intl = toIntValue(ctx, "Int", value);
                    if (intl == null) {
                        return;
                    }
                    if (intl > getStatCap("int")) {
                        ctx.sendMessage(Message.raw("INT cap for server is set to " + getStatCap("int") + "."));
                        return;
                    }
                    stats.setIntl(intl);
                    break;
                case "end":
                case "endurance":
                    Integer end = toIntValue(ctx, "End", value);
                    if (end == null) {
                        return;
                    }
                    if (end > getStatCap("end")) {
                        ctx.sendMessage(Message.raw("END cap for server is set to " + getStatCap("end") + "."));
                        return;
                    }
                    stats.setEnd(end);
                    break;
                case "cha":
                    Integer cha = toIntValue(ctx, "Cha", value);
                    if (cha == null) {
                        return;
                    }
                    if (cha > getStatCap("cha")) {
                        ctx.sendMessage(Message.raw("CHA cap for server is set to " + getStatCap("cha") + "."));
                        return;
                    }
                    stats.setCha(cha);
                    break;
                default:
                    ctx.sendMessage(Message.raw("Unknown attribute '" + attributeRaw + "'. Try: level, xp, str, dex, con, int, end, cha."));
                    return;
            }

            EntityStatMap statMap = worldStore.ensureAndGetComponent(target.ref, EntityStatMap.getComponentType());
            ConstitutionHealthEffect.apply(statMap, stats, config);
            IntellectManaEffect.apply(statMap, stats, config);
            EnduranceStaminaEffect.apply(statMap, stats, config);
            Player targetPlayer = worldStore.getComponent(target.ref, Player.getComponentType());
            if (targetPlayer != null) {
                LightFootSpeedEffect.apply(target.ref, worldStore, targetPlayer, stats, config, plugin);
            }
            StatsPage.refreshIfOpen(target.ref, worldStore);
            if (config == null || config.isHudEnabled()) {
                RpgStatsHud.refreshIfActive(target.ref, worldStore);
            }

            ctx.sendMessage(Message.raw("Set " + attribute.toUpperCase() + " for " + target.name + " to " + value + "."));
            plugin.logInfo("Set " + attribute + " for " + target.name + " to " + value);
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

    private Long parseLong(CommandContext ctx, String raw) {
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException ex) {
            ctx.sendMessage(Message.raw("Value must be a whole number."));
            return null;
        }
    }

    private Integer toIntValue(CommandContext ctx, String label, long value) {
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            ctx.sendMessage(Message.raw(label + " value is out of range for a 32-bit integer."));
            return null;
        }
        return (int) value;
    }

    private void logPermissionDebug(CommandContext ctx, String targetRaw, String attributeRaw) {
        boolean hasSetPermission = ctx.sender().hasPermission(RpgStatsPermissions.STATS_SET);
        boolean hasSetOthersPermission = ctx.sender().hasPermission(RpgStatsPermissions.STATS_SET_OTHERS);
        boolean hasWildcard = ctx.sender().hasPermission("*");
        boolean hasFake = ctx.sender().hasPermission("rpgstats.debug.fake");

        plugin.logInfo("Permission debug /stats set: sender=" + ctx.sender().getDisplayName()
                + " uuid=" + ctx.sender().getUuid()
                + " set=" + hasSetPermission
                + " set.others=" + hasSetOthersPermission
                + " wildcard=" + hasWildcard
                + " fake=" + hasFake
                + " target=" + targetRaw
                + " attribute=" + attributeRaw);

        PermissionsModule permissions = PermissionsModule.get();
        if (permissions == null) {
            plugin.logInfo("Permission debug: PermissionsModule unavailable.");
            return;
        }
        List<PermissionProvider> providers = permissions.getProviders();
        if (providers == null || providers.isEmpty()) {
            plugin.logInfo("Permission debug: no permission providers registered.");
            return;
        }
        plugin.logInfo("Permission debug: providers=" + providers.size() + " tampered=" + permissions.areProvidersTampered());
        for (PermissionProvider provider : providers) {
            if (provider == null) {
                continue;
            }
            Set<String> userPerms = provider.getUserPermissions(ctx.sender().getUuid());
            String userSummary = summarizePermissions(userPerms, RpgStatsPermissions.STATS_SET);
            String userResult = safeHasPermission(userPerms, RpgStatsPermissions.STATS_SET);
            Set<String> groups = provider.getGroupsForUser(ctx.sender().getUuid());
            String groupSummary = summarizeGroupPermissions(provider, groups, RpgStatsPermissions.STATS_SET);
            plugin.logInfo("Permission debug provider=" + provider.getName()
                    + " user=" + userSummary
                    + " userResult=" + userResult
                    + " groups=" + (groups == null ? "[]" : groups)
                    + " groupResults=" + groupSummary);
        }
    }

    private String summarizePermissions(Set<String> perms, String node) {
        if (perms == null) {
            return "null";
        }
        String className = perms.getClass().getName();
        String hasStar = safeContains(perms, "*");
        String hasNegStar = safeContains(perms, "-*");
        String hasNode = safeContains(perms, node);
        String hasNodeWildcard = safeContains(perms, node + ".*");
        String hasNegNode = safeContains(perms, "-" + node);
        String hasNegNodeWildcard = safeContains(perms, "-" + node + ".*");
        return "class=" + className
                + " flags=[*=" + hasStar
                + ",-*=" + hasNegStar
                + ",node=" + hasNode
                + ",node.*=" + hasNodeWildcard
                + ",-node=" + hasNegNode
                + ",-node.*=" + hasNegNodeWildcard
                + "]";
    }

    private String summarizeGroupPermissions(PermissionProvider provider, Set<String> groups, String node) {
        if (groups == null || groups.isEmpty()) {
            return "[]";
        }
        StringBuilder summary = new StringBuilder();
        boolean first = true;
        for (String group : groups) {
            Set<String> groupPerms = provider.getGroupPermissions(group);
            String groupResult = safeHasPermission(groupPerms, node);
            if (!first) {
                summary.append("; ");
            }
            summary.append(group)
                    .append(":")
                    .append(summarizePermissions(groupPerms, node))
                    .append(",result=")
                    .append(groupResult);
            first = false;
        }
        return summary.toString();
    }

    private int getStatCap(String attribute) {
        if (config == null) {
            return 25;
        }
        return config.getStatCap(attribute);
    }

    private String safeContains(Set<String> perms, String value) {
        try {
            return String.valueOf(perms.contains(value));
        } catch (RuntimeException ex) {
            return "error:" + ex.getClass().getSimpleName();
        }
    }

    private String safeHasPermission(Set<String> perms, String node) {
        try {
            Boolean result = PermissionsModule.hasPermission(perms, node);
            return result == null ? "null" : result.toString();
        } catch (RuntimeException ex) {
            return "error:" + ex.getClass().getSimpleName();
        }
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
