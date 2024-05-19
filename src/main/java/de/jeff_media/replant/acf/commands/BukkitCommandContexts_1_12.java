package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.BukkitCommandContexts;
import org.bukkit.NamespacedKey;

class BukkitCommandContexts_1_12 {
    BukkitCommandContexts_1_12() {
    }

    static void register(BukkitCommandContexts bukkitCommandContexts) {
        bukkitCommandContexts.registerContext(NamespacedKey.class, bukkitCommandExecutionContext -> {
            String string = bukkitCommandExecutionContext.popFirstArg();
            String[] stringArray = ACFPatterns.COLON.split(string, 2);
            if (stringArray.length == 1) {
                String string2 = bukkitCommandExecutionContext.getFlagValue("namespace", (String)null);
                if (string2 == null) {
                    return NamespacedKey.minecraft((String)stringArray[0]);
                }
                return new NamespacedKey(string2, stringArray[0]);
            }
            return new NamespacedKey(stringArray[0], stringArray[1]);
        });
    }
}

