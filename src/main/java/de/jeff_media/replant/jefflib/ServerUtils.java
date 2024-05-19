package de.jeff_media.replant.jefflib;

import de.jeff_media.replant.jefflib.ClassUtils;
import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.ReflUtils;
import de.jeff_media.replant.jefflib.data.TPS;
import de.jeff_media.replant.jefflib.internal.ServerListPingEventFactory;
import java.io.File;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.server.ServerListPingEvent;
import org.jetbrains.annotations.NotNull;

public final class ServerUtils {
    private static final Field CURRENT_TICK_FIELD;
    private static final InetAddress LOCALHOST;
    private static boolean HAS_TRANSLATION_KEY_PROVIDER;

    public static CompletableFuture<String> getEffectiveMotd() {
        if (LOCALHOST == null) {
            return CompletableFuture.completedFuture(Bukkit.getMotd());
        }
        return CompletableFuture.supplyAsync(() -> {
            ServerListPingEvent serverListPingEvent = ServerListPingEventFactory.createServerListPingEvent(LOCALHOST.getHostName(), LOCALHOST, Bukkit.getMotd(), false, Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
            Bukkit.getPluginManager().callEvent((Event)serverListPingEvent);
            return serverListPingEvent.getMotd();
        });
    }

    public static boolean isRunningMockBukkit() {
        return Bukkit.getServer().getClass().getName().equals("be.seeseemelk.mockbukkit.ServerMock");
    }

    public static boolean isRunningForge() {
        return ClassUtils.exists("net.minecraftforge.server.ServerMain");
    }

    public static boolean isRunningSpigot() {
        return ClassUtils.exists("net.md_5.bungee.api.ChatColor");
    }

    public static boolean isRunningPaper() {
        return ClassUtils.exists("com.destroystokyo.paper.PaperConfig");
    }

    public static ServerLifePhase getLifePhase() {
        int n = ServerUtils.getCurrentTick();
        if (n == -1) {
            return ServerLifePhase.STARTUP;
        }
        if (n == -2) {
            return ServerLifePhase.UNKNOWN;
        }
        return JeffLib.getNMSHandler().isServerRunnning() ? ServerLifePhase.RUNNING : ServerLifePhase.SHUTDOWN;
    }

    public static int getCurrentTick() {
        if (CURRENT_TICK_FIELD == null) {
            return -2;
        }
        try {
            return CURRENT_TICK_FIELD.getInt(Bukkit.getScheduler());
        }
        catch (IllegalAccessException illegalAccessException) {
            return -2;
        }
    }

    public static TPS getTps() {
        return new TPS(JeffLib.getNMSHandler().getTps());
    }

    @NotNull
    public static File getServerFolder() {
        return Paths.get("", new String[0]).toAbsolutePath().toFile();
    }

    public static boolean hasTranslationKeyProvider() {
        return HAS_TRANSLATION_KEY_PROVIDER;
    }

    private ServerUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        InetAddress inetAddress;
        Field field;
        try {
            field = Bukkit.getScheduler().getClass().getDeclaredField("currentTick");
            field.setAccessible(true);
        }
        catch (Exception exception) {
            field = null;
        }
        CURRENT_TICK_FIELD = field;
        try {
            inetAddress = InetAddress.getLocalHost();
        }
        catch (UnknownHostException unknownHostException) {
            inetAddress = null;
        }
        LOCALHOST = inetAddress;
        HAS_TRANSLATION_KEY_PROVIDER = ReflUtils.getMethod(Material.class, "getBlockTranslationKey") != null;
    }

    public static enum ServerLifePhase {
        STARTUP,
        RUNNING,
        SHUTDOWN,
        UNKNOWN;

    }
}

