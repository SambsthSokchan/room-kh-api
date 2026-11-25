package com.roomkh.config;

import com.roomkh.entity.Admin;
import com.roomkh.entity.LandlordProfile;
import com.roomkh.entity.User;
import com.roomkh.enums.*;
import com.roomkh.repository.AdminRepository;
import com.roomkh.repository.LandlordProfileRepository;
import com.roomkh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final LandlordProfileRepository landlordProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@roomkh.com")) {
            log.info("Seeding default admin user: admin@roomkh.com");
            User adminUser = User.builder()
                    .fullName("RoomKH Admin")
                    .email("admin@roomkh.com")
                    .passwordHash(passwordEncoder.encode("Admin1234!"))
                    .phoneNumber("+85512000000")
                    .role(UserRole.ADMIN)
                    .userSegment(UserSegment.OTHER)
                    .language(LanguageType.KHMER)
                    .status(UserStatus.ACTIVE)
                    .build();

            User savedUser = userRepository.save(adminUser);

            Admin admin = Admin.builder()
                    .user(savedUser)
                    .adminRole("SUPER_ADMIN")
                    .build();

            adminRepository.save(admin);
            log.info("Default admin user created successfully with email: admin@roomkh.com");
        }

        if (!userRepository.existsByEmail("vichea.landlord@example.com")) {
            log.info("Seeding default landlord user: vichea.landlord@example.com");
            User landlordUser = User.builder()
                    .fullName("Chea Vichea")
                    .email("vichea.landlord@example.com")
                    .passwordHash(passwordEncoder.encode("password123"))
                    .phoneNumber("+85512000002")
                    .role(UserRole.LANDLORD)
                    .userSegment(UserSegment.OTHER)
                    .language(LanguageType.KHMER)
                    .status(UserStatus.ACTIVE)
                    .build();

            User savedUser = userRepository.save(landlordUser);

            LandlordProfile profile = LandlordProfile.builder()
                    .user(savedUser)
                    .businessName("Vichea Modern Apartment")
                    .businessType(BusinessType.APARTMENT)
                    .contactNumber("+85512000002")
                    .businessEmail("vichea.landlord@example.com")
                    .businessAddress("St. 271, Toul Kork, Phnom Penh")
                    .idType(IdCardType.NATIONAL_ID)
                    .idNumber("123456789")
                    .landlordStatus(LandlordStatus.APPROVED)
                    .build();

            landlordProfileRepository.save(profile);
            log.info("Default landlord created successfully with email: vichea.landlord@example.com");
        }
    }
}
