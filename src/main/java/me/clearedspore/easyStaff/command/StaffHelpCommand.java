package me.clearedspore.easyStaff.command;

import me.clearedspore.configFile.MessagesFile;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StaffHelpCommand implements CommandExecutor {
    private final FileConfiguration config;
    private final Map<UUID, Long> lastReportTime = new HashMap<>();

    public StaffHelpCommand(FileConfiguration config) {
        this.config = config;
    }


    private boolean isEasyCommandsInstalled() {
        return Bukkit.getPluginManager().isPluginEnabled("EasyCommands");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player p){
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < args.length; i++) {
                        builder.append(args[i]);
                        builder.append(" ");
                    }
                    String MSG = builder.toString();
                    MSG = MSG.stripTrailing();

                     p.sendMessage("§9[Help] §fOnline staff has been notified and the will help you as soon as possible!");

                    for (Player online : Bukkit.getOnlinePlayers()) {
                        if (online.hasPermission("easystaff.staffhelp.notify")) {

                            TextComponent messageButton = new TextComponent("[Message]");
                            messageButton.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
                            messageButton.setBold(true);
                            messageButton.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + p.getName() + " "));

                            TextComponent TeleportButton = new TextComponent("[Teleport]");
                            TeleportButton.setColor(net.md_5.bungee.api.ChatColor.BLUE);
                            TeleportButton.setBold(true);
                            if(isEasyCommandsInstalled()) {
                                TeleportButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/easycommands:teleport " + p.getName()));
                            } else {
                                TeleportButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleport " + p.getName()));
                            }

                            String border = org.bukkit.ChatColor.BLUE + "=========================";
                            TextComponent message = new TextComponent();
                            message.addExtra(border + "\n");
                            message.addExtra(org.bukkit.ChatColor.WHITE + p.getName() + org.bukkit.ChatColor.BLUE + " has request staff!" + "\n");
                            message.addExtra(org.bukkit.ChatColor.BLUE + "Reason: " + org.bukkit.ChatColor.WHITE + MSG + "\n");
                            message.addExtra(messageButton);
                            message.addExtra(" ");
                            message.addExtra(TeleportButton);
                            message.addExtra("\n" + border);

                            online.spigot().sendMessage(message);
                        }
                    }
                }
        return true;
    }
}
