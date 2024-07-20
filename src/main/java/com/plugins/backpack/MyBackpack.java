package com.plugins.backpack;

import com.plugins.backpack.BackpackCommand;
import com.plugins.backpack.StorageListener;
import com.plugins.backpack.StorageManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MyBackpack extends JavaPlugin {
    private StorageManager storageManager;

    // 插件启用时调用
    @Override
    public void onEnable() {
        // 保存默认配置文件 config.yml 到插件数据文件夹中
        this.saveDefaultConfig();
        // 初始化 StorageManager
        storageManager = new StorageManager(this);
        // 注册 backpack 命令，并将其执行器设置为 BackpackCommand
        getCommand("backpack").setExecutor(new BackpackCommand(storageManager));
        // 注册事件监听器 StorageListener
        getServer().getPluginManager().registerEvents(new StorageListener(storageManager, this), this);
    }

    // 插件禁用时调用
    @Override
    public void onDisable() {
        // 保存所有仓库数据
        storageManager.saveAll();
    }
}
