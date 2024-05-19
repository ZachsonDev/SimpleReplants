package de.jeff_media.replant.acf.locales;

import de.jeff_media.replant.acf.locales.MessageKey;
import de.jeff_media.replant.acf.locales.UTF8Control;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.jetbrains.annotations.NotNull;

public class LanguageTable {
    private final Locale locale;
    private final Map<MessageKey, String> messages = new HashMap<MessageKey, String>();

    LanguageTable(Locale locale) {
        this.locale = locale;
    }

    public String addMessage(MessageKey messageKey, String string) {
        return this.messages.put(messageKey, string);
    }

    public String getMessage(MessageKey messageKey) {
        return this.messages.get(messageKey);
    }

    public void addMessages(@NotNull Map<MessageKey, String> map) {
        this.messages.putAll(map);
    }

    public Locale getLocale() {
        return this.locale;
    }

    public boolean addMessageBundle(String string) {
        return this.addMessageBundle(this.getClass().getClassLoader(), string);
    }

    public boolean addMessageBundle(ClassLoader classLoader, String string) {
        try {
            return this.addResourceBundle(ResourceBundle.getBundle(string, this.locale, classLoader, new UTF8Control()));
        }
        catch (MissingResourceException missingResourceException) {
            return false;
        }
    }

    public boolean addResourceBundle(ResourceBundle resourceBundle) {
        for (String string : resourceBundle.keySet()) {
            this.addMessage(MessageKey.of(string), resourceBundle.getString(string));
        }
        return !resourceBundle.keySet().isEmpty();
    }
}

