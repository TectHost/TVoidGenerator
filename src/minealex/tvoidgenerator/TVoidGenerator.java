package minealex.tvoidgenerator;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("deprecation")
public class TVoidGenerator extends JavaPlugin {
    private FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = YamlConfiguration.loadConfiguration(getResource("config.yml"));
        getCommand("tvoidgenerator").setExecutor(this);
        getLogger().info("El plugin TVoidGenerator ha sido habilitado.");
    }

    @Override
    public void onDisable() {
        getLogger().info("El plugin TVoidGenerator ha sido deshabilitado.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tvoidgenerator")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("generate")) {
                if (sender.hasPermission("tvoidgenerator.generate")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.generate-help")));
                    return true;
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.no-permission")));
                    return true;
                }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("generate")) {
                if (sender.hasPermission("tvoidgenerator.generate")) {
                    String nombreMundo = args[1];
                    generarMundoConBedrock(nombreMundo);
                    String successMsg = ChatColor.translateAlternateColorCodes('&', config.getString("messages.generate-success")).replace("{world}", nombreMundo);
                    sender.sendMessage(successMsg);
                    return true;
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.no-permission")));
                    return true;
                }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("tp")) {
                if (sender.hasPermission("tvoidgenerator.tp")) {
                    String nombreMundo = args[1];
                    teletransportarse(sender, nombreMundo);
                    return true;
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.no-permission")));
                    return true;
                }
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("tvoidgenerator.reload")) {
                    reloadConfig();
                    config = getConfig();
                    String reloadMsg = ChatColor.translateAlternateColorCodes('&', config.getString("messages.reload-success"));
                    sender.sendMessage(reloadMsg);
                    return true;
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.no-permission")));
                    return true;
                }
            }
        }
        return false;
    }

    public void generarMundoConBedrock(String nombreMundo) {
        WorldCreator creator = new WorldCreator(nombreMundo);
        creator.environment(World.Environment.NORMAL);
        creator.generateStructures(false);
        creator.generator(new CustomChunkGenerator());
        World mundo = Bukkit.getServer().createWorld(creator);
        mundo.setSpawnLocation(0, 64, 0); // Establecer la ubicación de aparición del mundo
    }

    public void teletransportarse(CommandSender sender, String nombreMundo) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            World mundo = findWorld(nombreMundo);
            if (mundo != null) {
                player.teleport(mundo.getSpawnLocation());
                String successMsg = ChatColor.translateAlternateColorCodes('&', config.getString("messages.tp-success")).replace("{world}", nombreMundo);
                player.sendMessage(successMsg);
            } else {
                String notFoundMsg = ChatColor.translateAlternateColorCodes('&', config.getString("messages.tp-not-found")).replace("{world}", nombreMundo);
                sender.sendMessage(notFoundMsg);
            }
        } else {
            String notPlayerMsg = ChatColor.translateAlternateColorCodes('&', config.getString("messages.tp-not-player"));
            sender.sendMessage(notPlayerMsg);
        }
    }

    public World findWorld(String nombreMundo) {
        for (World world : Bukkit.getWorlds()) {
            if (world.getName().equalsIgnoreCase(nombreMundo)) {
                return world;
            }
        }
        return null;
    }

    public class CustomChunkGenerator extends ChunkGenerator {
        @Override
        public byte[] generate(World world, Random random, int x, int z) {
            byte[] result = new byte[32768];
            if (x == 0 && z == 0) {
                // Generar un bloque de bedrock en las coordenadas 0, 60, 0
                result[getBlockIndex(x, 60, z)] = (byte) Material.BEDROCK.getId();
            }
            return result;
        }

        private int getBlockIndex(int x, int y, int z) {
            return (x * 16 + z) * 128 + y;
        }
    }
}
