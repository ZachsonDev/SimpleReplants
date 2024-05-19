package de.jeff_media.replant.jefflib.ai.navigation;

import com.allatori.annotations.DoNotRename;
import de.jeff_media.replant.jefflib.EntityUtils;
import de.jeff_media.replant.jefflib.ai.navigation.JumpController;
import de.jeff_media.replant.jefflib.ai.navigation.LookController;
import de.jeff_media.replant.jefflib.ai.navigation.MoveController;
import de.jeff_media.replant.jefflib.ai.navigation.PathNavigation;
import de.jeff_media.replant.jefflib.internal.annotations.NMS;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

@DoNotRename
public final class Controls {
    private final MoveController moveController;
    private final JumpController jumpController;
    private final LookController lookController;
    private final PathNavigation navigation;

    private Controls(@NotNull Mob mob) {
        this.moveController = EntityUtils.getMoveController(mob);
        this.jumpController = EntityUtils.getJumpController(mob);
        this.lookController = EntityUtils.getLookController(mob);
        this.navigation = EntityUtils.getNavigation(mob);
    }

    private Controls(MoveController moveController, JumpController jumpController, LookController lookController, PathNavigation pathNavigation) {
        this.moveController = moveController;
        this.jumpController = jumpController;
        this.lookController = lookController;
        this.navigation = pathNavigation;
    }

    @NMS
    @NotNull
    public static Controls of(@NotNull Mob mob) {
        return new Controls(mob);
    }

    public static Controls of(@NotNull MoveController moveController, @NotNull JumpController jumpController, @NotNull LookController lookController, @NotNull PathNavigation pathNavigation) {
        return new Controls(moveController, jumpController, lookController, pathNavigation);
    }

    public MoveController getMoveController() {
        return this.moveController;
    }

    public LookController getLookController() {
        return this.lookController;
    }

    public JumpController getJumpController() {
        return this.jumpController;
    }

    public PathNavigation getNavigation() {
        return this.navigation;
    }
}

