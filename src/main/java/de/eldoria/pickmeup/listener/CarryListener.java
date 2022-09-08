package de.eldoria.pickmeup.listener;

import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.scheduling.DelayedActions;
import de.eldoria.pickmeup.PickMeUp;
import de.eldoria.pickmeup.config.Configuration;
import de.eldoria.pickmeup.scheduler.ThrowBarHandler;
import de.eldoria.pickmeup.scheduler.TrailHandler;
import de.eldoria.pickmeup.services.ProtectionService;
import de.eldoria.pickmeup.util.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
    private ProtectionService protectionService;
    private final Plugin plugin;
    private final ThrowBarHandler throwBarHandler;
    private final Set<UUID> blocked = new HashSet<>();
    private final Map<UUID, MountState> mountStates = new HashMap<>();
    private final TrailHandler trailHandler;
    private final MessageSender messageSender;
    private final DelayedActions delayedActions;

    public CarryListener(Plugin plugin, Configuration config, ProtectionService protectionService) {
        this.plugin = plugin;
        this.throwBarHandler = new ThrowBarHandler(plugin);
        this.trailHandler = new TrailHandler(plugin);
        this.config = config;
        this.protectionService = protectionService;
        messageSender = MessageSender.getPluginMessageSender(PickMeUp.class);
        delayedActions = DelayedActions.start(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        entityCarryInteraction(event.getPlayer(), event.getRightClicked(), event);
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityInteract(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player player)
            entityCarryInteraction(player, event.getEntity(), event);
    }

    void entityCarryInteraction(Player player, Entity entity, Cancellable cancellable){
        if (!config.worldSettings().allowInWorld(player.getWorld())) return;

        if (!protectionService.canInteract(player, entity.getLocation())) return;

        if (blocked.contains(entity.getUniqueId())) return;

        MountState mountState = mountStates.get(player.getUniqueId());
        if (mountState == MountState.SNEAK_THROW) {
            if (player.getPassengers().contains(entity)
                    && player.getEquipment().getItemInMainHand().getType() == Material.AIR) {

                unmountAll(player);

                throwBarHandler.getAndRemove(player);

                mountStates.remove(player.getUniqueId());

                blocked.add(entity.getUniqueId());

                delayedActions.schedule(() -> blocked.remove(entity.getUniqueId()), 20);

                cancellable.setCancelled(true);
                return;
            }
        }

        if (player.getEquipment().getItemInMainHand().getType() != Material.AIR) return;
        if (!config.mobSettings().canBePickedUp(player, entity.getType())) return;
        if (!player.getPassengers().isEmpty()) return;
        if (!player.isSneaking()) return;

        if (!entity.getPassengers().isEmpty() && !config.carrySettings().isAllowStacking()) {
            if (!player.hasPermission(Permissions.BYPASS_NOSTACK)) {
                messageSender.sendLocalizedError(player, "nostack");
                return;
            }
        }

        mountStates.put(player.getUniqueId(), MountState.SNEAK_MOUNT);
        player.addPassenger(entity);
        cancellable.setCancelled(true);
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
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                    () -> {
                        if (player.isSneaking()) {
                            throwBarHandler.register(player);
                        }
                    }, 10);
            return;
        }

        if (!event.isSneaking() && mountState == MountState.SNEAK_THROW) {
            if (!throwBarHandler.isRegistered(player)) {
                unmountAll(player);
            } else {
                double force = throwBarHandler.getAndRemove(player);
                Vector throwVec = player.getEyeLocation().getDirection().normalize().multiply(force * config.carrySettings().throwForce());
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);
                for (Entity passenger : player.getPassengers()) {
                    delayedActions.schedule(() -> trailHandler.startTrail(passenger), 2);
                    player.removePassenger(passenger);
                    passenger.setVelocity(throwVec);
                    plugin.getLogger().config("Throwing entity | Location:" + player.getLocation().toVector()
                            + " | Force: " + force
                            + " | ThrowForce: " + config.carrySettings().throwForce()
                            + " | ViewVec: " + throwVec);
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
