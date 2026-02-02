package com.bsnacks.rpgstats.ui;

import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;
import com.bsnacks.rpgstats.party.Party;
import com.bsnacks.rpgstats.party.PartyService;
import com.bsnacks.rpgstats.utils.HudHelper;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class RpgStatsHud extends CustomUIHud {

    private static final String HUD_PATH = "Hud/RpgStatsHud.ui";
    private static final String PARTY_ENTRY_PATH = "Hud/PartyHudMemberEntry.ui";
    private static final String PARTY_CONTAINER = "#PartyHudContainer";
    private static final String PARTY_LIST = "#PartyHudList";
    private static final int DEFAULT_PARTY_HUD_OFFSET_X = 20;
    private static final int DEFAULT_PARTY_HUD_OFFSET_Y = 20;
    private final ComponentType<EntityStore, RpgStats> rpgStatsType;
    private final RpgStatsConfig config;
    private final PartyService partyService;
    private final PlayerRef playerRef;

    public RpgStatsHud(PlayerRef playerRef, ComponentType<EntityStore, RpgStats> rpgStatsType,
                       RpgStatsConfig config, PartyService partyService) {
        super(playerRef);
        this.playerRef = playerRef;
        this.rpgStatsType = rpgStatsType;
        this.config = config;
        this.partyService = partyService;
    }

    @Override
    protected void build(UICommandBuilder uiCommandBuilder) {
        uiCommandBuilder.append(HUD_PATH);
    }

    public void refresh(Ref<EntityStore> ref, Store<EntityStore> store) {
        if (ref == null || !ref.isValid() || store == null) {
            return;
        }
        UICommandBuilder builder = new UICommandBuilder();
        applyState(ref, store, builder);
        applyPartyState(ref, store, builder);
        update(false, builder);
    }

    private void applyState(Ref<EntityStore> ref, Store<EntityStore> store, UICommandBuilder uiCommandBuilder) {
        RpgStats stats = store.ensureAndGetComponent(ref, rpgStatsType);
        stats.migrateIfNeeded();
        applyState(stats, uiCommandBuilder);
    }

    private static void applyState(RpgStats stats, UICommandBuilder uiCommandBuilder) {
        if (stats == null) {
            return;
        }

        int level = stats.getLevel();
        long xpInto = stats.getXpIntoLevel();
        long xpToNext = stats.getXpToNextLevel();
        long xpTotal = xpToNext == 0L ? xpInto : xpInto + xpToNext;
        float progress = xpTotal <= 0L ? 0.0f : (float) xpInto / (float) xpTotal;
        if (xpToNext == 0L) {
            progress = 1.0f;
        }

        uiCommandBuilder.set("#XpHudLevel.Text", "LV " + level);
        uiCommandBuilder.set("#XpHudText.Text", xpToNext == 0L
                ? "XP MAX"
                : "XP " + xpInto + "/" + xpTotal);
        uiCommandBuilder.set("#XpHudBar.Value", progress);
    }

    public void refreshParty(Ref<EntityStore> ref, Store<EntityStore> store) {
        if (ref == null || !ref.isValid() || store == null) {
            return;
        }
        UICommandBuilder builder = new UICommandBuilder();
        applyPartyState(ref, store, builder);
        update(false, builder);
    }

    private void applyPartyState(Ref<EntityStore> ref, Store<EntityStore> store, UICommandBuilder uiCommandBuilder) {
        uiCommandBuilder.set(PARTY_CONTAINER + ".Visible", false);
        uiCommandBuilder.clear(PARTY_LIST);
        if (config != null && (!config.isHudEnabled() || !config.isPartyHudEnabled() || !config.isPartyEnabled())) {
            return;
        }
        if (partyService == null || playerRef == null) {
            return;
        }
        UUID selfUuid = playerRef.getUuid();
        if (selfUuid == null) {
            return;
        }
        Party party = partyService.getPartyFor(selfUuid);
        if (party == null) {
            return;
        }
        List<PlayerRef> onlineMembers = getOnlinePartyMembers(party);
        if (onlineMembers.isEmpty()) {
            return;
        }

        applyPartyHudPosition(uiCommandBuilder);
        uiCommandBuilder.set(PARTY_CONTAINER + ".Visible", true);

        int radiusBlocks = config == null ? 0 : config.getPartyXpShareRadiusBlocks();
        double radiusSq = radiusBlocks > 0 ? (double) radiusBlocks * radiusBlocks : -1.0;
        Vector3d selfPos = readPosition(playerRef, store);

        for (int i = 0; i < onlineMembers.size(); i++) {
            PlayerRef memberRef = onlineMembers.get(i);
            uiCommandBuilder.append(PARTY_LIST, PARTY_ENTRY_PATH);
            String selector = PARTY_LIST + "[" + i + "]";
            String name = memberRef.getUsername();
            if (name == null || name.isBlank()) {
                name = "Player";
            }
            uiCommandBuilder.set(selector + " #PartyHudMemberName.Text", name);

            Integer level = readLevel(memberRef, store);
            uiCommandBuilder.set(selector + " #PartyHudMemberLevel.Text", level == null ? "LV ?" : "LV " + level);

            HealthSnapshot health = readHealth(memberRef, store);
            if (health.valid) {
                int percent = Math.max(0, Math.min(100, Math.round(health.percent * 100.0f)));
                uiCommandBuilder.set(selector + " #PartyHudMemberHealthBar.Value", health.percent);
                uiCommandBuilder.set(selector + " #PartyHudMemberHealthPct.Text", "HP " + percent + "%");
            } else {
                uiCommandBuilder.set(selector + " #PartyHudMemberHealthBar.Value", 0.0f);
                uiCommandBuilder.set(selector + " #PartyHudMemberHealthPct.Text", "HP ?%");
            }

            boolean outOfRange = isOutOfRange(selfPos, memberRef, store, radiusSq);
            uiCommandBuilder.set(selector + " #PartyHudMemberRange.Visible", outOfRange);
        }
    }

    private void applyPartyHudPosition(UICommandBuilder uiCommandBuilder) {
        int offsetX = config == null ? DEFAULT_PARTY_HUD_OFFSET_X : config.getPartyHudOffsetX();
        int offsetY = config == null ? DEFAULT_PARTY_HUD_OFFSET_Y : config.getPartyHudOffsetY();
        uiCommandBuilder.set(PARTY_CONTAINER + ".Anchor.Left", offsetX);
        uiCommandBuilder.set(PARTY_CONTAINER + ".Anchor.Top", offsetY);
    }

    private List<PlayerRef> getOnlinePartyMembers(Party party) {
        if (party == null) {
            return List.of();
        }
        Universe universe = Universe.get();
        if (universe == null) {
            return List.of();
        }
        List<PlayerRef> members = new ArrayList<>();
        for (UUID memberUuid : party.getMembers()) {
            if (memberUuid == null) {
                continue;
            }
            PlayerRef ref = universe.getPlayer(memberUuid);
            if (ref != null && ref.isValid()) {
                members.add(ref);
            }
        }
        return members;
    }

    private Integer readLevel(PlayerRef targetRef, Store<EntityStore> currentStore) {
        if (targetRef == null || !targetRef.isValid()) {
            return null;
        }
        Ref<EntityStore> targetEntity = targetRef.getReference();
        if (targetEntity == null || !targetEntity.isValid()) {
            return null;
        }
        Store<EntityStore> targetStore = targetEntity.getStore();
        if (targetStore == null || (currentStore != null && targetStore != currentStore)) {
            return null;
        }
        RpgStats stats = targetStore.getComponent(targetEntity, rpgStatsType);
        if (stats == null) {
            return null;
        }
        stats.migrateIfNeeded();
        return stats.getLevel();
    }

    private HealthSnapshot readHealth(PlayerRef targetRef, Store<EntityStore> currentStore) {
        if (targetRef == null || !targetRef.isValid()) {
            return HealthSnapshot.invalid();
        }
        Ref<EntityStore> targetEntity = targetRef.getReference();
        if (targetEntity == null || !targetEntity.isValid()) {
            return HealthSnapshot.invalid();
        }
        Store<EntityStore> targetStore = targetEntity.getStore();
        if (targetStore == null || (currentStore != null && targetStore != currentStore)) {
            return HealthSnapshot.invalid();
        }
        EntityStatMap statMap = targetStore.getComponent(targetEntity, EntityStatMap.getComponentType());
        if (statMap == null) {
            return HealthSnapshot.invalid();
        }
        EntityStatValue healthStat = statMap.get(DefaultEntityStatTypes.getHealth());
        if (healthStat == null) {
            return HealthSnapshot.invalid();
        }
        float max = healthStat.getMax();
        if (max <= 0.0f) {
            return HealthSnapshot.invalid();
        }
        float current = healthStat.get();
        float percent = Math.max(0.0f, Math.min(1.0f, current / max));
        return new HealthSnapshot(current, max, percent, true);
    }

    private Vector3d readPosition(PlayerRef targetRef, Store<EntityStore> currentStore) {
        if (targetRef == null || !targetRef.isValid()) {
            return null;
        }
        Ref<EntityStore> targetEntity = targetRef.getReference();
        if (targetEntity == null || !targetEntity.isValid()) {
            return null;
        }
        Store<EntityStore> targetStore = targetEntity.getStore();
        if (targetStore == null || (currentStore != null && targetStore != currentStore)) {
            return null;
        }
        TransformComponent transform = targetStore.getComponent(targetEntity, TransformComponent.getComponentType());
        if (transform == null) {
            return null;
        }
        return transform.getPosition();
    }

    private boolean isOutOfRange(Vector3d selfPos, PlayerRef memberRef, Store<EntityStore> store, double radiusSq) {
        if (radiusSq <= 0.0) {
            return false;
        }
        if (selfPos == null) {
            return false;
        }
        Vector3d memberPos = readPosition(memberRef, store);
        if (memberPos == null) {
            return true;
        }
        double dx = selfPos.getX() - memberPos.getX();
        double dy = selfPos.getY() - memberPos.getY();
        double dz = selfPos.getZ() - memberPos.getZ();
        double distSq = dx * dx + dy * dy + dz * dz;
        return distSq > radiusSq;
    }

    public void refresh(RpgStats stats) {
        if (stats == null) {
            return;
        }
        UICommandBuilder builder = new UICommandBuilder();
        applyState(stats, builder);
        update(false, builder);
    }

    public static void refreshIfActive(Ref<EntityStore> ref, Store<EntityStore> store) {
        if (ref == null || !ref.isValid() || store == null) {
            return;
        }
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }
        refreshIfActive(player, ref, store);
    }

    /**
     * Refresh HUD using Player, Ref, and Store. Properly checks for MultipleHUD support.
     */
    public static void refreshIfActive(Player player, Ref<EntityStore> ref, Store<EntityStore> store) {
        if (player == null || ref == null || !ref.isValid() || store == null) {
            return;
        }
        var holder = player.toHolder();
        PlayerRef playerRef = holder.getComponent(PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }
        var customHud = HudHelper.getCustomHud(player, playerRef);
        if (customHud instanceof RpgStatsHud rpgStatsHud) {
            rpgStatsHud.refresh(ref, store);
        }
    }

    public static void refreshPartyIfActive(Player player, Ref<EntityStore> ref, Store<EntityStore> store) {
        if (player == null || ref == null || !ref.isValid() || store == null) {
            return;
        }
        var holder = player.toHolder();
        PlayerRef playerRef = holder.getComponent(PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }
        var customHud = HudHelper.getCustomHud(player, playerRef);
        if (customHud instanceof RpgStatsHud rpgStatsHud) {
            rpgStatsHud.refreshParty(ref, store);
        }
    }

    /**
     * Safe to call from inside a system. Uses the already-fetched Player and RpgStats
     * without calling any store methods.
     */
    public static void refreshIfActive(Player player, RpgStats stats) {
        if (player == null || stats == null) {
            return;
        }
        var holder = player.toHolder();
        PlayerRef playerRef = holder.getComponent(PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }
        var customHud = HudHelper.getCustomHud(player, playerRef);
        if (customHud instanceof RpgStatsHud rpgStatsHud) {
            rpgStatsHud.refresh(stats);
        }
    }

    private static final class HealthSnapshot {
        private final float current;
        private final float max;
        private final float percent;
        private final boolean valid;

        private HealthSnapshot(float current, float max, float percent, boolean valid) {
            this.current = current;
            this.max = max;
            this.percent = percent;
            this.valid = valid;
        }

        private static HealthSnapshot invalid() {
            return new HealthSnapshot(0.0f, 0.0f, 0.0f, false);
        }
    }
}
