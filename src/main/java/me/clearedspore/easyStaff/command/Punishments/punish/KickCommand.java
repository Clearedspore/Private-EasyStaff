package me.clearedspore.easyStaff.command.Punishments.punish;

import me.clearedspore.command.settings.SettingsManager;
import me.clearedspore.easyStaff.command.Punishments.PunishmentsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class KickCommand implements CommandExecutor {

    private final PunishmentsManager punishmentsManager;

    public KickCommand(PunishmentsManager punishmentsManager) {
        this.punishmentsManager = punishmentsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /kick <player> <reason>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || !target.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        punishmentsManager.recordKick(target.getUniqueId(), sender.getName(), reason, new Date());

        if (target.isOnline()) {
            ((Player) target).kickPlayer(ChatColor.RED + "You have been kicked.\nReason: " + reason);
        }

        sender.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " has been kicked for: " + reason);
        return true;
    }
}