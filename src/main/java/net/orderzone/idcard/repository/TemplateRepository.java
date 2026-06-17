package net.orderzone.idcard.repository;

import net.orderzone.idcard.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA Repository for Template entity.
 * Note: Template no longer has an 'active' flag — search by code or name.
 */
@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    Optional<Template> findByCode(String code);

    Optional<Template> findByName(String name);

    boolean existsByCode(String code);

    boolean existsByName(String name);
}
