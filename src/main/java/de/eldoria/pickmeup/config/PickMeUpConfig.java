/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.pickmeup.config;

import de.eldoria.eldoutilities.config.JacksonConfig;
import dev.chojo.ocular.key.Key;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The configuration of PickMeUp, read from {@code config.yml}.
 */
public class PickMeUpConfig extends JacksonConfig<ConfigFile> {
    public PickMeUpConfig(@NotNull Plugin plugin) {
        super(plugin, Key.builder(Path.of("config.yml"), ConfigFile::new).build());
    }

    public GeneralSettings generalSettings() {
        return main().generalSettings();
    }

    public CarrySettings carrySettings() {
        return main().carrySettings();
    }

    public MobSettings mobSettings() {
        return main().mobSettings();
    }

    public WorldSettings worldSettings() {
        return main().worldSettings();
    }
}
