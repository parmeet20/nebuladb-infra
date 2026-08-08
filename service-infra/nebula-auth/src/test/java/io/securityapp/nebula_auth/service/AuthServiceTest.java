package io.securityapp.nebula_auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.securityapp.nebula_auth.dtos.LoginRequestDto;
import io.securityapp.nebula_auth.dtos.LoginResponseDto;
import io.securityapp.nebula_auth.dtos.MeResponseDto;
import io.securityapp.nebula_auth.dtos.SignupRequestDto;
import io.securityapp.nebula_auth.dtos.SignupResponseDto;
import io.securityapp.nebula_auth.entity.User;
import io.securityapp.nebula_auth.repo.UserRepository;
import io.securityapp.nebula_auth.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private SignupRequestDto validSignupRequest;
    private LoginRequestDto validLoginRequest;
    private User testUser;
    private String testToken = "test.jwt.token";

    @BeforeEach
    void setUp() {
        validSignupRequest = new SignupRequestDto("testuser", "testpassword");
        validLoginRequest = new LoginRequestDto("testuser", "testpassword");

        testUser = User.builder()
                .id("test-user-id")
                .username("testuser")
                .password("encodedpassword")
                .email("test@example.com")
                .build();
    }

    @Test
    void signup_shouldSaveAndReturnSignupResponseDto_whenValidRequest() {
        // Arrange
        when(encoder.encode("testpassword")).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        SignupResponseDto result = authService.signup(validSignupRequest);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.username());
        assertEquals("encodedpassword", result.password());

        // Verify encoder was called
        verify(encoder).encode("testpassword");

        // Verify userRepository.save was called with correct user
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals("testuser", capturedUser.getUsername());
        assertEquals("encodedpassword", capturedUser.getPassword());
        assertNull(capturedUser.getEmail()); // email not set in signup
    }

    @Test
    void login_shouldReturnLoginResponseDto_whenValidCredentials() throws Exception {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken(testUser)).thenReturn(testToken);

        // Act
        LoginResponseDto result = authService.login(validLoginRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testToken, result.token());

        // Verify authenticationManager.authenticate was called
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Verify jwtService.generateToken was called with the user
        verify(jwtService).generateToken(testUser);
    }

    @Test
    void getMe_shouldReturnMeResponseDto_whenValidToken() {
        // Arrange
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("testuser");
        when(claims.get("userid", String.class)).thenReturn("test-user-id");
        when(claims.get("email", String.class)).thenReturn("test@example.com");
        when(jwtService.getAllClaimsFromToken(testToken)).thenReturn(claims);

        // Act
        MeResponseDto result = authService.getMe(testToken);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.username());
        assertEquals("test-user-id", result.userId());
        assertEquals("test@example.com", result.email());

        // Verify jwtService.getAllClaimsFromToken was called
        verify(jwtService).getAllClaimsFromToken(testToken);
    }

    // Test for invalid token handling in getMe (if needed, though current implementation doesn't handle exceptions)
    @Test
    void getMe_shouldThrowException_whenInvalidToken() {
        // Arrange
        when(jwtService.getAllClaimsFromToken(anyString())).thenThrow(new RuntimeException("Invalid token"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authService.getMe("invalid.token");
        });

        verify(jwtService).getAllClaimsFromToken("invalid.token");
    }
}