# RPG Stats Plugin - Guided Walkthrough

This project is a java plugin mod for the game Hytale. It gives each player RPG-style stats (level, XP, and attributes) and lets you view and edit them with commands and an in-game GUI.

## What the mod does as of now

- Tracks player stats (level, XP, STR, DEX, CON, INT, END, CHA).
- Uses **total XP** for leveling (max level is configurable; default **25**).
- Earns **1 stat point per level** (level 2 = 1 point, level 3 = 2 points, etc).
- Tracks **ability points per level** (configurable via `ability_points_per_level`).
- Light Foot ability with 3 levels (+5%, +10%, +15% movement speed).
- Armor Proficiency ability with 3 levels (+5%, +10%, +15% Physical/Projectile resistance while wearing armor).
- Glancing Blow ability with 3 levels (10%, 15%, 20% chance to dodge hostile NPC damage).
- Health Regeneration ability with 3 levels (2/3/4 health per second).
- Stamina Regeneration ability with 3 levels (0.75/1.0/1.25 stamina per second).
- Strong Lungs ability with 3 levels (+50%, +100%, +150% oxygen).
- Lucky Shot ability with 3 levels (10%, 20%, 30% chance to not consume ammo) - **WIP: Effect not yet functional**.
- Critical Strike ability with 3 levels (10%/15%/20% chance to deal 1.5x damage).
- Lifesteal ability with 3 levels (3%/6%/9% of damage dealt heals you).
- Thorns ability with 3 levels (25%/50%/75% of damage taken reflected to attackers).
- Lets players spend points with `/stats add <stat>`.
- Lets admins set or reset stats with `/stats set` and `/stats reset`.
- Provides a stats GUI with tabs (Stats, Abilities, Reset) and an XP progress bar.
- Shows an optional HUD XP bar (toggle with `hud_enabled` in `config.toml`).
- Awards XP on hostile NPC kills and shows chat updates. XP is determined by health of hostile entity.
- Applies **Strength damage multiplier**: `damage = baseDamage * (str / damage_multiplier_base)`.
  - STR 10 = 1.0x, STR 11 = 1.1x, STR 20 = 2.0x, STR 25 = 2.5x.
- Applies **Constitution max health**: `+health_per_point` per point above 10; below 10 reduces max health (floor 10).
- Applies **Intellect max mana**: `+mana_per_point` per point above 10; below 10 reduces max mana (floor 0).
- Applies **Endurance max stamina**: `+stamina_per_point` per point above 10; below 10 reduces max stamina (floor 1).
- Applies **Dexterity mining speed**: `mining_speed_base + mining_speed_per_point * (DEX - 10)` (clamped to `0.5x` min and `2.5x` max).
- END replaces WIS (spent WIS points are migrated via stat history).
- Reduction in levels result in the last point spent on stats to be refunded. (LIFO order).

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
Opens the stats GUI (tabs for Stats, Abilities, and Reset).

Reset your stats (GUI tab):
- The Reset tab includes a warning and a button to reset your stats.
- Requires `rpgstats.reset`.

Spend a stat point:
```text
/stats add str
/stats add int
```
Valid stats: `str`, `dex`, `con`, `int`, `end`, `cha`.
- CHA is currently an unused stat but will be implemented in the future.

Spend ability points:
- Open `/stats` and use the Abilities tab to upgrade abilities (max level 3 each):
  - Light Foot, Armor Proficiency, Glancing Blow, Health Regen, Stamina Regen
  - Strong Lungs, Lucky Shot (WIP), Critical Strike, Lifesteal, Thorns

Manually add ability points (admin):
```text
/stats add ability
```
Requires `rpgstats.add.ability`.

Admin-style commands:
```text
/stats set str self 20
/stats set dex OtherPlayer 18
/stats reset self
/stats reset OtherPlayer
/stats reload
```
When setting `level`, values above `max_level` are blocked with an error message.

## Permissions

Permission root: `rpgstats`

- View stats: `rpgstats.view`
- Spend stat points: `rpgstats.add`
- Grant ability points: `rpgstats.add.ability`
- Set stats for self: `rpgstats.set`
- Set stats for others: `rpgstats.set.others`
- Reset stats for self: `rpgstats.reset`
- Reset stats for others: `rpgstats.reset.others`
- Reload config: `rpgstats.set`

