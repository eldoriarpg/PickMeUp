/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.pickmeup.services.hooks.protection;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CitizensHook extends AProtectionHook {
    public CitizensHook() {
        super("Citizens");
    }

    @Override
    public void init(Plugin plugin) {
    }

    @Override
    public boolean canInteract(Player player, Entity entity, Location location) {
        return !entity.hasMetadata("NPC");
    }
}
