package net.azisaba.life;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LanguageManager {

    private final CraftGUI plugin;
    private final Map<Locale, YamlConfiguration> languageFiles = new HashMap<>();
    private final Locale defaultLocale = Locale.JAPAN;
    private final String languageDirectory;
    private final MessageFormatter messageFormatter = new MessageFormatter();

    public LanguageManager(CraftGUI plugin) {
        this.plugin = plugin;
        this.languageDirectory = plugin.getConfig().getString("language-directory", "lang");
        loadLanguageFiles();
        registerPlaceholders();
    }

    private void loadLanguageFiles() {
        File langDir = new File(plugin.getDataFolder(), languageDirectory);
        if (!langDir.exists()) {
            langDir.mkdirs();
        }

        File defaultLangFile = new File(langDir, defaultLocale.toString().replace("_", "-") + ".yml");
        if (!defaultLangFile.exists()) {
            plugin.saveResource("lang/" + defaultLocale.toString().replace("_", "-") + ".yml", false);
        }
        languageFiles.put(defaultLocale, YamlConfiguration.loadConfiguration(defaultLangFile));

        File[] files = langDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                if (fileName.equals(defaultLocale.toString().replace("_", "-") + ".yml")) continue;
                String localeCode = fileName.substring(0, fileName.lastIndexOf(".")).replace("_", "-");
                plugin.getLogger().info("");
                try {
                    Locale locale = Locale.forLanguageTag(localeCode);
                    languageFiles.put(locale, YamlConfiguration.loadConfiguration(file));
                    plugin.getLogger().info(fileName + "を読み込みました");
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("不正なロケールコードのファイルが見つかりました: " + fileName);
                }
                plugin.getLogger().info("");
            }
        }
    }

    public String getMessage(Player player, String key, Object... args) {
        Locale locale = (player != null) ? getPlayerLocale(player) : defaultLocale;
        YamlConfiguration langFile = languageFiles.getOrDefault(locale, languageFiles.get(defaultLocale));
        String messageFormat = langFile.getString(key, "Could not find message key: " + key);
        return messageFormatter.format(messageFormat, player, args);
    }

    private Locale getPlayerLocale(Player player) {
        try {
            return Locale.forLanguageTag(player.getLocale().replace("_", "-"));
        } catch (Exception e) {
            return defaultLocale;
        }
    }

    private void registerPlaceholders() {
        messageFormatter.registerPlaceholder("player", (player, args) -> (player != null) ? player.getName() : "console");
    }

    public void reloadConfiguration() {
        plugin.reloadConfig();
        loadLanguageFiles();
    }
}