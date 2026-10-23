package de.eldoria.pickmeup.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.executor.ITabExecutor;
import de.eldoria.pickmeup.PickMeUp;
import de.eldoria.pickmeup.util.Permissions;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Reload extends AdvancedCommand implements ITabExecutor {
    private final PickMeUp plugin;

    public Reload(PickMeUp plugin) {
        super(plugin, CommandMeta.builder("reload")
                .withPermission(Permissions.RELOAD)
                .build());
        this.plugin = plugin;
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) {
        plugin.reload();
        messageSender().sendMessage(sender, "reload.success");
        PickMeUp.logger().info("PickMeUp reloaded!");
    }
}
