package de.eldoria.pickmeup.util;

import org.bukkit.entity.EntityType;

public final class Permissions {
    public static final String BASE = "pickmeup.";
    public static final String RELOAD = BASE + "reload";
    public static final String BYPASS_NOSTACK = BASE + "bypass.nostack";

    private Permissions() {
    }

    public static String getPickUpPermission(EntityType type) {
        return BASE + "pickup." + type.name().toLowerCase();
    }
}
