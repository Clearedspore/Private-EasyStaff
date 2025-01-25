package me.clearedspore.easyStaff.command.Report;

import me.clearedspore.easyStaff.util.ReportDetails;
import me.clearedspore.easyStaff.util.ReportManager;
import me.clearedspore.feature.Logs.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GetReportGUI implements Listener {
    private ReportManager reportManager;

    public GetReportGUI(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    public void openReportGUI(Player p, int page) {
        Map<String, ReportDetails> reports = reportManager.getReports(p);
        List<ReportDetails> reportList = new ArrayList<>(reports.values());
        int totalPages = (int) Math.ceil((double) reportList.size() / 27);

        page = Math.max(0, Math.min(page, totalPages - 1));

        Inventory gui = Bukkit.createInventory(null, 36, ChatColor.BLUE + "Reports");

        int startIndex = page * 27;
        int endIndex = Math.min(startIndex + 27, reportList.size());
        for (int i = startIndex, slot = 0; i < endIndex; i++, slot++) {
            ReportDetails report = reportList.get(i);
            ItemStack reportItem = new ItemStack(Material.PAPER);
            ItemMeta reportMeta = reportItem.getItemMeta();

            if (reportMeta != null) {
                reportMeta.setDisplayName(ChatColor.YELLOW + report.getSuspectName() + " - " + report.getReason());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.BLUE + "Reporter: " + ChatColor.WHITE + report.getReporterName());
                lore.add(ChatColor.BLUE + "Reason: " + ChatColor.WHITE +  report.getReason());
                String timeAgo = TimeUtils.formatTimeAgo(report.getCreationTime());
                lore.add(ChatColor.BLUE + "Created: " + ChatColor.WHITE + timeAgo);
                lore.add(ChatColor.GRAY + "");
                lore.add(ChatColor.YELLOW + "Left click to accept the report");
                lore.add(ChatColor.YELLOW + "Right click to deny the report");
                reportMeta.setLore(lore);
                reportItem.setItemMeta(reportMeta);
            }


            gui.setItem(slot, reportItem);
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

        if (!ChatColor.stripColor(e.getView().getTitle()).equalsIgnoreCase("reports")) return;

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
                        openReportGUI(p, currentPage - 1);
                    } else if (itemName.equalsIgnoreCase("Next Page")) {
                        openReportGUI(p, currentPage + 1);
                    }
                }
            }
            return;
        }

        if (clickedItem.getType() == Material.PAPER) {
            String[] parts = itemName.split(" - ");
            if (parts.length == 2) {
                String suspectName = parts[0];
                String reason = parts[1];
                String reportKey = suspectName + " - " + reason;

                if (e.isLeftClick()) {
                    reportManager.acceptReport(p, reportKey);
                } else if (e.isRightClick()) {
                    reportManager.denyReport(p, reportKey);
                }
                p.closeInventory();
            }
        }
    }
}

