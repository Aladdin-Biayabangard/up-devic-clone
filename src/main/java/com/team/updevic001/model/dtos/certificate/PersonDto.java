package com.team.updevic001.model.dtos.certificate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Embeddable
@Data
@FieldDefaults(level = PRIVATE)
public class PersonDto {

    @Column(name = "person_first_name")
    String firstName;

    @Column(name = "person_last_name")
    String lastName;

}
