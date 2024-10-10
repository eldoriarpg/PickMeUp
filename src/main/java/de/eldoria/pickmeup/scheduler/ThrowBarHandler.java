package de.eldoria.pickmeup.scheduler;

import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.threading.ReschedulingTask;
import de.eldoria.eldoutilities.utils.EMath;
import de.eldoria.pickmeup.PickMeUp;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ThrowBarHandler extends ReschedulingTask {
    private static final String BAR_FRAGMENT = "█";
    private static final String[] GRADIENT = {"0000fc", "0022d9", "0045b6", "006893", "008b70", "00ad4e", "00d02b", "00f308", "1afc00", "3dfc00", "60fd00", "83fd00", "a7fd00", "cafe00", "edfe00", "fef600", "fee400", "fdd200", "fdc100", "fdaf00", "fc9d00", "fc8c00", "fc7a00", "fc6900", "fc5700", "fd4600", "fd3400", "fe2300", "fe1100", "ff0000"};
    private static final int[] INT_GRADIENT = {0x0000fc, 0x0022d9, 0x0045b6, 0x006893, 0x008b70, 0x00ad4e, 0x00d02b, 0x00f308, 0x1afc00, 0x3dfc00, 0x60fd00, 0x83fd00, 0xa7fd00, 0xcafe00, 0xedfe00, 0xfef600, 0xfee400, 0xfdd200, 0xfdc100, 0xfdaf00, 0xfc9d00, 0xfc8c00, 0xfc7a00, 0xfc6900, 0xfc5700, 0xfd4600, 0xfd3400, 0xfe2300, 0xfe1100, 0xff0000};
    private static final List<String> FULL_BAR;

    static {
        FULL_BAR = new LinkedList<>();
        for (String color : GRADIENT) {
            FULL_BAR.add("<#%s>%s".formatted(color, BAR_FRAGMENT));
        }
    }

    private final Map<Player, AtomicInteger> currentValues = new HashMap<>();
    private final MessageSender sender;
    private int idleTicks;

    public ThrowBarHandler(Plugin plugin) {
        super(plugin);
        sender = MessageSender.getPluginMessageSender(PickMeUp.class);
    }

    public static int calculateIndex(int currValue) {
        int doubleBar = INT_GRADIENT.length * 2;
        int clampedValue = currValue % doubleBar;
        double ratio = calculateForce(clampedValue);
        return (int) Math.round(ratio * INT_GRADIENT.length);
    }

    public static double calculateForce(int currValue) {
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
        for (Map.Entry<Player, AtomicInteger> entry : currentValues.entrySet()) {
            int currValue = entry.getValue().incrementAndGet();
            String barDisplay = String.join("", FULL_BAR.subList(0, calculateIndex(currValue)));
            sender.sendActionBar(entry.getKey(), barDisplay);
        }
        if (currentValues.isEmpty()) {
            idleTicks++;
            if (idleTicks >= 200) {
                cancel();
            }
        }
    }

    public void register(Player player) {
        currentValues.put(player, new AtomicInteger(0));
        if (!isRunning()) {
            schedule();
        }
        idleTicks = 0;
    }

    public double getAndRemove(Player player) {
        AtomicInteger remove = currentValues.remove(player);
        if (remove != null) {
            return calculateForce(remove.get());
        }
        return 0;
    }

    public boolean isRegistered(Player player) {
        return currentValues.containsKey(player);
    }
}
