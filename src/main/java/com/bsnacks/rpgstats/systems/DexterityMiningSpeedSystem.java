package com.bsnacks.rpgstats.systems;

import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public final class DexterityMiningSpeedSystem extends EntityEventSystem<EntityStore, DamageBlockEvent> {

    private final ComponentType<EntityStore, RpgStats> rpgStatsType;
    private final RpgStatsConfig config;

    public DexterityMiningSpeedSystem(ComponentType<EntityStore, RpgStats> rpgStatsType, RpgStatsConfig config) {
        super(DamageBlockEvent.class);
        this.rpgStatsType = rpgStatsType;
        this.config = config;
    }

    @Override
    public Archetype<EntityStore> getQuery() {
        return Archetype.empty();
    }

    @Override
    public void handle(int entityIndex, ArchetypeChunk<EntityStore> chunk, Store<EntityStore> store,
                       CommandBuffer<EntityStore> commandBuffer, DamageBlockEvent event) {
        if (event == null) {
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

        double base = config == null ? 1.0 : config.getMiningSpeedBase();
        double perPoint = config == null ? 0.10 : config.getMiningSpeedPerPoint();
        float multiplier = (float) (base + perPoint * (stats.getDex() - RpgStats.BASE_STAT));
        multiplier = Math.max(0.5f, Math.min(2.5f, multiplier));

        event.setDamage(event.getDamage() * multiplier);
    }
}
