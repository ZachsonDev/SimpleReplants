package de.jeff_media.replant.jefflib;

import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.data.HexColor;
import de.jeff_media.replant.jefflib.internal.cherokee.StringUtils;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public final class TextUtils {
    private static final int MIN_BANNER_WIDTH = 30;
    private static final char BANNER_CHAR = '#';
    private static final String EMPTY = "";
    private static final String REGEX_HEX = "[\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F]";
    private static final String REGEX_HEX_GRADIENT = "<#([\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F])>(.*?)<#/([\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F])>";
    private static final Pattern PATTERN_HEX_GRADIENT = Pattern.compile("<#([\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F])>(.*?)<#/([\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F])>");
    private static final String REGEX_AMPERSAND_HASH = "&#([\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F])";
    private static final Pattern PATTERN_AMPERSAND_HASH = Pattern.compile("&#([\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F])");
    private static final String REGEX_XML_LIKE_HASH = "<#([\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F])>";
    private static final Pattern PATTERN_XML_LIKE_HASH = Pattern.compile("<#([\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F])>");
    private static AtomicReference<Plugin> itemsAdderPlugin;
    private static AtomicReference<Plugin> placeholderApiPlugin;

    public static void banner(CharSequence charSequence) {
        int n = Math.max(charSequence.length() + 4, 30);
        StringUtils.leftPad(EMPTY, n, '#');
        JeffLib.getPlugin().getLogger().info(StringUtils.center(" " + charSequence + " ", n, '#'));
    }

    public static String format(String string) {
        return TextUtils.format(string, null);
    }

    public static String format(String string, @Nullable OfflinePlayer offlinePlayer) {
        string = TextUtils.replaceEmojis(string);
        string = TextUtils.replacePlaceholders(string, offlinePlayer);
        string = TextUtils.color(string);
        return string;
    }

    public static String replaceEmojis(String string) {
        if (itemsAdderPlugin == null) {
            itemsAdderPlugin = new AtomicReference<Plugin>(Bukkit.getPluginManager().getPlugin("ItemsAdder"));
        }
        if (itemsAdderPlugin.get() != null) {
            try {
                string = FontImageWrapper.replaceFontImages((String)string);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return string;
    }

    public static String replacePlaceholders(String string, @Nullable OfflinePlayer offlinePlayer) {
        if (placeholderApiPlugin == null) {
            placeholderApiPlugin = new AtomicReference<Plugin>(Bukkit.getPluginManager().getPlugin("PlaceholderAPI"));
        }
        if (placeholderApiPlugin.get() != null) {
            try {
                string = PlaceholderAPI.setPlaceholders((OfflinePlayer)offlinePlayer, (String)string);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return string;
    }

    public static String color(String string) {
        string = string.replace("&&", "{ampersand}");
        string = TextUtils.replaceGradients(string);
        string = TextUtils.replaceRegexWithGroup(string, PATTERN_XML_LIKE_HASH, 1, TextUtils::addAmpersandsToHex);
        string = TextUtils.replaceRegexWithGroup(string, PATTERN_AMPERSAND_HASH, 1, TextUtils::addAmpersandsToHex);
        string = ChatColor.translateAlternateColorCodes((char)'&', (String)string);
        string = string.replace("{ampersand}", "&");
        return string;
    }

    private static String replaceGradients(String string) {
        Object object;
        string = string.replaceAll("<#/([\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F][\\da-fA-F])>", "<#/$1><#$1>");
        Matcher matcher = PATTERN_HEX_GRADIENT.matcher(string);
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            object = new HexColor(matcher.group(1));
            HexColor hexColor = new HexColor(matcher.group(3));
            String string2 = matcher.group(2);
            matcher.appendReplacement(stringBuffer, HexColor.applyGradient(string2, (HexColor)object, hexColor));
        }
        matcher.appendTail(stringBuffer);
        object = stringBuffer.toString();
        while (((String)object).matches(".*&x&[\\da-zA-Z]&[\\da-zA-Z]&[\\da-zA-Z]&[\\da-zA-Z]&[\\da-zA-Z]&[\\da-zA-Z]$")) {
            object = ((String)object).substring(0, ((String)object).length() - 14);
        }
        return object;
    }

    private static String replaceRegexWithGroup(CharSequence charSequence, Pattern pattern, int n, Function<String, String> function) {
        Matcher matcher = pattern.matcher(charSequence);
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(stringBuffer, function.apply(matcher.group(n)));
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private static String addAmpersandsToHex(String string) {
        if (string.length() != 6) {
            throw new IllegalArgumentException("Hex-Codes must always have 6 letters.");
        }
        char[] cArray = string.toCharArray();
        StringBuilder stringBuilder = new StringBuilder("&x");
        for (char c : cArray) {
            stringBuilder.append("&").append(c);
        }
        return stringBuilder.toString();
    }

    public static List<String> format(List<String> list, @Nullable OfflinePlayer offlinePlayer) {
        list = new ArrayList<String>(list);
        for (int i = 0; i < list.size(); ++i) {
            list.set(i, TextUtils.format(list.get(i), offlinePlayer));
        }
        return list;
    }

    public static List<String> replaceInString(List<String> list, Map<String, String> map) {
        list.replaceAll(string -> TextUtils.replaceInString(string, map));
        return list;
    }

    public static String replaceInString(String string, Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) continue;
            string = string.replace(entry.getKey(), entry.getValue());
        }
        return string;
    }

    public static List<String> replaceInString(List<String> list, String ... stringArray) {
        list.replaceAll(string -> TextUtils.replaceInString(string, stringArray));
        return list;
    }

    public static String replaceInString(String string, String ... stringArray) {
        if (stringArray.length % 2 != 0) {
            throw new IllegalArgumentException("placeholders must have an even length");
        }
        for (int i = 0; i < stringArray.length; i += 2) {
            if (stringArray[i] == null || stringArray[i + 1] == null) continue;
            string = string.replace(stringArray[i], stringArray[i + 1]);
        }
        return string;
    }

    private TextUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

