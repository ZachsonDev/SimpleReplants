package de.jeff_media.replant.jefflib.ai.goal;

import com.allatori.annotations.DoNotRename;
import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.ai.goal.CustomGoalExecutor;
import de.jeff_media.replant.jefflib.ai.goal.GoalFlag;
import de.jeff_media.replant.jefflib.ai.goal.PathfinderGoal;
import de.jeff_media.replant.jefflib.ai.navigation.Controls;
import de.jeff_media.replant.jefflib.ai.navigation.JumpController;
import de.jeff_media.replant.jefflib.ai.navigation.LookController;
import de.jeff_media.replant.jefflib.ai.navigation.MoveController;
import de.jeff_media.replant.jefflib.ai.navigation.PathNavigation;
import java.util.EnumSet;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DoNotRename
public abstract class CustomGoal
implements PathfinderGoal,
CustomGoalExecutor {
    private final Mob bukkitEntity;
    private final CustomGoalExecutor executor;

    protected CustomGoal(Mob mob) {
        this.bukkitEntity = mob;
        this.executor = JeffLib.getNMSHandler().getCustomGoalExecutor(this, mob);
    }

    public CustomGoalExecutor getExecutor() {
        return this.executor;
    }

    @Override
    public abstract boolean canUse();

    @Override
    public Mob getBukkitEntity() {
        return this.bukkitEntity;
    }

    @Override
    @NotNull
    public PathNavigation getNavigation() {
        return this.executor.getNavigation();
    }

    @Override
    @NotNull
    public MoveController getMoveController() {
        return this.executor.getMoveController();
    }

    @Override
    @NotNull
    public LookController getLookController() {
        return this.executor.getLookController();
    }

    @Override
    @NotNull
    public JumpController getJumpController() {
        return this.executor.getJumpController();
    }

    @Override
    @NotNull
    public Controls getControls() {
        return CustomGoalExecutor.super.getControls();
    }

    @Override
    @NotNull
    public EnumSet<GoalFlag> getGoalFlags() {
        return this.executor.getGoalFlags();
    }

    @Override
    public void setGoalFlags(@Nullable EnumSet<GoalFlag> enumSet) {
        this.executor.setGoalFlags(enumSet);
    }
}

