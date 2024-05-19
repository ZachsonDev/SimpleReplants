package de.jeff_media.replant.listeners;

import de.jeff_media.replant.Main;
import de.jeff_media.replant.jefflib.BlockUtils;
import de.jeff_media.replant.utils.SaplingUtils;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class TwerkListener
implements Listener {
    private static final Main main = Main.getInstance();

    @EventHandler
    public void onTwerk(PlayerToggleSneakEvent playerToggleSneakEvent) {
        if (!main.getConfig().getBoolean("tree-twerking")) {
            return;
        }
        Player player = playerToggleSneakEvent.getPlayer();
        List<Block> list = BlockUtils.getBlocksInRadius(player.getLocation(), main.getConfig().getInt("twerk-radius"), BlockUtils.RadiusType.SPHERE, block -> SaplingUtils.isSapling(block.getType()));
    }
}

