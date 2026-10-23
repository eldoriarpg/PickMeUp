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
- The entity is one your server allows you to pick up. See [Which entities can be picked up](./entities.md).
- You are in a world where Pick Me Up is turned on.
- You are allowed to build at that spot, if the server uses a protection plugin.

When nothing happens, one of these is the reason.

## Carry

Stop sneaking after the pick-up. Now you can walk, run and jump, and the entity stays on top of you.

## Set down

Press sneak shortly and let go within half a second. The entity drops off at your position.

You can also cancel a throw. While you hold sneak for a throw, right-click the entity you carry with an empty hand.
It drops off and the throw is called off. For one second after that, nobody can pick it up again.

## Throw

1. While carrying, hold sneak.
2. After half a second a power bar appears above your hotbar. It runs from blue to red.
3. The bar fills up in about one and a half seconds. Then it empties again, then fills again, and so on.
4. Let go of sneak. The entity flies in the direction you look.

The fuller the bar, the harder the throw. Let go when the bar is red for the strongest throw. A thrown entity leaves a
trail of green sparkles until it lands.

:::warning Holding too long
If you keep holding sneak after the bar has filled up for the second time, the throw gets stronger than the bar
shows. It keeps getting stronger the longer you hold.
:::

## Stacking

Sometimes the entity you want already carries someone. For example, a player who holds a chicken. By default you
cannot pick up such an entity, and you see "You cant pick up something with a passenger."

Server owners can allow it with `allowStacking`, or give single players the `pickmeup.bypass.nostack` permission.
Then you can pick up a player together with whatever they carry, and build a tower.
