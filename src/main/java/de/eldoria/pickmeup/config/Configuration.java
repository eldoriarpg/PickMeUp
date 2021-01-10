package de.eldoria.pickmeup.config;

import de.eldoria.eldoutilities.configuration.EldoConfig;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class Configuration extends EldoConfig {
    public Configuration(Plugin plugin) {
        super(plugin);
    }

    public boolean isWorldActive(World world) {
        List<String> worlds = getConfig().getStringList("worlds");
        if (getConfig().getBoolean("blacklist", false)) {
            return !worlds.contains(world.getName());
        }
        return worlds.contains(world.getName());
    }

    @Override
    protected void reloadConfigs() {
        getConfig().addDefault("allowedMobTypes", Arrays.asList("CHICKEN", "RABBIT", "CAT", "WOLF", "FOX"));
        getConfig().addDefault("blacklist", false);
    }

    public boolean canPickUpMob(EntityType type) {
        return getConfig().getStringList("allowedMobTypes").contains(type.name());
    }

    public String getLanguage() {
        return getConfig().getString("language", "en_US");
    }

    public boolean isUpdateCheck() {
        return getConfig().getBoolean("updateCheck", true);
    }

    public boolean allowStacking() {
        return getConfig().getBoolean("allowStacking");
    }

    public double getThrowForce() {
        return getConfig().getDouble("throwForce", 2);
    }
}
