package tect.host;

import commands.CommandTabCompleter;
import commands.Commands;
import managers.ConfigManager;
import managers.MessagesManager;
import org.bukkit.plugin.java.JavaPlugin;
import utils.TranslateColors;

import java.util.Objects;

public final class TVoidGenerator extends JavaPlugin {
    private TranslateColors translateColors;
    private ConfigManager configManager;
    private MessagesManager messagesManager;

    @Override
    public void onEnable() {
        getLogger().info("Starting TVoidGenerator...");

        loadConfigFiles();
        loadCommands();
        initializeManagers();

        getLogger().info("TVoidGenerator Started!");
    }

    @Override
    public void onDisable() {
        getLogger().warning("Stopping TVoidGenerator!");
    }

    public void loadCommands() {
        Objects.requireNonNull(this.getCommand("tvoidgenerator")).setExecutor(new Commands(this));
        Objects.requireNonNull(this.getCommand("tvoidgenerator")).setTabCompleter(new CommandTabCompleter());
    }

    public void initializeManagers() {
        translateColors = new TranslateColors();
    }

    public void loadConfigFiles() {
        configManager = new ConfigManager(this);
        messagesManager = new MessagesManager(this);
    }

    public TranslateColors getTranslateColors() {
        return translateColors;
    }
    public ConfigManager getConfigManager() {
        return configManager;
    }
    public MessagesManager getMessagesManager() {
        return messagesManager;
    }
}
