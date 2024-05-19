package de.jeff_media.replant.handlers;

import com.google.common.base.Enums;
import de.jeff_media.replant.Main;
import java.util.Locale;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class ParticleManager {
    private final Main main = Main.getInstance();
    private final String node;

    public ParticleManager(String string) {
        this.node = string;
    }

    public boolean isEnabled() {
        return this.main.getConfig().getBoolean(this.node + "-particles-enabled");
    }

    public Particle getParticleType() {
        return (Particle)Enums.getIfPresent(Particle.class, (String)this.main.getConfig().getString(this.node + "-particles-type").toUpperCase(Locale.ROOT)).or((Object)Particle.VILLAGER_HAPPY);
    }

    public int getParticleCount() {
        return this.main.getConfig().getInt(this.node + "-particles-count");
    }

    public void spawnParticles(Block block) {
        if (!this.isEnabled()) {
            return;
        }
        Location location = block.getLocation().add(new Vector(0.5, 0.1, 0.5));
        Particle particle = this.getParticleType();
        int n = this.getParticleCount();
        location.getWorld().spawnParticle(particle, location, this.getParticleCount(), 0.5, 0.1, 0.5);
    }
}

