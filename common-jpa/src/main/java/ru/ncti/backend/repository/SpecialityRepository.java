package ru.ncti.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ncti.backend.entiny.Speciality;

@Repository
public interface SpecialityRepository extends JpaRepository<Speciality, Long> {
}
