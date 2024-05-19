package de.jeff_media.replant.jefflib;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public final class WordUtils {
    @Deprecated
    public static String getNiceMaterialName(Material material) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator iterator = Arrays.stream(material.name().split("_")).iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(WordUtils.upperCaseFirstLetterOnly((String)iterator.next()));
            if (!iterator.hasNext()) continue;
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    public static String upperCaseFirstLetterOnly(String string) {
        return WordUtils.upperCaseFirstLetter(string.toLowerCase(Locale.ROOT));
    }

    public static String upperCaseFirstLetter(String string) {
        if (string.length() < 1) {
            return string;
        }
        if (string.length() == 1) {
            return string.toUpperCase(Locale.ROOT);
        }
        return string.substring(0, 1).toUpperCase(Locale.ROOT) + string.substring(1);
    }

    public static String getNiceName(@NotNull NamespacedKey namespacedKey) {
        return WordUtils.getNiceName(namespacedKey.getKey());
    }

    public static String getNiceName(@NotNull String string) {
        String[] stringArray = string.split("_");
        Iterator iterator = Arrays.stream(stringArray).iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while (iterator.hasNext()) {
            stringBuilder.append(WordUtils.upperCaseFirstLetterOnly(((String)iterator.next()).toLowerCase(Locale.ROOT)));
            if (!iterator.hasNext()) continue;
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    public static String buildString(String[] stringArray, int n) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = n; i < stringArray.length; ++i) {
            stringBuilder.append(stringArray[i]).append(" ");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    public static int countChar(String string, char c) {
        int n = 0;
        char[] cArray = string.toCharArray();
        for (int i = 0; i < string.length(); ++i) {
            if (cArray[i] != c) continue;
            ++n;
        }
        return n;
    }

    public static String getGenitiveSuffix(String string) {
        String string2 = ChatColor.stripColor((String)string);
        if (string2.endsWith("s")) {
            return "'";
        }
        return "'s";
    }

    private WordUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

