# Ability Ideas

This document contains ability specifications for implementation. Each entry includes all fields needed to create a new ability.

---

## Template

```
### [Ability Name]

**Status:** Idea | In Progress | Implemented

**Description:** [What the ability does - shown in UI tooltip]

**Max Ranks:** [Number of ranks, typically 3]

**Effect Type:** [Choose one or more]
- Stat Modifier (health, mana, stamina, speed, etc.)
- Damage Modifier (outgoing damage multiplier)
- Damage Reduction (incoming damage reduction)
- Chance-Based (% chance to trigger effect)
- Regeneration (periodic resource recovery)
- Resource Pool (increases max of a resource)
- On-Hit Effect (triggers when attacking)
- On-Damage Effect (triggers when taking damage)

**Scaling per Rank:**
- Rank 1: [value]
- Rank 2: [value]
- Rank 3: [value]

**Activation:** [When does it apply]
- Passive (always active)
- Conditional (describe condition, e.g., "while wearing armor", "when health below 50%")
- On Event (describe trigger, e.g., "on taking damage", "on killing enemy")

**Requirements:** [Any conditions to benefit from the ability]
- None
- Must be wearing armor
- Must be holding specific item type
- Other: [describe]

**Upgrade Cost:** [Ability points per rank, default is 1/2/3]
- Rank 1: [cost]
- Rank 2: [cost]
- Rank 3: [cost]

**Config Keys:**
- `[ability_name]_[effect]_per_level_[unit]` (e.g., `light_foot_speed_per_level_pct`)

**Notes:** [Any additional implementation details, balance considerations, or references]
```

---

## Implemented Abilities

### Light Foot
**Status:** Implemented
**Effect Type:** Stat Modifier (movement speed)
**Scaling:** +5% / +10% / +15%
**Config Key:** `light_foot_speed_per_level_pct`

### Armor Proficiency
**Status:** Implemented
**Effect Type:** Damage Reduction (Physical/Projectile)
**Scaling:** +5% / +10% / +15%
**Activation:** Conditional (while wearing armor)
**Config Key:** `armor_proficiency_resistance_per_level_pct`

### Glancing Blow
**Status:** Implemented
**Effect Type:** Chance-Based (dodge NPC damage)
**Scaling:** 10% / 15% / 20% chance
**Config Key:** `glancing_blow_chance_per_level_pct`

### Health Regeneration
**Status:** Implemented
**Effect Type:** Regeneration (health per second)
**Scaling:** 2 / 3 / 4 HP/sec
**Config Key:** `health_regen_per_level_per_sec`

### Stamina Regeneration
**Status:** Implemented
**Effect Type:** Regeneration (stamina per second)
**Scaling:** 1.5 / 2.0 / 2.5 stamina/sec
**Config Key:** `stamina_regen_per_level_per_sec`

### Strong Lungs
**Status:** Implemented
**Effect Type:** Resource Pool (oxygen capacity)
**Scaling:** +50% / +100% / +150%
**Config Key:** `strong_lungs_oxygen_per_level_pct`

### Lucky Shot
**Status:** WIP (awaiting Hytale API hook)
**Effect Type:** Chance-Based (negate ammo consumption)
**Description:** When using a bow or crossbow, chance to not consume ammo
**Scaling:** 10% / 20% / 30% chance
**Activation:** On Event (when firing bow/crossbow)
**Requirements:** Must be using a bow or crossbow
**Config Key:** `lucky_shot_chance_per_level_pct`
**Notes:** Level tracking and UI implemented. Effect trigger awaiting proper Hytale projectile/ammo event hooks.

---

## Ideas

### Critical Strike
**Status:** Implemented
**Effect Type:** Chance-Based + Damage Modifier
**Description:** Chance to deal bonus damage on attack
**Max Ranks:** 3
**Scaling per Rank:**
- Rank 1: 10% chance, 1.5x damage
- Rank 2: 15% chance, 1.5x damage
- Rank 3: 20% chance, 1.5x damage
**Activation:** On Event (when dealing damage)
**Requirements:** None
**Upgrade Cost:** 1 / 2 / 3
**Config Keys:**
- `critical_strike_chance_per_level_pct` (default 5.0, so 10/15/20 at levels 1-3)
- `critical_strike_base_chance_pct` (default 5.0)
- `critical_strike_damage_multiplier` (default 1.5)
**Notes:** Uses DamageEventSystem pattern. Displays "Critical strike!" message when triggered.

