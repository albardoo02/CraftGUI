package net.azisaba.life;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtil {

    private final CraftGUI plugin;
    private final LanguageManager languageManager;
    private final String prefix;

    public MessageUtil(CraftGUI plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
        String prefixConfig = plugin.getConfig().getString("Prefix");
        if (prefixConfig == null) {
            this.prefix = "§7[§aCraftGUI§7]§r ";
        } else {
            this.prefix = ChatColor.translateAlternateColorCodes('&', prefixConfig) + " ";
        }
    }

    public void sendMessage(CommandSender sender, String key, Object... args) {
        String message = languageManager.getMessage((sender instanceof Player) ? (Player) sender : null, key, args);
        if (message != null && !message.isEmpty()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
        }
    }
}
