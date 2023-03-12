package de.eldoria.pickmeup.services.hooks.protection;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TownyHook extends AProtectionHook {
    private TownyAPI townyAPI;

    public TownyHook() {
        super("Towny");
    }

    @Override
    public void init(Plugin plugin) {
        townyAPI = TownyAPI.getInstance();
    }

    @Override
    public boolean canInteract(Player player, Entity entity, Location location) {
        if (!townyAPI.isTownyWorld(location.getWorld())) {
            return true;
        }
        return PlayerCacheUtil.getCachePermission(player, location, location.getBlock().getType(), TownyPermission.ActionType.DESTROY);
    }
}
