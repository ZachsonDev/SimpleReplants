package de.jeff_media.replant.jefflib.ai.navigation;

import com.allatori.annotations.DoNotRename;
import de.jeff_media.replant.jefflib.internal.annotations.NMS;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

@DoNotRename
public interface LookController {
    @NMS
    public void setLookAt(Vector var1);

    @NMS(value="1.17")
    public void setLookAt(Entity var1);

    @NMS
    public void setLookAt(Entity var1, float var2, float var3);

    @NMS
    public void setLookAt(double var1, double var3, double var5);

    @NMS
    public void setLookAt(double var1, double var3, double var5, float var7, float var8);

    @NMS(value="1.18")
    public boolean isLookingAtTarget();

    @NMS
    public double getWantedX();

    @NMS
    public double getWantedY();

    @NMS
    public double getWantedZ();
}

