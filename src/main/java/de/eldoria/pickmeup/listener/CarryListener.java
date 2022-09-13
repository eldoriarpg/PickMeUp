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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.*;

public class CarryListener implements Listener {
    private final Configuration config;
    private final ProtectionService protectionService;
    private final Plugin plugin;
    private final ThrowBarHandler throwBarHandler;
    private final Set<UUID> blocked = new HashSet<>();

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

        if (blocked.contains(entity.getUniqueId())) return;

        // If the player doesn't have the hand empty, leave
        if (Objects.requireNonNull(player.getEquipment()).getItemInMainHand().getType() != Material.AIR) return;

        MountHandler mountHandler = mountHandlers.get(player.getUniqueId());
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
        handler.state = MountState.SNEAK_MOUNT;
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
        if (!event.isSneaking() && mountHandler.state == MountState.SNEAK_MOUNT) {
            mountHandler.state = MountState.WALKING;
            return;
        }

        //If the player is begun sneaking with the WALKING state we start the throw delayedTask and set state to SNEAK_THROW
        if (event.isSneaking() && mountHandler.state == MountState.WALKING) {
            mountHandler.state = MountState.SNEAK_THROW;
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
        if (!event.isSneaking() && mountHandler.state == MountState.SNEAK_THROW) {
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
    public void onEntityDismount(EntityDismountEvent event){
        if(event.getDismounted() instanceof Player player)
            Optional.ofNullable(mountHandlers.get(player.getUniqueId()))
                    .ifPresentOrElse(handler -> {
                        if(handler.getDirectPassengers().isEmpty())
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

    private enum MountState {
        SNEAK_MOUNT, WALKING, SNEAK_THROW
    }

    private class MountHandler{
        public MountState state;
        public BukkitTask throwTask;

        private final Player owner;

        private ArmorStand offsetter;

        public MountHandler(Player owner){
            this.owner = owner;
        }

        public void cleanTasks(){
            if (throwTask != null && !throwTask.isCancelled()) throwTask.cancel();
        }

        public void dispose(){
            cleanTasks();

            if (offsetter == null)
                return;

            for (Entity passenger :
                    offsetter.getPassengers()) {
                offsetter.removePassenger(passenger);
            }
            offsetter.remove();
            offsetter = null;
        }

        public boolean addPassenger(Entity passenger){
            Entity mountable = getMountable();

            if(mountable.getUniqueId() == passenger.getUniqueId())
                return false;

            return mountable.addPassenger(passenger);
        }

        public boolean removePassenger(Entity passenger){
            Entity mountable = getMountable();

            if(mountable.getUniqueId() == passenger.getUniqueId())
                return false;

            boolean result = mountable.removePassenger(passenger);

            // If there are no more passengers, dispose
            if(mountable.getPassengers().isEmpty())
                dispose();

            return result;
        }

        public List<Entity> getDirectPassengers(){
            Entity mountable = getMountable();

            return mountable.getPassengers();
        }

        public List<Entity> getAllPassengers(boolean filterOffsetters){
            return getAllPassengers(owner, filterOffsetters);
        }
        public int stackCount(boolean filterOffsetters){
            return stackCount(owner, filterOffsetters);
        }

        private void setupOffsetter() {
            if (offsetter != null && !offsetter.isDead())
                return;

            ArmorStand armorStand = (ArmorStand) owner.getWorld().spawnEntity(owner.getLocation(), EntityType.ARMOR_STAND);

            armorStand.setInvulnerable(true);
            armorStand.setInvisible(true);
            //armorStand.setMarker(true);
            armorStand.setSmall(true);
            armorStand.setBasePlate(false);
            //armorStand.setLeftArmPose(90);
            //armorStand.setRightArmPose(90);


            // Mark it as an offsetter so it can be easily identified for removal
            PersistentDataContainer dataContainer = armorStand.getPersistentDataContainer();
            dataContainer.set(PickMeUp.offsetterIdentifierKey(), PersistentDataType.BYTE, (byte) 1);


            // Get the current passengers of the owner
            List<Entity> passengers = owner.getPassengers();

            if (owner.addPassenger(armorStand)) {
                offsetter = armorStand;
                // Transfer any passengers from the owner to the offsetter
                for (Entity passenger : passengers)
                    offsetter.addPassenger(passenger);
            } else {
                armorStand.remove();
            }
        }

        private Entity getMountable(){
            setupOffsetter();

            // If the offsetter is somehow null, default to the owner himself
            if(offsetter != null)
                return offsetter;
            return owner;
        }


        public static boolean isOffsetter(Entity entity){
            PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
            byte isOffsetter = dataContainer.getOrDefault(PickMeUp.offsetterIdentifierKey(), PersistentDataType.BYTE, (byte)0);
            return isOffsetter == (byte) 1;
        }

        public static List<Entity> getDirectPassengers(Entity origin, boolean filterOffsetters){
            List<Entity> passengers = origin.getPassengers();
            for (Entity passenger : passengers) {
                if(isOffsetter(passenger)) {
                    passengers.addAll(passenger.getPassengers());
                    if(filterOffsetters)
                        passengers.remove(passenger);
                }

            }

            return passengers;
        }

        public static List<Entity> getAllPassengers(Entity origin, boolean filterOffsetters){
            List<Entity> passengers = getPassengersRecursive(origin);

            if(filterOffsetters)
                passengers = passengers.stream().filter(MountHandler::isOffsetter).toList();

            return passengers;
        }

        private static List<Entity> getPassengersRecursive(Entity mountable){
            List<Entity> passengers = mountable.getPassengers();
            for (Entity passenger :
                    passengers) {
                passengers.addAll(getPassengersRecursive(passenger));
            }

            return passengers;
        }

        public static int stackCount(Entity origin, boolean filterOffsetters){
            return stackCountRecursive(origin, filterOffsetters, 0);
        }

        private static int stackCountRecursive(Entity mountable, boolean filterOffsetters, int level){
            List<Entity> passengers = mountable.getPassengers();

            int resultLevel = level;
            if(!filterOffsetters || !isOffsetter(mountable))
                resultLevel += 1;

            for (Entity passenger :
                    passengers) {
                int deeperLevel = stackCountRecursive(passenger, filterOffsetters, resultLevel);

                if(deeperLevel > resultLevel)
                    resultLevel = deeperLevel;

            }

            return resultLevel;
        }
    }
}
