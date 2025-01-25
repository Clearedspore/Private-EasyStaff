package me.clearedspore.easyStaff;

import me.clearedspore.command.settings.SettingsManager;
import me.clearedspore.easyStaff.command.AltsCommand;
import me.clearedspore.easyStaff.command.Chat.ChatMuteListener;
import me.clearedspore.easyStaff.command.Chat.ClearChatCommand;
import me.clearedspore.easyStaff.command.Chat.ToggleChatCommand;
import me.clearedspore.easyStaff.command.Chat.channels.ChannelCommand;
import me.clearedspore.easyStaff.command.Chat.channels.ChannelListener;
import me.clearedspore.easyStaff.command.EasyStaffHelp;
import me.clearedspore.easyStaff.command.Punishments.*;
import me.clearedspore.easyStaff.command.Punishments.punish.*;
import me.clearedspore.easyStaff.command.Punishments.tabcompleter.TempBanTabCompleter;
import me.clearedspore.easyStaff.command.Report.GetReportGUI;
import me.clearedspore.easyStaff.command.Report.ReportGUI;
import me.clearedspore.easyStaff.command.VanishCommand;
import me.clearedspore.easyStaff.listener.PlayerIPListener;
import me.clearedspore.easyStaff.listener.VanishListener;
import me.clearedspore.easyStaff.util.ChannelManager;
import me.clearedspore.easyStaff.command.Chat.channels.ChatChannel;
import me.clearedspore.easyStaff.command.Freeze.FreezeCommand;
import me.clearedspore.easyStaff.command.Freeze.FreezeListener;
import me.clearedspore.easyStaff.command.MaintenanceSection.MaintenanceCommand;
import me.clearedspore.easyStaff.command.MaintenanceSection.MaintenanceJoinListener;
import me.clearedspore.easyStaff.command.Report.GetReportsCommand;
import me.clearedspore.easyStaff.command.Report.ReportCommand;
import me.clearedspore.easyStaff.command.StaffHelpCommand;
import me.clearedspore.easyStaff.listener.JoinLeaveListener;
import me.clearedspore.easyStaff.util.MaintenanceManager;
import me.clearedspore.easyStaff.util.ReportManager;
import me.clearedspore.easyStaff.util.VanishManager;
import me.clearedspore.feature.Logs.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

public final class EasyStaff extends JavaPlugin {

    private SettingsManager settingsManager;
    private LogManager logManager;
    private MaintenanceManager maintenanceManager;
    private ReportManager reportManager;
    private ChannelManager channelManager;
    private PlayerIPListener playerIPListener;
    private VanishManager vanishManager;




    private void registerDynamicCommands() {
        Map<String, ChatChannel> channels = channelManager.getChannels();
        for (ChatChannel channel : channels.values()) {
            for (String command : channel.getCommands()) {
                ChannelCommand ChannelCommand = new ChannelCommand(channel, channelManager, vanishManager);
                registerCommand(command.substring(1), ChannelCommand);
            }
        }
    }

