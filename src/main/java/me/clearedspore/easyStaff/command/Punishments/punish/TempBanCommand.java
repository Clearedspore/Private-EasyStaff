package me.clearedspore.easyStaff.command.Punishments.punish;

import me.clearedspore.easyStaff.command.Punishments.PunishmentsManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;


public class TempBanCommand implements CommandExecutor {

    private final PunishmentsManager punishmentsManager;
    private final FileConfiguration config;
    private final FileConfiguration reasonsConfig;

    public TempBanCommand(PunishmentsManager punishmentsManager, FileConfiguration config, JavaPlugin plugin) {
        this.punishmentsManager = punishmentsManager;
        this.config = config;
        this.reasonsConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "punishments/reasons.yml"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /tempban <player> <duration> <reason> or /tempban <player> <reason>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
        BanEntry banEntry = banList.getBanEntry(target.getName());
        if (banEntry != null) {
            sender.sendMessage(ChatColor.RED + "Player " + target.getName() + " is already banned.");
            return true;
        }

        String reason;
        String duration;
        if (args.length == 3 && sender.hasPermission("easystaff.punishments.custom")) {
            duration = args[1];
            reason = args[2];
        } else {
            reason = args[1];
            duration = getBanDuration(target.getUniqueId(), reason);
            if (duration == null) {
                sender.sendMessage(ChatColor.RED + "Invalid reason or no duration found.");
                return true;
            }
        }

        long banDuration = parseDuration(duration);
        Date expiryDate = banDuration > 0 ? new Date(System.currentTimeMillis() + banDuration) : null;
        String banMessage = ChatColor.RED + "You have been temporarily banned\n" + ChatColor.WHITE + "Reason: " + reason + "\n" + ChatColor.WHITE + "Duration: " + formatDuration(banDuration);

        banList.addBan(target.getName(), banMessage, expiryDate, sender.getName());
        punishmentsManager.recordTempBan(target.getUniqueId(), sender.getName(), reason, duration, new Date());

        List<String> notifyMessages = config.getStringList("onlinenotify");
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission("easystaff.punishments.notify")) {
                online.sendMessage(ChatColor.translateAlternateColorCodes('&', "§9[Staff] §e" + sender.getName() + " §fhas temporarily banned §e" + target.getName() + " §ffor §c" + reason + " §eExpires in §f " + duration));
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

    private String getBanDuration(UUID playerUUID, String reason) {
        List<Map<String, String>> banHistory = punishmentsManager.getBanHistory(playerUUID);
        int offenceCount = 0;

        for (Map<String, String> record : banHistory) {
            if (record.get("reason").equalsIgnoreCase(reason)) {
                offenceCount++;
            }
        }

        String offenceKey = String.valueOf(offenceCount + 1);
        String duration = reasonsConfig.getString("bans." + reason + ".offences." + offenceKey + ".duration");

        if (duration == null) {
            duration = reasonsConfig.getString("bans." + reason + ".offences.final.duration");
        }

        return duration;
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
                    time += value * 1000;
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