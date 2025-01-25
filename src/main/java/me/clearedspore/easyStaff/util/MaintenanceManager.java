package me.clearedspore.easyStaff.util;

import me.clearedspore.EasyCommands;
import me.clearedspore.easyStaff.EasyStaff;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MaintenanceManager {

    private final EasyStaff plugin;
    private final Set<String> whitelist;

    public MaintenanceManager(EasyStaff plugin) {
        this.plugin = plugin;
        this.whitelist = new HashSet<>(plugin.getConfig().getStringList("whitelist"));
    }

    public boolean isMaintenanceEnabled() {
        return plugin.getConfig().getBoolean("maintenance");
    }

    public void setMaintenanceEnabled(boolean enabled) {
        plugin.getConfig().set("maintenance", enabled);
        plugin.saveConfig();
    }

    public boolean addToWhitelist(String playerName) {
        boolean added = whitelist.add(playerName);
        if (added) {
            saveWhitelist();
        }
        return added;
    }

    public boolean removeFromWhitelist(String playerName) {
        boolean removed = whitelist.remove(playerName);
        if (removed) {
            saveWhitelist();
        }
        return removed;
    }

    public boolean isWhitelisted(String playerName) {
        return whitelist.contains(playerName);
    }

    private void saveWhitelist() {
        plugin.getConfig().set("whitelist", whitelist.stream().toList());
        plugin.saveConfig();
    }
    public List<String> getWhitelistedPlayers() {
        return new ArrayList<>(whitelist);
    }
}