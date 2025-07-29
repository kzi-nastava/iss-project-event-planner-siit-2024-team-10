package com.ftn.iss.eventPlanner.exception;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
