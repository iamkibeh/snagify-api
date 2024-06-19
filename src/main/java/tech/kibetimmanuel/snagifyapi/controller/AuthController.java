package tech.kibetimmanuel.snagifyapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.kibetimmanuel.snagifyapi.dto.LoginResponse;
import tech.kibetimmanuel.snagifyapi.dto.LoginUserDto;
import tech.kibetimmanuel.snagifyapi.dto.RegisterUserDto;
import tech.kibetimmanuel.snagifyapi.dto.UserResponse;
import tech.kibetimmanuel.snagifyapi.entity.User;
import tech.kibetimmanuel.snagifyapi.service.AuthenticationService;
import tech.kibetimmanuel.snagifyapi.service.JwtService;
import tech.kibetimmanuel.snagifyapi.service.UserService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final JwtService  jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginUserDto credentials) {
        User authenticatedUser = authenticationService.authenticate(credentials);
        var response = authenticationService.mapUserToDto(authenticatedUser);
        String refreshToken = jwtService.generateRefreshToken(authenticatedUser);
        ResponseCookie jwtRefreshCookie = jwtService.generateJwtRefreshTokenCookie(refreshToken);
        return  ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(response);
    }


    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        UserResponse userResponse = userService.mapUserToResponse(registeredUser);
        return new ResponseEntity<>(userResponse, HttpStatus.ACCEPTED);
    }

    @GetMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }
}
