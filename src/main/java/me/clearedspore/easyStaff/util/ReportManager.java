package me.clearedspore.easyStaff.util;



import me.clearedspore.easyStaff.EasyStaff;
import me.clearedspore.easyStaff.command.Report.GetReportGUI;
import me.clearedspore.easyStaff.command.Report.ReportGUI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportManager {
    private final EasyStaff plugin;
    private GetReportGUI getViewReportGUI;
    private ReportGUI reportGUI;
    private final Map<String, ReportDetails> reports = new HashMap<>();
    private final Map<String, List<String>> pendingNotifications = new HashMap<>();
    private File reportsFile;
    private FileConfiguration reportsConfig;

    private boolean isEasyCommandsInstalled() {
        return Bukkit.getPluginManager().isPluginEnabled("EasyCommands");
    }



    public ReportManager(EasyStaff plugin) {
        this.plugin = plugin;
        loadReports();
    }
    public EasyStaff getPlugin() {
        return plugin;
    }

    public void setviewreportsGui(GetReportGUI gui){
        this.getViewReportGUI = gui;
    }

    public GetReportGUI getviewreportsGUI(){
        return getViewReportGUI;
    }

    public void setReportGUI(ReportGUI gui){
        this.reportGUI = gui;
    }

    public ReportGUI getReportGUI(){
        return reportGUI;
    }

    private void createReportsFile() {
        File storageDir = new File(plugin.getDataFolder(), "storage");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        reportsFile = new File(storageDir, "reports.yml");
        if (!reportsFile.exists()) {
            try {
                reportsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create reports.yml file!");
            }
        }
        reportsConfig = YamlConfiguration.loadConfiguration(reportsFile);
    }

    public void loadReports() {
        createReportsFile();
        reports.clear();

        if (reportsConfig.getConfigurationSection("reports") != null) {
            for (String name : reportsConfig.getConfigurationSection("reports").getKeys(false)) {
                String reportDetailsString = reportsConfig.getString("reports." + name);
                if (reportDetailsString != null) {
                    String[] parts = reportDetailsString.split(":");
                    if (parts.length == 4) {
                        reports.put(name, new ReportDetails(parts[0], parts[1], parts[2]));
                    }
                }
            }
        }
    }

    public void saveReports() {
        if (reportsConfig == null || reportsFile == null) return;

        reportsConfig.set("reports", null);
        for (Map.Entry<String, ReportDetails> entry : reports.entrySet()) {
            reportsConfig.set("reports." + entry.getKey(), entry.getValue().toString());
        }

        try {
            reportsConfig.save(reportsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save reports.yml file!");
        }
    }
    public void reloadReports() {
        if (reportsFile == null) {
            createReportsFile();
        }
        reportsConfig = YamlConfiguration.loadConfiguration(reportsFile);
        reports.clear();

        if (reportsConfig.getConfigurationSection("reports") != null) {
            for (String name : reportsConfig.getConfigurationSection("reports").getKeys(false)) {
                String reportDetailsString = reportsConfig.getString("reports." + name);
                if (reportDetailsString != null) {
                    String[] parts = reportDetailsString.split(":");
                    if (parts.length == 4) {
                        reports.put(name, new ReportDetails(parts[0], parts[1], parts[2]));
                    }
                }
            }
        }
    }

    public void addPendingNotification(String playerName, String message) {
        pendingNotifications.computeIfAbsent(playerName, k -> new ArrayList<>()).add(message);
    }

    public List<String> getPendingNotifications(String playerName) {
        return pendingNotifications.getOrDefault(playerName, new ArrayList<>());
    }

    public void clearPendingNotifications(String playerName) {
        pendingNotifications.remove(playerName);
    }

    public Map<String, ReportDetails> getReports(Player p) {
        if (!p.hasPermission("easystaff.reports.getreports")) {
            p.sendMessage(ChatColor.RED + "You don't have permission to view reports!");
            return null;
        }
        return new HashMap<>(reports);
    }

    private String getReporterName(String reportKey) {
        ReportDetails details = reports.get(reportKey);
        return details != null ? details.getReporterName() : null;
    }

    public boolean reportPlayer(Player reporter, OfflinePlayer suspect, String reason) {
        if (!reporter.hasPermission("easystaff.reports.report")) {
            reporter.sendMessage(ChatColor.RED + "You don't have permission to report a player!");
            return false;
        }
        String suspectKey = suspect.getName() + " - " + reason;
        ReportDetails reportDetails = new ReportDetails(reporter.getName(), suspect.getName(), reason);
        reports.put(suspectKey, reportDetails);
        saveReports();

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission("easystaff.reports.reportnotify")) {
                String border = ChatColor.BLUE + "=========================";

                TextComponent acceptButton = new TextComponent("[Accept]");
                acceptButton.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                acceptButton.setBold(true);
                acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports accept " + suspectKey));

                TextComponent denyButton = new TextComponent("[Deny]");
                denyButton.setColor(net.md_5.bungee.api.ChatColor.RED);
                denyButton.setBold(true);
                denyButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports deny " + suspectKey));

                TextComponent teleportButton = new TextComponent("[Teleport]");
                teleportButton.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
                teleportButton.setBold(true);
                if(isEasyCommandsInstalled()) {
                    teleportButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/easycommands:teleport " + suspect.getName()));
                } else {
                    teleportButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleport " + suspect.getName()));
                }

                TextComponent manageButton = new TextComponent("[Manage report]");
                manageButton.setColor(net.md_5.bungee.api.ChatColor.BLUE);
                manageButton.setBold(true);
                manageButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports"));


                TextComponent message = new TextComponent();
                message.addExtra(border + "\n");
                message.addExtra(ChatColor.WHITE + reporter.getName() + ChatColor.BLUE + " has reported " + ChatColor.WHITE + suspect.getName() + "\n");
                message.addExtra(ChatColor.BLUE + "Reason: " + ChatColor.WHITE + reason + "\n");
                message.addExtra(acceptButton);
                message.addExtra(" ");
                message.addExtra(denyButton);
                message.addExtra(" ");
                message.addExtra(teleportButton);
                message.addExtra(" ");
                message.addExtra(manageButton);
                message.addExtra("\n" + border);

                online.spigot().sendMessage(message);
            }
        }

        return true;
    }

    public void denyReport(Player p, String name) {
        if (!p.hasPermission("easystaff.reports.manage")) {
            p.sendMessage(ChatColor.RED + "You don't have permission to manage reports!");
            return;
        }

        if (!reports.containsKey(name)) {
            p.sendMessage(ChatColor.RED + "The report does not exist.");
            return;
        }

        reports.remove(name);
        String reporterName = getReporterName(name);

        if (reporterName != null) {
            Player reporter = Bukkit.getPlayerExact(reporterName);
            String message = "§9[Reports] §fYour report against " + name + " was §cdenied";
            if (reporter != null && reporter.isOnline()) {
                reporter.sendMessage(ChatColor.RED + message);
            } else {
                addPendingNotification(reporterName, message);
            }
        }

        p.sendMessage("§9[Reports] §fYou have denied the report!");
        notifyStaff(p.getName(), name, "denied");
        saveReports();
    }

    public void acceptReport(Player p, String name) {
        if (!p.hasPermission("easystaff.reports.manage")) {
            p.sendMessage(ChatColor.RED + "You don't have permission to manage reports!");
            return;
        }

        if (!reports.containsKey(name)) {
            p.sendMessage(ChatColor.RED + "The report does not exist.");
            return;
        }

        reports.remove(name);
        String reporterName = getReporterName(name);

        if (reporterName != null) {
            Player reporter = Bukkit.getPlayerExact(reporterName);
            String message = " §9[Staff] §fYour report against " + name + " was §eaccepted";
            if (reporter != null && reporter.isOnline()) {
                reporter.sendMessage(ChatColor.GREEN + message);
            } else {
                addPendingNotification(reporterName, message);
            }
        }

        p.sendMessage(ChatColor.BLUE + "You have accepted the report");
        notifyStaff(p.getName(), name, "accepted");
        saveReports();
    }

    private void notifyStaff(String managerName, String reportName, String action) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission("easystaff.reports.reportnotify")) {
                online.sendMessage("§9[Staff] §e" + managerName + " §fhas §e" + action + " §fthe report against §e" + reportName);
            }
        }
    }
}
