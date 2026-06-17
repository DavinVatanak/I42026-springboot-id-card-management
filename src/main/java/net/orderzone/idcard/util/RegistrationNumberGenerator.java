package net.orderzone.idcard.util;

import net.orderzone.idcard.model.ProfileType;
import net.orderzone.idcard.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Year;

/**
 * Generates unique, formatted registration numbers.
 *
 * Format:
 *   STUDENT  → YYYY-STU-### (e.g. 2026-STU-001)
 *   EMPLOYEE → YYYY-EMP-### (e.g. 2026-EMP-001)
 *   USER     → YYYY-USR-### (e.g. 2026-USR-001)
 *
 * Uniqueness guaranteed via DB check loop.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationNumberGenerator {

    private final ProfileRepository profileRepository;

    public String generate(ProfileType profileType) {
        String year   = String.valueOf(Year.now().getValue());
        String prefix = resolvePrefix(profileType);

        long baseCount = profileRepository.countByType(profileType) + 1;
        String candidate;

        do {
            candidate = String.format("%s-%s-%03d", year, prefix, baseCount);
            baseCount++;
        } while (profileRepository.existsByRegistrationNumber(candidate));

        log.debug("Generated registration number: {}", candidate);
        return candidate;
    }

    private String resolvePrefix(ProfileType profileType) {
        return switch (profileType) {
            case STUDENT  -> "STU";
            case EMPLOYEE -> "EMP";
            case USER     -> "USR";
        };
    }
}
