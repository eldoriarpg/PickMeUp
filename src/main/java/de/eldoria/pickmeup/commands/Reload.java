package de.eldoria.pickmeup.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.ITabExecutor;
import de.eldoria.pickmeup.PickMeUp;
import de.eldoria.pickmeup.util.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Reload extends AdvancedCommand implements ITabExecutor {
    public Reload(Plugin plugin) {
        super(plugin, CommandMeta.builder("reload")
                .withPermission(Permissions.RELOAD)
                .build());
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        plugin().onEnable();
        messageSender().sendMessage(sender, "reload.success");
        PickMeUp.logger().info("PickMeUp reloaded!");
    }
}
