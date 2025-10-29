package net.sand.logoutlives.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class setCommandFiller implements TabCompleter {

    private static final List<String> SETTINGS = Arrays.asList(
            "dropsInventory",
            "invulnerable",
            "chunksToLoad",
            "inventoryPeeking",
            "enableNpcAi",
            "canOpenDoors",
            // NEW NPC DEATH SETTINGS
            "enableTitle",
            "mainTitle",
            "lightning",
            "lightningDamage",
            "enableSound"
    );

    private static final List<String> BOOLEAN_VALUES = Arrays.asList(
            "true", "false"
    );

    private static final List<String> LIGHTNING_DAMAGE_VALUES = Arrays.asList(
            "DAMAGE", "NO DAMAGE"
    );

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // First argument — suggest setting names
            for (String option : SETTINGS) {
                if (option.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(option);
                }
            }
            return completions;
        }

        if (args.length == 2) {
            String key = args[0].toLowerCase();

            if (key.equals("chunkstoload") || key.equals("chunksload")) {
                // Suggest some reasonable chunk radius values
                completions.addAll(Arrays.asList("1", "2", "3"));
            } else if (key.equals("lightningdamage")) {
                // Suggest DAMAGE / NO DAMAGE
                for (String val : LIGHTNING_DAMAGE_VALUES) {
                    if (val.toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(val);
                    }
                }
            } else if (BOOLEAN_VALUES.contains(args[1].toLowerCase()) || key.equals("enabletitle") || key.equals("lightning") || key.equals("enableSound") || key.equals("enablenpcai") || key.equals("canopendoors") || key.equals("dropsinventory") || key.equals("invulnerable") || key.equals("inventorypeeking")) {
                // Suggest boolean values for other boolean settings
                for (String val : BOOLEAN_VALUES) {
                    if (val.toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(val);
                    }
                }
            }
            // mainTitle is free text — don't suggest anything
            return completions;
        }

        return completions;
    }
}
