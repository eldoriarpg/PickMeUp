/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.pickmeup.services.hooks.protection;

import net.william278.huskclaims.api.BukkitHuskClaimsAPI;
import net.william278.huskclaims.libraries.cloplib.operation.OperationType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Allows picking up where HuskClaims lets the player interact with entities, so claim trust levels and flags decide
 * as they do for right-clicking an entity.
 */
public class HuskClaimsHook extends AProtectionHook {
    private BukkitHuskClaimsAPI huskClaims;

    public HuskClaimsHook() {
        super("HuskClaims");
    }

    @Override
    public void init(Plugin plugin) {
        huskClaims = BukkitHuskClaimsAPI.getInstance();
    }

    @Override
    public boolean canInteract(Player player, Entity entity, Location location) {
        return huskClaims.isOperationAllowed(
                huskClaims.getOnlineUser(player), OperationType.ENTITY_INTERACT, huskClaims.getPosition(location));
    }
}
