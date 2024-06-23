package tech.kibetimmanuel.snagifyapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.kibetimmanuel.snagifyapi.dto.LoginResponse;
import tech.kibetimmanuel.snagifyapi.dto.LoginUserDto;
import tech.kibetimmanuel.snagifyapi.dto.RegisterUserDto;
import tech.kibetimmanuel.snagifyapi.entity.User;
import tech.kibetimmanuel.snagifyapi.repository.UserRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    public User signup(RegisterUserDto userInput) {
        User user = User.builder()
                .name(userInput.getName())
                .email(userInput.getEmail())
                .password(passwordEncoder.encode(userInput.getPassword()))
                .build();

        return userRepo.save(user);
    }

    public LoginResponse mapUserToDto(User authenticatedUser) {
        return LoginResponse.builder()
                .user(userService.mapUserToResponse(authenticatedUser))
                .accessToken(jwtService.generateToken(authenticatedUser))
//                .refreshToken(jwtService.generateRefreshToken(authenticatedUser))
                .expiresIn(jwtService.getAccessTokenExpiration())
                .build();
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshToken = jwtService.getJwtRefreshTokenFromCookies(request);
        if (refreshToken != null) {
            final String email = jwtService.extractUsername(refreshToken);

            if (email != null) {
                var user = userRepo.findUserByEmail(email)
                        .orElseThrow();

                if (jwtService.isTokenValid(refreshToken, user)) {
                    String accessToken = jwtService.generateToken(user);
                    Map<String, Object> authResponse = new HashMap<>();
                    authResponse.put("access_token", accessToken);
//                    authResponse.put("refresh_token", refreshToken);
                    authResponse.put("expires_in", jwtService.getAccessTokenExpiration());
                    response.setContentType("application/json");
                    new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
                }
            }
        } else {
            // Handle null refresh token
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("message", "Refresh token expired. Please login again!");
            errorMap.put("status", 401);
            errorMap.put("type", "error");
            errorMap.put("timestamps", System.currentTimeMillis());
            new ObjectMapper().writeValue(response.getOutputStream(), errorMap);
        }
    }

    public User currentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
