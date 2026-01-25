package com.bsnacks.rpgstats.ui;

import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.utils.HudHelper;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public final class RpgStatsHud extends CustomUIHud {

    private static final String HUD_PATH = "Hud/RpgStatsHud.ui";
    private final ComponentType<EntityStore, RpgStats> rpgStatsType;

    public RpgStatsHud(PlayerRef playerRef, ComponentType<EntityStore, RpgStats> rpgStatsType) {
        super(playerRef);
        this.rpgStatsType = rpgStatsType;
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
}
