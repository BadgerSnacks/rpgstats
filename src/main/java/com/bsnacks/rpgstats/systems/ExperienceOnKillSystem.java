package com.bsnacks.rpgstats.systems;

import com.bsnacks.rpgstats.components.FlameTouchAttribution;
import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;
import com.bsnacks.rpgstats.logging.RpgStatsFileLogger;
import com.bsnacks.rpgstats.party.PartyService;
import com.bsnacks.rpgstats.party.PartyXpDistributor;
import com.bsnacks.rpgstats.services.NpcLevelCalculator;
import com.bsnacks.rpgstats.ui.LevelUpSplash;
import com.bsnacks.rpgstats.ui.RpgStatsHud;
import com.bsnacks.rpgstats.ui.StatsPage;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.asset.type.attitude.Attitude;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;

import java.util.UUID;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;

public final class ExperienceOnKillSystem extends DeathSystems.OnDeathSystem {

    private final ComponentType<EntityStore, RpgStats> rpgStatsType;
    private final ComponentType<EntityStore, FlameTouchAttribution> attributionType;
    private final ComponentType<EntityStore, NPCEntity> npcType;
    private final ComponentType<EntityStore, Player> playerType;
    private final ComponentType<EntityStore, EntityStatMap> statMapType;
    private final RpgStatsFileLogger fileLogger;
    private final RpgStatsConfig config;
    private final com.bsnacks.rpgstats.RpgStatsPlugin plugin;

    public ExperienceOnKillSystem(ComponentType<EntityStore, RpgStats> rpgStatsType,
                                  ComponentType<EntityStore, FlameTouchAttribution> attributionType,
                                  RpgStatsFileLogger fileLogger,
                                  RpgStatsConfig config,
                                  com.bsnacks.rpgstats.RpgStatsPlugin plugin) {
        this.rpgStatsType = rpgStatsType;
        this.attributionType = attributionType;
        this.fileLogger = fileLogger;
        this.config = config;
        this.plugin = plugin;
        npcType = NPCEntity.getComponentType();
        playerType = Player.getComponentType();
        statMapType = EntityStatMap.getComponentType();
    }

    @Override
    public Query<EntityStore> getQuery() {
        return npcType;
    }

