package de.jeff_media.replant.listeners;

import de.jeff_media.replant.Main;
import de.jeff_media.replant.config.Config;
import de.jeff_media.replant.config.Permissions;
import de.jeff_media.replant.handlers.SeedManager;
import de.jeff_media.replant.hooks.OfflineGrowthHandler;
import de.jeff_media.replant.jefflib.EnumUtils;
import de.jeff_media.replant.jefflib.data.McVersion;
import de.jeff_media.replant.utils.ItemUtils;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CropListener
implements Listener {
    private static final Main main = Main.getInstance();
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();
    private static final Set<Material> DISABLED_CROPS = EnumUtils.getEnumsFromListAsSet(Material.class, Arrays.asList("TWISTING_VINES", "TWISTING_VINES_PLANT", "WEEPING_VINES", "WEEPING_VINES_PLANT"));
    private boolean isRightClickHarvest = false;

    public CropListener() {
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)main);
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onTrampleCrops(PlayerInteractEvent playerInteractEvent) {
        Player player = playerInteractEvent.getPlayer();
        if (playerInteractEvent.getAction() != Action.PHYSICAL) {
            return;
        }
        if (!main.getConfig().getBoolean("leather-armor-prevents-trampling")) {
            return;
        }
        if (playerInteractEvent.getClickedBlock() == null || playerInteractEvent.getClickedBlock().getType() != Material.FARMLAND) {
            return;
        }
        double d = 0.0;
        for (ItemStack itemStack : player.getInventory().getArmorContents()) {
            if (itemStack == null) continue;
            if (itemStack.getType().name().startsWith("LEATHER_")) {
                d += 0.25;
            }
            if (!main.getConfig().getBoolean("only-require-boots") || itemStack.getType() != Material.LEATHER_BOOTS) continue;
            d = 1.0;
            break;
        }
        if (random.nextDouble(0.0, 1.0) < d) {
            playerInteractEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onCropRightClick(PlayerInteractEvent playerInteractEvent) {
        if (!McVersion.current().isAtLeast(1, 17, 0)) {
            return;
        }
        if (playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = playerInteractEvent.getPlayer();
        if (!this.isApplicable(ClickType.RIGHT, player)) {
            return;
        }
        if (!Permissions.isAllowed((Permissible)player, "replant.use")) {
            return;
        }
        if (!main.getPlayerManager().hasCropsEnabled(player)) {
            return;
        }
        if (main.getConfig().getBoolean("dont-replant-when-harvesting-with-bonemeal") && this.hasBonemeal(player)) {
            return;
        }
        Block block = playerInteractEvent.getClickedBlock();
        BlockState blockState = block.getState();
        BlockData blockData = blockState.getBlockData();
        if (!(blockData instanceof Ageable)) {
            return;
        }
        if (DISABLED_CROPS.contains(block.getType())) {
            return;
        }
        Ageable ageable = (Ageable)blockData;
        if (ageable.getAge() < ageable.getMaximumAge()) {
            return;
        }
        this.isRightClickHarvest = true;
        boolean bl = player.breakBlock(block);
        player.swingMainHand();
        this.isRightClickHarvest = false;
        if (bl) {
            player.playSound(block.getLocation(), Sound.BLOCK_CROP_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    private boolean hasBonemeal(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        ItemStack itemStack2 = player.getInventory().getItemInOffHand();
        if (itemStack != null && itemStack.getType() == Material.BONE_MEAL) {
            return true;
        }
        return itemStack2 != null && itemStack2.getType() == Material.BONE_MEAL;
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onCropHarvestLowest(BlockDropItemEvent blockDropItemEvent) {
        if (main.getConfig().getString("replant-priority").equalsIgnoreCase("lowest")) {
            this.onCropHarvest(blockDropItemEvent);
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onCropHarvestLow(BlockDropItemEvent blockDropItemEvent) {
        if (main.getConfig().getString("replant-priority").equalsIgnoreCase("low")) {
            this.onCropHarvest(blockDropItemEvent);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onCropHarvestNormal(BlockDropItemEvent blockDropItemEvent) {
        if (main.getConfig().getString("replant-priority").equalsIgnoreCase("normal")) {
            this.onCropHarvest(blockDropItemEvent);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onCropHarvestHigh(BlockDropItemEvent blockDropItemEvent) {
        if (main.getConfig().getString("replant-priority").equalsIgnoreCase("high")) {
            this.onCropHarvest(blockDropItemEvent);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onCropHarvestHighest(BlockDropItemEvent blockDropItemEvent) {
        if (main.getConfig().getString("replant-priority").equalsIgnoreCase("highest")) {
            this.onCropHarvest(blockDropItemEvent);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onCropHarvestMonitor(BlockDropItemEvent blockDropItemEvent) {
        if (main.getConfig().getString("replant-priority").equalsIgnoreCase("monitor")) {
            this.onCropHarvest(blockDropItemEvent);
        }
    }

    private boolean isApplicable(ClickType clickType, Player player) {
        boolean bl;
        boolean bl2 = main.getConfig().getBoolean("crop-replant-requires-hoe");
        boolean bl3 = main.getConfig().getBoolean("crop-replant-requires-rightclick");
        boolean bl4 = main.getConfig().getBoolean("crop-replant-requires-both");
        boolean bl5 = ItemUtils.hasHoe(player);
        boolean bl6 = bl = clickType == ClickType.RIGHT;
        if (!bl3 && bl) {
            return false;
        }
        if (bl4 && bl3 && bl2) {
            return bl5 && bl;
        }
        if (bl2 && bl3) {
            return bl5 || bl;
        }
        if (bl2) {
            return bl5;
        }
        if (bl3) {
            return bl;
        }
        return !bl;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        if (blockBreakEvent.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (this.preventHarvestingWithoutHoe(blockBreakEvent.getBlock().getType(), blockBreakEvent.getPlayer().getInventory().getItemInMainHand())) {
            blockBreakEvent.setCancelled(true);
            return;
        }
    }

    public void onCropHarvest(BlockDropItemEvent blockDropItemEvent) {
        BlockFace blockFace;
        Directional directional;
        Player player;
        if (!this.isApplicable(this.isRightClickHarvest ? ClickType.RIGHT : ClickType.LEFT, player = blockDropItemEvent.getPlayer())) {
            return;
        }
        if (!Permissions.isAllowed((Permissible)player, "replant.use")) {
            return;
        }
        if (!main.getPlayerManager().hasCropsEnabled(player)) {
            return;
        }
        final Block block = blockDropItemEvent.getBlock();
        BlockState blockState = blockDropItemEvent.getBlockState();
        BlockData blockData = blockState.getBlockData();
        if (DISABLED_CROPS.contains(block.getType())) {
            return;
        }
        if (!(blockData instanceof Ageable)) {
            return;
        }
        if (!main.getCropConf().isCropEnabled(blockState.getType())) {
            return;
        }
        if (this.isSugarCaneButNotRoot(blockState)) {
            return;
        }
        if (blockData instanceof Directional) {
            directional = (Directional)blockData;
            blockFace = directional.getFacing();
        } else {
            blockFace = null;
        }
        directional = (Ageable)blockData;
        if (directional.getAge() < directional.getMaximumAge() && main.getConfig().getBoolean("crop-replant-only-fully-grown")) {
            return;
        }
        final Material material = blockState.getType();
        final Material material2 = SeedManager.getSeedFromCrop(material);
        if (!SeedManager.isValidGround(block, material)) {
            return;
        }
        Main.debug("To replant: " + material2);
        if (main.getConfig().getBoolean("crop-replant-costs") && !CropListener.removeSeed(material2, blockDropItemEvent.getItems(), player)) {
            return;
        }
        new BukkitRunnable(){

            public void run() {
                if (block.getType().isAir() && SeedManager.isValidGround(block, material)) {
                    BlockData blockData;
                    if (main.getConfig().getBoolean("call-blockplaceevent", true)) {
                        blockData = new BlockPlaceEvent(block, block.getState(), block.getRelative(BlockFace.DOWN), new ItemStack(material2), player, true, EquipmentSlot.HAND);
                        Bukkit.getPluginManager().callEvent((Event)blockData);
                    }
                    block.setType(SeedManager.getSeedToReplant(material));
                    blockData = block.getBlockData();
                    if (blockData instanceof Directional && blockFace != null) {
                        if (!block.getRelative(blockFace).getType().isAir()) {
                            ((Directional)blockData).setFacing(blockFace);
                            block.setBlockData(blockData);
                        } else {
                            block.setType(Material.AIR);
                            return;
                        }
                    }
                    OfflineGrowthHandler.register(block);
                    main.getCropParticleManager().spawnParticles(block);
                } else {
                    World world = block.getWorld();
                    Location location = block.getLocation();
                    ItemStack itemStack = new ItemStack(material2, 1);
                    if (itemStack.getType().isAir() || itemStack.getAmount() == 0) {
                        return;
                    }
                    ItemStack itemStack2 = itemStack.clone();
                    try {
                        world.dropItemNaturally(location, itemStack);
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
            }
        }.runTaskLater((Plugin)main, (long)Config.getCropReplantDelayInTicks());
    }

    private boolean preventHarvestingWithoutHoe(Material material, ItemStack itemStack) {
        if (!main.getConfig().getBoolean("prevent-harvesting-without-hoe")) {
            return false;
        }
        if (Tag.CROPS.isTagged((Keyed)material)) {
            return !ItemUtils.isHoe(itemStack);
        }
        return false;
    }

    private boolean isSugarCaneButNotRoot(BlockState blockState) {
        boolean bl = blockState.getType() == Material.SUGAR_CANE;
        boolean bl2 = blockState.getBlock().getRelative(BlockFace.DOWN).getType() == Material.SUGAR_CANE;
        return bl && bl2;
    }

    private static boolean removeSeed(Material material, List<Item> list, Player player) {
        Item item;
        Material material2 = SeedManager.getSeedToRemove(material);
        Iterator<Item> iterator = list.iterator();
        while (iterator.hasNext()) {
            item = iterator.next();
            ItemStack itemStack = item.getItemStack();
            if (itemStack.getType() != material2) continue;
            if (!main.getConfig().getBoolean("crop-replant-drops-seeds")) {
                iterator.remove();
                continue;
            }
            if (itemStack.getAmount() > 1) {
                itemStack.setAmount(itemStack.getAmount() - 1);
            } else {
                iterator.remove();
            }
            return true;
        }
        if (!main.getConfig().getBoolean("crop-replant-drops-seeds")) {
            return true;
        }
        item = player.getInventory();
        if (item.contains(material2)) {
            item.remove(new ItemStack(material2, 1));
        }
        return false;
    }
}

