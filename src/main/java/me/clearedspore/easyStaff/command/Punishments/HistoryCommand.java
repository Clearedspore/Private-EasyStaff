package me.clearedspore.easyStaff.command.Punishments;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HistoryCommand implements CommandExecutor {

    private final PunishmentsManager punishmentsManager;

    public HistoryCommand(PunishmentsManager punishmentsManager) {
        this.punishmentsManager = punishmentsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /history <player>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || !target.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "Player not found or has never played before.");
            return true;
        }

        Player player = (Player) sender;
        Inventory historyMenu = Bukkit.createInventory(null, 27, "History: " + target.getName());

        historyMenu.setItem(11, createCarpetItem(Material.RED_CARPET, ChatColor.RED + "Bans"));


        historyMenu.setItem(12, createCarpetItem(Material.YELLOW_CARPET, ChatColor.YELLOW + "Kicks"));
        player.openInventory(historyMenu);

        return true;
    }

    private ItemStack createCarpetItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}