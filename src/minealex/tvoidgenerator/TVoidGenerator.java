package minealex.tvoidgenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
        getCommand("tvoidgenerator").setTabCompleter(this);
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
                    generarMundoConBedrock("nuevoMundo");
                    sender.sendMessage(formatMessage("generate-success", "{world}", "nuevoMundo"));
                    return true;
                } else {
                    sender.sendMessage(formatMessage("no-permission"));
                    return true;
                }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("generate")) {
                if (sender.hasPermission("tvoidgenerator.generate")) {
                    String nombreMundo = args[1];
                    generarMundoConBedrock(nombreMundo);
                    sender.sendMessage(formatMessage("generate-success", "{world}", nombreMundo));
                    return true;
                } else {
                    sender.sendMessage(formatMessage("no-permission"));
                    return true;
                }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("tp")) {
                if (sender.hasPermission("tvoidgenerator.tp")) {
                    String nombreMundo = args[1];
                    teletransportarse(sender, nombreMundo);
                    return true;
                } else {
                    sender.sendMessage(formatMessage("no-permission"));
                    return true;
                }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("import")) {
                if (sender.hasPermission("tvoidgenerator.import")) {
                    String nombreMundo = args[1];
                    importarMundo(sender, nombreMundo); // Pasamos también el parámetro 'sender'
                    return true;
                } else {
                    sender.sendMessage(formatMessage("no-permission"));
                    return true;
                }
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                // ... código existente para recargar la configuración
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tvoidgenerator")) {
            List<String> completions = new ArrayList<>();

            if (args.length == 1) {
                completions.add("generate");
                completions.add("tp");
                completions.add("import");
                completions.add("reload");
            } else if (args.length == 2 && args[0].equalsIgnoreCase("generate")) {
                // Aquí puedes agregar lógica para autocompletar nombres de mundos disponibles, si lo deseas.
            } else if (args.length == 2 && args[0].equalsIgnoreCase("tp")) {
                // Aquí puedes agregar lógica para autocompletar nombres de mundos disponibles, si lo deseas.
            } else if (args.length == 2 && args[0].equalsIgnoreCase("import")) {
                // Aquí puedes agregar lógica para autocompletar nombres de mundos disponibles, si lo deseas.
            }

            return completions;
        }

        return Collections.emptyList();
    }

    public void generarMundoConBedrock(String nombreMundo) {
        WorldCreator creator = new WorldCreator(nombreMundo);
        creator.environment(World.Environment.NORMAL);
        creator.generateStructures(false);
        creator.generator(new CustomChunkGenerator());
        World mundo = Bukkit.getServer().createWorld(creator);
        mundo.setSpawnLocation(0, 2, 0); // Cambiar el punto de aparición del mundo a las coordenadas 0, 2, 0
    }

    public void teletransportarse(CommandSender sender, String nombreMundo) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            World mundo = findWorld(nombreMundo);

            // Si el mundo no está cargado, lo intentamos cargar
            if (mundo == null) {
                mundo = cargarMundo(nombreMundo);
                if (mundo == null) {
                    sender.sendMessage(formatMessage("tp-not-found", "{world}", nombreMundo));
                    return;
                }
            }

            // Teletransportarse al spawn del mundo (coordenadas 0, 2, 0)
            player.teleport(new Location(mundo, 0, 2, 0));
            sender.sendMessage(formatMessage("tp-success", "{world}", nombreMundo));
        } else {
            sender.sendMessage(formatMessage("tp-not-player"));
        }
    }

    public World findWorld(String nombreMundo) {
        for (World world : Bukkit.getWorlds()) {
            if (world.getName().equalsIgnoreCase(nombreMundo)) {
                return world;
            }
        }
        return null; // Agregamos este return statement en caso de que no se encuentre el mundo
    }

    public World cargarMundo(String nombreMundo) {
        World mundo = Bukkit.getWorld(nombreMundo);

        // Si el mundo no está cargado, lo intentamos cargar desde el disco
        if (mundo == null) {
            WorldCreator creator = new WorldCreator(nombreMundo);
            creator.environment(World.Environment.NORMAL);
            creator.generateStructures(false);
            creator.generator(new CustomChunkGenerator());
            mundo = Bukkit.getServer().createWorld(creator);
        }

        return mundo;
    }

    public void importarMundo(CommandSender sender, String nombreMundo) {
        File worldFolder = new File(Bukkit.getWorldContainer(), nombreMundo);
        if (worldFolder.exists()) {
            sender.sendMessage(formatMessage("import-exists", "{world}", nombreMundo));
            return;
        }

        File sourceWorldFolder = new File(getServer().getWorldContainer(), "worlds" + File.separator + nombreMundo);
        if (!sourceWorldFolder.exists()) {
            sender.sendMessage(formatMessage("import-not-found", "{world}", nombreMundo));
            return;
        }

        try {
            Bukkit.getServer().createWorld(new WorldCreator(nombreMundo));
            sender.sendMessage(formatMessage("import-success", "{world}", nombreMundo));
        } catch (Exception e) {
            sender.sendMessage(formatMessage("tp-not-found", "{world}", nombreMundo));
            e.printStackTrace();
        }
    }

    public String formatMessage(String key, String... replacements) {
        String message = config.getString("messages." + key, "&cInvalid message key: " + key);

        for (int i = 0; i < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    // Clase CustomChunkGenerator (sin cambios)
    public class CustomChunkGenerator extends ChunkGenerator {
        @Override
        public byte[] generate(World world, Random random, int x, int z) {
            byte[] result = new byte[32768];
            if (x == 0 && z == 0) {
                result[getBlockIndex(x, 0, z)] = (byte) Material.BEDROCK.getId(); // Cambiar las coordenadas del bloque de bedrock a 0, 0, 0
            }
            return result;
        }

        private int getBlockIndex(int x, int y, int z) {
            return (x * 16 + z) * 128 + y;
        }
    }
}
