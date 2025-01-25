package me.clearedspore.easyStaff.command;

import me.clearedspore.easyStaff.EasyStaff;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EasyStaffHelp implements CommandExecutor, TabCompleter {
    private final EasyStaff plugin;

    public EasyStaffHelp(EasyStaff plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "reload":
                    if (player.hasPermission("easystaff.help.reload")) {
                        plugin.reloadConfig();
                        player.sendMessage("§9[Staff] §fReloading files");
                    } else {
                        player.sendMessage("§cNo permissions!");
                    }
                    break;
                case "help":
                    if (player.hasPermission("easystaff.help")) {
                        player.sendMessage(ChatColor.BLUE + "=============================");
                        player.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "EasyStaff Help:");
                        player.sendMessage(ChatColor.BLUE + "");
                        player.sendMessage(ChatColor.BLUE + "[] < not required <> < required");
                        player.sendMessage("§e/chat-toggle: §fToggles the chat mute");
                        player.sendMessage("§e/clearchat: §fClear the chat. easystaff.clearchat.bypass bypasses this");
                        player.sendMessage("§e/freeze <player>: §fFreezes or unfreezes a player");
                        player.sendMessage("§e/maintenance on: §fTurn on the maintenance");
                        player.sendMessage("§e/maintenance off: §fTurn off the maintenance");
                        player.sendMessage("§e/maintenance add [player]: §gAdd a player to the maintenance whitelist");
                        player.sendMessage("§e/maintenance remove [player]: §gRemoves a player from the maintenance whitelist");
                        player.sendMessage("§e/maintenance kickall: §gKicks everyone that is not on the maintenance whitelist");
                        player.sendMessage("§e/easystaff help 2 for page 2 ");
                    } else {
                        player.sendMessage("§cNo permission");
                    }
                    break;
                case "info":
                    if (player.hasPermission("easystaff.help")) {
                        player.sendMessage("§9EasyStaff:");
                        player.sendMessage("§fVersion: 1.0");
                        player.sendMessage("§fLatest version: 1.0");
                        player.sendMessage("§fPlugin made by ClearedSpore");
                        player.sendMessage("§fDo not edit, sell or steal code from this plugin!");
                        player.sendMessage("§fThis is the newest and best staff plugin!");
                    } else {
                        player.sendMessage("§cNo permission");
                    }
                    break;
                case "version":
                    if (player.hasPermission("easystaff.help")) {
                        player.sendMessage("§fVersion: 1.0");
                        player.sendMessage("§fLatest version: 1.0");
                    } else {
                        player.sendMessage("§cNo permission");
                    }
                    break;
            }

        } else if (args.length == 2) {
            switch (args[1].toLowerCase()) {
                case "2":
                    if (player.hasPermission("easystaff.help")) {
                        player.sendMessage(ChatColor.BLUE + "=============================");
                        player.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "EasyStaff Help page 2:");
                        player.sendMessage(ChatColor.BLUE + "");
                        player.sendMessage(ChatColor.BLUE + "[] < not required <> < required");
                        player.sendMessage("§e/report <player> [reason]: §fReport a player using /report <player> <reason> or do /report <player> to open a gui!");
                        player.sendMessage("§e/reports: §fManage all reports");
                    } else {
                        player.sendMessage("§cno permission");
                    }
                    break;
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("easystaff.help.reload")) {
                suggestions.add("reload");
            }
                if (sender.hasPermission("easystaff.help")) {
                    suggestions.add("help");
                    suggestions.add("info");
                    suggestions.add("version");
            }
        }
        return suggestions;
    }
}
