package com.edu.university.modules.auth.service;

import com.edu.university.common.security.JwtUtils;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.auth.dto.AuthDtos.*;
import com.edu.university.modules.auth.entity.RefreshToken;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.mapper.AuthMapper;
import com.edu.university.modules.auth.repository.RoleRepository;
import com.edu.university.modules.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenService tokenService;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private AuthMapper authMapper;

    @InjectMocks
    private AuthService authService;

    private Users user;

    @BeforeEach
    void setUp() {
        user = Users.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@edu.com")
                .password("encoded-password")
                .failedLoginAttempts(0)
                .isActive(true)
                .build();
    }

    // ================= LOGIN TESTS =================

    @Test
    void authenticateUser_Success() {
        // Arrange
        LoginRequest request = new LoginRequest("testuser", "password");
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        RefreshToken refreshToken = mock(RefreshToken.class);

        when(userRepository.findByIdentifier(anyString())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(any())).thenReturn("access-token");
        when(tokenService.createRefreshToken(any(), any(), any(), any())).thenReturn(refreshToken);
        when(refreshToken.getTokenPlain()).thenReturn("refresh-token");
        when(userDetails.getId()).thenReturn(user.getId());
        when(userDetails.getUsername()).thenReturn(user.getUsername());
        // authorities return via roles mapping logic in method

        // Act
        JwtResponse response = authService.authenticateUser(request);

        // Assert
        assertNotNull(response);
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        verify(userRepository).save(user);
        assertEquals(0, user.getFailedLoginAttempts());
    }

    @Test
    void authenticateUser_UserNotFound() {
        // Arrange
        LoginRequest request = new LoginRequest("unknown", "password");
        when(userRepository.findByIdentifier("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> authService.authenticateUser(request));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void authenticateUser_AccountLocked() {
        // Arrange
        LoginRequest request = new LoginRequest("testuser", "password");
        user.setLockUntil(LocalDateTime.now().plusMinutes(15));
        when(userRepository.findByIdentifier("testuser")).thenReturn(Optional.of(user));

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> authService.authenticateUser(request));
        assertEquals(ErrorCode.FORBIDDEN, ex.getErrorCode());
    }

    @Test
    void authenticateUser_InvalidCredentials() {
        // Arrange
        LoginRequest request = new LoginRequest("testuser", "wrong-password");
        when(userRepository.findByIdentifier("testuser")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> authService.authenticateUser(request));
        assertEquals(ErrorCode.INVALID_CREDENTIALS, ex.getErrorCode());
        verify(userRepository).save(user);
        assertEquals(1, user.getFailedLoginAttempts());
    }

    // ================= REGISTER TESTS =================

    @Test
    void registerUser_Success() {
        // Arrange
        SignupRequest request = new SignupRequest("newuser", "password123", "newuser@edu.com", "ROLE_STUDENT");
        Role role = Role.builder().name("ROLE_STUDENT").build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@edu.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any())).thenReturn(user);

        // Act
        authService.registerUser(request);

        // Assert
        verify(userRepository).save(any(Users.class));
        verify(authMapper).toUserResponseDTO(any());
    }

    @Test
    void registerUser_DuplicateUsername() {
        // Arrange
        SignupRequest request = new SignupRequest("testuser", "password", "test@edu.com", null);
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> authService.registerUser(request));
        assertEquals(ErrorCode.USERNAME_ALREADY_EXISTS, ex.getErrorCode());
    }

    // ================= CHANGE PASSWORD TESTS =================

    @Test
    void changePassword_Success() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest("old-password", "new-password");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", user.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("new-password", user.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("new-password")).thenReturn("new-encoded-password");

        // Act
        authService.changePassword(user.getId(), request);

        // Assert
        assertEquals("new-encoded-password", user.getPassword());
        verify(userRepository).save(user);
        verify(tokenService).revokeAllUserTokens(user);
    }

    @Test
    void changePassword_WrongOldPassword() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest("wrong-password", "new-password");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", user.getPassword())).thenReturn(false);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> authService.changePassword(user.getId(), request));
        assertEquals(ErrorCode.INVALID_CREDENTIALS, ex.getErrorCode());
    }

    @Test
    void changePassword_SameNewPassword() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest("password", "password");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", user.getPassword())).thenReturn(true);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> authService.changePassword(user.getId(), request));
        assertEquals(ErrorCode.INVALID_INPUT, ex.getErrorCode());
    }
}
