package net.sand.logoutlives.listeners;

import net.sand.logoutlives.LogoutLives;
import net.sand.logoutlives.serializable.LogoutVillager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

public class PlayerJoinServer implements Listener {

    private final LogoutLives plugin = LogoutLives.get();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Ensure the chunk is loaded (optional)
        player.getWorld().loadChunk(player.getLocation().getChunk());

        // Save the player's skin using the new Profile API
        savePlayerSkin(player);

        // Handle any logout villager logic
        handleLogoutVillager(player);


    }

    private void savePlayerSkin(Player player) {
        try {
            PlayerProfile profile = player.getPlayerProfile();
            PlayerTextures textures = profile.getTextures();

            URL skinUrl = textures.getSkin();
            boolean signed = textures.isSigned();

            if (skinUrl == null) {
                plugin.getLogger().info("No skin URL found for player " + player.getName());
                return;
            }

            // Prepare folder
            File skinFolder = new File(plugin.getDataFolder(), "skins");
            if (!skinFolder.exists()) {
                skinFolder.mkdirs();
            }

            // Prepare file per player UUID
            UUID uuid = player.getUniqueId();
            File skinFile = new File(skinFolder, uuid.toString() + ".json");

            // Write JSON data
            try (FileWriter writer = new FileWriter(skinFile)) {
                writer.write("{\n");
                writer.write("  \"skinUrl\": \"" + skinUrl.toString() + "\",\n");
                writer.write("  \"signed\": " + signed + "\n");
                writer.write("}\n");
            }

            plugin.getLogger().info("Saved skin data for player: " + player.getName());

        } catch (IOException e) {
            plugin.getLogger().warning("Failed saving skin data for player " + player.getName() + ": " + e.getMessage());
        } catch (Exception e) {
            plugin.getLogger().warning("Unexpected error saving skin data for player " + player.getName() + ": " + e.getMessage());
        }
    }

    private void handleLogoutVillager(Player player) {
        for (LogoutVillager lv : LogoutLives.villagersL.values()) {
            if (!player.getUniqueId().equals(lv.getPlayerUUID())) continue;

            Entity villagerEntity = plugin.getServer().getEntity(lv.getVillagerUUID());

            Location loc = new Location(
                    plugin.getServer().getWorld(lv.getWorld()),
                    lv.getVillagerX(),
                    lv.getVillagerY(),
                    lv.getVillagerZ()
            );

            if (lv.isDead()) {
                if (plugin.getConfig().getBoolean("dropsInventory")) {
                    player.getInventory().clear();
                }
                player.teleport(loc);
                plugin.getLogger().info(player.getDisplayName() + " died offline, now online");
                player.setHealth(0);
                LogoutLives.villagersL.remove(lv.getVillagerUUID());
                return;
            }

            // Remove the saved record and the entity if present
            LogoutLives.villagersL.remove(lv.getVillagerUUID());
            if (villagerEntity != null && villagerEntity.isValid()) {
                villagerEntity.remove();
                player.teleport(villagerEntity.getLocation());
            } else {
                player.teleport(loc);
            }

            // 🧼 Clear leftover potion effects that cause damage immunity
            player.removePotionEffect(org.bukkit.potion.PotionEffectType.ABSORPTION);
            player.removePotionEffect(PotionEffectType.RESISTANCE);
            player.removePotionEffect(org.bukkit.potion.PotionEffectType.FIRE_RESISTANCE);

            plugin.getLogger().info("Removed LogoutVillager for player: " + lv.getPlayerName());
            return;
        }

        plugin.getLogger().info("No logout villager found for player: " + player.getDisplayName());
    }

}
