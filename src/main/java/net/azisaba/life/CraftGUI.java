package net.azisaba.life;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class CraftGUI extends JavaPlugin {

    private ConfigManager configManager;
    private FileConfiguration itemsConfig;
    private LanguageManager languageManager;
    private MessageUtil messageUtil;

    @Override
    public void onEnable() {
        languageManager = new LanguageManager(this);
        messageUtil = new MessageUtil(this, languageManager);
        configManager = new ConfigManager(this);

        this.saveDefaultConfig();
        configManager.loadItemsConfig();

        FileConfiguration config = getConfig();
        String currentVersion = config.getString("configVersion", "1.0");
        String CONFIG_VERSION = "1.0";
        if (!currentVersion.equalsIgnoreCase(CONFIG_VERSION)) {
            configManager.updateConfig();
        } else {
            this.getLogger().info("Configファイルは最新です");
        }
        File itemsFile = new File(getDataFolder(), "items.yml");
        if (!itemsFile.exists()) {
            saveResource("items.yml", false);
        }
        itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);

        this.getLogger().info("CraftGUI has been enabled");
        this.getCommand("craftgui").setExecutor(new CommandHandler(this, messageUtil, configManager));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /*
    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public MessageUtil getMessageUtil() {
        return messageUtil;
    }

    public ConfiManager getConfigManager() {
        return configManager;
    }
    */
}
