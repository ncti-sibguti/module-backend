package ru.ncti.backend.entiny;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
@Entity
public class Teacher extends User {

    @OneToMany(mappedBy = "teacher")
    @JsonIgnore
    private List<Schedule> schedules;
}
