---
title: Which entities can be picked up
sidebar_position: 4
---

# Which entities can be picked up

Four things decide whether a player can pick up an entity: the entity list, an optional permission, the world, and
protection plugins. All settings live in `config.yml`. The full list of keys is on
[Configuration](./configuration.md).

## The entity list

`mobSettings.allowedMobs` is a list of entity types. The names are the Bukkit entity type names, such as `PIG`,
`COW`, `VILLAGER` or `PLAYER`. Players count as `PLAYER`. Upper or lower case does not matter.

The default list is:

```yaml
mobSettings:
  allowedMobs:
  - PIG
  - CHICKEN
  - RABBIT
  - WOLF
  - OAK_BOAT
  - SHEEP
  - PARROT
```

The list can work in two ways. `mobSettings.blacklist` picks the one you want.

| `blacklist` | What the list means |
|---|---|
| `false` (default) | Only the listed entity types can be picked up. |
| `true` | Every entity type can be picked up, except the listed ones. |

:::note Unknown names
A name the server does not know stays in the file, but is ignored. The server log reports it once. Old names that
Minecraft has renamed still work: `BOAT` counts as `OAK_BOAT`, `CHEST_BOAT` as `OAK_CHEST_BOAT`, `MUSHROOM_COW` as
`MOOSHROOM`, `SNOWMAN` as `SNOW_GOLEM`, and `MINECART_CHEST` and the other `MINECART_*` names as `CHEST_MINECART` and
so on.
:::

:::tip Picking up players
To let players pick up each other, add `PLAYER` to the list. With `blacklist: true`, players can be picked up unless
you list `PLAYER`.

Each player can still opt out with `/pickmeup toggle`. See
[Players who do not want to be picked up](./using.md#players-who-do-not-want-to-be-picked-up).
:::

## A permission per entity type

Set `mobSettings.requirePermission` to `true`, and players also need a permission for each entity type:

```text
pickmeup.pickup.<entity type>
```

The entity type is written in lower case, for example `pickmeup.pickup.pig` or `pickmeup.pickup.player`. The entity
list is still checked first. A permission does not let a player pick up a type the list does not allow.

With `requirePermission: false`, which is the default, everyone can pick up every allowed type.

## Worlds

By default Pick Me Up works in every world. To limit it, turn on `worldSettings.restrictWorlds` and list world names in
`worldSettings.worlds`.

```yaml
worldSettings:
  restrictWorlds: true
  blacklist: false
  worlds:
  - world
```

| `blacklist` | What the world list means |
|---|---|
| `false` (default) | Pick-up only works in the listed worlds. |
| `true` | Pick-up works everywhere except in the listed worlds. |

The world is checked when a player picks something up.

## Protection plugins

When one of these plugins is installed, Pick Me Up asks it before every pick-up, after all other checks. The check uses
the spot where the entity stands.

| Plugin | Who can pick up |
|---|---|
| BentoBox | In BentoBox worlds, only members of the island the entity is on. Outside of islands in those worlds, nobody. |
| GriefPrevention | Inside a claim, players with container trust or more in that claim. Outside of claims, everyone. |
| PlotSquared | On a plot, players who are added to that plot. Roads and places outside of plots are free. |
| Towny | In Towny worlds, players who may destroy blocks at that spot. |
| RedProtect | Inside a region, players who may build there. Outside of regions, everyone. |
| WorldGuard | Decided by the `pickmeup` region flag, see below. Regions without the flag do not limit picking up. |
| HuskClaims | Inside a claim, players who may interact with entities there, following the claim's trust levels and flags. Outside of claims, as HuskClaims allows in the wilderness. |
| Citizens | Nobody can pick up an NPC. |

In all other places the protection plugin does not limit picking up.

### The WorldGuard flag

Pick Me Up adds the region flag `pickmeup`. Deny it to stop strangers from picking up entities in a region:

```text
/rg flag <region> pickmeup deny
```

The flag applies to non-members by default, so members and owners of the region can still pick up there. To block
everyone, add the group:

```text
/rg flag <region> pickmeup -g all deny
```

Players with WorldGuard's region bypass are never blocked.
