package ru.ncti.modulebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ncti.modulebackend.entiny.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
}
