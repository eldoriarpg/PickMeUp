/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.pickmeup.config;

/**
 * How entities are picked up, stacked and thrown.
 */
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class CarrySettings {
    private double throwForce = 2.0;
    private int throwDelay = 10;
    private boolean allowStacking = false;
    private int maximumStacking = 0;
    private boolean leftClickPickup = false;
    private boolean leftClickPickupPlayers = false;
    private boolean teleportWithCarrier = false;

    /**
     * @return the multiplier of the charged throw strength, never negative. The resulting speed is capped at
     *     {@link de.eldoria.pickmeup.scheduler.ThrowBarHandler#MAX_THROW_VELOCITY} blocks per tick.
     */
    public double throwForce() {
        return Double.isFinite(throwForce) ? Math.max(0, throwForce) : 0;
    }

    /**
     * @return the ticks sneak has to be held while carrying before the throw bar starts charging
     */
    public int throwDelay() {
        return Math.max(0, throwDelay);
    }

    /**
     * @return whether entities which carry entities themselves may be picked up
     */
    public boolean allowStacking() {
        return allowStacking;
    }

    /**
     * @return the maximum height of a carried stack, the carrier excluded; zero or less for no limit
     */
    public int maximumStacking() {
        return maximumStacking;
    }

    /**
     * @return whether entities can also be picked up by punching them while sneaking, which Bedrock players need
     */
    public boolean leftClickPickup() {
        return leftClickPickup;
    }

    /**
     * @return whether punching while sneaking also picks up players, given {@link #leftClickPickup()}
     */
    public boolean leftClickPickupPlayers() {
        return leftClickPickupPlayers;
    }

    /**
     * @return whether carried entities follow their carrier through teleports and portals instead of being set down
     */
    public boolean teleportWithCarrier() {
        return teleportWithCarrier;
    }
}
