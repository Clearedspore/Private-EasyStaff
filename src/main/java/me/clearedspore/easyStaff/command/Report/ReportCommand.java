package me.clearedspore.easyStaff.command.Report;

import me.clearedspore.configFile.MessagesFile;
import me.clearedspore.easyStaff.util.ReportManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ReportCommand implements CommandExecutor, TabCompleter {
    private final ReportManager reportManager;
    private final FileConfiguration config;
    private final Map<UUID, Long> lastReportTime = new HashMap<>();


    public ReportCommand(ReportManager reportManager, FileConfiguration config) {
        this.reportManager = reportManager;
        this.config = config;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p) {


            long cooldown = parseCooldown(config.getString("reportcooldown", "5 minutes"));
            long currentTime = System.currentTimeMillis();
            long lastTime = lastReportTime.getOrDefault(p.getUniqueId(), 0L);
            if (!p.hasPermission("easystaff.reports.bypasscooldown")) {
                if (currentTime - lastTime < cooldown) {
                    long remainingTime = (cooldown - (currentTime - lastTime)) / 1000;
                    p.sendMessage(ChatColor.RED + "You must wait " + remainingTime + " seconds before reporting again.");
                    return true;
                }
            } else {

                String playerName = args[0];
                Player targetPlayer = Bukkit.getPlayerExact(playerName);
                OfflinePlayer target;

                if (targetPlayer != null) {
                    target = targetPlayer;
                } else {
                    target = Bukkit.getOfflinePlayer(playerName);
                    if (!target.hasPlayedBefore()) {
                        p.sendMessage(ChatColor.RED + "That player does not exist!");
                        return true;
                    }
                }
                if (args.length == 1) {
                    reportManager.getReportGUI().openReportGUI(p, 0, target);
                } else {
                    String reason = args[1].toLowerCase();
                    List<String> validReasons = config.getStringList("reportreasons").stream()
                            .map(String::toLowerCase)
                            .collect(Collectors.toList());

                    if (validReasons.contains(reason)) {
                        boolean success = reportManager.reportPlayer(p, target, reason);
                        if (success) {
                            p.sendMessage("§9[Reports] §fReport submitted");
                            lastReportTime.put(p.getUniqueId(), currentTime);
                        } else {
                            p.sendMessage(ChatColor.RED + "Failed to submit report.");
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "Invalid reason. Valid reasons are: " + String.join(", ", validReasons));
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            return config.getStringList("reportreasons");
        }
        return new ArrayList<>();
    }
    private long parseCooldown(String cooldownString) {
        String[] parts = cooldownString.split(" ");
        if (parts.length != 2) return TimeUnit.MINUTES.toMillis(5);

        long duration = Long.parseLong(parts[0]);
        String unit = parts[1].toLowerCase();

        switch (unit) {
            case "seconds":
                return TimeUnit.SECONDS.toMillis(duration);
            case "minutes":
                return TimeUnit.MINUTES.toMillis(duration);
            case "hours":
                return TimeUnit.HOURS.toMillis(duration);
            case "days":
                return TimeUnit.DAYS.toMillis(duration);
            default:
                return TimeUnit.MINUTES.toMillis(5);
        }
    }
}
