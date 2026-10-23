---
title: Configuration
sidebar_position: 5
---

# Configuration

All settings live in `plugins/PickMeUp/config.yml`. The file is written with the default values on the first start.
There are no in-game commands to change settings. Edit the file and run `/pickmeup reload`.

## The default file

```yaml
debug: INFO
generalSettings:
  ==: pickMeUpGeneralSettings
  language: en_US
  updateCheck: true
carrySettings:
  ==: pickMeUpCarrySettings
  throwForce: 2.0
  allowStacking: false
mobSettings:
  ==: pickMeUpMobSettings
  allowedMobs:
  - PIG
  - CHICKEN
  - RABBIT
  - WOLF
  - BOAT
  - SHEEP
  - PARROT
  requirePermission: false
  blacklist: false
worldSettings:
  ==: pickMeUpWorldSettings
  restrictWorlds: false
  blacklist: false
  worlds:
  - world
```

:::warning Keep the `==` lines
The `==:` lines tell the server which kind of settings block follows. Do not change or remove them.
:::

A key that is missing from the file falls back to its default.

## `debug`

The log level of the plugin. Leave it at `INFO` unless support asks you to change it.

## `generalSettings`

| Key | Default | What it does |
|---|---|---|
| `language` | `en_US` | The language of the plugin messages. Included are `en_US`, `de_DE` and `zh_CN`. |
| `updateCheck` | `true` | Tells players with the `pickmeup.reload` permission when a new version is out. |

## `carrySettings`

| Key | Default | What it does |
|---|---|---|
| `throwForce` | `2.0` | How hard a throw with a full power bar is. Higher values throw further. A half-full bar throws with a quarter of this. |
| `allowStacking` | `false` | Lets players pick up an entity that is carrying something itself. Without it, only players with `pickmeup.bypass.nostack` can. |

See [Picking up and throwing](./using.md) for how throwing and stacking work in the game.

## `mobSettings`

| Key | Default | What it does |
|---|---|---|
| `allowedMobs` | `PIG`, `CHICKEN`, `RABBIT`, `WOLF`, `BOAT`, `SHEEP`, `PARROT` | A list of entity type names. Players are `PLAYER`. |
| `blacklist` | `false` | `false`: only the listed types can be picked up. `true`: all types except the listed ones can be picked up. |
| `requirePermission` | `false` | When `true`, players also need `pickmeup.pickup.<entity type>` for each type, in lower case. |

See [Which entities can be picked up](./entities.md) for examples.

## `worldSettings`

| Key | Default | What it does |
|---|---|---|
| `restrictWorlds` | `false` | When `false`, Pick Me Up works in every world and the other two keys are ignored. |
| `blacklist` | `false` | `false`: pick-up only works in the listed worlds. `true`: it works everywhere except there. |
| `worlds` | `world` | A list of world names. |
