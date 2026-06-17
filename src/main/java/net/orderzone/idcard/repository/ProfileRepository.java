package net.orderzone.idcard.repository;

import net.orderzone.idcard.model.Profile;
import net.orderzone.idcard.model.ProfileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for Profile entity.
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByEmail(String email);

    Optional<Profile> findByUuid(String uuid);

    Optional<Profile> findByRegistrationNumber(String registrationNumber);

    boolean existsByEmail(String email);

    boolean existsByRegistrationNumber(String registrationNumber);

    List<Profile> findByType(ProfileType type);

    /**
     * Full-text search on fullName (case-insensitive).
     */
    @Query("SELECT p FROM Profile p WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Profile> searchByFullName(@Param("name") String name);

    /**
     * Count profiles by type — used for registration number generation.
     */
    long countByType(ProfileType type);
}
