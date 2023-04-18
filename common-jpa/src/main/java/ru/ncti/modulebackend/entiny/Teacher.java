package ru.ncti.modulebackend.entiny;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
public class Teacher extends User {

    @OneToMany(mappedBy = "teacher")
    @JsonIgnore
    private Set<Schedule> schedules;

    public Set<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(Set<Schedule> schedules) {
        this.schedules = schedules;
    }
}
