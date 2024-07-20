package com.plugins.backpack;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Storage {
    private final JavaPlugin plugin;
    private final UUID owner;
    private final Map<Integer, Inventory> pages = new HashMap<>();
    private final int maxPages;
    private final int pageSize;
    private final Material previousPageIcon;
    private final Material nextPageIcon;
    private int currentPage = 0; // 当前页面索引

    // 构造函数，用于创建新的 Storage 对象
    public Storage(JavaPlugin plugin, Player player) {
        this.plugin = plugin;
        this.owner = player.getUniqueId();
        this.maxPages = plugin.getConfig().getInt("backpack.max-pages", 2);
        this.pageSize = plugin.getConfig().getInt("backpack.default-size", 45) + 9;
        this.previousPageIcon = getMaterialFromConfig("backpack.previous-page-icon", Material.ARROW);
        this.nextPageIcon = getMaterialFromConfig("backpack.next-page-icon", Material.ARROW);
        load();
    }

    // 构造函数，用于从配置文件加载 Storage 对象
    public Storage(JavaPlugin plugin, FileConfiguration config, UUID owner) {
        this.plugin = plugin;
        this.owner = owner;
        this.maxPages = plugin.getConfig().getInt("backpack.max-pages", 2);
        this.pageSize = plugin.getConfig().getInt("backpack.default-size", 45) + 9;
        this.previousPageIcon = getMaterialFromConfig("backpack.previous-page-icon", Material.ARROW);
        this.nextPageIcon = getMaterialFromConfig("backpack.next-page-icon", Material.ARROW);

        // 创建和加载每一页的 Inventory
        for (int i = 0; i < maxPages; i++) {
            Inventory inventory = Bukkit.createInventory(null, pageSize, "Storage Page " + (i + 1));
            // 从配置文件中获取每页的内容
            List<ItemStack> itemsList = (List<ItemStack>) config.get("page" + i);
            ItemStack[] items = itemsList != null ? itemsList.toArray(new ItemStack[0]) : new ItemStack[0];
            inventory.setContents(items);
            // 设置上一页和下一页图标
            if (i == maxPages - 1) {
                setNavigationItems(inventory, i);
            }
            pages.put(i, inventory);
        }
    }

    private Material getMaterialFromConfig(String path, Material defaultMaterial) {
        String materialName = plugin.getConfig().getString(path, defaultMaterial.name());
        try {
            return Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return defaultMaterial; // 如果配置的物品无效，则使用默认值
        }
    }

    private void setNavigationItems(Inventory inventory, int pageIndex) {
        // 设定上一页图标
        if (pageIndex > 0) {
            ItemStack previousPageItem = new ItemStack(previousPageIcon);
            ItemMeta previousMeta = previousPageItem.getItemMeta();
            if (previousMeta != null) {
                previousMeta.setDisplayName("Previous Page");
                previousPageItem.setItemMeta(previousMeta);
            }
            inventory.setItem(pageSize - 9, previousPageItem); // 最下面一行的最左边
        }

        // 设定下一页图标
        if (pageIndex < maxPages - 1) {
            ItemStack nextPageItem = new ItemStack(nextPageIcon);
            ItemMeta nextMeta = nextPageItem.getItemMeta();
            if (nextMeta != null) {
                nextMeta.setDisplayName("Next Page");
                nextPageItem.setItemMeta(nextMeta);
            }
            inventory.setItem(pageSize - 1, nextPageItem); // 最下面一行的最右边
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int page) {
        if (page >= 0 && page < maxPages) {
            this.currentPage = page;
        }
    }

    public Inventory getPage(int page) {
        return pages.computeIfAbsent(page, p -> {
            Inventory inventory = Bukkit.createInventory(null, pageSize, "Storage Page " + (p + 1));
            if (p == maxPages - 1) {
                setNavigationItems(inventory, p);
            }
            return inventory;
        });
    }

    public int getMaxPages() {
        return maxPages;
    }

    public void save() {
        File file = new File(plugin.getDataFolder(), "storages/" + owner + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (int i = 0; i < maxPages; i++) {
            Inventory inventory = pages.get(i);
            if (inventory != null) {
                config.set("page" + i, inventory.getContents());
            }
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        File file = new File(plugin.getDataFolder(), "storages/" + owner + ".yml");
        if (!file.exists()) return;
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (int i = 0; i < maxPages; i++) {
            Inventory inventory = Bukkit.createInventory(null, pageSize, "Storage Page " + (i + 1));
            ItemStack[] items = ((List<ItemStack>) config.get("page" + i)).toArray(new ItemStack[0]);
            inventory.setContents(items);
            pages.put(i, inventory);
        }
    }
}
