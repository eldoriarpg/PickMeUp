/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.pickmeup.services.hooks.protection;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * The WorldGuard region flag {@value #NAME}, which decides who may pick up entities in a region.
 * <p>
 * It applies to non-members by default, so {@code /rg flag <region> pickmeup deny} keeps members allowed. WorldGuard
 * only accepts new flags before it is enabled, so the flag is registered while PickMeUp loads.
 */
public final class WorldGuardFlag {
    public static final String NAME = "pickmeup";
    private static StateFlag flag;

    private WorldGuardFlag() {}

    /**
     * Registers the flag, or takes over a state flag of the same name another plugin registered first.
     *
     * @param logger where a conflicting flag is reported
     */
    public static void register(Logger logger) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        StateFlag created = new StateFlag(NAME, true, RegionGroup.NON_MEMBERS);
        try {
            registry.register(created);
            flag = created;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get(NAME);
            if (existing instanceof StateFlag stateFlag) {
                flag = stateFlag;
            } else {
                logger.warning("The WorldGuard flag \"" + NAME
                        + "\" is taken by another plugin. The region flag is not available.");
            }
        }
    }

    /**
     * @return the registered flag, empty when registration failed or WorldGuard is not installed
     */
    public static Optional<StateFlag> flag() {
        return Optional.ofNullable(flag);
    }
}
