package com.neg.technology.human.resource.leave.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Gender {
    MALE,
    FEMALE,
    NONE;

    @JsonCreator
    public static Gender fromString(String value) {
        if (value == null) return null;
        return Gender.valueOf(value.trim().toUpperCase());
    }
}