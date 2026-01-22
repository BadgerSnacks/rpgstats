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
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Set;

/**
 * Lifesteal ability system.
 *
 * When a player deals damage, they heal for a percentage of the damage dealt.
 * Percentage scales with ability level: 3%/6%/9% at levels 1-3.
 */
public final class LifestealSystem extends DamageEventSystem {

    private static final double DEFAULT_LIFESTEAL_PER_LEVEL_PCT = 3.0;

    private final ComponentType<EntityStore, RpgStats> rpgStatsType;
    private final Set<Dependency<EntityStore>> dependencies;
    private final RpgStatsConfig config;

    public LifestealSystem(ComponentType<EntityStore, RpgStats> rpgStatsType, RpgStatsConfig config) {
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

        // Check if the attacker is a player
        Player attacker = commandBuffer.getComponent(attackerRef, Player.getComponentType());
        if (attacker == null) {
            return;
        }

        // Get the player's RPG stats
        RpgStats stats = commandBuffer.getComponent(attackerRef, rpgStatsType);
        if (stats == null) {
            return;
        }

        int level = stats.getLifestealLevel();
        if (level <= 0) {
            return;
        }

        // Calculate lifesteal amount
        double perLevelPct = config == null ? DEFAULT_LIFESTEAL_PER_LEVEL_PCT : config.getLifestealPerLevelPct();
        if (perLevelPct < 0.0) {
            perLevelPct = 0.0;
        }
        double lifestealPct = perLevelPct * level;
        if (lifestealPct <= 0.0) {
            return;
        }

        // Calculate heal amount based on damage dealt
        float damageAmount = damage.getAmount();
        if (damageAmount <= 0) {
            return;
        }

        float healAmount = (float) (damageAmount * (lifestealPct / 100.0));
        if (healAmount <= 0) {
            return;
        }

        // Heal the attacker using EntityStatMap
        EntityStatMap statMap = commandBuffer.getComponent(attackerRef, EntityStatMap.getComponentType());
        if (statMap != null) {
            statMap.addStatValue(DefaultEntityStatTypes.getHealth(), healAmount);
        }
    }

    /**
     * Gets the lifesteal percentage for a given level.
     * @param level The Lifesteal ability level (0-3)
     * @param config The config to read the per-level percentage from
     * @return The total lifesteal percentage
     */
    public static float getLifestealPercent(int level, RpgStatsConfig config) {
        int clampedLevel = Math.max(0, level);
        double perLevelPct = config == null ? DEFAULT_LIFESTEAL_PER_LEVEL_PCT : config.getLifestealPerLevelPct();
        if (perLevelPct < 0.0) {
            perLevelPct = 0.0;
        }
        return (float) (perLevelPct * clampedLevel);
    }
}
