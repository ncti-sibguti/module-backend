package ru.ncti.modulebackend.entiny;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "shedule")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String day;

    @OneToOne
    private Subject firstSubject;
    @OneToOne
    private Subject secondSubject;
    @OneToOne
    private Subject threthSubject;
    @OneToOne
    private Subject fourthSubject;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "group_id")
    private Group group;


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Subject getFirstSubject() {
        return firstSubject;
    }

    public void setFirstSubject(Subject firstSubject) {
        this.firstSubject = firstSubject;
    }

    public Subject getSecondSubject() {
        return secondSubject;
    }

    public void setSecondSubject(Subject secondSubject) {
        this.secondSubject = secondSubject;
    }

    public Subject getThrethSubject() {
        return threthSubject;
    }

    public void setThrethSubject(Subject threthSubject) {
        this.threthSubject = threthSubject;
    }

    public Subject getForthSubject() {
        return fourthSubject;
    }

    public void setForthSubject(Subject forthSubject) {
        this.fourthSubject = forthSubject;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
