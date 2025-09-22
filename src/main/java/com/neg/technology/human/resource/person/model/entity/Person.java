package com.neg.technology.human.resource.person.model.entity;

import com.neg.technology.human.resource.person.model.enums.Gender;
import com.neg.technology.human.resource.person.model.enums.MaritalStatus;
import com.neg.technology.human.resource.utility.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "person")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Person extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "national_id", length = 20)
    private String nationalId;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(columnDefinition = "TEXT")
    private String email;

    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "marital_status")
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;
}
