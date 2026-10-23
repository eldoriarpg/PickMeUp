/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.pickmeup.listener;

import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.scheduling.DelayedActions;
import de.eldoria.pickmeup.config.CarrySettings;
import de.eldoria.pickmeup.config.PickMeUpConfig;
import de.eldoria.pickmeup.scheduler.ThrowBarHandler;
import de.eldoria.pickmeup.scheduler.TrailHandler;
import de.eldoria.pickmeup.services.ProtectionService;
import de.eldoria.pickmeup.util.Permissions;
import de.eldoria.pickmeup.util.PickupToggle;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Picks up entities on a sneaking click with an empty hand and throws them when sneak is held and released again.
 * <p>
 * A carrier goes through {@link MountState}: picking up while sneaking, walking with the entity once sneak is
 * released, and charging a throw once sneak is pressed again. Releasing sneak before the throw bar appears drops the
 * entity, releasing it afterwards throws it. A carrier's state ends whenever the carried entity leaves them, so only
 * passengers this listener mounted are ever touched.
 */
public class CarryListener implements Listener {
    private static final int REPICKUP_DELAY = 20;
    private final PickMeUpConfig config;
    private final ProtectionService protectionService;
    private final PickupToggle pickupToggle;
    private final Plugin plugin;
    private final ThrowBarHandler throwBarHandler;
    private final TrailHandler trailHandler;
    private final MessageSender messageSender;
    private final DelayedActions delayedActions;
    private final Set<UUID> blocked = new HashSet<>();
    private final Map<UUID, MountState> mountStates = new HashMap<>();

