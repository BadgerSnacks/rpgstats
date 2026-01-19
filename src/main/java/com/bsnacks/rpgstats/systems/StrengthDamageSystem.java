package com.bsnacks.rpgstats.systems;

import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.dependency.SystemGroupDependency;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Set;

public final class StrengthDamageSystem extends DamageEventSystem {

    private final ComponentType<EntityStore, RpgStats> rpgStatsType;
    private final Set<Dependency<EntityStore>> dependencies;
    private final RpgStatsConfig config;

    public StrengthDamageSystem(ComponentType<EntityStore, RpgStats> rpgStatsType, RpgStatsConfig config) {
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

        Damage.Source source = damage.getSource();
        if (!(source instanceof Damage.EntitySource)) {
            return;
        }

        Ref<EntityStore> attackerRef = ((Damage.EntitySource) source).getRef();
        if (attackerRef == null || !attackerRef.isValid()) {
            return;
        }

        Player attacker = commandBuffer.getComponent(attackerRef, Player.getComponentType());
        if (attacker == null) {
            return;
        }

        RpgStats stats = commandBuffer.getComponent(attackerRef, rpgStatsType);
        if (stats == null) {
            return;
        }

        double base = config == null ? 10.0 : config.getDamageMultiplierBase();
        if (base <= 0.0) {
            base = 10.0;
        }
        float multiplier = (float) (stats.getStr() / base);
        if (multiplier <= 0f) {
            return;
        }

        float newAmount = damage.getAmount() * multiplier;
        damage.setAmount(newAmount);
    }
}
