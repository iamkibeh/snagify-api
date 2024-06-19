package tech.kibetimmanuel.snagifyapi.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    @JsonProperty("access_token")
    private String accessToken;
//    @JsonProperty("refresh_token")
//    private String refreshToken;
    private UserResponse user;
    private long expiresIn;
}
