package net.orderzone.idcard.repository;

import net.orderzone.idcard.model.Profile;
import net.orderzone.idcard.model.ProfileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository layer tests using @DataJpaTest with H2 in-memory DB.
 * Validates all custom query methods against real SQL.
 */
@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.generate-ddl=true",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@DisplayName("ProfileRepository Integration Tests")
class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;

    private Profile student;
    private Profile employee;

    @BeforeEach
    void setUp() {
        profileRepository.deleteAll();

        student = Profile.builder()
                .uuid(UUID.randomUUID().toString())
                .registrationNumber("2026-STU-001")
                .type(ProfileType.STUDENT)
                .fullName("Alice Smith")
                .department("Computer Science")
                .title("Bachelor Year 3")
                .email("alice@example.com")
                .phone("+855-12-000-001")
                .dateOfBirth(LocalDate.of(2002, 1, 15))
                .build();

        employee = Profile.builder()
                .uuid(UUID.randomUUID().toString())
                .registrationNumber("2026-EMP-001")
                .type(ProfileType.EMPLOYEE)
                .fullName("Bob Jones")
                .department("Engineering")
                .title("Senior Developer")
                .email("bob@example.com")
                .phone("+855-23-000-002")
                .dateOfBirth(LocalDate.of(1985, 6, 20))
                .build();

        profileRepository.saveAll(List.of(student, employee));
    }

    @Test
    @DisplayName("findByEmail: finds profile by exact email")
    void findByEmail_Found() {
        Optional<Profile> result = profileRepository.findByEmail("alice@example.com");
        assertThat(result).isPresent();
        assertThat(result.get().getFullName()).isEqualTo("Alice Smith");
    }

    @Test
    @DisplayName("findByEmail: empty for unknown email")
    void findByEmail_NotFound() {
        assertThat(profileRepository.findByEmail("nobody@example.com")).isEmpty();
    }

    @Test
    @DisplayName("existsByEmail: true when email exists")
    void existsByEmail_True() {
        assertThat(profileRepository.existsByEmail("bob@example.com")).isTrue();
    }

    @Test
    @DisplayName("existsByEmail: false for unknown email")
    void existsByEmail_False() {
        assertThat(profileRepository.existsByEmail("ghost@example.com")).isFalse();
    }

    @Test
    @DisplayName("findByRegistrationNumber: returns correct profile")
    void findByRegistrationNumber_Found() {
        Optional<Profile> result = profileRepository.findByRegistrationNumber("2026-STU-001");
        assertThat(result).isPresent();
        assertThat(result.get().getType()).isEqualTo(ProfileType.STUDENT);
    }

    @Test
    @DisplayName("findByType: filters correctly by ProfileType")
    void findByType_Filtered() {
        List<Profile> students = profileRepository.findByType(ProfileType.STUDENT);
        assertThat(students).hasSize(1);
        assertThat(students.get(0).getDepartment()).isEqualTo("Computer Science");
    }

    @Test
    @DisplayName("searchByFullName: case-insensitive partial match")
    void searchByFullName_CaseInsensitive() {
        List<Profile> results = profileRepository.searchByFullName("alice");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Bachelor Year 3");
    }

    @Test
    @DisplayName("countByType: counts correctly")
    void countByType_Correct() {
        assertThat(profileRepository.countByType(ProfileType.STUDENT)).isEqualTo(1);
        assertThat(profileRepository.countByType(ProfileType.EMPLOYEE)).isEqualTo(1);
        assertThat(profileRepository.countByType(ProfileType.USER)).isZero();
    }
}
