---
title: Permissions
sidebar_position: 2
---

# Permissions

Pick Me Up registers its permissions with the server, each with a default. "Operators" means operators have it and
other players need it granted through a permission plugin. "Everyone" means every player has it unless you take it
away.

| Permission | Default | Allows |
|---|---|---|
| `pickmeup.toggle` | Everyone | `/pickmeup toggle`, to decide whether other players can pick you up. |
| `pickmeup.reload` | Operators | `/pickmeup reload` and `/pickmeup debug`. |
| `pickmeup.updates` | Operators | Hearing about new versions on join, and downloading them from the offer in chat. |
| `pickmeup.bypass.nostack` | Operators | Picking up an entity that carries something, even when `allowStacking` is `false`. |
| `pickmeup.bypass.maxstack` | Operators | Picking up a stack higher than `maximumStacking`. |
| `pickmeup.bypass.toggle` | Operators | Picking up players who turned it off with `/pickmeup toggle`. |

Two more permissions are checked but not registered. Operators have them, and other players have none until you grant
them:

| Permission | Allows |
|---|---|
| `pickmeup.pickup.<entity type>` | Picking up this entity type, for example `pickmeup.pickup.pig`. Only checked when `requirePermission` is `true`. |
| `eldoutilitites.debug` | `/pickmeup debug` |

Picking up itself needs no permission. When `requirePermission` is `false`, which is the default, every player can
pick up every allowed entity type.

The entity type in `pickmeup.pickup.<entity type>` is the Bukkit entity type name in lower case. Some examples:

| Entity | Permission |
|---|---|
| Pig | `pickmeup.pickup.pig` |
| Oak boat | `pickmeup.pickup.oak_boat` |
| Player | `pickmeup.pickup.player` |

See [Which entities can be picked up](../entities.md) for how the list and the permission work together.
