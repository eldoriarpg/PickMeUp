package de.eldoria.pickmeup.services.hooks.protection;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.database.objects.Island;

import java.util.Optional;

public class BentoBoxHook extends AProtectionHook {
    private BentoBox bentoBox;

    public BentoBoxHook() {
        super("BentoBox");
    }

    @Override
    public void init(Plugin plugin) {
        bentoBox = BentoBox.getInstance();
    }

    @Override
    public boolean canInteract(Player player, Entity entity, Location location) {
        if (!bentoBox.getIWM().inWorld(location)) {
            return true;
        }
        Optional<Island> island = bentoBox.getIslandsManager().getProtectedIslandAt(location);
        return island.map(i -> i.getMemberSet().contains(player.getUniqueId())).orElse(false);
    }
}
