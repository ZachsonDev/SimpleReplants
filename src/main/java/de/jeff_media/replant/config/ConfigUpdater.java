package de.jeff_media.replant.config;

import de.jeff_media.replant.Main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public final class ConfigUpdater {
    private static final String[] LINES_CONTAINING_STRING_LISTS = new String[0];
    private static final String[] LINES_IGNORED = new String[]{"config-version:", "plugin-version:"};
    private static final String[] CONFLICTING_NODES_NEEDING_NO_QUOTES = new String[0];
    private static final String[] NODES_NEEDING_DOUBLE_QUOTES = new String[]{"prefix"};
    private static final String[] NODES_NEEDING_SINGLE_QUOTES = new String[0];

    private static void backupCurrentConfig(Main main) {
        File file = new File(ConfigUpdater.getFilePath(main, "config.yml"));
        File file2 = new File(ConfigUpdater.getFilePath(main, "config-backup-" + main.getConfig().getString("plugin-version") + ".yml"));
        if (file2.exists()) {
            file2.delete();
        }
        if (file.getAbsoluteFile().renameTo(file2.getAbsoluteFile())) {
            Main.debug("Could not rename " + file.getAbsolutePath() + " to " + file2.getAbsolutePath());
        }
    }

    private static void debug(Logger logger, String string) {
    }

    private static String getFilePath(Main main, String string) {
        return main.getDataFolder() + File.separator + string;
    }

    private static List<String> getNewConfigAsArrayList(Main main) {
        try {
            List<String> list = Files.readAllLines(Paths.get(ConfigUpdater.getFilePath(main, "config.yml"), new String[0]), StandardCharsets.UTF_8);
            return list;
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
            return null;
        }
    }

    private static long getNewConfigVersion() {
        InputStream inputStream = ((Object)((Object)Main.getInstance())).getClass().getResourceAsStream("/config-version.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            return Long.parseLong(bufferedReader.readLine());
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
            return 0L;
        }
    }

    private static String getQuotes(String string) {
        for (String string2 : CONFLICTING_NODES_NEEDING_NO_QUOTES) {
            if (!string.startsWith(string2)) continue;
            return "";
        }
        for (String string2 : NODES_NEEDING_DOUBLE_QUOTES) {
            if (!string.startsWith(string2)) continue;
            return "\"";
        }
        for (String string2 : NODES_NEEDING_SINGLE_QUOTES) {
            if (!string.startsWith(string2)) continue;
            return "'";
        }
        return "";
    }

    private static boolean lineContainsIgnoredNode(String string) {
        for (String string2 : LINES_IGNORED) {
            if (!string.startsWith(string2)) continue;
            return true;
        }
        return false;
    }

    private static boolean lineIsStringList(String string) {
        for (String string2 : LINES_CONTAINING_STRING_LISTS) {
            if (!string.startsWith(string2)) continue;
            return true;
        }
        return false;
    }

    private static void saveArrayListToConfig(Main main, List<String> list) {
        try {
            BufferedWriter bufferedWriter = Files.newBufferedWriter(new File(ConfigUpdater.getFilePath(main, "config.yml")).toPath(), StandardCharsets.UTF_8, new OpenOption[0]);
            for (String string : list) {
                bufferedWriter.write(string + System.lineSeparator());
            }
            bufferedWriter.close();
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    public static void updateConfig() {
        Main main = Main.getInstance();
        Logger logger = main.getLogger();
        ConfigUpdater.debug(logger, "Newest config version  = " + ConfigUpdater.getNewConfigVersion());
        ConfigUpdater.debug(logger, "Current config version = " + main.getConfig().getLong("config-version"));
        if (main.getConfig().getLong("config-version") >= ConfigUpdater.getNewConfigVersion()) {
            ConfigUpdater.debug(logger, "The config currently used has an equal or newer version than the one shipped with this release.");
            return;
        }
        logger.info("===========================================");
        logger.info("You are using an outdated config file.");
        logger.info("Your config file will now be updated to the");
        logger.info("newest version. You changes will be kept.");
        logger.info("===========================================");
        ConfigUpdater.backupCurrentConfig(main);
        main.saveDefaultConfig();
        Set set = main.getConfig().getKeys(false);
        ArrayList<String> arrayList = new ArrayList<String>();
        Iterator<String> iterator = ConfigUpdater.getNewConfigAsArrayList(main).iterator();
        while (iterator.hasNext()) {
            String string;
            String string2 = string = iterator.next();
            if (string.startsWith("-") || string.startsWith(" -") || string.startsWith("  -")) {
                ConfigUpdater.debug(logger, "Not including default String list entry: " + string);
                string2 = null;
            } else if (ConfigUpdater.lineContainsIgnoredNode(string)) {
                ConfigUpdater.debug(logger, "Not updating this line: " + string);
            } else if (ConfigUpdater.lineIsStringList(string)) {
                string2 = null;
                arrayList.add(string);
                String string3 = string.split(":")[0];
                for (String string4 : main.getConfig().getStringList(string3)) {
                    arrayList.add("- " + string4);
                }
            } else {
                for (Object object : set) {
                    String string4;
                    if (!string.startsWith((String)object + ":")) continue;
                    string4 = ConfigUpdater.getQuotes((String)object);
                    String string5 = main.getConfig().get((String)object).toString();
                    if (((String)object).equals("hologram-text")) {
                        string5 = string5.replaceAll("\n", "\\\\n");
                    }
                    string2 = (String)object + ": " + string4 + string5 + string4;
                }
            }
            if (string2 == null) continue;
            arrayList.add(string2);
        }
        ConfigUpdater.saveArrayListToConfig(main, arrayList);
    }
}

