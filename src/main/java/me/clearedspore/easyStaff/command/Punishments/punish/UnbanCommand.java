package me.clearedspore.easyStaff.command.Punishments.punish;

import me.clearedspore.easyStaff.command.Punishments.PunishmentsManager;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UnbanCommand implements CommandExecutor {

    private final PunishmentsManager punishmentsManager;

    public UnbanCommand(PunishmentsManager punishmentsManager) {
        this.punishmentsManager = punishmentsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /unban <player> <reason>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
        if (banList.isBanned(target.getName())) {
            banList.pardon(target.getName());
            punishmentsManager.recordUnban(target.getUniqueId(), sender.getName(), reason);

            Map<String, String> unbanDetails = new HashMap<>();
            unbanDetails.put("unbanIssuer", sender.getName());
            unbanDetails.put("unbanReason", reason);

            sender.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " has been unbanned. Reason: " + reason);
        } else {
            sender.sendMessage(ChatColor.RED + "Player " + target.getName() + " is not banned.");
        }

        return true;
    }
}