Note: LuckPerms requires explicit denies to block commands. If you don't want players using a command, add a deny for the specific node (for example, `rpgstats.set`).
Note: Without a permissions mod, only OP (wildcard `*`) can use `/stats set`, `/stats reset <player>`, or `/stats reload`. Players can still use `/stats`, `/stats add`, and `/stats reset self`.

## Config (config.toml)

The plugin writes `config.toml` to the plugin data directory on first run. Edit the file and restart the server.

Default keys:
```toml
config_version = 5
xp_multiplier = 0.35
max_level = 25
ability_points_per_level = 2
ability_rank1_cost = 1
ability_rank2_cost = 2
ability_rank3_cost = 3
light_foot_speed_per_level_pct = 5.0
armor_proficiency_resistance_per_level_pct = 5.0
glancing_blow_chance_per_level_pct = 5.0
health_regen_per_level_per_sec = 1.0
stamina_regen_per_level_per_sec = 0.15
strong_lungs_oxygen_per_level_pct = 100.0
lucky_shot_chance_per_level_pct = 10.0
critical_strike_chance_per_level_pct = 5.0
critical_strike_base_chance_pct = 5.0
critical_strike_damage_multiplier = 1.5
lifesteal_per_level_pct = 3.0
thorns_reflect_per_level_pct = 25.0
damage_multiplier_base = 10.0
mining_speed_base = 1.0
mining_speed_per_point = 0.10
health_per_point = 10.0 # (CON)
mana_per_point = 10.0 # (INT)
stamina_per_point = 1.0 # (END)
hud_enabled = true
str_cap = 25
dex_cap = 25
con_cap = 25
int_cap = 25
end_cap = 25
cha_cap = 25
```
If a player tries to set or add a stat above its cap, the command returns a message with the configured limit.
Ability points are tracked per level using `ability_points_per_level` and shown in the Abilities tab.
`ability_points_per_level` is clamped to prevent overflow: max is `floor(2147483647 / max(1, max_level - 1))`.
`ability_rank1_cost`, `ability_rank2_cost`, `ability_rank3_cost` control how many points each rank costs (default: 1/2/3).
Total cost to max an ability = rank1 + rank2 + rank3 (default: 6 points).
Light Foot speed, Armor Proficiency resistance, and Glancing Blow dodge chance scale per level using
`light_foot_speed_per_level_pct`, `armor_proficiency_resistance_per_level_pct`, and `glancing_blow_chance_per_level_pct`
(values are percentages). Glancing Blow has a 5% base dodge chance, so levels 1-3 grant 10%, 15%, and 20% total dodge chance.
Strong Lungs increases max oxygen per level using `strong_lungs_oxygen_per_level_pct` (percentage-based additive bonus).
Critical Strike chance is calculated as `base + (per_level * level)` with configurable damage multiplier.
Lifesteal heals you for a percentage of damage dealt, scaling with `lifesteal_per_level_pct`.
Thorns reflects a percentage of damage taken back to attackers, scaling with `thorns_reflect_per_level_pct`.
Health/Stamina regeneration abilities add per-level points per second using
`health_regen_per_level_per_sec` and `stamina_regen_per_level_per_sec`.
Set `hud_enabled = false` to disable the HUD XP bar (useful for HUD mod conflicts like TextSigns).

XP blacklist (xp_blacklist.toml):
```toml
npc_types = []
roles = []
```
Use `xp_blacklist.toml` to prevent specific NPCs from granting XP. Use the exact `npcTypeId` or role name shown in the diagnostics log (example: `Deer_Doe`). Entries are case-insensitive.
New installs ship with a default non-hostile NPC list in `npc_types`.
If you already have `xp_blacklist.toml`, keep your file and paste any new entries from the default list into your existing `npc_types` array (one entry per line is fine). Save the file and run `/stats reload`.
Multi-line arrays are supported in `xp_blacklist.toml` if you want to keep long lists readable.

## Planned features
- CHA will apply some kind of discount to NPC shops.
- More abilities that affect gameplay in non-destructive ways.
- Classes (maybe if it fits).
- Uses for the ability modifiers (DND style System).
- GUI enhancements (help buttons per stat and other UX polish).
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
