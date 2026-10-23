/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.pickmeup.services.hooks.protection;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Asks the {@link WorldGuardFlag pickmeup flag} of the regions at the entity. Regions without the flag allow picking
 * up; players with WorldGuard bypass are always allowed.
 */
public class WorldGuardHook extends AProtectionHook {
    private StateFlag flag;

    public WorldGuardHook() {
        super("WorldGuard");
    }

    @Override
    public void init(Plugin plugin) {
        flag = WorldGuardFlag.flag()
                .orElseThrow(() -> new IllegalStateException("The " + WorldGuardFlag.NAME + " flag is not registered"));
    }

    @Override
    public boolean canInteract(Player player, Entity entity, Location location) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        WorldGuard worldGuard = WorldGuard.getInstance();
        if (worldGuard.getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld())) {
            return true;
        }
        RegionQuery query = worldGuard.getPlatform().getRegionContainer().createQuery();
        return query.testState(BukkitAdapter.adapt(location), localPlayer, flag);
    }
}
