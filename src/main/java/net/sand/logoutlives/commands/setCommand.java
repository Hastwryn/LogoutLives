package net.sand.logoutlives.commands;

import net.sand.logoutlives.LogoutLives;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class setCommand implements CommandExecutor {

    private final LogoutLives plugin = LogoutLives.get();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /set <dropsInventory|invulnerable|chunksToLoad|inventoryPeeking|enableNpcAi|enableTitle|mainTitle|lightning|lightningDamage|enableSound> <value>");
            return true;
        }

        String key = args[0].toLowerCase();
        String value = args[1];

        switch (key) {
            case "dropsinventory":
                handleBooleanSetting(sender, "dropsInventory", value);
                return true;

            case "invulnerable":
                handleBooleanSetting(sender, "invulnerable", value);
                return true;

            case "chunksload":
            case "chunkstoload":
                try {
                    int chunks = Integer.parseInt(value);
                    if (chunks < 1) chunks = 1;
                    plugin.getConfig().set("chunksToLoad", chunks);
                    plugin.saveConfig();
                    plugin.reloadConfig();
                    sender.sendMessage(ChatColor.GREEN + "✔ chunksToLoad set to " + ChatColor.AQUA + chunks);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Please enter a valid number for chunksToLoad.");
                }
                return true;

            case "inventorypeeking":
                handleBooleanSetting(sender, "inventoryPeeking", value);
                return true;

            case "enablenpcai":
                handleBooleanSetting(sender, "enableNpcAi", value);
                return true;

            case "canopendoors":
                handleBooleanSetting(sender, "canOpenDoors", value);
                return true;

            // NEW NPC DEATH SETTINGS
            case "enabletitle":
                handleBooleanSetting(sender, "enableTitle", value);
                return true;

            case "maintitle":
                plugin.getConfig().set("mainTitle", value);
                plugin.saveConfig();
                plugin.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "✔ mainTitle set to " + ChatColor.AQUA + value);
                return true;

            case "lightning":
                handleBooleanSetting(sender, "lightning", value);
                return true;

            case "lightningdamage":
                if (value.equalsIgnoreCase("DAMAGE") || value.equalsIgnoreCase("NO DAMAGE")) {
                    plugin.getConfig().set("lightningDamage", value.toUpperCase());
                    plugin.saveConfig();
                    plugin.reloadConfig();
                    sender.sendMessage(ChatColor.GREEN + "✔ lightningDamage set to " + ChatColor.AQUA + value.toUpperCase());
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid value. Use DAMAGE or NO DAMAGE.");
                }
                return true;

            case "enablesound":
                handleBooleanSetting(sender, "enableSound", value);
                return true;

            default:
                sender.sendMessage(ChatColor.RED + "Unknown setting. Use one of: dropsInventory, invulnerable, chunksToLoad, inventoryPeeking, enableNpcAi, enableTitle, mainTitle, lightning, lightningDamage, enableSound");
                return true;
        }
    }



    private void handleBooleanSetting(CommandSender sender, String path, String value) {
        boolean boolValue;

        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on") || value.equalsIgnoreCase("enable")) {
            boolValue = true;
        } else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("off") || value.equalsIgnoreCase("disable")) {
            boolValue = false;
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid value. Use true/false or on/off.");
            return;
        }

        plugin.getConfig().set(path, boolValue);
        plugin.saveConfig();
        plugin.reloadConfig();

        sender.sendMessage(ChatColor.GREEN + "✔ " + path + " set to " + ChatColor.AQUA + boolValue);
    }
}
