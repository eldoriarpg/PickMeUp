package de.eldoria.pickmeup;

import de.eldoria.eldoutilities.bstats.EldoMetrics;
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
import de.eldoria.pickmeup.services.ProtectionService;
import de.eldoria.pickmeup.util.Permissions;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Arrays;
import java.util.List;

public class PickMeUp extends EldoPlugin {

    private boolean initialized;
    private Configuration configuration;
    private static PickMeUp instance;
    private static NamespacedKey offsetterIdentifierKey;

    public PickMeUp(){
        instance = this;
        offsetterIdentifierKey = new NamespacedKey(this, "IsOffsetter");
    }

    @Override
    public void onPluginEnable(boolean reload) {
        if (!initialized) {
            configuration = new Configuration(this);
            ProtectionService protectionService = ProtectionService.of(this);
            ILocalizer.create(this, "en_US", "de_DE", "zh_CN");
            MessageSender.create(this, "§6[PMU]");
            registerListener(new CarryListener(this, configuration, protectionService));
            registerCommand("pickmeup", new PickMeUpCommand(this));
            Updater.butler(new ButlerUpdateData(this, Permissions.RELOAD,
                    configuration.generalSettings().isUpdateCheck(),
                    false, 21, ButlerUpdateData.HOST));
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
    public List<Class<? extends ConfigurationSerializable>> getConfigSerialization() {
        return Arrays.asList(CarrySettings.class, GeneralSettings.class, MobSettings.class, WorldSettings.class);
    }

    public void onReload() {
        onDisable();
        onEnable();
    }
    public static PickMeUp instance(){
        return instance;
    }
    public static NamespacedKey offsetterIdentifierKey(){
        return offsetterIdentifierKey;
    }

}
