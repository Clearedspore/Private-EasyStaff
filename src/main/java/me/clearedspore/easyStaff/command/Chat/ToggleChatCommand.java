package me.clearedspore.easyStaff.command.Chat;

import me.clearedspore.EasyCommands;
import me.clearedspore.command.settings.SettingsManager;
import me.clearedspore.configFile.MessagesFile;
import me.clearedspore.easyStaff.EasyStaff;
import me.clearedspore.feature.Logs.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleChatCommand implements CommandExecutor {
    private final EasyStaff plugin;
    private final SettingsManager settingsManager;
    private final LogManager logManager;
    public ToggleChatCommand(EasyStaff plugin, SettingsManager settingsManager, LogManager logManager) {
        this.plugin = plugin;
        this.settingsManager = settingsManager;
        this.logManager = logManager;
    }

    private boolean isEasyCommandsInstalled() {
        return Bukkit.getPluginManager().isPluginEnabled("EasyCommands");
    }


    private boolean isMuteEnabled() {
        return plugin.getConfig().getBoolean("mutechat");
    }

    private void setMuteEnabled(boolean enabled) {
        plugin.getConfig().set("mutechat", enabled);
        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            if(isMuteEnabled() == true){
                setMuteEnabled(false);
            } else {
                setMuteEnabled(true);
            }
        } else {
            if(isMuteEnabled() == true){
                setMuteEnabled(false);
            } else {
                setMuteEnabled(true);
            }
            if(isEasyCommandsInstalled()) {
                logManager.log(((Player) sender).getUniqueId(), ChatColor.YELLOW + sender.getName() + ChatColor.WHITE + " has " + (isMuteEnabled() ? "disabled" : "enabled") + " the chat");
            }
        for (Player online : Bukkit.getOnlinePlayers()){
            if(settingsManager.isLogEnabled(online)) {
                if (online.hasPermission("easycommands.logs") || online.hasPermission("easystaff.logs")) {
                    online.sendMessage(ChatColor.GRAY + "[" + sender.getName() + " has " + (isMuteEnabled() ? "enabled" : "disabled")+ " the chat]");
                }
            }
        }
        }
        for(Player online : Bukkit.getOnlinePlayers()){
            online.sendMessage(ChatColor.BLUE + "Chat is now " + (isMuteEnabled() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
        }
        return true;
    }
}
