package net.orderzone.idcard.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Builder Design Pattern for constructing Profile objects with sensible defaults.
 *
 * Usage:
 *   Profile profile = ProfileBuilder.builder()
 *       .fullName("John Doe")
 *       .email("john@gmail.com")
 *       .type(ProfileType.STUDENT)
 *       .build();
 */
public class ProfileBuilder {

    private ProfileType type;
    private String fullName;
    private String department;
    private String title;
    private String email;
    private String phone;
    private String bloodGroup;
    private LocalDate dateOfBirth;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private Template template;
    private BarcodeType barcodeType = BarcodeType.CODE_128;

    private ProfileBuilder() {}

    public static ProfileBuilder builder() {
        return new ProfileBuilder();
    }

    public ProfileBuilder type(ProfileType type) {
        this.type = type;
        return this;
    }

    public ProfileBuilder fullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public ProfileBuilder department(String department) {
        this.department = department;
        return this;
    }

    public ProfileBuilder title(String title) {
        this.title = title;
        return this;
    }

    public ProfileBuilder email(String email) {
        this.email = email;
        return this;
    }

    public ProfileBuilder phone(String phone) {
        this.phone = phone;
        return this;
    }

    public ProfileBuilder bloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
        return this;
    }

    public ProfileBuilder dateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public ProfileBuilder issueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
        return this;
    }

    public ProfileBuilder expiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }

    public ProfileBuilder template(Template template) {
        this.template = template;
        return this;
    }

    public ProfileBuilder barcodeType(BarcodeType barcodeType) {
        this.barcodeType = barcodeType;
        return this;
    }

    /**
     * Builds the Profile entity.
     * Generates a UUID automatically.
     * registrationNumber must be set by the service layer after construction.
     *
     * @throws IllegalStateException if required fields are missing
     */
    public Profile build() {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalStateException("fullName is required");
        }
        if (type == null) {
            throw new IllegalStateException("type (ProfileType) is required");
        }

        return Profile.builder()
                .uuid(UUID.randomUUID().toString())
                .type(type)
                .fullName(fullName)
                .department(department)
                .title(title)
                .email(email)
                .phone(phone)
                .bloodGroup(bloodGroup)
                .dateOfBirth(dateOfBirth)
                .issueDate(issueDate)
                .expiryDate(expiryDate)
                .template(template)
                .barcodeType(barcodeType != null ? barcodeType : BarcodeType.CODE_128)
                .build();
    }
}
