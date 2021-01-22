package de.eldoria.pickmeup.config;

import de.eldoria.eldoutilities.configuration.EldoConfig;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

@Getter
public class Configuration extends EldoConfig {
    private GeneralSettings generalSettings;
    private CarrySettings carrySettings;
    private MobSettings mobSettings;
    private WorldSettings worldSettings;

    public Configuration(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected void reloadConfigs() {
        generalSettings = getConfig().getObject("generalSettings", GeneralSettings.class, new GeneralSettings());
        carrySettings = getConfig().getObject("carrySettings", CarrySettings.class, new CarrySettings());
        mobSettings = getConfig().getObject("mobSettings", MobSettings.class, new MobSettings());
        worldSettings = getConfig().getObject("worldSettings", WorldSettings.class, new WorldSettings());
    }

    @Override
    protected void saveConfigs() {
        getConfig().set("generalSettings", generalSettings);
        getConfig().set("carrySettings", carrySettings);
        getConfig().set("mobSettings", mobSettings);
        getConfig().set("worldSettings", worldSettings);
    }
}
