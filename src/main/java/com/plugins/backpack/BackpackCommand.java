package com.plugins.backpack;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackpackCommand implements CommandExecutor {
    private final StorageManager storageManager;

    public BackpackCommand(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        Bukkit.getLogger().info("Executing /backpack command for player " + player.getName());
        Storage storage = storageManager.getStorage(player);
        player.openInventory(storage.getPage(0));
        return true;
    }
}
