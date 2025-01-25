package me.clearedspore.easyStaff.command.Chat.channels;

import me.clearedspore.easyStaff.util.ChannelManager;
import me.clearedspore.easyStaff.util.VanishManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChannelCommand implements CommandExecutor {

    private final ChatChannel channel;
    private final ChannelManager channelManager;
    private final VanishManager vanishManager;

    public ChannelCommand(ChatChannel channel, ChannelManager channelManager, VanishManager vanishManager) {
        this.channel = channel;
        this.channelManager = channelManager;
        this.vanishManager = vanishManager;
    }

    public ChatChannel getChannel() {
        return channel;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission(channel.getPermission())) {
            player.sendMessage("You do not have permission to use this channel.");
            return true;
        }
        if (channel.isActive(player)) {
            channel.togglePlayer(player, channelManager);
            channelManager.setPlayerChannel(player, null);
        } else {
            channel.togglePlayer(player, channelManager);
            channelManager.setPlayerChannel(player, channel);
        }
        if(vanishManager.isInVanish(player)) {
            vanishManager.updateActionBar(player);
        } else {
            return true;
        }

        return true;
    }
}