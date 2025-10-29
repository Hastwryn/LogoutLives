package net.sand.logoutlives.pluginManagers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class PluginLoader {

    /**
     * Loads a version-specific dependency plugin from resources/libs based on the server's API version.
     *
     * @param plugin The main plugin instance (this)
     */
    public static void loadVersionedPlugin(JavaPlugin plugin) {
        PluginManager pm = Bukkit.getPluginManager();

        // Check if dependency is already loaded (optional)
        Plugin dependency = pm.getPlugin("LibsDisguises");

        // Get the server/plugin API version
        String apiVersion = plugin.getDescription().getAPIVersion();
        if (apiVersion == null) apiVersion = "default"; // fallback if api-version not set

        // Construct jar path: resources/libs/DependencyName-<apiVersion>.jar
        String jarName = "libs/LibsDisguises-" + apiVersion + ".jar";

        // Extract the jar to the server plugins folder
        File pluginsFolder = new File(plugin.getServer().getWorldContainer(), "plugins");
        File outFile = new File(pluginsFolder, new File(jarName).getName());

        try {
            PluginUtils.extractPluginJar(plugin, jarName, outFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to extract " + jarName);
            e.printStackTrace();
            return;
        }

        // Load and enable the plugin dynamically
        try {
            Plugin loaded = pm.loadPlugin(outFile);
            loaded.onLoad(); // optional
            pm.enablePlugin(loaded);
            plugin.getLogger().info("Loaded version-specific plugin: " + loaded.getName());
        } catch (InvalidPluginException | InvalidDescriptionException e) {
            plugin.getLogger().severe("Failed to load plugin " + outFile.getName());
            e.printStackTrace();
        }
    }
}
