package de.eldoria.pickmeup.services.hooks.protection;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Allows picking up inside a claim to players with container trust, the trust GriefPrevention protects animals with.
 */
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
    public boolean canInteract(Player player, Entity entity, Location location) {
        if (!griefPrevention.claimsEnabledForWorld(location.getWorld())) {
            return true;
        }
        Claim claim = griefPrevention.dataStore.getClaimAt(location, true, null);
        return claim == null || claim.checkPermission(player, ClaimPermission.Inventory, null) == null;
    }
}
