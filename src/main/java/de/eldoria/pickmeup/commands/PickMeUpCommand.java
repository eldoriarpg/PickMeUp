package de.eldoria.pickmeup.commands;

import de.eldoria.eldoutilities.simplecommands.EldoCommand;
import org.bukkit.plugin.Plugin;

public class PickMeUpCommand extends EldoCommand {
    public PickMeUpCommand(Plugin plugin) {
        super(plugin);
        registerCommand("reload", new Reload(plugin));
        registerCommand("about", new About(plugin));
    }
}
