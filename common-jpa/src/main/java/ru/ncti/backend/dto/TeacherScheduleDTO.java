package ru.ncti.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TeacherScheduleDTO {
    private String firstname;
    private String lastname;
    private String surname;
}
