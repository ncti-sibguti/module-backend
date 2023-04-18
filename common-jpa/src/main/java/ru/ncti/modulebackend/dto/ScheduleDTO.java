package ru.ncti.modulebackend.dto;

public class ScheduleDTO {
    private String day;
    private Long group;
    private Integer numberPair;
    private Long teacher;
    private Long subject;
    private String classroom;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Long getGroup() {
        return group;
    }

    public void setGroup(Long group) {
        this.group = group;
    }

    public Integer getNumberPair() {
        return numberPair;
    }

    public void setNumberPair(Integer numberPair) {
        this.numberPair = numberPair;
    }

    public Long getTeacher() {
        return teacher;
    }

    public void setTeacher(Long teacher) {
        this.teacher = teacher;
    }

    public Long getSubject() {
        return subject;
    }

    public void setSubject(Long subject) {
        this.subject = subject;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }
}
