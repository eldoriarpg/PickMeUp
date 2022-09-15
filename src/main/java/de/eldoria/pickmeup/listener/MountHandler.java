package de.eldoria.pickmeup.listener;

import de.eldoria.eldoutilities.utils.DataContainerUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class MountHandler {
    @SuppressWarnings("deprecation")
    private static final NamespacedKey offsetterIdentifierKey = new NamespacedKey("pickmeup", "is-offsetter");


    public MountState state;
    public BukkitTask throwTask;

    private final Player owner;

    private ArmorStand offsetter;

    public MountHandler(Player owner) {
        this.owner = owner;
    }

    public void cleanTasks() {
        if (throwTask != null && !throwTask.isCancelled()) throwTask.cancel();
    }

    public void dispose() {
        cleanTasks();

        if (offsetter == null) return;

        for (Entity passenger : offsetter.getPassengers()) {
            offsetter.removePassenger(passenger);
        }
        offsetter.remove();
        offsetter = null;
    }

    public boolean addPassenger(Entity passenger) {
        Entity mountable = getMountable();

        if (mountable.getUniqueId() == passenger.getUniqueId())
            return false;

        return mountable.addPassenger(passenger);
    }

    public boolean removePassenger(Entity passenger) {
        Entity mountable = getMountable();

        boolean result = false;

        if (mountable.getUniqueId() != passenger.getUniqueId()) {
            result = mountable.removePassenger(passenger);
        }
        // If there are no more passengers, dispose
        if (mountable.getPassengers().isEmpty()) dispose();

        return result;
    }

    public List<Entity> getDirectPassengers() {
        return getMountable().getPassengers();
    }

    public List<Entity> getAllPassengers(boolean filterOffsetters) {
        return getAllPassengers(owner, filterOffsetters);
    }

    public int stackCount(boolean filterOffsetters) {
        return stackCount(owner, filterOffsetters);
    }

    private void setupOffsetter() {
        if (offsetter != null && !offsetter.isDead())
            return;

        ArmorStand armorStand = owner.getWorld().spawn(owner.getLocation(), ArmorStand.class);

        armorStand.setInvulnerable(true);
        armorStand.setInvisible(true);
        //armorStand.setMarker(true);
        armorStand.setSmall(true);
        armorStand.setBasePlate(false);
        //armorStand.setLeftArmPose(90);
        //armorStand.setRightArmPose(90);


        // Mark it as an offsetter so it can be easily identified for removal
        DataContainerUtil.putValue(armorStand, offsetterIdentifierKey, PersistentDataType.BYTE, (byte) 1);


        // Get the current passengers of the owner
        List<Entity> passengers = owner.getPassengers();

        if (owner.addPassenger(armorStand)) {
            offsetter = armorStand;
            // Transfer any passengers from the owner to the offsetter
            for (Entity passenger : passengers) {
                offsetter.addPassenger(passenger);
            }
        } else {
            armorStand.remove();
        }
    }

    private Entity getMountable() {
        setupOffsetter();

        // If the offsetter is somehow null, default to the owner himself
        return offsetter != null ? offsetter : owner;
    }


    public static boolean isOffsetter(Entity entity) {
        return DataContainerUtil.get(entity, offsetterIdentifierKey, PersistentDataType.BYTE)
                .map(b -> b == (byte) 0)
                .orElse(false);
    }

    public static List<Entity> getDirectPassengers(Entity origin, boolean filterOffsetters) {
        List<Entity> passengers = origin.getPassengers();
        for (Entity passenger : passengers) {
            if (isOffsetter(passenger)) {
                passengers.addAll(passenger.getPassengers());
                if (filterOffsetters)
                    passengers.remove(passenger);
            }

        }

        return passengers;
    }

    public static List<Entity> getAllPassengers(Entity origin, boolean filterOffsetters) {
        List<Entity> passengers = getPassengersRecursive(origin);

        if (filterOffsetters) {
            passengers = passengers.stream().filter(MountHandler::isOffsetter).toList();
        }

        return passengers;
    }

    private static List<Entity> getPassengersRecursive(Entity mountable) {
        List<Entity> passengers = mountable.getPassengers();
        for (Entity passenger : passengers) {
            passengers.addAll(getPassengersRecursive(passenger));
        }

        return passengers;
    }

    public static int stackCount(Entity origin, boolean filterOffsetters) {
        return stackCountRecursive(origin, filterOffsetters, 0);
    }

    private static int stackCountRecursive(Entity mountable, boolean filterOffsetters, int level) {
        List<Entity> passengers = mountable.getPassengers();

        int resultLevel = level;
        if (!filterOffsetters || !isOffsetter(mountable)) {
            resultLevel += 1;
        }

        for (Entity passenger : passengers) {
            int deeperLevel = stackCountRecursive(passenger, filterOffsetters, resultLevel);

            if (deeperLevel > resultLevel) {
                resultLevel = deeperLevel;
            }

        }

        return resultLevel;
    }

    public enum MountState {
        SNEAK_MOUNT, WALKING, SNEAK_THROW
    }
}
