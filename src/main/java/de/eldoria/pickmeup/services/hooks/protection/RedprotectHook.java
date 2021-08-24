package de.eldoria.pickmeup.services.hooks.protection;

import br.net.fabiozumbi12.RedProtect.Bukkit.API.RedProtectAPI;
import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class RedprotectHook extends AProtectionHook {
    private RedProtectAPI redProtectAPI;

    public RedprotectHook() {
        super("RedProtect");
    }

    @Override
    public void init(Plugin plugin) {
        redProtectAPI = RedProtect.get().getAPI();
    }

    @Override
    public boolean canInteract(Player player, Location location) {
        Region region = redProtectAPI.getRegion(location);
        if (region == null) {
            return true;
        }
        return region.canBuild(player);
    }
}
