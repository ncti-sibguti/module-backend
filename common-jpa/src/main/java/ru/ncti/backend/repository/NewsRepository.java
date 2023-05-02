package ru.ncti.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ncti.backend.entiny.News;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
}
