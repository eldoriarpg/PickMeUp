---
title: Getting started
sidebar_position: 2
---

# Getting started

This page takes you from an empty plugin folder to your first thrown pig.

## Install the plugin

1. Check that your server runs **Paper 1.21.11 or newer** on **Java 25 or newer**. Paper 26.x and Folia work as well.
2. Download the Pick Me Up jar from [Hangar](https://hangar.papermc.io/eldoriarpg/PickMeUp) or
   [Lyna](https://lyna.eldoria.de/products/5) and put it into the `plugins` folder.
3. Restart the server. Do not use `/reload`.

Pick Me Up needs no other plugins. If BentoBox, GriefPrevention, HuskClaims, PlotSquared, Towny, RedProtect, WorldGuard or Citizens is
installed, Pick Me Up hooks into it on start. The server log says which hooks were enabled.

After the first start you find `plugins/PickMeUp/config.yml`. It holds every setting. See
[Configuration](./configuration.md).

:::tip Coming from 1.x
Replace the old jar and restart. Your old `config.yml` is converted on the first start. See
[Updating from 1.x](./updating.md).
:::

## Try it out

Out of the box, players can pick up pigs, chickens, rabbits, wolves, oak boats, sheep and parrots. No permission is
needed for that.

1. Stand next to a pig with an empty hand.
2. Sneak and right-click the pig. It now sits on top of you.
3. Stop sneaking and walk around. The pig comes with you.
4. Hold sneak. After half a second a power bar appears above your hotbar.
5. Let go of sneak. The pig flies off in the direction you look.

The full controls are on [Picking up and throwing](./using.md).

## Next steps

- Decide which entities your players may pick up, and whether they need a permission for it. See
  [Which entities can be picked up](./entities.md).
- Change the throw strength, the throw delay or the language in the [Configuration](./configuration.md).
- Have Bedrock players? Turn on `leftClickPickup`, so they can pick up by punching. See
  [`carrySettings`](./configuration.md#carrysettings).
- After editing `config.yml`, run `/pickmeup reload`. See [Commands](./reference/commands.md).

:::info Update checks and statistics
Pick Me Up checks for new versions on start and every six hours. It tells the console and players with the
`pickmeup.updates` permission about them, and can download them for you. See
[`generalSettings`](./configuration.md#generalsettings). The plugin also sends anonymous usage statistics through
bStats.
:::
