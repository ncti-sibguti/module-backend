package ru.ncti.modulebackend.entiny;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
public class Teacher extends User {

    @OneToMany(mappedBy = "teacher")
    private Set<Subject> subject;


    public Set<Subject> getSubject() {
        return subject;
    }

    public void setSubject(Set<Subject> subject) {
        this.subject = subject;
    }
}
