---
title: Permissions
sidebar_position: 2
---

# Permissions

Pick Me Up does not register its permissions with the server. That means operators have all of them, and other
players have none until you grant them. Use a permission plugin to hand them out.

| Permission | Allows |
|---|---|
| `pickmeup.pickup.<entity type>` | Picking up this entity type, for example `pickmeup.pickup.pig`. Only checked when `requirePermission` is `true`. |
| `pickmeup.bypass.nostack` | Picking up an entity that carries something, even when `allowStacking` is `false`. |
| `pickmeup.reload` | `/pickmeup reload` and `/pickmeup debug`. Also receives the message about new versions. |
| `eldoutilitites.debug` | `/pickmeup debug` |

Picking up itself needs no permission. When `requirePermission` is `false`, which is the default, every player can
pick up every allowed entity type.

The entity type in `pickmeup.pickup.<entity type>` is the Bukkit entity type name in lower case. Some examples:

| Entity | Permission |
|---|---|
| Pig | `pickmeup.pickup.pig` |
| Player | `pickmeup.pickup.player` |

See [Which entities can be picked up](../entities.md) for how the list and the permission work together.
