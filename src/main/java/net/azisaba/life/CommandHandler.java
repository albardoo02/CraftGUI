package net.azisaba.life;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {

    private final CraftGUI plugin;
    private final MessageUtil messageUtil;
    private final ConfigManager configManager;

    public CommandHandler(CraftGUI plugin, MessageUtil messageUtil, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageUtil = messageUtil;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            messageUtil.sendMessage(sender, "");
        }
        Player player = (Player) sender;
        FileConfiguration config = plugin.getConfig();
        FileConfiguration itemsConfig = configManager.getItemsConfig();
        if (args.length == 0) {
            messageUtil.sendMessage(player, "help");
            player.sendMessage("configVersion:" + config.getString("configVersion"));
            player.sendMessage(itemsConfig.getString("test"));

        }
        return true;
    }

}
