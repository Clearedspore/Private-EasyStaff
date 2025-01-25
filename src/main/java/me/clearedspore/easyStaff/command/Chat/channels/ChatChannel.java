package me.clearedspore.easyStaff.command.Chat.channels;

import me.clearedspore.easyStaff.EasyStaff;
import me.clearedspore.easyStaff.util.ChannelManager;
import me.clearedspore.easyStaff.util.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatChannel {
    private final List<String> commands;
    private final String prefix;
    private final String permission;
    private final Set<Player> activePlayers = new HashSet<>();
    private final ChannelManager channelManager;
    private final EasyStaff plugin;

    public ChatChannel(List<String> commands, String prefix, String permission, ChannelManager channelManager, EasyStaff plugin) {
        this.commands = commands;
        this.prefix = prefix;
        this.permission = permission;
        this.channelManager = channelManager;
        this.plugin = plugin;
    }

    public List<String> getCommands() {
        return commands;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isActive(Player player) {
        return activePlayers.contains(player);
    }

    public void togglePlayer(Player player, ChannelManager channelManager) {
        if (isActive(player)) {
            // Capture the current channel name before removing the player
            String channelName = channelManager.getChannelName(player);

            // Remove the player from the active players list
            activePlayers.remove(player);

            // Send the leave message with the captured channel name
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9You have left the " + channelName + " &9channel."));
        } else {
            // Remove the player from any other active channels
            for (ChatChannel channel : channelManager.getChannels().values()) {
                if (channel.isActive(player)) {
                    channel.activePlayers.remove(player);
                }
            }

            // Add the player to the current channel's active players list
            activePlayers.add(player);

            // Update the player's channel in the channel manager
            channelManager.setPlayerChannel(player, this);

            // Capture the new channel name after adding the player
            String channelName = channelManager.getChannelName(player);

            // Send the join message with the captured channel name
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9You have joined the " + channelName + " &9channel."));
        }
    }
    public Set<Player> getActivePlayers() {
        return activePlayers;
    }
}
