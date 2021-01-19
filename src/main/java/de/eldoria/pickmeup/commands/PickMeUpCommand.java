package de.eldoria.pickmeup.commands;

import de.eldoria.eldoutilities.simplecommands.EldoCommand;
import de.eldoria.eldoutilities.simplecommands.commands.DefaultAbout;
import de.eldoria.eldoutilities.simplecommands.commands.DefaultDebug;
import de.eldoria.pickmeup.util.Permissions;
import org.bukkit.plugin.Plugin;

public class PickMeUpCommand extends EldoCommand {
    public PickMeUpCommand(Plugin plugin) {
        super(plugin);
        registerCommand("debug", new DefaultDebug(plugin, Permissions.RELOAD));
        registerCommand("reload", new Reload(plugin));
        registerCommand("about", new DefaultAbout(plugin));
    }
}
