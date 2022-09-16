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
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;
import java.util.Arrays;
import java.util.Optional;
import java.util.Objects;

public class CarryListener implements Listener {
    private final Configuration config;
    private final ProtectionService protectionService;
    private final Plugin plugin;
    private final ThrowBarHandler throwBarHandler;
    //private final Set<UUID> blocked = new HashSet<>();

    private final Map<UUID, MountHandler> mountHandlers = new HashMap<>();

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

    private void entityCarryInteraction(Player player, Entity entity, Cancellable cancellable){
        if (!config.worldSettings().allowInWorld(player.getWorld())) return;

        if (!protectionService.canInteract(player, entity.getLocation())) return;

        //if (blocked.contains(entity.getUniqueId())) return;

        // If the player doesn't have the hand empty, leave
        if (Objects.requireNonNull(player.getEquipment()).getItemInMainHand().getType() != Material.AIR) return;

        //MountHandler mountHandler = mountHandlers.get(player.getUniqueId());
        /*//If the player interacts with the entity that is carrying with an empty hand
        if (mountHandler != null
                && mountHandler.state == MountState.SNEAK_THROW
                && mountHandler.getDirectPassengers().contains(entity)) {

            //Dismount entity
            if(!mountHandler.removePassenger(entity))
                return;

            //Block the entity from being picked again, to avoid spam picking
            blocked.add(entity.getUniqueId());
            delayedActions.schedule(() -> blocked.remove(entity.getUniqueId()), 20);

            cancellable.setCancelled(true);
            return;
        }*/

        //If the player is not sneaking leave
        if (!player.isSneaking()) return;

        //Check if the settings/perms allow the player to pick up this entity
        if (!config.mobSettings().canBePickedUp(player, entity.getType())) return;

        /*if (!player.hasPermission(Permissions.BYPASS_MAXSELFCARRY)
                && (mountHandler != null
                ? mountHandler.getDirectPassengers().size()
                : MountHandler.getDirectPassengers(player, true).size())
                >= config.carrySettings().maximumSelfCarry()) return;*/


        if (!config.carrySettings().isAllowStacking()
                &&!player.hasPermission(Permissions.BYPASS_NOSTACK)
                && MountHandler.stackCount(entity, true) > 1) {
            messageSender.sendLocalizedError(player, "nostack");
            return;

        }

        if(config.carrySettings().maximumStacking() != 0
                && !player.hasPermission(Permissions.BYPASS_MAXSTACK)
                && MountHandler.stackCount(entity, true) + MountHandler.stackCount(player, true) - 1 > config.carrySettings().maximumStacking())
            return;


        mountHandlers.putIfAbsent(player.getUniqueId(), new MountHandler(player));
        MountHandler handler = mountHandlers.get(player.getUniqueId());
        // Set the player state as SNEAK_MOUNT since the player just mounted an entity
        handler.state = MountHandler.MountState.SNEAK_MOUNT;
        // Clear any extra data in case it was leftover
        throwBarHandler.getAndRemove(player);
        handler.cleanTasks();

        handler.addPassenger(entity);

        cancellable.setCancelled(true);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        //If there is no mountState for the player, just dismount everything
        if (!mountHandlers.containsKey(player.getUniqueId())) {
            unmountAll(player);
            return;
        }

        MountHandler mountHandler = mountHandlers.get(player.getUniqueId());

        //If the player stopped sneaking and was SNEAK_MOUNT, change the state to WALKING
        if (!event.isSneaking() && mountHandler.state == MountHandler.MountState.SNEAK_MOUNT) {
            mountHandler.state = MountHandler.MountState.WALKING;
            return;
        }

        //If the player is begun sneaking with the WALKING state we start the throw delayedTask and set state to SNEAK_THROW
        if (event.isSneaking() && mountHandler.state == MountHandler.MountState.WALKING) {
            mountHandler.state = MountHandler.MountState.SNEAK_THROW;
            mountHandler.throwTask =
                    Bukkit.getScheduler().runTaskLater(plugin,
                            () -> {
                                //If the player is still sneaking after the delay
                                if (player.isSneaking()) {
                                    throwBarHandler.register(player);
                                }
                            }, config.carrySettings().throwDelay());
            return;
        }

        //If the player stopped sneaking with the SNEAK_THROW state
        if (!event.isSneaking() && mountHandler.state == MountHandler.MountState.SNEAK_THROW) {
            //If the player is not registered on the throwBarHandler then we simply dismount the entities
            if (!throwBarHandler.isRegistered(player)) {
                unmountAll(player);
            } else {
                double force = throwBarHandler.getAndRemove(player);
                Vector throwVec = player.getEyeLocation().getDirection().normalize().multiply(force * config.carrySettings().throwForce());
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);
                for (Entity passenger : mountHandler.getDirectPassengers()) {
                    delayedActions.schedule(() -> trailHandler.startTrail(passenger), 2);
                    mountHandler.removePassenger(passenger);
                    passenger.setVelocity(throwVec);
                    plugin.getLogger().config("Throwing entity | Location:" + player.getLocation().toVector()
                            + " | Force: " + force
                            + " | ThrowForce: " + config.carrySettings().throwForce()
                            + " | ViewVec: " + throwVec);
                }
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event){
        Arrays.stream(event.getChunk().getEntities())
                .filter(MountHandler::isOffsetter) // Filter for offsetters
                .forEach(Entity::remove); // Remove them
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent event){
        if(event.getDismounted() instanceof Player player)
            Optional.ofNullable(mountHandlers.get(player.getUniqueId()))
                    .ifPresentOrElse(handler -> {
                        if(handler.getDirectPassengers().size() - 1 <= 0)
                            removePlayerData(player);
                    },
                            () ->{
                                removePlayerData(player);
                            });


        // If an ArmorStand just dismounted any entity
        if(event.getEntity() instanceof ArmorStand armorStand){
            if(MountHandler.isOffsetter(armorStand)){ // If so, remove it from existence
                armorStand.remove();
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        removePlayerData(event.getPlayer());
    }

    private void unmountAll(Player player) {
        for (Entity passenger : player.getPassengers()) {
            player.removePassenger(passenger);
        }
    }

    private void removePlayerData(Player player){
        //If there is a pending task, cancel it
        Optional.ofNullable(mountHandlers.remove(player.getUniqueId()))
                .ifPresent(MountHandler::dispose);
        throwBarHandler.getAndRemove(player);
    }
}

