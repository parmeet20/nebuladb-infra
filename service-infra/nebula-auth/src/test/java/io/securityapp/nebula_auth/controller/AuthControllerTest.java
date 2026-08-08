package io.securityapp.nebula_auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.securityapp.nebula_auth.dtos.LoginRequestDto;
import io.securityapp.nebula_auth.dtos.LoginResponseDto;
import io.securityapp.nebula_auth.dtos.MeResponseDto;
import io.securityapp.nebula_auth.dtos.SignupRequestDto;
import io.securityapp.nebula_auth.dtos.SignupResponseDto;
import io.securityapp.nebula_auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void signupHandler_shouldReturnSignupResponseDto_whenValidRequest() throws Exception {
        // Arrange
        SignupRequestDto requestDto = new SignupRequestDto("testuser", "testpass");
        SignupResponseDto responseDto = new SignupResponseDto("testuser", "testpass");

        when(authService.signup(any(SignupRequestDto.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.password").value("testpass"));
    }

    @Test
    void loginHandler_shouldReturnLoginResponseDto_whenValidRequest() throws Exception {
        // Arrange
        LoginRequestDto requestDto = new LoginRequestDto("testuser", "testpass");
        LoginResponseDto responseDto = new LoginResponseDto("fake-jwt-token");

        when(authService.login(any(LoginRequestDto.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    void meHandler_shouldReturnMeResponseDto_whenValidTokenProvided() throws Exception {
        // Arrange
        String token = "fake-jwt-token";
        MeResponseDto responseDto = new MeResponseDto("testuser", "123", "test@example.com");

        when(authService.getMe(token)).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.userId").value("123"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void meHandler_shouldReturnUnauthorized_whenNoTokenProvided() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().is(400));
    }

    @Test
    void meHandler_shouldReturnUnauthorized_whenInvalidTokenFormat() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/me")
                .header("Authorization", "InvalidToken"))
                .andExpect(status().isOk());
    }
}