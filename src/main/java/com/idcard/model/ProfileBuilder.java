package com.idcard.model;

import java.time.LocalDate;

/**
 * Builder Design Pattern implementation for constructing Profile objects.
 * Provides a fluent API for creating profiles with defaults pre-applied.
 *
 * Usage:
 *   Profile profile = ProfileBuilder.builder()
 *       .fullName("John Doe")
 *       .email("john@gmail.com")
 *       .profileType(ProfileType.STUDENT)
 *       .build();
 */
public class ProfileBuilder {

    private String fullName;
    private String email;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private String photoPath;
    private ProfileType profileType;

    // Private constructor – use static builder() factory
    private ProfileBuilder() {}

    /**
     * Creates a new ProfileBuilder instance.
     */
    public static ProfileBuilder builder() {
        return new ProfileBuilder();
    }

    public ProfileBuilder fullName(String fullName) {
        this.fullName = fullName;
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

    public ProfileBuilder address(String address) {
        this.address = address;
        return this;
    }

    public ProfileBuilder dateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public ProfileBuilder photoPath(String photoPath) {
        this.photoPath = photoPath;
        return this;
    }

    public ProfileBuilder profileType(ProfileType profileType) {
        this.profileType = profileType;
        return this;
    }

    /**
     * Constructs a Profile instance.
     * Note: registrationNumber is set by the service layer after persistence.
     *
     * @return a new Profile entity (not yet persisted)
     * @throws IllegalStateException if required fields are missing
     */
    public Profile build() {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalStateException("fullName is required");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("email is required");
        }
        if (profileType == null) {
            throw new IllegalStateException("profileType is required");
        }

        return Profile.builder()
                .fullName(fullName)
                .email(email)
                .phone(phone)
                .address(address)
                .dateOfBirth(dateOfBirth)
                .photoPath(photoPath)
                .profileType(profileType)
                .build();
    }
}
