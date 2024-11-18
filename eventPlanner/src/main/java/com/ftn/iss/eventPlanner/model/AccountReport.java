package com.ftn.iss.eventPlanner.model;

import java.time.LocalDateTime;

public class AccountReport {
    private int id;
    private String description;
    private LocalDateTime processingTimestamp;
    private Status status;
    private Account reporter;
    private Account reportee;

    public AccountReport() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getProcessingTimestamp() {
        return processingTimestamp;
    }

    public void setProcessingTimestamp(LocalDateTime processingTimestamp) {
        this.processingTimestamp = processingTimestamp;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Account getReporter() {
        return reporter;
    }

    public void setReporter(Account reporter) {
        this.reporter = reporter;
    }

    public Account getReportee() {
        return reportee;
    }

    public void setReportee(Account reportee) {
        this.reportee = reportee;
    }
}
