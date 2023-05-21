package ru.ncti.backend.dto.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherViewDTO {
    private String firstname;
    private String lastname;
    private String surname;
    private String email;
}
