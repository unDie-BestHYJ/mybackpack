package com.plugins.backpack;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StorageManager {
    private final JavaPlugin plugin;
    private final Map<UUID, Storage> storages = new HashMap<>();

    public StorageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadStorages();
    }

    public Storage getStorage(Player player) {
        return storages.computeIfAbsent(player.getUniqueId(), uuid -> new Storage(plugin, player));
    }

    public void loadStorages() {
        File dataFolder = new File(plugin.getDataFolder(), "storages");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        for (File file : dataFolder.listFiles()) {
            if (file.getName().endsWith(".yml")) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                UUID uuid = UUID.fromString(file.getName().replace(".yml", ""));
                storages.put(uuid, new Storage(plugin, config, uuid));
            }
        }
    }


    // 保存所有玩家的仓库数据
    public void saveAll() {
        for (Storage storage : storages.values()) {
            storage.save();
        }
    }
}
