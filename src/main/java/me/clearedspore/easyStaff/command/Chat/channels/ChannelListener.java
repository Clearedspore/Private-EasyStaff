package me.clearedspore.easyStaff.command.Chat.channels;

import me.clearedspore.easyStaff.util.ChannelManager;
import me.clearedspore.easyStaff.util.VanishManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChannelListener implements Listener {
        private final ChannelManager channelManager;
        public ChannelListener(ChannelManager channelManager) {
            this.channelManager = channelManager;
        }

        @EventHandler
        public void onPlayerChat(AsyncPlayerChatEvent event) {
            Player player = event.getPlayer();
            ChatChannel currentChannel = channelManager.getPlayerChannel(player);
            if (currentChannel != null && currentChannel.isActive(player)) {
                event.setCancelled(true);
                String message = ChatColor.translateAlternateColorCodes('&', currentChannel.getPrefix() + " " + player.getName() + ": " + event.getMessage());
                for (Player p : currentChannel.getActivePlayers()) {
                    p.sendMessage(message);
                }
            }
        }

        @EventHandler
        public void onPlayerLeave(PlayerQuitEvent event) {
            Player player = event.getPlayer();
            ChatChannel currentChannel = channelManager.getPlayerChannel(player);
            if (currentChannel != null) {
                currentChannel.togglePlayer(player, channelManager);
            }
        }
    }
