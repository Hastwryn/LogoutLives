package net.sand.logoutlives.listeners;

import net.sand.logoutlives.LogoutLives;
import net.sand.logoutlives.util.SaveFilesLL;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.sand.logoutlives.serializable.LogoutVillager;

import java.io.IOException;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerQuitServer implements Listener {

    // Player Logout
    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        // Don't spawn a villager if player is in Creative or Spectator
        if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) return;

        LogoutVillager lv = new LogoutVillager(p.getName(), p.getUniqueId(), false);

        try {
            SaveFilesLL.saveInventory(p);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Gather inventory + potion effects
        ItemStack helmet = p.getInventory().getHelmet();
        ItemStack chest = p.getInventory().getChestplate();
        ItemStack legs = p.getInventory().getLeggings();
        ItemStack boots = p.getInventory().getBoots();
        ItemStack mainHand = p.getInventory().getItemInMainHand();
        ItemStack offHand = p.getInventory().getItemInOffHand();
        Collection<PotionEffect> effects = p.getActivePotionEffects();

        Bukkit.getScheduler().runTaskLater(LogoutLives.get(), () -> {
            lv.create(p.getLocation(), helmet, chest, legs, boots, mainHand, offHand, effects);
            if (lv.getVillagerUUID() != null) {
                LogoutLives.villagersL.put(lv.getVillagerUUID(), lv);
            }
        }, 1L);
    }
}
