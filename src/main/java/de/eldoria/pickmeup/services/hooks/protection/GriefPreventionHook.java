package de.eldoria.pickmeup.services.hooks.protection;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class GriefPreventionHook extends AProtectionHook {
    private GriefPrevention griefPrevention;

    public GriefPreventionHook() {
        super("GriefPrevention");
    }

    @Override
    public void init(Plugin plugin) {
        griefPrevention = GriefPrevention.instance;
    }

    @Override
    public boolean canInteract(Player player, Location location) {
        if (!griefPrevention.claimsEnabledForWorld(location.getWorld())) {
            return true;
        }
        return griefPrevention.allowBreak(player, location.getBlock(), location) == null;
    }
}
