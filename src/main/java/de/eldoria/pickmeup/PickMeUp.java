package de.eldoria.pickmeup;

import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.eldoutilities.localization.Localizer;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.metrics.EldoMetrics;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.eldoutilities.updater.Updater;
import de.eldoria.eldoutilities.updater.lynaupdater.LynaUpdateData;
import de.eldoria.pickmeup.commands.PickMeUpCommand;
import de.eldoria.pickmeup.config.CarrySettings;
import de.eldoria.pickmeup.config.Configuration;
import de.eldoria.pickmeup.config.GeneralSettings;
import de.eldoria.pickmeup.config.MobSettings;
import de.eldoria.pickmeup.config.WorldSettings;
import de.eldoria.pickmeup.listener.CarryListener;
import de.eldoria.pickmeup.services.ProtectionService;
import de.eldoria.pickmeup.util.Permissions;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class PickMeUp extends EldoPlugin {

    private boolean initialized;
    private Configuration configuration;

    @Override
    public void onPluginEnable(boolean reload) {
        if (!initialized) {
            configuration = new Configuration(this);
            ProtectionService protectionService = ProtectionService.of(this);
            Localizer.create(this, "en_US", "de_DE", "zh_CN");
            MessageSender.builder(this).prefix("<gold>[PMU]").register();
            registerListener(new CarryListener(this, configuration, protectionService));
            registerCommand("pickmeup", new PickMeUpCommand(this));
            EldoMetrics metrics = new EldoMetrics(this, 9960);
            if (metrics.isEnabled()) {
                getLogger().info("§2Metrics enabled. Thank you <3");
            }
            initialized = true;
        } else {
            configuration.reload();
        }
        ILocalizer.getPluginLocalizer(this).setLocale(configuration.generalSettings().language());
    }

    @Override
    public void onPostStart() throws Throwable {
        Updater.lyna(LynaUpdateData.builder(this, 5)
                .notifyPermission(Permissions.RELOAD)
                .notifyUpdate(configuration.generalSettings().isUpdateCheck())
                .build()
        );
    }

    @Override
    public Level getLogLevel() {
        return Level.INFO;
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
