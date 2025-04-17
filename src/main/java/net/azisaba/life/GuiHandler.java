package net.azisaba.life;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiHandler implements Listener {

    private final CraftGUI plugin;
    private final MessageUtil messageUtil;
    private final Map<Integer, Inventory> guiPages = new HashMap<>();
    private final FileConfiguration itemsConfig;

    public GuiHandler(CraftGUI plugin, MessageUtil messageUtil) {
        this.plugin = plugin;
        this.messageUtil = messageUtil;
        File file = new File(plugin.getDataFolder(), "items.yml");
        if (!file.exists()) {
            plugin.saveResource("items.yml", false);
        }
        itemsConfig = YamlConfiguration.loadConfiguration(file);
        createGuiPages();
    }

    public void openGui(Player player, int page) {
        Inventory inv = guiPages.get(page);
        if (inv != null) {
            player.openInventory(inv);
        } else {
            messageUtil.sendMessage(player, "&cそのページは存在しません");
        }

    }


    private void createGuiPages() {
        ConfigurationSection itemsSection = itemsConfig.getConfigurationSection("Items");
        if (itemsSection == null) return;

        for (String pageKey : itemsSection.getKeys(false)) {
            int pageNum = Integer.parseInt(pageKey.replace("page", ""));
            Inventory gui = Bukkit.createInventory(null, 54, "圧縮GUI - Page " + pageNum);

            ConfigurationSection page = itemsSection.getConfigurationSection(pageKey);
            for (String slotKey : page.getKeys(false)) {
                int slot = Integer.parseInt(slotKey.replace("slot", ""));
                ConfigurationSection config = page.getConfigurationSection(slotKey);

                boolean isMythic = config.getBoolean("IsMythicMobsItem");
                String name = config.getString("DisplayName");
                String loreKey = config.getString("Lore");
                Material mat = Material.matchMaterial(config.getString("Material"));

                ItemStack item = new ItemStack(mat);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(name);
                    meta.setLore(itemsConfig.getStringList("Lores." + loreKey));
                    meta.setLocalizedName(pageKey + "-" + slotKey);
                    item.setItemMeta(meta);
                }

                gui.setItem(slot, item);
            }

            // メニューバー
            if (pageNum > 1) {
                gui.setItem(45, navItem(Material.ARROW, "§a前のページ", pageNum - 1));
            }
            gui.setItem(49, navItem(Material.BARRIER, "§c閉じる", -1));
            if (itemsSection.contains("page" + (pageNum + 1))) {
                gui.setItem(53, navItem(Material.ARROW, "§a次のページ", pageNum + 1));
            }

            guiPages.put(pageNum, gui);
        }
    }

    private ItemStack navItem(Material material, String name, int targetPage) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLocalizedName("nav-" + targetPage);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (!e.getView().getTitle().startsWith("圧縮GUI")) return;

        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String loc = clicked.getItemMeta().getLocalizedName();
        if (loc == null) return;

        if (loc.startsWith("nav-")) {
            int page = Integer.parseInt(loc.replace("nav-", ""));
            if (page == -1) player.closeInventory();
            else {
                openGui(player, page);
            }
            return;
        }

        String[] parts = loc.split("-");
        if (parts.length != 2) return;

        ConfigurationSection slot = itemsConfig.getConfigurationSection("Items." + parts[0] + "." + parts[1]);
        if (slot == null) return;

        boolean isMythic = slot.getBoolean("IsMythicMobsItem");
        int needAmount = slot.getInt("NeedAmount");
        String giveId = slot.getString("GiveMythicMobsItemID");
        Material mat = Material.matchMaterial(slot.getString("Material"));

        int total = 0;
        List<ItemStack> matches = new ArrayList<>();

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;

            boolean match = false;
            if (isMythic) {
                if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer()
                        .has(new NamespacedKey(plugin, "MYTHIC_TYPE"), PersistentDataType.STRING)) {
                    match = item.getType() == mat;
                }
            } else {
                if (!item.hasItemMeta() || !item.getItemMeta().getPersistentDataContainer()
                        .has(new NamespacedKey(plugin, "MYTHIC_TYPE"), PersistentDataType.STRING)) {
                    match = item.getType() == mat;
                }
            }

            if (match) {
                total += item.getAmount();
                matches.add(item);
                if (total >= needAmount) break;
            }
        }

        if (total >= needAmount) {
            int remaining = needAmount;
            for (ItemStack item : matches) {
                int amt = item.getAmount();
                if (amt <= remaining) {
                    player.getInventory().removeItem(item);
                    remaining -= amt;
                } else {
                    item.setAmount(amt - remaining);
                    break;
                }
            }

            if (isMythic && giveId != null && !giveId.isEmpty()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm i give " + player.getName() + " " + giveId + " 1");
            } else {
                player.getInventory().addItem(new ItemStack(mat, 1));
            }

            player.sendMessage("§a圧縮に成功しました！");
        } else {
            player.sendMessage("§c必要なアイテムが足りません。");
        }
    }
}
