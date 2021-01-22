package de.eldoria.pickmeup.scheduler;

import de.eldoria.eldoutilities.scheduling.SelfSchedulingWorker;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class TrailHandler extends SelfSchedulingWorker<Entity, Set<Entity>> {
    private final Set<Entity> remove = new HashSet<>();

    public TrailHandler(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected void execute(Entity entity) {
        if (!entity.isValid()) {
            remove.add(entity);
            return;
        }
        if (entity.isOnGround()) {
            remove.add(entity);
            return;
        }

        Vector offset = entity.getVelocity().normalize().multiply(1);
        entity.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, entity.getLocation().clone().subtract(offset), 2);
    }

    public void startTrail(Entity entity) {
        register(entity);
    }

    @Override
    protected void tick() {
        remove.forEach(this::unregister);
        remove.clear();
    }

    @Override
    protected Set<Entity> getQueueImplementation() {
        return new HashSet<>();
    }
}
