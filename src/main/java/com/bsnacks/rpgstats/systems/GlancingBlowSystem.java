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
import com.hypixel.hytale.server.npc.entities.NPCEntity;

import java.util.Random;
import java.util.Set;

public final class GlancingBlowSystem extends DamageEventSystem {

    private static final double BASE_DODGE_CHANCE_PCT = 5.0;
    private static final double DEFAULT_DODGE_CHANCE_PER_LEVEL_PCT = 5.0;

    private final ComponentType<EntityStore, RpgStats> rpgStatsType;
    private final ComponentType<EntityStore, NPCEntity> npcType;
    private final Set<Dependency<EntityStore>> dependencies;
    private final RpgStatsConfig config;
    private final Random random;

    public GlancingBlowSystem(ComponentType<EntityStore, RpgStats> rpgStatsType, RpgStatsConfig config) {
        this.rpgStatsType = rpgStatsType;
        this.npcType = NPCEntity.getComponentType();
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

        // Only apply to damage from entities (excludes environmental damage)
        Damage.Source source = damage.getSource();
        if (!(source instanceof Damage.EntitySource)) {
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

        int level = stats.getGlancingBlowLevel();
        if (level <= 0) {
            return;
        }

        // Check if the damage source is a hostile NPC
        Ref<EntityStore> sourceRef = ((Damage.EntitySource) source).getRef();
        if (sourceRef == null || !sourceRef.isValid()) {
            return;
        }

        NPCEntity npc = commandBuffer.getComponent(sourceRef, npcType);
        if (npc == null) {
            return;
        }

        // Calculate dodge chance: base + (per level × level)
        double perLevelPct = config == null ? DEFAULT_DODGE_CHANCE_PER_LEVEL_PCT : config.getGlancingBlowChancePerLevelPct();
        if (perLevelPct < 0.0) {
            perLevelPct = 0.0;
        }
        double dodgeChance = BASE_DODGE_CHANCE_PCT + (perLevelPct * level);
        if (dodgeChance <= 0.0) {
            return;
        }
        if (dodgeChance > 100.0) {
            dodgeChance = 100.0;
        }

        // Roll for dodge
        double roll = random.nextDouble() * 100.0;
        if (roll < dodgeChance) {
            damage.setAmount(0f);
            target.sendMessage(Message.raw("Glancing blow!"));
        }
    }

    public static float getDodgeChance(int level, RpgStatsConfig config) {
        int clampedLevel = Math.max(0, level);
        double perLevelPct = config == null ? DEFAULT_DODGE_CHANCE_PER_LEVEL_PCT : config.getGlancingBlowChancePerLevelPct();
        if (perLevelPct < 0.0) {
            perLevelPct = 0.0;
        }
        return (float) (BASE_DODGE_CHANCE_PCT + (perLevelPct * clampedLevel));
    }
}
