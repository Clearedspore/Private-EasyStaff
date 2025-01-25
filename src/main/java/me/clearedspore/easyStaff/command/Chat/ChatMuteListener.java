package me.clearedspore.easyStaff.command.Chat;

import me.clearedspore.EasyCommands;
import me.clearedspore.easyStaff.EasyStaff;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatMuteListener implements Listener {

    private final EasyStaff plugin;

    public ChatMuteListener(EasyStaff plugin) {
        this.plugin = plugin;
    }

    private boolean isMuteEnabled() {
        return plugin.getConfig().getBoolean("mutechat");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if(p.hasPermission("easystaff.chat-toggle.bypass")){
            return;
        } else if(!p.hasPermission("easystaff.chat-toggle.bypass")) {
            if (isMuteEnabled() == true) {
                e.setCancelled(true);
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe chat is currently muted."));
                return;
            } else {
                e.setCancelled(false);
            }
        }
    }
}
