package com.idcard.util;

import com.idcard.model.ProfileType;
import com.idcard.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Year;

/**
 * Utility component for generating unique, formatted registration numbers.
 *
 * Format:
 *   STUDENT  → YYYY-STU-###  (e.g., 2026-STU-001)
 *   EMPLOYEE → YYYY-EMP-###  (e.g., 2026-EMP-001)
 *   USER     → YYYY-USR-###  (e.g., 2026-USR-001)
 *
 * Uniqueness is guaranteed by checking the database on each generation
 * and incrementing the counter until a free slot is found.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationNumberGenerator {

    private final ProfileRepository profileRepository;

    /**
     * Generates the next unique registration number for the given profile type.
     *
     * @param profileType the type of profile (STUDENT, EMPLOYEE, USER)
     * @return a unique registration number string
     */
    public String generate(ProfileType profileType) {
        String year = String.valueOf(Year.now().getValue());
        String prefix = resolvePrefix(profileType);

        // Start counter from current count + 1 to avoid collision in concurrent saves
        long baseCount = profileRepository.countByProfileType(profileType) + 1;

        // Keep incrementing until we find an unused number
        String candidate;
        do {
            candidate = String.format("%s-%s-%03d", year, prefix, baseCount);
            baseCount++;
        } while (profileRepository.existsByRegistrationNumber(candidate));

        log.debug("Generated registration number: {}", candidate);
        return candidate;
    }

    /**
     * Maps a ProfileType to its 3-letter code segment.
     */
    private String resolvePrefix(ProfileType profileType) {
        return switch (profileType) {
            case STUDENT  -> "STU";
            case EMPLOYEE -> "EMP";
            case USER     -> "USR";
        };
    }
}
