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
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Set;

public final class ArmorProficiencySystem extends DamageEventSystem {

    private static final double DEFAULT_RESISTANCE_PER_LEVEL_PCT = 5.0;
    private static final float MAX_REDUCTION = 0.95f;

    private final ComponentType<EntityStore, RpgStats> rpgStatsType;
    private final Set<Dependency<EntityStore>> dependencies;
    private final RpgStatsConfig config;

    public ArmorProficiencySystem(ComponentType<EntityStore, RpgStats> rpgStatsType, RpgStatsConfig config) {
        this.rpgStatsType = rpgStatsType;
        this.config = config;
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
        DamageCause cause = damage.getCause();
        if (cause == null || (cause != DamageCause.PHYSICAL && cause != DamageCause.PROJECTILE)) {
            return;
        }

        Ref<EntityStore> targetRef = chunk.getReferenceTo(entityIndex);
        if (targetRef == null || !targetRef.isValid()) {
            return;
        }

        Player target = commandBuffer.getComponent(targetRef, Player.getComponentType());
        if (target == null) {
            return;
        }

        RpgStats stats = commandBuffer.getComponent(targetRef, rpgStatsType);
        if (stats == null) {
            return;
        }

        int level = stats.getArmorProficiencyLevel();
        if (level <= 0) {
            return;
        }

        Inventory inventory = target.getInventory();
        ItemContainer armor = inventory == null ? null : inventory.getArmor();
        if (armor == null || armor.isEmpty()) {
            return;
        }

        double perLevelPct = config == null ? DEFAULT_RESISTANCE_PER_LEVEL_PCT : config.getArmorProficiencyResistancePerLevelPct();
        if (perLevelPct <= 0.0) {
            return;
        }
        if (perLevelPct > 100.0) {
            perLevelPct = 100.0;
        }
        float reduction = (float) ((perLevelPct / 100.0) * level);
        if (reduction <= 0f) {
            return;
        }
        if (reduction > MAX_REDUCTION) {
            reduction = MAX_REDUCTION;
        }

        float newAmount = damage.getAmount() * (1.0f - reduction);
        if (newAmount < 0f) {
            newAmount = 0f;
        }
        damage.setAmount(newAmount);
    }

    public static float getResistanceBonus(int level, RpgStatsConfig config) {
        int clampedLevel = Math.max(0, level);
        double perLevelPct = config == null ? DEFAULT_RESISTANCE_PER_LEVEL_PCT : config.getArmorProficiencyResistancePerLevelPct();
        if (perLevelPct < 0.0) {
            perLevelPct = 0.0;
        } else if (perLevelPct > 100.0) {
            perLevelPct = 100.0;
        }
        return (float) ((perLevelPct / 100.0) * clampedLevel);
    }
}
