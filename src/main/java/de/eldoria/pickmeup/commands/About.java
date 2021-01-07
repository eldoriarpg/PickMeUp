package de.eldoria.pickmeup.commands;

import de.eldoria.eldoutilities.localization.Replacement;
import de.eldoria.eldoutilities.simplecommands.EldoCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

public class About extends EldoCommand {
    public About(Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        PluginDescriptionFile descr = getPlugin().getDescription();
        messageSender().sendLocalizedMessage(sender, "about",
                Replacement.create("PLUGIN_NAME", descr.getName(), 'b'),
                Replacement.create("AUTHORS", String.join(", ", descr.getAuthors()), 'b'),
                Replacement.create("VERSION", descr.getVersion(), 'b'),
                Replacement.create("WEBSITE", descr.getWebsite(), 'b'),
                Replacement.create("DISCORD", "https://discord.gg/rfRuUge", 'b'));
        return true;
    }

}
