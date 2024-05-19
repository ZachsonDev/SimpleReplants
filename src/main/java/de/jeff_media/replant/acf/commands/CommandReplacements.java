package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.CommandManager;
import de.jeff_media.replant.acf.commands.LogLevel;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Nullable;

public class CommandReplacements {
    private final CommandManager manager;
    private final Map<String, Map.Entry<Pattern, String>> replacements = new LinkedHashMap<String, Map.Entry<Pattern, String>>();

    CommandReplacements(CommandManager commandManager) {
        this.manager = commandManager;
        this.addReplacement0("truthy", "true|false|yes|no|1|0|on|off|t|f");
    }

    public void addReplacements(String ... stringArray) {
        if (stringArray.length == 0 || stringArray.length % 2 != 0) {
            throw new IllegalArgumentException("Must pass a number of arguments divisible by 2.");
        }
        for (int i = 0; i < stringArray.length; i += 2) {
            this.addReplacement(stringArray[i], stringArray[i + 1]);
        }
    }

    public String addReplacement(String string, String string2) {
        return this.addReplacement0(string, string2);
    }

    @Nullable
    private String addReplacement0(String string, String string2) {
        Pattern pattern;
        AbstractMap.SimpleImmutableEntry<Pattern, String> simpleImmutableEntry;
        Map.Entry entry = this.replacements.put(string = ACFPatterns.PERCENTAGE.matcher(string.toLowerCase(Locale.ENGLISH)).replaceAll(""), simpleImmutableEntry = new AbstractMap.SimpleImmutableEntry<Pattern, String>(pattern = Pattern.compile("%\\{" + Pattern.quote(string) + "}|%" + Pattern.quote(string) + "\\b", 2), string2));
        if (entry != null) {
            return (String)entry.getValue();
        }
        return null;
    }

    public String replace(String string) {
        if (string == null) {
            return null;
        }
        for (Map.Entry<Pattern, String> entry : this.replacements.values()) {
            string = entry.getKey().matcher(string).replaceAll(entry.getValue());
        }
        Matcher matcher = ACFPatterns.REPLACEMENT_PATTERN.matcher(string);
        while (matcher.find()) {
            this.manager.log(LogLevel.ERROR, "Found unregistered replacement: " + matcher.group());
        }
        return string;
    }
}

