package com.bsnacks.rpgstats.commands;

import com.bsnacks.rpgstats.RpgStatsPlugin;
import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;
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
        this.plugin = plugin;
        this.rpgStatsType = rpgStatsType;
        this.config = config;
        attributeArg = withRequiredArg("attribute", "Stat name", ArgTypes.STRING);
        targetArg = withRequiredArg("target", "self or player name", ArgTypes.STRING);
        valueArg = withRequiredArg("value", "New value", ArgTypes.STRING);
    }

    @Override
    protected void executeSync(CommandContext ctx) {
        CommandUtil.requirePermission(ctx.sender(), plugin.getBasePermission() + ".stats.set");
        String attributeRaw = attributeArg.get(ctx);
        String targetRaw = targetArg.get(ctx);
        String valueRaw = valueArg.get(ctx);

        Long value = parseLong(ctx, valueRaw);
        if (value == null) {
            return;
        }

        if (!"self".equalsIgnoreCase(targetRaw)) {
            CommandUtil.requirePermission(ctx.sender(), plugin.getBasePermission() + ".stats.set.others");
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

            String attribute = attributeRaw.toLowerCase();
            switch (attribute) {
                case "level":
                    Integer level = toIntValue(ctx, "Level", value);
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
                    stats.setStr(str);
                    break;
                case "dex":
                    Integer dex = toIntValue(ctx, "Dex", value);
                    if (dex == null) {
                        return;
                    }
                    stats.setDex(dex);
                    break;
                case "con":
                    Integer con = toIntValue(ctx, "Con", value);
                    if (con == null) {
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
                    stats.setIntl(intl);
                    break;
                case "end":
                case "endurance":
                    Integer end = toIntValue(ctx, "End", value);
                    if (end == null) {
                        return;
                    }
                    stats.setEnd(end);
                    break;
                case "cha":
                    Integer cha = toIntValue(ctx, "Cha", value);
                    if (cha == null) {
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

    private static final class Target {
        private final Ref<EntityStore> ref;
        private final String name;

        private Target(Ref<EntityStore> ref, String name) {
            this.ref = ref;
            this.name = name;
        }
    }
}
