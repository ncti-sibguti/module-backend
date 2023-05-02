package ru.ncti.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ncti.backend.entiny.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
}
