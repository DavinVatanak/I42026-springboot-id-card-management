package com.idcard.repository;

import com.idcard.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for Template entity.
 */
@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    Optional<Template> findByName(String name);

    boolean existsByName(String name);

    /**
     * Returns all active templates.
     */
    List<Template> findByActiveTrue();
}
