package net.sand.logoutlives.pluginManagers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.bukkit.plugin.java.JavaPlugin;

public class PluginUtils {

    /**
     * Extract a bundled plugin jar from resources to a target location.
     */
    public static File extractPluginJar(JavaPlugin plugin, String resourcePath, File outFile) throws IOException {
        try (InputStream in = plugin.getResource(resourcePath)) { // Use main plugin instance
            if (in == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            Files.copy(in, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return outFile;
        }
    }
}