    public CarryListener(
            Plugin plugin, PickMeUpConfig config, ProtectionService protectionService, PickupToggle pickupToggle) {
        this.plugin = plugin;
        this.config = config;
        this.protectionService = protectionService;
        this.pickupToggle = pickupToggle;
        throwBarHandler = new ThrowBarHandler(plugin);
        trailHandler = new TrailHandler(plugin);
        messageSender = MessageSender.getPluginMessageSender(plugin);
        delayedActions = DelayedActions.start(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        Entity target = event.getRightClicked();

        if (mountStates.get(player.getUniqueId()) == MountState.SNEAK_THROW
                && player.getPassengers().contains(target)
                && hasEmptyHand(player)) {
            drop(player);
            event.setCancelled(true);
            return;
        }

        if (tryPickUp(player, target)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPunch(EntityDamageByEntityEvent event) {
        CarrySettings settings = config.carrySettings();
        if (!settings.leftClickPickup()) return;
        if (!(event.getDamager() instanceof Player player)) return;
        if (event.getEntity() instanceof Player && !settings.leftClickPickupPlayers()) return;

        if (tryPickUp(player, event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        MountState state = mountStates.get(player.getUniqueId());
        if (state == null) return;

        switch (state) {
            case SNEAK_MOUNT -> {
                if (!event.isSneaking()) mountStates.put(player.getUniqueId(), MountState.WALKING);
            }
            case WALKING -> {
                if (event.isSneaking()) startCharging(player);
            }
            case SNEAK_THROW -> {
                if (event.isSneaking()) return;
                if (throwBarHandler.isRegistered(player)) {
                    throwPassengers(player);
                } else {
                    drop(player);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDismount(EntityDismountEvent event) {
        if (!(event.getDismounted() instanceof Player player)) return;
        if (!mountStates.containsKey(player.getUniqueId())) return;
        if (player.getPassengers().stream().allMatch(passenger -> passenger.equals(event.getEntity()))) {
            clear(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (mountStates.containsKey(event.getPlayer().getUniqueId())) {
            unmountAll(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (mountStates.containsKey(event.getEntity().getUniqueId())) {
            unmountAll(event.getEntity());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (mountStates.containsKey(event.getPlayer().getUniqueId())) {
            unmountAll(event.getPlayer());
        }
    }

    /**
     * Lets every carrier drop what they carry. Called when the plugin is disabled, so nothing stays mounted on a
     * player without PickMeUp knowing about it.
     */
    public void dropAll() {
        for (UUID carrier : new ArrayList<>(mountStates.keySet())) {
            Player player = Bukkit.getPlayer(carrier);
            if (player != null) unmountAll(player);
        }
        mountStates.clear();
    }

    private boolean tryPickUp(Player player, Entity target) {
        if (!player.isSneaking() || !hasEmptyHand(player)) return false;
        if (mountStates.containsKey(player.getUniqueId())
                || !player.getPassengers().isEmpty()) return false;
        if (blocked.contains(target.getUniqueId())) return false;
        if (target.isInsideVehicle()) return false;
        if (!config.worldSettings().allowInWorld(player.getWorld())) return false;
        if (!config.mobSettings().canBePickedUp(player, target.getType())) return false;

        if (target instanceof Player targetPlayer
                && !pickupToggle.isPickupable(targetPlayer)
                && !player.hasPermission(Permissions.BYPASS_TOGGLE)) {
            messageSender.sendErrorActionBar(
                    player, "error.notPickupable", Placeholder.unparsed("player", targetPlayer.getName()));
            return false;
        }

        if (!target.getPassengers().isEmpty()) {
            CarrySettings settings = config.carrySettings();
            if (!settings.allowStacking() && !player.hasPermission(Permissions.BYPASS_NOSTACK)) {
                messageSender.sendErrorActionBar(player, "error.noStack");
                return false;
            }
            int maximum = settings.maximumStacking();
            if (maximum > 0 && stackHeight(target) > maximum && !player.hasPermission(Permissions.BYPASS_MAXSTACK)) {
                messageSender.sendErrorActionBar(
                        player, "error.maxStack", Placeholder.unparsed("max", String.valueOf(maximum)));
                return false;
            }
        }

        if (!protectionService.canInteract(player, target)) return false;
        if (!player.addPassenger(target)) return false;

        mountStates.put(player.getUniqueId(), MountState.SNEAK_MOUNT);
        return true;
    }

    private void startCharging(Player player) {
        UUID carrier = player.getUniqueId();
        mountStates.put(carrier, MountState.SNEAK_THROW);
        delayedActions.schedule(
                () -> {
                    if (player.isSneaking()
                            && mountStates.get(carrier) == MountState.SNEAK_THROW
                            && !player.getPassengers().isEmpty()) {
                        throwBarHandler.register(player);
                    }
                },
                config.carrySettings().throwDelay());
    }

    private void throwPassengers(Player player) {
        double force = throwBarHandler.getAndRemove(player);
        double throwForce = config.carrySettings().throwForce();
        Vector throwVec = player.getEyeLocation().getDirection().multiply(force * throwForce);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);

        List<Entity> passengers = new ArrayList<>(player.getPassengers());
        clear(player);
        for (Entity passenger : passengers) {
            player.removePassenger(passenger);
            passenger.setVelocity(throwVec);
            delayedActions.schedule(() -> trailHandler.startTrail(passenger), 2);
            plugin.getLogger()
                    .config("Throwing entity | Location:" + player.getLocation().toVector()
                            + " | Force: " + force
                            + " | ThrowForce: " + throwForce
                            + " | ViewVec: " + throwVec);
        }
    }

    private void drop(Player player) {
        for (Entity passenger : player.getPassengers()) {
            UUID id = passenger.getUniqueId();
            blocked.add(id);
            delayedActions.schedule(() -> blocked.remove(id), REPICKUP_DELAY);
        }
        unmountAll(player);
    }

    private void unmountAll(Player player) {
        clear(player);
        new ArrayList<>(player.getPassengers()).forEach(player::removePassenger);
    }

    private void clear(Player player) {
        mountStates.remove(player.getUniqueId());
        throwBarHandler.remove(player);
    }

    private static boolean hasEmptyHand(Player player) {
        return player.getInventory().getItemInMainHand().getType().isAir();
    }

    /**
     * Counts the entities in the stack an entity carries, the entity included.
     *
     * @param entity the bottom of the stack
     * @return the height of the stack
     */
    private static int stackHeight(Entity entity) {
        int highest = 0;
        for (Entity passenger : entity.getPassengers()) {
            highest = Math.max(highest, stackHeight(passenger));
        }
        return highest + 1;
    }

    private enum MountState {
        SNEAK_MOUNT,
        WALKING,
        SNEAK_THROW
    }
}
