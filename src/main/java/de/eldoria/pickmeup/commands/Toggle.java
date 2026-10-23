/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.pickmeup.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.pickmeup.util.Permissions;
import de.eldoria.pickmeup.util.PickupToggle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Lets a player decide whether other players may pick them up.
 */
public class Toggle extends AdvancedCommand implements IPlayerTabExecutor {
    private final PickupToggle pickupToggle;

    public Toggle(Plugin plugin, PickupToggle pickupToggle) {
        super(plugin, CommandMeta.builder("toggle")
                .withPermission(Permissions.TOGGLE)
                .build());
        this.pickupToggle = pickupToggle;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        boolean pickupable = pickupToggle.toggle(player);
        messageSender().sendMessage(player, pickupable ? "toggle.enabled" : "toggle.disabled");
    }
}