    @Override
    public void onComponentAdded(Ref<EntityStore> ref, DeathComponent death, Store<EntityStore> store,
                                 CommandBuffer<EntityStore> commandBuffer) {
        NPCEntity npc = commandBuffer.getComponent(ref, npcType);
        if (npc == null) {
            return;
        }

        String roleName = safeRoleName(npc);
        String npcTypeId = safeNpcTypeId(npc);
        logDebug("NPC death detected: role=" + roleName + " npcTypeId=" + npcTypeId
                + " ref=" + ref.getIndex());

        if (config != null && config.isXpBlacklisted(npcTypeId, roleName)) {
            logDebug("XP skipped: blacklisted NPC role=" + roleName + " npcTypeId=" + npcTypeId);
            return;
        }

        Ref<EntityStore> attackerRef = resolveAttackerRef(npc, death, ref, commandBuffer);
        if (attackerRef == null || !attackerRef.isValid()) {
            logDebug("No valid attacker ref for NPC death: ref=" + ref.getIndex());
            return;
        }

        Player killer = commandBuffer.getComponent(attackerRef, playerType);
        if (killer == null) {
            logDebug("Attacker is not a player: attackerRef=" + attackerRef.getIndex());
            return;
        }

        Role role = npc.getRole();
        if (!isHostile(role, attackerRef, commandBuffer)) {
            logDebug("NPC not hostile to player: role=" + roleName + " player=" + killer.getDisplayName());
            return;
        }

        PlayerRef killerRef = commandBuffer.getComponent(attackerRef, PlayerRef.getComponentType());
        UUID killerUuid = killerRef == null ? null : killerRef.getUuid();

        EntityStatMap statMap = commandBuffer.getComponent(ref, statMapType);
        double multiplier = config == null ? 0.35d : config.getXpMultiplier();

        // Calculate NPC level for logging/future XP scaling
        int npcLevel = calculateNpcLevel(ref, npcTypeId, statMap, commandBuffer);

        int xpGained = ExperienceCalculator.calculate(npc, statMap, multiplier);
        if (xpGained <= 0) {
            logDebug("No XP awarded: role=" + roleName + " npcLevel=" + npcLevel
                    + " maxHealth=" + ExperienceCalculator.getMaxHealth(npc, statMap));
            return;
        }

        PartyService partyService = plugin == null ? null : plugin.getPartyService();
        PartyXpDistributor.PartyXpDistribution distribution = PartyXpDistributor.distribute(
                killerUuid, attackerRef, store, commandBuffer, xpGained, config, partyService);

        int killerXp = xpGained;
        for (PartyXpDistributor.PartyXpShare share : distribution.getShares()) {
            if (share == null || share.getXp() <= 0) {
                continue;
            }
            Ref<EntityStore> targetRef = share.getRef();
            if (targetRef == null || !targetRef.isValid()) {
                continue;
            }
            Player targetPlayer = commandBuffer.getComponent(targetRef, playerType);
            if (targetPlayer == null) {
                continue;
            }
            RpgStats stats = commandBuffer.ensureAndGetComponent(targetRef, rpgStatsType);
            stats.migrateIfNeeded();
            int oldLevel = stats.getLevel();
            long oldXp = stats.getXp();
            stats.setXp(oldXp + share.getXp());
            int newLevel = stats.getLevel();
            boolean chatEnabled = config == null || config.isXpChatMessagesEnabled();
            if (chatEnabled) {
                String prefix = distribution.isShared() && !share.isKiller() ? "Party XP: " : null;
                sendXpMessage(targetPlayer, stats, share.getXp(), oldLevel, prefix);
            }
            StatsPage.refreshIfOpen(targetPlayer, stats);
            if (config == null || config.isHudEnabled()) {
                RpgStatsHud.refreshIfActive(targetPlayer, stats);
            }
            if (newLevel > oldLevel) {
                LevelUpSplash.showForPlayer(targetPlayer, newLevel, plugin);
            }
            if (share.isKiller()) {
                killerXp = share.getXp();
            }
        }

        if (distribution.isShared() && distribution.getEligibleCount() > 1) {
            logDebug("XP shared: base=" + xpGained + " total=" + distribution.getTotalXp()
                    + " eligible=" + distribution.getEligibleCount()
                    + " killerXp=" + killerXp + " npcLevel=" + npcLevel);
        } else {
            logDebug("XP awarded: player=" + killer.getDisplayName() + " xp=" + killerXp
                    + " npcLevel=" + npcLevel);
        }
    }

    /**
     * Calculates the NPC's level using the NpcLevelCalculator.
     */
    private int calculateNpcLevel(Ref<EntityStore> npcRef, String npcTypeId,
                                  EntityStatMap statMap, CommandBuffer<EntityStore> commandBuffer) {
        NpcLevelCalculator calculator = plugin.getNpcLevelCalculator();
        if (calculator == null) {
            logDebug("NpcLevelCalculator not available, defaulting to level 1");
            return 1;
        }

        // Get entity UUID for caching
        UUID entityUuid = null;
        try {
            UUIDComponent uuidComp = commandBuffer.getComponent(npcRef, UUIDComponent.getComponentType());
            if (uuidComp != null) {
                entityUuid = uuidComp.getUuid();
            }
        } catch (Exception ex) {
            logDebug("Failed to get UUIDComponent: " + ex.getMessage());
        }
        if (entityUuid == null) {
            entityUuid = UUID.randomUUID();
        }

        // Get max HP from stat map
        float maxHp = 0f;
        if (statMap != null) {
            try {
                int healthIdx = DefaultEntityStatTypes.getHealth();
                EntityStatValue healthStat = statMap.get(healthIdx);
                if (healthStat != null) {
                    maxHp = healthStat.getMax();
                }
            } catch (Exception ex) {
                logDebug("Failed to get max HP: " + ex.getMessage());
            }
        }

        // Calculate level (no zone ID for now - could be added later)
        int npcLevel = calculator.computeLevel(entityUuid, npcTypeId, maxHp, null);
        logDebug("NPC level calculated: type=" + npcTypeId + " hp=" + String.format("%.1f", maxHp)
                + " level=" + npcLevel);
        return npcLevel;
    }

