package ru.ncti.modulebackend.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleDTO {
    private String day;
    private Long group;
    private Integer numberPair;
    private Long teacher;
    private Long subject;
    private String classroom;
}
