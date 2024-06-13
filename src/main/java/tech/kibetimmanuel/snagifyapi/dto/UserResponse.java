package tech.kibetimmanuel.snagifyapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private UUID id;
    private String email;
    private boolean enabled;
    private Collection<? extends GrantedAuthority> roles;
    private Date createdAt;
    private Date updatedAt;
}
