package com.neg.technology.human.resource.leave.model.enums;


import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;


public enum LeaveStatus {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    CANCELLED("CANCELLED");

    private final String status;

    LeaveStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    // String değerden enum'u bulmak için yeni statik metot
    @JsonCreator
    public static LeaveStatus fromString(String statusText) {
        return Stream.of(LeaveStatus.values())
                .filter(leaveStatus -> leaveStatus.getStatus().equalsIgnoreCase(statusText))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown leave status: " + statusText));
    }
}