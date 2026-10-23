---
title: Pick Me Up
sidebar_position: 1
---

# Pick Me Up

Pick Me Up lets players pick up mobs, carry them around and throw them. Sneak, right-click a pig with an empty hand,
and the pig sits on your head. Walk it home, set it down, or hold sneak and send it flying.

You choose which entities can be picked up. That can be a short list of friendly animals, or nearly everything,
players included.

This documentation describes Pick Me Up 2.0. Coming from 1.x? See [Updating from 1.x](./updating.md).

## What it can do

- **Pick up and carry.** Sneak and right-click an entity with an empty hand. It rides on top of you while you walk.
  If you allow it, Bedrock players can punch instead. See [Picking up and throwing](./using.md).
- **Throw.** Hold sneak while carrying. A colored power bar fills up, and the throw gets stronger the fuller the bar
  is. Thrown entities leave a small particle trail.
- **Decide what can be picked up.** Use an allow list or a block list of entity types, ask for a permission per type,
  and limit the plugin to some worlds. See [Which entities can be picked up](./entities.md).
- **Let players opt out.** With `/pickmeup toggle`, players decide whether others may pick them up.
- **Stack them.** If you allow it, players can pick up someone who is already carrying someone, up to a height you
  choose.
- **Respect claims.** With BentoBox, GriefPrevention, HuskClaims, PlotSquared, Towny, RedProtect or WorldGuard installed, players can only pick
  up entities where they are allowed to. Citizens NPCs can never be picked up. See
  [Protection plugins](./entities.md#protection-plugins).
- **Stay up to date.** Pick Me Up tells you about new versions and can download them for you.

## Requirements

| What | Version |
|---|---|
| Server | Paper or Folia 1.21.11 or newer, 26.x included |
| Java | 25 or newer |
| Other plugins | None required |
| Internet | On the first start, to download the libraries |
| Optional | BentoBox, GriefPrevention, HuskClaims, PlotSquared, Towny, RedProtect, WorldGuard, Citizens |

## Where to go next

- [Getting started](./getting-started.md) installs the plugin and shows the first steps.
- [Picking up and throwing](./using.md) explains the controls for your players.
- [Which entities can be picked up](./entities.md) explains the entity list, permissions, worlds and claims.
- [Configuration](./configuration.md) lists every setting in `config.yml`.
- [Updating from 1.x](./updating.md) explains what happens to your old configuration.
- [Commands](./reference/commands.md) and [Permissions](./reference/permissions.md) are the reference tables.
