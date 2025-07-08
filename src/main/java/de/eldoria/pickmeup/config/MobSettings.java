package de.eldoria.pickmeup.config;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.eldoutilities.serialization.TypeResolvingMap;
import de.eldoria.pickmeup.util.Permissions;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SerializableAs("pickMeUpMobSettings")
public class MobSettings implements ConfigurationSerializable {
    private static final transient List<EntityType> DEFAULT_MOBS;

    static {
        DEFAULT_MOBS = new ArrayList<EntityType>() {{
            add(EntityType.PIG);
            add(EntityType.CHICKEN);
            add(EntityType.RABBIT);
            add(EntityType.WOLF);
            add(EntityType.BOAT);
            add(EntityType.SHEEP);
            add(EntityType.PARROT);
        }};
    }

    private List<EntityType> allowedMobs = DEFAULT_MOBS;
    private boolean requirePermission;
    private boolean blacklist;

    public MobSettings(Map<String, Object> objectMap) {
        TypeResolvingMap map = SerializationUtil.mapOf(objectMap);
        allowedMobs = map.getValueOrDefault("allowedMobs", DEFAULT_MOBS, EntityType.class);
        requirePermission = map.getValueOrDefault("requirePermission", requirePermission);
        blacklist = map.getValueOrDefault("blacklist", blacklist);
    }

    public MobSettings() {

    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .addEnum("allowedMobs", allowedMobs)
                .add("requirePermission", requirePermission)
                .add("blacklist", blacklist)
                .build();
    }

    public boolean canBePickedUp(Player player, EntityType type) {
        if (allowedMobs == null) return false;
        boolean allowed;
        if (blacklist) {
            allowed = !allowedMobs.contains(type);
        } else {
            allowed = allowedMobs.contains(type);
        }
        if (!allowed) {
            return false;
        }
        if (requirePermission) {
            allowed = player.hasPermission(Permissions.getPickUpPermission(type));
        }
        return allowed;
    }
}
