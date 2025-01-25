package me.clearedspore.easyStaff.command.Punishments;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HistoryMenuListener implements Listener {

    private final PunishmentsManager punishmentsManager;

    public HistoryMenuListener(PunishmentsManager punishmentsManager) {
        this.punishmentsManager = punishmentsManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.startsWith("History: ")) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null) return;

        String playerName = title.substring(9);
        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);

        switch (clickedItem.getType()) {
            case RED_CARPET:
                player.sendMessage(ChatColor.GREEN + "Opening Ban History...");
                openHistoryMenu(player, playerName, punishmentsManager.getBanHistory(target.getUniqueId()), "Ban History");
                break;
            case YELLOW_CARPET:
                player.sendMessage(ChatColor.GREEN + "Opening Kick History...");
                openHistoryMenu(player, playerName, punishmentsManager.getKickHistory(target.getUniqueId()), "Kick History");
                break;
        }
    }

    private void openHistoryMenu(Player player, String playerName, List<Map<String, String>> history, String historyType) {
        Inventory historyMenu = Bukkit.createInventory(null, 36, historyType + ": " + playerName);
        setupHistoryMenu(historyMenu, history);
        player.openInventory(historyMenu);
    }

    private void setupHistoryMenu(Inventory menu, List<Map<String, String>> history) {
        for (int i = 0; i < 9; i++) {
            if (i == 0 || i == 8) {
                ItemStack arrow = new ItemStack(Material.ARROW);
                ItemMeta arrowMeta = arrow.getItemMeta();
                arrowMeta.setDisplayName(i == 0 ? ChatColor.YELLOW + "Previous Page" : ChatColor.YELLOW + "Next Page");
                arrow.setItemMeta(arrowMeta);
                menu.setItem(i, arrow);
            } else {
                ItemStack grayCarpet = new ItemStack(Material.GRAY_CARPET);
                menu.setItem(i, grayCarpet);
            }
        }

        int startIndex = 9;
        for (int i = 0; i < history.size(); i++) {
            Map<String, String> record = history.get(i);
            Material woolColor = determineWoolColor(record);
            String displayName = ChatColor.GREEN + "Record #" + (i + 1);
            List<String> lore = formatLore(record);

            ItemStack recordItem = new ItemStack(woolColor);
            ItemMeta recordMeta = recordItem.getItemMeta();
            recordMeta.setDisplayName(displayName);
            recordMeta.setLore(lore);
            recordItem.setItemMeta(recordMeta);
            menu.setItem(startIndex + i, recordItem);
        }
    }

    private Material determineWoolColor(Map<String, String> record) {
        String duration = record.get("duration");
        if (duration == null || record.containsKey("unbanIssuer")) {
            return Material.RED_WOOL;
        }

        try {
            Date issuedOn = punishmentsManager.getDateFormat().parse(record.get("issuedOn"));
            long banDuration = parseDuration(duration);
            if (banDuration > 0 && new Date().after(new Date(issuedOn.getTime() + banDuration))) {
                return Material.RED_WOOL;
            } else {
                return Material.LIME_WOOL;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return Material.RED_WOOL;
        }
    }

    private List<String> formatLore(Map<String, String> record) {
        List<String> lore = new ArrayList<>();
        String issuer = record.get("issuer");
        String issuedOnStr = record.get("issuedOn");
        String reason = record.get("reason");

        lore.add(ChatColor.YELLOW + "Punisher: " + ChatColor.WHITE + issuer);
        lore.add(ChatColor.YELLOW + "Issued on: " + ChatColor.WHITE + issuedOnStr);
        lore.add(ChatColor.YELLOW + "Reason: " + ChatColor.WHITE + reason);

        String duration = record.get("duration");
        String remaining = "expired";

        try {
            Date issuedOn = punishmentsManager.getDateFormat().parse(issuedOnStr);
            long banDuration = parseDuration(duration);
            if (banDuration < 0) {
                remaining = "permanent";
            } else if (new Date().before(new Date(issuedOn.getTime() + banDuration))) {
                remaining = formatDuration(new Date(issuedOn.getTime() + banDuration).getTime() - System.currentTimeMillis());
            }
        } catch (ParseException e) {
            e.printStackTrace();
            remaining = "unknown";
        }

        lore.add(ChatColor.YELLOW + "Remaining: " + ChatColor.WHITE + remaining);

        if (record.containsKey("unbanIssuer")) {
            String unbanIssuer = record.get("unbanIssuer");
            String unbanReason = record.get("unbanReason");
            lore.add("");
            lore.add(ChatColor.YELLOW + "Removal issuer: " + ChatColor.WHITE + unbanIssuer);
            lore.add(ChatColor.YELLOW + "Removal reason: " + ChatColor.WHITE + unbanReason);
        } else if (remaining.equals("expired")) {
            lore.add("");
            lore.add(ChatColor.YELLOW + "Removal issuer: " + ChatColor.WHITE + "CONSOLE");
            lore.add(ChatColor.YELLOW + "Removal reason: " + ChatColor.WHITE + "expired");
        }

        return lore;
    }

    private long parseDuration(String duration) {
        if (duration.equalsIgnoreCase("permanent")) {
            return -1;
        }
        long time = 0;
        String[] parts = duration.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        for (int i = 0; i < parts.length; i += 2) {
            int value = Integer.parseInt(parts[i]);
            char unit = parts[i + 1].charAt(0);
            switch (unit) {
                case 'd':
                    time += value * 86400000L;
                    break;
                case 'h':
                    time += value * 3600000L;
                    break;
                case 'm':
                    time += value * 60000L;
                    break;
                case 's':
                    time += value * 1000L;
                    break;
            }
        }
        return time;
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