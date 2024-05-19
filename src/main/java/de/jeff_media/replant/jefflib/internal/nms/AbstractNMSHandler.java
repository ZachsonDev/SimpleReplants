package de.jeff_media.replant.jefflib.internal.nms;

import com.mojang.authlib.GameProfile;
import de.jeff_media.replant.jefflib.ai.goal.CustomGoal;
import de.jeff_media.replant.jefflib.ai.goal.CustomGoalExecutor;
import de.jeff_media.replant.jefflib.ai.goal.PathfinderGoal;
import de.jeff_media.replant.jefflib.ai.navigation.JumpController;
import de.jeff_media.replant.jefflib.ai.navigation.LookController;
import de.jeff_media.replant.jefflib.ai.navigation.MoveController;
import de.jeff_media.replant.jefflib.ai.navigation.PathNavigation;
import de.jeff_media.replant.jefflib.data.Hologram;
import de.jeff_media.replant.jefflib.data.OfflinePlayerPersistentDataContainer;
import de.jeff_media.replant.jefflib.data.SerializedEntity;
import de.jeff_media.replant.jefflib.data.tuples.Pair;
import de.jeff_media.replant.jefflib.exceptions.UseApiNowException;
import de.jeff_media.replant.jefflib.internal.annotations.Internal;
import de.jeff_media.replant.jefflib.internal.nms.AbstractNMSBlockHandler;
import de.jeff_media.replant.jefflib.internal.nms.AbstractNMSMaterialHandler;
import de.jeff_media.replant.jefflib.internal.nms.BukkitUnsafe;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public interface AbstractNMSHandler {
    public AbstractNMSMaterialHandler getMaterialHandler();

    public AbstractNMSBlockHandler getBlockHandler();

    public void changeNMSEntityName(@NotNull Object var1, @NotNull String var2);

    public Object createHologram(@NotNull Location var1, @NotNull String var2, @NotNull Hologram.Type var3);

    public void showEntityToPlayer(@NotNull Object var1, @NotNull Player var2);

    public void hideEntityFromPlayer(@NotNull Object var1, @NotNull Player var2);

    public void sendPacket(@NotNull Player var1, @NotNull Object var2);

    public Pair<String, String> getBiomeName(@NotNull Location var1);

    public void playTotemAnimation(@NotNull Player var1);

    public void setHeadTexture(@NotNull Block var1, @NotNull GameProfile var2);

    default public String itemStackToJson(@NotNull ItemStack itemStack) {
        throw new UseApiNowException();
    }

    default public ItemStack itemStackFromJson(@NotNull String json) throws Exception {
        throw new UseApiNowException();
    }

    public void setFullTimeWithoutTimeSkipEvent(@NotNull World var1, long var2, boolean var4);

    public double[] getTps();

    public int getItemStackSizeInBytes(ItemStack var1) throws IOException;

    public String getDefaultWorldName();

    public PathfinderGoal createTemptGoal(Creature var1, Stream<Material> var2, double var3, boolean var5);

    public PathfinderGoal createAvoidEntityGoal(Creature var1, Predicate<LivingEntity> var2, float var3, double var4, double var6);

    public PathfinderGoal createMoveToBlockGoal(Creature var1, Set<Material> var2, double var3, int var5, int var6);

    public PathfinderGoal createMoveToBlockGoal(Creature var1, Predicate<Block> var2, double var3, int var5, int var6);

    public void addGoal(Mob var1, PathfinderGoal var2, int var3);

    public void removeGoal(Mob var1, PathfinderGoal var2);

    public void removeAllGoals(Mob var1);

    public void addTargetGoal(Mob var1, PathfinderGoal var2, int var3);

    public void removeTargetGoal(Mob var1, PathfinderGoal var2);

    public void removeAllTargetGoals(Mob var1);

    public boolean moveTo(Mob var1, double var2, double var4, double var6, double var8);

    public boolean isServerRunnning();

    public CustomGoalExecutor getCustomGoalExecutor(CustomGoal var1, Mob var2);

    @Nullable
    public Vector getRandomPos(Creature var1, int var2, int var3);

    @Nullable
    public Vector getRandomPosAway(Creature var1, int var2, int var3, Vector var4);

    @Nullable
    public Vector getRandomPosTowards(Creature var1, int var2, int var3, Vector var4, double var5);

    @NotNull
    public MoveController getMoveControl(Mob var1);

    @NotNull
    public JumpController getJumpControl(Mob var1);

    @NotNull
    public LookController getLookControl(Mob var1);

    @NotNull
    public PathNavigation getPathNavigation(Mob var1);

    @Nullable
    public Advancement loadVolatileAdvancement(NamespacedKey var1, String var2);

    @NotNull
    public BukkitUnsafe getUnsafe();

    public String serializePdc(PersistentDataContainer var1);

    public void deserializePdc(String var1, PersistentDataContainer var2) throws Exception;

    public void respawnPlayer(Player var1);

    public SerializedEntity serialize(Entity var1);

    public void applyNbt(Entity var1, String var2);

    public OfflinePlayerPersistentDataContainer getPDCFromDatFile(File var1) throws IOException;

    public void updatePdcInDatFile(OfflinePlayerPersistentDataContainer var1) throws IOException;
}

