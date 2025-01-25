package me.clearedspore.easyStaff.util;

import me.clearedspore.easyStaff.command.Chat.channels.ChatChannel;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.nametag.NameTagManager;
import me.neznamy.tab.api.tablist.TabListFormatManager;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.chat.TextComponent;

public class VanishManager {

    private final Plugin plugin;
    private final ChannelManager channelManager;
    private final Map<UUID, Integer> vanishTasks = new HashMap<>();

    public VanishManager(Plugin plugin, ChannelManager channelManager) {
        this.plugin = plugin;
        this.channelManager = channelManager;
    }

    public void toggleVanish(Player player) {
        boolean isVanished = player.hasMetadata("vanished");
        Plugin tabPlugin = Bukkit.getPluginManager().getPlugin("TAB");
        TabListFormatManager tabManager = TabAPI.getInstance().getTabListFormatManager();
        TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
        NameTagManager nameTagManager = TabAPI.getInstance().getNameTagManager();

        if (isVanished) {
            player.removeMetadata("vanished", plugin);
            player.setInvisible(false);
            if(tabPlugin.isEnabled()) {
                tabManager.setSuffix(tabPlayer, "");
                nameTagManager.setSuffix(tabPlayer, "");
            }
            cancelVanishTask(player);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', "&fYou are now visible.")));
            if(plugin.getConfig().getBoolean("flight", true == true)){
                player.setAllowFlight(false);
            }

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.showPlayer(plugin, player);
            }
            String joinMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("join-msg").replace("%player%", player.getName()));
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.showPlayer(plugin, player);
                onlinePlayer.sendMessage(joinMessage);
            }

        } else {
            player.setMetadata("vanished", new FixedMetadataValue(plugin, true));
            player.setInvisible(true);
            if(tabPlugin.isEnabled()) {
                nameTagManager.setSuffix(tabPlayer, ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("vanish-tag")));
                tabManager.setSuffix(tabPlayer, ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("vanish-tag")));
            }

            ChatChannel currentChannel = channelManager.getPlayerChannel(player);
            String channelName = currentChannel != null ? currentChannel.getPrefix() : "general";

            if(plugin.getConfig().getBoolean("flight", true == true)){
                player.setAllowFlight(true);
            }

            startVanishTask(player, ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("actionbar").replace("%channel%", channelName)));
            String leaveMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("leave-msg").replace("%player%", player.getName()));
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.hasPermission("easystaff.seevanished")) {
                    onlinePlayer.hidePlayer(plugin, player);
                } else {
                    onlinePlayer.setInvisible(false);
                    onlinePlayer.showPlayer(plugin, player);
                }
                onlinePlayer.sendMessage(leaveMessage);
            }
        }
    }

    public boolean isInVanish(Player player){
        return player.hasMetadata("vanished");
    }

    private void startVanishTask(Player player, String actionbar) {
        int taskId = new BukkitRunnable() {
            @Override
            public void run() {
                String currentChannel = channelManager.getChannelName(player);
                String channelName = currentChannel != null ? currentChannel : "general";
                String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("actionbar").replace("%channel%", channelName));
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            }
        }.runTaskTimer(plugin, 0L, 10L).getTaskId();
        vanishTasks.put(player.getUniqueId(), taskId);
    }

    private void cancelVanishTask(Player player) {
        Integer taskId = vanishTasks.remove(player.getUniqueId());
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    public void updateActionBar(Player player) {
        String currentChannel = channelManager.getChannelName(player);
        String channelName = currentChannel != null ? currentChannel : "general";
        String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("actionbar").replace("%channel%", channelName));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
}
