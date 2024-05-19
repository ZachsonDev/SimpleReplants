package de.jeff_media.replant.utils;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.command.CommandSender;

public class CommandUtils {
    public static void printUsage(CommandSender commandSender, String string, HashMap<String, String> hashMap) {
        ArrayList<String> arrayList = new ArrayList<String>(hashMap.keySet());
        arrayList.sort(String::compareTo);
        for (String string2 : arrayList) {
            String string3 = hashMap.get(string2);
            commandSender.sendMessage("\u00a76" + (string == null ? "" : string + " ") + string2 + " \u00a77- \u00a7f" + string3);
        }
    }
}

