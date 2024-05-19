package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFBukkitUtil;
import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.ACFUtil;
import de.jeff_media.replant.acf.commands.BukkitCommandContexts_1_12;
import de.jeff_media.replant.acf.commands.BukkitCommandExecutionContext;
import de.jeff_media.replant.acf.commands.BukkitCommandIssuer;
import de.jeff_media.replant.acf.commands.BukkitCommandManager;
import de.jeff_media.replant.acf.commands.CommandContexts;
import de.jeff_media.replant.acf.commands.InvalidCommandArgument;
import de.jeff_media.replant.acf.commands.MessageKeys;
import de.jeff_media.replant.acf.commands.MinecraftMessageKeys;
import de.jeff_media.replant.acf.commands.bukkit.contexts.OnlinePlayer;
import de.jeff_media.replant.acf.locales.MessageKeyProvider;
import java.util.HashSet;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Contract;

public class BukkitCommandContexts
extends CommandContexts<BukkitCommandExecutionContext> {
    public BukkitCommandContexts(BukkitCommandManager bukkitCommandManager) {
        super(bukkitCommandManager);
        this.registerContext(OnlinePlayer.class, bukkitCommandExecutionContext -> this.getOnlinePlayer((BukkitCommandIssuer)bukkitCommandExecutionContext.getIssuer(), bukkitCommandExecutionContext.popFirstArg(), false));
        this.registerContext(de.jeff_media.replant.acf.commands.contexts.OnlinePlayer.class, bukkitCommandExecutionContext -> {
            OnlinePlayer onlinePlayer = this.getOnlinePlayer((BukkitCommandIssuer)bukkitCommandExecutionContext.getIssuer(), bukkitCommandExecutionContext.popFirstArg(), false);
            return new de.jeff_media.replant.acf.commands.contexts.OnlinePlayer(onlinePlayer.getPlayer());
        });
        this.registerContext(OnlinePlayer[].class, bukkitCommandExecutionContext -> {
            BukkitCommandIssuer bukkitCommandIssuer = (BukkitCommandIssuer)bukkitCommandExecutionContext.getIssuer();
            String string = bukkitCommandExecutionContext.popFirstArg();
            boolean bl = bukkitCommandExecutionContext.hasFlag("allowmissing");
            HashSet<OnlinePlayer> hashSet = new HashSet<OnlinePlayer>();
            Pattern pattern = ACFPatterns.COMMA;
            String string2 = bukkitCommandExecutionContext.getFlagValue("splitter", (String)null);
            if (string2 != null) {
                pattern = Pattern.compile(Pattern.quote(string2));
            }
            for (String string3 : pattern.split(string)) {
                OnlinePlayer onlinePlayer = this.getOnlinePlayer(bukkitCommandIssuer, string3, bl);
                if (onlinePlayer == null) continue;
                hashSet.add(onlinePlayer);
            }
            if (hashSet.isEmpty() && !bukkitCommandExecutionContext.hasFlag("allowempty")) {
                bukkitCommandIssuer.sendError(MinecraftMessageKeys.NO_PLAYER_FOUND_SERVER, "{search}", string);
                throw new InvalidCommandArgument(false);
            }
            return hashSet.toArray(new OnlinePlayer[hashSet.size()]);
        });
        this.registerIssuerAwareContext(World.class, bukkitCommandExecutionContext -> {
            World world;
            String string = bukkitCommandExecutionContext.getFirstArg();
            World world2 = world = string != null ? Bukkit.getWorld((String)string) : null;
            if (world != null) {
                bukkitCommandExecutionContext.popFirstArg();
            }
            if (world == null && bukkitCommandExecutionContext.getSender() instanceof Player) {
                world = ((Entity)bukkitCommandExecutionContext.getSender()).getWorld();
            }
            if (world == null) {
                throw new InvalidCommandArgument((MessageKeyProvider)MinecraftMessageKeys.INVALID_WORLD, new String[0]);
            }
            return world;
        });
        this.registerIssuerAwareContext(CommandSender.class, BukkitCommandExecutionContext::getSender);
        this.registerIssuerAwareContext(Player.class, bukkitCommandExecutionContext -> {
            boolean bl = bukkitCommandExecutionContext.isOptional();
            CommandSender commandSender = bukkitCommandExecutionContext.getSender();
            boolean bl2 = commandSender instanceof Player;
            if (!bukkitCommandExecutionContext.hasFlag("other")) {
                PlayerInventory playerInventory;
                Player player;
                Player player2 = player = bl2 ? (Player)commandSender : null;
                if (player == null && !bl) {
                    throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.NOT_ALLOWED_ON_CONSOLE, false, new String[0]);
                }
                PlayerInventory playerInventory2 = playerInventory = player != null ? player.getInventory() : null;
                if (playerInventory != null && bukkitCommandExecutionContext.hasFlag("itemheld") && !ACFBukkitUtil.isValidItem(playerInventory.getItem(playerInventory.getHeldItemSlot()))) {
                    throw new InvalidCommandArgument((MessageKeyProvider)MinecraftMessageKeys.YOU_MUST_BE_HOLDING_ITEM, false, new String[0]);
                }
                return player;
            }
            String string = bukkitCommandExecutionContext.popFirstArg();
            if (string == null && bl) {
                if (bukkitCommandExecutionContext.hasFlag("defaultself")) {
                    if (bl2) {
                        return (Player)commandSender;
                    }
                    throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.NOT_ALLOWED_ON_CONSOLE, false, new String[0]);
                }
                return null;
            }
            if (string == null) {
                throw new InvalidCommandArgument();
            }
            OnlinePlayer onlinePlayer = this.getOnlinePlayer((BukkitCommandIssuer)bukkitCommandExecutionContext.getIssuer(), string, false);
            return onlinePlayer.getPlayer();
        });
        this.registerContext(OfflinePlayer.class, bukkitCommandExecutionContext -> {
            OfflinePlayer offlinePlayer;
            String string = bukkitCommandExecutionContext.popFirstArg();
            if (bukkitCommandExecutionContext.hasFlag("uuid")) {
                UUID uUID;
                try {
                    uUID = UUID.fromString(string);
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    throw new InvalidCommandArgument((MessageKeyProvider)MinecraftMessageKeys.NO_PLAYER_FOUND_OFFLINE, "{search}", string);
                }
                offlinePlayer = Bukkit.getOfflinePlayer((UUID)uUID);
            } else {
                offlinePlayer = Bukkit.getOfflinePlayer((String)string);
            }
            if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()) {
                if (!bukkitCommandExecutionContext.hasFlag("uuid") && !bukkitCommandManager.isValidName(string)) {
                    throw new InvalidCommandArgument((MessageKeyProvider)MinecraftMessageKeys.IS_NOT_A_VALID_NAME, "{name}", string);
                }
                throw new InvalidCommandArgument((MessageKeyProvider)MinecraftMessageKeys.NO_PLAYER_FOUND_OFFLINE, "{search}", string);
            }
            return offlinePlayer;
        });
        this.registerContext(ChatColor.class, bukkitCommandExecutionContext -> {
            String string;
            String string2;
            String string3 = bukkitCommandExecutionContext.popFirstArg();
            Stream<ChatColor> stream = Stream.of(ChatColor.values());
            if (bukkitCommandExecutionContext.hasFlag("colorsonly")) {
                stream = stream.filter(chatColor -> chatColor.ordinal() <= 15);
            }
            if ((string2 = bukkitCommandExecutionContext.getFlagValue("filter", (String)null)) != null) {
                string = string2 = ACFUtil.simplifyString(string2);
                stream = stream.filter(chatColor -> string.equals(ACFUtil.simplifyString(chatColor.name())));
            }
            if ((string = (ChatColor)ACFUtil.simpleMatch(ChatColor.class, string3)) == null) {
                String string4 = stream.map(chatColor -> "<c2>" + ACFUtil.simplifyString(chatColor.name()) + "</c2>").collect(Collectors.joining("<c1>,</c1> "));
                throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.PLEASE_SPECIFY_ONE_OF, "{valid}", string4);
            }
            return string;
        });
        this.registerContext(Location.class, bukkitCommandExecutionContext -> {
            String string;
            String string2;
            String string3 = bukkitCommandExecutionContext.popFirstArg();
            CommandSender commandSender = bukkitCommandExecutionContext.getSender();
            String[] stringArray = ACFPatterns.COLON.split(string3, 2);
            if (stringArray.length == 0) {
                throw new InvalidCommandArgument(true);
            }
            if (stringArray.length < 2 && !(commandSender instanceof Player) && !(commandSender instanceof BlockCommandSender)) {
                throw new InvalidCommandArgument((MessageKeyProvider)MinecraftMessageKeys.LOCATION_PLEASE_SPECIFY_WORLD, new String[0]);
            }
            Location location = null;
            if (stringArray.length == 2) {
                string2 = stringArray[0];
                string = stringArray[1];
            } else if (commandSender instanceof Player) {
                location = ((Player)commandSender).getLocation();
                string2 = location.getWorld().getName();
                string = stringArray[0];
            } else if (commandSender instanceof BlockCommandSender) {
                location = ((BlockCommandSender)commandSender).getBlock().getLocation();
                string2 = location.getWorld().getName();
                string = stringArray[0];
            } else {
                throw new InvalidCommandArgument(true);
            }
            boolean bl = string.startsWith("~");
            stringArray = ACFPatterns.COMMA.split(bl ? string.substring(1) : string);
            if (stringArray.length < 3) {
                throw new InvalidCommandArgument((MessageKeyProvider)MinecraftMessageKeys.LOCATION_PLEASE_SPECIFY_XYZ, new String[0]);
            }
            Double d = ACFUtil.parseDouble(stringArray[0], bl ? Double.valueOf(0.0) : null);
            Double d2 = ACFUtil.parseDouble(stringArray[1], bl ? Double.valueOf(0.0) : null);
            Double d3 = ACFUtil.parseDouble(stringArray[2], bl ? Double.valueOf(0.0) : null);
            if (location != null && bl) {
                d = d + location.getX();
                d2 = d2 + location.getY();
                d3 = d3 + location.getZ();
            } else if (bl) {
                throw new InvalidCommandArgument((MessageKeyProvider)MinecraftMessageKeys.LOCATION_CONSOLE_NOT_RELATIVE, new String[0]);
            }
            if (d == null || d2 == null || d3 == null) {
                throw new InvalidCommandArgument((MessageKeyProvider)MinecraftMessageKeys.LOCATION_PLEASE_SPECIFY_XYZ, new String[0]);
            }
            World world = Bukkit.getWorld((String)string2);
            if (world == null) {
                throw new InvalidCommandArgument((MessageKeyProvider)MinecraftMessageKeys.INVALID_WORLD, new String[0]);
            }
            if (stringArray.length >= 5) {
                Float f = ACFUtil.parseFloat(stringArray[3]);
                Float f2 = ACFUtil.parseFloat(stringArray[4]);
                if (f2 == null || f == null) {
                    throw new InvalidCommandArgument((MessageKeyProvider)MinecraftMessageKeys.LOCATION_PLEASE_SPECIFY_XYZ, new String[0]);
                }
                return new Location(world, d.doubleValue(), d2.doubleValue(), d3.doubleValue(), f.floatValue(), f2.floatValue());
            }
            return new Location(world, d.doubleValue(), d2.doubleValue(), d3.doubleValue());
        });
        if (bukkitCommandManager.mcMinorVersion >= 12) {
            BukkitCommandContexts_1_12.register(this);
        }
    }

    @Contract(value="_,_,false -> !null")
    OnlinePlayer getOnlinePlayer(BukkitCommandIssuer bukkitCommandIssuer, String string, boolean bl) {
        Player player = ACFBukkitUtil.findPlayerSmart(bukkitCommandIssuer, string);
        if (player == null) {
            if (bl) {
                return null;
            }
            throw new InvalidCommandArgument(false);
        }
        return new OnlinePlayer(player);
    }
}

