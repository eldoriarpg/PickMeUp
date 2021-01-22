package de.eldoria.pickmeup.config;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import lombok.Data;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SerializableAs("pickMeUpWorldSettings")
@Data
public class WorldSettings implements ConfigurationSerializable {
    private boolean restrictWorlds = false;
    private boolean blacklist = false;
    private List<String> worlds = new ArrayList<String>() {{
        add("world");
    }};

    public WorldSettings(Map<String, Object> objectMap) {
        SerializationUtil.mapOnObject(objectMap, this);
    }

    public WorldSettings() {
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.objectToMap(this);
    }

    public boolean allowInWorld(World world) {
        if (!restrictWorlds) return true;
        if (blacklist) {
            return !worlds.contains(world.getName());
        }
        return worlds.contains(world.getName());
    }
}
