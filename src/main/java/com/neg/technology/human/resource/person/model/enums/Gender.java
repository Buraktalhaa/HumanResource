package com.neg.technology.human.resource.person.model.enums;

public enum Gender {
    MALE,
    FEMALE,
    OTHER;

        public static Gender fromString(String value) {
        if (value == null) return null;
        try {
            return Gender.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}
