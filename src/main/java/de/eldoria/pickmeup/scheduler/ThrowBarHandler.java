/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.pickmeup.scheduler;

import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.threading.ReschedulingTask;
import de.eldoria.eldoutilities.utils.EMath;
import de.eldoria.pickmeup.PickMeUp;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ThrowBarHandler extends ReschedulingTask {
    private static final String BAR_FRAGMENT = "█";
    private static final String[] GRADIENT = {
        "0000fc", "0022d9", "0045b6", "006893", "008b70", "00ad4e", "00d02b", "00f308", "1afc00", "3dfc00", "60fd00",
        "83fd00", "a7fd00", "cafe00", "edfe00", "fef600", "fee400", "fdd200", "fdc100", "fdaf00", "fc9d00", "fc8c00",
        "fc7a00", "fc6900", "fc5700", "fd4600", "fd3400", "fe2300", "fe1100", "ff0000"
    };
    private static final int[] INT_GRADIENT = {
        0x0000fc, 0x0022d9, 0x0045b6, 0x006893, 0x008b70, 0x00ad4e, 0x00d02b, 0x00f308, 0x1afc00, 0x3dfc00, 0x60fd00,
        0x83fd00, 0xa7fd00, 0xcafe00, 0xedfe00, 0xfef600, 0xfee400, 0xfdd200, 0xfdc100, 0xfdaf00, 0xfc9d00, 0xfc8c00,
        0xfc7a00, 0xfc6900, 0xfc5700, 0xfd4600, 0xfd3400, 0xfe2300, 0xfe1100, 0xff0000
    };
    private static final List<String> FULL_BAR;

    static {
        FULL_BAR = new LinkedList<>();
        for (String color : GRADIENT) {
            FULL_BAR.add("<#%s>%s".formatted(color, BAR_FRAGMENT));
        }
    }

    private final Map<UUID, AtomicInteger> currentValues = new HashMap<>();
    private final MessageSender sender;
    private int idleTicks;

    public ThrowBarHandler(Plugin plugin) {
        super(plugin);
        sender = MessageSender.getPluginMessageSender(PickMeUp.class);
    }

    public static int calculateIndex(int currValue) {
        double ratio = calculateForce(currValue);
        return (int) Math.round(ratio * INT_GRADIENT.length);
    }

    /**
     * The throw strength after the bar charged for the given ticks. The bar fills and empties again in cycles of twice
     * its length, so the strength stays between 0 and 1 however long sneak is held.
     *
     * @param ticks ticks the bar has charged
     * @return strength between 0 and 1
     */
    public static double calculateForce(int ticks) {
        int currValue = ticks % (INT_GRADIENT.length * 2);
        double ratio;
        if (currValue <= INT_GRADIENT.length) {
            ratio = currValue / (double) INT_GRADIENT.length;
            ratio = EMath.parabolaValue(0, 0, 1, 1, ratio);
        } else {
            ratio = (currValue - INT_GRADIENT.length) / (double) INT_GRADIENT.length;
            ratio = EMath.parabolaValue(0, 0, 1, 1, 1 - ratio);
        }
        return ratio;
    }

    @Override
    public void run() {
        currentValues.entrySet().removeIf(entry -> {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null || !player.isOnline() || !player.isSneaking()) {
                if (player != null && player.isOnline()) {
                    sender.sendActionBar(player, "");
                }
                return true;
            }

            int currValue = entry.getValue().incrementAndGet();
            sender.sendActionBar(player, String.join("", FULL_BAR.subList(0, calculateIndex(currValue))));
            return false;
        });

        if (currentValues.isEmpty()) {
            if (++idleTicks >= 200) {
                cancel();
            }
        } else {
            idleTicks = 0;
        }
    }

    public void register(Player player) {
        currentValues.put(player.getUniqueId(), new AtomicInteger(0));
        if (!isRunning()) {
            schedule();
            idleTicks = 0;
        }
    }

    public double getAndRemove(Player player) {
        AtomicInteger removed = currentValues.remove(player.getUniqueId());
        if (removed == null) return 0;
        sender.sendActionBar(player, "");
        return calculateForce(removed.get());
    }

    public boolean isRegistered(Player player) {
        return currentValues.containsKey(player.getUniqueId());
    }

    public void remove(Player player) {
        if (currentValues.remove(player.getUniqueId()) != null && player.isOnline()) {
            sender.sendActionBar(player, "");
        }
    }
}
