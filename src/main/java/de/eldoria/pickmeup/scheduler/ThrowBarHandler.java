/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.pickmeup.scheduler;

import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.utils.EMath;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Charges the throw bar of sneaking carriers and shows it in their action bar.
 * <p>
 * Every charging player has their own task on the player's scheduler, so the bar is driven by the thread that owns the
 * player on Paper and Folia alike. The charge ends when the player stops sneaking, leaves, or is removed.
 */
public class ThrowBarHandler {
    private static final String BAR_FRAGMENT = "█";
    private static final String[] GRADIENT = {
        "0000fc", "0022d9", "0045b6", "006893", "008b70", "00ad4e", "00d02b", "00f308", "1afc00", "3dfc00", "60fd00",
        "83fd00", "a7fd00", "cafe00", "edfe00", "fef600", "fee400", "fdd200", "fdc100", "fdaf00", "fc9d00", "fc8c00",
        "fc7a00", "fc6900", "fc5700", "fd4600", "fd3400", "fe2300", "fe1100", "ff0000"
    };
    private static final List<String> FULL_BAR;

    static {
        FULL_BAR = new ArrayList<>();
        for (String color : GRADIENT) {
            FULL_BAR.add("<#%s>%s".formatted(color, BAR_FRAGMENT));
        }
    }

    /**
     * The highest speed a throw gives, in blocks per tick. A full bar at the default throw force reaches 2, so the cap
     * only stops a configured throw force from launching entities faster than the server can handle.
     */
    public static final double MAX_THROW_VELOCITY = 4.0;

    private final Plugin plugin;
    private final MessageSender sender;
    private final Map<UUID, Charge> charges = new ConcurrentHashMap<>();

    public ThrowBarHandler(Plugin plugin) {
        this.plugin = plugin;
        sender = MessageSender.getPluginMessageSender(plugin);
    }

    public static int calculateIndex(int currValue) {
        double ratio = calculateForce(currValue);
        return (int) Math.round(ratio * GRADIENT.length);
    }

    /**
     * The throw strength after the bar charged for the given ticks. The bar fills and empties again in cycles of twice
     * its length, so the strength stays between 0 and 1 however long sneak is held.
     *
     * @param ticks ticks the bar has charged
     * @return strength between 0 and 1
     */
    public static double calculateForce(int ticks) {
        int currValue = ticks % (GRADIENT.length * 2);
        double ratio;
        if (currValue <= GRADIENT.length) {
            ratio = currValue / (double) GRADIENT.length;
            ratio = EMath.parabolaValue(0, 0, 1, 1, ratio);
        } else {
            ratio = (currValue - GRADIENT.length) / (double) GRADIENT.length;
            ratio = EMath.parabolaValue(0, 0, 1, 1, 1 - ratio);
        }
        return ratio;
    }

    /**
     * The velocity a throw gives a carried entity.
     *
     * @param direction  where the carrier looks
     * @param strength   the charged strength, see {@link #calculateForce(int)}
     * @param throwForce the configured multiplier of the strength
     * @return the velocity in the look direction, never faster than {@value #MAX_THROW_VELOCITY} blocks per tick
     */
    public static Vector throwVelocity(Vector direction, double strength, double throwForce) {
        double speed = Math.clamp(strength * throwForce, 0, MAX_THROW_VELOCITY);
        return direction.clone().normalize().multiply(speed);
    }

    /**
     * Starts charging the throw bar of a player, replacing a charge already running.
     *
     * @param player the carrier who holds sneak
     */
    public void register(Player player) {
        remove(player);
        UUID id = player.getUniqueId();
        Charge charge = new Charge();
        charges.put(id, charge);
        charge.task = player.getScheduler()
                .runAtFixedRate(plugin, task -> tick(player, charge), () -> charges.remove(id, charge), 1, 1);
        if (charge.task == null) charges.remove(id, charge);
    }

    /**
     * Ends the charge of a player and hides the bar.
     *
     * @param player the carrier
     * @return the strength the bar had, or 0 when the player was not charging
     */
    public double getAndRemove(Player player) {
        Charge charge = charges.remove(player.getUniqueId());
        if (charge == null) return 0;
        charge.cancel();
        sender.sendActionBar(player, "");
        return calculateForce(charge.ticks);
    }

    public boolean isRegistered(Player player) {
        return charges.containsKey(player.getUniqueId());
    }

    public void remove(Player player) {
        getAndRemove(player);
    }

    private void tick(Player player, Charge charge) {
        if (!player.isSneaking()) {
            if (charges.remove(player.getUniqueId(), charge)) {
                charge.cancel();
                sender.sendActionBar(player, "");
            }
            return;
        }
        charge.ticks++;
        sender.sendActionBar(player, String.join("", FULL_BAR.subList(0, calculateIndex(charge.ticks))));
    }

    /**
     * The running charge of one player. Only touched by the thread that owns the player.
     */
    private static final class Charge {
        private volatile int ticks;
        private volatile ScheduledTask task;

        private void cancel() {
            if (task != null) task.cancel();
        }
    }
}
