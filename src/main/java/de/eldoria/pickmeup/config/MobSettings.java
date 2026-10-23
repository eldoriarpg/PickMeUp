/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.pickmeup.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.eldoria.pickmeup.PickMeUp;
import de.eldoria.pickmeup.util.Permissions;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Which entities may be picked up.
 * <p>
 * The entity types are kept as written, so a name this server does not know stays in the file instead of being
 * dropped on the next save. Names Minecraft renamed are read as their new type, unknown ones are reported once.
 */
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class MobSettings {
    private static final Map<String, String> RENAMED = Map.of(
            "BOAT", "OAK_BOAT",
            "CHEST_BOAT", "OAK_CHEST_BOAT",
            "MUSHROOM_COW", "MOOSHROOM",
            "SNOWMAN", "SNOW_GOLEM",
            "MINECART_CHEST", "CHEST_MINECART",
            "MINECART_FURNACE", "FURNACE_MINECART",
            "MINECART_TNT", "TNT_MINECART",
            "MINECART_HOPPER", "HOPPER_MINECART",
            "MINECART_MOB_SPAWNER", "SPAWNER_MINECART",
            "MINECART_COMMAND", "COMMAND_BLOCK_MINECART");

    private List<String> allowedMobs = new ArrayList<>(List.of("PIG", "CHICKEN", "RABBIT", "WOLF", "OAK_BOAT", "SHEEP", "PARROT"));
    private boolean requirePermission = false;
    private boolean blacklist = false;
    @JsonIgnore
    private Set<EntityType> resolved;

    public boolean canBePickedUp(Player player, EntityType type) {
        boolean listed = resolved().contains(type);
        if (blacklist == listed) return false;
        return !requirePermission || player.hasPermission(Permissions.getPickUpPermission(type));
    }

    private Set<EntityType> resolved() {
        if (resolved == null) {
            resolved = resolve(allowedMobs);
        }
        return resolved;
    }

    private static Set<EntityType> resolve(List<String> names) {
        Set<EntityType> types = EnumSet.noneOf(EntityType.class);
        for (String name : names) {
            String upper = name.toUpperCase(Locale.ROOT);
            try {
                types.add(EntityType.valueOf(RENAMED.getOrDefault(upper, upper)));
            } catch (IllegalArgumentException e) {
                PickMeUp.logger().warning("Unknown entity type \"" + name + "\" in mobSettings.allowedMobs. It is ignored.");
            }
        }
        return types;
    }
}
