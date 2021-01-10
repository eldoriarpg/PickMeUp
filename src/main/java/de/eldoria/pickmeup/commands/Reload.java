package de.eldoria.pickmeup.commands;

import de.eldoria.eldoutilities.simplecommands.EldoCommand;
import de.eldoria.pickmeup.PickMeUp;
import de.eldoria.pickmeup.util.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Reload extends EldoCommand {
    public Reload(Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (denyAccess(sender, Permissions.RELOAD)) {
            return true;
        }
        PickMeUp.getInstance(PickMeUp.class).onEnable();
        messageSender().sendLocalizedMessage(sender, "reload.success");
        PickMeUp.logger().info("PickMeUp reloaded!");
        return true;
    }
}
