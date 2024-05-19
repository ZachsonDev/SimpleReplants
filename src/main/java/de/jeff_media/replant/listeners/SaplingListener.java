package de.jeff_media.replant.listeners;

import de.jeff_media.replant.Main;
import de.jeff_media.replant.utils.SaplingUtils;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.plugin.Plugin;

public class SaplingListener
implements Listener {
    private final Main main = Main.getInstance();

    public SaplingListener() {
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this.main);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onSaplingSpawn(ItemSpawnEvent itemSpawnEvent) {
        Entity entity;
        Item item = itemSpawnEvent.getEntity();
        if (!SaplingUtils.isSapling(item)) {
            return;
        }
        UUID uUID = item.getThrower();
        Entity entity2 = entity = uUID == null ? null : Bukkit.getEntity((UUID)uUID);
        if (entity instanceof Player && !this.main.getConfig().getBoolean("plant-fallen-saplings-thrown-by-player")) {
            return;
        }
        if (!this.main.getCropConf().isSaplingEnabled(item.getItemStack().getType())) {
            return;
        }
        this.main.getSaplingManager().register(item);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onSaplingSpawn(BlockDropItemEvent blockDropItemEvent) {
        for (Item item : blockDropItemEvent.getItems()) {
            if (!SaplingUtils.isSapling(item) || SaplingUtils.isSapling(blockDropItemEvent.getBlockState().getType())) continue;
            this.main.getSaplingManager().register(item);
        }
    }
}

