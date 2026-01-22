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
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Random;
import java.util.Set;

/**
 * Critical Strike ability system.
 *
 * When a player deals damage, they have a chance to deal bonus damage.
 * Chance scales with ability level: base + (per_level * level)
 * Default: 10% / 15% / 20% at levels 1-3
 */
public final class CriticalStrikeSystem extends DamageEventSystem {

    private static final double DEFAULT_CHANCE_PER_LEVEL_PCT = 5.0;
    private static final double DEFAULT_BASE_CHANCE_PCT = 5.0;
    private static final double DEFAULT_DAMAGE_MULTIPLIER = 1.5;

    private final ComponentType<EntityStore, RpgStats> rpgStatsType;
    private final Set<Dependency<EntityStore>> dependencies;
    private final RpgStatsConfig config;
    private final Random random;

    public CriticalStrikeSystem(ComponentType<EntityStore, RpgStats> rpgStatsType, RpgStatsConfig config) {
        this.rpgStatsType = rpgStatsType;
        this.config = config;
        this.random = new Random();
        dependencies = Set.of(
                new SystemGroupDependency<>(Order.AFTER, DamageModule.get().getGatherDamageGroup()),
                new SystemGroupDependency<>(Order.AFTER, DamageModule.get().getFilterDamageGroup()),
                new SystemDependency<>(Order.BEFORE, DamageSystems.ApplyDamage.class)
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

        int level = stats.getCriticalStrikeLevel();
        if (level <= 0) {
            return;
        }

        // Calculate critical strike chance: base + (per level * level)
        double baseChance = config == null ? DEFAULT_BASE_CHANCE_PCT : config.getCriticalStrikeBaseChancePct();
        double perLevelPct = config == null ? DEFAULT_CHANCE_PER_LEVEL_PCT : config.getCriticalStrikeChancePerLevelPct();
        if (baseChance < 0.0) {
            baseChance = 0.0;
        }
        if (perLevelPct < 0.0) {
            perLevelPct = 0.0;
        }
        double critChance = baseChance + (perLevelPct * level);
        if (critChance <= 0.0) {
            return;
        }
        if (critChance > 100.0) {
            critChance = 100.0;
        }

        // Roll for critical strike
        double roll = random.nextDouble() * 100.0;
        if (roll < critChance) {
            // Critical hit! Multiply damage
            double multiplier = config == null ? DEFAULT_DAMAGE_MULTIPLIER : config.getCriticalStrikeDamageMultiplier();
            if (multiplier < 1.0) {
                multiplier = 1.0;
            }
            float originalDamage = damage.getAmount();
            float critDamage = (float) (originalDamage * multiplier);
            damage.setAmount(critDamage);
            attacker.sendMessage(Message.raw("Critical strike! (" + String.format("%.1fx", multiplier) + ")"));
        }
    }

    /**
     * Gets the critical strike chance for a given level.
     * @param level The Critical Strike ability level (0-3)
     * @param config The config to read chance values from
     * @return The total chance as a percentage (0-100)
     */
    public static float getCriticalChance(int level, RpgStatsConfig config) {
        int clampedLevel = Math.max(0, level);
        double baseChance = config == null ? DEFAULT_BASE_CHANCE_PCT : config.getCriticalStrikeBaseChancePct();
        double perLevelPct = config == null ? DEFAULT_CHANCE_PER_LEVEL_PCT : config.getCriticalStrikeChancePerLevelPct();
        if (baseChance < 0.0) {
            baseChance = 0.0;
        }
        if (perLevelPct < 0.0) {
            perLevelPct = 0.0;
        }
        return (float) Math.min(100.0, baseChance + (perLevelPct * clampedLevel));
    }
}
