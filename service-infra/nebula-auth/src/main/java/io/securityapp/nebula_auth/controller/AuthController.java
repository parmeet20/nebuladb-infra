package io.securityapp.nebula_auth.controller;

import io.securityapp.nebula_auth.dtos.LoginRequestDto;
import io.securityapp.nebula_auth.dtos.LoginResponseDto;
import io.securityapp.nebula_auth.dtos.MeResponseDto;
import io.securityapp.nebula_auth.dtos.SignupRequestDto;
import io.securityapp.nebula_auth.dtos.SignupResponseDto;
import io.securityapp.nebula_auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signupHandler(@RequestBody SignupRequestDto dto){
        return ResponseEntity.ok(authService.signup(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginHandler(@RequestBody LoginRequestDto dto){
        return ResponseEntity.ok(authService.login(dto));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponseDto> meHandler(@RequestHeader("Authorization") String authHeader){
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(authService.getMe(token));
    }
}