package de.jeff_media.replant.config;

import de.jeff_media.replant.Main;
import de.jeff_media.replant.utils.FileUtils;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class Messages {
    private static final Main main = Main.getInstance();
    private static final File langDir = new File(main.getDataFolder(), "lang");
    private static YamlConfiguration yaml;
    private static String PREFIX;
    public static String COMMAND_PLAYERS_ONLY;
    public static String CONFIG_RELOADED;
    public static String REPLANT_CROPS_ENABLED;
    public static String REPLANT_CROPS_DISABLED;
    public static String NO_PERMISSION;

    public Messages(String string) {
        File file = this.getLangFile(string);
        if (!file.exists()) {
            main.getLogger().warning(String.format("Language '%s' not found in %s - falling back to default English (en) translation.", string, langDir.getAbsolutePath()));
            yaml = YamlConfiguration.loadConfiguration((Reader)new InputStreamReader(FileUtils.getFileFromResourceAsStream("lang/en.yml")));
        } else {
            main.getLogger().info(String.format("Using language '%s'", string));
            yaml = YamlConfiguration.loadConfiguration((File)file);
        }
        PREFIX = main.getConfig().getString("prefix", "");
        COMMAND_PLAYERS_ONLY = yaml.getString("command-players-only");
        CONFIG_RELOADED = yaml.getString("config-reloaded");
        REPLANT_CROPS_ENABLED = yaml.getString("replant-crops-enabled");
        REPLANT_CROPS_DISABLED = yaml.getString("replant-crops-disabled");
        NO_PERMISSION = yaml.getString("no-permission", "&cI'm sorry, but you do not have permission to perform this commmand.");
    }

    private File getLangFile(String string) {
        langDir.mkdirs();
        return new File(langDir, string + ".yml");
    }

    public static void sendMessage(CommandSender commandSender, String string) {
        if (string == null || string.length() == 0) {
            return;
        }
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes((char)'&', (String)(PREFIX + string)));
    }
}

