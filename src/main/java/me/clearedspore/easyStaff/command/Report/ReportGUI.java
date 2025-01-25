package me.clearedspore.easyStaff.command.Report;

import me.clearedspore.easyStaff.util.ReportManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class ReportGUI implements Listener {
    private ReportManager reportManager;
    private final FileConfiguration config;

    public ReportGUI(ReportManager reportManager, FileConfiguration config) {
        this.reportManager = reportManager;
        this.config = config;
    }

    public void openReportGUI(Player p, int page, OfflinePlayer target) {
        List<String> reasons = config.getStringList("reportreasons");
        int totalPages = (int) Math.ceil((double) reasons.size() / 27);

        page = Math.max(0, Math.min(page, totalPages - 1));

        Inventory gui = Bukkit.createInventory(null, 36, ChatColor.BLUE + "Report " + target.getName());

        int startIndex = page * 27;
        int endIndex = Math.min(startIndex + 27, reasons.size());
        for (int i = startIndex, slot = 0; i < endIndex; i++, slot++) {
            String reason = reasons.get(i);
            ItemStack reasonItem = new ItemStack(Material.PAPER);
            ItemMeta meta = reasonItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.YELLOW + reason);
                reasonItem.setItemMeta(meta);
            }


            gui.setItem(i, reasonItem);
        }


        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName(ChatColor.RED + "Close");
            closeButton.setItemMeta(closeMeta);
        }
        gui.setItem(31, closeButton);


        if (page > 0) {
            ItemStack previousButton = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = previousButton.getItemMeta();
            if (prevMeta != null) {
                prevMeta.setDisplayName(ChatColor.BLUE + "Previous Page");
                prevMeta.setLore(Collections.singletonList(ChatColor.WHITE + "current page " + page));
                previousButton.setItemMeta(prevMeta);
            }
            gui.setItem(27, previousButton);
        }


        if (page < totalPages - 1) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            if (nextMeta != null) {
                nextMeta.setDisplayName(ChatColor.BLUE + "Next Page");
                nextMeta.setLore(Collections.singletonList(ChatColor.WHITE + "Current page " + page));
                nextButton.setItemMeta(nextMeta);
            }
            gui.setItem(35, nextButton);
        }

        p.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;

        String title = ChatColor.stripColor(e.getView().getTitle());
        if (!title.startsWith("Report ")) return;

        e.setCancelled(true);

        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        String itemName = ChatColor.stripColor(meta.getDisplayName());

        if (clickedItem.getType() == Material.BARRIER) {
            p.closeInventory();
            return;
        }

        if (clickedItem.getType() == Material.ARROW && meta.hasLore()) {
            List<String> lore = meta.getLore();
            if (lore != null && !lore.isEmpty()) {
                String[] parts = ChatColor.stripColor(lore.get(0)).split(" ");
                if (parts.length >= 3) {
                    int currentPage = Integer.parseInt(parts[2]);
                    if (itemName.equalsIgnoreCase("Previous Page")) {
                        openReportGUI(p, currentPage - 1, Bukkit.getOfflinePlayer(title.substring(7)));
                    } else if (itemName.equalsIgnoreCase("Next Page")) {
                        openReportGUI(p, currentPage + 1, Bukkit.getOfflinePlayer(title.substring(7)));
                    }
                }
            }
            return;
        }

        if (clickedItem.getType() == Material.PAPER) {
            String reason = itemName;
            String targetPlayerName = title.substring(7);

            reportManager.reportPlayer(p, Bukkit.getOfflinePlayer(targetPlayerName), reason);
            p.sendMessage(ChatColor.GREEN + "Report submitted for " + targetPlayerName + " with reason: " + reason);
            p.closeInventory();
        }
    }
}
