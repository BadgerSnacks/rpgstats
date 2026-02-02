# Changelog

## 0.7.0

### Changed
- `config_version` bumped to 16.
- XP gain chat messages are disabled by default (toggle via config).

### Added
- Config option `xp_chat_messages_enabled` (default false) to toggle XP gain chat messages.
- Party system: `/sparty` commands for create/invite/accept/decline/leave/kick/disband/info.
- Party UI tab with create/leave actions and player cards.
- Party HUD overlay (top-left) showing party members, level, health percent, and out-of-range indicator.
- Party XP sharing for kill and mining XP with radius checks and scaled-killer distribution.

## 0.6.1

### Changed
- `config_version` bumped to 15.

### Added
- /stats hud command to toggle the RPG stats HUD per player.
- Party HUD overlay showing party members, levels, and health percentage with out-of-range indicator.
- Party HUD config options: `party_hud_offset_x`, `party_hud_offset_y`, `party_hud_refresh_ticks`.
- Party XP sharing for kills and mining using the scaled-killer distribution and share radius rules.

## 0.6.0

### Added
- Log rotation on version updates: existing logs are renamed with `-old-{version}` suffix when plugin version changes.
- Configurable max ability level via `max_ability_level` in `config.toml` (default 3, range 1-10).
  - Allows server admins to let players upgrade abilities beyond rank 3.
  - Rank costs beyond rank 3 equal the rank number (rank 4 costs 4 points, rank 5 costs 5 points, etc.).
- **Level-up splash notification** using EventTitleUtil for prominent on-screen celebration with chat message fallback.
- **NPC Leveling System** for calculating mob levels based on HP and zone configuration:
  - `npc_leveling.toml` config file with zone definitions and entity overrides.
  - Zone-based level ranges (e.g., Emerald Grove L1-15, Howling Sands L15-50).
  - Entity override support with wildcards (e.g., `hytale:trork_*:8:false`).
  - Deterministic level variation using entity UUID for consistency.
  - Level caching for performance with configurable expiration.
  - NPC levels logged in kill XP debug output.
- `NpcLevelData` component for caching calculated NPC levels.
- `NpcLevelCalculator` service for centralized level computation.

### Changed
- `config_version` bumped to 13.
- `RpgStatsHud.ui` rewritten with inline styles to fix CustomUI crash (removed external Common.ui reference).
- `LevelUpSplash.java` now uses EventTitleUtil for splash screen with graceful fallback to chat messages.
- `ExperienceOnKillSystem` now calculates and logs NPC level for each kill.

### Fixed
- **CustomUI document loading crash** on player join - caused by reference to non-existent Common.ui file.
- Config loading crash when upgrading from older versions missing new config keys (null pointer in `stripQuotes`).

### Deferred
- Crafting XP system - postponed until Hytale provides proper events for bench crafting.
  - Current Hytale API only fires crafting events for instant crafts, not bench crafting with time.
  - Config file structure (`crafting_xp.toml`) is designed and ready for when the API supports it.

## 0.5.x

### Added
- Mining XP system with configurable rewards per block type (`mining_xp.toml`).
- Wildcard support for mining XP (e.g., `Ore_Iron_*` matches all iron ore variants).
- HUD refresh on player load to fix level display on first join.
- Flame Touch XP attribution - players now receive XP when NPCs die from burn damage.
- Configurable `flame_touch_finisher_threshold` for low-health NPC kills.

### Changed
- Flame Touch only activates when holding a weapon (not tools or empty hand).
- Flame Touch uses damage modification instead of nested damage events.
- Flame Touch finisher ticks every 0.25 seconds for faster death resolution.

### Fixed
- NPCs no longer become invulnerable during combat (NpcLevelAssignSystem rewrite).
- Flame Touch burn kills now properly credit XP.
- Flame Touch finisher catches NPCs stuck in "broken death state".

### Removed
- NPC Leveling System completely removed (caused entity state corruption issues).

## 0.4.0

### Added
- Flame Touch ability with 3 ranks that adds bonus fire damage and applies Burn on hit (+2/+4/+6).
- Configurable `flame_touch_damage_per_level` in config.toml.
- Gourmand ability with 3 ranks that increases stat gains from consumable items (+10%/+20%/+30%).
- Configurable `gourmand_food_bonus_per_level_pct` in config.toml.
- Tool-gated mining XP system with configurable block rewards in `mining_xp.toml`.
- Mining XP wildcards like `Ore_Iron_*` to match all ore variants and auto-normalization for old item-style IDs.

### Changed
- `config_version` bumped to 9.
- Component version bumped to 18 for Gourmand support.

## 0.3.0

### Added
- MultipleHUD mod support - RPGStats HUD now coexists with other HUD mods when MultipleHUD is installed.
- `HudHelper` utility class for managing HUD compatibility with other mods.
- Tool Proficiency ability with 3 ranks that provides a chance to preserve tool durability (15%/30%/45%).
- Configurable `tool_proficiency_chance_per_level_pct` in config.toml.
- Lucky Miner ability with 3 ranks that provides a chance for bonus ore when mining (10%/20%/30%).
- Configurable `lucky_miner_chance_per_level_pct` in config.toml.
- Refund Attributes button on Stats tab to reset all attribute points (STR/DEX/CON/INT/END/CHA) back to base 10.
- Refund Abilities button on Abilities tab to reset all ability levels back to 0.
- Stats page "How stats work" section with descriptions for all attributes.
- Custom stone_bg backgrounds for UI panels.

### Changed
- `config_version` bumped to 7.
- Component version bumped to 16 for Lucky Miner support.
- Abilities tab now has 6 rows (12 abilities total including WIP Lucky Shot).

