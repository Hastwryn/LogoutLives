package net.sand.logoutlives.listeners;

import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class EntityTarget implements Listener {

    @EventHandler
    public void onEntityTarget(EntityTargetEvent e) {
        // Cancel any targeting where the target is a logout villager
        if (e.getTarget() instanceof Villager villager) {
            if (villager.hasMetadata("LogoutVillager")) {
                e.setCancelled(true);
            }
        }

        // Also cancel if the entity itself is a logout villager (extra safety)
        if (e.getEntity() instanceof Villager villager) {
            if (villager.hasMetadata("LogoutVillager")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityTargetLiving(EntityTargetLivingEntityEvent e) {
        // Specifically handle living entity targeting for extra coverage
        if (e.getTarget() instanceof Villager villager) {
            if (villager.hasMetadata("LogoutVillager")) {
                e.setCancelled(true);
            }
        }

        if (e.getEntity() instanceof Villager villager) {
            if (villager.hasMetadata("LogoutVillager")) {
                e.setCancelled(true);
            }
        }
    }
}
