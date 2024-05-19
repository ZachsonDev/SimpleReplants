package de.jeff_media.replant.acf.commands;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import de.jeff_media.replant.acf.commands.CommandManager;
import de.jeff_media.replant.acf.commands.LogLevel;
import de.jeff_media.replant.acf.locales.LocaleManager;
import de.jeff_media.replant.acf.locales.MessageKey;
import de.jeff_media.replant.acf.locales.MessageKeyProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import org.jetbrains.annotations.NotNull;

public class Locales {
    public static final Locale ENGLISH = Locale.ENGLISH;
    public static final Locale GERMAN = Locale.GERMAN;
    public static final Locale FRENCH = Locale.FRENCH;
    public static final Locale JAPANESE = Locale.JAPANESE;
    public static final Locale ITALIAN = Locale.ITALIAN;
    public static final Locale KOREAN = Locale.KOREAN;
    public static final Locale CHINESE = Locale.CHINESE;
    public static final Locale SIMPLIFIED_CHINESE = Locale.SIMPLIFIED_CHINESE;
    public static final Locale TRADITIONAL_CHINESE = Locale.TRADITIONAL_CHINESE;
    public static final Locale SPANISH = new Locale("es");
    public static final Locale DUTCH = new Locale("nl");
    public static final Locale DANISH = new Locale("da");
    public static final Locale CZECH = new Locale("cs");
    public static final Locale GREEK = new Locale("el");
    public static final Locale LATIN = new Locale("la");
    public static final Locale BULGARIAN = new Locale("bg");
    public static final Locale AFRIKAANS = new Locale("af");
    public static final Locale HINDI = new Locale("hi");
    public static final Locale HEBREW = new Locale("he");
    public static final Locale POLISH = new Locale("pl");
    public static final Locale PORTUGUESE = new Locale("pt");
    public static final Locale FINNISH = new Locale("fi");
    public static final Locale SWEDISH = new Locale("sv");
    public static final Locale RUSSIAN = new Locale("ru");
    public static final Locale ROMANIAN = new Locale("ro");
    public static final Locale VIETNAMESE = new Locale("vi");
    public static final Locale THAI = new Locale("th");
    public static final Locale TURKISH = new Locale("tr");
    public static final Locale UKRANIAN = new Locale("uk");
    public static final Locale ARABIC = new Locale("ar");
    public static final Locale WELSH = new Locale("cy");
    public static final Locale NORWEGIAN_BOKMAAL = new Locale("nb");
    public static final Locale NORWEGIAN_NYNORSK = new Locale("nn");
    public static final Locale HUNGARIAN = new Locale("hu");
    private final CommandManager manager;
    private final LocaleManager<CommandIssuer> localeManager;
    private final Map<ClassLoader, SetMultimap<String, Locale>> loadedBundles = new HashMap<ClassLoader, SetMultimap<String, Locale>>();
    private final List<ClassLoader> registeredClassLoaders = new ArrayList<ClassLoader>();

    public Locales(CommandManager commandManager) {
        this.manager = commandManager;
        this.localeManager = LocaleManager.create(commandManager::getIssuerLocale);
        this.addBundleClassLoader(this.getClass().getClassLoader());
    }

    public void loadLanguages() {
        this.addMessageBundles("acf-core");
    }

    public Locale getDefaultLocale() {
        return this.localeManager.getDefaultLocale();
    }

    public Locale setDefaultLocale(Locale locale) {
        return this.localeManager.setDefaultLocale(locale);
    }

    public void loadMissingBundles() {
        Set<Locale> set = this.manager.getSupportedLanguages();
        for (Locale locale : set) {
            for (SetMultimap<String, Locale> setMultimap : this.loadedBundles.values()) {
                for (String string : new HashSet(setMultimap.keys())) {
                    this.addMessageBundle(string, locale);
                }
            }
        }
    }

    public void addMessageBundles(String ... stringArray) {
        for (String string : stringArray) {
            Set<Locale> set = this.manager.getSupportedLanguages();
            for (Locale locale : set) {
                this.addMessageBundle(string, locale);
            }
        }
    }

    public boolean addMessageBundle(String string, Locale locale) {
        boolean bl = false;
        for (ClassLoader classLoader : this.registeredClassLoaders) {
            if (!this.addMessageBundle(classLoader, string, locale)) continue;
            bl = true;
        }
        return bl;
    }

    public boolean addMessageBundle(ClassLoader classLoader, String string, Locale locale) {
        SetMultimap<String, Locale> setMultimap = this.loadedBundles.getOrDefault(classLoader, (SetMultimap<String, Locale>)HashMultimap.create());
        if (!setMultimap.containsEntry((Object)string, (Object)locale) && this.localeManager.addMessageBundle(classLoader, string, locale)) {
            setMultimap.put((Object)string, (Object)locale);
            this.loadedBundles.put(classLoader, setMultimap);
            return true;
        }
        return false;
    }

    public void addMessageStrings(Locale locale, @NotNull Map<String, String> map) {
        HashMap<MessageKey, String> hashMap = new HashMap<MessageKey, String>(map.size());
        map.forEach((string, string2) -> hashMap.put(MessageKey.of(string), (String)string2));
        this.localeManager.addMessages(locale, hashMap);
    }

    public void addMessages(Locale locale, @NotNull Map<? extends MessageKeyProvider, String> map) {
        LinkedHashMap<MessageKey, String> linkedHashMap = new LinkedHashMap<MessageKey, String>();
        for (Map.Entry<? extends MessageKeyProvider, String> entry : map.entrySet()) {
            linkedHashMap.put(entry.getKey().getMessageKey(), entry.getValue());
        }
        this.localeManager.addMessages(locale, linkedHashMap);
    }

    public String addMessage(Locale locale, MessageKeyProvider messageKeyProvider, String string) {
        return this.localeManager.addMessage(locale, messageKeyProvider.getMessageKey(), string);
    }

    public String getMessage(CommandIssuer commandIssuer, MessageKeyProvider messageKeyProvider) {
        MessageKey messageKey = messageKeyProvider.getMessageKey();
        String string = this.localeManager.getMessage(commandIssuer, messageKey);
        if (string == null) {
            this.manager.log(LogLevel.ERROR, "Missing Language Key: " + messageKey.getKey());
            string = "<MISSING_LANGUAGE_KEY:" + messageKey.getKey() + ">";
        }
        return string;
    }

    public String getOptionalMessage(CommandIssuer commandIssuer, MessageKey messageKey) {
        if (commandIssuer == null) {
            return this.localeManager.getTable(this.getDefaultLocale()).getMessage(messageKey);
        }
        return this.localeManager.getMessage(commandIssuer, messageKey);
    }

    public String replaceI18NStrings(String string) {
        if (string == null) {
            return null;
        }
        Matcher matcher = ACFPatterns.I18N_STRING.matcher(string);
        if (!matcher.find()) {
            return string;
        }
        CommandIssuer commandIssuer = CommandManager.getCurrentCommandIssuer();
        matcher.reset();
        StringBuffer stringBuffer = new StringBuffer(string.length());
        while (matcher.find()) {
            MessageKey messageKey = MessageKey.of(matcher.group("key"));
            matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(this.getMessage(commandIssuer, messageKey)));
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    public boolean addBundleClassLoader(ClassLoader classLoader) {
        return !this.registeredClassLoaders.contains(classLoader) && this.registeredClassLoaders.add(classLoader);
    }
}

