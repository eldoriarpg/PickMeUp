package de.eldoria.pickmeup.listener;

import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.pickmeup.PickMeUp;
import de.eldoria.pickmeup.config.Configuration;
import de.eldoria.pickmeup.util.Permissions;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class CarryListener implements Listener {
    private final Configuration config;
    MessageSender messageSender;

    public CarryListener(Configuration config) {
        this.config = config;
        MessageSender pluginMessageSender = MessageSender.getPluginMessageSender(PickMeUp.class);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        if (event.getPlayer().getEquipment().getItemInMainHand().getType() != Material.AIR) return;
        if (!config.canPickUpMob(event.getRightClicked().getType())) return;
        if (!event.getPlayer().getPassengers().isEmpty()) return;
        if (!event.getPlayer().isSneaking()) return;
        if (!event.getPlayer().hasPermission(Permissions.getPickUpPermission(event.getRightClicked().getType()))) {
            messageSender.sendLocalizedError(event.getPlayer(), "noperm");
            return;
        }
        if (!event.getRightClicked().getPassengers().isEmpty() && !config.allowStacking()) {
            if (!event.getPlayer().hasPermission(Permissions.BYPASS_NOSTACK)) {
                messageSender.sendLocalizedError(event.getPlayer(), "nostack");
                return;
            }
        }
        event.getPlayer().addPassenger(event.getRightClicked());
        event.setCancelled(true);
    }

    @EventHandler
    public void onDismount(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;

        for (Entity passenger : event.getPlayer().getPassengers()) {
            event.getPlayer().removePassenger(passenger);
        }
    }

}
