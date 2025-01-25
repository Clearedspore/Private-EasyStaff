package me.clearedspore.easyStaff.command;

import me.clearedspore.easyStaff.command.Punishments.PunishmentsManager;
import me.clearedspore.easyStaff.listener.PlayerIPListener;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class AltsCommand implements CommandExecutor {

    private final PlayerIPListener playerIPListener;

    public AltsCommand(PlayerIPListener playerIPListener) {
        this.playerIPListener = playerIPListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Please specify a player name.");
            return false;
        }

        String playername = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(playername);

        if (target == null || !target.hasPlayedBefore()) {
            sender.sendMessage("Player not found.");
            return false;
        }

        String targetIP = getPlayerIP(target);
        if (targetIP == null) {
            sender.sendMessage("Could not retrieve IP address for the player.");
            return false;
        }

        List<String> alts = new ArrayList<>();

        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            String playerIP = getPlayerIP(player);
            if (playerIP != null && playerIP.equals(targetIP) && !player.equals(target)) {
                String playerName = player.getName();
                if (player.isOnline()) {
                    alts.add("§a" + playerName);
                } else {
                    alts.add("§7" + playerName);
                }
            }
        }

        if (alts.isEmpty()) {
            sender.sendMessage("No alternate accounts found for " + playername + ".");
        } else {
            sender.sendMessage("§eAlts for " + playername + ": §a[Online] §7[Offline]");
            sender.sendMessage(String.join(" ", alts));
        }

        return true;
    }

    private String getPlayerIP(OfflinePlayer player) {
        if (player.isOnline()) {
            return player.getPlayer().getAddress().getAddress().getHostAddress();
        }
        return playerIPListener.getLastKnownIP(player.getUniqueId());
    }
}