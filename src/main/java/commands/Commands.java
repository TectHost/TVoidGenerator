package commands;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import tect.host.TVoidGenerator;
import utils.EmptyWorldGenerator;
import utils.FlatWorldGenerator;
import utils.SingleBiomeProvider;

import java.io.File;
import java.util.*;

public class Commands implements CommandExecutor {
    private final TVoidGenerator plugin;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public Commands(TVoidGenerator plugin) {
        this.plugin = plugin;
        initializeSubCommands();
    }

    private void initializeSubCommands() {
        subCommands.put("generate", this::generateWorld);
        subCommands.put("tp", this::teleportPlayer);
        subCommands.put("list", this::listWorlds);
        subCommands.put("import", this::importWorld);
        subCommands.put("reload", this::reloadConfig);
        subCommands.put("version", this::version);
    }

    private void listWorlds(String[] strings, CommandSender sender) {
        List<World> worlds = Bukkit.getWorlds();
        if (worlds.isEmpty()) {
            sender.sendMessage(translate(plugin.getMessagesManager().getListEmpty()));
            return;
        }
        sender.sendMessage(translate(plugin.getMessagesManager().getListHeader()));
        worlds.forEach(world -> sender.sendMessage("§7- " + world.getName()));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!sender.hasPermission("tvg.admin")) {
            sendNoPermissionMessage(sender);
            return false;
        }

        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand != null) {
            subCommand.execute(args, sender);
        } else {
            sender.sendMessage(translate(plugin.getMessagesManager().getUnknown()));
        }
        return true;
    }

    private void sendNoPermissionMessage(@NotNull CommandSender sender) {
        sender.sendMessage(translate(plugin.getMessagesManager().getNoPerm()));
    }

    private void sendHelpMessage(@NotNull CommandSender sender) {
        Arrays.stream(plugin.getMessagesManager().getHelpSubcommands())
                .forEach(message -> sender.sendMessage(translate(message)));
    }

    private void generateWorld(String @NotNull [] args, CommandSender sender) {
        if (args.length < 2) {
            sender.sendMessage(translate(plugin.getMessagesManager().getGenerateUsage()));
            return;
        }

        String worldName = args[1];
        String worldType = getArgValue(args, "--t", "--type").orElse("empty");
        Biome biome = getArgValue(args, "--b", "--biome").map(this::getBiomeByName).orElse(Biome.PLAINS);

        WorldCreator creator = new WorldCreator(worldName);
        switch (worldType) {
            case "flat" -> creator.generator(new FlatWorldGenerator());
            case "empty" -> creator.generator(new EmptyWorldGenerator());
            default -> {
                sender.sendMessage(translate(plugin.getMessagesManager().getInvalidType().replace("%type%", worldType)));
                return;
            }
        }

        creator.biomeProvider(new SingleBiomeProvider(biome));
        Optional.ofNullable(Bukkit.createWorld(creator))
                .ifPresentOrElse(
                        world -> {
                            if ("empty".equals(worldType)) world.getBlockAt(0, 50, 0).setType(Material.BEDROCK);
                            sender.sendMessage(translate(plugin.getMessagesManager().getGenerateSuccess().replace("%world%", worldName)));
                        },
                        () -> sender.sendMessage(translate(plugin.getMessagesManager().getGenerateError().replace("%world%", worldName)))
                );
    }

    private void teleportPlayer(String[] args, CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sendNoPermissionMessage(sender);
            return;
        }
        if (args.length != 2) {
            player.sendMessage(translate(plugin.getMessagesManager().getTeleportUsage()));
            return;
        }

        Optional.ofNullable(Bukkit.getWorld(args[1]))
                .ifPresentOrElse(
                        world -> {
                            player.teleport(new Location(world, 0, 55, 0));
                            player.sendMessage(translate(plugin.getMessagesManager().getTeleportSuccess().replace("%world%", args[1])));
                        },
                        () -> player.sendMessage(translate(plugin.getMessagesManager().getTeleportNotFound()))
                );
    }

    private void importWorld(String @NotNull [] args, CommandSender sender) {
        if (args.length != 2) {
            sender.sendMessage(translate(plugin.getMessagesManager().getImportUsage()));
            return;
        }

        String worldName = args[1];
        File worldFolder = new File(Bukkit.getServer().getWorldContainer(), worldName);

        if (!worldFolder.exists()) {
            sender.sendMessage(translate(plugin.getMessagesManager().getImportNotFound()));
            return;
        }

        Optional.ofNullable(Bukkit.getServer().createWorld(new WorldCreator(worldName)))
                .ifPresentOrElse(
                        world -> sender.sendMessage(translate(plugin.getMessagesManager().getImportSuccess().replace("%world%", worldName))),
                        () -> sender.sendMessage(translate(plugin.getMessagesManager().getImportError()))
                );
    }

    private void reloadConfig(String[] args, @NotNull CommandSender sender) {
        plugin.getConfigManager().reloadConfig();
        plugin.getMessagesManager().reloadConfig();
        sender.sendMessage(translate(plugin.getMessagesManager().getReloadMessage()));
    }

    private void version(String[] args, @NotNull CommandSender sender) {
        sender.sendMessage(translate(plugin.getMessagesManager().getVersionMessage().replace("%version%", plugin.getDescription().getVersion())));
    }

    private Biome getBiomeByName(@NotNull String biomeName) {
        return Optional.ofNullable(Registry.BIOME.get(NamespacedKey.minecraft(biomeName.toLowerCase()))).orElse(Biome.PLAINS);
    }

    private Optional<String> getArgValue(String @NotNull [] args, String... flags) {
        for (int i = 0; i < args.length - 1; i++) {
            for (String flag : flags) {
                if (args[i].equalsIgnoreCase(flag)) return Optional.of(args[i + 1]);
            }
        }
        return Optional.empty();
    }

    private Component translate(String message) {
        return plugin.getTranslateColors().translateColors(null, message);
    }

    @FunctionalInterface
    private interface SubCommand {
        void execute(String[] args, CommandSender sender);
    }
}
