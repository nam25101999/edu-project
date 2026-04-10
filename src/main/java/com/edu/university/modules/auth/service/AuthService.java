package com.edu.university.modules.auth.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.common.security.JwtUtils;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.auth.dto.AuthDtos;
import com.edu.university.modules.auth.dto.AuthDtos.*;
import com.edu.university.modules.auth.entity.RefreshToken;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.auth.annotation.LogAction;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.edu.university.modules.auth.repository.RoleRepository;
import com.edu.university.modules.auth.mapper.AuthMapper;
import com.edu.university.modules.auth.dto.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final HttpServletRequest httpRequest;
    private final AuthMapper authMapper;
    private final com.edu.university.modules.student.repository.StudentRepository studentRepository;

    // ================= LOGIN =================
    @Transactional
    @LogAction(action = "LOGIN", entityName = "USER")
    public AuthDtos.JwtResponse authenticateUser(AuthDtos.LoginRequest request) {

        Users user = userRepository.findByIdentifier(request.identifier())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 1. Chá»‘ng Brute Force: Kiá»ƒm tra xem tÃ i khoáº£n cÃ³ Ä‘ang bá»‹ khÃ³a
        // khÃ´ng
        if (user.getLockUntil() != null && user.getLockUntil().isAfter(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.FORBIDDEN,
                    "TÃ i khoáº£n Ä‘ang bá»‹ khÃ³a táº¡m thá»i do nháº­p sai quÃ¡ nhiá»u láº§n. Vui lÃ²ng thá»­ láº¡i sau.");
        }

        Authentication authentication;
        try {
            // 2. XÃ¡c thá»±c Spring Security
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), request.password()));
        } catch (BadCredentialsException e) {
            handleFailedLogin(user);
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, "Tài khoản hoặc mật khẩu không chính xác");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // 3. Reset bá»™ Ä‘áº¿m lá»—i & Cáº­p nháº­t Last Login
        user.setLastLoginAt(LocalDateTime.now());
        user.setFailedLoginAttempts(0);
        user.setLockUntil(null);
        user.setLastLoginIp(getClientIp());
        user.setLastLoginUserAgent(getUserAgent());
        userRepository.save(user);

        // 4. Sinh Tokens (Quáº£n lÃ½ thiáº¿t bá»‹ qua TokenService)
        String accessToken = jwtUtils.generateJwtToken(authentication);

        // Cáº¥p Refresh Token má»›i (Sinh ra 1 Family ID má»›i cho thiáº¿t bá»‹ nÃ y)
        RefreshToken refreshToken = tokenService.createRefreshToken(user, getClientIp(), getUserAgent(),
                UUID.randomUUID().toString());

        // Láº¥y danh sÃ¡ch táº¥t cáº£ cÃ¡c Role cá»§a User thay vÃ¬ chá»‰ láº¥y 1
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .toList();

        // Khá»Ÿi táº¡o object UserInfo
        AuthDtos.JwtResponse.UserInfo userInfo = new AuthDtos.JwtResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles,
                user.isActive(),
                user.isEmailVerified(),
                user.getLastLoginAt() != null ? user.getLastLoginAt().toString() : null,
                getStudentProfile(user.getId()));

        // Tráº£ vá» cáº¥u trÃºc JSON chuáº©n
        return new JwtResponse(
                accessToken,
                refreshToken.getTokenPlain(),
                "Bearer",
                3600L, // Thá»i gian háº¿t háº¡n tÃ­nh báº±ng giÃ¢y (3600s = 1 giá»)
                userInfo);
    }

    // ================= REGISTER =================
    @Transactional
    @LogAction(action = "SIGNUP", entityName = "USER")
    public UserResponseDTO registerUser(AuthDtos.SignupRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Users user = Users.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .isActive(false) // Mới đăng ký thì chưa active, phải Verify Email
                .emailVerified(false)
                .build();

        // Láº¥y Role tá»« DB vÃ  gÃ¡n cho User lÃºc Ä‘Äƒng kÃ½
        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                        "Lá»—i há»‡ thá»‘ng: ChÆ°a cáº¥u hÃ¬nh Role máº·c Ä‘á»‹nh."));

        user.getRoles().add(studentRole);

        Users savedUser = userRepository.save(user);
        return authMapper.toUserResponseDTO(savedUser);
    }

    // ================= LOGOUT =================
    @Transactional
    @LogAction(action = "LOGOUT", entityName = "USER")
    public void logout(String plainRefreshToken) {
        // 1. VÃ´ hiá»‡u hÃ³a token trong Database (Chuyá»ƒn is_revoked = true)
        tokenService.logout(plainRefreshToken);
    }

    // ================= LOGOUT ALL DEVICES =================
    @Transactional
    @LogAction(action = "LOGOUT_ALL", entityName = "USER")
    public void logoutAllDevices(UUID userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.incrementTokenVersion(); // Kick toÃ n bá»™ Access Token (Cáº§n check version trong JwtFilter)
        userRepository.save(user);

        tokenService.revokeAllUserTokens(user); // Thu há»“i toÃ n bá»™ Refresh Token
    }

    // ================= CHANGE PASSWORD =================
    @Transactional
    @LogAction(action = "CHANGE_PASSWORD", entityName = "USER")
    public void changePassword(UUID userId, AuthDtos.ChangePasswordRequest request) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, "Máº­t kháº©u cÅ© khÃ´ng chÃ­nh xÃ¡c!");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT,
                    "Máº­t kháº©u má»›i khÃ´ng Ä‘Æ°á»£c trÃ¹ng máº­t kháº©u cÅ©!");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        user.incrementTokenVersion(); // Buá»™c Ä‘Äƒng nháº­p láº¡i trÃªn toÃ n bá»™ thiáº¿t bá»‹

        userRepository.save(user);
        tokenService.revokeAllUserTokens(user);
    }

    /**
     * Lấy thông tin chi tiết sinh viên của user hiện tại.
     */
    public StudentProfileDTO getStudentProfile(UUID userId) {
        return studentRepository.findByUserId(userId)
                .map(s -> new StudentProfileDTO(
                        s.getStudentCode(),
                        s.getFullName(),
                        s.getPhone(),
                        s.getGender(),
                        s.getDateOfBirth(),
                        s.getAddress(),
                        s.getMajor() != null ? s.getMajor().getName() : "N/A",
                        s.getDepartment() != null ? s.getDepartment().getName() : "N/A",
                        s.getPersonalIdentificationNumber(),
                        s.getDateOfIssue(),
                        s.getCardPlace(),
                        s.getCurrentAddress()))
                .orElse(null);
    }

    /**
     * Cập nhật thông tin profile của user hiện tại.
     */
    @Transactional
    @LogAction(action = "UPDATE_PROFILE", entityName = "USER")
    public void updateProfile(UUID userId, AuthDtos.ProfileUpdateRequest request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Lấy Student hiện tại, nếu chưa có thì tự động khởi tạo mới (Dành cho tài khoản mới đăng ký)
        com.edu.university.modules.student.entity.Student student = studentRepository.findByUserId(userId).orElseGet(() -> {
            com.edu.university.modules.student.entity.Student newStudent = new com.edu.university.modules.student.entity.Student();
            newStudent.setUser(user);
            newStudent.setEmail(user.getEmail());
            newStudent.setStudentCode("SV" + System.currentTimeMillis()); // Mã SV cấp phát tạm thời
            return newStudent;
        });

        if (request.fullName() != null)
            student.setFullName(request.fullName());
        if (request.phone() != null)
            student.setPhone(request.phone());
        if (request.gender() != null)
            student.setGender(request.gender());
        if (request.dateOfBirth() != null)
            student.setDateOfBirth(request.dateOfBirth());
        if (request.address() != null)
            student.setAddress(request.address());
        if (request.personalIdentificationNumber() != null)
            student.setPersonalIdentificationNumber(request.personalIdentificationNumber());
        if (request.dateOfIssue() != null)
            student.setDateOfIssue(request.dateOfIssue());
        if (request.cardPlace() != null)
            student.setCardPlace(request.cardPlace());
        if (request.currentAddress() != null)
            student.setCurrentAddress(request.currentAddress());
            
        studentRepository.save(student);

        // Có thể mở rộng cập nhật Employee tương tự nếu cần
    }

    // ================= REFRESH TOKEN =================
    @Transactional
    public AuthDtos.TokenRefreshResponse refreshToken(AuthDtos.TokenRefreshRequest request) {

        RefreshToken newRtEntity = tokenService.rotateToken(request.refreshToken(), getClientIp(), getUserAgent());

        String newAccessToken = jwtUtils.generateTokenFromUsername(
                newRtEntity.getUser().getUsername());

        return new AuthDtos.TokenRefreshResponse(
                newAccessToken,
                newRtEntity.getTokenPlain() // Plain text
        );
    }

    // ================= CRUD OPERATIONS FOR USER =================

    // 1. GET ALL USERS (PAGINATED)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(authMapper::toUserResponseDTO);
    }

    // 1b. GET ALL USERS EXCEPT STUDENTS (STAFF)
    public Page<UserResponseDTO> getStaffUsers(Pageable pageable) {
        return userRepository.findByRoles_NameNot("STUDENT", pageable)
                .map(authMapper::toUserResponseDTO);
    }

    // 2. GET USER BY ID
    public UserResponseDTO getUserById(UUID id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                        "KhÃ´ng tÃ¬m tháº¥y ngÆ°á»i dÃ¹ng vá»›i ID nÃ y"));
        return authMapper.toUserResponseDTO(user);
    }

    // 3. CREATE USER (DÃ nh cho Admin - CÃ³ thá»ƒ gÃ¡n Roles tuá»³ chá»‰nh)
    @Transactional
    @LogAction(action = "CREATE", entityName = "USER")
    public UserResponseDTO createUser(String username, String email, String password, List<String> roleNames,
            boolean isActive) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Users user = Users.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .isActive(isActive)
                .emailVerified(isActive)
                .build();

        // GÃ¡n Role
        if (roleNames != null && !roleNames.isEmpty()) {
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                                "Role khÃ´ng tá»“n táº¡i: " + roleName));
                user.getRoles().add(role);
            }
        } else {
            // Role máº·c Ä‘á»‹nh náº¿u Admin khÃ´ng chá»n
            Role defaultRole = roleRepository.findByName("STUDENT")
                    .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                            "ChÆ°a cáº¥u hÃ¬nh Role máº·c Ä‘á»‹nh."));
            user.getRoles().add(defaultRole);
        }

        Users savedUser = userRepository.save(user);
        return authMapper.toUserResponseDTO(savedUser);
    }

    // 4. UPDATE USER
    @Transactional
    @LogAction(action = "UPDATE", entityName = "USER")
    public UserResponseDTO updateUser(UUID id, String email, Boolean isActive, List<String> roleNames) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                        "KhÃ´ng tÃ¬m tháº¥y ngÆ°á»i dÃ¹ng Ä‘á»ƒ cáº­p nháº­t"));

        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(email);
        }

        if (isActive != null) {
            user.setIsActive(isActive);
            if (!isActive) {
                // Náº¿u khÃ³a tÃ i khoáº£n, Ã©p Ä‘Äƒng xuáº¥t thiáº¿t bá»‹ hiá»‡n táº¡i
                user.incrementTokenVersion();
                tokenService.revokeAllUserTokens(user);
            }
        }

        // Cáº­p nháº­t Roles
        if (roleNames != null) {
            user.getRoles().clear();
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                                "Role khÃ´ng tá»“n táº¡i: " + roleName));
                user.getRoles().add(role);
            }
        }

        Users savedUser = userRepository.save(user);
        return authMapper.toUserResponseDTO(savedUser);
    }

    @Transactional
    @LogAction(action = "DELETE", entityName = "USER")
    public void deleteUser(UUID id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                        "KhÃ´ng tÃ¬m tháº¥y ngÆ°á»i dÃ¹ng Ä‘á»ƒ xÃ³a"));

        // PhÆ°Æ¡ng phÃ¡p tá»‘t nháº¥t cho User lÃ  XÃ³a má»m (Soft Delete)
        user.softDelete();

        userRepository.save(user);

        // Buá»™c ngÆ°á»i dÃ¹ng bá»‹ xÃ³a vÄƒng khá»i má»i thiáº¿t bá»‹
        user.incrementTokenVersion();
        tokenService.revokeAllUserTokens(user);
    }

    // ================= PRIVATE METHODS =================
    private void handleFailedLogin(Users user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        user.setLastFailedLoginAt(LocalDateTime.now());

        if (user.getFailedLoginAttempts() >= 5) {
            user.setLockUntil(LocalDateTime.now().plusMinutes(15)); // KhÃ³a 15 phÃºt
            log.warn("TÃ i khoáº£n {} Ä‘Ã£ bá»‹ khÃ³a 15 phÃºt do Brute Force", user.getUsername());
        }
        userRepository.save(user);
    }

    private String getClientIp() {
        if (httpRequest == null)
            return null;
        String ip = httpRequest.getHeader("X-Forwarded-For");
        return (ip == null || ip.isEmpty()) ? httpRequest.getRemoteAddr() : ip.split(",")[0];
    }

    private String getUserAgent() {
        return httpRequest != null ? httpRequest.getHeader("User-Agent") : null;
    }
}
