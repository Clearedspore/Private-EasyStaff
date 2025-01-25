package me.clearedspore.easyStaff.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

public class PlayerIPListener implements Listener {

    private final HashMap<UUID, String> playerIPs = new HashMap<>();
    private final File ipFile = new File("plugins/EasyStaff/storage/playerIPs.txt");

    public PlayerIPListener() {
        loadIPs();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        String playerIP = event.getPlayer().getAddress().getAddress().getHostAddress();
        playerIPs.put(playerUUID, playerIP);
        saveIPs();
    }

    public String getLastKnownIP(UUID playerUUID) {
        return playerIPs.get(playerUUID);
    }

    private void loadIPs() {
        if (!ipFile.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(ipFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    playerIPs.put(UUID.fromString(parts[0]), parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveIPs() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ipFile))) {
            for (UUID uuid : playerIPs.keySet()) {
                writer.write(uuid.toString() + " " + playerIPs.get(uuid));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}