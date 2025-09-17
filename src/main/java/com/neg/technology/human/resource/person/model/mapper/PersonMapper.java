package com.neg.technology.human.resource.person.model.mapper;

import com.neg.technology.human.resource.person.model.entity.Person;
import com.neg.technology.human.resource.person.model.request.CreatePersonRequest;
import com.neg.technology.human.resource.person.model.request.UpdatePersonRequest;
import com.neg.technology.human.resource.person.model.response.PersonResponse;
import com.neg.technology.human.resource.leave.model.enums.Gender; // Import the correct Gender enum
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonMapper {

    public PersonResponse toResponse(Person person) {
        if (person == null) return null;

        // Convert Gender enum to String for the response DTO
        String genderString = (person.getGender() != null) ? person.getGender().name() : null;

        return new PersonResponse(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getNationalId(),
                person.getBirthDate(),
                genderString,
                person.getEmail(),
                person.getPhone(),
                person.getAddress(),
                person.getMaritalStatus()
        );
    }

    public List<PersonResponse> toResponseList(List<Person> persons) {
        if (persons == null || persons.isEmpty()) {
            return List.of();
        }
        return persons.stream()
                .map(this::toResponse)
                .toList();
    }

    public Person toEntity(CreatePersonRequest dto) {
        if (dto == null) return null;

        // Convert String to Gender enum for the entity
        Gender genderEnum = (dto.getGender() != null) ? Gender.valueOf(dto.getGender().toUpperCase()) : null;

        return Person.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .nationalId(dto.getNationalId())
                .birthDate(dto.getBirthDate())
                .gender(genderEnum)
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .maritalStatus(dto.getMaritalStatus())
                .build();
    }

    public void updateEntity(Person person, UpdatePersonRequest dto) {
        if (person == null || dto == null) return;

        if (dto.getFirstName() != null) person.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) person.setLastName(dto.getLastName());
        if (dto.getNationalId() != null) person.setNationalId(dto.getNationalId());
        if (dto.getBirthDate() != null) person.setBirthDate(dto.getBirthDate());

        // Convert String to Gender enum for the entity update
        if (dto.getGender() != null) {
            Gender genderEnum = Gender.valueOf(dto.getGender().toUpperCase());
            person.setGender(genderEnum);
        }

        if (dto.getEmail() != null) person.setEmail(dto.getEmail());
        if (dto.getPhone() != null) person.setPhone(dto.getPhone());
        if (dto.getAddress() != null) person.setAddress(dto.getAddress());
        if (dto.getMaritalStatus() != null) person.setMaritalStatus(dto.getMaritalStatus());
    }
}