package de.jeff_media.replant.config;

import de.jeff_media.replant.Main;
import org.bukkit.permissions.Permissible;

public class Permissions {
    public static final String PERMISSION_USE = "replant.use";
    public static final String PERMISSION_RELOAD = "replant.reload";

    public static boolean isAllowed(Permissible permissible, String string) {
        switch (string) {
            case "replant.reload": {
                return permissible.hasPermission(PERMISSION_RELOAD);
            }
        }
        return !Main.getInstance().getConfig().getBoolean("use-permissions") || permissible.hasPermission(string);
    }
}

