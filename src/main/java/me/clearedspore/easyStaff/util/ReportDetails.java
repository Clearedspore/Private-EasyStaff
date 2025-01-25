package me.clearedspore.easyStaff.util;

import java.time.Instant;

public class ReportDetails {
    private final String reporterName;
    private final String suspectName;
    private final String reason;
    private final Instant creationTime;

    public ReportDetails(String reporterName, String suspectName, String reason) {
        this.reporterName = reporterName;
        this.suspectName = suspectName;
        this.reason = reason;
        this.creationTime = Instant.now();
    }

    public String getReporterName() {
        return reporterName;
    }

    public String getSuspectName() {
        return suspectName;
    }

    public String getReason() {
        return reason;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    @Override
    public String toString() {
        return reporterName + ":" + suspectName + ":" + reason;
    }
}
