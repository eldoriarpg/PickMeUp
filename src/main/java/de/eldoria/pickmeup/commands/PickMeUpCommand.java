package de.eldoria.pickmeup.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.defaultcommands.DefaultAbout;
import de.eldoria.eldoutilities.commands.defaultcommands.DefaultDebug;
import de.eldoria.pickmeup.util.Permissions;
import org.bukkit.plugin.Plugin;

public class PickMeUpCommand extends AdvancedCommand {
    public PickMeUpCommand(Plugin plugin) {
        super(plugin, CommandMeta.builder("pickmeup")
                .withSubCommand(new DefaultDebug(plugin, Permissions.RELOAD))
                .withSubCommand(new Reload(plugin))
                .withSubCommand(new DefaultAbout(plugin))
                .build());
    }
}