    private boolean isHostile(Role role, Ref<EntityStore> targetRef, CommandBuffer<EntityStore> commandBuffer) {
        if (role == null || targetRef == null) {
            return false;
        }
        NPCPlugin npcPlugin = NPCPlugin.get();
        if (npcPlugin == null || npcPlugin.getAttitudeMap() == null) {
            return !role.isFriendly(targetRef, commandBuffer);
        }
        Attitude attitude = npcPlugin.getAttitudeMap().getAttitude(role, targetRef, commandBuffer);
        logDebug("Attitude check: attitude=" + attitude + " role=" + role.getRoleName());
        if (attitude == null) {
            boolean hostileFallback = !role.isFriendly(targetRef, commandBuffer);
            logDebug("Attitude fallback: hostile=" + hostileFallback + " role=" + role.getRoleName());
            return hostileFallback;
        }
        return attitude == Attitude.HOSTILE;
    }

    private Ref<EntityStore> resolveAttackerRef(NPCEntity npc, DeathComponent death,
                                                Ref<EntityStore> npcRef, CommandBuffer<EntityStore> commandBuffer) {
        Damage damage = death.getDeathInfo();
        if (damage != null) {
            Damage.Source source = damage.getSource();
            if (source instanceof Damage.EntitySource) {
                Ref<EntityStore> ref = ((Damage.EntitySource) source).getRef();
                if (ref != null && ref.isValid()) {
                    logDebug("Attacker resolved from death info: ref=" + ref.getIndex());
                    return ref;
                }
            }
        }

        var damageData = npc.getDamageData();
        if (damageData != null) {
            Ref<EntityStore> ref = damageData.getMostDamagingAttacker();
            if (ref != null && ref.isValid()) {
                logDebug("Attacker resolved from damage data: ref=" + ref.getIndex());
                return ref;
            }
            Ref<EntityStore> any = damageData.getAnyAttacker();
            if (any != null && any.isValid()) {
                logDebug("Fallback attacker resolved from damage data: ref=" + any.getIndex());
                return any;
            }
        }

        // Check for Flame Touch attribution (for burn kills)
        if (attributionType != null && npcRef != null) {
            FlameTouchAttribution attribution = commandBuffer.getComponent(npcRef, attributionType);
            if (attribution != null) {
                Ref<EntityStore> flameTouchAttacker = attribution.getAttackerIfValid();
                if (flameTouchAttacker != null && flameTouchAttacker.isValid()) {
                    logDebug("Attacker resolved from Flame Touch attribution: ref=" + flameTouchAttacker.getIndex()
                            + " remaining=" + String.format("%.1f", attribution.getRemainingSeconds()) + "s");
                    return flameTouchAttacker;
                } else {
                    logDebug("Flame Touch attribution expired or invalid");
                }
            }
        }

        return null;
    }

    private void logDebug(String message) {
        if (fileLogger != null) {
            fileLogger.log(message);
        }
    }

    private String safeRoleName(NPCEntity npc) {
        String roleName = npc.getRoleName();
        if (roleName != null && !roleName.isBlank()) {
            return roleName;
        }
        Role role = npc.getRole();
        return role == null ? "unknown" : role.getRoleName();
    }

    private String safeNpcTypeId(NPCEntity npc) {
        String typeId = npc.getNPCTypeId();
        if (typeId == null || typeId.isBlank()) {
            return "unknown";
        }
        return typeId;
    }

    private void sendXpMessage(Player player, RpgStats stats, int xpGained, int oldLevel, String prefix) {
        int newLevel = stats.getLevel();
        long xpIntoLevel = stats.getXpIntoLevel();
        long xpRemaining = stats.getXpToNextLevel();

        StringBuilder message = new StringBuilder(64);
        if (prefix != null && !prefix.isBlank()) {
            message.append(prefix);
        }
        message.append("Gained ").append(xpGained).append(" XP.");
        if (newLevel > oldLevel) {
            message.append(" Level up! Now level ").append(newLevel).append(".");
        }
        if (xpRemaining == 0L) {
            message.append(" XP ").append(xpIntoLevel).append("/MAX.");
        } else {
            long xpNeeded = xpIntoLevel + xpRemaining;
            message.append(" XP ").append(xpIntoLevel).append("/").append(xpNeeded).append(".");
        }

        player.sendMessage(Message.raw(message.toString()));
    }
}
