package me.clearedspore.easyStaff.command.Punishments.tabcompleter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TempBanTabCompleter implements TabCompleter {

    private final JavaPlugin plugin;

    public TempBanTabCompleter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                suggestions.add(player.getName());
            }
        } else if (args.length == 2 || args.length == 3) {

            File reasonsFile = new File(plugin.getDataFolder(), "punishments/reasons.yml");
            FileConfiguration reasonsConfig = YamlConfiguration.loadConfiguration(reasonsFile);

            if (reasonsConfig.contains("bans")) {
                suggestions.addAll(reasonsConfig.getConfigurationSection("bans").getKeys(false));
            }
        }

        return suggestions;
    }
}