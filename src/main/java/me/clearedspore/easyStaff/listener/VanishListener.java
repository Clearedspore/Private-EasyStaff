package me.clearedspore.easyStaff.listener;

import me.clearedspore.easyStaff.util.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.Plugin;

public class VanishListener implements Listener {
    private final Plugin plugin;
    private final VanishManager vanishManager;

    public VanishListener(Plugin plugin, VanishManager vanishManager) {
        this.plugin = plugin;
        this.vanishManager = vanishManager;
    }



    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joiningPlayer = event.getPlayer();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (vanishManager.isInVanish(onlinePlayer)) {
                if (!joiningPlayer.hasPermission("easystaff.seevanished")) {
                    joiningPlayer.hidePlayer(plugin, onlinePlayer);
                } else {
                    joiningPlayer.showPlayer(plugin, onlinePlayer);
                    onlinePlayer.setInvisible(false);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("vanished")) {
            if (plugin.getConfig().getBoolean("block-break") == false) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPvp(EntityDamageEvent event){
        Player player = (Player) event.getEntity();
        if (player.hasMetadata("vanished")) {
            if (plugin.getConfig().getBoolean("pvp")  == false) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        if (player.hasMetadata("vanished")) {
            if (plugin.getConfig().getBoolean("block-place") == false) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event){
        Player player = event.getPlayer();
        if (player.hasMetadata("vanished")) {
            if (plugin.getConfig().getBoolean("item-pickup") == false) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        if (player.hasMetadata("vanished")) {
            if (plugin.getConfig().getBoolean("item-drop") == false) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(InventoryInteractEvent event){
        Player player = (Player) event.getWhoClicked();
        if (player.hasMetadata("vanished")) {
            if (plugin.getConfig().getBoolean("inventory-interact") == false) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("vanished")) {
            Action action = event.getAction();
            Material clickedBlock = event.getClickedBlock() != null ? event.getClickedBlock().getType() : null;


            if (action == Action.PHYSICAL || (action == Action.RIGHT_CLICK_BLOCK && isInteractableBlock(clickedBlock))) {
                event.setCancelled(true);
            }
        }
    }
    private boolean isInteractableBlock(Material material) {
        if (material == null) return false;
        String name = material.name();
        return name.endsWith("PRESSURE_PLATE") || name.endsWith("DOOR") || name.endsWith("BUTTON") || name.startsWith("TRIPWIRE") ||
                name.endsWith("LEVER") || name.endsWith("GATE") || name.endsWith("TRAPDOOR") || name.endsWith("FENCE_GATE") ||
                name.endsWith("CHEST") || name.endsWith("SHULKER_BOX") || name.endsWith("BARREL") || name.endsWith("ANVIL") ||
                name.endsWith("FURNACE") || name.endsWith("DISPENSER") || name.endsWith("DROPPER") || name.endsWith("HOPPER") ||
                name.endsWith("BREWING_STAND") || name.endsWith("ENCHANTING_TABLE") || name.endsWith("CRAFTING_TABLE") ||
                name.endsWith("LECTERN") || name.endsWith("BELL") || name.endsWith("JUKEBOX") || name.endsWith("NOTE_BLOCK") ||
                name.endsWith("COMPOSTER") || name.endsWith("CARTOGRAPHY_TABLE") || name.endsWith("GRINDSTONE") ||
                name.endsWith("LOOM") || name.endsWith("SMITHING_TABLE") || name.endsWith("STONECUTTER") ||
                name.endsWith("BEACON") || name.endsWith("DAYLIGHT_DETECTOR") || name.endsWith("COMMAND_BLOCK") ||
                name.endsWith("STRUCTURE_BLOCK") || name.endsWith("JIGSAW") || name.endsWith("REPEATER") || name.endsWith("COMPARATOR") ||
                name.startsWith("TRIPWIRE") || name.startsWith("BUTTON") || name.startsWith("DOOR") || name.startsWith("LEVER") ||
                name.startsWith("GATE") || name.startsWith("TRAPDOOR") || name.startsWith("FENCE_GATE");
    }

}