package me.clearedspore.easyStaff.listener;

import me.clearedspore.EasyCommands;
import me.clearedspore.configFile.MessagesFile;
import me.clearedspore.easyStaff.EasyStaff;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class JoinLeaveListener implements Listener {

    private final EasyStaff plugin;

    public JoinLeaveListener(EasyStaff plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if(p.hasPermission("easystaff.joinleavenotify")) {
            for (Player online : Bukkit.getOnlinePlayers()) {

                if (online.hasPermission("easystaff.joinleavenotify")) {
                    online.sendMessage("§9[Staff] §f" + p.getName() + " §ehas §ajoined §ethe server!");
                }
            }
        }

    }
    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        Player p = e.getPlayer();
        if(p.hasPermission("easystaff.joinleavenotify")) {
            for (Player online : Bukkit.getOnlinePlayers()) {

                if (online.hasPermission("easystaff.joinleavenotify")) {
                    online.sendMessage("§9[Staff] §f" + p.getName() + " §ehas §cleft §ethe server!");
                }
            }
        } else return;
    }
}
