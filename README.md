# RPG Stats Plugin - Guided Walkthrough

This project is a java plugin mod for the game Hytale. It gives each player RPG-style stats (level, XP, and attributes) and lets you view and edit them with commands (GUI Planned for later).

## What the mod does as of now

- Tracks player stats (level, XP, STR, DEX, CON, INT, END, CHA).
- Uses **total XP** for leveling (max level is configurable; default **25**).
- Earns **1 stat point per level** (level 2 = 1 point, level 3 = 2 points, etc).
- Lets players spend points with `/stats add <stat>`.
- Lets admins set or reset stats with `/stats set` and `/stats reset`.
- Awards XP on hostile NPC kills and shows chat updates. XP is determined by health of hostile entity.
- Applies **Strength damage multiplier**: `damage = baseDamage * (str / damage_multiplier_base)`.
  - STR 10 = 1.0x, STR 11 = 1.1x, STR 20 = 2.0x, STR 25 = 2.5x.
- Applies **Constitution max health**: `+health_per_point` per point above 10.
- Applies **Intellect max mana**: `+mana_per_point` per point above 10.
- Applies **Endurance max stamina**: `+stamina_per_point` per point above 10.
- Applies **Dexterity mining speed**: `mining_speed_base + mining_speed_per_point * (DEX - 10)` (clamped to `0.5x` min and `2.5x` max).
- END replaces WIS (spent WIS points are migrated via stat history).

If the level goes down, the **last spent point is undone first** (LIFO order).

## How to Install:
To use the mod on any server copy the jar into:
```
%APPDATA%/Hytale/UserData/Mods/
```
To use the mod on a specific server (single player world) copy the jar into:
```
%APPDATA&/Hytale/UserData/Saves/<NameOfServer>/mods
```
- For external servers just place the jar file into /mods from the root folder.
- Server must be restarted after install/update.

## How to use it in-game

Basic command:
```text
/stats
```
Shows your level, total XP, available stat points, and attribute values.

Spend a stat point:
```text
/stats add str
/stats add int
```
Valid stats: `str`, `dex`, `con`, `int`, `end`, `cha`.
- CHA is currently an unused stat but will be implemented in the future.

Admin-style commands:
```text
/stats set str self 20
/stats set dex OtherPlayer 18
/stats reset self
/stats reset OtherPlayer
```

If you target other players, you need permission:
- `your.plugin.base.stats.set.others`
- `your.plugin.base.stats.reset.others`

(The base comes from `plugin.getBasePermission()` at runtime.)

## Config (config.toml)

The plugin writes `config.toml` to the plugin data directory on first run. Edit the file and restart the server.

Default keys:
```toml
xp_multiplier = 0.35
max_level = 25
damage_multiplier_base = 10.0
mining_speed_base = 1.0
mining_speed_per_point = 0.10
health_per_point = 10.0 # (CON)
mana_per_point = 10.0 # (INT)
stamina_per_point = 1.0 # (END)
```

## Planned features
- Ability points to spend on ability's per level that can be configured from the .toml file.
- CHA will apply some kind of discount to NPC shops.
- Ability's that will affect gameplay in non-destructive ways.
- Classes (maybe if it fits).
- Uses for the ability modifiers (DND style System).
- GUI for displaying stats, current level, current xp, stat points available, and a way to distribute/reset stat points.
- A way to apply levels to NPC's
- Weapon bonus, change for weapons to spawn with a +1,+2, and +3 variation.
- Negative stat effects from poison
- Debuffs that effect stats
- Buffs that effect stats

## Leveling rules (simple formula - Nerd stuff from here on out)

The XP needed for the **next** level is:
```text
xpToNext(L) = 100 + 50*(L-1) + 20*(L-1)^2
```

XP is stored as **total XP**, and your level is calculated from that total.

## XP from NPC kills

- Only **hostile** NPCs grant XP.
- XP formula: `round(maxHealth * xp_multiplier)`
- Boss bonus: if `maxHealth >= 200`, XP is multiplied by `1.5`. (This may change after testing)
- XP is clamped to `min = 1, max = 1000`.
- Player chat shows XP gained and progress toward next level.

## Diagnostics log

RPGStats writes a per-session diagnostics log under the plugin data directory:
```
logs/log-YYYY-MM-DD_HH-mm-ss.txt
```
The server log prints the exact path on startup.

## File-by-file Guide

### 1) `src/main/java/com/bsnacks/rpgstats/RpgStatsPlugin.java`

**Purpose:** The entry point. Registers the stats component, listeners, commands, and systems.

### 2) `src/main/java/com/bsnacks/rpgstats/components/RpgStats.java`

**Purpose:** Stores all stats and the leveling rules.

Key ideas:
- `xp` is **total XP**.
- `level` is recalculated from `xp`.
- `StatHistory` tracks which points were spent so they can be undone if the level drops.

If you add a new stat:
1. Add a field.
2. Add it to `CODEC` (keys must start with uppercase, like `"Luck"`).
3. Update `clone()`.
4. Bump `CURRENT_VERSION` and add a migration.

### 3) `src/main/java/com/bsnacks/rpgstats/listeners/PlayerListeners.java`

**Purpose:** Ensures a player gets a stats component when they are ready.

### 4) Commands

- `StatsCommand` shows stats.
- `StatsAddCommand` spends a stat point.
- `StatsSetCommand` sets stats for a target.
- `StatsResetCommand` resets stats for a target.

### 5) `src/main/java/com/bsnacks/rpgstats/systems/StrengthDamageSystem.java`

**Purpose:** Multiplies outgoing damage by `str / 10.0` for player attackers.

### 6) Stat effect systems

- `src/main/java/com/bsnacks/rpgstats/systems/ConstitutionHealthEffect.java`: adds max health from CON.
- `src/main/java/com/bsnacks/rpgstats/systems/IntellectManaEffect.java`: adds max mana from INT.
- `src/main/java/com/bsnacks/rpgstats/systems/EnduranceStaminaEffect.java`: adds max stamina from END.
- `src/main/java/com/bsnacks/rpgstats/systems/DexterityMiningSpeedSystem.java`: scales block damage per swing based on DEX.

### 7) XP + config helpers

- `src/main/java/com/bsnacks/rpgstats/systems/ExperienceOnKillSystem.java`: awards XP on hostile NPC kills.
- `src/main/java/com/bsnacks/rpgstats/systems/ExperienceCalculator.java`: XP math based on NPC max health.
- `src/main/java/com/bsnacks/rpgstats/config/RpgStatsConfig.java`: loads `config.toml`.
- `src/main/java/com/bsnacks/rpgstats/logging/RpgStatsFileLogger.java`: writes diagnostics logs.

## Build from source.

Build:
```bash
./gradlew build
```

JAR output:
```
build/libs/rpgstats-(Version number).jar
```