package tech.kibetimmanuel.snagifyapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Authenticator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.kibetimmanuel.snagifyapi.dto.LoginResponse;
import tech.kibetimmanuel.snagifyapi.dto.LoginUserDto;
import tech.kibetimmanuel.snagifyapi.dto.RegisterUserDto;
import tech.kibetimmanuel.snagifyapi.dto.UserResponse;
import tech.kibetimmanuel.snagifyapi.entity.User;
import tech.kibetimmanuel.snagifyapi.service.AuthenticationService;
import tech.kibetimmanuel.snagifyapi.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginUserDto credentials) {
        User authenticatedUser = authenticationService.authenticate(credentials);
        var response = authenticationService.mapUserToDto(authenticatedUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        UserResponse userResponse = userService.mapUserToResponse(registeredUser);
        return new ResponseEntity<>(userResponse, HttpStatus.ACCEPTED);
    }
}
