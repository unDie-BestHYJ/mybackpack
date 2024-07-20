package com.plugins.backpack;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class StorageListener implements Listener {
    private final StorageManager storageManager;
    private final FileConfiguration config;

    public StorageListener(StorageManager storageManager, JavaPlugin plugin) {
        this.storageManager = storageManager;
        this.config = plugin.getConfig();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getTitle().startsWith("Storage Page")) {
            int slot = event.getRawSlot();
            Player player = (Player) event.getWhoClicked();
            Storage storage = storageManager.getStorage(player);

            // 确保只有最下面一行不能点击
            if (slot >= inventory.getSize() - 9) {
                // 获取配置中的上一页和下一页图标
                Material previousPageIcon = getMaterialFromConfig("backpack.previous-page-icon", Material.ARROW);
                Material nextPageIcon = getMaterialFromConfig("backpack.next-page-icon", Material.ARROW);

                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.hasItemMeta()) {
                    ItemMeta meta = clickedItem.getItemMeta();
                    if (meta != null) {
                        String displayName = meta.getDisplayName();
                        if (Material.ARROW.equals(displayName) && storage.getCurrentPage() > 0) {
                            event.setCancelled(true); // 取消点击事件，避免物品被移动
                            player.openInventory(storage.getPage(storage.getCurrentPage() - 1));
                        } else if (Material.ARROW.equals(displayName) && storage.getCurrentPage() < storage.getMaxPages() - 1) {
                            event.setCancelled(true); // 取消点击事件，避免物品被移动
                            player.openInventory(storage.getPage(storage.getCurrentPage() + 1));
                        } else {
                            event.setCancelled(true); // 取消点击事件，避免物品被移动
                        }
                    }
                } else {
                    event.setCancelled(true); // 取消点击事件，避免物品被移动
                }
            } else {
                // 允许在其他位置进行点击
                event.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getTitle().startsWith("Storage Page")) {
            Player player = (Player) event.getPlayer();
            Storage storage = storageManager.getStorage(player);
            storage.save(); // 保存玩家的仓库数据
        }
    }

    private Material getMaterialFromConfig(String path, Material defaultMaterial) {
        String materialName = config.getString(path, defaultMaterial.name());
        try {
            return Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return defaultMaterial; // 如果配置的物品无效，则使用默认值
        }
    }
}
