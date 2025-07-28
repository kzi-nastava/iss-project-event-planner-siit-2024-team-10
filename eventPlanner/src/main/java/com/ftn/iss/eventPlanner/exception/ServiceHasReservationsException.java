package com.ftn.iss.eventPlanner.exception;

public class ServiceHasReservationsException extends RuntimeException {
    public ServiceHasReservationsException(String message) {
        super(message);
    }
}
