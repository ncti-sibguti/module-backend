package ru.ncti.backend.dto.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentViewDTO {
    private String firstname;
    private String lastname;
    private String surname;
    private String email;
    private String group;
    private String speciality;
    private Integer course;

    @Getter
    @Setter
    @Builder
    public static class TeacherScheduleViewDTO {
        private Integer numberPair;
        private String subject;
        private String classroom;
        private List<String> groups;
    }
}
