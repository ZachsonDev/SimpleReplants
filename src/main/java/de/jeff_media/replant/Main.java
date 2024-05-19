package de.jeff_media.replant;

import de.jeff_media.replant.acf.commands.PaperCommandManager;
import de.jeff_media.replant.commands.MainCommand;
import de.jeff_media.replant.config.ConfigUpdater;
import de.jeff_media.replant.config.CropTypesConfig;
import de.jeff_media.replant.config.Messages;
import de.jeff_media.replant.daddy.Daddy_Stepsister;
import de.jeff_media.replant.handlers.ParticleManager;
import de.jeff_media.replant.handlers.SaplingManager;
import de.jeff_media.replant.hooks.PluginHandler;
import de.jeff_media.replant.hooks.WorldGuardHandler;
import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.exceptions.NMSNotSupportedException;
import de.jeff_media.replant.listeners.CropListener;
import de.jeff_media.replant.listeners.SaplingListener;
import de.jeff_media.replant.nbt.PlayerManager;
import de.jeff_media.replant.updatechecker.UpdateChecker;
import de.jeff_media.replant.updatechecker.UserAgentBuilder;
import de.jeff_media.replant.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main
extends JavaPlugin {
    private static Main instance;
    private PluginHandler worldGuardHandler = new PluginHandler();
    private PlayerManager playerManager;
    private SaplingManager saplingManager;
    private ParticleManager saplingParticleManager;
    private ParticleManager cropParticleManager;
    private CropTypesConfig cropConf;
    private static boolean debug;

    public static Main getInstance() {
        return instance;
    }

    public ParticleManager getSaplingParticleManager() {
        return this.saplingParticleManager;
    }

    public CropTypesConfig getCropConf() {
        return this.cropConf;
    }

    public ParticleManager getCropParticleManager() {
        return this.cropParticleManager;
    }

    public static void debug(String string) {
        if (debug) {
            Main.getInstance().getLogger().warning("[DEBUG] " + string);
        }
    }

    public PluginHandler getWorldGuardHandler() {
        return this.worldGuardHandler;
    }

    public void onEnable() {
        Main.loadConfig0();
        Daddy_Stepsister.init((Plugin)this);
        if (Daddy_Stepsister.allows(null)) {
            Daddy_Stepsister.createVerificationFile();
        }
        JeffLib.init((Plugin)this);
        try {
            JeffLib.enableNMS();
        }
        catch (NMSNotSupportedException nMSNotSupportedException) {
            // empty catch block
        }
        instance = this;
        this.saveDefaultConfig();
        FileUtils.saveDefaultLangFiles();
        this.reload();
        new CropListener();
        new SaplingListener();
        PaperCommandManager paperCommandManager = new PaperCommandManager((Plugin)this);
        paperCommandManager.registerCommand(new MainCommand());
        this.playerManager = new PlayerManager();
        this.saplingManager = new SaplingManager();
        this.saplingParticleManager = new ParticleManager("sapling");
        this.cropParticleManager = new ParticleManager("crop");
        this.initUpdateChecker();
    }

    private void initUpdateChecker() {
        UpdateChecker.init((Plugin)this, "https://api.jeff-media.de/replant/latest-version.txt").setUserAgent(UserAgentBuilder.getDefaultUserAgent().addSpigotUserId()).setDonationLink("https://paypal.me/mfnalex").setDownloadLink("https://www.spigotmc.org/resources/authors/mfnalex.175238/").suppressUpToDateMessage(true);
        if (this.getConfig().getString("check-for-updates").equalsIgnoreCase("true")) {
            UpdateChecker.getInstance().checkEveryXHours(this.getConfig().getDouble("check-for-updates-interval")).checkNow();
        } else if (this.getConfig().getString("check-for-updates").equalsIgnoreCase("on-startup")) {
            UpdateChecker.getInstance().checkNow();
        }
    }

    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public SaplingManager getSaplingManager() {
        return this.saplingManager;
    }

    public void reload() {
        this.reloadConfig();
        ConfigUpdater.updateConfig();
        debug = this.getConfig().getBoolean("debug");
        this.cropConf = new CropTypesConfig();
        new Messages(this.getConfig().getString("language"));
        if (this.getConfig().getBoolean("use-worldguard") && Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            try {
                this.worldGuardHandler = new WorldGuardHandler();
            }
            catch (Exception exception) {
                this.getLogger().warning("Could not hook into WorldGuard although it seems to be installed.");
                this.getLogger().warning("Detected WorldGuard version: " + Bukkit.getPluginManager().getPlugin("WorldGuard").getDescription().getVersion());
                this.worldGuardHandler = new PluginHandler();
            }
        }
    }

    static {
        debug = false;
    }

    private static /* bridge */ /* synthetic */ void loadConfig0() {
    }
}

