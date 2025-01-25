package me.clearedspore.easyStaff.util;

import me.clearedspore.easyStaff.EasyStaff;
import me.clearedspore.easyStaff.command.Chat.channels.ChatChannel;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelManager {
    private final Map<String, ChatChannel> channels = new HashMap<>();
    private final Map<Player, ChatChannel> playerChannels = new HashMap<>();
    private final EasyStaff plugin;

    public ChannelManager(EasyStaff plugin) {
        this.plugin = plugin;
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("channels");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                List<String> commands = section.getStringList(key + ".command");
                String prefix = section.getString(key + ".prefix");
                String permission = section.getString(key + ".permission");
                if (!commands.isEmpty() && prefix != null && permission != null) {
                    ChatChannel chatChannel = new ChatChannel(commands, prefix, permission, this, plugin);
                    for (String command : commands) {
                        channels.put(command, chatChannel);
                    }
                }
            }
        }
    }

    public void setPlayerChannel(Player player, ChatChannel channel) {
        ChatChannel currentChannel = playerChannels.get(player);
        if (currentChannel != null) {
            currentChannel.getActivePlayers().remove(player);
        }
        if (channel != null) {
            playerChannels.put(player, channel);
            channel.getActivePlayers().add(player);
        } else {
            playerChannels.remove(player);
        }
    }

    public ChatChannel getPlayerChannel(Player player) {
        return playerChannels.getOrDefault(player, getDefaultChannel());
    }

    private ChatChannel getDefaultChannel() {
        return channels.get("general");
    }


    public String getChannelName(Player player) {
        ChatChannel currentChannel = getPlayerChannel(player);
        if (currentChannel != null) {

            ConfigurationSection section = plugin.getConfig().getConfigurationSection("channels");
            if (section != null) {
                for (String key : section.getKeys(false)) {
                    if (section.getStringList(key + ".command").contains(currentChannel.getCommands().get(0))) {
                        return section.getString(key + ".name", "general");
                    }
                }
            }
        }
        return "general";
    }

    public ChatChannel getChannel(String command) {
        return channels.get(command);
    }

    public Map<String, ChatChannel> getChannels() {
        return channels;
    }
}
