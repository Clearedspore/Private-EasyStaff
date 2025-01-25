package me.clearedspore.easyStaff.command.Freeze;

import me.clearedspore.command.settings.SettingsManager;
import me.clearedspore.configFile.MessagesFile;
import me.clearedspore.feature.Logs.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.clearedspore.EasyCommands.Frozen;

public class FreezeCommand implements CommandExecutor {

    private final SettingsManager settingsManager;
    private boolean isEasyCommandsInstalled() {
        return Bukkit.getPluginManager().isPluginEnabled("EasyCommands");
    }


    public FreezeCommand(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player p){
                if(args.length == 0){
                    p.sendMessage(ChatColor.RED + "Please provide a player name!");
                }

                String playername = args[0];

                Player target = Bukkit.getServer().getPlayerExact(playername);
                if(Frozen.contains(target)){
                    target.setInvulnerable(false);
                    target.setGlowing(false);
                    Frozen.remove(target);
                    if(isEasyCommandsInstalled()) {
                        LogManager.getInstance().log(p.getUniqueId(), ChatColor.YELLOW + p.getName() + ChatColor.WHITE + " has unfreezed " + target.getDisplayName());
                    }
                    p.sendMessage("§9[Staff] §fYou have unfreezed " + target.getName());
                    for (Player online : Bukkit.getOnlinePlayers()){
                        if(settingsManager.isLogEnabled(p)) {
                            if (online.hasPermission("easycommands.logs") || online.hasPermission("easystaff.logs")) {
                                online.sendMessage(ChatColor.GRAY + "[" + p.getDisplayName() + " has unfreezed " + target.getDisplayName() + "]");
                            }
                        }
                    }

                }else {
                    Frozen.add(target);

                    if(isEasyCommandsInstalled()) {
                        LogManager.getInstance().log(p.getUniqueId(), ChatColor.YELLOW + p.getName() + ChatColor.WHITE + " has freezed " + target.getDisplayName());
                    }
                    p.sendMessage("§9[Staff] §fYou have freezed " + target.getName());
                    target.sendMessage("§cYou have been frozen do not log out!!");
                    target.sendMessage("§cYou have been frozen do not log out!!");
                    target.sendMessage("§cYou have been frozen do not log out!!");

                    for (Player online : Bukkit.getOnlinePlayers()){
                        if(settingsManager.isLogEnabled(online)) {
                            if (online.hasPermission("easycommands.logs") || online.hasPermission("easystaff.logs")) {
                                online.sendMessage(ChatColor.GRAY + "[" + p.getDisplayName() + " has frozen " + target.getDisplayName() + "]");
                            }
                        }
                    }
                }
            }
        return true;
    }
}
