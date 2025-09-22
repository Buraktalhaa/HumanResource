package com.neg.technology.human.resource.person.model.enums;

public enum MaritalStatus {
    SINGLE,
    MARRIED;

    public static MaritalStatus fromString(String value) {
        if (value == null) return null;
        try {
            return MaritalStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return SINGLE;
        }
    }
}