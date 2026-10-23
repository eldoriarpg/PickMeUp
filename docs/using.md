---
title: Picking up and throwing
sidebar_position: 3
---

# Picking up and throwing

This page is for your players. Everything works with the sneak key, the right mouse button and an empty hand.

## Pick up

Sneak and right-click an entity while your main hand is empty. The entity sits on top of you.

It only works when all of this is true:

- Your main hand is empty.
- You are not carrying anything yet. You carry one entity at a time.
- The entity does not sit in a vehicle, and nobody else carries it.
- You are in a world where Pick Me Up is turned on.
- The entity is one your server allows you to pick up. See [Which entities can be picked up](./entities.md).
- You are allowed to pick up at that spot, if the server uses a protection plugin.
- A player you want to pick up has not turned it off. See [below](#players-who-do-not-want-to-be-picked-up).

When nothing happens, one of these is the reason. Some reasons are shown in the action bar above your hotbar.

:::tip Bedrock players
If the server turns on `leftClickPickup`, you can also pick up by punching. Sneak and left-click the entity with an
empty hand. The punch does no damage when it picks something up. Whether punching also picks up players is a
separate setting.
:::

## Players who do not want to be picked up

Every player decides whether others may pick them up. Run `/pickmeup toggle` to turn it off, and again to turn it
back on. Your choice is kept when you log out and when the server restarts.

When you try to pick up a player who turned it off, the action bar tells you, for example "Alex does not want to be
picked up." Players with the `pickmeup.bypass.toggle` permission can pick them up anyway.

## Carry

Stop sneaking after the pick-up. Now you can walk, run and jump, and the entity stays on top of you.

A carried player can get off by sneaking. When the entity you carry gets off or dies, you carry nothing anymore. You
also drop what you carry when you teleport, die or leave the server.

## Set down

Press sneak shortly and let go before the power bar appears. By default that is within half a second. The entity drops
off at your position.

You can also cancel a throw. While you hold sneak for a throw, right-click the entity you carry with an empty hand.
It drops off and the throw is called off.

After you set an entity down, nobody can pick it up again for one second.

## Throw

1. While carrying, hold sneak.
2. After half a second a power bar appears above your hotbar. It runs from blue to red.
3. The bar fills up in about one and a half seconds. Then it empties again, then fills again, and so on.
4. Let go of sneak. The entity flies in the direction you look.

The fuller the bar, the harder the throw. Let go when the bar is red for the strongest throw. The throw is always as
strong as the bar shows, however long you hold sneak.

A thrown entity leaves a trail of green sparkles. The trail stops when the entity lands, falls into water, or after
five seconds.

## Stacking

Sometimes the entity you want already carries someone. For example, a player who holds a chicken. By default you
cannot pick up such an entity, and you see "You can not pick up something with a passenger."

Server owners can allow it with `allowStacking`, or give single players the `pickmeup.bypass.nostack` permission.
Then you can pick up a player together with whatever they carry, and build a tower.

The server can limit how high that tower gets with `maximumStacking`. When the stack you want to pick up is too high,
you see for example "You can not carry a stack higher than 3." Players with `pickmeup.bypass.maxstack` have no limit.
