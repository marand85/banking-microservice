package com.example.microservice.atmservice.model;

public enum RequestType {
    FAILURE_RESTART(0),
    PRIORITY(1),
    SIGNAL_LOW(2),
    STANDARD(3);

    private final int priority;

    RequestType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
