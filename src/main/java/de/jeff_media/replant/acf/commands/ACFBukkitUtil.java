package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.ACFUtil;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import de.jeff_media.replant.acf.commands.CommandManager;
import de.jeff_media.replant.acf.commands.MinecraftMessageKeys;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ACFBukkitUtil {
    public static String formatLocation(Location location) {
        if (location == null) {
            return null;
        }
        return location.getWorld().getName() + ":" + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)string);
    }

    @Deprecated
    public static void sendMsg(CommandSender commandSender, String string) {
        string = ACFBukkitUtil.color(string);
        for (String string2 : ACFPatterns.NEWLINE.split(string)) {
            commandSender.sendMessage(string2);
        }
    }

    public static Location stringToLocation(String string) {
        return ACFBukkitUtil.stringToLocation(string, null);
    }

    public static Location stringToLocation(String string, World world) {
        String[] stringArray;
        if (string == null) {
            return null;
        }
        String[] stringArray2 = ACFPatterns.COLON.split(string);
        if (stringArray2.length >= 4 || stringArray2.length == 3 && world != null) {
            String string2 = world != null ? world.getName() : stringArray2[0];
            int n = stringArray2.length == 3 ? 0 : 1;
            double d = Double.parseDouble(stringArray2[n]);
            double d2 = Double.parseDouble(stringArray2[n + 1]);
            double d3 = Double.parseDouble(stringArray2[n + 2]);
            Location location = new Location(Bukkit.getWorld((String)string2), d, d2, d3);
            if (stringArray2.length >= 6) {
                location.setPitch(Float.parseFloat(stringArray2[4]));
                location.setYaw(Float.parseFloat(stringArray2[5]));
            }
            return location;
        }
        if (stringArray2.length == 2 && (stringArray = ACFPatterns.COMMA.split(stringArray2[1])).length == 3) {
            String string3 = world != null ? world.getName() : stringArray2[0];
            double d = Double.parseDouble(stringArray[0]);
            double d4 = Double.parseDouble(stringArray[1]);
            double d5 = Double.parseDouble(stringArray[2]);
            return new Location(Bukkit.getWorld((String)string3), d, d4, d5);
        }
        return null;
    }

    public static String fullLocationToString(Location location) {
        if (location == null) {
            return null;
        }
        return new StringBuilder(64).append(location.getWorld().getName()).append(':').append(ACFUtil.precision(location.getX(), 4)).append(':').append(ACFUtil.precision(location.getY(), 4)).append(':').append(ACFUtil.precision(location.getZ(), 4)).append(':').append(ACFUtil.precision(location.getPitch(), 4)).append(':').append(ACFUtil.precision(location.getYaw(), 4)).toString();
    }

    public static String fullBlockLocationToString(Location location) {
        if (location == null) {
            return null;
        }
        return new StringBuilder(64).append(location.getWorld().getName()).append(':').append(location.getBlockX()).append(':').append(location.getBlockY()).append(':').append(location.getBlockZ()).append(':').append(ACFUtil.precision(location.getPitch(), 4)).append(':').append(ACFUtil.precision(location.getYaw(), 4)).toString();
    }

    public static String blockLocationToString(Location location) {
        if (location == null) {
            return null;
        }
        return new StringBuilder(32).append(location.getWorld().getName()).append(':').append(location.getBlockX()).append(':').append(location.getBlockY()).append(':').append(location.getBlockZ()).toString();
    }

    public static double distance(@NotNull Entity entity, @NotNull Entity entity2) {
        return ACFBukkitUtil.distance(entity.getLocation(), entity2.getLocation());
    }

    public static double distance2d(@NotNull Entity entity, @NotNull Entity entity2) {
        return ACFBukkitUtil.distance2d(entity.getLocation(), entity2.getLocation());
    }

    public static double distance2d(@NotNull Location location, @NotNull Location location2) {
        location = location.clone();
        location.setY(location2.getY());
        return ACFBukkitUtil.distance(location, location2);
    }

    public static double distance(@NotNull Location location, @NotNull Location location2) {
        if (location.getWorld() != location2.getWorld()) {
            return 0.0;
        }
        return location.distance(location2);
    }

    public static Location getTargetLoc(Player player) {
        return ACFBukkitUtil.getTargetLoc(player, 128);
    }

    public static Location getTargetLoc(Player player, int n) {
        return ACFBukkitUtil.getTargetLoc(player, n, 1.5);
    }

    public static Location getTargetLoc(Player player, int n, double d) {
        try {
            Location location = player.getTargetBlock((Set)null, n).getLocation();
            location.setY(location.getY() + d);
            return location;
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static Location getRandLoc(Location location, int n) {
        return ACFBukkitUtil.getRandLoc(location, n, n, n);
    }

    public static Location getRandLoc(Location location, int n, int n2) {
        return ACFBukkitUtil.getRandLoc(location, n, n2, n);
    }

    @NotNull
    public static Location getRandLoc(Location location, int n, int n2, int n3) {
        Location location2 = location.clone();
        location2.setX(ACFUtil.rand(location.getX() - (double)n, location.getX() + (double)n));
        location2.setY(ACFUtil.rand(location.getY() - (double)n2, location.getY() + (double)n2));
        location2.setZ(ACFUtil.rand(location.getZ() - (double)n3, location.getZ() + (double)n3));
        return location2;
    }

    public static String removeColors(String string) {
        return ChatColor.stripColor((String)ACFBukkitUtil.color(string));
    }

    public static String replaceChatString(String string, String string2, String string3) {
        return ACFBukkitUtil.replaceChatString(string, Pattern.compile(Pattern.quote(string2), 2), string3);
    }

    public static String replaceChatString(String string, Pattern pattern, String string2) {
        String[] stringArray = pattern.split(string + "1");
        if (stringArray.length < 2) {
            return pattern.matcher(string).replaceAll(string2);
        }
        string = stringArray[0];
        for (int i = 1; i < stringArray.length; ++i) {
            String string3 = ChatColor.getLastColors((String)string);
            string = string + string2 + string3 + stringArray[i];
        }
        return string.substring(0, string.length() - 1);
    }

    public static boolean isWithinDistance(@NotNull Player player, @NotNull Player player2, int n) {
        return ACFBukkitUtil.isWithinDistance(player.getLocation(), player2.getLocation(), n);
    }

    public static boolean isWithinDistance(@NotNull Location location, @NotNull Location location2, int n) {
        return location.getWorld() == location2.getWorld() && location.distance(location2) <= (double)n;
    }

    public static Player findPlayerSmart(CommandSender commandSender, String string) {
        CommandManager commandManager = CommandManager.getCurrentCommandManager();
        if (commandManager != null) {
            return ACFBukkitUtil.findPlayerSmart(commandManager.getCommandIssuer(commandSender), string);
        }
        throw new IllegalStateException("You may not use the ACFBukkitUtil#findPlayerSmart(CommandSender) async to the command execution.");
    }

    public static Player findPlayerSmart(CommandIssuer commandIssuer, String string) {
        CommandSender commandSender = (CommandSender)commandIssuer.getIssuer();
        if (string == null) {
            return null;
        }
        String string2 = ACFUtil.replace(string, ":confirm", "");
        List list = Bukkit.getServer().matchPlayer(string2);
        ArrayList<Player> arrayList = new ArrayList<Player>();
        ACFBukkitUtil.findMatches(string, commandSender, list, arrayList);
        if (list.size() > 1 || arrayList.size() > 1) {
            String string3 = list.stream().map(Player::getName).collect(Collectors.joining(", "));
            commandIssuer.sendError(MinecraftMessageKeys.MULTIPLE_PLAYERS_MATCH, "{search}", string2, "{all}", string3);
            return null;
        }
        if (list.isEmpty()) {
            if (!commandIssuer.getManager().isValidName(string2)) {
                commandIssuer.sendError(MinecraftMessageKeys.IS_NOT_A_VALID_NAME, "{name}", string2);
                return null;
            }
            Player player = ACFUtil.getFirstElement(arrayList);
            if (player == null) {
                commandIssuer.sendError(MinecraftMessageKeys.NO_PLAYER_FOUND_SERVER, "{search}", string2);
                return null;
            }
            commandIssuer.sendInfo(MinecraftMessageKeys.PLAYER_IS_VANISHED_CONFIRM, "{vanished}", player.getName());
            return null;
        }
        return (Player)list.get(0);
    }

    private static void findMatches(String string, CommandSender commandSender, List<Player> list, List<Player> list2) {
        Iterator<Player> iterator = list.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (!(commandSender instanceof Player) || ((Player)commandSender).canSee(player)) continue;
            if (commandSender.hasPermission("acf.seevanish")) {
                if (string.endsWith(":confirm")) continue;
                list2.add(player);
                iterator.remove();
                continue;
            }
            iterator.remove();
        }
    }

    public static boolean isValidName(@Nullable String string) {
        return string != null && !string.isEmpty() && ACFPatterns.VALID_NAME_PATTERN.matcher(string).matches();
    }

    static boolean isValidItem(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() != Material.AIR && itemStack.getAmount() > 0;
    }
}

