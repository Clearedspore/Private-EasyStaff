package me.clearedspore.easyStaff.command.Punishments;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PunishmentsManager {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    private final JavaPlugin plugin;
    private final File reasonsFile;
    private final FileConfiguration reasonsConfig;
    private final File punishmentsDataFile;
    private final FileConfiguration punishmentsData;

    public PunishmentsManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.reasonsFile = new File(plugin.getDataFolder(), "punishments/reasons.yml");
        this.reasonsConfig = YamlConfiguration.loadConfiguration(reasonsFile);
        this.punishmentsDataFile = new File(plugin.getDataFolder(), "storage/punishmentsdata.yml");
        this.punishmentsData = YamlConfiguration.loadConfiguration(punishmentsDataFile);
        createPunishmentsDataFile();
    }

    private void createPunishmentsDataFile() {
        if (!punishmentsDataFile.exists()) {
            try {
                punishmentsDataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getBanMessage(String reason, String duration) {
        List<String> messageLines = reasonsConfig.getStringList("messages.ban");
        StringBuilder message = new StringBuilder();
        for (String line : messageLines) {
            line = line.replace("%reason%", reason).replace("%duration%", duration);
            message.append(line).append("\n");
        }
        return message.toString();
    }

    public String getBanReason(Player player) {
        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
        BanEntry banEntry = banList.getBanEntry(player.getName());
        if (banEntry != null) {
            String fullBanMessage = banEntry.getReason();
            return extractReason(fullBanMessage);
        }
        return "No reason specified";
    }

    private String extractReason(String fullBanMessage) {
        if (fullBanMessage == null) {
            return "No reason specified";
        }
        String[] lines = fullBanMessage.split("\n");
        for (String line : lines) {
            if (line.startsWith("Reason: ")) {
                return line.substring("Reason: ".length()).trim();
            }
        }
        return "No reason specified";
    }

    public String getBanDuration(UUID playerUUID) {
        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
        BanEntry banEntry = banList.getBanEntry(Bukkit.getOfflinePlayer(playerUUID).getName());
        if (banEntry != null && banEntry.getExpiration() != null) {
            long remainingTimeMillis = banEntry.getExpiration().getTime() - System.currentTimeMillis();
            return formatDuration(remainingTimeMillis);
        }
        return "permanent";
    }
    public void recordUnban(UUID playerId, String unbanIssuer, String unbanReason) {
        String path = playerId.toString() + ".bans";
        List<Map<String, String>> bans = (List<Map<String, String>>) punishmentsData.getList(path, new ArrayList<>());

        if (!bans.isEmpty()) {
            Map<String, String> lastBan = bans.get(0);
            lastBan.put("unbanIssuer", unbanIssuer);
            lastBan.put("unbanReason", unbanReason);
            lastBan.put("unbannedOn", dateFormat.format(new Date()));


            punishmentsData.set(path, bans);
            savePunishmentsData();
        }
    }

    public void recordTempBan(UUID playerUUID, String issuer, String reason, String duration, Date issuedOn) {
        String path = playerUUID.toString() + ".bans";
        List<Map<String, String>> bans = (List<Map<String, String>>) punishmentsData.getList(path, new ArrayList<>());
        Map<String, String> banRecord = new HashMap<>();
        banRecord.put("issuer", issuer);
        banRecord.put("reason", reason);
        banRecord.put("duration", duration);
        banRecord.put("issuedOn", dateFormat.format(issuedOn)); // Format date here
        bans.add(0, banRecord);
        punishmentsData.set(path, bans);
        savePunishmentsData();
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public void recordBan(UUID playerUUID, String issuer, String reason, String duration, Date issuedOn) {
        String path = playerUUID.toString() + ".bans";
        List<Map<String, String>> bans = (List<Map<String, String>>) punishmentsData.getList(path, new ArrayList<>());
        Map<String, String> banRecord = new HashMap<>();
        banRecord.put("issuer", issuer);
        banRecord.put("reason", reason);
        banRecord.put("duration", duration);
        banRecord.put("issuedOn", dateFormat.format(issuedOn)); // Format date here
        bans.add(0, banRecord);
        punishmentsData.set(path, bans);
        savePunishmentsData();
    }

    public void recordKick(UUID playerUUID, String issuer, String reason, Date issuedOn) {
        String path = playerUUID.toString() + ".kicks";
        List<Map<String, String>> kicks = (List<Map<String, String>>) punishmentsData.getList(path, new ArrayList<>());
        Map<String, String> kickRecord = new HashMap<>();
        kickRecord.put("issuer", issuer);
        kickRecord.put("reason", reason);
        kickRecord.put("issuedOn", issuedOn.toString());
        kicks.add(0, kickRecord);
        punishmentsData.set(path, kicks);
        savePunishmentsData();
    }

    private long parseDuration(String duration) {
        long millis = 0;
        Pattern pattern = Pattern.compile("(\\d+)([dhms])");
        Matcher matcher = pattern.matcher(duration);
        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            switch (matcher.group(2)) {
                case "d": millis += value * 24 * 60 * 60 * 1000; break;
                case "h": millis += value * 60 * 60 * 1000; break;
                case "m": millis += value * 60 * 1000; break;
                case "s": millis += value * 1000; break;
            }
        }
        return millis;
    }

    public List<Map<String, String>> getKickHistory(UUID playerUUID) {
        return (List<Map<String, String>>) punishmentsData.getList(playerUUID.toString() + ".kicks", new ArrayList<>());
    }

    public List<Map<String, String>> getBanHistory(UUID playerUUID) {
        return (List<Map<String, String>>) punishmentsData.getList(playerUUID.toString() + ".bans", new ArrayList<>());
    }

    private void savePunishmentsData() {
        try {
            punishmentsData.save(punishmentsDataFile);
        } catch (IOException e) {
            e.printStackTrace();
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