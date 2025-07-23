package com.ftn.iss.eventPlanner.exception;

public class ReportAlreadySentException extends RuntimeException {
    public ReportAlreadySentException(String message) {
        super(message);
    }
}