### Fixed
- HUD not updating when PartyPlugin (or other HUD mods) are installed.
- HUD updates now properly check if RPGStats HUD is active before sending updates.
- Lucky Miner now properly spawns bonus ore using CommandBuffer (fixes crash when picking up ore).

## 0.2.1

### Added
- Configurable ability rank costs via `config.toml` (`ability_rank1_cost`, `ability_rank2_cost`, `ability_rank3_cost`).
  - Server admins can now customize the point cost for each ability rank (default: 1/2/3 points for ranks 1/2/3).
  - Total cost to max an ability is configurable (default: 6 points).

### Changed
- `config_version` bumped to 5.

### Fixed
- GUI crash when killing enemies.

## 0.2.0

### Added

- Strong Lungs ability with 3 ranks that increases oxygen capacity (+50%/+100%/+150%).
- Lucky Shot ability with 3 ranks that provides a chance to not consume ammo when firing bows/crossbows (10%/20%/30%) - **WIP: awaiting Hytale API hook for projectile events**.
- Critical Strike ability with 3 ranks that provides a chance to deal bonus damage (10%/15%/20% chance at 1.5x damage).
- Lifesteal ability with 3 ranks that heals for a percentage of damage dealt (3%/6%/9%).
- Ability regeneration system for health and stamina.
- `/stats reload` command to hot-reload configuration without server restart.
- Comprehensive Hytale modding best practices documentation in `AGENTS.md`.
- Ability specification document `abilities.md` for tracking ability ideas.
- GUI size increased to 1020x820 pixels for better visibility.
- Abilities tab now displays two abilities per row in a grid layout.
- Ability description font size increased to 14.
- Ability card icons added (48x48 placeholder images).
- Upgrade button width increased to 80 pixels.
- Thorns ability scaling changed from 10%/20%/30% to 25%/50%/75%.

### Changed

- Complete ability system with 9 abilities fully implemented and configurable.
- All ability scaling values are now configurable via `config.toml`.
- Improved permission system with LuckPerms compatibility.
- Component version bumped to 13 for Lifesteal support.

### Fixed

- UI refresh issues when gaining XP or spending points.

## 0.1.4

### Added

- Configurable ability points per level (`ability_points_per_level`).
- Ability points display in the Abilities tab.
- `/stats add ability` to grant ability points manually (permission `rpgstats.add.ability`).
- Light Foot ability with 3 ranks that boosts movement speed (+5%/+10%/+15%).
- Armor Proficiency ability with 3 ranks that reduce Physical/Projectile damage while wearing armor (+5%/+10%/+15%).
- Glancing Blow ability with 3 ranks that provides a chance to dodge hostile NPC damage (10%/15%/20%).
- Health Regeneration ability with 3 ranks (2/3/4 health per second).
- Stamina Regeneration ability with 3 ranks (1.5/2.0/2.5 stamina per second).
- Abilities tab layout with a scrollable list, icons, descriptions, and upgrade button.
- Ability costs now scale by level (1/2/3 points for levels 1/2/3).

### Changed

- `config_version` bumped to 4.
- Ability points per level are clamped to prevent overflow.
- Ability scaling now reads from config (`light_foot_speed_per_level_pct`, `armor_proficiency_resistance_per_level_pct`,
  `glancing_blow_chance_per_level_pct`, `health_regen_per_level_per_sec`, `stamina_regen_per_level_per_sec`).
- Stamina regeneration rate reduced from 1.0 to 0.5 points per level for better balance (total: 1.5/2.0/2.5 at levels 1-3).
- Renamed "Thick Skin" ability to "Armor Proficiency" throughout codebase and UI.
- Config key renamed from `thick_skin_resistance_per_level_pct` to `armor_proficiency_resistance_per_level_pct`.
- Replaced deprecated `Player.getPlayerRef()` with proper component-based PlayerRef retrieval.
- Replaced deprecated `Player.getPlayerConnection()` with `PlayerRef.getPacketHandler()` in LightFootSpeedEffect.

### Fixed

- Fixed a mismatched brace in `RpgStatsPage.ui` that prevented CustomUI documents from loading.

## 0.1.3

### Added

- Stats GUI page opened by `/stats` with tabs (Stats, Abilities placeholder, Reset).
- XP progress bar, level/points display, and stat spend buttons in the GUI.
- Reset tab with warning and self-reset button (permission `rpgstats.reset`).
- Live GUI refresh when XP is awarded.

### Removed

- None.

### Fixed

- None.

## 0.1.2

### Added

- Initial changelog file.
- Documented default config values in `config.toml` comments.
- Clarified `damage_multiplier_base` behavior in `config.toml` comments (lower number = more damage).
- Standardized permission root to `rpgstats` for LuckPerms compatibility.
- Explicit permission nodes are now registered so LuckPerms can list them.
- Documented that LuckPerms uses explicit denies to block commands by default.
- Added XP blacklist config for NPC type IDs and roles to block XP from non-hostiles.
- Added `xp_blacklist.toml` so XP blacklist entries can live outside `config.toml`.
- Documented that `xp_blacklist.toml` entries should match `npcTypeId` or role names from the diagnostics log.
- Default `xp_blacklist.toml` now ships with a starter list of non-hostile NPC type IDs.
- Added per-stat cap settings in `config.toml` (default 25) with enforcement in `/stats add` and `/stats set`.
- Restricted `/stats set`, `/stats reset <player>`, and `/stats reload` to OP or explicit permission grants.

### Removed

- None.

### Fixed

- Stats now persist between sessions after logout/login.
- `/stats set level` now blocks values above `max_level` and reports the server cap.
- Health, mana, and stamina now scale down when stats drop below 10, with floors (health 10, mana 0, stamina 1).
- XP blacklist parsing now supports multi-line arrays and loads immediately after first creation.
- Stat values now clamp to a minimum of 1 when set.
