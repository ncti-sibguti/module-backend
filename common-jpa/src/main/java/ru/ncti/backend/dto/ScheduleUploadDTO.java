package ru.ncti.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleUploadDTO {
    private String day;
    private String group;
    private String subject;
    private String teacher;
    private Integer numberPair;
    private Integer weekType;
    private String classroom;
}
