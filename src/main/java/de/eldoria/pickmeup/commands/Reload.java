package de.eldoria.pickmeup.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.AdvancedCommandAdapter;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IConsoleTabExecutor;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.commands.executor.ITabExecutor;
import de.eldoria.eldoutilities.simplecommands.EldoCommand;
import de.eldoria.pickmeup.PickMeUp;
import de.eldoria.pickmeup.util.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import static com.palmergames.bukkit.towny.Towny.getPlugin;

public class Reload extends AdvancedCommand implements ITabExecutor {
    public Reload(Plugin plugin) {
        super(plugin, CommandMeta.builder("reload")
                .withPermission(Permissions.RELOAD)
                .build());
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        plugin().onEnable();
        messageSender().sendLocalizedMessage(sender, "reload.success");
        PickMeUp.logger().info("PickMeUp reloaded!");
    }
}
