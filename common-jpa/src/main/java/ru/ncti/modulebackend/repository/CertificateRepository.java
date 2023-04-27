package ru.ncti.modulebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ncti.modulebackend.entiny.Certificate;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
}
