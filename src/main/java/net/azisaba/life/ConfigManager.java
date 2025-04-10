package net.azisaba.life;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ConfigManager {

    private final CraftGUI plugin;
    private FileConfiguration itemsConfig;

    public ConfigManager(CraftGUI plugin) {
        this.plugin = plugin;
    }

    public void updateConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        File oldFolder = new File(plugin.getDataFolder(), "oldConfig");

        if (!oldFolder.exists()) {
            oldFolder.mkdir();
        }
        String oldFileName = "config-v" + plugin.getConfig().getString("configVersion", "1.0") + ".yml";
        Path oldPath = Paths.get(plugin.getDataFolder().getPath(), "oldConfig", oldFileName);
        try {
            Files.move(configFile.toPath(), oldPath, StandardCopyOption.REPLACE_EXISTING);
            plugin.getLogger().info("config.ymlに新しいバージョンが見つかったため、config.ymlを更新しています...");
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.reloadConfig();
        plugin.getLogger().info("config.ymlを更新しました");
    }

    public  FileConfiguration getItemsConfig() {
        return itemsConfig;
    }

    public void loadItemsConfig() {
        File itemsFile = new File(plugin.getDataFolder(), "items.yml");
        if (!itemsFile.exists()) {
            plugin.saveResource("items.yml", false);
        }
        itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);
    }

    public void saveItemsConfig() {
        try {
            itemsConfig.save(new File(plugin.getDataFolder(), "items.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadItemsConfig() {
        loadItemsConfig();
    }
}
