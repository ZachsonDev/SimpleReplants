package de.jeff_media.replant.jefflib.ai.navigation;

import com.allatori.annotations.DoNotRename;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DoNotRename
public interface PathNavigation {
    default public boolean moveTo(@NotNull BlockVector pos, double speedModifier) {
        return this.moveTo(pos.getX(), pos.getY(), pos.getZ(), speedModifier);
    }

    public boolean moveTo(double var1, double var3, double var5, double var7);

    default public boolean moveTo(@NotNull Entity entity, double speedModifier) {
        return this.moveTo(entity.getLocation(), speedModifier);
    }

    default public boolean moveTo(@NotNull Location loc, double speedModifier) {
        return this.moveTo(loc.getX(), loc.getY(), loc.getZ(), speedModifier);
    }

    @Nullable
    public BlockVector getTargetPos();

    public void setSpeedModifier(double var1);

    public void recomputePath();

    public boolean isDone();

    public boolean isInProgress();

    public void stop();

    public boolean isStableDestination(BlockVector var1);

    public void setCanFloat(boolean var1);

    public boolean shouldRecomputePath(BlockVector var1);

    public float getMaxDistanceToWaypoint();

    public boolean isStuck();
}

