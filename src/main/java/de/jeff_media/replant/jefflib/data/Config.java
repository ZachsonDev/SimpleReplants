package de.jeff_media.replant.jefflib.data;

import de.jeff_media.replant.jefflib.JeffLib;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Objects;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class Config
extends YamlConfiguration {
    private final String filename;
    private final File file;

    public Config(@NotNull String string) {
        this.filename = string;
        this.file = new File(JeffLib.getPlugin().getDataFolder(), string);
        this.loadDefaults();
        this.reload();
    }

    private void loadDefaults() {
        YamlConfiguration yamlConfiguration;
        block14: {
            yamlConfiguration = new YamlConfiguration();
            try (InputStream inputStream = JeffLib.getPlugin().getResource(this.filename);){
                if (inputStream == null) break block14;
                try (InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(inputStream));){
                    yamlConfiguration.load((Reader)inputStreamReader);
                }
            }
            catch (IOException iOException) {
                throw new IllegalArgumentException("Could not load included config file " + this.filename, iOException);
            }
            catch (InvalidConfigurationException invalidConfigurationException) {
                throw new IllegalArgumentException("Invalid default config for " + this.filename, invalidConfigurationException);
            }
        }
        this.setDefaults((Configuration)yamlConfiguration);
    }

    public void reload() {
        this.saveDefaultConfig();
        try {
            this.load(this.file);
        }
        catch (IOException iOException) {
            new IllegalArgumentException("Could not find or load file " + this.filename, iOException).printStackTrace();
        }
        catch (InvalidConfigurationException invalidConfigurationException) {
            JeffLib.getLogger().severe("Your config file " + this.filename + " is invalid, using default values now. Please fix the below mentioned errors and try again:");
            invalidConfigurationException.printStackTrace();
        }
    }

    private void saveDefaultConfig() {
        if (!this.file.exists()) {
            File file = this.file.getParentFile();
            if (file != null && !file.exists() && !file.mkdirs()) {
                throw new UncheckedIOException(new IOException("Could not create directory " + file.getAbsolutePath()));
            }
            JeffLib.getPlugin().saveResource(this.filename, false);
        }
    }

    public void save() {
        this.save(this.file);
    }
}

