package de.jeff_media.replant.jefflib.ai.goal;

import com.allatori.annotations.DoNotRename;
import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.ai.goal.PathfinderGoal;
import de.jeff_media.replant.jefflib.internal.annotations.NMS;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

@DoNotRename
public class GoalSelector {
    protected final Mob mob;

    protected GoalSelector(@NotNull Mob mob) {
        this.mob = mob;
    }

    public static GoalSelector of(@NotNull Mob mob) {
        return new GoalSelector(mob);
    }

    @NMS
    public void addGoal(@NotNull PathfinderGoal pathfinderGoal, int n) {
        JeffLib.getNMSHandler().addGoal(this.mob, pathfinderGoal, n);
    }

    @NMS
    public void removeGoal(@NotNull PathfinderGoal pathfinderGoal) {
        JeffLib.getNMSHandler().removeGoal(this.mob, pathfinderGoal);
    }

    @NMS
    public void removeAllGoals() {
        JeffLib.getNMSHandler().removeAllGoals(this.mob);
    }
}

