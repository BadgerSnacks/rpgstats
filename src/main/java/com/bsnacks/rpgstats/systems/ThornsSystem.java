package com.bsnacks.rpgstats.systems;

import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.dependency.SystemGroupDependency;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Set;

/**
 * Thorns ability system.
 *
 * When a player takes damage from an attacker, reflects a percentage of damage back.
 * Percentage scales with ability level: 25%/50%/75% at levels 1-3.
 */
public final class ThornsSystem extends DamageEventSystem {

    private static final double DEFAULT_THORNS_REFLECT_PER_LEVEL_PCT = 25.0;

    private final ComponentType<EntityStore, RpgStats> rpgStatsType;
    private final Set<Dependency<EntityStore>> dependencies;
    private final RpgStatsConfig config;

    public ThornsSystem(ComponentType<EntityStore, RpgStats> rpgStatsType, RpgStatsConfig config) {
        this.rpgStatsType = rpgStatsType;
        this.config = config;
        dependencies = Set.of(
                new SystemGroupDependency<>(Order.AFTER, DamageModule.get().getGatherDamageGroup()),
                new SystemGroupDependency<>(Order.AFTER, DamageModule.get().getFilterDamageGroup()),
                new SystemDependency<>(Order.AFTER, DamageSystems.ApplyDamage.class)
        );
    }

    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return dependencies;
    }

    @Override
    public Archetype<EntityStore> getQuery() {
        return Archetype.empty();
    }

    @Override
    public void handle(int entityIndex, ArchetypeChunk<EntityStore> chunk, Store<EntityStore> store,
                       CommandBuffer<EntityStore> commandBuffer, Damage damage) {
        if (damage == null) {
            return;
        }

        // Get the entity being damaged (defender)
        Ref<EntityStore> defenderRef = chunk.getReferenceTo(entityIndex);
        if (defenderRef == null || !defenderRef.isValid()) {
            return;
        }

        // Check if the defender is a player
        Player defender = commandBuffer.getComponent(defenderRef, Player.getComponentType());
        if (defender == null) {
            return;
        }

        // Get the defender's RPG stats
        RpgStats stats = commandBuffer.getComponent(defenderRef, rpgStatsType);
        if (stats == null) {
            return;
        }

        int level = stats.getThornsLevel();
        if (level <= 0) {
            return;
        }

        // Only apply to damage from entities
        Damage.Source source = damage.getSource();
        if (!(source instanceof Damage.EntitySource)) {
            return;
        }

        // Get the attacker reference
        Ref<EntityStore> attackerRef = ((Damage.EntitySource) source).getRef();
        if (attackerRef == null || !attackerRef.isValid()) {
            return;
        }

        // Don't reflect damage to self
        if (attackerRef.equals(defenderRef)) {
            return;
        }

        // Calculate reflect amount
        double perLevelPct = config == null ? DEFAULT_THORNS_REFLECT_PER_LEVEL_PCT : config.getThornsReflectPerLevelPct();
        if (perLevelPct < 0.0) {
            perLevelPct = 0.0;
        }
        double reflectPct = perLevelPct * level;
        if (reflectPct <= 0.0) {
            return;
        }

        // Calculate reflected damage based on damage taken
        float damageAmount = damage.getAmount();
        if (damageAmount <= 0) {
            return;
        }

        float reflectAmount = (float) (damageAmount * (reflectPct / 100.0));
        if (reflectAmount <= 0) {
            return;
        }

        // Apply damage to the attacker using EntityStatMap
        EntityStatMap attackerStatMap = commandBuffer.getComponent(attackerRef, EntityStatMap.getComponentType());
        if (attackerStatMap != null) {
            // Subtract health from attacker (negative value to deal damage)
            attackerStatMap.addStatValue(DefaultEntityStatTypes.getHealth(), -reflectAmount);
        }
    }

    /**
     * Gets the thorns reflect percentage for a given level.
     * @param level The Thorns ability level (0-3)
     * @param config The config to read the per-level percentage from
     * @return The total thorns reflect percentage
     */
    public static float getThornsReflectPercent(int level, RpgStatsConfig config) {
        int clampedLevel = Math.max(0, level);
        double perLevelPct = config == null ? DEFAULT_THORNS_REFLECT_PER_LEVEL_PCT : config.getThornsReflectPerLevelPct();
        if (perLevelPct < 0.0) {
            perLevelPct = 0.0;
        }
        return (float) (perLevelPct * clampedLevel);
    }
}
