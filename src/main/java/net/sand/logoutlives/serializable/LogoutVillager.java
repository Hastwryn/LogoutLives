package net.sand.logoutlives.serializable;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import net.sand.logoutlives.LogoutLives;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.Attribute;

import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import me.libraryaddict.disguise.disguisetypes.watchers.PlayerWatcher;
import org.bukkit.inventory.EquipmentSlot;

import me.libraryaddict.disguise.disguisetypes.watchers.PlayerWatcher;
import org.bukkit.potion.PotionEffectType;


public class LogoutVillager implements Serializable {

    private final String playerName;
    private final UUID playerUUID;
    private UUID villagerUUID;
    private Boolean dead;

    private String world;
    private double x, y, z;

    public LogoutVillager(String playerName, UUID playerUUID, Boolean dead) {
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.dead = dead;
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public UUID getVillagerUUID() {
        return villagerUUID;
    }

    public Boolean isDead() {
        return dead;
    }

    public void setDead(Boolean dead) {
        this.dead = dead;
    }

    public double getVillagerX() {
        return x;
    }

    public double getVillagerY() {
        return y;
    }

    public double getVillagerZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public void setVillagerLocation(Location loc) {
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.world = Objects.requireNonNull(loc.getWorld()).getName();
    }

    @Override
    public String toString() {
        return "LogoutVillager [playerName=" + playerName + ", dead=" + dead + "]";
    }

    public void create(Location loc, ItemStack helmet, ItemStack chest, ItemStack legs, ItemStack boots,
                       ItemStack mainHand, ItemStack offHand, Collection<PotionEffect> effects) {

        World w = loc.getWorld();
        if (w == null) return;

        Entity entity = w.spawnEntity(loc, EntityType.VILLAGER);
        if (!(entity instanceof Villager villager)) return;

        boolean enableNpcAi = LogoutLives.get().getConfig().getBoolean("enableNpcAi", false);

        // Basic villager setup
        villager.setCustomName(playerName);
        villager.setCustomNameVisible(true);
        villager.setPersistent(true);
        villager.setAI(true);
        villager.setAware(enableNpcAi);     // <-- only aware if AI is enabled
        villager.setGravity(true);
        villager.setInvulnerable(false);
        villager.setSilent(true);
        villager.setCollidable(true);



        this.villagerUUID = villager.getUniqueId();
        this.setVillagerLocation(villager.getLocation());

        // Schedule disguise and equipment setup
        Bukkit.getScheduler().runTaskLater(LogoutLives.get(), () -> {
            try {
                // 1️⃣ Create the player disguise
                PlayerDisguise disguise = new PlayerDisguise(playerName);

                // Apply skin from JSON
                File skinFile = new File(LogoutLives.get().getDataFolder(), "skins/" + playerUUID + ".json");
                if (skinFile.exists()) {
                    JsonObject json = JsonParser.parseReader(new FileReader(skinFile)).getAsJsonObject();
                    String value = json.has("value") ? json.get("value").getAsString() : null;
                    String signature = json.has("signature") ? json.get("signature").getAsString() : null;

                    if (value != null && signature != null) {
                        try {
                            var method = PlayerDisguise.class.getDeclaredMethod("setSkin", String.class, String.class);
                            method.setAccessible(true);
                            method.invoke(disguise, value, signature);
                        } catch (Exception ignored) {}
                    }
                }

                // 2️⃣ Mirror armor and hand items visually
                PlayerWatcher watcher = disguise.getWatcher();
                watcher.setHelmet(helmet);
                watcher.setChestplate(chest);
                watcher.setLeggings(legs);
                watcher.setBoots(boots);
                watcher.setItemInMainHand(mainHand);
                watcher.setItemInOffHand(offHand);

                // 3️⃣ Mirror potion effects visually (on the entity itself)
                if (effects != null) {
                    for (PotionEffect effect : effects) {
                        villager.addPotionEffect(effect);
                    }
                }

                // 4️⃣ Apply the disguise to make the villager look like the player
                DisguiseAPI.disguiseToAll(villager, disguise);

                // 5️⃣ Equip the villager itself so armor affects combat
                villager.getEquipment().setHelmet(helmet);
                villager.getEquipment().setChestplate(chest);
                villager.getEquipment().setLeggings(legs);
                villager.getEquipment().setBoots(boots);
                villager.getEquipment().setItemInMainHand(mainHand);
                villager.getEquipment().setItemInOffHand(offHand);

                // 6️⃣ Set base villager HP and absorption hearts for armor
                double baseHP = 20.0;
                villager.setHealth(baseHP);

                // Count armor pieces for absorption effect
                int armorPieces = 0;
                if (helmet != null) armorPieces++;
                if (chest != null) armorPieces++;
                if (legs != null) armorPieces++;
                if (boots != null) armorPieces++;

                if (armorPieces > 0) {
                    // Absorption level = armorPieces - 1 (1 extra heart per armor piece)
                    villager.addPotionEffect(new PotionEffect(
                            PotionEffectType.ABSORPTION,
                            Integer.MAX_VALUE,
                            armorPieces - 1,
                            false, false
                    ));
                }

                Bukkit.getLogger().info("Villager fully mirrored player: " + playerName + " with " + armorPieces + " armor pieces");

            } catch (Exception e) {
                Bukkit.getLogger().warning("Failed to mirror player " + playerName + ": " + e.getMessage());
            }
        }, 1L);

        // Mark villager for plugin logic
        villager.setMetadata("LogoutVillager", new FixedMetadataValue(LogoutLives.get(), true));
    }



}
