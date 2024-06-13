package tech.kibetimmanuel.snagifyapi.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private UserResponse user;
    private long expiresIn;
}
