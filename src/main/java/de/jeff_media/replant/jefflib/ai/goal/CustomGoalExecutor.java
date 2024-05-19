package de.jeff_media.replant.jefflib.ai.goal;

import com.allatori.annotations.DoNotRename;
import de.jeff_media.replant.jefflib.ai.goal.GoalFlag;
import de.jeff_media.replant.jefflib.ai.navigation.Controls;
import de.jeff_media.replant.jefflib.ai.navigation.JumpController;
import de.jeff_media.replant.jefflib.ai.navigation.LookController;
import de.jeff_media.replant.jefflib.ai.navigation.MoveController;
import de.jeff_media.replant.jefflib.ai.navigation.PathNavigation;
import java.util.EnumSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DoNotRename
public interface CustomGoalExecutor {
    @NotNull
    public EnumSet<GoalFlag> getGoalFlags();

    public void setGoalFlags(@Nullable EnumSet<GoalFlag> var1);

    @NotNull
    public PathNavigation getNavigation();

    @NotNull
    public MoveController getMoveController();

    @NotNull
    public LookController getLookController();

    @NotNull
    public JumpController getJumpController();

    @NotNull
    default public Controls getControls() {
        return Controls.of(this.getMoveController(), this.getJumpController(), this.getLookController(), this.getNavigation());
    }
}