    public void registerCommand(String commandName, ChannelCommand executor) {
        try {
            Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getPluginManager());
            BukkitCommand command = new BukkitCommand(commandName) {
                @Override
                public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                    return executor.onCommand(sender, this, commandLabel, args);
                }
            };
            command.setDescription("Dynamic command for " + commandName);
            command.setPermission(executor.getChannel().getPermission());
            commandMap.register(getDescription().getName(), command);
            getLogger().info("Registered command: " + commandName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PunishmentsManager punishmentsManager;

    @Override
    public void onEnable() {
        createStorageDirectory();

        punishmentsManager = new PunishmentsManager(this);
        createPunishmentsDirectory();
        createReasonsFile();

        playerIPListener = new PlayerIPListener();

        settingsManager = new SettingsManager();
        logManager = new LogManager();
        maintenanceManager = new MaintenanceManager(this);

        reportManager = new ReportManager(this);
        GetReportGUI reportGUI = new GetReportGUI(reportManager);
        ReportGUI reportGUI1 = new ReportGUI(reportManager, getConfig());
        reportManager.setviewreportsGui(reportGUI);
        reportManager.setReportGUI(reportGUI1);

        channelManager = new ChannelManager(this);
        vanishManager = new VanishManager(this, channelManager);
        registerDynamicCommands();
        getConfig().options().copyDefaults();
        saveDefaultConfig();


        getCommand("chat-toggle").setExecutor(new ToggleChatCommand(this, settingsManager, logManager));
        getCommand("freeze").setExecutor(new FreezeCommand(settingsManager));
        getCommand("chat-toggle").setExecutor(new ToggleChatCommand(this, settingsManager, logManager));
        getCommand("clearchat").setExecutor(new ClearChatCommand());
        getCommand("staffhelp").setExecutor(new StaffHelpCommand(getConfig()));
        getCommand("easystaff").setExecutor(new EasyStaffHelp(this));
        getCommand("easystaff").setTabCompleter(new EasyStaffHelp(this));
        getCommand("alts").setExecutor(new AltsCommand(playerIPListener));
        getCommand("vanish").setExecutor(new VanishCommand(vanishManager));

        getCommand("report").setExecutor(new ReportCommand(reportManager, getConfig()));
        getCommand("report").setTabCompleter(new ReportCommand(reportManager, getConfig()));
        getCommand("reports").setExecutor(new GetReportsCommand(reportManager));
        getServer().getPluginManager().registerEvents(new ReportGUI(reportManager, getConfig()), this);
        getServer().getPluginManager().registerEvents(new GetReportGUI(reportManager), this);

        getCommand("kick").setExecutor(new KickCommand(punishmentsManager));
        getCommand("ban").setExecutor(new BanCommand(punishmentsManager, getConfig()));
        getCommand("tempban").setExecutor(new TempBanCommand(punishmentsManager, getConfig(), this));
        getCommand("tempban").setTabCompleter(new TempBanTabCompleter(this));
        getCommand("unban").setExecutor(new UnbanCommand(punishmentsManager));
        getCommand("history").setExecutor(new HistoryCommand(punishmentsManager));
        getCommand("ban").setTabCompleter(new TempBanTabCompleter(this));

        getServer().getPluginManager().registerEvents(new HistoryMenuListener(punishmentsManager), this);
        getServer().getPluginManager().registerEvents(new PlayerLoginListener(this, punishmentsManager), this);

        getCommand("maintenance").setExecutor(new MaintenanceCommand(this, maintenanceManager));
        getCommand("maintenance").setTabCompleter(new MaintenanceCommand(this, maintenanceManager));
        getServer().getPluginManager().registerEvents(new MaintenanceJoinListener(this, maintenanceManager), this);

        getServer().getPluginManager().registerEvents(new FreezeListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatMuteListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinLeaveListener(this), this);
        getServer().getPluginManager().registerEvents(new ChannelListener(channelManager), this);
        getServer().getPluginManager().registerEvents(new PlayerIPListener(), this);
        getServer().getPluginManager().registerEvents(new VanishListener(this, vanishManager), this);
    }


    private void createPunishmentsDirectory() {
        File punishmentsDir = new File(getDataFolder(), "punishments");
        if (!punishmentsDir.exists()) {
            punishmentsDir.mkdirs();
        }
    }

    private void createStorageDirectory(){
        File storageDir = new File(getDataFolder(), "storage");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
    }

    private void createReasonsFile() {
        File reasonsFile = new File(getDataFolder(), "punishments/reasons.yml");
        if (!reasonsFile.exists()) {
            getLogger().info("reasons.yml does not exist, attempting to save resource.");
            saveResource("punishments/reasons.yml", false);
            if (reasonsFile.exists()) {
                getLogger().info("reasons.yml successfully created.");
            } else {
                getLogger().warning("Failed to create reasons.yml.");
            }
        } else {
            getLogger().info("reasons.yml already exists.");
        }
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