### Lifesteal
**Status:** Implemented
**Effect Type:** On-Hit Effect + Regeneration
**Description:** Heal for a percentage of damage dealt
**Max Ranks:** 3
**Scaling per Rank:**
- Rank 1: 3% lifesteal
- Rank 2: 6% lifesteal
- Rank 3: 9% lifesteal
**Activation:** On Event (when dealing damage to enemies)
**Requirements:** None
**Upgrade Cost:** 1 / 2 / 3
**Config Keys:**
- `lifesteal_per_level_pct` (default 3.0)
**Notes:** Heals attacker after damage is dealt using EntityStatMap.addStatValue().

### Mana Regeneration
**Status:** Idea
**Effect Type:** Regeneration (mana per second)
**Description:** Passively regenerate mana over time
**Max Ranks:** 3
**Scaling per Rank:**
- Rank 1: +1 mana/sec
- Rank 2: +2 mana/sec
- Rank 3: +3 mana/sec
**Activation:** Passive (always active)
**Requirements:** None
**Upgrade Cost:** 1 / 2 / 3
**Config Keys:**
- `mana_regen_per_level_per_sec` (default 1.0)
**Notes:** Uses DelayedEntitySystem pattern from AbilityRegenSystem.

### Fortitude
**Status:** Idea
**Effect Type:** Damage Reduction (flat)
**Description:** Reduce all incoming damage by a flat amount
**Max Ranks:** 3
**Scaling per Rank:**
- Rank 1: -1 damage
- Rank 2: -2 damage
- Rank 3: -3 damage
**Activation:** Passive (when taking any damage)
**Requirements:** None
**Upgrade Cost:** 1 / 2 / 3
**Config Keys:**
- `fortitude_damage_reduction_per_level` (default 1.0)
**Notes:** Applied before percentage reductions. Damage cannot go below 0.

### XP Boost
**Status:** Idea
**Effect Type:** Stat Modifier (experience gain)
**Description:** Gain bonus XP from kills
**Max Ranks:** 3
**Scaling per Rank:**
- Rank 1: +10% XP
- Rank 2: +20% XP
- Rank 3: +30% XP
**Activation:** On Event (when gaining XP from kills)
**Requirements:** None
**Upgrade Cost:** 1 / 2 / 3
**Config Keys:**
- `xp_boost_per_level_pct` (default 10.0)
**Notes:** Modifies ExperienceOnKillSystem XP calculation. Good use for Charisma stat synergy.

### Thorns
**Status:** Implemented
**Effect Type:** On-Damage Effect + Damage Modifier
**Description:** Reflect a percentage of damage back to attackers
**Max Ranks:** 3
**Scaling per Rank:**
- Rank 1: 25% reflect
- Rank 2: 50% reflect
- Rank 3: 75% reflect
**Activation:** On Event (when taking damage from an attacker)
**Requirements:** Attacker must be a valid entity
**Upgrade Cost:** 1 / 2 / 3
**Config Keys:**
- `thorns_reflect_per_level_pct` (default 25.0)
**Notes:** Uses DamageEventSystem to detect incoming damage and apply counter-damage to attacker via EntityStatMap.addStatValue() with negative health.

### Tool Proficiency
**Status:** Implemented
**Effect Type:** Chance-Based (durability preservation)
**Description:** Chance to not consume tool durability when using tools
**Max Ranks:** 3
**Scaling per Rank:**
- Rank 1: 15% chance
- Rank 2: 30% chance
- Rank 3: 45% chance
**Activation:** On Event (when using tools to mine blocks)
**Requirements:** Must be using a tool (pickaxe, axe, shovel, etc.)
**Upgrade Cost:** 1 / 2 / 3
**Config Keys:**
- `tool_proficiency_chance_per_level_pct` (default 15.0)
**Notes:** Uses DamageBlockEvent to detect tool usage. Level tracking and UI implemented. Effect currently logs preservation triggers - full durability restoration pending direct Hytale API access for ItemStack durability modification.

### Lucky Miner
**Status:** Implemented
**Effect Type:** Chance-Based (bonus ore)
**Description:** Chance to receive bonus ore when mining ore blocks
**Max Ranks:** 3
**Scaling per Rank:**
- Rank 1: 10% chance
- Rank 2: 20% chance
- Rank 3: 30% chance
**Activation:** On Event (when breaking ore blocks)
**Requirements:** Must be mining an ore block (block ID contains "ore")
**Upgrade Cost:** 1 / 2 / 3
**Config Keys:**
- `lucky_miner_chance_per_level_pct` (default 10.0)
**Notes:** Uses BreakBlockEvent to detect ore mining. Currently notifies player of bonus ore - actual item dropping pending ItemComponent.generateItemDrops() integration.

