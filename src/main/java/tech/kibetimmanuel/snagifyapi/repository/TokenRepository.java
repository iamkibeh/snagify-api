package tech.kibetimmanuel.snagifyapi.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import tech.kibetimmanuel.snagifyapi.entity.Token;
import tech.kibetimmanuel.snagifyapi.enums.TokenType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("""
            select t from token t inner join users u on t.user.id = u.id
            where u.id = :userId and (t.isExpired = false or t.isRevoked = false )
            """)
    List<Token> findAllValidTokensByUser(UUID userId);

    Optional<Token> findByToken(String token);

    @Transactional
    @Modifying
    @Query("update token t SET t.isExpired = true, t.isRevoked = true where t.user.id = :id and (t.isExpired = false or t.isRevoked = false )")
    void expireTokensForUser(UUID id);

    @Transactional
    @Modifying
    @Query("UPDATE token t SET t.isExpired = true, t.isRevoked = true where t.user.id = :id and t.type = :tokenType and t.isExpired = false ")
    void revokeAccessTokensForUser(UUID id, TokenType tokenType);
}
