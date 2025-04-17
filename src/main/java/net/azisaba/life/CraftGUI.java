package net.azisaba.life;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Locale;

public class CraftGUI extends JavaPlugin {

    private ConfigManager configManager;
    private FileConfiguration itemsConfig;
    private LanguageManager languageManager;
    private MessageUtil messageUtil;
    private Locale defaultLocale;
    private GuiHandler guiHandler;

    @Override
    public void onEnable() {
        languageManager = new LanguageManager(this, defaultLocale);
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
        this.guiHandler = new GuiHandler(this, messageUtil);
        this.getServer().getPluginManager().registerEvents(guiHandler, this);
        this.getCommand("craftgui").setExecutor(new CommandHandler(this, messageUtil, configManager, languageManager, guiHandler));
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
