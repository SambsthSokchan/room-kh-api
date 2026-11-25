package com.roomkh.dto.request;

import com.roomkh.enums.LanguageType;
import com.roomkh.enums.UserSegment;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    @NotBlank(message = "Full name is required")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 150)
    private String email;

    @Size(max = 20)
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    private String password;

    // Optional: if provided, must match password
    private String confirmPassword;

    @NotNull(message = "User segment is required (STUDENT, WORKER, or OTHER)")
    private UserSegment userSegment;

    private LanguageType language = LanguageType.KHMER;

    // Student fields (flat)
    @Size(max = 150)
    private String university;

    @Size(max = 150)
    private String faculty;

    @Min(1)
    @Max(8)
    private Short studyYear;

    // Worker fields (flat)
    @Size(max = 150)
    private String workplace;

    @Size(max = 100)
    private String occupation;

    @Size(max = 100)
    private String jobPosition;

    // Nested profile support
    private StudentProfileDTO studentProfile;
    private WorkerProfileDTO workerProfile;

    @Data
    public static class StudentProfileDTO {
        private String university;
        private String faculty;
        private Short studyYear;
    }

    @Data
    public static class WorkerProfileDTO {
        private String companyName;
        private String jobTitle;
        private String workplaceAddress;
        private String workplace;
        private String occupation;
        private String jobPosition;
    }
}
