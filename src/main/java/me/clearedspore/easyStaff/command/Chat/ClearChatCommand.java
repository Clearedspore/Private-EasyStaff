package me.clearedspore.easyStaff.command.Chat;

import me.clearedspore.configFile.MessagesFile;
import me.clearedspore.feature.Logs.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand implements CommandExecutor {

    private boolean isEasyCommandsInstalled() {
        return Bukkit.getPluginManager().isPluginEnabled("EasyCommands");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p) {
            if(isEasyCommandsInstalled()) {
                LogManager.getInstance().log(p.getUniqueId(), ChatColor.YELLOW + p.getName() + ChatColor.WHITE + " Has cleared the chat");
            }
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.hasPermission("easystaff.clearchat.bypass")) {
                    p.sendMessage("§9[Staff] &fThe chat has been cleared by " + p.getName());
                } else if (!online.hasPermission(("easystaff.clearchat.bypass"))) {

                    clearPlayerChat(online);

                    p.sendMessage("§eChat has been cleared!");
                }
            }
        }
        return true;
    }

    private void clearPlayerChat(Player target) {
        for (int i = 0; i < 100; i++) {
            target.sendMessage("");
        }
    }
}
