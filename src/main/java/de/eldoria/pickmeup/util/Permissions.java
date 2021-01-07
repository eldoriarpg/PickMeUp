package de.eldoria.pickmeup.util;

import org.bukkit.entity.EntityType;

public class Permissions {
    public static final String BASE = "pickmeup.";
    public static final String RELOAD = BASE + "reload";
    public static final String BYPASS_NOSTACK = BASE + "bypass.nostack";

    public static String getPickUpPermission(EntityType type) {
        return BASE + "pickup." + type.name().toLowerCase();
    }
}
