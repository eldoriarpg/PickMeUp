package de.eldoria.pickmeup;

import de.eldoria.eldoutilities.config.template.PluginBaseConfiguration;
import de.eldoria.eldoutilities.localization.Localizer;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.metrics.EldoMetrics;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.pickmeup.commands.PickMeUpCommand;
import de.eldoria.pickmeup.config.LegacyConfig;
import de.eldoria.pickmeup.config.PickMeUpConfig;
import de.eldoria.pickmeup.listener.CarryListener;
import de.eldoria.pickmeup.services.ProtectionService;
import de.eldoria.pickmeup.updates.Updates;
import de.eldoria.pickmeup.util.PickupToggle;
import org.bstats.bukkit.Metrics;

import java.util.logging.Level;

public class PickMeUp extends EldoPlugin {
    private PickMeUpConfig configuration;
    private Localizer localizer;
    private CarryListener carryListener;
    private Updates updates;

    @Override
    public void onPluginLoad() throws Throwable {
        LegacyConfig.migrate(getDataFolder().toPath(), getLogger());
        configuration = new PickMeUpConfig(this);
        PluginBaseConfiguration base = configuration.secondary(PluginBaseConfiguration.KEY);
        if (base.version() == 0) {
            base.version(1);
            base.lastInstalledVersion(this);
        }
        configuration.main();
        configuration.save();
    }

    @Override
    public void onPluginEnable() throws Throwable {
        localizer = Localizer.builder(this, "en_US")
                .setIncludedLocales("de_DE", "zh_CN")
                .build();
        localizer.setDefaultLocale(configuration.generalSettings().language());
        MessageSender.builder(this)
                .prefix("<gold>[PMU]")
                .localizer(localizer)
                .register();

        PickupToggle pickupToggle = new PickupToggle(this);
        carryListener = new CarryListener(this, configuration, ProtectionService.of(this), pickupToggle);
        registerListener(carryListener);
        registerCommand("pickmeup", new PickMeUpCommand(this, pickupToggle));

        new Metrics(this, 9960);
        if (EldoMetrics.isEnabled(this)) {
            getLogger().info("Metrics enabled. Thank you <3");
        }

        updates = new Updates(this, configuration);
        updates.apply();
    }

    @Override
    public void onPluginDisable() throws Throwable {
        if (carryListener != null) carryListener.dropAll();
        if (updates != null) updates.stop();
    }

    /**
     * Reads the configuration again and applies the language and update settings.
     */
    public void reload() {
        configuration.reload();
        localizer.setDefaultLocale(configuration.generalSettings().language());
        updates.apply();
    }

    @Override
    public Level getLogLevel() {
        if (configuration == null) return Level.INFO;
        return configuration.secondary(PluginBaseConfiguration.KEY).logLevel();
    }
}
