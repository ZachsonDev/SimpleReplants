package de.jeff_media.replant.handlers;

import de.jeff_media.replant.Main;
import de.jeff_media.replant.config.Config;
import de.jeff_media.replant.hooks.OfflineGrowthHandler;
import de.jeff_media.replant.utils.SaplingUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SaplingManager {
    private final Main main = Main.getInstance();
    private final ArrayList<UUID> saplings = new ArrayList();

    public SaplingManager() {
        new BukkitRunnable(){

            public void run() {
                if (!SaplingManager.this.main.getConfig().getBoolean("plant-fallen-saplings")) {
                    return;
                }
                int n = Config.getSaplingReplantDelayInTicks();
                for (Item item : SaplingManager.this.getAll()) {
                    if (item.getTicksLived() < n) continue;
                    SaplingManager.this.replantSapling(item);
                }
            }
        }.runTaskTimer((Plugin)this.main, 20L, 20L);
    }

    public ArrayList<Item> getAll() {
        ArrayList<Item> arrayList = new ArrayList<Item>();
        Iterator<UUID> iterator = this.saplings.iterator();
        while (iterator.hasNext()) {
            UUID uUID = iterator.next();
            Entity entity = Bukkit.getEntity((UUID)uUID);
            if (entity == null || entity.isDead() || entity.getType() != EntityType.DROPPED_ITEM) {
                iterator.remove();
                continue;
            }
            arrayList.add((Item)entity);
        }
        return arrayList;
    }

    public boolean isRegistered(Item item) {
        return this.saplings.contains(item.getUniqueId());
    }

    public void register(Item item) {
        this.saplings.add(item.getUniqueId());
    }

    public void remove(Item item) {
        ItemStack itemStack = item.getItemStack();
        int n = itemStack.getAmount();
        if (n == 1) {
            this.unregister(item);
            item.remove();
        } else {
            itemStack.setAmount(n - 1);
            item.setItemStack(itemStack);
        }
    }

    private void replantSapling(Item item) {
        if (!item.isOnGround()) {
            return;
        }
        Block block = item.getLocation().getBlock();
        if (block.getType() == Material.SOUL_SAND) {
            block = block.getRelative(BlockFace.UP);
        }
        if ((block = SaplingUtils.findValidSpot(block, item)) == null) {
            Main.debug("Could not find valid spot");
            this.unregister(item);
            return;
        }
        UUID uUID = item.getThrower();
        Player player = null;
        if (uUID != null && Bukkit.getEntity((UUID)uUID) instanceof Player) {
            player = Bukkit.getPlayer((UUID)uUID);
        }
        if (this.main.getConfig().getBoolean("use-worldguard") && player != null && !this.main.getWorldGuardHandler().canBuild(player, block)) {
            Main.debug("Sapling inside protected WorldGuard region.");
            this.unregister(item);
            return;
        }
        if (this.main.getConfig().getBoolean("call-block-place-event") && player != null) {
            BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(block, block.getState(), block.getRelative(BlockFace.DOWN), item.getItemStack(), player, true, EquipmentSlot.HAND);
            Bukkit.getPluginManager().callEvent((Event)blockPlaceEvent);
            if (blockPlaceEvent.isCancelled()) {
                Main.debug("BlockPlaceEvent cancelled.");
                this.unregister(item);
                return;
            }
        }
        this.remove(item);
        this.main.getSaplingParticleManager().spawnParticles(block);
        block.setType(item.getItemStack().getType());
        OfflineGrowthHandler.register(block);
    }

    public void unregister(Item item) {
        this.saplings.remove(item.getUniqueId());
    }
}