### Flame Touch
**Status:** TODO
**Effect Type:** On-Hit Effect + Damage Modifier
**Description:** Your weapon attacks deal bonus fire damage
**Max Ranks:** 3
**Scaling per Rank:**
- Rank 1: +2 fire damage
- Rank 2: +4 fire damage
- Rank 3: +6 fire damage
**Activation:** On Event (when dealing melee/ranged damage)
**Requirements:** None
**Upgrade Cost:** 1 / 2 / 3
**Config Keys:**
- `flame_touch_damage_per_level` (default 2.0)
**Notes:** Needs to hook into damage events and apply fire damage type. May also apply burning status effect at higher ranks.

### Night Owl
**Status:** TODO
**Effect Type:** Stat Modifier (vision)
**Description:** See clearly in dark environments
**Max Ranks:** 3
**Scaling per Rank:**
- Rank 1: Slight night vision
- Rank 2: Moderate night vision
- Rank 3: Full night vision
**Activation:** Passive (always active)
**Requirements:** None
**Upgrade Cost:** 1 / 2 / 3
**Config Keys:**
- `night_owl_vision_per_level_pct` (default 33.0)
**Notes:** Needs to hook into player vision/gamma settings. May use shader effects or brightness modifiers.

### Deep Pockets
**Status:** TODO
**Effect Type:** Stat Modifier (inventory)
**Description:** Increase the maximum stack size of items in your inventory
**Max Ranks:** 3
**Scaling per Rank:**
- Rank 1: +25% stack size
- Rank 2: +50% stack size
- Rank 3: +100% stack size
**Activation:** Passive (always active)
**Requirements:** None
**Upgrade Cost:** 1 / 2 / 3
**Config Keys:**
- `deep_pockets_stack_per_level_pct` (default 25.0)
**Notes:** Needs to hook into inventory/item stack system. May require client-side sync for UI display.

### Magnetic Pull
**Status:** TODO
**Effect Type:** Stat Modifier (pickup range)
**Description:** Attract nearby dropped items from a greater distance
**Max Ranks:** 3
**Scaling per Rank:**
- Rank 1: +2 block pickup range
- Rank 2: +4 block pickup range
- Rank 3: +6 block pickup range
**Activation:** Passive (always active)
**Requirements:** None
**Upgrade Cost:** 1 / 2 / 3
**Config Keys:**
- `magnetic_pull_range_per_level` (default 2.0)
**Notes:** Needs to modify item entity pickup radius. May need to hook into item collection events or modify player pickup hitbox.

### Gourmand
**Status:** TODO
**Effect Type:** Stat Modifier (food effects)
**Description:** Food provides enhanced benefits
**Max Ranks:** 3
**Scaling per Rank:**
- Rank 1: +25% food effect strength/duration
- Rank 2: +50% food effect strength/duration
- Rank 3: +100% food effect strength/duration
**Activation:** On Event (when consuming food)
**Requirements:** None
**Upgrade Cost:** 1 / 2 / 3
**Config Keys:**
- `gourmand_effect_per_level_pct` (default 25.0)
**Notes:** Needs to hook into food consumption events. Could affect hunger restoration, buff duration, or both.

### Fire Resistance
**Status:** TODO
**Effect Type:** Damage Reduction (fire)
**Description:** Take reduced damage from fire and lava
**Max Ranks:** 3
**Scaling per Rank:**
- Rank 1: 25% fire damage reduction
- Rank 2: 50% fire damage reduction
- Rank 3: 75% fire damage reduction
**Activation:** Passive (when taking fire damage)
**Requirements:** None
**Upgrade Cost:** 1 / 2 / 3
**Config Keys:**
- `fire_resistance_per_level_pct` (default 25.0)
**Notes:** Needs to hook into damage events and filter for fire damage type. Could also reduce burning duration at higher ranks.

---

## UI Features TODO

### Attribute Points Refund Button
**Status:** Implemented
**Description:** A button that resets all spent attribute points (STR, DEX, CON, INT, END, CHA), returning them to the player's available stat points pool.
**Location:** Stats tab, Attributes card - positioned just above the player level display.
**Behavior:**
- Clicking the button resets all attributes to BASE_STAT (10).
- All spent stat points are returned to the available pool.
- Requires `rpgstats.reset` permission.
- Health, mana, and stamina effects are re-applied after refund.
**Notes:** Currently free to use with no cooldown. Future versions may add config options for cost/cooldown.

### Ability Points Refund Button
**Status:** Implemented
**Description:** A button that resets all spent ability points, returning them to the player's available ability points pool.
**Location:** Abilities tab - positioned at the top right of the header row, next to "Available" points.
**Behavior:**
- Clicking the button resets all ability levels to 0.
- All spent ability points are returned to the available pool.
- Requires `rpgstats.reset` permission.
- Ability effects (speed boosts, oxygen capacity) are reset after refund.
**Notes:** Currently free to use with no cooldown. Future versions may add config options for cost/cooldown.