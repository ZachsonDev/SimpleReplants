package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.BukkitCommandManager;
import de.jeff_media.replant.acf.commands.Locales;
import de.jeff_media.replant.acf.locales.MessageKey;
import java.io.File;
import java.util.Locale;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class BukkitLocales
extends Locales {
    private final BukkitCommandManager manager;

    public BukkitLocales(BukkitCommandManager bukkitCommandManager) {
        super(bukkitCommandManager);
        this.manager = bukkitCommandManager;
        this.addBundleClassLoader(this.manager.getPlugin().getClass().getClassLoader());
    }

    @Override
    public void loadLanguages() {
        super.loadLanguages();
        String string = "acf-" + this.manager.plugin.getDescription().getName();
        this.addMessageBundles("acf-minecraft", string, string.toLowerCase(Locale.ENGLISH));
    }

    public boolean loadYamlLanguageFile(File file, Locale locale) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.load(file);
        return this.loadLanguage((FileConfiguration)yamlConfiguration, locale);
    }

    public boolean loadYamlLanguageFile(String string, Locale locale) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.load(new File(this.manager.plugin.getDataFolder(), string));
        return this.loadLanguage((FileConfiguration)yamlConfiguration, locale);
    }

    public boolean loadLanguage(FileConfiguration fileConfiguration, Locale locale) {
        boolean bl = false;
        for (String string : fileConfiguration.getKeys(true)) {
            String string2;
            if (!fileConfiguration.isString(string) && !fileConfiguration.isDouble(string) && !fileConfiguration.isLong(string) && !fileConfiguration.isInt(string) && !fileConfiguration.isBoolean(string) || (string2 = fileConfiguration.getString(string)) == null || string2.isEmpty()) continue;
            this.addMessage(locale, MessageKey.of(string), string2);
            bl = true;
        }
        return bl;
    }
}

