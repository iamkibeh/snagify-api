package tech.kibetimmanuel.snagifyapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RegisterUserDto {
    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotEmpty(message = "Email cannot be empty")
    private String email;
    @NotEmpty(message = "Password cannot be empty" )
    @NotBlank(message = "Password cannot be blank" )
    @Length(min = 6,  message = "password length must be at least 6 characters long")
    private String password;
}
