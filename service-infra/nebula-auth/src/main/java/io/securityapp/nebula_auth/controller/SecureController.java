package io.securityapp.nebula_auth.controller;

import io.securityapp.nebula_auth.dtos.LoginResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/fun")
public class SecureController {
    @GetMapping
    public ResponseEntity<List<LoginResponseDto>> secureHandler(){
        return ResponseEntity.ok(
                List.of(
                        new LoginResponseDto("token1"),
                        new LoginResponseDto("token2"),
                        new LoginResponseDto("token3"),
                        new LoginResponseDto("token4"),
                        new LoginResponseDto("token5"),
                        new LoginResponseDto("token6"),
                        new LoginResponseDto("token7"),
                        new LoginResponseDto("token8"),
                        new LoginResponseDto("token9"),
                        new LoginResponseDto("token10")
                )
        );
    }
}
