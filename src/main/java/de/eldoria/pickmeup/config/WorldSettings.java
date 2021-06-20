package de.eldoria.pickmeup.config;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.eldoutilities.serialization.TypeResolvingMap;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SerializableAs("pickMeUpWorldSettings")
public class WorldSettings implements ConfigurationSerializable {
    private boolean restrictWorlds;
    private boolean blacklist;
    private List<String> worlds = new ArrayList<String>() {{
        add("world");
    }};

    public WorldSettings(Map<String, Object> objectMap) {
        TypeResolvingMap map = SerializationUtil.mapOf(objectMap);
        restrictWorlds = map.getValueOrDefault("restrictWorlds", restrictWorlds);
        blacklist = map.getValueOrDefault("blacklist", blacklist);
        worlds = map.getValueOrDefault("worlds", worlds);
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
