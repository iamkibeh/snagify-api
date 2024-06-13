package tech.kibetimmanuel.snagifyapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.kibetimmanuel.snagifyapi.dto.LoginResponse;
import tech.kibetimmanuel.snagifyapi.dto.LoginUserDto;
import tech.kibetimmanuel.snagifyapi.dto.RegisterUserDto;
import tech.kibetimmanuel.snagifyapi.entity.User;
import tech.kibetimmanuel.snagifyapi.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepo;
    private final UserService userService;
    private final JwtService jwtService;

    public User authenticate(LoginUserDto userInput) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userInput.getEmail(), userInput.getPassword()));
        return userRepo.findUserByEmail(userInput.getEmail()).orElseThrow();
    }

    public User signup(RegisterUserDto userInput){
        User user = User.builder()
                .email(userInput.getEmail())
                .password(passwordEncoder.encode(userInput.getPassword()))
                .build();

        return userRepo.save(user);
    }

    public LoginResponse mapUserToDto(User authenticatedUser) {
        return LoginResponse.builder()
                .user(userService.mapUserToResponse(authenticatedUser))
                .token(jwtService.generateToken(authenticatedUser))
                .expiresIn(jwtService.getAccessTokenExpiration())
                .build();
    }
}
