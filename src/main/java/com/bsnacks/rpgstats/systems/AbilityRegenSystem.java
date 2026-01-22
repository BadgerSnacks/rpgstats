package com.bsnacks.rpgstats.systems;

import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public final class AbilityRegenSystem extends DelayedEntitySystem<EntityStore> {

    private static final float TICK_INTERVAL_SEC = 1.0f;
    private static final double DEFAULT_REGEN_PER_LEVEL_PER_SEC = 1.0;

    private final ComponentType<EntityStore, RpgStats> rpgStatsType;
    private final Query<EntityStore> query;
    private final RpgStatsConfig config;

    public AbilityRegenSystem(ComponentType<EntityStore, RpgStats> rpgStatsType, RpgStatsConfig config) {
        super(TICK_INTERVAL_SEC);
        this.rpgStatsType = rpgStatsType;
        this.config = config;
        this.query = Archetype.of(Player.getComponentType(), EntityStatMap.getComponentType(), rpgStatsType);
    }

    @Override
    public Query<EntityStore> getQuery() {
        return query;
    }

    @Override
    public void tick(float deltaSeconds, int entityIndex, ArchetypeChunk<EntityStore> chunk, Store<EntityStore> store,
                     CommandBuffer<EntityStore> commandBuffer) {
        if (deltaSeconds <= 0f) {
            return;
        }

        Ref<EntityStore> ref = chunk.getReferenceTo(entityIndex);
        if (ref == null || !ref.isValid()) {
            return;
        }

        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        RpgStats stats = commandBuffer.getComponent(ref, rpgStatsType);
        if (stats == null) {
            return;
        }

        int healthLevel = stats.getHealthRegenLevel();
        int staminaLevel = stats.getStaminaRegenLevel();
        if (healthLevel <= 0 && staminaLevel <= 0) {
            return;
        }

        EntityStatMap statMap = commandBuffer.getComponent(ref, EntityStatMap.getComponentType());
        if (statMap == null) {
            return;
        }

        if (healthLevel > 0) {
            double perLevel = config == null ? DEFAULT_REGEN_PER_LEVEL_PER_SEC : config.getHealthRegenPerLevelPerSec();
            applyRegen(statMap, DefaultEntityStatTypes.getHealth(), deltaSeconds, healthLevel, perLevel, true);
        }

        if (staminaLevel > 0) {
            double perLevel = config == null ? DEFAULT_REGEN_PER_LEVEL_PER_SEC : config.getStaminaRegenPerLevelPerSec();
            applyRegen(statMap, DefaultEntityStatTypes.getStamina(), deltaSeconds, staminaLevel, perLevel, false);
        }
    }

    private void applyRegen(EntityStatMap statMap, int statId, float deltaSeconds, int level,
                            double perLevelPerSec, boolean requirePositive) {
        if (level <= 0 || perLevelPerSec <= 0.0) {
            return;
        }
        EntityStatValue stat = statMap.get(statId);
        if (stat == null) {
            return;
        }
        float current = stat.get();
        if (requirePositive && current <= 0f) {
            return;
        }
        float max = stat.getMax();
        if (current >= max) {
            return;
        }
        float bonus = (float) (perLevelPerSec * level * deltaSeconds);
        if (bonus <= 0f) {
            return;
        }
        statMap.addStatValue(statId, bonus);
    }
}
