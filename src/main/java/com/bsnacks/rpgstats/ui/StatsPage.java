package com.bsnacks.rpgstats.ui;

import com.bsnacks.rpgstats.RpgStatsPlugin;
import com.bsnacks.rpgstats.components.RpgStats;
import com.bsnacks.rpgstats.config.RpgStatsConfig;
import com.bsnacks.rpgstats.permissions.RpgStatsPermissions;
import com.bsnacks.rpgstats.systems.ConstitutionHealthEffect;
import com.bsnacks.rpgstats.systems.EnduranceStaminaEffect;
import com.bsnacks.rpgstats.systems.IntellectManaEffect;
import com.bsnacks.rpgstats.systems.LightFootSpeedEffect;
import com.bsnacks.rpgstats.systems.ArmorProficiencySystem;
import com.bsnacks.rpgstats.systems.GlancingBlowSystem;
import com.bsnacks.rpgstats.systems.StrongLungsOxygenEffect;
import com.bsnacks.rpgstats.systems.LuckyShotSystem;
import com.bsnacks.rpgstats.systems.CriticalStrikeSystem;
import com.bsnacks.rpgstats.systems.LifestealSystem;
import com.bsnacks.rpgstats.systems.ThornsSystem;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Locale;

public final class StatsPage extends InteractiveCustomUIPage<StatsPage.StatsPageEventData> {

    private static final String PAGE_PATH = "Pages/RpgStatsPage.ui";
    private static final String TAB_STATS = "Stats";
    private static final String TAB_ABILITIES = "Abilities";
    private static final String TAB_RESET = "Reset";
    private static final String ACTION_TAB_SWITCH = "TabSwitch";
    private static final String ACTION_SPEND_STAT = "SpendStat";
    private static final String ACTION_SPEND_ABILITY = "SpendAbility";
    private static final String ACTION_RESET_STATS = "ResetStats";
    private static final String ABILITY_LIGHT_FOOT = "light_foot";
    private static final String ABILITY_ARMOR_PROFICIENCY = "armor_proficiency";
    private static final String ABILITY_GLANCING_BLOW = "glancing_blow";
    private static final String ABILITY_HEALTH_REGEN = "health_regen";
    private static final String ABILITY_STAMINA_REGEN = "stamina_regen";
    private static final String ABILITY_STRONG_LUNGS = "strong_lungs";
    private static final String ABILITY_LUCKY_SHOT = "lucky_shot";
    private static final String ABILITY_CRITICAL_STRIKE = "critical_strike";
    private static final String ABILITY_LIFESTEAL = "lifesteal";
    private static final String ABILITY_THORNS = "thorns";
    private static final int DEFAULT_STAT_CAP = 25;
    private static final double BASE_REGEN_PER_SEC = 1.0;

    private static final Value<String> TAB_STYLE_ACTIVE = Value.ref("Common.ui", "DefaultTextButtonStyle");
    private static final Value<String> TAB_STYLE_INACTIVE = Value.ref("Common.ui", "SecondaryTextButtonStyle");
    private final ComponentType<EntityStore, RpgStats> rpgStatsType;
    private final RpgStatsConfig config;
    private final RpgStatsPlugin plugin;
    private String activeTab = TAB_STATS;

    public StatsPage(PlayerRef playerRef, ComponentType<EntityStore, RpgStats> rpgStatsType,
                     RpgStatsConfig config, RpgStatsPlugin plugin) {
        super(playerRef, CustomPageLifetime.CanDismiss, StatsPageEventData.CODEC);
        this.rpgStatsType = rpgStatsType;
        this.config = config;
        this.plugin = plugin;
    }

    @Override
    public void build(Ref<EntityStore> ref, UICommandBuilder uiCommandBuilder,
                      UIEventBuilder uiEventBuilder, Store<EntityStore> store) {
        uiCommandBuilder.append(PAGE_PATH);
        bindEvents(uiEventBuilder);
        Player player = store.getComponent(ref, Player.getComponentType());
        applyState(ref, store, player, uiCommandBuilder);
    }

