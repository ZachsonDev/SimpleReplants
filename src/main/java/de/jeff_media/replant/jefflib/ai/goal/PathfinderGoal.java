package de.jeff_media.replant.jefflib.ai.goal;

import com.allatori.annotations.DoNotRename;
import org.bukkit.entity.Mob;

@DoNotRename
public interface PathfinderGoal {
    public Mob getBukkitEntity();

    default public void start() {
    }

    default public void stop() {
    }

    default public void tick() {
    }

    default public boolean canContinueToUse() {
        return this.canUse();
    }

    public boolean canUse();

    default public boolean isInterruptable() {
        return true;
    }

    default public boolean requiresUpdateEveryTick() {
        return false;
    }
}

