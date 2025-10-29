package net.sand.logoutlives.util;

import net.sand.logoutlives.LogoutLives;
import net.sand.logoutlives.serializable.LogoutVillager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.entity.Entity;

public class TickChecker {

    public static void scheduleTimer(Plugin plugin, final World world) {
        AtomicInteger timer = new AtomicInteger(100);

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (timer.get() == 0) {
                for (LogoutVillager logoutL : LogoutLives.villagersL.values()) {
                    if (logoutL.isDead()) continue; // Skip dead villagers

                    Entity entity = plugin.getServer().getEntity(logoutL.getVillagerUUID());
                    Location loc;

                    if (entity instanceof Villager villager) {
                        // If it's a Villager, update its location
                        loc = villager.getLocation();
                        logoutL.setVillagerLocation(loc);
                        // Optional: log info
                        // LogoutLives.get().getLogger().info(logoutL.getPlayerName() + ": Villager found at " + loc);
                    } else {
                        // Entity is either null or not a Villager
                        World villagerWorld = world; // fallback world
                        if (entity != null) villagerWorld = entity.getWorld();
                        loc = new Location(villagerWorld, logoutL.getVillagerX(), logoutL.getVillagerY(), logoutL.getVillagerZ());

                        // Load surrounding chunks
                        loadChunk(loc);

                        // Optional: log warning
                        // LogoutLives.get().getLogger().warning(logoutL.getPlayerName() + ": Villager not found. Loading chunk at " + loc);
                    }
                }
                timer.set(100);
            } else {
                timer.getAndDecrement();
            }
        }, 1L, 1L);
    }


	public static void loadChunk(Location loc) {
		int chunksToLoad = (int) LogoutLives.get().getConfig().get("chunksToLoad");
		Location nloc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());

		if (chunksToLoad < 0) {
			chunksToLoad = 0;
		} else {
			chunksToLoad--;
		}

		for (int x = -chunksToLoad; x <= chunksToLoad; x++) {
			for (int z = -chunksToLoad; z <= chunksToLoad; z++) {
				nloc.setX(loc.getX() + (x * 16));
				nloc.setZ(loc.getZ()  + (z * 16));
				nloc.getChunk().load();
				//LogoutLives.get().getLogger().info("Pintado-> X: " + nloc.getX() + " Z:" + nloc.getZ());
			}
		}

	}

	public static void pintarChunk(Location loc) {
		for (int x = 0; x <= 15; x++) {
			for (int z = 0; z <= 15; z++) {
				loc.getChunk().getBlock(x, (int) loc.getY()-1, z).setType(Material.EMERALD_BLOCK);
			}
		}
	}
}
