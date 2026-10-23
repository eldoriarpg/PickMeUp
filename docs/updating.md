---
title: Updating from 1.x
sidebar_position: 6
---

# Updating from 1.x

Pick Me Up 2.0 needs **Paper 1.21.11 or newer** (Folia works too) and **Java 25**. Check both before you update.

To update, stop the server, replace the old jar in the `plugins` folder with the new one, and start the server again.
You do not have to change anything by hand.

## What happens on the first start

Pick Me Up sees that `config.yml` was written by 1.x, because it contains `==:` lines. Then it:

1. Saves your old file as `plugins/PickMeUp/config.v1.yml`.
2. Converts `config.yml`. All your settings are kept. New settings are added with their default value.
3. Moves the old message files from `plugins/PickMeUp/messages` to `plugins/PickMeUp/messages.v1`, and writes fresh
   ones.

The server log reports each step.

:::warning Edited message files
The message files of 1.x do not work with 2.0. If you had changed texts, copy your changes from `messages.v1` into
the new files in `messages`.
:::

## What changed in the configuration

| 1.x | 2.0 |
|---|---|
| `==:` lines | Gone. The file is plain YAML now. |
| `debug` | Gone from `config.yml`. The log level is `logLevel` in `plugins/PickMeUp/base_configuration.yml`. |
| `generalSettings.updateCheck` | Now `generalSettings.updates.notice`. Your old value is kept. |

New settings are `carrySettings.throwDelay`, `maximumStacking`, `leftClickPickup` and `leftClickPickupPlayers`, and
`generalSettings.updates.autoUpdate` and `askForAutoUpdate`. See [Configuration](./configuration.md).

## Entity names

Some entity types have new names in current Minecraft versions. Pick Me Up still understands the old names in
`allowedMobs`, so you do not have to change your list:

| Old name | Read as |
|---|---|
| `BOAT` | `OAK_BOAT` |
| `CHEST_BOAT` | `OAK_CHEST_BOAT` |
| `MUSHROOM_COW` | `MOOSHROOM` |
| `SNOWMAN` | `SNOW_GOLEM` |
| `MINECART_CHEST`, `MINECART_TNT` and the other `MINECART_*` names | `CHEST_MINECART`, `TNT_MINECART` and so on |

A name the server does not know stays in the file, but is ignored. The server log reports it once.

## Permissions and update messages

- Messages about new versions now go to players with `pickmeup.updates` instead of `pickmeup.reload`.
- All permissions except `pickmeup.pickup.<entity type>` and `eldoutilitites.debug` are now registered with a
  default. See [Permissions](./reference/permissions.md).
- `/pickmeup toggle` is new. Every player can use it to stop others from picking them up.
