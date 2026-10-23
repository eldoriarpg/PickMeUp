/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.pickmeup.util;

import org.bukkit.entity.EntityType;

import java.util.Locale;

public final class Permissions {
    public static final String BASE = "pickmeup.";
    public static final String RELOAD = BASE + "reload";
    public static final String UPDATES = BASE + "updates";
    public static final String TOGGLE = BASE + "toggle";
    public static final String BYPASS_NOSTACK = BASE + "bypass.nostack";
    public static final String BYPASS_MAXSTACK = BASE + "bypass.maxstack";
    public static final String BYPASS_TOGGLE = BASE + "bypass.toggle";

    private Permissions() {
    }

    public static String getPickUpPermission(EntityType type) {
        return BASE + "pickup." + type.name().toLowerCase(Locale.ROOT);
    }
}
