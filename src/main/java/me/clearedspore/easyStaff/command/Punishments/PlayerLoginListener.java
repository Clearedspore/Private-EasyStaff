package me.clearedspore.easyStaff.command.Punishments;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class PlayerLoginListener implements Listener {

    private final JavaPlugin plugin;
    private final PunishmentsManager punishmentsManager;

    public PlayerLoginListener(JavaPlugin plugin, PunishmentsManager punishmentsManager) {
        this.plugin = plugin;
        this.punishmentsManager = punishmentsManager;
    }


    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
        BanEntry banEntry = banList.getBanEntry(player.getName());

        if (banEntry != null) {
            Date expiration = banEntry.getExpiration();
            String duration;

            if (expiration == null) {
                duration = "permanent";
            } else {
                long timeUntilExpiration = expiration.getTime() - System.currentTimeMillis();
                duration = formatDuration(timeUntilExpiration);
            }

            String reason = punishmentsManager.getBanReason(player);
            String banMessage = punishmentsManager.getBanMessage(reason, duration);
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, banMessage);

            String notifyMessage = String.format("§e%s §ftried to join while being banned. (§eExpires: §f%s)",
                    player.getName(), duration);
            Bukkit.getOnlinePlayers().stream()
                    .filter(p -> p.hasPermission("easystaff.punishments.notify"))
                    .forEach(p -> p.sendMessage(notifyMessage));
        }
    }

    private String formatDuration(long durationMillis) {
        if (durationMillis < 0) {
            return "permanent";
        }
        long seconds = durationMillis / 1000 % 60;
        long minutes = durationMillis / (1000 * 60) % 60;
        long hours = durationMillis / (1000 * 60 * 60) % 24;
        long days = durationMillis / (1000 * 60 * 60 * 24);

        return String.format("%d days, %d hours, %d minutes, %d seconds", days, hours, minutes, seconds);
    }
}