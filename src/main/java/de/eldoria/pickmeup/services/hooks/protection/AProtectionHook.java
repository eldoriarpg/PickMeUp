package de.eldoria.pickmeup.services.hooks.protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class AProtectionHook {
    private final String pluginName;

    public AProtectionHook(String pluginName) {
        this.pluginName = pluginName;
    }

    public abstract void init(Plugin plugin);

    public abstract boolean canInteract(Player player, Location location);

    public String pluginName() {
        return pluginName;
    }
}
