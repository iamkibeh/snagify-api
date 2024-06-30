package tech.kibetimmanuel.snagifyapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.kibetimmanuel.snagifyapi.entity.Token;
import tech.kibetimmanuel.snagifyapi.entity.User;
import tech.kibetimmanuel.snagifyapi.enums.TokenType;
import tech.kibetimmanuel.snagifyapi.repository.TokenRepository;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    @Transactional
    public void handleLogin(User user, String newAccessToken, String newRefreshToken) {
        tokenRepository.expireTokensForUser(user.getId());

        Token accessToken = Token.builder()
                .user(user)
                .token(newAccessToken)
                .type(TokenType.ACCESS)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(accessToken);

        Token refreshToken = Token.builder()
                .user(user)
                .token(newRefreshToken)
                .type(TokenType.REFRESH)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(refreshToken);
    }
}
