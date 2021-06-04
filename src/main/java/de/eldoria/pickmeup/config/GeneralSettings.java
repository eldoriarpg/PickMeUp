package de.eldoria.pickmeup.config;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SerializableAs("pickMeUpGeneralSettings")
public class GeneralSettings implements ConfigurationSerializable {
    private final String language = "en_US";
    private final boolean updateCheck = true;

    public GeneralSettings(Map<String, Object> objectMap) {
        SerializationUtil.mapOnObject(objectMap, this);
    }

    public GeneralSettings() {

    }

    public String language() {
        return language;
    }

    public boolean isUpdateCheck() {
        return updateCheck;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.objectToMap(this);
    }
}
