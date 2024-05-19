package de.jeff_media.replant.jefflib;

import com.allatori.annotations.DoNotRename;
import de.jeff_media.replant.jefflib.ClassUtils;
import de.jeff_media.replant.jefflib.ProtectionUtils;
import de.jeff_media.replant.jefflib.ServerUtils;
import de.jeff_media.replant.jefflib.data.McVersion;
import de.jeff_media.replant.jefflib.events.PlayerJumpEvent;
import de.jeff_media.replant.jefflib.exceptions.NMSNotSupportedException;
import de.jeff_media.replant.jefflib.internal.annotations.Internal;
import de.jeff_media.replant.jefflib.internal.annotations.NMS;
import de.jeff_media.replant.jefflib.internal.glowenchantment.GlowEnchantmentFactory;
import de.jeff_media.replant.jefflib.internal.listeners.BlockTrackListener;
import de.jeff_media.replant.jefflib.internal.listeners.PlayerScrollListener;
import de.jeff_media.replant.jefflib.internal.nms.AbstractNMSHandler;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public final class JeffLib {
    public static String LATEST_NMS = "v1_20_5";
    private static final Random random = new Random();
    private static final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
    private static Plugin plugin;
    private static boolean debug;
    private static String version;
    private static AbstractNMSHandler nmsHandler;
    private static boolean initDone;

    public static Logger getLogger() {
        Plugin plugin = JeffLib.getPlugin();
        if (plugin != null) {
            return plugin.getLogger();
        }
        return Bukkit.getLogger();
    }

    @Internal
    static void setPluginMock(Plugin plugin) {
        if (!ServerUtils.isRunningMockBukkit()) {
            throw new IllegalAccessException();
        }
        JeffLib.plugin = plugin;
    }

    @Deprecated
    @Internal
    private static Plugin getPlugin0() {
        if (plugin == null) {
            plugin = JavaPlugin.getProvidingPlugin(ClassUtils.getCurrentClass(1));
        }
        return plugin;
    }

    @DoNotRename
    public static Plugin getPlugin() {
        if (plugin == null) {
            try {
                plugin = JavaPlugin.getProvidingPlugin(ClassUtils.getCurrentClass(1));
                JeffLib.init(plugin);
            }
            catch (IllegalArgumentException | IllegalStateException runtimeException) {
                Object object;
                ArrayList<String> arrayList = new ArrayList<String>();
                String string = "";
                try {
                    object = Thread.currentThread().getStackTrace();
                    PluginDescriptionFile pluginDescriptionFile = JeffLib.getPluginDescriptionFile();
                    String string2 = pluginDescriptionFile.getMain();
                    Object object2 = null;
                    for (Object object3 : object) {
                        if (!((StackTraceElement)object3).getClassName().equals(string2)) continue;
                        if (object2 == null) {
                            object2 = object3;
                        }
                        String string3 = ((StackTraceElement)object3).getLineNumber() <= 0 ? "?" : String.valueOf(((StackTraceElement)object3).getLineNumber());
                        arrayList.add(((StackTraceElement)object3).getClassName() + "." + ((StackTraceElement)object3).getMethodName() + "(" + ((StackTraceElement)object3).getFileName() + ":" + string3 + ")");
                    }
                    if (object2 != null) {
                        String string4 = ((StackTraceElement)object2).getLineNumber() <= 0 ? "?" : String.valueOf(((StackTraceElement)object2).getLineNumber());
                        string = ((StackTraceElement)object2).getFileName() + " at line " + (String)string4;
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                object = Bukkit.getLogger();
                if (arrayList.isEmpty()) {
                    ((Logger)object).severe("[JeffLib] Oh no! I couldn't find the instance of your plugin!");
                    ((Logger)object).severe("[JeffLib] It seems like you're trying to use JeffLib before your plugin was enabled by the PluginManager.");
                    ((Logger)object).severe("[JeffLib]");
                    ((Logger)object).severe("[JeffLib] Please either wait until your plugin's onLoad() or onEnable() method was called, or call");
                    ((Logger)object).severe("[JeffLib] \"JeffLib.init(this)\" in your plugin's constructor or init block.");
                } else {
                    ((Logger)object).severe("[JeffLib] Oh no! You're trying to access one of JeffLib's methods before your plugin was enabled at the following location:");
                    ((Logger)object).severe("[JeffLib]");
                    for (String string2 : arrayList) {
                        ((Logger)object).severe("[JeffLib]   " + string2);
                    }
                    ((Logger)object).severe("[JeffLib]");
                    ((Logger)object).severe("[JeffLib] Please call \"JeffLib.init(this)\" before doing whatever you do in " + string + ", or wait until your plugin's onLoad() or onEnable() method was called.");
                }
                throw new IllegalStateException();
            }
        }
        return plugin;
    }

    private static PluginDescriptionFile getPluginDescriptionFile() {
        URLClassLoader uRLClassLoader = (URLClassLoader)JeffLib.class.getClassLoader();
        Field field = uRLClassLoader.getClass().getDeclaredField("description");
        field.setAccessible(true);
        return (PluginDescriptionFile)field.get(uRLClassLoader);
    }

    public static void debug(String string) {
        if (debug) {
            JeffLib.getLogger().info("[JeffLib] [Debug] " + string);
        }
    }

    public static void setDebug(boolean bl) {
        debug = bl;
    }

    @DoNotRename
    @Internal
    @NMS
    public static AbstractNMSHandler getNMSHandler() {
        if (nmsHandler == null) {
            JeffLib.enableNMS();
            if (nmsHandler == null) {
                throw new NMSNotSupportedException();
            }
        }
        return nmsHandler;
    }

    @NMS
    public static void enableNMS() {
        String string;
        String string3;
        block11: {
            string3 = JeffLib.class.getPackage().getName();
            McVersion mcVersion = McVersion.current();
            if (mcVersion.equals(new McVersion(1, 20, 6))) {
                mcVersion = new McVersion(1, 20, 5);
            }
            if (mcVersion.equals(new McVersion(1, 20, 3))) {
                mcVersion = new McVersion(1, 20, 4);
            }
            if (mcVersion.equals(new McVersion(1, 20))) {
                mcVersion = new McVersion(1, 20, 1);
            }
            string = mcVersion.isAtLeast(1, 19) ? "v" + mcVersion.getMajor() + "_" + mcVersion.getMinor() + (mcVersion.getPatch() > 0 ? "_" + mcVersion.getPatch() : "") : Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            try {
                nmsHandler = (AbstractNMSHandler)Class.forName(string3 + ".internal.nms." + string + ".NMSHandler").getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (ReflectiveOperationException reflectiveOperationException) {
                String string4 = ClassUtils.listAllClasses().stream().filter(string2 -> string2.endsWith(string + ".NMSHandler")).findFirst().orElse(null);
                if (string4 == null) break block11;
                try {
                    nmsHandler = (AbstractNMSHandler)Class.forName(string4).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                }
                catch (ReflectiveOperationException reflectiveOperationException2) {
                    // empty catch block
                }
            }
        }
        if (nmsHandler == null) {
            try {
                nmsHandler = (AbstractNMSHandler)Class.forName(string3 + ".internal.nms." + LATEST_NMS + ".NMSHandler").getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (ReflectiveOperationException reflectiveOperationException) {
                // empty catch block
            }
            if (nmsHandler == null) {
                throw new NMSNotSupportedException("JeffLib " + version + " does not support NMS for " + McVersion.current().getName() + "(" + string + ")");
            }
        }
    }

    public static ThreadLocalRandom getThreadLocalRandom() {
        return threadLocalRandom;
    }

    public static Random getRandom() {
        return random;
    }

    public static void registerPlayerScrollEvent() {
        Bukkit.getPluginManager().registerEvents((Listener)new PlayerScrollListener(), JeffLib.getPlugin());
    }

    public static void registerPlayerJumpEvent() {
        PlayerJumpEvent.registerListener();
    }

    public static void registerBlockTracker() {
        if (McVersion.current().isAtLeast(1, 16, 3)) {
            Bukkit.getPluginManager().registerEvents((Listener)new BlockTrackListener(), JeffLib.getPlugin());
        } else {
            JeffLib.getPlugin().getLogger().info("You are using an MC version below 1.16.3 - Block Tracking features will be disabled.");
        }
    }

    public static void init(Plugin plugin) {
        JeffLib.plugin = plugin;
        if (!initDone) {
            ProtectionUtils.loadPluginProtections();
        }
        if (!McVersion.current().isAtLeast(1, 20, 5)) {
            GlowEnchantmentFactory.register();
        }
        initDone = true;
    }

    @Deprecated
    public static void init(Plugin plugin, boolean bl) {
        JeffLib.init(plugin);
        if (bl) {
            JeffLib.enableNMS();
        }
    }

    private JeffLib() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static boolean isDebug() {
        return debug;
    }

    public static String getVersion() {
        return version;
    }

    static {
        debug = false;
        version = "N/A";
        initDone = false;
        if (!ServerUtils.isRunningMockBukkit()) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(JeffLib.class.getResourceAsStream("/jefflib.version")), StandardCharsets.UTF_8));){
                version = bufferedReader.readLine();
            }
            catch (Throwable throwable) {}
        } else {
            version = "MOCK";
        }
    }

    public static final class KitchenSink {
    }
}

