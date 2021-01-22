package de.eldoria.pickmeup;

import de.eldoria.eldoutilities.bstats.Metrics;
import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.eldoutilities.updater.Updater;
import de.eldoria.eldoutilities.updater.butlerupdater.ButlerUpdateData;
import de.eldoria.pickmeup.commands.PickMeUpCommand;
import de.eldoria.pickmeup.config.CarrySettings;
import de.eldoria.pickmeup.config.Configuration;
import de.eldoria.pickmeup.config.GeneralSettings;
import de.eldoria.pickmeup.config.MobSettings;
import de.eldoria.pickmeup.config.WorldSettings;
import de.eldoria.pickmeup.listener.CarryListener;
import de.eldoria.pickmeup.scheduler.ThrowBarHandler;
import de.eldoria.pickmeup.util.Permissions;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class PickMeUp extends EldoPlugin {

    private boolean initialized;
    private Configuration configuration;

    public static Logger logger() {
        return getInstance(PickMeUp.class).getLogger();
    }

    @Override
    public void onEnable() {
        if (!initialized) {
            configuration = new Configuration(this);
            ILocalizer.create(this, "en_US", "de_DE");
            MessageSender.create(this, "§6[PMU]");
            ThrowBarHandler throwBarHandler = new ThrowBarHandler(this);
            registerListener(new CarryListener(this, configuration, throwBarHandler));
            registerCommand("pickmeup", new PickMeUpCommand(this));
            Updater.Butler(new ButlerUpdateData(this, Permissions.RELOAD,
                    configuration.getGeneralSettings().isUpdateCheck(),
                    false, 21, ButlerUpdateData.HOST));
            Metrics metrics = new Metrics(this, 9960);
            if (metrics.isEnabled()) {
                getLogger().info("§2Metrics enabled. Thank you <3");
            }
            initialized = true;
        } else {
            configuration.reload();
        }
        ILocalizer.getPluginLocalizer(this).setLocale(configuration.getGeneralSettings().getLanguage());
    }

    @Override
    public List<Class<? extends ConfigurationSerializable>> getConfigSerialization() {
        return Arrays.asList(CarrySettings.class, GeneralSettings.class, MobSettings.class, WorldSettings.class);
    }

    public void onReload() {
        onDisable();
        onEnable();
    }
}
