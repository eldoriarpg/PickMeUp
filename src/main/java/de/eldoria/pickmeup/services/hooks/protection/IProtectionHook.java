package de.eldoria.pickmeup.services.hooks.protection;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface IProtectionHook {
    void init(Plugin plugin);

    /**
     * Checks if the player can interact with the entity at a given location
     *
     * @param player   player which wants to interact
     * @param entity   the entity which should be picked up
     * @param location the location where the entity should be picked up. Convenience value for {@link Entity#getLocation()}
     * @return true when the player is allowed to pick the entity up.
     */
    boolean canInteract(Player player, Entity entity, Location location);

    String pluginName();
}
