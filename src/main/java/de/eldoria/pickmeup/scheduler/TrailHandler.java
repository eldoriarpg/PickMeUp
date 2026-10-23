package de.eldoria.pickmeup.scheduler;

import de.eldoria.eldoutilities.scheduling.SelfSchedulingWorker;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Draws a particle trail behind thrown entities until they land, hit water or have flown for {@value #MAX_TICKS}
 * ticks.
 */
public class TrailHandler extends SelfSchedulingWorker<Entity, Set<Entity>> {
    private static final int MAX_TICKS = 100;
    private final Set<Entity> remove = new HashSet<>();
    private final Map<Entity, Integer> age = new HashMap<>();

    public TrailHandler(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected void execute(Entity entity) {
        int ticks = age.merge(entity, 1, Integer::sum);
        if (!entity.isValid() || entity.isOnGround() || entity.isInWater() || ticks > MAX_TICKS) {
            remove.add(entity);
            return;
        }

        Vector velocity = entity.getVelocity();
        if (velocity.lengthSquared() < 1.0E-6) return;
        Vector offset = velocity.normalize();
        entity.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, entity.getLocation().subtract(offset).add(0, 0.25, 0), 2);
    }

    public void startTrail(Entity entity) {
        age.remove(entity);
        register(entity);
    }

    @Override
    protected void tick() {
        remove.forEach(entity -> {
            unregister(entity);
            age.remove(entity);
        });
        remove.clear();
    }

    @Override
    protected Set<Entity> getQueueImplementation() {
        return new HashSet<>();
    }
}
