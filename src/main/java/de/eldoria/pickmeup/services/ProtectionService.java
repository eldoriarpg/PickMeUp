package de.eldoria.pickmeup.services;

import de.eldoria.pickmeup.services.hooks.protection.AProtectionHook;
import de.eldoria.pickmeup.services.hooks.protection.BentoBoxHook;
import de.eldoria.pickmeup.services.hooks.protection.GriefPreventionHook;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProtectionService {
    private final Plugin plugin;
    private List<AProtectionHook> hooks = new ArrayList<>();

    public ProtectionService(Plugin plugin) {
        this.plugin = plugin;
    }

    public static ProtectionService of(Plugin plugin) {
        ProtectionService protectionService = new ProtectionService(plugin);
        protectionService.init();
        return protectionService;
    }

    private void init() {
        PluginManager pm = plugin.getServer().getPluginManager();
        Arrays.stream(hooks())
                .filter(hook -> pm.isPluginEnabled(hook.pluginName()))
                .forEach(hook -> {
                    hook.init(plugin);
                    hooks.add(hook);
                });
    }

    public boolean canInteract(Player player, Location location) {
        for (AProtectionHook hook : hooks) {
            if (!hook.canInteract(player, location)) return false;
        }
        return true;
    }

    private static AProtectionHook[] hooks() {
        return new AProtectionHook[]{new BentoBoxHook(), new GriefPreventionHook()};
    }
}
