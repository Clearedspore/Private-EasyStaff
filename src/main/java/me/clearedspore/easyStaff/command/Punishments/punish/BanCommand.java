package me.clearedspore.easyStaff.command.Punishments.punish;

import me.clearedspore.easyStaff.command.Punishments.PunishmentsManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BanCommand implements CommandExecutor {

    private final PunishmentsManager punishmentsManager;
    private final FileConfiguration config;

    public BanCommand(PunishmentsManager punishmentsManager, FileConfiguration config) {
        this.punishmentsManager = punishmentsManager;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /ban <player> <reason>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
        BanEntry banEntry = banList.getBanEntry(target.getName());
        if (banEntry != null) {
            sender.sendMessage(ChatColor.RED + "Player " + target.getName() + " is already banned.");
            return true;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String duration = "permanent"; // Always permanent for /ban command

        long banDuration = parseDuration(duration);
        Date expiryDate = banDuration > 0 ? new Date(System.currentTimeMillis() + banDuration) : null;
        String banMessage = ChatColor.RED + "You have been banned\n" + ChatColor.WHITE + "Reason: " + reason + "\n" + ChatColor.WHITE + "Duration: " + formatDuration(banDuration);

        banList.addBan(target.getName(), banMessage, expiryDate, sender.getName());
        punishmentsManager.recordBan(target.getUniqueId(), sender.getName(), reason, duration, new Date());

        List<String> notifyMessages = config.getStringList("onlinenotify");
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission("easystaff.punishments.notify")) {
                online.sendMessage(ChatColor.translateAlternateColorCodes('&', "§9[Staff] §e" + sender.getName() + " §fhas banned §e" + target.getName() + " §ffor §c" + reason + " §eExpires in §f permanent"));
                for (String message : notifyMessages) {
                    online.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            }
        }

        if (target.isOnline()) {
            ((Player) target).kickPlayer(banMessage);
        }

        return true;
    }

    private long parseDuration(String duration) {
        if (duration.equalsIgnoreCase("permanent")) {
            return -1;
        }
        long time = 0;
        String[] parts = duration.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        for (int i = 0; i < parts.length; i += 2) {
            int value = Integer.parseInt(parts[i]);
            char unit = parts[i + 1].charAt(0);
            switch (unit) {
                case 'd':
                    time += value * 86400000L;
                    break;
                case 'h':
                    time += value * 3600000L;
                    break;
                case 'm':
                    time += value * 60000L;
                    break;
                case 's':
                    time += value * 1000L;
                    break;
            }
        }
        return time;
    }

    private String formatDuration(long durationMillis) {
        if (durationMillis < 0) {
            return "permanent";
        }
        long seconds = durationMillis / 1000 % 60;
        long minutes = durationMillis / (1000 * 60) % 60;
        long hours = durationMillis / (1000 * 60 * 60) % 24;
        long days = durationMillis / (1000 * 60 * 60 * 24);

        return String.format("%d days, %d hours, %d minutes, %d seconds", days, hours, minutes, seconds);
    }
}