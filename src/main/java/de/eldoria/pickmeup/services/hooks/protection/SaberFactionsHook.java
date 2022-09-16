package de.eldoria.pickmeup.services.hooks.protection;

import com.massivecraft.factions.listeners.FactionsPlayerListener;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SaberFactionsHook extends AProtectionHook {
    public SaberFactionsHook() {
        super("Factions");
    }

    @Override
    public void init(Plugin plugin) {
    }

    @Override
    public boolean canInteract(Player player, Location location) {
        return FactionsPlayerListener.playerCanUseItemHere(player, location, Material.BUCKET, false, PermissableAction.ITEM);
    }
}
