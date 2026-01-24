# Changelog

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
