package de.eldoria.pickmeup.services;

import de.eldoria.pickmeup.services.hooks.protection.BentoBoxHook;
import de.eldoria.pickmeup.services.hooks.protection.GriefPreventionHook;
import de.eldoria.pickmeup.services.hooks.protection.IProtectionHook;
import de.eldoria.pickmeup.services.hooks.protection.PlotSquaredHook;
import de.eldoria.pickmeup.services.hooks.protection.RedProtectHook;
import de.eldoria.pickmeup.services.hooks.protection.TownyHook;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class ProtectionService {
    private final Plugin plugin;
    private final List<IProtectionHook> hooks = new ArrayList<>();

    public ProtectionService(Plugin plugin) {
        this.plugin = plugin;
    }

    public static ProtectionService of(Plugin plugin) {
        var protectionService = new ProtectionService(plugin);
        protectionService.init();
        return protectionService;
    }

    private void init() {
        plugin.getLogger().info("Setting up protection hooks");
        PluginManager pm = plugin.getServer().getPluginManager();
        Arrays.stream(hooks())
                .filter(hook -> {
                    if (pm.isPluginEnabled(hook.pluginName())) {
                        return true;
                    }
                    plugin.getLogger().info(hook.pluginName() + " not found. Skipping protection hook.");
                    return false;
                })
                .forEach(hook -> {
                    try {
                        hook.init(plugin);
                    } catch (Throwable e) {
                        plugin.getLogger().log(Level.WARNING, "Failed to enable protection hook for " + hook.pluginName(), e);
                        plugin.getLogger().log(Level.WARNING, "Please make sure you are using the latest version. If you are please report this error.");
                        return;
                    }
                    hooks.add(hook);
                    plugin.getLogger().info("Enabled protection hook for " + hook.pluginName());
                });
    }

    public boolean canInteract(Player player, Entity entity) {
        for (IProtectionHook hook : hooks) {
            if (!hook.canInteract(player, entity, entity.getLocation())) return false;
        }
        return true;
    }

    private static IProtectionHook[] hooks() {
        return new IProtectionHook[]{new BentoBoxHook(), new GriefPreventionHook(), new PlotSquaredHook(), new TownyHook(), new RedProtectHook()};
    }
}
