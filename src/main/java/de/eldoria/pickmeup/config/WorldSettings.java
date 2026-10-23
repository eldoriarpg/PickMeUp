/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.pickmeup.config;

import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class WorldSettings {
    private boolean restrictWorlds = false;
    private boolean blacklist = false;
    private List<String> worlds = new ArrayList<>(List.of("world"));

    public boolean allowInWorld(World world) {
        if (!restrictWorlds) return true;
        if (blacklist) {
            return !worlds.contains(world.getName());
        }
        return worlds.contains(world.getName());
    }
}
