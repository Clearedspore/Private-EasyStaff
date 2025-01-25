package me.clearedspore.easyStaff.command.Report;

import me.clearedspore.easyStaff.util.ReportManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetReportsCommand implements CommandExecutor {
    private final ReportManager reportManager;

    public GetReportsCommand(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p) {
            if (args.length == 0) {
                reportManager.getviewreportsGUI().openReportGUI(p, 0);
                return true;
            }
            if (args.length >= 2) {
                String action = args[0].toLowerCase();
                StringBuilder reportNameBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    reportNameBuilder.append(args[i]);
                    if (i < args.length - 1) {
                        reportNameBuilder.append(" ");
                    }
                }
                String reportName = reportNameBuilder.toString();

                switch (action) {
                    case "accept":
                        reportManager.acceptReport(p, reportName);
                        break;
                    case "deny":
                        reportManager.denyReport(p, reportName);
                        break;
                    default:
                        p.sendMessage("Invalid action. Use 'accept' or 'deny'.");
                        return true;
                }
            }
        }
        return true;
    }
}
