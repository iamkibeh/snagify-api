package tech.kibetimmanuel.snagifyapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.kibetimmanuel.snagifyapi.enums.TokenType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "token")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    @Enumerated(EnumType.STRING)
    private TokenType type;
    private boolean isExpired;
    private boolean isRevoked;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
