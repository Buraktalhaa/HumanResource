package com.neg.technology.human.resource.person.model.converter;

import com.neg.technology.human.resource.leave.model.enums.Gender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender, String> {

    @Override
    public String convertToDatabaseColumn(Gender attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public Gender convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return Gender.valueOf(dbData.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return Gender.NONE; // default değer
        }
    }
}
