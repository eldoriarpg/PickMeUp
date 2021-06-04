package de.eldoria.pickmeup.config;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.eldoutilities.serialization.TypeResolvingMap;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SerializableAs("pickMeUpCarrySettings")
public class CarrySettings implements ConfigurationSerializable {
    private double throwForce = 2.0;
    private boolean allowStacking;

    public CarrySettings(Map<String, Object> objectMap) {
        TypeResolvingMap map = SerializationUtil.mapOf(objectMap);
        throwForce = map.getValueOrDefault("throwForce", throwForce);
        allowStacking = map.getValueOrDefault("allowStacking", allowStacking);
    }

    public CarrySettings() {

    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.objectToMap(this);
    }

    public double throwForce() {
        return throwForce;
    }

    public boolean isAllowStacking() {
        return allowStacking;
    }
}