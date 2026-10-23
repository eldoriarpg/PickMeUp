package de.eldoria.pickmeup.util;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

/**
 * Whether a player lets other players pick them up. The choice is stored on the player, so it survives restarts.
 */
public final class PickupToggle {
    private final NamespacedKey key;

    public PickupToggle(Plugin plugin) {
        key = new NamespacedKey(plugin, "pickupable");
    }

    public boolean isPickupable(Player player) {
        return player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.BOOLEAN, true);
    }

    /**
     * Flips whether the player can be picked up.
     *
     * @param player the player who changes their choice
     * @return whether the player can be picked up now
     */
    public boolean toggle(Player player) {
        boolean pickupable = !isPickupable(player);
        player.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, pickupable);
        return pickupable;
    }
}
