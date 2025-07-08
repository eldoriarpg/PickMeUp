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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.*;

public class CarryListener implements Listener {

    private final Configuration config;
    private final ProtectionService protectionService;
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
        if (!config.worldSettings().allowInWorld(event.getPlayer().getWorld())) return;

        if (!protectionService.canInteract(event.getPlayer(), event.getRightClicked())) return;

        if (blocked.contains(event.getRightClicked().getUniqueId())) return;

        MountState mountState = mountStates.get(event.getPlayer().getUniqueId());
        if (mountState == MountState.SNEAK_THROW) {
            if (event.getPlayer().getPassengers().contains(event.getRightClicked())
                    && event.getPlayer().getEquipment().getItemInMainHand().getType() == Material.AIR) {
                unmountAll(event.getPlayer());
                throwBarHandler.getAndRemove(event.getPlayer());
                mountStates.remove(event.getPlayer().getUniqueId());
                blocked.add(event.getRightClicked().getUniqueId());
                delayedActions.schedule(() -> blocked.remove(event.getRightClicked().getUniqueId()), 20);
                event.setCancelled(true);
                return;
            }
        }

        Player player = event.getPlayer();
        if (player.getEquipment().getItemInMainHand().getType() != Material.AIR) return;
        if (!config.mobSettings().canBePickedUp(event.getPlayer(), event.getRightClicked().getType())) return;
        if (!player.getPassengers().isEmpty()) return;
        if (!player.isSneaking()) return;

        if (!event.getRightClicked().getPassengers().isEmpty() && !config.carrySettings().isAllowStacking()) {
            if (!player.hasPermission(Permissions.BYPASS_NOSTACK)) {
                messageSender.sendError(player, "nostack");
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
        UUID playerUUID = player.getUniqueId();
        MountState currentState = mountStates.get(playerUUID);

        if (currentState == null) {
            unmountAll(player);
            return;
        }

        if (!event.isSneaking()) {
            handleSneakRelease(player, playerUUID, currentState);
        } else {
            handleSneakPress(player, playerUUID, currentState);
        }
    }

    private void handleSneakRelease(Player player, UUID playerUUID, MountState currentState) {
        switch (currentState) {
            case SNEAK_MOUNT:
                mountStates.put(playerUUID, MountState.WALKING);
                break;
            case SNEAK_THROW:
                if (!throwBarHandler.isRegistered(player)) {
                    unmountAll(player);
                } else {
                    performThrow(player, playerUUID);
                }
                break;
        }
    }

    private void handleSneakPress(Player player, UUID playerUUID, MountState currentState) {
        if (currentState == MountState.WALKING) {
            mountStates.put(playerUUID, MountState.SNEAK_THROW);
            delayedActions.schedule(() -> {
                if (player.isSneaking() && mountStates.get(playerUUID) == MountState.SNEAK_THROW) {
                    throwBarHandler.register(player);
                }
            }, 10);
        }
    }

    private void performThrow(Player player, UUID playerUUID) {
        double force = throwBarHandler.getAndRemove(player);
        Vector throwVec = player.getEyeLocation().getDirection().normalize().multiply(force * config.carrySettings().throwForce());
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);

        List<Entity> passengers = new ArrayList<>(player.getPassengers());
        for (Entity passenger : passengers) {
            if (passenger == null || !passenger.isValid() || passenger.isDead()) {
                player.removePassenger(passenger);
                blocked.remove(passenger.getUniqueId());
                continue;
            }

            delayedActions.schedule(() -> {
                if (passenger.isValid() && !passenger.isDead()) {
                    trailHandler.startTrail(passenger);
                }
            }, 2);

            player.removePassenger(passenger);
            passenger.setVelocity(throwVec);

            plugin.getLogger().config("Throwing entity | Location:" + player.getLocation().toVector()
                    + " | Force: " + force
                    + " | ThrowForce: " + config.carrySettings().throwForce()
                    + " | ViewVec: " + throwVec);
        }
        mountStates.remove(playerUUID);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerUUID = player.getUniqueId();
        mountStates.remove(playerUUID);
        throwBarHandler.remove(player);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getPassengers().contains(entity)) {
                player.removePassenger(entity);
                mountStates.remove(player.getUniqueId());
                throwBarHandler.remove(player);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        mountStates.remove(playerUUID);
        throwBarHandler.remove(player);
    }

    private void unmountAll(Player player) {
        UUID playerUUID = player.getUniqueId();
        player.getPassengers().forEach(player::removePassenger);
        mountStates.remove(playerUUID);
        throwBarHandler.remove(player);
    }

    private enum MountState {
        SNEAK_MOUNT, WALKING, SNEAK_THROW
    }
}