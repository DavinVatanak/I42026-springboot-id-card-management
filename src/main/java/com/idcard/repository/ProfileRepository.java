package com.idcard.repository;

import com.idcard.model.Profile;
import com.idcard.model.ProfileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for Profile entity.
 * Provides standard CRUD plus custom query methods.
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByEmail(String email);

    Optional<Profile> findByRegistrationNumber(String registrationNumber);

    boolean existsByEmail(String email);

    boolean existsByRegistrationNumber(String registrationNumber);

    List<Profile> findByProfileType(ProfileType profileType);

    /**
     * Full-text search on fullName (case-insensitive).
     */
    @Query("SELECT p FROM Profile p WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Profile> searchByName(@Param("name") String name);

    /**
     * Count profiles by type — used for registration number generation.
     */
    long countByProfileType(ProfileType profileType);
}
