package de.jeff_media.replant.acf.locales;

import de.jeff_media.replant.acf.locales.LanguageTable;
import de.jeff_media.replant.acf.locales.MessageKey;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class LocaleManager<T> {
    private final Function<T, Locale> localeMapper;
    private Locale defaultLocale;
    private final Map<Locale, LanguageTable> tables = new HashMap<Locale, LanguageTable>();

    LocaleManager(Function<T, Locale> function, Locale locale) {
        this.localeMapper = function;
        this.defaultLocale = locale;
    }

    public static <T> LocaleManager<T> create(@NotNull Function<T, Locale> function) {
        return new LocaleManager<T>(function, Locale.ENGLISH);
    }

    public static <T> LocaleManager<T> create(@NotNull Function<T, Locale> function, Locale locale) {
        return new LocaleManager<T>(function, locale);
    }

    public Locale getDefaultLocale() {
        return this.defaultLocale;
    }

    public Locale setDefaultLocale(Locale locale) {
        Locale locale2 = this.defaultLocale;
        this.defaultLocale = locale;
        return locale2;
    }

    public boolean addMessageBundle(@NotNull String string, Locale ... localeArray) {
        return this.addMessageBundle(this.getClass().getClassLoader(), string, localeArray);
    }

    public boolean addMessageBundle(@NotNull ClassLoader classLoader, @NotNull String string, Locale ... localeArray) {
        if (localeArray.length == 0) {
            localeArray = new Locale[]{this.defaultLocale};
        }
        boolean bl = false;
        for (Locale locale : localeArray) {
            if (!this.getTable(locale).addMessageBundle(classLoader, string)) continue;
            bl = true;
        }
        return bl;
    }

    public void addMessages(@NotNull Locale locale, @NotNull Map<MessageKey, String> map) {
        this.getTable(locale).addMessages(map);
    }

    public String addMessage(@NotNull Locale locale, @NotNull MessageKey messageKey, @NotNull String string) {
        return this.getTable(locale).addMessage(messageKey, string);
    }

    public String getMessage(T t, @NotNull MessageKey messageKey) {
        Locale locale = this.localeMapper.apply(t);
        String string = this.getTable(locale).getMessage(messageKey);
        if (string == null && !locale.getCountry().isEmpty()) {
            string = this.getTable(new Locale(locale.getLanguage())).getMessage(messageKey);
        }
        if (string == null && !Objects.equals(locale, this.defaultLocale)) {
            string = this.getTable(this.defaultLocale).getMessage(messageKey);
        }
        return string;
    }

    @NotNull
    public LanguageTable getTable(@NotNull Locale locale) {
        return this.tables.computeIfAbsent(locale, LanguageTable::new);
    }

    public boolean addResourceBundle(ResourceBundle resourceBundle, Locale locale) {
        return this.getTable(locale).addResourceBundle(resourceBundle);
    }
}

