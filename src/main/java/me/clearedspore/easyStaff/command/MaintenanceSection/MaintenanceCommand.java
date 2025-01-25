package me.clearedspore.easyStaff.command.MaintenanceSection;

import me.clearedspore.EasyCommands;
import me.clearedspore.configFile.MessagesFile;
import me.clearedspore.easyStaff.EasyStaff;
import me.clearedspore.easyStaff.util.MaintenanceManager;
import me.clearedspore.feature.Logs.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MaintenanceCommand implements CommandExecutor, TabCompleter {

    private final EasyStaff plugin;
    private final MaintenanceManager maintenanceManager;

    public MaintenanceCommand(EasyStaff plugin, MaintenanceManager maintenanceManager) {
        this.plugin = plugin;
        this.maintenanceManager  = maintenanceManager;
    }

    private boolean isEasyCommandsInstalled() {
        return Bukkit.getPluginManager().isPluginEnabled("EasyCommands");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /maintenance <on|off|add|remove|kickall>");
            return true;
        }

        if (!(sender instanceof Player p)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "on" -> {
                if (p.hasPermission("easystaff.maintenance.toggle") || p.hasPermission("easystaff.maintenance.*")) {
                    maintenanceManager.setMaintenanceEnabled(true);
                    if(isEasyCommandsInstalled()) {
                        LogManager.getInstance().log(p.getUniqueId(), ChatColor.YELLOW + p.getName() + ChatColor.WHITE + " has enabled the maintenance");
                    }
                    p.sendMessage("§9[Staff] §fMaintenance enabled");
                } else {
                    p.sendMessage(ChatColor.RED + "You don't have permission to enable maintenance mode.");
                }
            }
            case "off" -> {
                if (p.hasPermission("easystaff.maintenance.toggle") || p.hasPermission("easystaff.maintenance.*")) {
                    maintenanceManager.setMaintenanceEnabled(false);
                    if(isEasyCommandsInstalled()) {
                        LogManager.getInstance().log(p.getUniqueId(), ChatColor.YELLOW + p.getName() + ChatColor.WHITE + " has disabled the maintenance");
                    }
                    p.sendMessage("§9[Staff] §fMaintenance disabled");
                } else {
                    p.sendMessage(ChatColor.RED + "You don't have permission to disable maintenance mode.");
                }
            }
            case "add" -> {
                if (!p.hasPermission("easystaff.maintenance.add") && !p.hasPermission("easystaff.maintenance.*")) {
                    p.sendMessage(ChatColor.RED + "You don't have permission to add players to the maintenance whitelist.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /maintenance add <player>");
                    return true;
                }
                if (maintenanceManager.addToWhitelist(args[1])) {
                    if(isEasyCommandsInstalled()) {
                        LogManager.getInstance().log(p.getUniqueId(), ChatColor.YELLOW + p.getName() + ChatColor.WHITE + " has added " + args[1] + " to the whitelist.");
                    }
                    p.sendMessage("§9[Staff] §f" + p.getName() + " has added " + args[1] + " to the whitelist");
                } else {
                    sender.sendMessage(ChatColor.RED + args[1] + " is already on the whitelist.");
                }
            }
            case "remove" -> {
                if (!p.hasPermission("easystaff.maintenance.remove") && !p.hasPermission("easystaff.maintenance.*")) {
                    p.sendMessage(ChatColor.RED + "You don't have permission to remove players from the maintenance whitelist.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /maintenance remove <player>");
                    return true;
                }
                if (maintenanceManager.removeFromWhitelist(args[1])) {
                    if(isEasyCommandsInstalled()) {
                        LogManager.getInstance().log(p.getUniqueId(), ChatColor.YELLOW + p.getName() + ChatColor.WHITE + " has removed " + args[1] + " from the whitelist.");
                    }
                    p.sendMessage("§9[Staff] §f" + p.getName() + " has removed " + args[1] + " from the whitelist");
                } else {
                    sender.sendMessage(ChatColor.RED + args[1] + " is not on the whitelist.");
                }
            }
            case "kickall" -> {
                if (!p.hasPermission("easystaff.maintenance.kickall") && !p.hasPermission("easystaff.maintenance.*")) {
                    p.sendMessage(ChatColor.RED + "You don't have permission to kick all players.");
                    return true;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!maintenanceManager.isWhitelisted(player.getName())) {
                        player.kickPlayer(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("kick-message", "&cMaintenance is enabled, you can't join!")));
                    }
                }
                if(isEasyCommandsInstalled()) {
                    LogManager.getInstance().log(p.getUniqueId(), ChatColor.YELLOW + p.getName() + ChatColor.WHITE + " has kicked all players from the server that are not whitelisted.");
                }
                p.sendMessage("§9[Staff] §fYou have kicked all the online players!");
            }
            default -> sender.sendMessage(ChatColor.RED + "Unknown command. Usage: /maintenance <on|off|add|remove|kickall>");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (!(sender instanceof Player p)) {
            return suggestions;
        }

        if (args.length == 1) {
            if (p.hasPermission("easystaff.maintenance.toggle")) {
                suggestions.add("on");
                suggestions.add("off");
            }
            if (p.hasPermission("easystaff.maintenance.add")) {
                suggestions.add("add");
            }
            if (p.hasPermission("easystaff.maintenance.remove")) {
                suggestions.add("remove");
            }
            if (p.hasPermission("easystaff.maintenance.kickall")) {
                suggestions.add("kickall");
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                suggestions.addAll(maintenanceManager.getWhitelistedPlayers());
            }
        }

        return suggestions;
    }
}
