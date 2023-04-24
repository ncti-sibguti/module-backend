package ru.ncti.modulebackend.entiny;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Teacher extends User {

    @OneToMany(mappedBy = "teacher")
    @JsonIgnore
    private List<Schedule> schedules;

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }
}
