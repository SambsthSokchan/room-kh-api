package com.roomkh.service.impl;

import com.roomkh.dto.request.LoginRequestDTO;
import com.roomkh.dto.request.RegisterRequestDTO;
import com.roomkh.dto.response.AuthResponseDTO;
import com.roomkh.dto.response.UserResponseDTO;
import com.roomkh.entity.Student;
import com.roomkh.entity.User;
import com.roomkh.entity.Worker;
import com.roomkh.enums.LanguageType;
import com.roomkh.enums.UserRole;
import com.roomkh.enums.UserSegment;
import com.roomkh.enums.UserStatus;
import com.roomkh.exception.BadRequestException;
import com.roomkh.exception.ConflictException;
import com.roomkh.exception.ForbiddenException;
import com.roomkh.exception.UnauthorizedException;
import com.roomkh.mapper.UserMapper;
import com.roomkh.repository.StudentRepository;
import com.roomkh.repository.UserRepository;
import com.roomkh.repository.WorkerRepository;
import com.roomkh.security.JwtTokenProvider;
import com.roomkh.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final WorkerRepository workerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDTO register(RegisterRequestDTO request) {
        if (request.getConfirmPassword() != null && !request.getConfirmPassword().isBlank()) {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new BadRequestException("Passwords do not match");
            }
        }

        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email is already registered");
        }

        String phone = (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank())
                ? request.getPhoneNumber().trim()
                : null;
        if (phone != null && userRepository.existsByPhoneNumber(phone)) {
            throw new ConflictException("Phone number is already registered");
        }

        // Resolve student profile fields (flat or nested)
        String university = request.getUniversity();
        String faculty = request.getFaculty();
        Short studyYear = request.getStudyYear();
        if (request.getStudentProfile() != null) {
            if (university == null || university.isBlank()) {
                university = request.getStudentProfile().getUniversity();
            }
            if (faculty == null || faculty.isBlank()) {
                faculty = request.getStudentProfile().getFaculty();
            }
            if (studyYear == null) {
                studyYear = request.getStudentProfile().getStudyYear();
            }
        }

        // Resolve worker profile fields (flat or nested)
        String workplace = request.getWorkplace();
        String occupation = request.getOccupation();
        String jobPosition = request.getJobPosition();
        if (request.getWorkerProfile() != null) {
            var wp = request.getWorkerProfile();
            if (workplace == null || workplace.isBlank()) {
                workplace = wp.getCompanyName() != null && !wp.getCompanyName().isBlank()
                        ? wp.getCompanyName()
                        : (wp.getWorkplace() != null ? wp.getWorkplace() : wp.getWorkplaceAddress());
            }
            if (jobPosition == null || jobPosition.isBlank()) {
                jobPosition = wp.getJobTitle() != null ? wp.getJobTitle() : wp.getJobPosition();
            }
            if (occupation == null || occupation.isBlank()) {
                occupation = wp.getOccupation() != null ? wp.getOccupation() : wp.getJobTitle();
            }
        }

        // Validate subtype fields before saving
        if (request.getUserSegment() == UserSegment.STUDENT
                && (university == null || university.isBlank())) {
            throw new BadRequestException("University is required for student accounts");
        }
        if (request.getUserSegment() == UserSegment.WORKER
                && (workplace == null || workplace.isBlank())) {
            throw new BadRequestException("Workplace is required for worker accounts");
        }

        User user = User.builder()
                .fullName(request.getFullName().trim())
                .email(email)
                .phoneNumber(phone)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.RENTER)
                .userSegment(request.getUserSegment())
                .language(request.getLanguage() != null ? request.getLanguage() : LanguageType.KHMER)
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        if (request.getUserSegment() == UserSegment.STUDENT) {
            Student student = Student.builder()
                    .user(savedUser)
                    .university(university.trim())
                    .faculty(faculty)
                    .studyYear(studyYear)
                    .build();
            studentRepository.save(student);
        } else if (request.getUserSegment() == UserSegment.WORKER) {
            Worker worker = Worker.builder()
                    .user(savedUser)
                    .workplace(workplace.trim())
                    .occupation(occupation)
                    .jobPosition(jobPosition)
                    .build();
            workerRepository.save(worker);
        }

        return userMapper.toResponse(savedUser);
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ForbiddenException("Account is " + user.getStatus().name().toLowerCase());
        }

        String token = jwtTokenProvider.generateToken(user);

        return AuthResponseDTO.builder()
                .accessToken(token)
                .user(userMapper.toResponse(user))
                .build();
    }
}
