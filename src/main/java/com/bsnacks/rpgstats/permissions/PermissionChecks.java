package com.bsnacks.rpgstats.permissions;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.permissions.provider.PermissionProvider;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class PermissionChecks {

    private PermissionChecks() {
    }

    public static boolean requirePrivileged(CommandContext ctx, String node) {
        if (ctx == null) {
            return false;
        }
        if (!ctx.isPlayer()) {
            return true;
        }
        CommandSender sender = ctx.sender();
        if (sender == null) {
            return false;
        }
        if (isOp(sender) || hasExplicitPermission(sender, node)) {
            return true;
        }
        ctx.sendMessage(Message.raw("You do not have permission to use this command."));
        return false;
    }

    private static boolean isOp(CommandSender sender) {
        return sender.hasPermission("*");
    }

    private static boolean hasExplicitPermission(CommandSender sender, String node) {
        if (node == null || node.isBlank()) {
            return false;
        }
        PermissionsModule permissions = PermissionsModule.get();
        if (permissions == null) {
            return false;
        }
        List<PermissionProvider> providers = permissions.getProviders();
        if (providers == null || providers.isEmpty()) {
            return false;
        }
        Set<String> combined = new HashSet<>();
        UUID uuid = sender.getUuid();
        for (PermissionProvider provider : providers) {
            if (provider == null) {
                continue;
            }
            safeAddAll(combined, provider.getUserPermissions(uuid));
            Set<String> groups = provider.getGroupsForUser(uuid);
            if (groups == null) {
                continue;
            }
            for (String group : groups) {
                safeAddAll(combined, provider.getGroupPermissions(group));
            }
        }
        return evaluatePermissions(combined, node);
    }

    private static void safeAddAll(Set<String> target, Set<String> source) {
        if (source == null) {
            return;
        }
        try {
            for (String value : source) {
                if (value != null && !value.isBlank()) {
                    target.add(value);
                }
            }
        } catch (RuntimeException ignored) {
        }
    }

    private static boolean evaluatePermissions(Set<String> perms, String node) {
        if (perms == null || perms.isEmpty()) {
            return false;
        }
        String normalizedNode = node.toLowerCase(Locale.ROOT);
        Set<String> normalizedPerms = new HashSet<>();
        for (String perm : perms) {
            if (perm == null) {
                continue;
            }
            String normalized = perm.trim().toLowerCase(Locale.ROOT);
            if (!normalized.isEmpty()) {
                normalizedPerms.add(normalized);
            }
        }

        if (normalizedPerms.contains("-*")) {
            return false;
        }
        if (normalizedPerms.contains("*")) {
            return true;
        }

        if (normalizedPerms.contains("-" + normalizedNode) || normalizedPerms.contains("-" + normalizedNode + ".*")) {
            return false;
        }
        if (normalizedPerms.contains(normalizedNode) || normalizedPerms.contains(normalizedNode + ".*")) {
            return true;
        }

        String[] parts = normalizedNode.split("\\.");
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (i > 0) {
                prefix.append('.');
            }
            prefix.append(parts[i]);
            String wildcard = prefix + ".*";
            if (normalizedPerms.contains("-" + wildcard)) {
                return false;
            }
            if (normalizedPerms.contains(wildcard)) {
                return true;
            }
        }

        return false;
    }
}
