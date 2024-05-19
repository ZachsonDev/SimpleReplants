package de.jeff_media.replant.jefflib.ai.navigation;

import com.allatori.annotations.DoNotRename;
import de.jeff_media.replant.jefflib.internal.annotations.NMS;

@DoNotRename
public interface MoveController {
    @NMS
    public boolean hasWanted();

    @NMS
    public double getSpeedModifier();

    @NMS
    public void setWantedPosition(double var1, double var3, double var5, double var7);

    @NMS
    public void strafe(float var1, float var2);

    @NMS
    public double getWantedX();

    @NMS
    public double getWantedY();

    @NMS
    public double getWantedZ();

    public static enum Operation {
        WAIT,
        MOVE_TO,
        STRAFE,
        JUMPING;

    }
}

