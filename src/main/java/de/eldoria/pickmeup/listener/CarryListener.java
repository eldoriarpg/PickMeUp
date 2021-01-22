package de.eldoria.pickmeup.listener;

import de.eldoria.eldoutilities.core.EldoUtilities;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.pickmeup.PickMeUp;
import de.eldoria.pickmeup.config.Configuration;
import de.eldoria.pickmeup.scheduler.ThrowBarHandler;
import de.eldoria.pickmeup.util.Permissions;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CarryListener implements Listener {
    private final Configuration config;
    private final Plugin plugin;
    private final ThrowBarHandler throwBarHandler;
    private final Set<UUID> blocked = new HashSet<>();
    private final Map<UUID, MountState> mountStates = new HashMap<>();
    private MessageSender messageSender;

    public CarryListener(Plugin plugin, Configuration config, ThrowBarHandler handler) {
        this.plugin = plugin;
        this.throwBarHandler = handler;
        this.config = config;
        MessageSender pluginMessageSender = MessageSender.getPluginMessageSender(PickMeUp.class);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        if (!config.getWorldSettings().allowInWorld(event.getPlayer().getWorld())) return;

        if (blocked.contains(event.getRightClicked().getUniqueId())) return;

        MountState mountState = mountStates.get(event.getPlayer().getUniqueId());
        if (mountState == MountState.SNEAK_THROW) {
            if (event.getPlayer().getPassengers().contains(event.getRightClicked())
                    && event.getPlayer().getEquipment().getItemInMainHand().getType() == Material.AIR) {
                unmountAll(event.getPlayer());
                throwBarHandler.getAndRemove(event.getPlayer());
                mountStates.remove(event.getPlayer().getUniqueId());
                blocked.add(event.getRightClicked().getUniqueId());
                EldoUtilities.getDelayedActions().schedule(() -> blocked.remove(event.getRightClicked().getUniqueId()), 20);
                event.setCancelled(true);
                return;
            }
        }


        Player player = event.getPlayer();
        if (player.getEquipment().getItemInMainHand().getType() != Material.AIR) return;
        if (!config.getMobSettings().canBePickedUp(event.getPlayer(), event.getRightClicked().getType())) return;
        if (!player.getPassengers().isEmpty()) return;
        if (!player.isSneaking()) return;

        if (!event.getRightClicked().getPassengers().isEmpty() && !config.getCarrySettings().isAllowStacking()) {
            if (!player.hasPermission(Permissions.BYPASS_NOSTACK)) {
                messageSender.sendLocalizedError(player, "nostack");
                return;
            }
        }

        mountStates.put(player.getUniqueId(), MountState.SNEAK_MOUNT);
        player.addPassenger(event.getRightClicked());
        event.setCancelled(true);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!mountStates.containsKey(player.getUniqueId())) {
            unmountAll(player);
            return;
        }

        MountState mountState = mountStates.get(player.getUniqueId());

        if (!event.isSneaking() && mountState == MountState.SNEAK_MOUNT) {
            mountStates.put(player.getUniqueId(), MountState.WALKING);
            return;
        }

        if (event.isSneaking() && mountState == MountState.WALKING) {
            mountStates.put(player.getUniqueId(), MountState.SNEAK_THROW);
            EldoUtilities.getDelayedActions().schedule(() -> {
                        if (player.isSneaking()) {
                            throwBarHandler.register(player);
                        }
                    }
                    , 10);
            return;
        }

        if (!event.isSneaking() && mountState == MountState.SNEAK_THROW) {
            if (!throwBarHandler.isRegistered(player)) {
                unmountAll(player);
            } else {
                double force = throwBarHandler.getAndRemove(player);
                Vector viewVec = player.getEyeLocation().getDirection().normalize().multiply(force * config.getCarrySettings().getThrowForce());
                for (Entity passenger : player.getPassengers()) {
                    player.removePassenger(passenger);
                    passenger.setVelocity(viewVec);
                    plugin.getLogger().config("Throwing entity | Location:" + player.getLocation().toVector().toString()
                            + " | Force: " + force
                            + " | ThrowForce: " + config.getCarrySettings().getThrowForce()
                            + " | ViewVec: " + viewVec.toString());
                }
            }
            mountStates.remove(player.getUniqueId());
        }
    }

    private void unmountAll(Player player) {
        for (Entity passenger : player.getPassengers()) {
            player.removePassenger(passenger);
        }
    }

    private enum MountState {
        SNEAK_MOUNT, WALKING, SNEAK_THROW
    }
}
