---
title: Configuration
sidebar_position: 5
---

# Configuration

All settings live in `plugins/PickMeUp/config.yml`. The file is written with the default values on the first start.
There are no in-game commands to change settings. Edit the file and run `/pickmeup reload`.

## The default file

```yaml
carrySettings:
  allowStacking: false
  leftClickPickup: false
  leftClickPickupPlayers: false
  maximumStacking: 0
  throwDelay: 10
  throwForce: 2.0
generalSettings:
  language: "en_US"
  updates:
    askForAutoUpdate: true
    autoUpdate: false
    notice: true
mobSettings:
  allowedMobs:
  - "PIG"
  - "CHICKEN"
  - "RABBIT"
  - "WOLF"
  - "OAK_BOAT"
  - "SHEEP"
  - "PARROT"
  blacklist: false
  requirePermission: false
worldSettings:
  blacklist: false
  restrictWorlds: false
  worlds:
  - "world"
```

A key that is missing from the file falls back to its default. Pick Me Up saves the file again on every start, so
settings added in a new version show up in your file with their default value.

:::info Log level
The log level of the plugin is not in `config.yml`. It is `logLevel` in `plugins/PickMeUp/base_configuration.yml`.
Leave it at `INFO` unless support asks you to change it.
:::

## `generalSettings`

| Key | Default | What it does |
|---|---|---|
| `language` | `en_US` | The language of the plugin messages. Included are `en_US`, `de_DE` and `zh_CN`. |
| `updates.notice` | `true` | Tells the console and players with the `pickmeup.updates` permission when a new version is out. Pick Me Up looks on start and every six hours. |
| `updates.autoUpdate` | `false` | Downloads a new stable release into the server's update folder. It is applied on the next restart. Only versions with the same major version are downloaded, so 2.x never updates to 3.0 on its own. |
| `updates.askForAutoUpdate` | `true` | Asks admins in chat whether to download a new version and whether to turn on `autoUpdate`. |

The message files are in `plugins/PickMeUp/messages`. You can edit them to change the texts. A message file for
another language that you put there is picked up too. Set `language` to its name.

:::tip The update offer in chat
When `askForAutoUpdate` is on, admins get an offer in chat: download the new version, turn on auto update, ask again
later, or never ask again. Pick Me Up saves the choice to `config.yml`. The offer uses the command
`/pickmeup:eldo-update`. See [Commands](./reference/commands.md#pickmeupeldo-update).
:::

## `carrySettings`

| Key | Default | What it does |
|---|---|---|
| `throwForce` | `2.0` | How hard a throw with a full power bar is. Higher values throw further. A half-full bar throws with a quarter of this. A negative value counts as `0`. A throw is never faster than 4 blocks per tick, so values above `4` only make weaker charges stronger. |
| `throwDelay` | `10` | How long, in ticks, a player has to hold sneak while carrying before the power bar appears. 20 ticks are one second. Letting go of sneak earlier sets the entity down. |
| `allowStacking` | `false` | Lets players pick up an entity that is carrying something itself. Without it, only players with `pickmeup.bypass.nostack` can. |
| `maximumStacking` | `0` | The highest stack a player may pick up. It counts the entity and everything riding on it. `0` or less means no limit. Players with `pickmeup.bypass.maxstack` have no limit. |
| `leftClickPickup` | `false` | Also lets players pick up by punching an entity while sneaking with an empty hand. Meant for Bedrock players. The punch does no damage when it picks up. |
| `leftClickPickupPlayers` | `false` | Lets punching pick up players too. Only works when `leftClickPickup` is `true`. |

`maximumStacking` is only checked when the entity carries something. A single entity can always be picked up.

See [Picking up and throwing](./using.md) for how throwing and stacking work in the game.

## `mobSettings`

| Key | Default | What it does |
|---|---|---|
| `allowedMobs` | `PIG`, `CHICKEN`, `RABBIT`, `WOLF`, `OAK_BOAT`, `SHEEP`, `PARROT` | A list of entity type names. Players are `PLAYER`. |
| `blacklist` | `false` | `false`: only the listed types can be picked up. `true`: all types except the listed ones can be picked up. |
| `requirePermission` | `false` | When `true`, players also need `pickmeup.pickup.<entity type>` for each type, in lower case. |

See [Which entities can be picked up](./entities.md) for examples.

## `worldSettings`

| Key | Default | What it does |
|---|---|---|
| `restrictWorlds` | `false` | When `false`, Pick Me Up works in every world and the other two keys are ignored. |
| `blacklist` | `false` | `false`: pick-up only works in the listed worlds. `true`: it works everywhere except there. |
| `worlds` | `world` | A list of world names. |
