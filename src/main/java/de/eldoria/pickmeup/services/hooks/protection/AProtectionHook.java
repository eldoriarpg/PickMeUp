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
