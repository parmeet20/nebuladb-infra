package io.securityapp.nebula_auth.service;

import io.securityapp.nebula_auth.dtos.LoginRequestDto;
import io.securityapp.nebula_auth.dtos.LoginResponseDto;
import io.securityapp.nebula_auth.dtos.SignupRequestDto;
import io.securityapp.nebula_auth.dtos.SignupResponseDto;
import io.securityapp.nebula_auth.entity.User;
import io.securityapp.nebula_auth.repo.UserRepository;
import io.securityapp.nebula_auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public SignupResponseDto signup(SignupRequestDto dto){
        User newusr = User
                .builder()
                .username(dto.username())
                .password(encoder.encode(dto.password()))
                .build();
        User createdUsr = userRepository.save(newusr);
        return new SignupResponseDto(createdUsr.getUsername(), createdUsr.getPassword());
    }

    public LoginResponseDto login(LoginRequestDto dto){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(dto.username(),dto.password()));

        User user = (User) authentication.getPrincipal();

        String token = jwtService.generateToken(user);
        return new LoginResponseDto(token);
    }

}