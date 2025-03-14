package commands;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("generate");
            suggestions.add("tp");
            suggestions.add("list");
            suggestions.add("import");
            suggestions.add("reload");
            suggestions.add("version");
        } else if ("generate".equalsIgnoreCase(args[0])) {
            if (args.length == 2) {
                suggestions.add("<world name>");
            } else {
                boolean hasType = false;
                boolean hasBiome = false;
                for (String arg : args) {
                    if (arg.equalsIgnoreCase("--type") || arg.equalsIgnoreCase("-t")) {
                        hasType = true;
                    } else if (arg.equalsIgnoreCase("--biome") || arg.equalsIgnoreCase("-b")) {
                        hasBiome = true;
                    }
                }

                String lastArg = args[args.length - 1].toLowerCase();

                if (lastArg.equals("--type") || lastArg.equals("-t")) {
                    suggestions.add("flat");
                    suggestions.add("empty");
                } else if (lastArg.equals("--biome") || lastArg.equals("-b")) {
                    for (Biome biome : Registry.BIOME) {
                        NamespacedKey key = Registry.BIOME.getKey(biome);
                        suggestions.add(key.getKey());
                    }
                } else {
                    if (!hasType) {
                        suggestions.add("--type");
                        suggestions.add("-t");
                    }
                    if (!hasBiome) {
                        suggestions.add("--biome");
                        suggestions.add("-b");
                    }
                }
            }
        } else if ("tp".equalsIgnoreCase(args[0])) {
            if (args.length == 2) {
                Bukkit.getWorlds().forEach(world -> suggestions.add(world.getName()));
            }
        }

        String input = args[args.length - 1].toLowerCase();
        suggestions.removeIf(suggestion -> !suggestion.toLowerCase().startsWith(input));

        return suggestions;
    }
}
