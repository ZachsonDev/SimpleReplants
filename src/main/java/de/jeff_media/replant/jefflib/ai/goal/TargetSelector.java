package de.jeff_media.replant.jefflib.ai.goal;

import com.allatori.annotations.DoNotRename;
import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.ai.goal.GoalSelector;
import de.jeff_media.replant.jefflib.ai.goal.PathfinderGoal;
import de.jeff_media.replant.jefflib.internal.annotations.NMS;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

@DoNotRename
public final class TargetSelector
extends GoalSelector {
    private TargetSelector(@NotNull Mob mob) {
        super(mob);
    }

    public static TargetSelector of(@NotNull Mob mob) {
        return new TargetSelector(mob);
    }

    @Override
    @NMS
    public void addGoal(@NotNull PathfinderGoal pathfinderGoal, int n) {
        JeffLib.getNMSHandler().addTargetGoal(this.mob, pathfinderGoal, n);
    }

    @Override
    @NMS
    public void removeGoal(@NotNull PathfinderGoal pathfinderGoal) {
        JeffLib.getNMSHandler().removeTargetGoal(this.mob, pathfinderGoal);
    }

    @Override
    @NMS
    public void removeAllGoals() {
        JeffLib.getNMSHandler().removeAllTargetGoals(this.mob);
    }
}

