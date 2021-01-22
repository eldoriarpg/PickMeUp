package de.eldoria.pickmeup.config;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import lombok.Data;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SerializableAs("pickMeUpGeneralSettings")
@Data
public class GeneralSettings implements ConfigurationSerializable {
    private String language = "en_US";
    private boolean updateCheck = true;

    public GeneralSettings(Map<String, Object> objectMap) {
        SerializationUtil.mapOnObject(objectMap, this);
    }

    public GeneralSettings() {

    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.objectToMap(this);
    }
}
