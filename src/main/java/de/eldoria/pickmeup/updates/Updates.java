package de.eldoria.pickmeup.updates;

import de.eldoria.eldoutilities.updater.AutoUpdateDecision;
import de.eldoria.eldoutilities.updater.Updater;
import de.eldoria.eldoutilities.updater.lynaupdater.LynaUpdateData;
import de.eldoria.eldoutilities.updater.lynaupdater.LynaUpdateResponse;
import de.eldoria.pickmeup.config.PickMeUpConfig;
import de.eldoria.pickmeup.config.UpdateSettings;
import de.eldoria.pickmeup.util.Permissions;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * Looks for new PickMeUp versions on Eldoria's release service Lyna, at start and every six hours after that. A new
 * version is told to the console and, on join, to players with {@link Permissions#UPDATES}. With auto update on, a new
 * stable release within the current major version is downloaded into the server's update folder and takes effect on
 * the next start. Otherwise admins are offered to download it and to switch auto update on from chat; their choice is
 * written to {@code config.yml}.
 */
public final class Updates {
    private final Plugin plugin;
    private final PickMeUpConfig configuration;
    private @Nullable Updater<LynaUpdateResponse, LynaUpdateData> updater;

    /**
     * @param plugin        the plugin whose versions are looked up
     * @param configuration where the update settings are read and an admin's choice is written
     */
    public Updates(Plugin plugin, PickMeUpConfig configuration) {
        this.plugin = plugin;
        this.configuration = configuration;
    }

    /**
     * Applies the current update settings: starts looking for new versions, hands the running updater the new
     * settings, or stops it when the settings neither want to hear about new versions nor download them.
     */
    public void apply() {
        UpdateSettings settings = configuration.generalSettings().updates();
        if (!settings.notice() && !settings.autoUpdate()) {
            stop();
            return;
        }
        LynaUpdateData data = data(settings);
        if (updater == null) {
            updater = Updater.lyna(data);
            updater.start();
        } else {
            updater.reconfigure(data);
        }
    }

    /**
     * Stops looking for new versions.
     */
    public void stop() {
        if (updater != null) updater.stop();
        updater = null;
    }

    private LynaUpdateData data(UpdateSettings settings) {
        return LynaUpdateData.builder(plugin)
                .notifyPermission(Permissions.UPDATES)
                .notifyUpdate(settings.notice())
                .autoUpdate(settings.autoUpdate())
                .askForAutoUpdate(settings.askForAutoUpdate())
                .autoUpdateHook(this::decided)
                .build();
    }

    private void decided(AutoUpdateDecision decision) {
        decide(configuration.generalSettings().updates(), decision);
        configuration.save();
    }

    /**
     * Keeps what an admin chose in chat in the update settings.
     *
     * @param settings the settings to change
     * @param decision what an admin chose in chat
     */
    static void decide(UpdateSettings settings, AutoUpdateDecision decision) {
        switch (decision) {
            case ENABLED -> settings.autoUpdate(true);
            case NEVER_ASK -> settings.askForAutoUpdate(false);
        }
    }
}
