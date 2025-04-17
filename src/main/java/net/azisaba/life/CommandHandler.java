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
    private final LanguageManager languageManager;
    private final GuiHandler guiHandler;

    public CommandHandler(CraftGUI plugin, MessageUtil messageUtil, ConfigManager configManager, LanguageManager languageManager, GuiHandler guiHandler) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageUtil = messageUtil;
        this.languageManager = languageManager;
        this.guiHandler = guiHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            messageUtil.sendMessage(sender, "this command only use player");
        }
        Player player = (Player) sender;
        FileConfiguration config = plugin.getConfig();
        FileConfiguration itemsConfig = configManager.getItemsConfig();

        if (args.length == 0) {
            /*
            messageUtil.sendMessage(player, "help");
            player.sendMessage("configVersion:" + config.getString("configVersion"));
            player.sendMessage(itemsConfig.getString("test"));
            */
            guiHandler.openGui(player, 1);
            messageUtil.sendMessage(player, "&2CraftGUIを開きました");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("version")) {
             messageUtil.sendMessage(player, "version");
            }
            else if (args[0].equalsIgnoreCase("reload")) {
                if (player.hasPermission("craftgui.command.reload")) {
                    languageManager.reloadConfiguration();
                    configManager.reloadItemsConfig();
                    messageUtil.sendMessage(player, "reload");
                } else {
                    messageUtil.sendMessage(player, "noPerm");
                }
                return true;
            }
        }

        return true;
    }

}
