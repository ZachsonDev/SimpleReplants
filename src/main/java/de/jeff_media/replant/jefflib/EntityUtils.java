package de.jeff_media.replant.jefflib;

import de.jeff_media.replant.jefflib.BlockUtils;
import de.jeff_media.replant.jefflib.Comparators;
import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.ServerUtils;
import de.jeff_media.replant.jefflib.ai.goal.GoalSelector;
import de.jeff_media.replant.jefflib.ai.goal.TargetSelector;
import de.jeff_media.replant.jefflib.ai.navigation.Controls;
import de.jeff_media.replant.jefflib.ai.navigation.JumpController;
import de.jeff_media.replant.jefflib.ai.navigation.LookController;
import de.jeff_media.replant.jefflib.ai.navigation.MoveController;
import de.jeff_media.replant.jefflib.ai.navigation.PathNavigation;
import de.jeff_media.replant.jefflib.exceptions.NMSNotSupportedException;
import de.jeff_media.replant.jefflib.internal.annotations.NMS;
import de.jeff_media.replant.jefflib.internal.nms.AbstractNMSTranslationKeyProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EntityUtils {
    private static final double DEFAULT_GENERIC_MOVEMENT_SPEED = 0.25;

    @Nullable
    public static Player getClosestOtherPlayer(@NotNull Player player) {
        return player.getWorld().getPlayers().stream().filter(player2 -> player2 != player).min(new Comparators.EntityByDistanceComparator((Entity)player)).orElse(null);
    }

    public static double getMovementSpeed(@NotNull LivingEntity livingEntity) {
        AttributeInstance attributeInstance = livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (attributeInstance != null) {
            return attributeInstance.getValue();
        }
        return 0.25;
    }

    @Nullable
    public static Player getClosestPlayer(@NotNull Entity entity) {
        return EntityUtils.getClosestPlayer(entity.getLocation());
    }

    @Nullable
    public static Player getClosestPlayer(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null) {
            return null;
        }
        return world.getPlayers().stream().min(new Comparators.EntityByDistanceComparator(location)).orElse(null);
    }

    @Nullable
    public static Player getClosestPlayer(@NotNull Block block) {
        return EntityUtils.getClosestPlayer(BlockUtils.getCenter(block));
    }

    public static Collection<Entity> getEntities(Block block, Block block2) {
        World world = block.getWorld();
        if (!world.equals((Object)block2.getWorld())) {
            throw new IllegalArgumentException("Both locations must share the same world!");
        }
        BoundingBox boundingBox = BoundingBox.of((Block)block, (Block)block2);
        return world.getNearbyEntities(boundingBox);
    }

    public static Collection<? extends Entity> getEntities(Block block, Block block2, EntityType entityType) {
        return EntityUtils.getEntities(block, block2, entityType.getEntityClass());
    }

    public static Collection<? extends Entity> getEntities(Block block, Block block2, Class<? extends Entity> clazz) {
        World world = block.getWorld();
        if (!world.equals((Object)block2.getWorld())) {
            throw new IllegalArgumentException("Both locations must share the same world!");
        }
        BoundingBox boundingBox = BoundingBox.of((Block)block, (Block)block2);
        Collection collection = world.getEntitiesByClass(clazz);
        collection.removeIf(entity -> !boundingBox.contains(entity.getLocation().toVector()));
        return collection;
    }

    public static <E extends Entity> List<E> castEntityList(Iterable<? extends Entity> iterable, Class<? extends E> clazz) {
        ArrayList<Entity> arrayList = new ArrayList<Entity>();
        for (Entity entity : iterable) {
            if (!clazz.isInstance(entity)) continue;
            arrayList.add(entity);
        }
        return arrayList;
    }

    @NotNull
    public static <T extends Entity> Collection<T> getEntities(@NotNull Class<T> clazz) {
        ArrayList arrayList = new ArrayList();
        for (World world : Bukkit.getWorlds()) {
            arrayList.addAll(world.getEntitiesByClass(clazz));
        }
        return arrayList;
    }

    @Nullable
    public static Entity getEntityById(int n) {
        for (Entity entity : EntityUtils.getAllEntities()) {
            if (entity.getEntityId() != n) continue;
            return entity;
        }
        return null;
    }

    @NotNull
    public static Collection<Entity> getAllEntities() {
        ArrayList<Entity> arrayList = new ArrayList<Entity>();
        for (World world : Bukkit.getWorlds()) {
            arrayList.addAll(world.getEntities());
        }
        return arrayList;
    }

    @NotNull
    public static GoalSelector getGoalSelector(@NotNull Mob mob) {
        return GoalSelector.of(mob);
    }

    @NotNull
    public static TargetSelector getTargetSelector(@NotNull Mob mob) {
        return TargetSelector.of(mob);
    }

    @NMS
    @NotNull
    public static PathNavigation getNavigation(@NotNull Mob mob) {
        return JeffLib.getNMSHandler().getPathNavigation(mob);
    }

    @NMS
    @NotNull
    public static MoveController getMoveController(@NotNull Mob mob) {
        return JeffLib.getNMSHandler().getMoveControl(mob);
    }

    @NMS
    @NotNull
    public static JumpController getJumpController(@NotNull Mob mob) {
        return JeffLib.getNMSHandler().getJumpControl(mob);
    }

    @NMS
    @NotNull
    public static LookController getLookController(@NotNull Mob mob) {
        return JeffLib.getNMSHandler().getLookControl(mob);
    }

    @NMS
    @NotNull
    public static Controls getControls(@NotNull Mob mob) {
        return Controls.of(mob);
    }

    @NMS
    public static void respawnPlayer(@NotNull Player player) {
        JeffLib.getNMSHandler().respawnPlayer(player);
    }

    @NMS
    public static void playTotemAnimation(@NotNull Player player) {
        EntityUtils.playTotemAnimation(player, null);
    }

    @NMS
    public static void playTotemAnimation(@NotNull Player player, @Nullable Integer n) {
        ItemStack itemStack = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert (itemMeta != null);
        itemMeta.setCustomModelData(n);
        itemStack.setItemMeta(itemMeta);
        ItemStack itemStack2 = player.getInventory().getItemInMainHand();
        player.getInventory().setItemInMainHand(itemStack);
        JeffLib.getNMSHandler().playTotemAnimation(player);
        player.getInventory().setItemInMainHand(itemStack2);
    }

    @NMS
    @NotNull
    public static String getTranslationKey(@NotNull EntityType entityType) {
        if (ServerUtils.hasTranslationKeyProvider()) {
            return entityType.getTranslationKey();
        }
        if (JeffLib.getNMSHandler() instanceof AbstractNMSTranslationKeyProvider) {
            return ((AbstractNMSTranslationKeyProvider)((Object)JeffLib.getNMSHandler())).getTranslationKey(entityType);
        }
        throw new NMSNotSupportedException("This version of NMS does not support getting the translation key of an EntityType");
    }

    private EntityUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

