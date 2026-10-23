/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.pickmeup.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

/**
 * Draws a particle trail behind thrown entities until they land, hit water or have flown for {@value #MAX_TICKS}
 * ticks.
 * <p>
 * Each trail runs on the scheduler of its entity, so it follows the entity across regions on Folia.
 */
public class TrailHandler {
    private static final int START_DELAY = 2;
    private static final int MAX_TICKS = 100;
    private final Plugin plugin;

    public TrailHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Starts the trail of an entity that was just thrown. The first ticks are skipped, so the entity has left the
     * ground before landing ends the trail.
     *
     * @param entity the thrown entity
     */
    public void startTrail(Entity entity) {
        int[] age = {0};
        entity.getScheduler().runAtFixedRate(plugin, task -> draw(task, entity, ++age[0]), null, START_DELAY, 1);
    }

    private static void draw(ScheduledTask task, Entity entity, int ticks) {
        if (!entity.isValid() || entity.isOnGround() || entity.isInWater() || ticks > MAX_TICKS) {
            task.cancel();
            return;
        }

        Vector velocity = entity.getVelocity();
        if (velocity.lengthSquared() < 1.0E-6) return;
        Vector offset = velocity.normalize();
        entity.getWorld()
                .spawnParticle(
                        Particle.HAPPY_VILLAGER,
                        entity.getLocation().subtract(offset).add(0, 0.25, 0),
                        2);
    }
}
