package com.bsnacks.rpgstats.utils;

import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public final class HudHelper {

    private static final Logger LOGGER = Logger.getLogger(HudHelper.class.getName());
    private static final PluginIdentifier MULTIPLE_HUD_ID = new PluginIdentifier("Buuz135", "MultipleHUD");
    public static final String RPGSTATS_HUD_ID = "rpgstats_hud";
    private static Boolean multipleHudAvailable = null;

    // Track our own HUD instances per player
    private static final Map<UUID, CustomUIHud> playerHuds = new ConcurrentHashMap<>();

    // Cached reflection objects for MultipleHUD
    private static Object multipleHudInstance = null;
    private static Method setCustomHudMethod = null;
    private static Method hideCustomHudMethod = null;

    private HudHelper() {
    }

    private static boolean checkMultipleHudAvailable() {
        if (multipleHudAvailable == null) {
            try {
                PluginBase multipleHudPlugin = PluginManager.get().getPlugin(MULTIPLE_HUD_ID);
                if (multipleHudPlugin != null) {
                    // Try to get the MultipleHUD instance via reflection
                    Class<?> multipleHudClass = Class.forName("com.buuz135.mhud.MultipleHUD");
                    Method getInstanceMethod = multipleHudClass.getMethod("getInstance");
                    multipleHudInstance = getInstanceMethod.invoke(null);

                    // Cache the methods we need
                    setCustomHudMethod = multipleHudClass.getMethod("setCustomHud",
                            Player.class, PlayerRef.class, String.class, CustomUIHud.class);
                    hideCustomHudMethod = multipleHudClass.getMethod("hideCustomHud",
                            Player.class, PlayerRef.class, String.class);

                    multipleHudAvailable = true;
                    LOGGER.info("[RPGStats] MultipleHUD detected - using multiple HUD support");
                } else {
                    multipleHudAvailable = false;
                    LOGGER.info("[RPGStats] MultipleHUD not found - using standard HUD mode");
                }
            } catch (Exception ex) {
                multipleHudAvailable = false;
                LOGGER.warning("[RPGStats] Error checking for MultipleHUD: " + ex.getMessage() + " - using standard HUD mode");
            }
        }
        return multipleHudAvailable;
    }

    public static boolean isMultipleHudAvailable() {
        return checkMultipleHudAvailable();
    }

    public static void setCustomHud(Player player, PlayerRef playerRef, CustomUIHud customHud) {
        UUID playerUuid = playerRef.getUuid();
        playerHuds.put(playerUuid, customHud);

        if (checkMultipleHudAvailable()) {
            try {
                setCustomHudMethod.invoke(multipleHudInstance, player, playerRef, RPGSTATS_HUD_ID, customHud);
            } catch (Exception ex) {
                LOGGER.warning("[RPGStats] Failed to set HUD via MultipleHUD: " + ex.getMessage());
                player.getHudManager().setCustomHud(playerRef, customHud);
            }
        } else {
            player.getHudManager().setCustomHud(playerRef, customHud);
        }
    }

    public static void hideCustomHud(Player player, PlayerRef playerRef) {
        UUID playerUuid = playerRef.getUuid();
        playerHuds.remove(playerUuid);

        if (checkMultipleHudAvailable()) {
            try {
                hideCustomHudMethod.invoke(multipleHudInstance, player, playerRef, RPGSTATS_HUD_ID);
            } catch (Exception ex) {
                LOGGER.warning("[RPGStats] Failed to hide HUD via MultipleHUD: " + ex.getMessage());
            }
        } else {
            var existingHud = player.getHudManager().getCustomHud();
            if (existingHud != null && existingHud.getClass().getName().contains("RpgStatsHud")) {
                player.getHudManager().setCustomHud(playerRef, null);
            }
        }
    }

    /**
     * Gets the RPGStats HUD for a player if it's currently active.
     * With MultipleHUD: Returns our tracked HUD since multiple HUDs can coexist.
     * Without MultipleHUD: Returns the HUD only if it's an RpgStatsHud instance.
     */
    public static CustomUIHud getCustomHud(Player player, PlayerRef playerRef) {
        if (checkMultipleHudAvailable()) {
            // With MultipleHUD, our HUD coexists with others - return our tracked instance
            return playerHuds.get(playerRef.getUuid());
        } else {
            // Without MultipleHUD, only return if our HUD is the active one
            var currentHud = player.getHudManager().getCustomHud();
            if (currentHud != null && currentHud.getClass().getName().contains("RpgStatsHud")) {
                return currentHud;
            }
            return null;
        }
    }

    /**
     * Called when a player disconnects to clean up tracked HUD references.
     */
    public static void removePlayer(UUID playerUuid) {
        playerHuds.remove(playerUuid);
    }
}
