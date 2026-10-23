/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.pickmeup.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.defaultcommands.DefaultAbout;
import de.eldoria.eldoutilities.commands.defaultcommands.DefaultDebug;
import de.eldoria.pickmeup.PickMeUp;
import de.eldoria.pickmeup.util.Permissions;
import de.eldoria.pickmeup.util.PickupToggle;

public class PickMeUpCommand extends AdvancedCommand {
    public PickMeUpCommand(PickMeUp plugin, PickupToggle pickupToggle) {
        super(
                plugin,
                CommandMeta.builder("pickmeup")
                        .withSubCommand(new DefaultDebug(plugin, Permissions.RELOAD))
                        .withSubCommand(new Reload(plugin))
                        .withSubCommand(new Toggle(plugin, pickupToggle))
                        .withSubCommand(new DefaultAbout(plugin))
                        .build());
    }
}
