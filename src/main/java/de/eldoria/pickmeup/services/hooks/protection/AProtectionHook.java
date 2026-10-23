/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.pickmeup.services.hooks.protection;

public abstract class AProtectionHook implements IProtectionHook {
    private final String pluginName;

    public AProtectionHook(String pluginName) {
        this.pluginName = pluginName;
    }

    @Override
    public String pluginName() {
        return pluginName;
    }
}
