package de.jeff_media.replant.nbt;

import de.jeff_media.replant.Main;
import de.jeff_media.replant.config.Messages;
import de.jeff_media.replant.nbt.PlayerSettings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerManager {
    private final Main main = Main.getInstance();

    public boolean hasTreesEnabled(Player player) {
        return PlayerSettings.hasEnabled(player, PlayerSettings.TREE_REPLANTING);
    }

    public boolean hasCropsEnabled(Player player) {
        return PlayerSettings.hasEnabled(player, PlayerSettings.CROP_REPLANTING);
    }

    public boolean toggleCrops(Player player) {
        boolean bl = !this.hasCropsEnabled(player);
        PlayerSettings.setEnabled(player, PlayerSettings.CROP_REPLANTING, bl);
        if (bl) {
            Messages.sendMessage((CommandSender)player, Messages.REPLANT_CROPS_ENABLED);
        } else {
            Messages.sendMessage((CommandSender)player, Messages.REPLANT_CROPS_DISABLED);
        }
        return bl;
    }
}