    @Override
    public void handleDataEvent(Ref<EntityStore> ref, Store<EntityStore> store, StatsPageEventData eventData) {
        if (eventData == null || eventData.type == null) {
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        String type = eventData.type;
        if (ACTION_TAB_SWITCH.equalsIgnoreCase(type)) {
            handleTabSwitch(ref, store, player, eventData.tab);
            return;
        }
        if (ACTION_SPEND_STAT.equalsIgnoreCase(type)) {
            handleSpendStat(ref, store, player, eventData.stat);
            return;
        }
        if (ACTION_SPEND_ABILITY.equalsIgnoreCase(type)) {
            handleSpendAbility(ref, store, player, eventData.ability);
            return;
        }
        if (ACTION_RESET_STATS.equalsIgnoreCase(type)) {
            handleResetStats(ref, store, player);
        }
    }

    private void bindEvents(UIEventBuilder uiEventBuilder) {
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#TabStats",
                new EventData()
                        .append(StatsPageEventData.KEY_TYPE, ACTION_TAB_SWITCH)
                        .append(StatsPageEventData.KEY_TAB, TAB_STATS));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#TabAbilities",
                new EventData()
                        .append(StatsPageEventData.KEY_TYPE, ACTION_TAB_SWITCH)
                        .append(StatsPageEventData.KEY_TAB, TAB_ABILITIES));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#TabReset",
                new EventData()
                        .append(StatsPageEventData.KEY_TYPE, ACTION_TAB_SWITCH)
                        .append(StatsPageEventData.KEY_TAB, TAB_RESET));

        bindStatButton(uiEventBuilder, "#AddStr", "str");
        bindStatButton(uiEventBuilder, "#AddDex", "dex");
        bindStatButton(uiEventBuilder, "#AddCon", "con");
        bindStatButton(uiEventBuilder, "#AddInt", "int");
        bindStatButton(uiEventBuilder, "#AddEnd", "end");
        bindStatButton(uiEventBuilder, "#AddCha", "cha");
        bindAbilityButton(uiEventBuilder, "#LightFootUpgrade", ABILITY_LIGHT_FOOT);
        bindAbilityButton(uiEventBuilder, "#ArmorProficiencyUpgrade", ABILITY_ARMOR_PROFICIENCY);
        bindAbilityButton(uiEventBuilder, "#GlancingBlowUpgrade", ABILITY_GLANCING_BLOW);
        bindAbilityButton(uiEventBuilder, "#HealthRegenUpgrade", ABILITY_HEALTH_REGEN);
        bindAbilityButton(uiEventBuilder, "#StaminaRegenUpgrade", ABILITY_STAMINA_REGEN);
        bindAbilityButton(uiEventBuilder, "#StrongLungsUpgrade", ABILITY_STRONG_LUNGS);
        bindAbilityButton(uiEventBuilder, "#LuckyShotUpgrade", ABILITY_LUCKY_SHOT);
        bindAbilityButton(uiEventBuilder, "#CriticalStrikeUpgrade", ABILITY_CRITICAL_STRIKE);
        bindAbilityButton(uiEventBuilder, "#LifestealUpgrade", ABILITY_LIFESTEAL);
        bindAbilityButton(uiEventBuilder, "#ThornsUpgrade", ABILITY_THORNS);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ResetStatsButton",
                new EventData()
                        .append(StatsPageEventData.KEY_TYPE, ACTION_RESET_STATS));
    }

    private void bindStatButton(UIEventBuilder uiEventBuilder, String buttonId, String statKey) {
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, buttonId,
                new EventData()
                        .append(StatsPageEventData.KEY_TYPE, ACTION_SPEND_STAT)
                        .append(StatsPageEventData.KEY_STAT, statKey));
    }

    private void bindAbilityButton(UIEventBuilder uiEventBuilder, String buttonId, String abilityId) {
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, buttonId,
                new EventData()
                        .append(StatsPageEventData.KEY_TYPE, ACTION_SPEND_ABILITY)
                        .append(StatsPageEventData.KEY_ABILITY, abilityId));
    }

    private void handleTabSwitch(Ref<EntityStore> ref, Store<EntityStore> store, Player player, String tab) {
        if (tab == null || tab.isBlank()) {
            return;
        }
        if (tab.equalsIgnoreCase(activeTab)) {
            return;
        }
        activeTab = tab;
        refreshUI(ref, store, player);
    }

    private void handleSpendStat(Ref<EntityStore> ref, Store<EntityStore> store, Player player, String statRaw) {
        if (player == null) {
            return;
        }

        if (!player.hasPermission(RpgStatsPermissions.STATS_ADD)) {
            player.sendMessage(Message.raw("You do not have permission to spend stat points."));
            refreshUI(ref, store, player);
            return;
        }

        RpgStats stats = store.ensureAndGetComponent(ref, rpgStatsType);
        stats.migrateIfNeeded();
        if (stats.getAvailableStatPoints() <= 0) {
            player.sendMessage(Message.raw("You do not have any stat points to spend."));
            refreshUI(ref, store, player);
            return;
        }

        String attribute = normalizeAttribute(statRaw);
        if (attribute == null) {
            player.sendMessage(Message.raw("Unknown attribute '" + statRaw + "'. Try: str, dex, con, int, end, cha."));
            refreshUI(ref, store, player);
            return;
        }

        int cap = getStatCap(attribute);
        int current = getStatValue(stats, attribute);
        if (current >= cap) {
            player.sendMessage(Message.raw(attribute.toUpperCase(Locale.ROOT) + " cap for server is set to " + cap + "."));
            refreshUI(ref, store, player);
            return;
        }

        if (!stats.spendStatPoint(attribute)) {
            player.sendMessage(Message.raw("Unknown attribute '" + statRaw + "'. Try: str, dex, con, int, end, cha."));
            refreshUI(ref, store, player);
            return;
        }

        EntityStatMap statMap = store.ensureAndGetComponent(ref, EntityStatMap.getComponentType());
        ConstitutionHealthEffect.apply(statMap, stats, config);
        IntellectManaEffect.apply(statMap, stats, config);
        EnduranceStaminaEffect.apply(statMap, stats, config);

        player.sendMessage(Message.raw("Added 1 point to " + attribute.toUpperCase(Locale.ROOT)
                + ". Remaining points: " + stats.getAvailableStatPoints() + "."));
        if (plugin != null) {
            plugin.logInfo("Player spent a point on " + attribute + ": " + player.getDisplayName());
        }
        refreshUI(ref, store, player);
    }

    private void handleSpendAbility(Ref<EntityStore> ref, Store<EntityStore> store, Player player, String abilityId) {
        if (player == null) {
            return;
        }

        if (!player.hasPermission(RpgStatsPermissions.STATS_ADD)) {
            player.sendMessage(Message.raw("You do not have permission to spend ability points."));
            refreshUI(ref, store, player);
            return;
        }

        RpgStats stats = store.ensureAndGetComponent(ref, rpgStatsType);
        stats.migrateIfNeeded();
        if (ABILITY_LIGHT_FOOT.equalsIgnoreCase(abilityId)) {
            int currentLevel = stats.getLightFootLevel();
            int cost = RpgStats.getAbilityUpgradeCost(currentLevel, RpgStats.LIGHT_FOOT_MAX_LEVEL);
            int available = stats.getAvailableAbilityPoints();
            if (cost == 0) {
                player.sendMessage(Message.raw("Light Foot is already at max level."));
                refreshUI(ref, store, player);
                return;
            }
            if (available < cost) {
                player.sendMessage(Message.raw("You need " + cost + " ability point"
                        + (cost == 1 ? "" : "s") + " to upgrade Light Foot."));
                refreshUI(ref, store, player);
                return;
            }
            if (!stats.upgradeLightFoot()) {
                player.sendMessage(Message.raw("Light Foot is already at max level."));
                refreshUI(ref, store, player);
                return;
            }

            LightFootSpeedEffect.apply(ref, store, player, stats, config, plugin);
            int level = stats.getLightFootLevel();
            int percent = Math.round(LightFootSpeedEffect.getSpeedBonus(level, config) * 100.0f);
            player.sendMessage(Message.raw("Light Foot upgraded to level " + level + " (+" + percent + "% speed)."
                    + " Remaining ability points: " + stats.getAvailableAbilityPoints() + "."));
            if (plugin != null) {
                plugin.logInfo("Player upgraded Light Foot to " + level + ": " + player.getDisplayName());
            }
            refreshUI(ref, store, player);
            return;
        }

        if (ABILITY_ARMOR_PROFICIENCY.equalsIgnoreCase(abilityId)) {
            int currentLevel = stats.getArmorProficiencyLevel();
            int cost = RpgStats.getAbilityUpgradeCost(currentLevel, RpgStats.ARMOR_PROFICIENCY_MAX_LEVEL);
            int available = stats.getAvailableAbilityPoints();
            if (cost == 0) {
                player.sendMessage(Message.raw("Armor Proficiency is already at max level."));
                refreshUI(ref, store, player);
                return;
            }
            if (available < cost) {
                player.sendMessage(Message.raw("You need " + cost + " ability point"
                        + (cost == 1 ? "" : "s") + " to upgrade Armor Proficiency."));
                refreshUI(ref, store, player);
                return;
            }
            if (!stats.upgradeArmorProficiency()) {
                player.sendMessage(Message.raw("Armor Proficiency is already at max level."));
                refreshUI(ref, store, player);
                return;
            }

            int level = stats.getArmorProficiencyLevel();
            int percent = Math.round(ArmorProficiencySystem.getResistanceBonus(level, config) * 100.0f);
            player.sendMessage(Message.raw("Armor Proficiency upgraded to level " + level + " (+" + percent + "% armor)."
                    + " Remaining ability points: " + stats.getAvailableAbilityPoints() + "."));
            if (plugin != null) {
                plugin.logInfo("Player upgraded Armor Proficiency to " + level + ": " + player.getDisplayName());
            }
            refreshUI(ref, store, player);
            return;
        }

        if (ABILITY_GLANCING_BLOW.equalsIgnoreCase(abilityId)) {
            int currentLevel = stats.getGlancingBlowLevel();
            int cost = RpgStats.getAbilityUpgradeCost(currentLevel, RpgStats.GLANCING_BLOW_MAX_LEVEL);
            int available = stats.getAvailableAbilityPoints();
            if (cost == 0) {
                player.sendMessage(Message.raw("Glancing Blow is already at max level."));
                refreshUI(ref, store, player);
                return;
            }
            if (available < cost) {
                player.sendMessage(Message.raw("You need " + cost + " ability point"
                        + (cost == 1 ? "" : "s") + " to upgrade Glancing Blow."));
                refreshUI(ref, store, player);
                return;
            }
            if (!stats.upgradeGlancingBlow()) {
                player.sendMessage(Message.raw("Glancing Blow is already at max level."));
                refreshUI(ref, store, player);
                return;
            }

            int level = stats.getGlancingBlowLevel();
            int percent = Math.round(GlancingBlowSystem.getDodgeChance(level, config));
            player.sendMessage(Message.raw("Glancing Blow upgraded to level " + level + " (" + percent + "% dodge)."
                    + " Remaining ability points: " + stats.getAvailableAbilityPoints() + "."));
            if (plugin != null) {
                plugin.logInfo("Player upgraded Glancing Blow to " + level + ": " + player.getDisplayName());
            }
            refreshUI(ref, store, player);
            return;
        }

        if (ABILITY_HEALTH_REGEN.equalsIgnoreCase(abilityId)) {
            int currentLevel = stats.getHealthRegenLevel();
            int cost = RpgStats.getAbilityUpgradeCost(currentLevel, RpgStats.HEALTH_REGEN_MAX_LEVEL);
            int available = stats.getAvailableAbilityPoints();
            if (cost == 0) {
                player.sendMessage(Message.raw("Health Regeneration is already at max level."));
                refreshUI(ref, store, player);
                return;
            }
            if (available < cost) {
                player.sendMessage(Message.raw("You need " + cost + " ability point"
                        + (cost == 1 ? "" : "s") + " to upgrade Health Regeneration."));
                refreshUI(ref, store, player);
                return;
            }
            if (!stats.upgradeHealthRegen()) {
                player.sendMessage(Message.raw("Health Regeneration is already at max level."));
                refreshUI(ref, store, player);
                return;
            }
            int level = stats.getHealthRegenLevel();
            double perLevel = config == null ? 1.0 : config.getHealthRegenPerLevelPerSec();
            double totalPerSec = BASE_REGEN_PER_SEC + perLevel * level;
            player.sendMessage(Message.raw("Health Regeneration upgraded to level " + level
                    + " (" + formatRate(totalPerSec) + "/s)."
                    + " Remaining ability points: " + stats.getAvailableAbilityPoints() + "."));
            if (plugin != null) {
                plugin.logInfo("Player upgraded Health Regeneration to " + level + ": " + player.getDisplayName());
            }
            refreshUI(ref, store, player);
            return;
        }

        if (ABILITY_STAMINA_REGEN.equalsIgnoreCase(abilityId)) {
            int currentLevel = stats.getStaminaRegenLevel();
            int cost = RpgStats.getAbilityUpgradeCost(currentLevel, RpgStats.STAMINA_REGEN_MAX_LEVEL);
            int available = stats.getAvailableAbilityPoints();
            if (cost == 0) {
                player.sendMessage(Message.raw("Stamina Regeneration is already at max level."));
                refreshUI(ref, store, player);
                return;
            }
            if (available < cost) {
                player.sendMessage(Message.raw("You need " + cost + " ability point"
                        + (cost == 1 ? "" : "s") + " to upgrade Stamina Regeneration."));
                refreshUI(ref, store, player);
                return;
            }
            if (!stats.upgradeStaminaRegen()) {
                player.sendMessage(Message.raw("Stamina Regeneration is already at max level."));
                refreshUI(ref, store, player);
                return;
            }
            int level = stats.getStaminaRegenLevel();
            double perLevel = config == null ? 1.0 : config.getStaminaRegenPerLevelPerSec();
            double totalPerSec = BASE_REGEN_PER_SEC + perLevel * level;
            player.sendMessage(Message.raw("Stamina Regeneration upgraded to level " + level
                    + " (" + formatRate(totalPerSec) + "/s)."
                    + " Remaining ability points: " + stats.getAvailableAbilityPoints() + "."));
            if (plugin != null) {
                plugin.logInfo("Player upgraded Stamina Regeneration to " + level + ": " + player.getDisplayName());
            }
            refreshUI(ref, store, player);
            return;
        }

        if (ABILITY_STRONG_LUNGS.equalsIgnoreCase(abilityId)) {
            int currentLevel = stats.getStrongLungsLevel();
            int cost = RpgStats.getAbilityUpgradeCost(currentLevel, RpgStats.STRONG_LUNGS_MAX_LEVEL);
            int available = stats.getAvailableAbilityPoints();
            if (cost == 0) {
                player.sendMessage(Message.raw("Strong Lungs is already at max level."));
                refreshUI(ref, store, player);
                return;
            }
            if (available < cost) {
                player.sendMessage(Message.raw("You need " + cost + " ability point"
                        + (cost == 1 ? "" : "s") + " to upgrade Strong Lungs."));
                refreshUI(ref, store, player);
                return;
            }
            if (!stats.upgradeStrongLungs()) {
                player.sendMessage(Message.raw("Strong Lungs is already at max level."));
                refreshUI(ref, store, player);
                return;
            }
            int level = stats.getStrongLungsLevel();
            double perLevel = config == null ? 50.0 : config.getStrongLungsOxygenPerLevelPct();
            double totalPct = perLevel * level;
            player.sendMessage(Message.raw("Strong Lungs upgraded to level " + level
                    + " (+" + formatPercent(totalPct) + "% oxygen)."
                    + " Remaining ability points: " + stats.getAvailableAbilityPoints() + "."));
            if (plugin != null) {
                plugin.logInfo("Player upgraded Strong Lungs to " + level + ": " + player.getDisplayName());
            }
            refreshUI(ref, store, player);
            return;
        }

        if (ABILITY_LUCKY_SHOT.equalsIgnoreCase(abilityId)) {
            int currentLevel = stats.getLuckyShotLevel();
            int cost = RpgStats.getAbilityUpgradeCost(currentLevel, RpgStats.LUCKY_SHOT_MAX_LEVEL);
            int available = stats.getAvailableAbilityPoints();
            if (cost == 0) {
                player.sendMessage(Message.raw("Lucky Shot is already at max level."));
                refreshUI(ref, store, player);
                return;
            }
            if (available < cost) {
                player.sendMessage(Message.raw("You need " + cost + " ability point"
                        + (cost == 1 ? "" : "s") + " to upgrade Lucky Shot."));
                refreshUI(ref, store, player);
                return;
            }
            if (!stats.upgradeLuckyShot()) {
                player.sendMessage(Message.raw("Lucky Shot is already at max level."));
                refreshUI(ref, store, player);
                return;
            }
            int level = stats.getLuckyShotLevel();
            double perLevel = config == null ? 10.0 : config.getLuckyShotChancePerLevelPct();
            double totalPct = perLevel * level;
            player.sendMessage(Message.raw("Lucky Shot upgraded to level " + level
                    + " (" + formatPercent(totalPct) + "% chance)."
                    + " Remaining ability points: " + stats.getAvailableAbilityPoints() + "."));
            if (plugin != null) {
                plugin.logInfo("Player upgraded Lucky Shot to " + level + ": " + player.getDisplayName());
            }
            refreshUI(ref, store, player);
            return;
        }

        if (ABILITY_CRITICAL_STRIKE.equalsIgnoreCase(abilityId)) {
            int currentLevel = stats.getCriticalStrikeLevel();
            int cost = RpgStats.getAbilityUpgradeCost(currentLevel, RpgStats.CRITICAL_STRIKE_MAX_LEVEL);
            int available = stats.getAvailableAbilityPoints();
            if (cost == 0) {
                player.sendMessage(Message.raw("Critical Strike is already at max level."));
                refreshUI(ref, store, player);
                return;
            }
            if (available < cost) {
                player.sendMessage(Message.raw("You need " + cost + " ability point"
                        + (cost == 1 ? "" : "s") + " to upgrade Critical Strike."));
                refreshUI(ref, store, player);
                return;
            }
            if (!stats.upgradeCriticalStrike()) {
                player.sendMessage(Message.raw("Critical Strike is already at max level."));
                refreshUI(ref, store, player);
                return;
            }
            int level = stats.getCriticalStrikeLevel();
            float totalChance = CriticalStrikeSystem.getCriticalChance(level, config);
            double multiplier = config == null ? 1.5 : config.getCriticalStrikeDamageMultiplier();
            player.sendMessage(Message.raw("Critical Strike upgraded to level " + level
                    + " (" + formatPercent(totalChance) + "% chance, " + String.format("%.1fx", multiplier) + " damage)."
                    + " Remaining ability points: " + stats.getAvailableAbilityPoints() + "."));
            if (plugin != null) {
                plugin.logInfo("Player upgraded Critical Strike to " + level + ": " + player.getDisplayName());
            }
            refreshUI(ref, store, player);
            return;
        }

        if (ABILITY_LIFESTEAL.equalsIgnoreCase(abilityId)) {
            int currentLevel = stats.getLifestealLevel();
            int cost = RpgStats.getAbilityUpgradeCost(currentLevel, RpgStats.LIFESTEAL_MAX_LEVEL);
            int available = stats.getAvailableAbilityPoints();
            if (cost == 0) {
                player.sendMessage(Message.raw("Lifesteal is already at max level."));
                refreshUI(ref, store, player);
                return;
            }
            if (available < cost) {
                player.sendMessage(Message.raw("You need " + cost + " ability point"
                        + (cost == 1 ? "" : "s") + " to upgrade Lifesteal."));
                refreshUI(ref, store, player);
                return;
            }
            if (!stats.upgradeLifesteal()) {
                player.sendMessage(Message.raw("Lifesteal is already at max level."));
                refreshUI(ref, store, player);
                return;
            }
            int level = stats.getLifestealLevel();
            float lifestealPct = LifestealSystem.getLifestealPercent(level, config);
            player.sendMessage(Message.raw("Lifesteal upgraded to level " + level
                    + " (" + formatPercent(lifestealPct) + "% of damage healed)."
                    + " Remaining ability points: " + stats.getAvailableAbilityPoints() + "."));
            if (plugin != null) {
                plugin.logInfo("Player upgraded Lifesteal to " + level + ": " + player.getDisplayName());
            }
            refreshUI(ref, store, player);
            return;
        }

        if (ABILITY_THORNS.equalsIgnoreCase(abilityId)) {
            int currentLevel = stats.getThornsLevel();
            int cost = RpgStats.getAbilityUpgradeCost(currentLevel, RpgStats.THORNS_MAX_LEVEL);
            int available = stats.getAvailableAbilityPoints();
            if (cost == 0) {
                player.sendMessage(Message.raw("Thorns is already at max level."));
                refreshUI(ref, store, player);
                return;
            }
            if (available < cost) {
                player.sendMessage(Message.raw("You need " + cost + " ability point"
                        + (cost == 1 ? "" : "s") + " to upgrade Thorns."));
                refreshUI(ref, store, player);
                return;
            }
            if (!stats.upgradeThorns()) {
                player.sendMessage(Message.raw("Thorns is already at max level."));
                refreshUI(ref, store, player);
                return;
            }
            int level = stats.getThornsLevel();
            float thornsPct = ThornsSystem.getThornsReflectPercent(level, config);
            player.sendMessage(Message.raw("Thorns upgraded to level " + level
                    + " (" + formatPercent(thornsPct) + "% damage reflected)."
                    + " Remaining ability points: " + stats.getAvailableAbilityPoints() + "."));
            if (plugin != null) {
                plugin.logInfo("Player upgraded Thorns to " + level + ": " + player.getDisplayName());
            }
            refreshUI(ref, store, player);
            return;
        }

        player.sendMessage(Message.raw("Unknown ability '" + abilityId + "'."));
        refreshUI(ref, store, player);
    }

    private void handleResetStats(Ref<EntityStore> ref, Store<EntityStore> store, Player player) {
        if (player == null) {
            return;
        }

        if (!player.hasPermission(RpgStatsPermissions.STATS_RESET)) {
            player.sendMessage(Message.raw("You do not have permission to reset stats."));
            refreshUI(ref, store, player);
            return;
        }

        RpgStats stats = store.ensureAndGetComponent(ref, rpgStatsType);
        stats.resetToDefaults();
        EntityStatMap statMap = store.ensureAndGetComponent(ref, EntityStatMap.getComponentType());
        ConstitutionHealthEffect.apply(statMap, stats, config);
        IntellectManaEffect.apply(statMap, stats, config);
        EnduranceStaminaEffect.apply(statMap, stats, config);
        LightFootSpeedEffect.apply(ref, store, player, stats, config, plugin);

        player.sendMessage(Message.raw("Your stats have been reset."));
        if (plugin != null) {
            plugin.logInfo("Player reset stats: " + player.getDisplayName());
        }
        refreshUI(ref, store, player);
    }

    private void refreshUI(Ref<EntityStore> ref, Store<EntityStore> store, Player player) {
        UICommandBuilder builder = new UICommandBuilder();
        applyState(ref, store, player, builder);
        sendUpdate(builder);
    }

    private void applyState(Ref<EntityStore> ref, Store<EntityStore> store, Player player,
                            UICommandBuilder uiCommandBuilder) {
        RpgStats stats = store.ensureAndGetComponent(ref, rpgStatsType);
        stats.migrateIfNeeded();

        uiCommandBuilder.set("#StrValue.Text", String.valueOf(stats.getStr()));
        uiCommandBuilder.set("#DexValue.Text", String.valueOf(stats.getDex()));
        uiCommandBuilder.set("#ConValue.Text", String.valueOf(stats.getCon()));
        uiCommandBuilder.set("#IntValue.Text", String.valueOf(stats.getIntl()));
        uiCommandBuilder.set("#EndValue.Text", String.valueOf(stats.getEnd()));
        uiCommandBuilder.set("#ChaValue.Text", String.valueOf(stats.getCha()));

        int level = stats.getLevel();
        int points = stats.getAvailableStatPoints();
        uiCommandBuilder.set("#LevelValue.Text", String.valueOf(level));
        uiCommandBuilder.set("#PointsValue.Text", String.valueOf(points));
        uiCommandBuilder.set("#AbilityPointsValue.Text", String.valueOf(stats.getAvailableAbilityPoints()));

        long xpInto = stats.getXpIntoLevel();
        long xpToNext = stats.getXpToNextLevel();
        long xpTotal = xpToNext == 0L ? xpInto : xpInto + xpToNext;
        float progress = xpTotal <= 0L ? 0.0f : (float) xpInto / (float) xpTotal;
        if (xpToNext == 0L) {
            progress = 1.0f;
        }

        uiCommandBuilder.set("#XpProgressBar.Value", progress);
        uiCommandBuilder.set("#XpText.Text", xpToNext == 0L
                ? "XP MAX"
                : "XP " + xpInto + "/" + xpTotal);

        updateTabVisibility(uiCommandBuilder);
        updateAddButtons(uiCommandBuilder, stats, player);
        updateAbilityButtons(uiCommandBuilder, stats, player);
    }

    private void updateTabVisibility(UICommandBuilder uiCommandBuilder) {
        boolean statsVisible = TAB_STATS.equalsIgnoreCase(activeTab);
        boolean abilitiesVisible = TAB_ABILITIES.equalsIgnoreCase(activeTab);
        boolean resetVisible = TAB_RESET.equalsIgnoreCase(activeTab);
        uiCommandBuilder.set("#StatsContent.Visible", statsVisible);
        uiCommandBuilder.set("#AbilitiesContent.Visible", abilitiesVisible);
        uiCommandBuilder.set("#ResetContent.Visible", resetVisible);
        uiCommandBuilder.set("#TabStats.Style", statsVisible ? TAB_STYLE_ACTIVE : TAB_STYLE_INACTIVE);
        uiCommandBuilder.set("#TabAbilities.Style", abilitiesVisible ? TAB_STYLE_ACTIVE : TAB_STYLE_INACTIVE);
        uiCommandBuilder.set("#TabReset.Style", resetVisible ? TAB_STYLE_ACTIVE : TAB_STYLE_INACTIVE);
    }

    private void updateAddButtons(UICommandBuilder uiCommandBuilder, RpgStats stats, Player player) {
        boolean canSpend = player != null && player.hasPermission(RpgStatsPermissions.STATS_ADD);
        int points = stats.getAvailableStatPoints();

        setAddButtonState(uiCommandBuilder, "#AddStr", canSpend && points > 0 && stats.getStr() < getStatCap("str"));
        setAddButtonState(uiCommandBuilder, "#AddDex", canSpend && points > 0 && stats.getDex() < getStatCap("dex"));
        setAddButtonState(uiCommandBuilder, "#AddCon", canSpend && points > 0 && stats.getCon() < getStatCap("con"));
        setAddButtonState(uiCommandBuilder, "#AddInt", canSpend && points > 0 && stats.getIntl() < getStatCap("int"));
        setAddButtonState(uiCommandBuilder, "#AddEnd", canSpend && points > 0 && stats.getEnd() < getStatCap("end"));
        setAddButtonState(uiCommandBuilder, "#AddCha", canSpend && points > 0 && stats.getCha() < getStatCap("cha"));
    }

    private void updateAbilityButtons(UICommandBuilder uiCommandBuilder, RpgStats stats, Player player) {
        boolean canSpend = player != null && player.hasPermission(RpgStatsPermissions.STATS_ADD);
        int points = stats.getAvailableAbilityPoints();
        int level = stats.getLightFootLevel();
        int percent = Math.round(LightFootSpeedEffect.getSpeedBonus(level, config) * 100.0f);
        uiCommandBuilder.set("#LightFootLevel.Text", "Level " + level + "/" + RpgStats.LIGHT_FOOT_MAX_LEVEL
                + " (+" + percent + "%)");
        double lightFootPerLevel = config == null ? 5.0 : config.getLightFootSpeedPerLevelPct();
        uiCommandBuilder.set("#LightFootDescription.Text", "Move " + formatPercent(lightFootPerLevel) + "%, "
                + formatPercent(lightFootPerLevel * 2.0) + "%, and " + formatPercent(lightFootPerLevel * 3.0)
                + "% faster at levels 1-3.");

        int lightFootCost = RpgStats.getAbilityUpgradeCost(level, RpgStats.LIGHT_FOOT_MAX_LEVEL);
        boolean canUpgrade = canSpend && lightFootCost > 0 && points >= lightFootCost;
        uiCommandBuilder.set("#LightFootUpgrade.HitTestVisible", canUpgrade);
        uiCommandBuilder.set("#LightFootUpgrade.Text", lightFootCost == 0 ? "Maxed" : String.valueOf(lightFootCost));

        int armorProficiencyLevel = stats.getArmorProficiencyLevel();
        int armorProficiencyPercent = Math.round(ArmorProficiencySystem.getResistanceBonus(armorProficiencyLevel, config) * 100.0f);
        uiCommandBuilder.set("#ArmorProficiencyLevel.Text", "Level " + armorProficiencyLevel + "/" + RpgStats.ARMOR_PROFICIENCY_MAX_LEVEL
                + " (+" + armorProficiencyPercent + "%)");
        double armorProficiencyPerLevel = config == null ? 5.0 : config.getArmorProficiencyResistancePerLevelPct();
        uiCommandBuilder.set("#ArmorProficiencyDescription.Text", "While wearing armor, reduce Physical and Projectile damage by "
                + formatPercent(armorProficiencyPerLevel) + "%, " + formatPercent(armorProficiencyPerLevel * 2.0) + "%, and "
                + formatPercent(armorProficiencyPerLevel * 3.0) + "%.");

        int armorProficiencyCost = RpgStats.getAbilityUpgradeCost(armorProficiencyLevel, RpgStats.ARMOR_PROFICIENCY_MAX_LEVEL);
        boolean canUpgradeArmorProficiency = canSpend && armorProficiencyCost > 0 && points >= armorProficiencyCost;
        uiCommandBuilder.set("#ArmorProficiencyUpgrade.HitTestVisible", canUpgradeArmorProficiency);
        uiCommandBuilder.set("#ArmorProficiencyUpgrade.Text", armorProficiencyCost == 0 ? "Maxed" : String.valueOf(armorProficiencyCost));

        int glancingBlowLevel = stats.getGlancingBlowLevel();
        int glancingBlowPercent = Math.round(GlancingBlowSystem.getDodgeChance(glancingBlowLevel, config));
        uiCommandBuilder.set("#GlancingBlowLevel.Text", "Level " + glancingBlowLevel + "/" + RpgStats.GLANCING_BLOW_MAX_LEVEL
                + " (" + glancingBlowPercent + "%)");
        double glancingBlowPerLevel = config == null ? 5.0 : config.getGlancingBlowChancePerLevelPct();
        uiCommandBuilder.set("#GlancingBlowDescription.Text", "Chance to dodge hostile NPC damage: "
                + formatPercent(5.0 + glancingBlowPerLevel) + "%, "
                + formatPercent(5.0 + glancingBlowPerLevel * 2.0) + "%, and "
                + formatPercent(5.0 + glancingBlowPerLevel * 3.0) + "%.");
        int glancingBlowCost = RpgStats.getAbilityUpgradeCost(glancingBlowLevel, RpgStats.GLANCING_BLOW_MAX_LEVEL);
        boolean canUpgradeGlancingBlow = canSpend && glancingBlowCost > 0 && points >= glancingBlowCost;
        uiCommandBuilder.set("#GlancingBlowUpgrade.HitTestVisible", canUpgradeGlancingBlow);
        uiCommandBuilder.set("#GlancingBlowUpgrade.Text", glancingBlowCost == 0 ? "Maxed" : String.valueOf(glancingBlowCost));

        int healthRegenLevel = stats.getHealthRegenLevel();
        double healthRegenPerLevel = config == null ? 1.0 : config.getHealthRegenPerLevelPerSec();
        double healthTotal = BASE_REGEN_PER_SEC + healthRegenPerLevel * healthRegenLevel;
        uiCommandBuilder.set("#HealthRegenLevel.Text", "Level " + healthRegenLevel + "/" + RpgStats.HEALTH_REGEN_MAX_LEVEL
                + " (" + formatRate(healthTotal) + "/s)");
        uiCommandBuilder.set("#HealthRegenDescription.Text", "Regenerate "
                + formatRate(BASE_REGEN_PER_SEC + healthRegenPerLevel) + ", "
                + formatRate(BASE_REGEN_PER_SEC + healthRegenPerLevel * 2.0) + ", and "
                + formatRate(BASE_REGEN_PER_SEC + healthRegenPerLevel * 3.0) + " health per second at levels 1-3.");
        int healthRegenCost = RpgStats.getAbilityUpgradeCost(healthRegenLevel, RpgStats.HEALTH_REGEN_MAX_LEVEL);
        boolean canUpgradeHealthRegen = canSpend && healthRegenCost > 0 && points >= healthRegenCost;
        uiCommandBuilder.set("#HealthRegenUpgrade.HitTestVisible", canUpgradeHealthRegen);
        uiCommandBuilder.set("#HealthRegenUpgrade.Text", healthRegenCost == 0 ? "Maxed" : String.valueOf(healthRegenCost));

        int staminaRegenLevel = stats.getStaminaRegenLevel();
        double staminaRegenPerLevel = config == null ? 1.0 : config.getStaminaRegenPerLevelPerSec();
        double staminaTotal = BASE_REGEN_PER_SEC + staminaRegenPerLevel * staminaRegenLevel;
        uiCommandBuilder.set("#StaminaRegenLevel.Text", "Level " + staminaRegenLevel + "/" + RpgStats.STAMINA_REGEN_MAX_LEVEL
                + " (" + formatRate(staminaTotal) + "/s)");
        uiCommandBuilder.set("#StaminaRegenDescription.Text", "Regenerate "
                + formatRate(BASE_REGEN_PER_SEC + staminaRegenPerLevel) + ", "
                + formatRate(BASE_REGEN_PER_SEC + staminaRegenPerLevel * 2.0) + ", and "
                + formatRate(BASE_REGEN_PER_SEC + staminaRegenPerLevel * 3.0) + " stamina per second at levels 1-3.");
        int staminaRegenCost = RpgStats.getAbilityUpgradeCost(staminaRegenLevel, RpgStats.STAMINA_REGEN_MAX_LEVEL);
        boolean canUpgradeStaminaRegen = canSpend && staminaRegenCost > 0 && points >= staminaRegenCost;
        uiCommandBuilder.set("#StaminaRegenUpgrade.HitTestVisible", canUpgradeStaminaRegen);
        uiCommandBuilder.set("#StaminaRegenUpgrade.Text", staminaRegenCost == 0 ? "Maxed" : String.valueOf(staminaRegenCost));

        int strongLungsLevel = stats.getStrongLungsLevel();
        double strongLungsPerLevel = config == null ? 50.0 : config.getStrongLungsOxygenPerLevelPct();
        double strongLungsTotalPct = strongLungsPerLevel * strongLungsLevel;
        uiCommandBuilder.set("#StrongLungsLevel.Text", "Level " + strongLungsLevel + "/" + RpgStats.STRONG_LUNGS_MAX_LEVEL
                + " (+" + formatPercent(strongLungsTotalPct) + "%)");
        uiCommandBuilder.set("#StrongLungsDescription.Text", "Increase max oxygen by "
                + formatPercent(strongLungsPerLevel) + "%, "
                + formatPercent(strongLungsPerLevel * 2.0) + "%, and "
                + formatPercent(strongLungsPerLevel * 3.0) + "% at levels 1-3.");
        int strongLungsCost = RpgStats.getAbilityUpgradeCost(strongLungsLevel, RpgStats.STRONG_LUNGS_MAX_LEVEL);
        boolean canUpgradeStrongLungs = canSpend && strongLungsCost > 0 && points >= strongLungsCost;
        uiCommandBuilder.set("#StrongLungsUpgrade.HitTestVisible", canUpgradeStrongLungs);
        uiCommandBuilder.set("#StrongLungsUpgrade.Text", strongLungsCost == 0 ? "Maxed" : String.valueOf(strongLungsCost));

        int luckyShotLevel = stats.getLuckyShotLevel();
        double luckyShotPerLevel = config == null ? 10.0 : config.getLuckyShotChancePerLevelPct();
        double luckyShotTotalPct = luckyShotPerLevel * luckyShotLevel;
        uiCommandBuilder.set("#LuckyShotLevel.Text", "Level " + luckyShotLevel + "/" + RpgStats.LUCKY_SHOT_MAX_LEVEL
                + " (" + formatPercent(luckyShotTotalPct) + "%)");
        uiCommandBuilder.set("#LuckyShotDescription.Text", "Chance to not consume ammo with bow/crossbow: "
                + formatPercent(luckyShotPerLevel) + "%, "
                + formatPercent(luckyShotPerLevel * 2.0) + "%, and "
                + formatPercent(luckyShotPerLevel * 3.0) + "% at levels 1-3.");
        int luckyShotCost = RpgStats.getAbilityUpgradeCost(luckyShotLevel, RpgStats.LUCKY_SHOT_MAX_LEVEL);
        boolean canUpgradeLuckyShot = canSpend && luckyShotCost > 0 && points >= luckyShotCost;
        uiCommandBuilder.set("#LuckyShotUpgrade.HitTestVisible", canUpgradeLuckyShot);
        uiCommandBuilder.set("#LuckyShotUpgrade.Text", luckyShotCost == 0 ? "Maxed" : String.valueOf(luckyShotCost));

        int criticalStrikeLevel = stats.getCriticalStrikeLevel();
        float criticalStrikeTotalChance = CriticalStrikeSystem.getCriticalChance(criticalStrikeLevel, config);
        double criticalStrikeDamageMultiplier = config == null ? 1.5 : config.getCriticalStrikeDamageMultiplier();
        uiCommandBuilder.set("#CriticalStrikeLevel.Text", "Level " + criticalStrikeLevel + "/" + RpgStats.CRITICAL_STRIKE_MAX_LEVEL
                + " (" + formatPercent(criticalStrikeTotalChance) + "%)");
        double criticalStrikeBaseChance = config == null ? 5.0 : config.getCriticalStrikeBaseChancePct();
        double criticalStrikePerLevel = config == null ? 5.0 : config.getCriticalStrikeChancePerLevelPct();
        uiCommandBuilder.set("#CriticalStrikeDescription.Text", "Chance to deal " + String.format("%.1fx", criticalStrikeDamageMultiplier)
                + " damage: " + formatPercent(criticalStrikeBaseChance + criticalStrikePerLevel) + "%, "
                + formatPercent(criticalStrikeBaseChance + criticalStrikePerLevel * 2.0) + "%, and "
                + formatPercent(criticalStrikeBaseChance + criticalStrikePerLevel * 3.0) + "% at levels 1-3.");
        int criticalStrikeCost = RpgStats.getAbilityUpgradeCost(criticalStrikeLevel, RpgStats.CRITICAL_STRIKE_MAX_LEVEL);
        boolean canUpgradeCriticalStrike = canSpend && criticalStrikeCost > 0 && points >= criticalStrikeCost;
        uiCommandBuilder.set("#CriticalStrikeUpgrade.HitTestVisible", canUpgradeCriticalStrike);
        uiCommandBuilder.set("#CriticalStrikeUpgrade.Text", criticalStrikeCost == 0 ? "Maxed" : String.valueOf(criticalStrikeCost));

        int lifestealLevel = stats.getLifestealLevel();
        float lifestealPct = LifestealSystem.getLifestealPercent(lifestealLevel, config);
        double lifestealPerLevel = config == null ? 3.0 : config.getLifestealPerLevelPct();
        uiCommandBuilder.set("#LifestealLevel.Text", "Level " + lifestealLevel + "/" + RpgStats.LIFESTEAL_MAX_LEVEL
                + " (" + formatPercent(lifestealPct) + "%)");
        uiCommandBuilder.set("#LifestealDescription.Text", "Heal for percentage of damage dealt: "
                + formatPercent(lifestealPerLevel) + "%, "
                + formatPercent(lifestealPerLevel * 2.0) + "%, and "
                + formatPercent(lifestealPerLevel * 3.0) + "% at levels 1-3.");
        int lifestealCost = RpgStats.getAbilityUpgradeCost(lifestealLevel, RpgStats.LIFESTEAL_MAX_LEVEL);
        boolean canUpgradeLifesteal = canSpend && lifestealCost > 0 && points >= lifestealCost;
        uiCommandBuilder.set("#LifestealUpgrade.HitTestVisible", canUpgradeLifesteal);
        uiCommandBuilder.set("#LifestealUpgrade.Text", lifestealCost == 0 ? "Maxed" : String.valueOf(lifestealCost));

        int thornsLevel = stats.getThornsLevel();
        float thornsPct = ThornsSystem.getThornsReflectPercent(thornsLevel, config);
        double thornsPerLevel = config == null ? 25.0 : config.getThornsReflectPerLevelPct();
        uiCommandBuilder.set("#ThornsLevel.Text", "Level " + thornsLevel + "/" + RpgStats.THORNS_MAX_LEVEL
                + " (" + formatPercent(thornsPct) + "%)");
        uiCommandBuilder.set("#ThornsDescription.Text", "Reflect damage back to attackers: "
                + formatPercent(thornsPerLevel) + "%, "
                + formatPercent(thornsPerLevel * 2.0) + "%, and "
                + formatPercent(thornsPerLevel * 3.0) + "% at levels 1-3.");
        int thornsCost = RpgStats.getAbilityUpgradeCost(thornsLevel, RpgStats.THORNS_MAX_LEVEL);
        boolean canUpgradeThorns = canSpend && thornsCost > 0 && points >= thornsCost;
        uiCommandBuilder.set("#ThornsUpgrade.HitTestVisible", canUpgradeThorns);
        uiCommandBuilder.set("#ThornsUpgrade.Text", thornsCost == 0 ? "Maxed" : String.valueOf(thornsCost));
    }

    private void setAddButtonState(UICommandBuilder uiCommandBuilder, String buttonId, boolean enabled) {
        uiCommandBuilder.set(buttonId + ".HitTestVisible", enabled);
    }

    private int getStatCap(String attribute) {
        if (config == null) {
            return DEFAULT_STAT_CAP;
        }
        return config.getStatCap(attribute);
    }

    private int getStatValue(RpgStats stats, String attribute) {
        switch (attribute) {
            case "str":
                return stats.getStr();
            case "dex":
                return stats.getDex();
            case "con":
                return stats.getCon();
            case "int":
                return stats.getIntl();
            case "end":
                return stats.getEnd();
            case "cha":
                return stats.getCha();
            default:
                return 0;
        }
    }

    private String normalizeAttribute(String attributeRaw) {
        if (attributeRaw == null) {
            return null;
        }
        String attribute = attributeRaw.trim().toLowerCase(Locale.ROOT);
        switch (attribute) {
            case "str":
            case "dex":
            case "con":
            case "end":
            case "cha":
                return attribute;
            case "endurance":
                return "end";
            case "int":
            case "intl":
                return "int";
            default:
                return null;
        }
    }

    private String formatPercent(double value) {
        double rounded = Math.round(value);
        if (Math.abs(value - rounded) < 0.01) {
            return String.valueOf((int) rounded);
        }
        return String.format(Locale.ROOT, "%.1f", value);
    }

    private String formatRate(double value) {
        double rounded = Math.round(value);
        if (Math.abs(value - rounded) < 0.01) {
            return String.valueOf((int) rounded);
        }
        return String.format(Locale.ROOT, "%.1f", value);
    }

    public static void refreshIfOpen(Ref<EntityStore> ref, Store<EntityStore> store) {
        if (ref == null || !ref.isValid() || store == null) {
            return;
        }
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }
        var customPage = player.getPageManager().getCustomPage();
        if (customPage instanceof StatsPage statsPage) {
            statsPage.refreshUI(ref, store, player);
        }
    }

    /**
     * Safe to call from inside a system. Skips refresh since store methods cannot be
     * called from systems. The page will update on next player interaction.
     */
    public static void refreshIfOpen(Player player, RpgStats stats) {
        // Cannot refresh from inside a system because refreshUI requires store access.
        // The page will update when the player interacts with it next.
    }

    public static final class StatsPageEventData {
        static final String KEY_TYPE = "Type";
        static final String KEY_STAT = "Stat";
        static final String KEY_TAB = "Tab";
        static final String KEY_ABILITY = "Ability";

        public static final BuilderCodec<StatsPageEventData> CODEC =
                BuilderCodec.builder(StatsPageEventData.class, StatsPageEventData::new)
                        .append(new KeyedCodec<>(KEY_TYPE, Codec.STRING), (d, v) -> d.type = v, d -> d.type).add()
                        .append(new KeyedCodec<>(KEY_STAT, Codec.STRING), (d, v) -> d.stat = v, d -> d.stat).add()
                        .append(new KeyedCodec<>(KEY_TAB, Codec.STRING), (d, v) -> d.tab = v, d -> d.tab).add()
                        .append(new KeyedCodec<>(KEY_ABILITY, Codec.STRING), (d, v) -> d.ability = v, d -> d.ability).add()
                        .build();

        public String type;
        public String stat;
        public String tab;
        public String ability;
    }
}
