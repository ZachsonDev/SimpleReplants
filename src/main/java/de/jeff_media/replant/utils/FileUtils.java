package de.jeff_media.replant.utils;

import de.jeff_media.replant.Main;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class FileUtils {
    public static void saveDefaultLangFiles() {
        Main main = Main.getInstance();
        File file = new File(main.getDataFolder(), "lang");
        file.mkdirs();
        for (String string : new String[]{"en", "de", "tr", "nl", "ru", "hu", "aze"}) {
            InputStream inputStream = FileUtils.getFileFromResourceAsStream("lang/" + string + ".yml");
            File file2 = new File(new File(main.getDataFolder(), "lang"), string + ".yml");
            try (FileOutputStream fileOutputStream = new FileOutputStream(file2, false);){
                int n;
                byte[] byArray = new byte[8192];
                while ((n = inputStream.read(byArray)) != -1) {
                    fileOutputStream.write(byArray, 0, n);
                }
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }

    public static InputStream getFileFromResourceAsStream(String string) {
        ClassLoader classLoader = Main.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(string);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + string);
        }
        return inputStream;
    }

    private static File getFileFromResource(String string) {
        ClassLoader classLoader = Main.class.getClassLoader();
        URL uRL = classLoader.getResource(string);
        if (uRL == null) {
            throw new IllegalArgumentException("file not found! " + string);
        }
        return new File(uRL.toURI());
    }
}

