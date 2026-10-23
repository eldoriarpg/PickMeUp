package de.eldoria.pickmeup.config;

/**
 * Content of {@code config.yml}. The section names are the ones of the Bukkit serialized configuration of 1.x, so an
 * old file reads into this class once its type markers are gone.
 */
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class ConfigFile {
    private GeneralSettings generalSettings = new GeneralSettings();
    private CarrySettings carrySettings = new CarrySettings();
    private MobSettings mobSettings = new MobSettings();
    private WorldSettings worldSettings = new WorldSettings();

    public GeneralSettings generalSettings() {
        return generalSettings;
    }

    public CarrySettings carrySettings() {
        return carrySettings;
    }

    public MobSettings mobSettings() {
        return mobSettings;
    }

    public WorldSettings worldSettings() {
        return worldSettings;
    }
}
