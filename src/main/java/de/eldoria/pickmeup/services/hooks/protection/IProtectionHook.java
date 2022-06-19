package de.eldoria.pickmeup.services.hooks.protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface IProtectionHook {
    void init(Plugin plugin);

    boolean canInteract(Player player, Location location);

    String pluginName();
}
