package managers;

import org.bukkit.configuration.file.FileConfiguration;
import tect.host.TVoidGenerator;
import utils.ConfigFile;

public class MessagesManager {

    private ConfigFile messagesFile;
    private final TVoidGenerator plugin;

    private String unknown;
    private String[] helpSubcommands;
    private String generateUsage;
    private String generateSuccess;
    private String generateError;
    private String onlyPlayer;
    private String teleportUsage;
    private String teleportSuccess;
    private String teleportNotFound;
    private String listEmpty;
    private String listHeader;
    private String importUsage;
    private String importNotFound;
    private String importSuccess;
    private String importError;
    private String reloadMessage;
    private String versionMessage;
    private String invalidType;
    private String invalidArg;
    private String invalidBiome;
    private String noPerm;

    public MessagesManager(TVoidGenerator plugin){
        this.messagesFile = new ConfigFile(plugin.getConfigManager().getLangFile(), "lang", plugin);
        this.plugin = plugin;
        this.messagesFile.registerConfig();
        loadConfig();
        generateAdditionalFiles();
    }

    public void loadConfig(){
        FileConfiguration config = messagesFile.getConfig();

        unknown = config.getString("messages.unknown_command");
        helpSubcommands = config.getStringList("messages.help.subcommands").toArray(new String[0]);
        generateUsage = config.getString("messages.generate.usage");
        generateSuccess = config.getString("messages.generate.success");
        generateError = config.getString("messages.generate.error");
        onlyPlayer = config.getString("messages.only_player");
        teleportUsage = config.getString("messages.teleport.usage");
        teleportSuccess = config.getString("messages.teleport.success");
        teleportNotFound = config.getString("messages.teleport.not_found");
        listEmpty = config.getString("messages.list.empty");
        listHeader = config.getString("messages.list.header");
        importUsage = config.getString("messages.import.usage");
        importNotFound = config.getString("messages.import.not_found");
        importSuccess = config.getString("messages.import.success");
        importError = config.getString("messages.import.error");
        reloadMessage = config.getString("messages.reload");
        versionMessage = config.getString("messages.version");
        invalidType = config.getString("messages.invalid-type");
        invalidArg = config.getString("messages.invalid-arg");
        invalidBiome = config.getString("messages.invalid-biome");
        noPerm = config.getString("messages.no-perm");
    }

    public void reloadConfig(){
        this.messagesFile = new ConfigFile(plugin.getConfigManager().getLangFile(), "lang", plugin);
        messagesFile.reloadConfig();
        loadConfig();
    }

    public void generateAdditionalFiles() {
        createConfigFile("messages_es.yml");
        createConfigFile("messages_en.yml");
    }

    private void createConfigFile(String fileName) {
        ConfigFile configFile = new ConfigFile(fileName, "lang", plugin);
        configFile.registerConfig();
    }

    public String getNoPerm() {
        return noPerm;
    }
    public String getVersionMessage() {
        return versionMessage;
    }
    public String getInvalidType() {
        return invalidType;
    }
    public String getReloadMessage() { return reloadMessage; }
    public String getUnknown() { return unknown; }
    public String[] getHelpSubcommands() { return helpSubcommands; }
    public String getGenerateUsage() { return generateUsage; }
    public String getGenerateSuccess() { return generateSuccess; }
    public String getGenerateError() { return generateError; }
    public String getOnlyPlayer() { return onlyPlayer; }
    public String getTeleportUsage() { return teleportUsage; }
    public String getTeleportSuccess() { return teleportSuccess; }
    public String getTeleportNotFound() { return teleportNotFound; }
    public String getListEmpty() { return listEmpty; }
    public String getListHeader() { return listHeader; }
    public String getImportUsage() { return importUsage; }
    public String getImportNotFound() { return importNotFound; }
    public String getImportSuccess() { return importSuccess; }
    public String getImportError() { return importError; }
    public String getInvalidArg() {
        return invalidArg;
    }
    public String getInvalidBiome() {
        return invalidBiome;
    }
}