package com.jeff_media.standalonepluginscreen;

import com.allatori.annotations.StringEncryption;
import com.jeff_media.standalonepluginscreen.StandalonePluginScreen;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@StringEncryption(value="disable")
public abstract class NagMessage {
    private static final String SETUP_SPIGOT_LINK = "https://www.spigotmc.org/wiki/spigot-installation/";
    private static final String DISCORD_LINK = "https://discord.jeff-media.com/";
    private static final String PLUGIN_NAME;
    private static final String PLUGIN_VERSION;
    private static final String TITLE = "{plugin} {version} requires Spigot";
    private static final String[] MESSAGE;

    public abstract void show();

    private static String getStringFromYaml(String string3, List<String> list) {
        return list.stream().filter(string2 -> string2.split(":")[0].trim().equals(string3)).map(string -> string.split(":")[1].replace("\"", "").trim()).findFirst().orElseThrow(() -> new RuntimeException("plugin.yml does not contain required field " + string3));
    }

    private static String insertPluginName(String string) {
        return string.replace("{plugin}", PLUGIN_NAME).replace("{version}", PLUGIN_VERSION);
    }

    static List<String> getMessage() {
        return Arrays.asList(MESSAGE).stream().map(NagMessage::insertPluginName).collect(Collectors.toList());
    }

    static String getTitle() {
        return NagMessage.insertPluginName(TITLE);
    }

    static String getSetupSpigotLink() {
        return SETUP_SPIGOT_LINK;
    }

    static String getDiscordLink() {
        return DISCORD_LINK;
    }

    static {
        MESSAGE = new String[]{"Thanks for downloading {plugin} {version}!", "", "{plugin} is a Spigot Plugin and is not meant to be run directly.", "You must put this .jar file into your server's plugins folder.", "", "See the following link for a tutorial on how to setup a Spigot server:", "{spigotLink}", "", "If you need help, feel free to join my Discord server:", "{discordLink}"};
        String string = "Unknown Plugin";
        String string2 = "?.?.?";
        try (InputStream inputStream = Objects.requireNonNull(StandalonePluginScreen.class.getResourceAsStream("/plugin.yml"), "Can't find plugin.yml file");
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);){
            List<String> list = bufferedReader.lines().collect(Collectors.toList());
            string = NagMessage.getStringFromYaml("name", list);
            string2 = NagMessage.getStringFromYaml("version", list);
        }
        catch (IOException | RuntimeException exception) {
            exception.printStackTrace();
        }
        PLUGIN_NAME = string;
        PLUGIN_VERSION = string2;
    }
}

